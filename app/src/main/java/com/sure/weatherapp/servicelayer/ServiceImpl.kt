package com.sure.weatherapp.servicelayer

import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request.Method
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.google.gson.Gson
import com.sure.weatherapp.servicelayer.models.ExceptionResponse
import com.sure.weatherapp.servicelayer.models.ResponseType
import com.sure.weatherapp.servicelayer.models.ServiceResponse
import com.sure.weatherapp.servicelayer.models.ServiceResult
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import org.json.JSONArray
import org.json.JSONObject
import java.nio.charset.Charset
import javax.inject.Inject
import kotlin.coroutines.resume

class ServiceImpl @Inject constructor(
    private val requestQueue: RequestQueue
) : Service {

    override suspend fun <T : Any> GET(
        url: String,
        parameters: String,
        responseType: Class<T>,
        isResponseArray: Boolean
    ): ServiceResult<T> =
        request(
            url = url,
            method = Method.GET,
            parameters = parameters,
            responseType = responseType,
            isResponseArray
        )

    private suspend fun <T : Any> request(
        url: String,
        method: Int,
        parameters: String,
        responseType: Class<T>,
        isResponseArray: Boolean
    ): ServiceResult<T> = suspendCancellableCoroutine { continuation ->
        if (isResponseArray) {
            val jsonArrayRequest = createJsonArrayRequest(
                url = url,
                method = method,
                parameters = parameters,
                continuation = continuation
            ) { response ->
                handleArrayResponse(response, responseType, continuation)
            }
            sendArrayRequest(jsonArrayRequest, continuation)
        } else {
            val jsonObjectRequest = createJsonObjectRequest(
                url = url,
                method = method,
                parameters = parameters,
                continuation = continuation
            ) { response ->
                handleObjectResponse(response, responseType, continuation)
            }

            sendObjectRequest(jsonObjectRequest, continuation)
        }

    }

    private fun <T : Any> handleObjectResponse(
        response: JSONObject?,
        responseType: Class<T>,
        continuation: CancellableContinuation<ServiceResult<T>>
    ) {
        println("Sage Service Layer: Handle response Called")
        val data = parseJsonToDataModel(jsonObject = response, modelClass = responseType)
        val apiResponse = ServiceResponse(ResponseType.SUCCESS, "Success: 200")
        val apiResult = ServiceResult(apiResponse, data)
        continuation.resume(apiResult)
    }

    private fun <T : Any> handleArrayResponse(
        response: JSONArray?,
        responseType: Class<T>,
        continuation: CancellableContinuation<ServiceResult<T>>
    ) {
        val data = parseJsonToDataModel(jsonArray = response, modelClass = responseType)
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
        println("Sage Service Layer Url: \"$BASE_URL/$url?apikey=$API_KEY$parameters\"")
        return object : JsonObjectRequest(
            method, "$BASE_URL/$url?apikey=$API_KEY$parameters", null,
            Response.Listener { response -> onResponse(response) },
            Response.ErrorListener { error ->
                handleError(
                    error,
                    continuation
                )
            }) {
        }
    }

    private fun <T : Any> createJsonArrayRequest(
        url: String,
        method: Int,
        parameters: String,
        continuation: CancellableContinuation<ServiceResult<T>>,
        onResponse: (JSONArray?) -> Unit
    ): JsonArrayRequest {
        println("Sage Service Layer Url: \"$BASE_URL/$url?apikey=$API_KEY$parameters\"")
        return object : JsonArrayRequest(
            method, "$BASE_URL/$url?apikey=$API_KEY$parameters", null,
            Response.Listener { response -> onResponse(response) },
            Response.ErrorListener { error ->
                handleError(
                    error,
                    continuation
                )
            }) {
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
                jsonObject = JSONObject(byteBody),
                modelClass = ExceptionResponse::class.java
            )
            val response = ServiceResponse(
                responseType = ResponseType.ERROR,
                message = data?.message.toString()
            )
            val apiResult = ServiceResult<T>(serviceResponse = response)
            continuation.resume(apiResult)
        } catch (e: Exception) {
            val apiResponse = ServiceResponse(
                responseType = ResponseType.CONNECTION_ERROR,
                message = "Something went wrong"
            )
            println("Sage ServiceLayer: ${e.localizedMessage}")
            val apiResult = ServiceResult<T>(serviceResponse = apiResponse)
            continuation.resume(apiResult)
        }
    }

    private fun <T : Any> sendObjectRequest(
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

    private fun <T : Any> sendArrayRequest(
        jsonArrayRequest: JsonArrayRequest,
        continuation: CancellableContinuation<ServiceResult<T>>
    ) {
        jsonArrayRequest.retryPolicy = DefaultRetryPolicy(
            5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        requestQueue.add(jsonArrayRequest)

        continuation.invokeOnCancellation {
            jsonArrayRequest.cancel()
        }
    }

    private fun <T : Any> parseJsonToDataModel(
        jsonObject: JSONObject? = null,
        jsonArray: JSONArray? = null,
        modelClass: Class<T>
    ): T? {
        return if (modelClass == Void::class.java) {
            null
        } else {
            if (jsonObject == null) {
                Gson().fromJson(jsonArray.toString(), modelClass)
            } else {
                Gson().fromJson(jsonObject.toString(), modelClass)
            }
        }
    }

    companion object {
        private const val BASE_URL = "http://dataservice.accuweather.com"
        private const val API_KEY = "H635o3A4axw01yD1hg1CxZQMUilRztZG"
    }
}