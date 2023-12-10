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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import java.sql.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import sheridan.czuberad.sideeye.Domain.*
import java.sql.Time
import java.util.Date
import java.util.concurrent.atomic.AtomicInteger


class DriverService {
    private var db = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser?.uid

    fun updateDriverStatus(alerts: List<Alert>?){
        if (currentUser != null) {
            val status = if (alerts?.any { it.alertSeverity == "High" } == true) {
                "High"
            } else if (alerts?.any { it.alertSeverity == "Mild" } == true) {
                "Mild"
            } else {
                "Low"
            }

            db.collection("Drivers").document(currentUser).update("status", status)
        }
    }


    fun addInitialSession(
        session: Session

    ){

        if(currentUser != null){
            session.sessionUUID?.let {
                db.collection("Drivers").document(currentUser).collection("Sessions").document(it).set(session)
                    .addOnSuccessListener {
                        Log.d(TAG, "PPP: Initial Session Added" )
                    }
            }
        }
    }

    fun addAlertToSession(
        alert: Alert,
        sessionUUID: String
    ){

        if (currentUser != null) {
            // Reference to the session document
            val sessionDocRef = db.collection("Drivers")
                .document(currentUser)
                .collection("Sessions")
                .document(sessionUUID)

            // Fetch the document
            sessionDocRef.get().addOnSuccessListener { document ->
                if (document != null) {
                    // Extract the alertUUIDList array from the document
                    val alertUUIDList = document["alertUUIDList"] as? MutableList<String> ?: mutableListOf()

                    // Append alert.alertUUID to the array
                    alert.alertUUID?.let { alertUUIDList.add(it) }

                    // Update the document with the modified array
                    sessionDocRef.update("alertUUIDList", alertUUIDList)
                        .addOnSuccessListener {
                            // Successfully updated the document
                            alert.alertUUID?.let { it1 -> db.collection("Alerts").document(it1).set(alert) }

                        }
                        .addOnFailureListener { e ->
                            // Handle failure
                        }
                } else {
                    // Handle the case where the document does not exist
                }
            }.addOnFailureListener {
                // Handle failure to fetch the document
            }
        }
    }


    fun addEndSession(session: Session){

        if(currentUser != null){
            val sessionDocRef = session.sessionUUID?.let {
                db.collection("Drivers")
                    .document(currentUser)
                    .collection("Sessions")
                    .document(it)
            }

            if (sessionDocRef != null) {
                sessionDocRef.update("fatigueList",session.fatigueList)
                    .addOnSuccessListener {
                        Log.d(TAG, "PPP fatigueList updated")
                    }
            }

            if (sessionDocRef != null) {
                sessionDocRef.update("endSession",session.endSession)
                    .addOnSuccessListener {
                        Log.d(TAG, "PPP endSession updated")
                    }
            }

        }


    }

    fun addSession(
        session: Session,
        alertList: ArrayList<Alert>
    ) {

        if (currentUser != null) {

            session.sessionUUID?.let {
                db.collection("Drivers").document(currentUser).collection("Sessions").document(it)
                    .set(session).addOnCompleteListener { complete ->

//                        if (complete.isSuccessful) {
//                            alertList.forEach { alert ->
//                                alert.alertUUID?.let { it1 ->
//                                    db.collection("Alerts").document(it1).set(alert)
//                                }
//                            }
//                        }
                    }
            }


        }


    }

    fun fetchSessionById(sessionId: String, callback: (Session?) ->Unit){
        if (currentUser != null) {
            db.collection("Drivers").document(currentUser).collection("Sessions").document(sessionId).get()
                .addOnSuccessListener {
                    if(it.exists()){
                        val session = it.toObject(Session::class.java)
                        Log.d(TAG, "IF SERVICE")
                        callback(session)
                    }
                    else{
                        Log.d(TAG, "ELSE SERVICE")
                        callback(null)

                    }
                }.addOnFailureListener {
                    Log.d(TAG, "FAILURE SERVICE")
                    callback(null)
                }
        }

    }

    fun addReactionTest(averageReactionTime:Long, reactionTestUUID:String) {
        // Save the average reaction time and test completion time to db
            val reactionTestsRef = db.collection("ReactionTests")
            val testRef = reactionTestsRef.document(reactionTestUUID)
            val reactionData = hashMapOf(
                "averageReactionTime" to averageReactionTime,
                "timestamp" to FieldValue.serverTimestamp()
            )
        testRef.set(reactionData, SetOptions.merge())
                .addOnSuccessListener { Log.d(TAG, "Reaction data saved!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error saving reaction data", e) }

    }

    fun addQuestionnaire(category:String, questionnaireUUID:String) {
        // Save the questionnaire category and test completion time to db
        val questionnairesRef = db.collection("Questionnaires")
        val testRef = questionnairesRef.document(questionnaireUUID)
        val reactionData = hashMapOf(
            "category" to category,
            "timestamp" to FieldValue.serverTimestamp()
        )
        testRef.set(reactionData, SetOptions.merge())
            .addOnSuccessListener { Log.d(TAG, "Questionnaire data saved!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error saving questionnaire data", e) }

    }


    fun getOverallReactionTimeAverage(
        onSuccess: (Long) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        // Use the previously defined function to fetch individual reaction test results
        getReactionTestResults(
            onSuccess = { reactionTestResults ->
                // Calculate the overall reaction time average
                val overallAverage = if (reactionTestResults.isNotEmpty() && reactionTestResults.size >= 5) {
                    val totalScore = reactionTestResults.sumOf { it.avgTime ?: 0 }
                    totalScore / reactionTestResults.size.toLong()
                } else {
                    0L // Default value if there are no reaction test results
                }

                onSuccess(overallAverage)
            },
            onFailure = onFailure
        )
    }


//    fun fetchAlertListBySessionID(sessionUUID: String){
//        if(currentUser != null){
//            val sessionDocRef = db.collection("Drivers")
//                .document(currentUser)
//                .collection("Sessions")
//                .document(sessionUUID)
//
//            sessionDocRef.get().addOnSuccessListener {document ->
//
//                val alertsList: MutableList<Alert> = mutableListOf()
//
//                val alertUUIDList = document["alertUUIDList"] as? MutableList<String> ?: mutableListOf()
//
//
//
//
//
//            }
//        }
//    }

    fun fetchAlertListBySessionID(sessionUUID: String, callback: (List<Alert>?) -> Unit) {
        if (currentUser != null) {
            val sessionDocRef = db.collection("Drivers")
                .document(currentUser)
                .collection("Sessions")
                .document(sessionUUID)

            sessionDocRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val alertUUIDList = document["alertUUIDList"] as? List<String> ?: emptyList()

                    if (alertUUIDList.isEmpty()) {
                        callback(emptyList())
                    } else {
                        val tasks = alertUUIDList.map { uuid ->
                            db.collection("Alerts").document(uuid).get()
                        }

                        Tasks.whenAllSuccess<DocumentSnapshot>(tasks).addOnSuccessListener { documents ->
                            val alertsList = documents.mapNotNull { doc ->
                                if (doc.exists()) doc.toObject(Alert::class.java) else null
                            }
                            callback(alertsList)
                        }.addOnFailureListener {
                            callback(null)
                        }
                    }
                } else {
                    callback(null)
                }
            }.addOnFailureListener {
                callback(null)
            }
        }
    }

    fun fetchFatiguesBySessionUUID(sessionUUID: String, callback: (List<com.google.firebase.Timestamp>?) -> Unit) {
        if (currentUser != null) {
            val sessionDocRef = db.collection("Drivers")
                .document(currentUser)
                .collection("Sessions")
                .document(sessionUUID)

            sessionDocRef.get().addOnSuccessListener { document ->
                val fatigueTimeStamps = document["fatigueList"] as? List<com.google.firebase.Timestamp> ?: emptyList()
                callback(fatigueTimeStamps) // Use the callback to return the data
            }.addOnFailureListener {
                callback(null) // Handle the failure case
            }
        } else {
            callback(null) // Handle the case where currentUser is null
        }
    }







    fun getReactionTestResults(
        onSuccess: (List<ReactionTest>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val reactionTestResults = mutableListOf<ReactionTest>()
        val reactionTestUids = mutableSetOf<String>() // Using a Set to avoid duplicates

        // Fetch all session UIDs
        fetchAllSessionsByCurrentID { sessions ->
            sessions?.let {
                val fetchedCount = AtomicInteger(0)

                it.forEach { session ->
                    session.reactionTestUUID?.let { reactionTestUids.add(it) }

                    // Fetch reaction test results for each UID
                    reactionTestUids.forEach { reactionTestUid ->
                        db.collection("ReactionTests")
                            .document(reactionTestUid)
                            .get()
                            .addOnSuccessListener { document ->
                                val timestamp = document["timestamp"] as? com.google.firebase.Timestamp
                                val score = document.getLong("averageReactionTime") ?: 0

                                // Convert Timestamp to formatted string
                                val formattedTimeStamp = timestamp?.let {
                                    SimpleDateFormat("MMM dd, yyy - HH:mm", Locale.getDefault()).format(it.toDate())
                                } ?: ""

                                // Check if the reaction test result is not already in the list
                                if (!reactionTestResults.any { it.uid == reactionTestUid }) {
                                    reactionTestResults.add(
                                        ReactionTest(reactionTestUid, score, formattedTimeStamp, session.sessionUUID)
                                    )
                                    Log.d("driverservice", "$reactionTestResults")
                                }

                                // Check if all reactions tests have been fetched
                                if (fetchedCount.incrementAndGet() == reactionTestUids.size) {
                                    onSuccess(reactionTestResults)
                                }
                            }
                            .addOnFailureListener { exception ->
                                onFailure(exception)
                            }
                    }
                }
            } ?: onFailure(Exception("Failed to fetch sessions"))
        }
    }


    fun getQuestionnaireResults(
        onSuccess: (List<Questionnaire>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val questionnaireResults = mutableListOf<Questionnaire>()
        val questionnaireUids = mutableSetOf<String>() // Using a Set to avoid duplicates

        // Fetch all session UIDs
        fetchAllSessionsByCurrentID { sessions ->
            sessions?.let {
                val fetchedCount = AtomicInteger(0)

                it.forEach { session ->
                    session.questionnaireUUID?.let { questionnaireUids.add(it) }

                    // Fetch questionnaire results for each UID
                    questionnaireUids.forEach { questionnaireUid ->
                        db.collection("Questionnaires")
                            .document(questionnaireUid)
                            .get()
                            .addOnSuccessListener { document ->
                                val timestamp = document["timestamp"] as? com.google.firebase.Timestamp
                                val category = document.getString("category") ?: ""

                                // Convert Timestamp to formatted string
                                val formattedTimeStamp = timestamp?.let {
                                    SimpleDateFormat("MMM dd, yyy - HH:mm", Locale.getDefault()).format(it.toDate())
                                } ?: ""

                                // Check if the questionnaire result is not already in the list
                                if (!questionnaireResults.any { it.uid == questionnaireUid }) {
                                    questionnaireResults.add(
                                        Questionnaire(questionnaireUid, category, formattedTimeStamp, session.sessionUUID)
                                    )
                                    Log.d("driverservice", "$questionnaireResults")

                                }

                                // Check if all questionnaire results have been fetched
                                if (fetchedCount.incrementAndGet() == questionnaireUids.size) {
                                    onSuccess(questionnaireResults)
                                }
                            }
                            .addOnFailureListener { exception ->
                                onFailure(exception)
                            }
                    }
                }
            } ?: onFailure(Exception("Failed to fetch sessions"))
        }
    }

    fun fetchAllSessionsByCurrentID(callback: (List<Session>?) -> Unit) {

        if (currentUser != null) {
            db.collection("Drivers").document(currentUser).collection("Sessions").get()
                .addOnSuccessListener {
                    val sessionList = it.mapNotNull { document ->
                        document.toObject(Session::class.java)
                    }
                    callback(sessionList)
                }.addOnFailureListener {
                    callback(null)
                }
        }
    }

    fun checkIsDriverInDB(emailText: String): Task<QuerySnapshot> {

        return db.collection("Drivers").whereEqualTo("email", emailText).get()

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
                                status = it.getString("status") ?: ""
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


        fun fetchSessions(callback: (Map<String, Int>?) -> Unit) {

            Log.d("YOO", "FETCHSESSIONS FUNCTION CALL")
            if (currentUser != null) {
                db.collection("Drivers")
                    .document(currentUser)
                    .collection("Sessions")
                    .orderBy("endSession", Query.Direction.DESCENDING)
                    .get()
                    .addOnSuccessListener { sessionsSnapshot ->
                        val sessionAlertsCountMap = mutableMapOf<String, Int>()
                        val sessionIds = sessionsSnapshot.documents.map { it.id }

                        if (sessionIds.isEmpty()) {
                            callback(mapOf()) // Return empty map if there are no sessions
                            return@addOnSuccessListener
                        }

                        for (document in sessionsSnapshot.documents) {
                            val alertUUIDList = document.get("alertUUIDList") as? List<*>
                            val sessionId = document.id
                            val alertsCount = alertUUIDList?.size ?: 0
                            sessionAlertsCountMap[sessionId] = alertsCount
                        }

                        callback(sessionAlertsCountMap)
                    }
                    .addOnFailureListener {
                        callback(null)
                    }
            }

        }
    }