package sheridan.czuberad.sideeye

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import sheridan.czuberad.sideeye.Domain.Alert
import java.util.UUID

class EyeDetectionActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eye_detection)

        val textView = findViewById<TextView>(R.id.textView_eye_detection)
        textView.text = "yooooooooo"

        val detectOnclick = findViewById<Button>(R.id.button_eye_detection)

        detectOnclick.setOnClickListener {
            val currentUser = Firebase.auth.currentUser

            db = FirebaseFirestore.getInstance()
            val uid = UUID.randomUUID().toString()

            var AlertList = arrayListOf<Alert>()
            AlertList.add(Alert("Eye"))
            AlertList.add(Alert("Face"))

            AlertList.forEach{
                if (currentUser != null) {
                    db.collection("Drivers").document(currentUser.uid).collection("Sessions").document(uid).collection("Alerts").document().set(it)
                }
            }

        }
    }
}