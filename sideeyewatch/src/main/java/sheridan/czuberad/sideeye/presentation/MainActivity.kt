/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package sheridan.czuberad.sideeye.presentation

import android.content.Context
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.getSystemService
import androidx.wear.compose.material.Card
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.TimeTextDefaults
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import sheridan.czuberad.sideeye.R
import sheridan.czuberad.sideeye.presentation.theme.SideEyeTheme
import java.text.DateFormat
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.draw.clip

//class MainActivity : ComponentActivity(), MessageClient.OnMessageReceivedListener{
class MainActivity : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            Scaffold(
                timeText = {
                    TimeText(
                        timeTextStyle = TimeTextDefaults.timeTextStyle(
                            fontSize = 15.sp
                        )
                    )
                }
            ) {
                WearApp("Android")
            }

        }

    }
@Composable
fun WearApp(greetingName: String) {
    val alertText = remember { mutableStateOf("-") }
    val fatigueText = remember{ mutableStateOf("-")}
    val durationText = remember { mutableStateOf("00:00:00") }
    var time by remember { mutableStateOf(0) }
    var isSessionRunning by remember { mutableStateOf(false) }
    var isAlertActive by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = alertText) {
        val wearableListener =
            MessageClient.OnMessageReceivedListener { messageEvent ->

                when(messageEvent.path){
                    "/SESSION_STATUS" ->{
                        if(String(messageEvent.data) == "SESSION_START"){
                            //durationText.value = "SESSION STARTED"
                            isSessionRunning = true

                            coroutineScope.launch {
                                while(isSessionRunning){
                                    delay(1000)
                                    time++
                                    durationText.value = String.format("%02d:%02d:%02d", time/3600, (time % 3600) / 60, time % 60)
                                }
                            }

                        }
                        else if(String(messageEvent.data) == "SESSION_END"){
                            //durationText.value = "SESSION ENDED"
                            isSessionRunning = false
                        }

                    }
                    "/SESSION_ALERT"->{
                        alertText.value = String(messageEvent.data)
                    }

                    "/SESSION_FATIGUE"->{
                        fatigueText.value = String(messageEvent.data)
                    }

                    "/SESSION_CURRENT_ALERT"->{
                        if(String(messageEvent.data) == "ALERT_ACTIVE"){
                            isAlertActive = true

                        }
                        else if(String(messageEvent.data) == "ALERT_INACTIVE"){
                            isAlertActive = false

                        }
                    }
                }

            }

        Wearable.getMessageClient(context)
            .addListener(wearableListener)
    }

    SideEyeTheme {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            TimeText()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.background),
                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    style = TextStyle(
                        fontSize = 10.sp
                    ),
                    text = "Session Duration"
                )
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = Color(0xFF39AFEA),
                    style = TextStyle(
                        fontSize = 30.sp
                    ),
                    text = durationText.value
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    style = TextStyle(
                        fontSize = 10.sp
                    ),
                    text = "Session Overview"
                )
                Spacer(modifier = Modifier.height(5.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically

                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = alertText.value,
                            color = Color(0xFF39AFEA),
                            style = TextStyle(
                                fontSize = 30.sp
                            )
                        )
                        Text(
                            text = "Total Alerts",
                            style = TextStyle(
                                fontSize = 10.sp
                            )

                        )
                    }
                    Spacer(modifier = Modifier.width(35.dp))
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {

                        Text(
                            text = fatigueText.value,
                            color = Color(0xFF39AFEA),
                            style = TextStyle(
                                fontSize = 30.sp
                            )
                        )
                        Text(
                            text = "Total Fatigues",
                            style = TextStyle(
                                fontSize = 10.sp
                            )
                        )
                    }

                }
            }

            if(isAlertActive){
                Card(
                    modifier = Modifier.align(Alignment.Center)
                        .clip(CircleShape)
                        .fillMaxSize(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Black.copy(alpha = 0.8f)
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "ACTIVE ALERT", color = Color.Red, fontSize = 25.sp)
                    }

                }
            }

        }


    }
}
@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearApp("Preview Android")
}
}