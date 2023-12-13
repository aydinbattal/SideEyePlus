package sheridan.czuberad.sideeye.UI

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import sheridan.czuberad.sideeye.ApplicationLogic.IndependentDriverLogic
import sheridan.czuberad.sideeye.Domain.Questionnaire
import sheridan.czuberad.sideeye.Domain.ReactionTest
import sheridan.czuberad.sideeye.Domain.Session
import sheridan.czuberad.sideeye.Services.CompanyService
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.log


@Composable
fun SessionHistory(navController: NavHostController) {


    val viewModel: IndependentDriverLogic = viewModel()

    LaunchedEffect(Unit){
        viewModel.getSessionCardInfoList()
    }

    val sessionList = viewModel.sessions.value ?: emptyList()

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color.White),
        verticalArrangement = Arrangement.Top,
    horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Session History", fontSize = 30.sp, modifier = Modifier.padding(10.dp))

            SessionList(navController,sessionList)

    }
}



@Composable
fun SessionList(navController: NavHostController, sessionList: List<Session>) {

    
    LazyColumn{
        items(sessionList){item ->
            SessionListItem(item, navController)
        }
    }
    

}

@Composable
fun SessionListItem(item: Session, navController: NavHostController) {

    val companyService = CompanyService()
    var reactionTestResult by remember { mutableStateOf<ReactionTest?>(null) }
    var questionnaireResult by remember { mutableStateOf<Questionnaire?>(null) }

    if (item != null) {
        LaunchedEffect(item.reactionTestUUID) {
            companyService.fetchReactionTestById(item.reactionTestUUID ?: "") { result ->
                reactionTestResult = result
            }

            companyService.fetchQuestionnaireById(item.questionnaireUUID ?: "") { result ->
                questionnaireResult = result
            }
        }
    }
//    val viewModel: IndependentDriverLogic = viewModel()
//
//    LaunchedEffect(Unit){
//        item.sessionUUID?.let { viewModel.getSessionTimeLine(it) }
//    }
//    val alertObjectList = viewModel.alertSessionlist.value
//    val fatigueTimeStampList = viewModel.fatigueTimeStampList.value
//
//    viewModel.setTimeLineList(alertObjectList, fatigueTimeStampList)
//
//    val sessionTimeLine = viewModel.sessionTimeLine.value

    Card(modifier = Modifier
        .fillMaxWidth()
        .height(175.dp)
        .padding(10.dp)
        .clickable { navController.navigate("sessionDetail/${item.sessionUUID}") },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)){


            Column(modifier = Modifier.fillMaxWidth(0.45f)) {
                Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.SpaceBetween,verticalAlignment = Alignment.CenterVertically){
                    Text(text = SimpleDateFormat("MMM dd, yyy", Locale.getDefault()).format(item.startSession), fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(SimpleDateFormat("HH:mm", Locale.getDefault()).format(item.startSession), fontSize = 12.sp )
                }
                Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.SpaceBetween,verticalAlignment = Alignment.CenterVertically){
                    Text(
                        text = item.endSession?.let {
                            SimpleDateFormat("MMM dd, yyy", Locale.getDefault()).format(it)
                        } ?: "Session is still running...",
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = item.endSession?.let {
                            SimpleDateFormat("HH:mm", Locale.getDefault()).format(it)
                        } ?: "",
                        fontSize = 12.sp
                    )
                }
                Spacer(modifier = Modifier.fillMaxHeight(0.2f))

                Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.SpaceBetween){
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Alerts",fontSize = 12.sp)
                        val size = item.alertUUIDList?.size ?:0
                        Text(text = size.toString(), fontWeight = FontWeight.ExtraBold)

                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Fatigue", fontSize = 12.sp)
                        Text(text = item.fatigueList?.size.toString(), fontWeight = FontWeight.ExtraBold)
                    }
                }

                Spacer(modifier = Modifier.fillMaxHeight(0.2f))


                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Reaction Tests: ", fontWeight = FontWeight.Bold)

                    Text(
                        text = buildAnnotatedString {
                            val result = reactionTestResult?.isPassed
                            val textColor = when {
                                result == true -> Color.Green
                                result == false -> Color.Red
                                else -> Color.Black
                            }

                            withStyle(
                                style = SpanStyle(color = textColor)
                            ) {
                                append(result?.let { if (it) "PASSED" else "FAILED" } ?: "N/A")
                            }
                        }
                    )

                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Questionnaire: ", fontWeight = FontWeight.Bold)

                    Text(
                        text = buildAnnotatedString {
                            val result = questionnaireResult?.isPassed
                            val textColor = when {
                                result == true -> Color.Green
                                result == false -> Color.Red
                                else -> Color.Black
                            }

                            withStyle(
                                style = SpanStyle(color = textColor)
                            ) {
                                append(result?.let { if (it) "PASSED" else "FAILED" } ?: "N/A")
                            }
                        }
                    )

                }

            }

                Card(modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )) {
                    Canvas(modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.5f)
                        .padding(top = 15.dp, bottom = 15.dp, start = 5.dp, end = 5.dp)
                    ) {
                        val dataPoints = listOf(0, 0, 0, 10, 10, 0, 0, 0, 0, 0, 0, 10, 0, 0)
                        val randomizedDataPoints = dataPoints.shuffled()
                        val maxDataValue = randomizedDataPoints.maxOrNull() ?: 1
                        val dataPointsOffsets = randomizedDataPoints.mapIndexed { index, value ->
                            Offset(
                                x = size.width * (index / (randomizedDataPoints.size - 1).toFloat()),
                                y = size.height - (size.height * (value / maxDataValue.toFloat()))
                            )
                        }

                        // Draw trend line by connecting points
                        for (i in 0 until dataPointsOffsets.size - 1) {
                            val start = dataPointsOffsets[i]
                            val end = dataPointsOffsets[i + 1]
                            drawLine(
                                color = Color(0xFF39AFEA),
                                start = start,
                                end = end,
                                strokeWidth = 2.dp.toPx()
                            )
                        }
                    }

//                    Canvas(modifier = Modifier
//                        .fillMaxWidth()
//                        .fillMaxHeight(0.5f)
//                        .padding(top = 35.dp, bottom = 35.dp, start = 15.dp, end = 15.dp)) {
//
//                        val dataPoints = listOf(10, 0, 0, 10, 10, 0, 0, 0, 0, 0, 0, 10, 0, 0)
//                        val randomizedDataPoints = dataPoints.shuffled()
//                        val maxDataValue = randomizedDataPoints.maxOrNull() ?: 1
//                        val dataPointsOffsets = randomizedDataPoints.mapIndexed { index, value ->
//
//                            Offset(
//                                x = size.width * (index / (randomizedDataPoints.size - 1).toFloat()),
//                                y = size.height * (1 - (value / maxDataValue.toFloat()))
//                            )
//                        }
//
//                        // Draw trend line by connecting points
//                        for (i in 0 until dataPointsOffsets.size - 1) {
//                            val start = dataPointsOffsets[i]
//                            val end = dataPointsOffsets[i + 1]
//                            drawLine(
//                                color = Color(0xFF39AFEA),
//                                start = start,
//                                end = end,
//                                strokeWidth = 2.dp.toPx()
//                            )
//                        }
//
//
//                    }



                }

                //add here






        }

        
    }

}

@Preview
@Composable
fun Preview(){
    //SessionList(navController)
}
