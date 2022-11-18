package sheridan.czuberad.sideeye

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import sheridan.czuberad.sideeye.Domain.Driver

class HomeDriverActivity : AppCompatActivity() {
    private lateinit var uid: String
    private lateinit var db: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_driver)
        val currentUser = Firebase.auth.currentUser

        val nameText = findViewById<TextView>(R.id.textView_driver_home_name)
        db = FirebaseFirestore.getInstance()
        getDriverData(currentUser, nameText)
        //val driverService = DriverService()
        //driverService.getDriverData(db, currentUser)


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
    private fun getDriverData(currentUser: FirebaseUser?, nameText: TextView) {
        if (currentUser != null) {
            this.db.collection("Drivers").document(currentUser.uid).get()
                .addOnSuccessListener { document ->
                    val driverData = document.toObject(Driver::class.java) ?: Driver()
                    nameText.text = driverData.email
                }
        }

    }
}