package sheridan.czuberad.sideeye.`Application Logic`

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import sheridan.czuberad.sideeye.Domain.Driver
import sheridan.czuberad.sideeye.Services.DriverService

class IndependentDriverLogic {
    private val driverService = DriverService()

    @Composable
    fun getCurrentDriverInfo(): Driver {

        var driver by remember{ mutableStateOf(Driver()) }

        LaunchedEffect(key1 = Unit){
            driverService.fetchCurrentUser {
                if(it != null){
                    driver = it
                }
            }
        }

        return driver

    }

//    fun getSessionHistoryMap(): MutableMap<Int, Int> {
//        Log.d("YOO", "Start of Call")
//        val graphMap = mutableMapOf<Int, Int>()
//        var counter = 1
//
//        driverService.fetchSessions { sessionAlertMap ->
//
//            sessionAlertMap?.forEach{(sessionId, alerts) ->
//                graphMap[counter] = alerts
//                counter++
//                Log.d("YOO", "SessionID: $sessionId Number of Alerts:$alerts")
//            }
//        }
//        Log.d("YOO", "SessionMapGraph: $graphMap")
//        return graphMap
//        Log.d("YOO", "End of Call")
//    }

    fun getSessionHistoryMap(callback: (MutableMap<Int, Int>) -> Unit) {
        Log.d("YOO", "Start of Call")
        val graphMap = mutableMapOf<Int, Int>()
        var counter = 1

        driverService.fetchSessions { sessionAlertMap ->
            sessionAlertMap?.forEach { (_, alerts) ->
                graphMap[counter] = alerts
                counter++
                Log.d("YOO", "Number of Alerts:$alerts")
            }

            Log.d("YOO", "SessionMapGraph: $graphMap")
            callback(graphMap)  // Notify that the data is loaded and processed
        }
    }
}