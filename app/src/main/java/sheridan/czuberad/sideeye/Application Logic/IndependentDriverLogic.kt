package sheridan.czuberad.sideeye.`Application Logic`

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import sheridan.czuberad.sideeye.Domain.Driver
import sheridan.czuberad.sideeye.Domain.Session
import sheridan.czuberad.sideeye.Domain.SessionSummary
import sheridan.czuberad.sideeye.Services.DriverService

class IndependentDriverLogic : ViewModel() {
    private val driverService = DriverService()

    private val _sessions = mutableStateOf<List<Session>?>(null)
    val sessions: State<List<Session>?> = _sessions

    private val _sessionDetail = mutableStateOf<Session?>(null)
    val sessionDetail: State<Session?> = _sessionDetail

    fun getSessionDetail(sessionId: String){

        driverService.fetchSessionById(sessionId){
            if(it != null){
                _sessionDetail.value = it
            }
            else{
                Log.d(TAG, "SESSION DETAIL NOT FOUND")
            }
        }


    }



    fun getSessionCardInfoList(){

        driverService.fetchAllSessionsByCurrentID { it ->
            if(it != null){
                Log.d(TAG, "Sessions: $it")

                val sortedSessionList = it.sortedByDescending { it.startSession }

                _sessions.value = sortedSessionList
            }
            else{
                Log.d(TAG, "Sessions else: $it")
            }
        }
    }

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
//    }get

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