package com.sure.weatherapp.servicelayer

import com.sure.weatherapp.servicelayer.models.ExceptionResponse
import com.sure.weatherapp.servicelayer.models.ResponseType
import com.sure.weatherapp.servicelayer.models.ServiceResponse
import com.sure.weatherapp.servicelayer.models.ServiceResult
import com.android.volley.DefaultRetryPolicy
import com.android.volley.NetworkResponse
import com.android.volley.ParseError
import com.android.volley.Request.Method
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.JsonObjectRequest
import com.google.gson.Gson
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import org.json.JSONException
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import javax.inject.Inject
import kotlin.coroutines.resume

class ServiceImpl @Inject constructor(
    private val requestQueue: RequestQueue
) : Service {

    override suspend fun <T : Any> GET(url: String, parameters: String, responseType: Class<T>): ServiceResult<T> =
        request(url = url, method = Method.GET, parameters = parameters, responseType = responseType)

    private suspend fun <T : Any> request(
        url: String,
        method: Int,
        parameters: String,
        responseType: Class<T>
    ): ServiceResult<T> = suspendCancellableCoroutine { continuation ->
        val jsonObjectRequest = createJsonObjectRequest(
            url = url,
            method = method,
            parameters = parameters,
            continuation = continuation
        ) { response ->
            handleResponse(response, responseType, continuation)
        }

        sendRequest(jsonObjectRequest, continuation)
    }

    private fun <T : Any> handleResponse(
        response: JSONObject?,
        responseType: Class<T>,
        continuation: CancellableContinuation<ServiceResult<T>>
    ) {

        val data = parseJsonToDataModel(response, responseType)
        val apiResponse = ServiceResponse(ResponseType.SUCCESS, "Success: 200")
        val apiResult = ServiceResult(apiResponse, data)
        continuation.resume(apiResult)
    }

    private fun <T : Any> createJsonObjectRequest(
        url: String,
        method: Int,
        parameters: String,
        continuation: CancellableContinuation<ServiceResult<T>>,
        onResponse: (JSONObject?) -> Unit
    ): JsonObjectRequest {
        return object : JsonObjectRequest(method, "$BASE_URL/$url?apikey=xnWseAuTPupnnl400wT9vlJZ8AHKEL4C$parameters", null,
            Response.Listener { response -> onResponse(response) },
            Response.ErrorListener { error ->
                handleError(
                    error,
                    continuation
                )
            }) {
//            override fun getHeaders(): Map<String, String> {
//                val headers = HashMap<String, String>()
//                headers["Host"] = "dataservice.accuweather.com"
//                return headers
//            }

            override fun parseNetworkResponse(response: NetworkResponse?): Response<JSONObject> {
                try {
                    val jsonString = response?.data?.let {
                        String(
                            it,
                            Charset.forName(HttpHeaderParser.parseCharset(response.headers))
                        )
                    }
                    var result: JSONObject? = null
                    if (!jsonString.isNullOrEmpty()) result = JSONObject(jsonString)
                    return Response.success(
                        result,
                        HttpHeaderParser.parseCacheHeaders(response)
                    )
                } catch (e: UnsupportedEncodingException) {
                    return Response.error(ParseError(e))
                } catch (je: JSONException) {
                    return Response.error(ParseError(je))
                }
            }

//            override fun getParams(): MutableMap<String, String> {
//               val params : HashMap<String, String> = HashMap()
//                params["apikey"] = "xnWseAuTPupnnl400wT9vlJZ8AHKEL4C"
//                params.putAll(parameters)
//
//                return params
//            }
        }
    }

    private fun <T : Any> handleError(
        error: VolleyError,
        continuation: CancellableContinuation<ServiceResult<T>>
    ) {
        val byteBody = String(
            error.networkResponse?.data ?: ByteArray(0),
            Charset.forName(HttpHeaderParser.parseCharset(error.networkResponse?.headers, "UTF-8"))
        )

        println("Sage ServiceLayer***: $byteBody")

        try {
            val data = parseJsonToDataModel(
                JSONObject(byteBody),
                ExceptionResponse::class.java
            )
            val response = ServiceResponse(
                responseType = ResponseType.ERROR,
                code = data?.status.toString(),
                message = data?.message.toString()
            )
            val apiResult = ServiceResult<T>(serviceResponse = response)
            continuation.resume(apiResult)
        } catch (e: Exception) {
            val apiResponse = ServiceResponse(
                responseType = ResponseType.CONNECTION_ERROR,
                code = "Connection Error",
                message = "Something went wrong"
            )
            println("Sage ServiceLayer: ${e.localizedMessage}")
            val apiResult = ServiceResult<T>(serviceResponse = apiResponse)
            continuation.resume(apiResult)
        }
    }

    private fun <T : Any> sendRequest(
        jsonObjectRequest: JsonObjectRequest,
        continuation: CancellableContinuation<ServiceResult<T>>
    ) {
        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
            5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        requestQueue.add(jsonObjectRequest)

        continuation.invokeOnCancellation {
            jsonObjectRequest.cancel()
        }
    }

    private fun <T : Any> parseJsonToDataModel(jsonObject: JSONObject?, modelClass: Class<T>): T? {
        return if (modelClass == Void::class.java) {
            null
        } else {
            Gson().fromJson(jsonObject.toString(), modelClass)
        }
    }

    companion object {
        private const val BASE_URL = "http://dataservice.accuweather.com"
    }
}