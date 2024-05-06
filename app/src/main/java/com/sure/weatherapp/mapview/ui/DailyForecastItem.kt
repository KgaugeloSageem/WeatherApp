package com.sure.weatherapp.mapview.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sure.weatherapp.ui.theme.AmberYellow
import com.sure.weatherapp.ui.theme.DarkCyan
import com.sure.weatherapp.ui.theme.WeatherAppTheme

@Composable
fun DailyForecastItem(
    modifier: Modifier = Modifier,
    date: String,
    minTemp: String,
    maxTemp: String,
    dayDescription: String,
    nightDescription: String
) {
    Column(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(BorderStroke(2.dp, Color.Cyan), shape = RoundedCornerShape(12.dp))
        ) {
            Row {
                DateViewCard(
                    day = date.substring(0, 2),
                    monthAndYear = date.substring(3)
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        modifier = Modifier.padding(top = 16.dp),
                        text = "Temperature",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    Row(
                        modifier = Modifier
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier.weight(1f),
                            text = "Min: $minTemp",
                            fontWeight = FontWeight.W700,
                            fontSize = 16.sp,
                            color = DarkCyan
                        )

                        Text(
                            modifier = Modifier,
                            text = "Max: $maxTemp",
                            fontWeight = FontWeight.W700,
                            fontSize = 16.sp,
                            color = Color.Red
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier.weight(1f),
                            text = "Day: $dayDescription",
                            fontWeight = FontWeight.W700,
                            fontSize = 10.sp,
                            color = AmberYellow
                        )

                        Text(
                            modifier = Modifier,
                            text = "Night: $nightDescription",
                            fontWeight = FontWeight.W700,
                            fontSize = 10.sp,
                            color = Color.Blue
                        )
                    }

                }
            }
        }
    }
}

@Composable
private fun DateViewCard(
    day: String,
    monthAndYear: String
) {
    Column(
        Modifier
            .background(
                Color.White,
                shape = RoundedCornerShape(12.dp)
            )
            .border(BorderStroke(2.dp, Color.Cyan), shape = RoundedCornerShape(12.dp))
            .padding(8.dp)
            .height(150.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier,
            text = day,
            fontWeight = FontWeight.W700,
            fontSize = 80.sp,
        )
        Text(
            modifier = Modifier
                .padding(top = 10.dp),
            text = monthAndYear,
            fontWeight = FontWeight.W700,
            fontSize = 16.sp,
            color = Color.Red
        )

    }

}

@Composable
@Preview
private fun DailyForecastItemPreview() {
    WeatherAppTheme {
        DailyForecastItem(
            date = "20 May 2000",
            minTemp = "20 C",
            maxTemp = "55 C",
            dayDescription = "Sunny",
            nightDescription = "Clear"
        )
    }
}


