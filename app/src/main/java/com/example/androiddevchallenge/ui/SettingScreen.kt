package com.example.androiddevchallenge.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Deprecated("")
@Composable
fun SettingScreen(onStart: (it: Long) -> Unit) {
    val context = LocalContext.current

    var minutes by remember { mutableStateOf(0) }
    var seconds by remember { mutableStateOf(0) }

    Column(modifier = Modifier.fillMaxHeight()) {

        Spacer(modifier = Modifier.height(100.dp))
        Row(
            modifier = Modifier
                .padding(10.dp)
        ) {

            TimeInputField(
                modifier = Modifier.weight(1f),
                value = minutes,
                label = "Minutes"
            ) {
                minutes = it.coerceAtMost(60)
            }

            Text(
                ":", modifier = Modifier
                    .padding(start = 5.dp, end = 5.dp)
                    .align(Alignment.CenterVertically),
                fontSize = 30.sp
            )

            TimeInputField(
                modifier = Modifier.weight(1f),
                value = seconds,
                label = "Seconds"
            ) {
                seconds = it.coerceAtMost(60)
            }

        }

        Spacer(modifier = Modifier.size(20.dp))

        Slider(
            modifier = Modifier.padding(start = 10.dp, end = 10.dp),
            value = (minutes * 60 + seconds).toFloat() * 1000,
            valueRange = 0f..60 * 60 * 1000F,
            steps = 5000,
            onValueChange = {
                minutes = (it / 1000 / 60).toInt()
                seconds = (it / 1000 % 60).toInt()
            },
            colors = SliderDefaults
                .colors(thumbColor = MaterialTheme.colors.secondary)
        )

        Text(
            text = "Slide to set the time",
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.size(20.dp))
        Button(modifier = Modifier
            .padding(start = 100.dp, end = 100.dp, top = 50.dp)
            .align(Alignment.CenterHorizontally),
            onClick = {
                onStart((minutes * 60 + seconds).toLong() * 1000)
            }) {
            Text("Start")
        }
    }

}

@Composable
fun TimeInputField(
    modifier: Modifier = Modifier,
    value: Int,
    label: String,
    onValueChange: (it: Int) -> Unit

) {
    Column(modifier) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = label,
            textAlign = TextAlign.Center
        )
        Image(
            imageVector = Icons.Default.ArrowDropUp,
            contentDescription = null,
            modifier = Modifier
                .size(80.dp)
                .clickable { onValueChange(value + 1) }
                .align(Alignment.CenterHorizontally)
        )

        TextField(
            value = "$value",
            onValueChange = {
                onValueChange(if (it.isEmpty()) 0 else it.toInt())
            },
            placeholder = {
                Text("Minutes")
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            isError = value > 60,
            textStyle = TextStyle.Default.copy(
                fontSize = 50.sp,
                textAlign = TextAlign.Center
            ),
            colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent),
            modifier = Modifier.fillMaxWidth()
        )
        Image(
            imageVector = Icons.Default.ArrowDropDown,
            contentDescription = null,
            modifier = Modifier
                .size(80.dp)
                .clickable { onValueChange(value - 1) }
                .align(Alignment.CenterHorizontally))
    }
}


@Preview
@Composable
fun SettingPreview() {
    SettingScreen {}
}