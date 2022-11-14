package sheridan.czuberad.sideeye

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance()
        var isCompany: Boolean = false
        var toggle:Switch = findViewById(R.id.switch_login)
        toggle.setOnCheckedChangeListener { _, isChecked ->
            isCompany = isChecked
        }

        val loginClick = findViewById<Button>(R.id.button_login)
        loginClick.setOnClickListener{
            var emailText = findViewById<EditText>(R.id.text_email_login).text.toString().trim()
            var passwordText = findViewById<EditText>(R.id.text_password_login).text.toString().trim()
            if (isCompany){
                auth.signInWithEmailAndPassword(emailText,passwordText).addOnCompleteListener{
                    if(it.isSuccessful){
                        val intent = Intent(this, HomeCompanyActivity::class.java)
                        startActivity(intent)
                    }
                    else{
                        Toast.makeText(baseContext, "UNABLE TO LOGIN - COMPANY", Toast.LENGTH_SHORT).show()
                    }
                }

            }
            else{
                auth.signInWithEmailAndPassword(emailText, passwordText).addOnCompleteListener{
                    if(it.isSuccessful){
                        val intent = Intent(this, HomeDriverActivity::class.java)
                        startActivity(intent)
                    }
                    else{
                        Toast.makeText(baseContext, "UNABLE TO LOGIN - DRIVER", Toast.LENGTH_SHORT).show()
                    }
                }

            }

        }
        val signupClick = findViewById<Button>(R.id.signup_button)
        signupClick.setOnClickListener{
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
    }
}