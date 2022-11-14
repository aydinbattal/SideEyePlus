package sheridan.czuberad.sideeye

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import sheridan.czuberad.sideeye.Services.FirebaseAdministration

class SignupActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private var admin = FirebaseAdministration()
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        auth = FirebaseAuth.getInstance()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)


        var isCompany = false
        var toggle:Switch = findViewById(R.id.switch_signup)
        toggle.setOnCheckedChangeListener { _, isChecked ->
            isCompany = isChecked
        }




        val homeClick = findViewById<Button>(R.id.button_signup)
        homeClick.setOnClickListener{
            var emailText = findViewById<EditText>(R.id.text_email_signup).text.toString().trim()
            var passwordText = findViewById<EditText>(R.id.text_password_signup).text.toString().trim()

            if (isCompany){
                auth.createUserWithEmailAndPassword(emailText, passwordText).addOnCompleteListener{
                    if(it.isSuccessful){
                        val intent = Intent(this, HomeCompanyActivity::class.java)
                        startActivity(intent)
                    }
                    else{
                        Toast.makeText(baseContext, "UNABLE TO SIGNUP - COMPANY",Toast.LENGTH_SHORT).show()
                    }
                }

            }

            else{
                auth.createUserWithEmailAndPassword(emailText, passwordText).addOnCompleteListener{
                    if(it.isSuccessful){
                        val intent = Intent(this, HomeDriverActivity::class.java)
                        startActivity(intent)
                    }
                    else{
                        Toast.makeText(baseContext, "UNABLE TO SIGNUP - COMPANY",Toast.LENGTH_SHORT).show()
                    }
                }
//                admin.signupDriver(emailText, passwordText, auth)
//                if(admin.sign){
//                    val intent = Intent(this, HomeDriverActivity::class.java)
//                    startActivity(intent)
//                }
//                else{
//                    Toast.makeText(baseContext, emailText + passwordText,Toast.LENGTH_SHORT).show()
//                }

            }

        }
    }
}