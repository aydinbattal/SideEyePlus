package sheridan.czuberad.sideeye

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import com.google.firebase.auth.FirebaseAuth

class SignupActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)


        var isCompany = false
        var toggle:Switch = findViewById(R.id.switch_signup)
        toggle.setOnCheckedChangeListener { _, isChecked ->
            isCompany = isChecked
        }

        var email_text = findViewById<EditText>(R.id.text_email_signup)
        var password_text = findViewById<EditText>(R.id.text_password_signup)


        val homeClick = findViewById<Button>(R.id.button_signup)
        homeClick.setOnClickListener{

            if (isCompany){
                val intent = Intent(this, HomeCompanyActivity::class.java)
                startActivity(intent)
            }

            else{
                val intent = Intent(this, HomeDriverActivity::class.java)
                startActivity(intent)
            }

        }
    }
}