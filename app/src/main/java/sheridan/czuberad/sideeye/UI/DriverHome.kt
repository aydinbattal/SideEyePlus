package sheridan.czuberad.sideeye.UI

import android.content.Intent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import sheridan.czuberad.sideeye.`Application Logic`.IndependentDriverLogic
import sheridan.czuberad.sideeye.Domain.Driver
import sheridan.czuberad.sideeye.EyeDetectionActivity
import sheridan.czuberad.sideeye.R
import sheridan.czuberad.sideeye.ReactionTestActivity
import kotlin.math.roundToInt


@Composable
fun DriverHome() {
    val data = listOf(
        Pair(6, 111.45),
        Pair(7, 111.0),
        Pair(8, 113.45),
        Pair(9, 112.25),
        Pair(10, 116.45),
        Pair(11, 113.35),
        Pair(12, 118.65),
        Pair(13, 110.15),
        Pair(14, 113.05),
        Pair(15, 114.25),
        Pair(16, 116.35),
        Pair(17, 117.45),
        Pair(18, 112.65),
        Pair(19, 115.45),
        Pair(20, 111.85)
    )
    val configuration = LocalConfiguration.current
    val context = LocalContext.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp


    val independentDriverLogic = IndependentDriverLogic()
    val currentDriver = independentDriverLogic.getCurrentDriverInfo()
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
            )// Optional: if you want to remove rounded corners
            // Optional: if you want to set a custom color
        ) {
            LineChart(
                data = data,
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

@Composable
fun LineChart(
    data: List<Pair<Int, Double>> = emptyList(),
    modifier: Modifier = Modifier,
    title: String = "Session Overview"
) {
    val spacing = 100f
    val graphColor = Color(0xFF39AFEA)
    val upperValue = remember { (data.maxOfOrNull { it.second }?.plus(1))?.roundToInt() ?: 0 }
    val lowerValue = remember { (data.minOfOrNull { it.second }?.toInt() ?: 0) }

    val drawingProgress = remember { Animatable(0f) }

    LaunchedEffect(drawingProgress) {
        drawingProgress.animateTo(
            targetValue = data.size.toFloat(),
            animationSpec = tween(durationMillis = 2000)
        )
    }



    Canvas(modifier = modifier) {
        val spacePerHour = (size.width - spacing) / data.size
        val height = size.height

        val path = Path()
        val drawingLimit = drawingProgress.value.toInt()

        for (i in 0 until drawingLimit) {
            val info = data[i]
            val ratio = (info.second - lowerValue) / (upperValue - lowerValue)
            val x1 = spacing + i * spacePerHour
            val y1 = height - spacing - (ratio * height).toFloat()

            if (i == 0) {
                path.moveTo(x1, y1)
            } else {
                path.lineTo(x1, y1)
            }
        }

        if (drawingLimit > 0 && drawingLimit < data.size) {
            val startInfo = data[drawingLimit - 1]
            val endInfo = data[drawingLimit]
            val t = drawingProgress.value - drawingLimit

            val startX = spacing + (drawingLimit - 1) * spacePerHour
            val startY = height - spacing - ((startInfo.second - lowerValue) / (upperValue - lowerValue) * height).toFloat()

            val endX = spacing + drawingLimit * spacePerHour
            val endY = height - spacing - ((endInfo.second - lowerValue) / (upperValue - lowerValue) * height).toFloat()

            val interpolatedX = lerp(startX, endX, t)
            val interpolatedY = lerp(startY, endY, t)

            path.lineTo(interpolatedX, interpolatedY)
        }

        drawPath(
            path = path,
            color = graphColor,
            style = Stroke(
                width = 2.dp.toPx(),
                cap = StrokeCap.Round
            )
        )

    }
}

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
