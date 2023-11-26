package sheridan.czuberad.sideeye

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
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
