package sheridan.czuberad.sideeye

import android.content.Context
import java.util.*

/**
 * SideEye+ created by aydin
 * student ID : 991521740
 * on 2023-11-24 */
object SharedPreferencesUtils {
    private const val PREFS_NAME = "SessionPrefs"
//    private const val KEY_SESSION_ID = "sessionId"
//    private const val KEY_SESSION_START_TIME = "sessionStartTime"
//    private const val KEY_SESSION_END_TIME = "sessionEndTime"
//    private const val KEY_IS_SESSION_STARTED = "isSessionStarted"
    private const val KEY_REACTION_TEST_UUID = "reactionTestUUID"
    private const val KEY_QUESTIONNAIRE_UUID = "questionnaireUUID"

    fun saveReactionTestId(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString(KEY_REACTION_TEST_UUID, generateTestId())
        editor.apply()
    }

    fun saveQuestionnaireId(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString(KEY_QUESTIONNAIRE_UUID, generateTestId())
        editor.apply()
    }

    private fun generateTestId(): String {
        return UUID.randomUUID().toString()
    }

    fun getReactionTestId(context: Context): String? {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_REACTION_TEST_UUID, null)
    }

    fun getQuestionnaireId(context: Context): String? {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_QUESTIONNAIRE_UUID, null)
    }

//    fun startSession(context: Context) {
//        Log.d("SessionManager", "startSession called")
//        if (!isSessionStarted(context)) {
//            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
//            val editor = prefs.edit()
//            editor.putString(KEY_SESSION_ID, generateSessionId())
//            editor.putLong(KEY_SESSION_START_TIME, System.currentTimeMillis())
//            editor.putBoolean(KEY_IS_SESSION_STARTED, true)
//            editor.apply()
//        } else {
//            // Session is already started
//            Log.d("SessionManager", "Session is already started")
//        }
//    }
//
//    fun endSession(context: Context) {
//        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
//        val editor = prefs.edit()
//        editor.putLong(KEY_SESSION_END_TIME, System.currentTimeMillis())
//        editor.remove(KEY_SESSION_ID)
//            .remove(KEY_SESSION_START_TIME)
//            .remove(KEY_IS_SESSION_STARTED)
//            .apply()
//    }
//
//    fun isSessionStarted(context: Context): Boolean {
//        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
//        val isSessionStarted = prefs.getBoolean(KEY_IS_SESSION_STARTED, false)
//        val sessionId = prefs.getString(KEY_SESSION_ID, null)
//        return isSessionStarted && sessionId != null
//    }
//
//    fun getSessionId(context: Context): String? {
//        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
//            .getString(KEY_SESSION_ID, null)
//    }
//
//    fun getSessionStartTime(context: Context): Long {
//        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
//            .getLong(KEY_SESSION_START_TIME, 0L)
//    }
//
//    fun getSessionEndTime(context: Context): Long {
//        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
//            .getLong(KEY_SESSION_END_TIME, 0L)
//    }
//
//    private fun generateSessionId(): String {
//        return UUID.randomUUID().toString()
//    }
//
//    fun saveSessionToFirestore(context: Context) {
//
//        val sessionId = getSessionId(context)
//        val startTime = getSessionStartTime(context)
//        val endTime = getSessionEndTime(context)
//        Log.d("SessionManager", "saveSessionToFirestore called ${sessionId}, ${startTime}, ${endTime}")
//
//        if (sessionId != null && startTime > 0) {
//            val db = FirebaseFirestore.getInstance()
//            val uid = Firebase.auth.currentUser?.uid
//            if (uid != null) {
//                val sessionsRef = db.collection("Sessions")
//
//                val startDate = Timestamp(Date(startTime))
//                val endDate = Timestamp(Date(endTime))
//
//                val userRef = sessionsRef.document(uid)
//                val sessionData = hashMapOf(
//                    "sessionId" to sessionId,
//                    "startTime" to startDate,
//                    "endTime" to endDate,
//                    // Add other session-related data as needed
//                )
//                userRef.set(sessionData, SetOptions.merge())
//                    .addOnSuccessListener { Log.d("SessionManager", "Session data saved!") }
//                    .addOnFailureListener { e -> Log.d("SessionManager", "Error saving session data", e) }
//            }
//        }
//    }
}

