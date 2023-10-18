package sheridan.czuberad.sideeye.UI

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Debug
import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.google.android.gms.tasks.Task
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.Wearable
import sheridan.czuberad.sideeye.`Application Logic`.IndependentDriverLogic
import sheridan.czuberad.sideeye.Domain.Driver
import sheridan.czuberad.sideeye.EyeDetectionActivity
import sheridan.czuberad.sideeye.R
import sheridan.czuberad.sideeye.ReactionTestActivity
import kotlin.math.roundToInt


@Composable
fun DriverHome() {
    val data = listOf(
        Pair(1, 0),
        Pair(2, 5),
        Pair(3, 2),
        Pair(4, 0),
        Pair(5, 1),
        Pair(6, 10),
        Pair(7, 2),
        Pair(8, 3),
        Pair(9, 0),
        Pair(10, 0)

    )
    val myMutableMap = mutableMapOf<Int, Int>()

    for (i in 1..10) {
        myMutableMap[i] = (1..10).random()
    }
    val configuration = LocalConfiguration.current
    val context = LocalContext.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    //sendMessage(context)
    val independentDriverLogic = IndependentDriverLogic()
    Log.d("YOO", "CALLING FROM UI")
    val currentDriver = independentDriverLogic.getCurrentDriverInfo()

//    var graphData: MutableMap<Int, Int>? = null
//    independentDriverLogic.getSessionHistoryMap{
//
//        Log.d("YOO", "HOME UI Resulting Map: $it")
//        graphData = it
//
//    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        verticalArrangement = Arrangement.Top
    ) {

        InfoCard(currentDriver)

        Card(
            modifier = Modifier
                .width(screenWidth)
                .height(screenHeight / 3)
                .zIndex(1f)
                .offset(y = (-50).dp)
                .padding(10.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            shape = RoundedCornerShape(15.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
                LineChart(
                    data = myMutableMap,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .align(Alignment.CenterHorizontally)
                )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(3.dp),
            horizontalArrangement = Arrangement.Center) {
            Button(
                modifier = Modifier.weight(1f),
                shape = RectangleShape,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF39AFEA)),
                onClick = {
                    val intent = Intent(context, EyeDetectionActivity::class.java)
                    context.startActivity(intent)

                }) {
                Text("Session Tracking")
            }
            Spacer(modifier = Modifier.width(5.dp))
            Button(

                shape = RectangleShape,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF39AFEA)),
                onClick = {
                    val intent = Intent(context, ReactionTestActivity::class.java)
                    context.startActivity(intent)
                }) {
                Text("Reaction Test")
            }
            
        }

//        LineChart(
//            data = data,
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(300.dp)
//                .align(Alignment.CenterHorizontally)
//                .border(1.dp, Color.Blue)
//        )
    }


}

fun sendMessage(context: Context) {
    val messageClient: MessageClient = Wearable.getMessageClient(context)

    val nodes: Task<List<Node>> = Wearable.getNodeClient(context).connectedNodes
    nodes.addOnCompleteListener { task ->
        if (task.isSuccessful && task.result != null) {
            val connectedNode = task.result?.firstOrNull()
            connectedNode?.let {
                Log.d("Yoo","Message being sent")
                messageClient.sendMessage(it.id, "/path_to_message", "69".toByteArray()).addOnSuccessListener {
                    Log.d("Yoo","Message sent")
                }
            }
        }
    }
}

@Composable
fun LineChart(
    data: MutableMap<Int, Int> = mutableMapOf(),
    modifier: Modifier = Modifier,
    title: String = "Session Overview"
) {
    val spacing = 100f
    val graphColor = Color(0xFF39AFEA)
    val upperValue = remember { (data.values.max().plus(1)) }
    val lowerValue = remember { data.values.min().toInt() }

    val drawingProgress = remember { Animatable(0f) }

    LaunchedEffect(drawingProgress) {
        drawingProgress.animateTo(
            targetValue = data.size.toFloat(),
            animationSpec = tween(durationMillis = 2000)
        )
    }

    Canvas(modifier = modifier) {
        val spacePerHour = (size.width - spacing) / data.size
        val lastDataPointX = spacing + (data.size - 1) * spacePerHour
        val height = size.height

        val graphTopPaddingRatio = 0.2f
        val graphBottomPaddingRatio = 0.2f
        val availableHeight = height * (1 - graphTopPaddingRatio - graphBottomPaddingRatio)
        val graphTopPadding = height * graphTopPaddingRatio

        val path = Path()
        val drawingLimit = drawingProgress.value.toInt()

        for ((index, entry) in data.entries.withIndex()) {
            if (index >= drawingLimit) break

            val ratio = (entry.value - lowerValue).toFloat() / (upperValue - lowerValue)
            val x1 = spacing + index * spacePerHour
            val y1 = graphTopPadding + (1 - ratio) * availableHeight

            if (index == 0) {
                path.moveTo(x1, y1)
            } else {
                path.lineTo(x1, y1)
            }
        }

        if (drawingLimit > 0 && drawingLimit < data.size) {
            val startValue = data.values.toList()[drawingLimit - 1]
            val endValue = data.values.toList()[drawingLimit]
            val t = drawingProgress.value - drawingLimit

            val startX = spacing + (drawingLimit - 1) * spacePerHour
            val startY = graphTopPadding + (1 - (startValue - lowerValue).toFloat() / (upperValue - lowerValue)) * availableHeight

            val endX = spacing + drawingLimit * spacePerHour
            val endY = graphTopPadding + (1 - (endValue - lowerValue).toFloat() / (upperValue - lowerValue)) * availableHeight

            val interpolatedX = lerp(startX, endX, t)
            val interpolatedY = lerp(startY, endY, t)

            path.lineTo(interpolatedX, interpolatedY)
        }

        drawPath(
            path = path,
            color = graphColor,
            style = Stroke(
                width = 4.dp.toPx(),
                cap = StrokeCap.Round
            )
        )

        drawLine(
            color = Color(0xFF39AFEA),
            start = Offset(spacing, size.height * graphBottomPaddingRatio),
            end = Offset(spacing, size.height - size.height * graphTopPaddingRatio + 10.dp.toPx()),
            strokeWidth = 2.dp.toPx()
        )
        // X axis line
        drawLine(
            color = Color(0xFF39AFEA),
            start = Offset(spacing, size.height - size.height * graphTopPaddingRatio + 10.dp.toPx()),
            end = Offset(lastDataPointX, size.height - size.height * graphTopPaddingRatio + 10.dp.toPx()),
            strokeWidth = 2.dp.toPx()
        )
    }
}




//@Composable
//fun LineChart(
//    data: List<Pair<Int, Int>> = emptyList(),
//    modifier: Modifier = Modifier,
//    title: String = "Session Overview"
//) {
//    val spacing = 100f
//    val graphColor = Color(0xFF39AFEA)
//    val upperValue = remember { (data.maxOfOrNull { it.second }?.plus(1)) ?: 0 }
//    val lowerValue = remember { (data.minOfOrNull { it.second }?.toInt() ?: 0) }
//
//    val drawingProgress = remember { Animatable(0f) }
//
//    LaunchedEffect(drawingProgress) {
//        drawingProgress.animateTo(
//            targetValue = data.size.toFloat(),
//            animationSpec = tween(durationMillis = 2000)
//        )
//    }
//
//    Canvas(modifier = modifier) {
//        val spacePerHour = (size.width - spacing) / data.size
//        val lastDataPointX = spacing + (data.size - 1) * spacePerHour
//        val height = size.height
//
//
//        val graphTopPaddingRatio = 0.2f
//        val graphBottomPaddingRatio = 0.2f
//        val availableHeight = height * (1 - graphTopPaddingRatio - graphBottomPaddingRatio)
//        val graphTopPadding = height * graphTopPaddingRatio
//
//        val path = Path()
//        val drawingLimit = drawingProgress.value.toInt()
//
//        for (i in 0 until drawingLimit) {
//            val info = data[i]
//            val ratio = (info.second - lowerValue).toFloat() / (upperValue - lowerValue)
//            val x1 = spacing + i * spacePerHour
//            val y1 = graphTopPadding + (1 - ratio) * availableHeight
//
//            if (i == 0) {
//                path.moveTo(x1, y1)
//            } else {
//                path.lineTo(x1, y1)
//            }
//        }
//
//        if (drawingLimit > 0 && drawingLimit < data.size) {
//            val startInfo = data[drawingLimit - 1]
//            val endInfo = data[drawingLimit]
//            val t = drawingProgress.value - drawingLimit
//
//            val startX = spacing + (drawingLimit - 1) * spacePerHour
//            val startY = graphTopPadding + (1 - (startInfo.second - lowerValue).toFloat() / (upperValue - lowerValue)) * availableHeight
//
//            val endX = spacing + drawingLimit * spacePerHour
//            val endY = graphTopPadding + (1 - (endInfo.second - lowerValue).toFloat() / (upperValue - lowerValue)) * availableHeight
//
//            val interpolatedX = lerp(startX, endX, t)
//            val interpolatedY = lerp(startY, endY, t)
//
//            path.lineTo(interpolatedX, interpolatedY)
//        }
//
//        drawPath(
//            path = path,
//            color = graphColor,
//            style = Stroke(
//                width = 4.dp.toPx(),
//                cap = StrokeCap.Round
//            )
//        )
//
//        drawLine(
//            color = Color(0xFF39AFEA),
//            start = Offset(spacing, size.height * graphBottomPaddingRatio),
//            end = Offset(spacing, size.height - size.height * graphTopPaddingRatio + 10.dp.toPx()),
//            strokeWidth = 2.dp.toPx()
//        )
//        //X axis line
//        drawLine(
//            color = Color(0xFF39AFEA),
//            start = Offset(spacing, size.height - size.height * graphTopPaddingRatio + 10.dp.toPx() ),
//            end = Offset(lastDataPointX, size.height - size.height * graphTopPaddingRatio + 10.dp.toPx()),
//            strokeWidth = 2.dp.toPx()
//        )
//
//
//
//    }
//}

@Composable
fun InfoCard(currentDriver: Driver) {


    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    Card(modifier = Modifier
        .width(screenWidth)
        .height(screenHeight / 3),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    colors = CardDefaults.cardColors(
        containerColor = Color(0xFF39AFEA)
    ),
    shape = RectangleShape) {

        Row(verticalAlignment = Alignment.CenterVertically) {

            Image(painter = painterResource(id = R.drawable.ic_launcher_foreground), contentDescription = "profile pic")
            Column {
                currentDriver.name?.let {
                    Text(
                        text = it,
                        color = Color.White
                    )
                }
                currentDriver.email?.let {
                    Text(
                        text = it,
                        color = Color.White
                    )
                }

            }

        }

    }
}

fun lerp(start: Float, stop: Float, fraction: Float): Float {
    return start + fraction * (stop - start)
}
