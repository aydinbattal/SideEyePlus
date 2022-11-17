package sheridan.czuberad.sideeye

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import sheridan.czuberad.sideeye.Domain.Alert
import sheridan.czuberad.sideeye.Domain.Session
import java.sql.Timestamp
import java.util.Date
import java.util.UUID

class EyeDetectionActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eye_detection)

        val textView = findViewById<TextView>(R.id.textView_eye_detection)
        //textView.text = "yooooooooo"

        val detectOnclick = findViewById<Button>(R.id.button_eye_detection)

        detectOnclick.setOnClickListener {

            val timestamp = Timestamp(System.currentTimeMillis())
            val dateStart = Date(timestamp.time)
            Toast.makeText(baseContext, dateStart.toString(), Toast.LENGTH_SHORT).show()

            val endSessionOnClick = findViewById<Button>(R.id.button_eye_detection_end)
            endSessionOnClick.setOnClickListener {
                val timestamp1 = Timestamp(System.currentTimeMillis())
                val dateEnd = Date(timestamp1.time)
                Toast.makeText(baseContext, dateEnd.toString(), Toast.LENGTH_SHORT).show()

                var session = Session(dateStart, dateEnd)
                val currentUser = Firebase.auth.currentUser
                db = FirebaseFirestore.getInstance()
                val uid = UUID.randomUUID().toString()

                var AlertList = arrayListOf<Alert>()
                AlertList.add(Alert("Eye"))
                AlertList.add(Alert("Face"))
                if (currentUser != null) {
                    db.collection("Drivers").document(currentUser.uid).collection("Sessions").document(uid).set(session).addOnCompleteListener {
                        if (it.isSuccessful){
                            AlertList.forEach{ alert ->
                                db.collection("Drivers").document(currentUser.uid).collection("Sessions").document(uid).collection("Alerts").document().set(alert)
                            }
                        }
                    }
                }



            }



        }
    }
}