package sheridan.czuberad.sideeye.UI

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController


@Composable
fun SessionHistory(navController: NavHostController) {
    





    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color.White),
        verticalArrangement = Arrangement.Top,
    horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Session History", fontSize = 30.sp, modifier = Modifier.padding(10.dp))

            SessionList(navController)

    }
}



@Composable
fun SessionList(navController: NavHostController) {

    val items = listOf("Item 1", "Item 2", "Item 3", "Item 4", "Item 5","item 6", "item 8")
    
    LazyColumn{
        items(items){item ->
            SessionListItem(item, navController)
        }
    }
    

}

@Composable
fun SessionListItem(item: String, navController: NavHostController) {
    
    Card(modifier = Modifier
        .fillMaxWidth()
        .height(150.dp)
        .padding(10.dp).clickable { navController.navigate("sessionDetail") },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)){


            Column(modifier = Modifier.fillMaxWidth(0.45f)) {
                Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.SpaceBetween,verticalAlignment = Alignment.CenterVertically){
                    Text(text = "Oct 13, 2023", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(5.dp))
                    Text("8:52:16 AM", fontSize = 12.sp )
                }
                Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.SpaceBetween,verticalAlignment = Alignment.CenterVertically){
                    Text(text = "Oct 13, 2023", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(5.dp))
                    Text("10:52:16 AM", fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.fillMaxHeight(0.4f))

                Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.SpaceBetween){
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Alerts",fontSize = 12.sp)
                        Text(text = "12", fontWeight = FontWeight.ExtraBold)

                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Fatigue", fontSize = 12.sp)
                        Text(text = "12", fontWeight = FontWeight.ExtraBold)
                    }
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

@Preview
@Composable
fun Preview(){
    //SessionList(navController)
}