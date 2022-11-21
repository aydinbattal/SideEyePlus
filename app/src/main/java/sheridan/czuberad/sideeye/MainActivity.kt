package sheridan.czuberad.sideeye

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import sheridan.czuberad.sideeye.Services.DriverService
import sheridan.czuberad.sideeye.Services.FirebaseAdministration
import sheridan.czuberad.sideeye.Services.OwnerService

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        var firebaseAdmin = FirebaseAdministration()
        var ownerService = OwnerService()
        var driverService = DriverService()
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
                firebaseAdmin.loginIn(emailText,passwordText).addOnCompleteListener { it ->
                    if (it.isSuccessful){
                        ownerService.checkIsCompanyInDB(emailText).addOnSuccessListener { documents ->
                            for(document in documents){
                                if (document.data["email"] == emailText){
                                    val intent = Intent(this, HomeCompanyActivity::class.java)
                                    startActivity(intent)
                                }
                            }
                        }
                    }
                    else{
                        Toast.makeText(baseContext, "UNABLE TO LOGIN - COMPANY", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            else{
                firebaseAdmin.loginIn(emailText, passwordText).addOnCompleteListener { it->
                    if(it.isSuccessful){
                        driverService.checkIsDriverInDB(emailText).addOnSuccessListener { documents ->
                            for (document in documents){
                                if(document.data["email"] == emailText){
                                    val intent = Intent(this, HomeDriverActivity::class.java)
                                    startActivity(intent)
                                }
                            }
                        }
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