package sheridan.czuberad.sideeye

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.w3c.dom.Text
import sheridan.czuberad.sideeye.Services.DriverService
import sheridan.czuberad.sideeye.databinding.ActivityHomeTestsBinding


class HomeTestsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeTestsBinding
    private val reactionTestRequestCode = 1
    private val questionnaireRequestCode = 2
    private val driverService = DriverService()
    private lateinit var startButton1: Button
    private lateinit var startButton2: Button
    private lateinit var historyButton: Button
    private lateinit var reactionTestResult1: TextView
    private lateinit var reactionTestResult2: TextView
    private lateinit var reactionTestStatus: TextView
    private lateinit var questionnaireStatus: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeTestsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        startButton1 = binding.reactionTest1Button
        startButton2 = binding.reactionTest2Button
        historyButton = binding.testHistoryButton
        reactionTestResult1 = binding.reactionResult1TextView
        reactionTestResult2 = binding.reactionResult2TextView
        reactionTestStatus = binding.reactionTestStatusTextView
        questionnaireStatus = binding.questionnaireStatusTextView

        startButton1.setOnClickListener {
            val intent = Intent(this@HomeTestsActivity, ReactionTestActivity::class.java)
            startActivityForResult(intent, reactionTestRequestCode) // Start the activity for a result

        }

        startButton2.setOnClickListener {
            val intent = Intent(this@HomeTestsActivity, QuestionnaireActivity::class.java)
            startActivityForResult(intent, questionnaireRequestCode)

        }

        historyButton.setOnClickListener {
            //todo: show tests history
            val intent = Intent(this@HomeTestsActivity, TestHistory::class.java)
            startActivity(intent)
        }
    }

    // Handle the result from ReactionTestActivity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == reactionTestRequestCode && resultCode == Activity.RESULT_OK) {
            val completedTest = data?.getBooleanExtra("completedTest", false) ?: false
            val averageReactionTime = data?.getLongExtra("averageReactionTime", 0L) ?: 0L

            if (completedTest) {
                // Disable the button
                startButton1.isEnabled = false
                startButton1.text = "Done"
                reactionTestResult1.text = "Average Reaction Time: $averageReactionTime ms"

                val deviceUtils = DeviceUtils()
                var threshold:Long = 0

                threshold = if(deviceUtils.isEmulator()){
                    650
                } else {
                    475
                }

                if (averageReactionTime <= threshold) {
                    val originalText = "Reaction Test: PASSED"
                    val spannable = SpannableString(originalText)
                    val passedColorSpan = ForegroundColorSpan(resources.getColor(R.color.green))
                    spannable.setSpan(passedColorSpan, originalText.indexOf("PASSED"), originalText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    reactionTestStatus.text = spannable
                } else {
                    val originalText = "Reaction Test: FAILED"
                    val spannable = SpannableString(originalText)
                    val passedColorSpan = ForegroundColorSpan(resources.getColor(R.color.red))
                    spannable.setSpan(passedColorSpan, originalText.indexOf("FAILED"), originalText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    reactionTestStatus.text = spannable
                }

                SharedPreferencesUtils.saveReactionTestId(this)
                val reactionTestUUID = SharedPreferencesUtils.getReactionTestId(this)
                if (reactionTestUUID != null) {
                    driverService.addReactionTest(averageReactionTime,reactionTestUUID)
                }
            }

        } else if (requestCode == questionnaireRequestCode && resultCode == Activity.RESULT_OK) {
            // Handle result from QuestionnaireActivity
            val completedTest = data?.getBooleanExtra("completedTest", false) ?: false
            val category = data?.getStringExtra("category") ?: ""

            if (completedTest) {
                // Handle the completion of the questionnaire
                // Disable the button
                startButton2.isEnabled = false
                startButton2.text = "Done"
                reactionTestResult2.text = "Fatigue Status: $category"

                SharedPreferencesUtils.saveQuestionnaireId(this)
                val questionnaireUUID = SharedPreferencesUtils.getReactionTestId(this)
                if (questionnaireUUID != null) {
                    driverService.addQuestionnaire(category,questionnaireUUID)
                }
            }
        }
    }
}
