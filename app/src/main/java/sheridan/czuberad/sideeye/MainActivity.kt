package sheridan.czuberad.sideeye

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import android.widget.ToggleButton

class MainActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var isCompany: Boolean = false
        var toggle:Switch = findViewById(R.id.switch_login)
        toggle.setOnCheckedChangeListener { _, isChecked ->
            isCompany = isChecked
        }

        val loginClick = findViewById<Button>(R.id.button_login)
        loginClick.setOnClickListener{
            if (isCompany){
                val intent = Intent(this, HomeCompanyActivity::class.java)
                startActivity(intent)
            }
            else{
                val intent = Intent(this, HomeDriverActivity::class.java)
                startActivity(intent)
            }

        }
        val signupClick = findViewById<Button>(R.id.signup_button)
        signupClick.setOnClickListener{
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
    }
}