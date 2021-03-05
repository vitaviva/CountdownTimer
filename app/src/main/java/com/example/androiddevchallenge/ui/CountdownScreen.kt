package com.example.androiddevchallenge.ui

import android.util.Log
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.concurrent.TimeUnit


@Composable
fun DisplayScreen(
    sumTime: Long,
    onCancel: () -> Unit
) {

    var trigger by remember { mutableStateOf(sumTime) }
    val elapsed by animateIntAsState(
        targetValue = trigger.toInt(),
        animationSpec = tween(sumTime.toInt(), easing = LinearEasing)
    )

    Log.e("wangp", trigger.toString())
    DisposableEffect(Unit) {
        trigger = 0
        onDispose { }
    }

    val (minutes, seconds, mills) = remember(elapsed) {
        val min = (elapsed / 1000 / 60).coerceAtLeast(0)
        val sec = (elapsed / 1000 % 60).coerceAtLeast(0)
        val mills = (elapsed % 1000)
        Triple(min, sec, mills)
    }


    Box() {
        Spacer(modifier = Modifier.size(30.dp))
        Column() {
            Row(Modifier.padding(start = 100.dp, end = 100.dp)) {
                CountdownText(modifier = Modifier.weight(1f), minutes)
                Text(":")
                CountdownText(modifier = Modifier.weight(1f), seconds)
                Text(":")
                CountdownText(modifier = Modifier.weight(1f), mills)

            }

            Spacer(modifier = Modifier.height(30.dp))

            CancelButton(onCancel)
        }

        Spacer(modifier = Modifier.size(10.dp))

        AnimationCircleCanvas(
            Modifier.align(Alignment.Center),
            sum = sumTime
        )

    }

}


@Composable
fun CountdownText(modifier: Modifier, value: Int) {
    Text(
        value.toString(),
        textAlign = TextAlign.Center,
        modifier = modifier,
    )
}


@Composable
private fun CancelButton(onCancel: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(start = 100.dp, end = 100.dp)
    ) {
        Button(
            modifier = Modifier.weight(1f),
            onClick = onCancel,
        ) {
            Text(text = "Cancel")
        }
    }

}

@Composable
private fun AnimationCircleCanvas(modifier: Modifier, sum: Long) {
    val transition = rememberInfiniteTransition()
    var isFinished by remember { mutableStateOf(false) }


    val animatedFloat by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(1000), RepeatMode.Restart)
    )
    val animatedScale by transition.animateFloat(
        initialValue = 1f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(tween(2000), RepeatMode.Reverse)
    )

    var trigger by remember { mutableStateOf(0f) }

    val animate2 by animateFloatAsState(
        targetValue = trigger,
        animationSpec = tween(
            durationMillis = sum.toInt(),
            easing = LinearEasing
        ),
        finishedListener = {
            isFinished = true
        }
    )

    DisposableEffect(Unit) {
        trigger = 360f
        onDispose {}
    }

    val stroke = Stroke(8f)
    val color = MaterialTheme.colors.primary
    val secondColor = MaterialTheme.colors.secondary
    Canvas(
        modifier = modifier
            .padding(16.dp)
            .size(400.dp)
    ) {
        val diameter = size.minDimension
        val radius = diameter / 2f
        val insideRadius = radius - stroke.width
        val topLeftOffset = Offset(10f, 10f)
        val size = Size(insideRadius * 2, insideRadius * 2)
        val rotationAngle = animatedFloat

        if (!isFinished) {

            drawCircle(
                color = secondColor,
                style = stroke,
                radius = radius * animatedScale
            )

            drawArc(
                color = color,
                startAngle = rotationAngle,
                sweepAngle = 150f,
                topLeft = topLeftOffset,
                size = size,
                useCenter = false,
                style = stroke,
            )

        }

        drawArc(
            startAngle = 270f,
            sweepAngle = animate2,
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.Yellow.copy(0.5f),
                    Color.Red.copy(0.5f),
                    Color.Blue.copy(0.5f)
                ),
            ),
            useCenter = true,
            style = Fill,
        )
    }
}

@Preview
@Composable
fun DisplayPreview() {
    DisplayScreen(0) {}
}

private fun interval(sumTime: Long, timeInMillis: Long, timeUnit: TimeUnit): Flow<Long> = flow {

    val delayTime = when (timeUnit) {
        TimeUnit.MICROSECONDS -> timeInMillis / 1000
        TimeUnit.NANOSECONDS -> timeInMillis / 1_000_000
        TimeUnit.SECONDS -> timeInMillis * 1000
        TimeUnit.MINUTES -> 60 * timeInMillis * 1000
        TimeUnit.HOURS -> 60 * 60 * timeInMillis * 1000
        TimeUnit.DAYS -> 24 * 60 * 60 * timeInMillis * 1000
        else -> timeInMillis
    }

    var sum = sumTime
    while (sum > 0) {
        delay(delayTime)
        sum -= delayTime
        emit(sum)
    }

}

