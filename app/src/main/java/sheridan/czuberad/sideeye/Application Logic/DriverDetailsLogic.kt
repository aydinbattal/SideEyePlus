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
import sheridan.czuberad.sideeye.Domain.*
import sheridan.czuberad.sideeye.Services.CompanyService
import sheridan.czuberad.sideeye.Services.DriverService

class DriverDetailsLogic : ViewModel() {
    private val companyService = CompanyService()

    private val _sessions = mutableStateOf<List<Session>?>(null)
    val sessions: State<List<Session>?> = _sessions

    private val _sessionDetail = mutableStateOf<Session?>(null)
    val sessionDetail: State<Session?> = _sessionDetail

    private val _alertSessionList = mutableStateOf<List<Alert>?>(null)
    val alertSessionlist: State<List<Alert>?> = _alertSessionList

    private val _fatigueTimeStampList = mutableStateOf<List<com.google.firebase.Timestamp>?>(null)
    val fatigueTimeStampList: State<List<com.google.firebase.Timestamp>?> = _fatigueTimeStampList

    private val _sessionTimeLine = mutableStateOf<List<Timeline>?>(null)
    val sessionTimeLine: State<List<Timeline>?> = _sessionTimeLine

    fun getSessionCardInfoList(email: String){

        companyService.getAllSessionsOfSelectedDriver(email) { it ->
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

    fun getSessionDetail(sessionId: String, email: String){

        companyService.setSelectedDriverId(email) { driverId ->
            if (driverId != null) {
                // selectedDriver is set, do something
                companyService.fetchDriverSessionById(sessionId){
                    if(it != null){
                        _sessionDetail.value = it
                    }
                    else{
                        Log.d(TAG, "SESSION DETAIL NOT FOUND")
                    }
                }
            } else {
                // Handle the case where selectedDriver couldn't be set
                Log.d("DriverDetailsLogic", "selecteddriver is null")
            }
        }
    }

    fun getSessionTimeLine(sessionUUID: String, email: String){

        var alertObjectList: List<Alert>? = null
        var fatigueTimeStampList: List<com.google.firebase.Timestamp>? = null

        companyService.setSelectedDriverId(email) { driverId ->
            if (driverId != null) {
                sessionUUID.let {
                    companyService.fetchAlertListBySessionID(it){alertList ->
                        if(alertList != null){
                            //set it to Alert Object List
                            alertObjectList = alertList
                            _alertSessionList.value = alertList
                            Log.d(TAG, "TIMELINE FUNCTION: AlertList: $alertObjectList")

                        } else{
                            Log.d(TAG, "TIMELINE FUNCTION: Session Alert Not Found")
                        }
                    }

                    companyService.fetchFatiguesBySessionUUID(it){fatigueList ->
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
            } else {
                // Handle the case where selectedDriver couldn't be set
                Log.d("DriverDetailsLogic", "selecteddriver is null")
            }
        }

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

    fun getSessionHistory(email:String){

        companyService.setSelectedDriverId(email) { driverId ->
            if (driverId != null) {
                companyService.fetchAllSessionsByCurrentID { it ->
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
        }

    }

}