package sheridan.czuberad.sideeye.UI

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import sheridan.czuberad.sideeye.`Application Logic`.IndependentDriverLogic
import sheridan.czuberad.sideeye.Domain.Timeline
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun SessionDetail(sessionID: String?) {


    val viewModel: IndependentDriverLogic = viewModel()

    LaunchedEffect(Unit){
        if (sessionID != null) {
            viewModel.getSessionDetail(sessionID)
            viewModel.getSessionTimeLine(sessionID)
        }
    }
    val session = viewModel.sessionDetail.value


    val alertObjectList = viewModel.alertSessionlist.value
    val fatigueTimeStampList = viewModel.fatigueTimeStampList.value

   Log.d(TAG, "SESSIONDETAIL Lists: $alertObjectList fatigueTimeStamps: $fatigueTimeStampList")

    viewModel.setTimeLineList(alertObjectList, fatigueTimeStampList)

    val sessionTimeLine = viewModel.sessionTimeLine.value
//
    Log.d(TAG, "SESSIONDETAIL LIST: $sessionTimeLine")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        verticalArrangement = Arrangement.Top
    ) {

        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp.dp
        val screenHeight = configuration.screenHeightDp.dp

        Card(
            modifier = Modifier
                .width(screenWidth)
                .height(screenHeight / 4),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF39AFEA)
            ),
            shape = RectangleShape
        ) {

            Text(
                text = "Session Details",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp, modifier = Modifier.padding(start = 10.dp)
            )
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp), horizontalArrangement = Arrangement.SpaceBetween){
                Column(modifier = Modifier.weight(1f)) {


                    if (session != null) {
                        Text(text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(session.startSession), color = Color.White,fontSize = 20.sp)
                    }
                    if (session != null) {
                        Text(text = SimpleDateFormat("MMM dd", Locale.getDefault()).format(session.startSession),color = Color.White,fontSize = 12.sp)
                    }
                    Text(text = "to",color = Color.White)

                    if (session != null) {
                        Text(
                            text = session.endSession?.let {
                                SimpleDateFormat("HH:mm", Locale.getDefault()).format(it)
                            } ?: "",
                            color = Color.White,
                            fontSize = 20.sp
                        )
                    }
                    if (session != null) {
                        Text(
                            text = session.endSession?.let {
                                SimpleDateFormat(
                                    "MMM dd",
                                    Locale.getDefault()
                                ).format(it)
                            } ?: "Session is still running...",
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }


                }

                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceBetween) {

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (session != null) {
                            Text(
                                text = session.alertUUIDList?.size.toString(),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        }
                        Text(
                            text = "Alerts",
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (session != null) {
                            Text(
                                text = session.fatigueList?.size.toString(),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        }
                        Text(
                            text = "Fatigue",
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }

                }

            }

        }
        ///AFTER CARD

        Card(modifier = Modifier
            .fillMaxWidth()
            .height(25.dp), shape = RectangleShape,colors = CardDefaults.cardColors(
            containerColor = Color(0xFFCACACA)
        )) {

            Column(modifier = Modifier
                .fillMaxSize()
                .padding(5.dp), verticalArrangement = Arrangement.Center) {
                Text(text = "Session Timeline", color = Color(0xFF2B2A2A))
            }


        }

        var sampleTimelineList = listOf(
            Timeline(null, 2,"Low","Alert Detection")
//            Timeline("9:23AM",2,"Low","Alert Detection"),
//            Timeline("9:53AM",4,"Mild","Alert Detection"),
//            Timeline("10:53AM",10,"High","Alert Detection"),
//            Timeline("11:32AM",12,"Mild","Fatigue Detection"),
//            Timeline("11:53AM",3,"Low","Alert Detection"),
//            Timeline("12:32PM",12,"Mild","Fatigue Detection"),
//            Timeline("1:09PM",2,"Low","Alert Detection"),
//            Timeline("2:32PM",12,"Mild","Fatigue Detection")

        )
        LazyColumn{
            items(sessionTimeLine ?: emptyList()){
                Card(modifier = Modifier.fillMaxWidth(),colors = CardDefaults.cardColors(
                    containerColor = Color.White),shape = RectangleShape){
                    Column {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp)) {

                            Row(verticalAlignment = Alignment.CenterVertically){
                                it.timelineTime?.let { it1 -> Text(text = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(it1) )}
                                Column(modifier = Modifier.padding(15.dp)) {
                                    it.type?.let { it1 -> Text(text = it1,fontSize = 20.sp) }

                                    it.severity?.let { it1 -> Text(text = it1,fontSize = 15.sp) }
                                }
                            }
                            //Text(text = it.duration.toString() + "s",fontSize = 18.sp)
                            it.duration?.let { duration -> Text(text = "${duration}s", fontSize = 18.sp) }
                        }
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .background(Color(0xFFC0C0C0)) )
                    }

                }

            }
        }
    }
}



