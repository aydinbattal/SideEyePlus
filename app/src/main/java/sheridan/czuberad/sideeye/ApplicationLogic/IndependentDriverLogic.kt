package sheridan.czuberad.sideeye.ApplicationLogic

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
import sheridan.czuberad.sideeye.Domain.Alert
import sheridan.czuberad.sideeye.Domain.Driver
import sheridan.czuberad.sideeye.Domain.Session
import sheridan.czuberad.sideeye.Domain.Timeline
import sheridan.czuberad.sideeye.Services.DriverService

class IndependentDriverLogic : ViewModel() {
    private val driverService = DriverService()

    private val _sessions = mutableStateOf<List<Session>?>(null)
    val sessions: State<List<Session>?> = _sessions

    private val _sessionDetail = mutableStateOf<Session?>(null)
    val sessionDetail: State<Session?> = _sessionDetail

    private val _alertSessionList = mutableStateOf<List<Alert>?>(null)
    val alertSessionlist: State<List<Alert>?> = _alertSessionList

    private val _fatigueTimeStampList = mutableStateOf<List<com.google.firebase.Timestamp>?>(null)
    val fatigueTimeStampList: State<List<com.google.firebase.Timestamp>?> = _fatigueTimeStampList



    private var _sessionTimeLine = mutableStateOf<List<Timeline>?>(null)
    val sessionTimeLine: State<List<Timeline>?> = _sessionTimeLine


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

    fun getSessionTimeLine(sessionUUID: String){

        var alertObjectList: List<Alert>? = null
        var fatigueTimeStampList: List<com.google.firebase.Timestamp>? = null
        val sessionTimeLineList = mutableListOf<Timeline>()

        sessionUUID.let {
            driverService.fetchAlertListBySessionID(it){alertList ->
                if(alertList != null){
                    //set it to Alert Object List
                    alertObjectList = alertList
                    _alertSessionList.value = alertList
                    Log.d(TAG, "TIMELINE FUNCTION: AlertList: $alertObjectList")

                } else{
                    Log.d(TAG, "TIMELINE FUNCTION: Session Alert Not Found")
                }
            }

            driverService.fetchFatiguesBySessionUUID(it){fatigueList ->
                if(fatigueList != null){
                    //set to fatigue TimeStamp List
                    fatigueTimeStampList = fatigueList
                    _fatigueTimeStampList.value = fatigueList
                    Log.d(TAG, "TIMELINE FUNCTION: FatigueList: $fatigueTimeStampList")
                } else{
                    Log.d(TAG, "TIMELINE FUNCTION: fatigue timestamp List not found")

                }

            }
        }

//        alertObjectList?.forEach { alert ->
//            sessionTimeLineList.add(
//                Timeline(alert.alertTime,alert.alertDuration,alert.alertSeverity, type = "Alert Detection")
//            )
//        }
//
//        fatigueTimeStampList?.forEach {
//            sessionTimeLineList.add(
//                Timeline(timelineTime = it, duration = null,severity = null, type = "Fatigue Detection")
//            )
//        }
    }

    fun setTimeLineList(alertObjectList: List<Alert>?, fatigueTimeStampList: List<com.google.firebase.Timestamp>?) {
        val sessionTimeLineList = mutableListOf<Timeline>()

        alertObjectList?.forEach { alert ->
            sessionTimeLineList.add(
                Timeline(alert.alertTime,alert.alertDuration,alert.alertSeverity, type = "Alert Detection")
            )
        }

        fatigueTimeStampList?.forEach {

            val date = it.toDate()
            sessionTimeLineList.add(
                Timeline(timelineTime = date, duration = null,severity = null, type = "Fatigue Detection")
            )
        }

        val sortedSessionTimeLineList = sessionTimeLineList.sortedBy { it.timelineTime }

        _sessionTimeLine.value = sortedSessionTimeLineList
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