package sheridan.czuberad.sideeye.UI

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.android.gms.tasks.Task
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.Wearable
import sheridan.czuberad.sideeye.`Application Logic`.IndependentDriverLogic
import sheridan.czuberad.sideeye.Domain.Driver
import sheridan.czuberad.sideeye.Domain.Session
import sheridan.czuberad.sideeye.EyeDetectionActivity
import sheridan.czuberad.sideeye.HomeTestsActivity
import sheridan.czuberad.sideeye.R
import java.text.SimpleDateFormat


@Composable
fun DriverHome(navController: NavHostController) {

    var needsUpdate by remember { mutableStateOf(false) }
    var graphData: MutableMap<Int, Int>? = null
    val viewModel: IndependentDriverLogic = viewModel()
    LaunchedEffect(Unit){
        viewModel.getSessionCardInfoList()
        /*viewModel.getSessionHistoryMap {
            Log.d("YOO", "HOME UI Resulting Map: $it")
            graphData = it
        }*/
        viewModel.getSessionHistoryMap()
    }



    val sessionList = viewModel.sessions.value ?: emptyList()
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

    val graphDataFull = viewModel.sessionHistoryMap.value

    graphData = graphDataFull?.entries?.take(10)?.associate { it.toPair() } as MutableMap<Int, Int>?

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

        Column() {
            InfoCard(currentDriver)
            Card(
                modifier = Modifier
                    .width(screenWidth)
                    .height(screenHeight / 3)
                    .zIndex(1f)
                    .offset(y = (-50).dp),

                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                shape = RoundedCornerShape(15.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(verticalArrangement = Arrangement.Top) {
                    Text(text = "Latest Session's Trend", color = Color(0xFF39AFEA),style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp  // Adjust the font size as needed
                    ), modifier = Modifier.padding(start = 30.dp, top = 10.dp))
                    graphData?.let {
                        LineChart(
                            data = it,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                    }
                }
                

            }
        }
        Column(verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxHeight()) {
            SessionCardListView(navController,sessionList)
            buttonLayout(context)
        }
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


@Composable
fun buttonLayout(context: Context) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(3.dp),
            horizontalArrangement = Arrangement.SpaceBetween) {
            Button(
                modifier = Modifier.width(185.dp),
                shape = RectangleShape,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF39AFEA)),
                onClick = {
                    val intent = Intent(context, EyeDetectionActivity::class.java)
                    context.startActivity(intent)

                }) {
                Text("Driving Session")
            }
            Button(

                shape = RectangleShape,
                modifier = Modifier.width(185.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF39AFEA)),
                onClick = {
                    val intent = Intent(context, HomeTestsActivity::class.java)
                    context.startActivity(intent)
                }) {
                Text("Fatigue Assessment")
            }

        }

}



@Composable
fun InfoCard(currentDriver: Driver) {


    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    Card(modifier = Modifier
        .width(screenWidth)
        .height(screenHeight / 4),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    colors = CardDefaults.cardColors(
        containerColor = Color(0xFF39AFEA)
    ),
    shape = RectangleShape) {
        
        Column(modifier = Modifier.padding(16.dp)) {
            currentDriver.name?.let { Text(text = "Hi, ${currentDriver.name}", color = Color.White, fontSize = 35.sp, fontWeight = FontWeight.Bold) }
            Text(
                text = "${currentDriver.companyName}",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 0.dp, bottom = 0.dp)
                .fillMaxWidth()
        ) {

            Text(
                text = "Welcome back to your driver dashboard!",
                color = Color.White,
                fontSize = 16.sp
            )
            // Add more details or customization based on your CompanyOwner data model
        }

//        Row(verticalAlignment = Alignment.CenterVertically) {
//
//            Image(painter = painterResource(id = R.drawable.ic_launcher_foreground), contentDescription = "profile pic")
//            Column {
//                currentDriver.name?.let {
//                    Text(
//                        text = it,
//                        color = Color.White
//                    )
//                }
//                currentDriver.email?.let {
//                    Text(
//                        text = it,
//                        color = Color.White
//                    )
//                }
//
//            }
//
//        }




    }
}

@Composable
fun SessionCardListView(navController: NavHostController, sessionList: List<Session>?) {
    //Card(modifier = Modifier.offset(y = (-100).dp),colors = CardDefaults.cardColors(
    Card(colors = CardDefaults.cardColors(
        containerColor = Color.White
    )) {
        
        Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                modifier = Modifier.padding(start = 7.dp),
                text = "Latest Sessions",
                color = Color(0xFF39AFEA),
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp  // Adjust the font size as needed
                )
            )

            TextButton(onClick = {navController.navigate("sessionListView")}, modifier = Modifier.height(33.dp),
            ) {
                Text(text = "View All", fontSize = 12.sp, color = Color(0xFF39AFEA))
            }
            
        }
        LazyRow(
            //modifier = Modifier.padding(bottom = 10.dp)
        ){
            if (sessionList != null) {
                val latestSessions = sessionList.take(10)
                //items(listOf(*sessionList.toTypedArray())){
                items(latestSessions){

                    Card(
                        modifier = Modifier
                            .height(150.dp)
                            .width(205.dp)
                            .padding(8.dp)
                            .clickable { navController.navigate("sessionDetail/${it.sessionUUID}") },
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )
                    ) {

                        Column(modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp), verticalArrangement = Arrangement.SpaceBetween) {

                            Column() {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(text = SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(it.startSession), fontWeight = FontWeight.Bold)
                                    Text(text = SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(it.startSession))
                                }

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(text = SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(it.endSession), fontWeight = FontWeight.Bold)
                                    Text(text = SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(it.endSession))
                                }
                            }



                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {

                                Column(horizontalAlignment = Alignment.CenterHorizontally){
                                    Text(text = "Alerts",
                                        color = Color(0xFF39AFEA),
                                        style = TextStyle(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp  // Adjust the font size as needed
                                        ))
                                    Text(text = it.alertUUIDList?.size.toString(),color = Color(0xFF39AFEA),
                                        style = TextStyle(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 20.sp  // Adjust the font size as needed
                                        ))
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally){
                                    Text(text = "Fatigue",
                                        color = Color(0xFF39AFEA),
                                        style = TextStyle(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp  // Adjust the font size as needed
                                        ))

                                    Text(text = it.fatigueList?.size.toString(),color = Color(0xFF39AFEA),
                                        style = TextStyle(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 20.sp  // Adjust the font size as needed
                                        ))
                                }


                            }


                        }


                        //                    Column(modifier = Modifier.fillMaxWidth()) {
        //
        //                        Row(horizontalArrangement = Arrangement.SpaceBetween){
        //                            Column(horizontalAlignment = Alignment.CenterHorizontally){
        //                                Text(text = "Alerts",
        //                                    color = Color(0xFF39AFEA),
        //                                    style = TextStyle(
        //                                        fontWeight = FontWeight.Bold,
        //                                        fontSize = 15.sp  // Adjust the font size as needed
        //                                    ))
        //                                Spacer(modifier = Modifier.width(100.dp))
        //                                Text(text = "15",color = Color(0xFF39AFEA),
        //                                    style = TextStyle(
        //                                        fontWeight = FontWeight.Bold,
        //                                        fontSize = 20.sp  // Adjust the font size as needed
        //                                    ))
        //                            }
        //
        //                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
        //                                Text(text = "Fatigue",
        //                                    color = Color(0xFF39AFEA),
        //                                    style = TextStyle(
        //                                        fontWeight = FontWeight.Bold,
        //                                        fontSize = 15.sp  // Adjust the font size as needed
        //                                    ))
        //                                Spacer(modifier = Modifier.width(300.dp))
        //                                Text(text = "30",color = Color(0xFF39AFEA),
        //                                    style = TextStyle(
        //                                        fontWeight = FontWeight.Bold,
        //                                        fontSize = 20.sp  // Adjust the font size as needed
        //                                    ))
        //
        //                            }
        //
        //
        //                        }
        //                        Text(text = "December 13, 2022")
        //                        Text(text = "Duration: 1:03:34")
        //
        //
        //                    }

                    }
                }
            }



        }
    }
    //Create DriverService method for retrieving Sessions and Alerts
    //Create IndependentDriver Logic method for mapping Sessions and Alerts return itemList
// Session(StartTime, EndTime, AlertCount, AlertList(pass into detail page))

}

fun lerp(start: Float, stop: Float, fraction: Float): Float {
    return start + fraction * (stop - start)
}

@Preview
@Composable
fun preview(){
    //buttonLayout()
}
