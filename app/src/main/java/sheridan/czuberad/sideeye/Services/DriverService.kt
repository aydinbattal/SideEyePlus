package sheridan.czuberad.sideeye.Services

import android.content.ContentValues.TAG
import android.os.Debug
import android.util.Log
import androidx.compose.ui.layout.LookaheadLayout
import androidx.compose.ui.platform.LocalDensity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import sheridan.czuberad.sideeye.Domain.Alert
import sheridan.czuberad.sideeye.Domain.Driver
import sheridan.czuberad.sideeye.Domain.Session

class DriverService {
    private var db = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser?.uid
//    fun addAlertToSessionById(
//        uid: String,
//        session: Session,
//        alertList: ArrayList<Alert>
//    ) {
//        var AlertList = arrayListOf<Alert>()
//
//        if (currentUser != null) {
//            db.collection("Drivers").document(currentUser).collection("Sessions").document(uid).set(session).addOnCompleteListener {
//                if (it.isSuccessful){
//                    alertList.forEach{ alert ->
//                        db.collection("Drivers").document(currentUser).collection("Sessions").document(uid).collection("Alerts").document().set(alert)
//                    }
//                }
//            }
//        }
//
//    }

    fun addSession(
        session: Session,
        alertList: ArrayList<Alert>
    ){

        if(currentUser != null){

            db.collection("Drivers").document(currentUser).collection("Sessions").document(session.sessionUUID.toString()).set(session).addOnCompleteListener{
                if(it.isSuccessful){
                    alertList.forEach{alert ->
                        db.collection("Alerts").document(alert.alertUUID.toString()).set(alert)
                    }
                }
            }


        }


    }

    fun checkIsDriverInDB(emailText: String): Task<QuerySnapshot> {

        return db.collection("Drivers").whereEqualTo("email",emailText).get()

    }

    fun fetchCurrentUser(callback: (Driver?) -> Unit) {
        if (currentUser != null) {
            db.collection("Drivers").document(currentUser).get()
                .addOnSuccessListener {
                    if (it != null && it.exists()) {
                        val driver = Driver().apply {
                            companyName = it.getString("companyName") ?: ""
                            email = it.getString("email") ?: ""
                            name = it.getString("name") ?: ""
                            phoneNumber = it.getString("phoneNumber") ?: ""
                            status = it.getBoolean("status") ?: false
                        }
                        callback(driver)
                    } else {
                        callback(null)
                    }
                }
                .addOnFailureListener {
                    callback(null)
                }
        } else {
            callback(null)
        }
    }


    fun fetchSessions(callback: (Map<String, Int>?) -> Unit){
        Log.d("YOO","FETCHSESSIONS FUNCTION CALL")
        if (currentUser != null) {
            db.collection("Drivers")
                .document(currentUser)
                .collection("Sessions")
                .get()
                .addOnSuccessListener { sessionsSnapshot ->
                    val sessionAlertsCountMap = mutableMapOf<String, Int>()
                    val sessionIds = sessionsSnapshot.documents.map { it.id }

                    if (sessionIds.isEmpty()) {
                        callback(mapOf()) // Return empty map if there are no sessions
                        return@addOnSuccessListener
                    }

                    // Counter for completed requests
                    var completedRequests = 0

                    // Iterate over all the session documents
                    for (sessionId in sessionIds) {
                        db.collection("Drivers")
                            .document(currentUser)
                            .collection("Sessions")
                            .document(sessionId)
                            .collection("Alerts")
                            .get()
                            .addOnSuccessListener { alertsSnapshot ->
                                sessionAlertsCountMap[sessionId] = alertsSnapshot.size()
                                completedRequests++

                                // Check if all requests are done
                                if (completedRequests == sessionIds.size) {
                                    callback(sessionAlertsCountMap)
                                }
                            }
                            .addOnFailureListener {
                                completedRequests++
                                sessionAlertsCountMap[sessionId] = 0

                                // Check if all requests are done
                                if (completedRequests == sessionIds.size) {
                                    callback(sessionAlertsCountMap)
                                }
                            }
                    }
                }
                .addOnFailureListener {
                    callback(null)
                }
        }




    }

}