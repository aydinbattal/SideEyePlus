package sheridan.czuberad.sideeye

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import sheridan.czuberad.sideeye.databinding.ActivityHomeTestsBinding


class HomeTestsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeTestsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeTestsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val startButton1 = binding.reactionTest1Button
        startButton1.setOnClickListener{
            val intent = Intent(this@HomeTestsActivity, ReactionTestActivity::class.java)
            startActivity(intent)
        }

    }
}