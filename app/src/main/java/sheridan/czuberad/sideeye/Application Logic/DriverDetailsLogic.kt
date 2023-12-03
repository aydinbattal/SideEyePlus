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
import sheridan.czuberad.sideeye.Services.CompanyService
import sheridan.czuberad.sideeye.Services.DriverService

class DriverDetailsLogic : ViewModel() {
    private val companyService = CompanyService()

    private val _sessions = mutableStateOf<List<Session>?>(null)
    val sessions: State<List<Session>?> = _sessions

    private val _sessionDetail = mutableStateOf<Session?>(null)
    val sessionDetail: State<Session?> = _sessionDetail

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
            }
        }
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