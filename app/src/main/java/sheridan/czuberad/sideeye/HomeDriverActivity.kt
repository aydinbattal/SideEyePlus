package sheridan.czuberad.sideeye

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class HomeDriverActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_driver)

        val eyeClick = findViewById<Button>(R.id.button_eye)
        eyeClick.setOnClickListener {
            val intent = Intent(this, EyeDetectionActivity::class.java)
            startActivity(intent)
        }

        val testClick = findViewById<Button>(R.id.button_test)
        testClick.setOnClickListener {
            val intent = Intent(this, ReactionTestActivity::class.java)
            startActivity(intent)
        }
    }
}