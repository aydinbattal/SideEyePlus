package sheridan.czuberad.sideeye

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class SignupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        val homeClick = findViewById<Button>(R.id.signupHome_button)
        homeClick.setOnClickListener{
            val intent = Intent(this, HomeDriverActivity::class.java)
            startActivity(intent)
        }
    }
}