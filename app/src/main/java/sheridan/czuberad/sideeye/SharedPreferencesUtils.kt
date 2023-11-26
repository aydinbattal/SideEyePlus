package sheridan.czuberad.sideeye

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

}

