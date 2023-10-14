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
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.TimeTextDefaults
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import sheridan.czuberad.sideeye.R
import sheridan.czuberad.sideeye.presentation.theme.SideEyeTheme
import java.text.DateFormat

//class MainActivity : ComponentActivity(), MessageClient.OnMessageReceivedListener{
class MainActivity : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            Scaffold(
                timeText = {
                    TimeText(
                        timeTextStyle = TimeTextDefaults.timeTextStyle(
                            fontSize = 10.sp
                        )
                    )
                }
            ) {

                WearApp("Android")
            }

        }

    }
//    override fun onResume() {
//        super.onResume()
//        Wearable.getMessageClient(this).addListener(this)
//    }
//    override fun onPause() {
//        super.onPause()
//        Wearable.getMessageClient(this).removeListener(this)
//    }
//    override fun onMessageReceived(messageEvent: MessageEvent) {
//        if (messageEvent.path == "/path_to_message") {
//
//            val message = String(messageEvent.data)
//            // Update the UI with the received message
//            Log.d("Yoo" ,"MessagedReceieved: $message")
//            setContent {
//                WearApp(message)
//            }
//        }
//    }
}
@Composable
fun WearApp(greetingName: String) {
    val alertText = remember { mutableStateOf("0") }
    val context = LocalContext.current
    LaunchedEffect(key1 = alertText) {
        val wearableListener =
            MessageClient.OnMessageReceivedListener { messageEvent ->
                alertText.value = String(messageEvent.data)
            }

        Wearable.getMessageClient(context)
            .addListener(wearableListener)
    }










    SideEyeTheme {
        /* If you have enough items in your list, use [ScalingLazyColumn] which is an optimized
         * version of LazyColumn for wear devices with some added features. For more information,
         * see d.android.com/wear/compose.
         */

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            TimeText()
        }


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
                text = "00:00:00"
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

            }

            


        }
    }
}



@Composable
fun Greeting(greetingName: String) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        color = MaterialTheme.colors.primary,
        text = stringResource(R.string.hello_world, greetingName)
    )
}
@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearApp("Preview Android")
}