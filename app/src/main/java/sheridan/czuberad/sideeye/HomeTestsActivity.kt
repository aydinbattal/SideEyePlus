package sheridan.czuberad.sideeye

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import sheridan.czuberad.sideeye.databinding.ActivityHomeTestsBinding


class HomeTestsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeTestsBinding
    private val requestCode = 1 // Define a request code
    private lateinit var startButton1: Button
    private lateinit var reactionTestResult1: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeTestsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        startButton1 = binding.reactionTest1Button
        reactionTestResult1 = binding.reactionResult1TextView

        startButton1.setOnClickListener {
            val intent = Intent(this@HomeTestsActivity, ReactionTestActivity::class.java)
            startActivityForResult(intent, requestCode) // Start the activity for a result
        }
    }

    // Handle the result from ReactionTestActivity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == this.requestCode && resultCode == Activity.RESULT_OK) {
            val completedTest = data?.getBooleanExtra("completedTest", false) ?: false
            val averageReactionTime = data?.getLongExtra("averageReactionTime", 0L) ?: 0L

            if (completedTest) {
                // Disable the button
                startButton1.isEnabled = false
                startButton1.text = "Done"
                reactionTestResult1.text = "Average Reaction Time: $averageReactionTime ms"

            }

        }
    }
}
