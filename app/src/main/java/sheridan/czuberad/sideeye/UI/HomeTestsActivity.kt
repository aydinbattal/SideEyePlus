package sheridan.czuberad.sideeye.UI

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.opengl.Visibility
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import sheridan.czuberad.sideeye.R
import sheridan.czuberad.sideeye.Services.DriverService
import sheridan.czuberad.sideeye.Utils.DeviceUtils
import sheridan.czuberad.sideeye.Utils.SharedPreferencesUtils
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
    private lateinit var warningTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeTestsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        startButton1 = binding.reactionTest1Button
        startButton2 = binding.reactionTest2Button
        historyButton = binding.testHistoryButton
        reactionTestResult1 = binding.reactionResult1TextView
        reactionTestResult2 = binding.questionnaireResultTextView
        reactionTestStatus = binding.reactionTestStatusTextView
        questionnaireStatus = binding.questionnaireStatusTextView
        warningTextView = binding.warningTextView

        startButton1.setOnClickListener {
            val intent = Intent(this@HomeTestsActivity, ReactionTestActivity::class.java)
            startActivityForResult(intent, reactionTestRequestCode) // Start the activity for a result

        }

        startButton2.setOnClickListener {
            val intent = Intent(this@HomeTestsActivity, QuestionnaireActivity::class.java)
            startActivityForResult(intent, questionnaireRequestCode)

        }

        historyButton.setOnClickListener {
            val intent = Intent(this@HomeTestsActivity, TestResultsHistoryActivity::class.java)
            startActivity(intent)
        }
    }

    // Handle the result from ReactionTestActivity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == reactionTestRequestCode && resultCode == Activity.RESULT_OK) {
            val completedTest = data?.getBooleanExtra("completedTest", false) ?: false
            val receivedAverageReactionTime = data?.getLongExtra("averageReactionTime", 0L) ?: 0L

            if (completedTest) {
                val deviceUtils = DeviceUtils()

                val averageReactionTime = if (deviceUtils.isEmulator() && receivedAverageReactionTime != 0L) {
                    receivedAverageReactionTime - 120 //due to around 120ms delay on emulator compared to real device
                } else {
                    receivedAverageReactionTime
                }

                // Disable the button
                startButton1.isEnabled = false
                startButton1.text = "Done"
                reactionTestResult1.text = "Average Reaction Time: $averageReactionTime ms"


                driverService.getOverallReactionTimeAverage(
                    onSuccess = { overallAverage ->

                        val threshold = if (overallAverage != 0L) {
                            (overallAverage * 1.1672).toLong()
                        } else {
                            467 //377 + 90
                        }



                        Log.d("hometestsactivity", "Overall Reaction Time Average: $overallAverage")
                        Log.d("hometestsactivity", "threshold: $threshold")

                        if (averageReactionTime <= threshold) {
                            val originalText = "Reaction Tests: PASSED"
                            val spannable = SpannableString(originalText)
                            val passedColorSpan = ForegroundColorSpan(resources.getColor(R.color.green))
                            spannable.setSpan(passedColorSpan, originalText.indexOf("PASSED"), originalText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                            reactionTestStatus.text = spannable

                            SharedPreferencesUtils.saveReactionTestStatus(this, true)
                        } else {
                            val originalText = "Reaction Tests: FAILED"
                            val spannable = SpannableString(originalText)
                            val passedColorSpan = ForegroundColorSpan(resources.getColor(R.color.red))
                            spannable.setSpan(passedColorSpan, originalText.indexOf("FAILED"), originalText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                            reactionTestStatus.text = spannable

                            warningTextView.visibility = View.VISIBLE
                            warningTextView.setTextColor(Color.RED)
                            warningTextView.text = "Please report to your supervisor immediately"

                            SharedPreferencesUtils.saveReactionTestStatus(this, false)
                        }

                        SharedPreferencesUtils.saveReactionTestId(this)
                        val reactionTestUUID = SharedPreferencesUtils.getReactionTestId(this)
                        if (reactionTestUUID != null) {
                            driverService.addReactionTest(averageReactionTime, reactionTestUUID)
                        }
                    },
                    onFailure = { exception ->
                        // Handle the failure, such as logging an error or displaying a message to the user
                        Log.e("hometestsactivity", "Error fetching overall reaction time average", exception)
                    }
                )
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

                if (category != "High") {
                    val originalText = "Questionnaire: PASSED"
                    val spannable = SpannableString(originalText)
                    val passedColorSpan = ForegroundColorSpan(resources.getColor(R.color.green))
                    spannable.setSpan(passedColorSpan, originalText.indexOf("PASSED"), originalText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    questionnaireStatus.text = spannable

                    SharedPreferencesUtils.saveQuestionnaireStatus(this, true)
                } else {
                    val originalText = "Questionnaire: FAILED"
                    val spannable = SpannableString(originalText)
                    val passedColorSpan = ForegroundColorSpan(resources.getColor(R.color.red))
                    spannable.setSpan(passedColorSpan, originalText.indexOf("FAILED"), originalText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    questionnaireStatus.text = spannable

                    warningTextView.visibility = View.VISIBLE
                    warningTextView.setTextColor(Color.RED)
                    warningTextView.text = "Please report to your supervisor immediately"

                    SharedPreferencesUtils.saveQuestionnaireStatus(this, false)
                }

                SharedPreferencesUtils.saveQuestionnaireId(this)
                val questionnaireUUID = SharedPreferencesUtils.getQuestionnaireId(this)
                if (questionnaireUUID != null) {
                    driverService.addQuestionnaire(category,questionnaireUUID)
                }
            }
        }


    }
}
