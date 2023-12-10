package sheridan.czuberad.sideeye.UI

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import sheridan.czuberad.sideeye.Domain.Company
import sheridan.czuberad.sideeye.Domain.Driver
import sheridan.czuberad.sideeye.R

class SignupActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    //private var admin = FirebaseAdministration()
    private lateinit var uid: String
    private lateinit var db: FirebaseFirestore
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {

        auth = FirebaseAuth.getInstance()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        var companyText = findViewById<EditText>(R.id.text_company_signup)
        var isCompany = false
        var toggle:Switch = findViewById(R.id.switch_signup)
        toggle.setOnCheckedChangeListener { _, isChecked ->
            isCompany = isChecked
            companyText.visibility = if(companyText.visibility == View.VISIBLE){
                View.INVISIBLE
            }
            else{
                View.VISIBLE
            }
        }




        val homeClick = findViewById<Button>(R.id.button_signup)
        homeClick.setOnClickListener{


            var emailText = findViewById<EditText>(R.id.text_email_signup).text.toString().trim()
            var passwordText = findViewById<EditText>(R.id.text_password_signup).text.toString().trim()
            val nameText = findViewById<EditText>(R.id.text_name_signup).text.toString()
            val phoneText = findViewById<EditText>(R.id.text_phone_signup).text.toString()
            val companyNameText = findViewById<EditText>(R.id.text_company_signup).text.toString()

            if (isCompany){
                auth.createUserWithEmailAndPassword(emailText, passwordText).addOnCompleteListener{
                    if(it.isSuccessful){
                        val currentUser = Firebase.auth.currentUser
                        currentUser?.let {
                            uid = currentUser.uid
                        }

                        val company = Company(nameText, emailText, companyNameText, phoneText)
                        db = FirebaseFirestore.getInstance()
                        db.collection("Owners").document(uid).set(company).addOnSuccessListener {
                            Toast.makeText(baseContext, "Data success",Toast.LENGTH_SHORT).show()
                        }.addOnFailureListener {
                            Toast.makeText(baseContext, "Data Fail",Toast.LENGTH_SHORT).show()
                        }
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
                        val currentUser = Firebase.auth.currentUser
                        currentUser?.let {
                            uid = currentUser.uid
                        }
                        val driver = Driver(nameText, emailText,phoneText,"Low","")

//                        val prompt = SignupPrompts()
//                        val name = prompt.signupDriverPrompt(this)

                        db = FirebaseFirestore.getInstance()
                        db.collection("Drivers").document(uid).set(driver).addOnSuccessListener {
                            Toast.makeText(baseContext, "Data success",Toast.LENGTH_SHORT).show()
                        }.addOnFailureListener {
                            Toast.makeText(baseContext, "Data Fail",Toast.LENGTH_SHORT).show()
                        }
                        val intent = Intent(this, HomeDriverActivity::class.java)
                        startActivity(intent)
                    }
                    else{
                        Toast.makeText(baseContext, "UNABLE TO SIGNUP - DRIVER",Toast.LENGTH_SHORT).show()
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

