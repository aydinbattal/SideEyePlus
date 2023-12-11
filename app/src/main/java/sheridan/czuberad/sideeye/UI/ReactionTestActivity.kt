package sheridan.czuberad.sideeye.UI

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import sheridan.czuberad.sideeye.R
import sheridan.czuberad.sideeye.databinding.ActivityReactionTestBinding
import java.util.*

class ReactionTestActivity : AppCompatActivity() {
    private lateinit var startButton: Button
    private lateinit var frameLayout: FrameLayout
    private lateinit var messageView: TextView
    private lateinit var testNumberView: TextView
    private lateinit var instructionsTitleView: TextView
    private lateinit var instructionsView: TextView
    private var isRed = true
    private var testRunning = false
    private var testStarted = false

    private var testCount = 0
    private var totalReactionTime = 0L
    private var averageReactionTime = 0L

    private lateinit var binding: ActivityReactionTestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityReactionTestBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        startButton = binding.startButton
        frameLayout = binding.frameLayout
        messageView = binding.messageView
        testNumberView = binding.testNumberView
        instructionsView = binding.testInstructions
        instructionsTitleView = binding.testInstructionsTitle

        startButton.setOnClickListener {
            if (!testStarted) {
                startTest()
            }
        }

        frameLayout.setOnClickListener {
            if (testCount >= 5) {
                showMessage("DONE\nAverage Reaction Time: $averageReactionTime ms")
            } else if (testRunning) {
                if (isRed) {
                    if (!testStarted) {
                        showMessage("Tap the button to start the test.")
                    } else {
                        stopTest("Tapped too early. Tap the button again to restart the test.")
                    }
                } else {
                    completeTest()
                }
            } else if (!testStarted) {
                showMessage("Tap the button to start the test.")
            }
        }

    }

    private var startTime: Long = 0

    private fun startTest() {
        instructionsTitleView.visibility = View.GONE
        instructionsView.visibility = View.GONE
        testNumberView.text = "Test #${testCount+1}"
        testNumberView.visibility = View.VISIBLE
        frameLayout.visibility = View.VISIBLE
        if (isRed) {
            if (testRunning) {
                return
            }
            isRed = true
            frameLayout.setBackgroundColor(getColor(R.color.red))
            messageView.visibility = View.GONE
        }

        // Delay before turning green
        val delayMillis = (2000..5000).random().toLong()

        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                if (testRunning) {
                    isRed = false
                    frameLayout.setBackgroundColor(getColor(R.color.green))
                    startTime = System.currentTimeMillis()
                }
                timer.cancel()
            }
        }, delayMillis)

        // Hide the UI elements and clear the message
        startButton.visibility = View.GONE
        testStarted = true
        testRunning = true
    }

    private fun completeTest() {
        // Calculate and display the reaction time
        val reactionTime = System.currentTimeMillis() - startTime
        // Reset the background to white and show the UI elements
        stopTest("")
        testNumberView.text = "Test #${testCount+1}"
        if (testCount < 4) { // Perform the test 5 times
            testCount++
            totalReactionTime += reactionTime
            startTest()
        } else { // All tests are done
            testCount++
            totalReactionTime += reactionTime
            averageReactionTime = totalReactionTime / 5
            //showMessage("DONE\nAverage Reaction Time: $averageReactionTime ms")
            startButton.visibility = View.GONE
            testNumberView.visibility = View.GONE
            testRunning = false
            testStarted = false

            // Navigate back to home screen for tests
            val intent = Intent()
            intent.putExtra("completedTest", true) // Set the boolean variable
            intent.putExtra("averageReactionTime", averageReactionTime) // Set the average reaction time
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    private fun stopTest(message: String) {
        isRed = true
        frameLayout.setBackgroundColor(getColor(R.color.white))
        startButton.text = "Restart Test"
        startButton.visibility = View.VISIBLE
        messageView.text = message
        messageView.visibility = View.VISIBLE
        testRunning = false
        testStarted = false
    }

    private fun showMessage(message: String) {
        messageView.text = message
        messageView.visibility = View.VISIBLE
    }
}