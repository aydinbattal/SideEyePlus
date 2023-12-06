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
import com.google.firebase.Timestamp
import sheridan.czuberad.sideeye.Domain.*
import java.util.Date
import java.util.concurrent.atomic.AtomicInteger


class DriverService {
    private var db = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser?.uid

    fun addSession(
        session: Session,
        alertList: ArrayList<Alert>
    ) {

        if (currentUser != null) {

            session.sessionUUID?.let {
                db.collection("Drivers").document(currentUser).collection("Sessions").document(it)
                    .set(session).addOnCompleteListener { complete ->
                        if (complete.isSuccessful) {
                            alertList.forEach { alert ->
                                alert.alertUUID?.let { it1 ->
                                    db.collection("Alerts").document(it1).set(alert)
                                }
                            }
                        }
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
                                val timestamp = document["timestamp"] as? Timestamp
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
                                val timestamp = document["timestamp"] as? Timestamp
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


        fun fetchSessions(callback: (Map<String, Int>?) -> Unit) {
            Log.d("YOO", "FETCHSESSIONS FUNCTION CALL")
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