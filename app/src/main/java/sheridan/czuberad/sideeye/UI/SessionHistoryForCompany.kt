package sheridan.czuberad.sideeye.UI

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import sheridan.czuberad.sideeye.Domain.Questionnaire
import sheridan.czuberad.sideeye.Domain.ReactionTest
import sheridan.czuberad.sideeye.`Application Logic`.IndependentDriverLogic
import sheridan.czuberad.sideeye.Domain.Session
import sheridan.czuberad.sideeye.Services.CompanyService
import sheridan.czuberad.sideeye.`Application Logic`.DriverDetailsLogic
import java.text.SimpleDateFormat
import java.util.Locale


@Composable
fun SessionHistoryForCompany(navController: NavHostController, email: String?) {


    val viewModel: DriverDetailsLogic = viewModel()

    LaunchedEffect(Unit){
        if (email != null) {
            viewModel.getSessionHistory(email)
        }
    }

    val sessionList = viewModel.sessions.value ?: emptyList()

    Column(modifier = Modifier
        .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(45.dp)
                .background(color = Color(0xFF39AFEA)),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = "Session History",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(8.dp)
            )
        }

            SessionListForCompany(navController,sessionList)

    }
}



@Composable
fun SessionListForCompany(navController: NavHostController, sessionList: List<Session>) {

    val items = listOf("Item 1", "Item 2", "Item 3", "Item 4", "Item 5","item 6", "item 8")
    
    LazyColumn{
        items(sessionList){item ->
            SessionListForCompanyItem(item, navController)
        }
    }
    

}

@Composable
fun SessionListForCompanyItem(item: Session, navController: NavHostController) {
    val companyService = CompanyService()

    Card(modifier = Modifier
        .fillMaxWidth()
        .height(180.dp)
        .padding(10.dp).clickable { navController.navigate("sessionDetail/${item.sessionUUID}") },
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
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
                        Text(text = item.alertUUIDList?.size.toString(), fontWeight = FontWeight.ExtraBold)

                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Fatigue", fontSize = 12.sp)
                        Text(text = item.fatigueList?.size.toString(), fontWeight = FontWeight.ExtraBold)
                    }
                }

                Spacer(modifier = Modifier.fillMaxHeight(0.2f))


                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Reaction Time: ", fontWeight = FontWeight.Bold)

                    var reactionTestResult by remember { mutableStateOf<ReactionTest?>(null) }

                    LaunchedEffect(item.reactionTestUUID) {
                        // Fetch ReactionTest asynchronously
                        companyService.fetchReactionTestById(item.reactionTestUUID ?: "") { result ->
                            reactionTestResult = result
                        }
                    }

                    Text(text = "${reactionTestResult?.avgTime ?: "Not Determined"} ms")

                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Category: ", fontWeight = FontWeight.Bold)

                    var questionnaireResult by remember { mutableStateOf<Questionnaire?>(null) }

                    LaunchedEffect(item.questionnaireUUID) {
                        // Fetch ReactionTest asynchronously
                        companyService.fetchQuestionnaireById(item.questionnaireUUID ?: "") { result ->
                            questionnaireResult = result
                        }
                    }

                    Text(text = "${questionnaireResult?.category ?: "Not Determined"}")

                }

            }

            Card(modifier = Modifier.weight(1f).fillMaxHeight(),colors = CardDefaults.cardColors(
                containerColor = Color.White
            )) {
                Canvas(modifier = Modifier.fillMaxSize().padding(top = 35.dp, bottom = 35.dp, start = 15.dp, end = 15.dp)) {
                    val dataPoints = listOf(10, 100, 30, 70, 40, 10, 20, 34, 50, 10, 15, 50, 60, 100)
                    val maxDataValue = dataPoints.maxOrNull() ?: 1
                    val dataPointsOffsets = dataPoints.mapIndexed { index, value ->
                        // Map data value to canvas coordinate
                        Offset(
                            x = size.width * (index / (dataPoints.size - 1).toFloat()),
                            y = size.height * (1 - (value / maxDataValue.toFloat()))
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

            }

//            Column(modifier = Modifier.weight(0.5f)) {
//                Row(modifier = Modifier.fillMaxWidth(0.45f),horizontalArrangement = Arrangement.SpaceBetween,verticalAlignment = Alignment.CenterVertically){
//                    Text(text = "Oct 13, 2023", fontWeight = FontWeight.Bold)
//                    Spacer(modifier = Modifier.width(5.dp))
//                    Text("8:52:16 AM", fontSize = 12.sp )
//                }
//                Row(modifier = Modifier.fillMaxWidth(0.45f),horizontalArrangement = Arrangement.SpaceBetween,verticalAlignment = Alignment.CenterVertically){
//                    Text(text = "Oct 13, 2023", fontWeight = FontWeight.Bold)
//                    Spacer(modifier = Modifier.width(5.dp))
//                    Text("10:52:16 AM", fontSize = 12.sp)
//                }
//                Spacer(modifier = Modifier.fillMaxHeight(0.4f))
//
//                Row(modifier = Modifier.fillMaxWidth(0.45f),horizontalArrangement = Arrangement.SpaceBetween){
//                    Column() {
//                        Text(text = "Alerts")
//                        Text(text = "12")
//
//                    }
//                    Column() {
//                        Text(text = "Fatigue")
//                        Text(text = "12")
//                    }
//                }
//
//            }
        }

        
    }

}

