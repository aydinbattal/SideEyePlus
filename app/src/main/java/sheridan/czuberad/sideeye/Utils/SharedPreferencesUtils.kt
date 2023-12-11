package sheridan.czuberad.sideeye.Utils

import android.content.Context
import java.util.*

/**
 * SideEye+ created by aydin
 * student ID : 991521740
 * on 2023-11-24 */
object SharedPreferencesUtils {
    private const val PREFS_NAME = "SessionPrefs"
    private const val KEY_REACTION_TEST_UUID = "reactionTestUUID"
    private const val KEY_QUESTIONNAIRE_UUID = "questionnaireUUID"
    private const val KEY_REACTION_TEST_STATUS = "reactionTestStatus"
    private const val KEY_QUESTIONNAIRE_STATUS = "questionnaireStatus"

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

    fun saveReactionTestStatus(context: Context, isPassed: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putBoolean(KEY_REACTION_TEST_STATUS, isPassed)
        editor.apply()
    }

    fun saveQuestionnaireStatus(context: Context, isPassed: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putBoolean(KEY_QUESTIONNAIRE_STATUS, isPassed)
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

    fun getReactionTestStatus(context: Context): Boolean {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_REACTION_TEST_STATUS, false)
    }

    fun getQuestionnaireStatus(context: Context): Boolean {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_QUESTIONNAIRE_STATUS, false)
    }

}

