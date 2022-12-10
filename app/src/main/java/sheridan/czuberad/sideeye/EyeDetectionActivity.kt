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
import sheridan.czuberad.sideeye.Camera.CameraXUtils
import sheridan.czuberad.sideeye.Domain.Session
import sheridan.czuberad.sideeye.Services.DriverService
import sheridan.czuberad.sideeye.`Application Logic`.EyeDetectionLogic
import java.util.UUID

class EyeDetectionActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore

    private lateinit var cameraXUtils: CameraXUtils
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eye_detection)
        var eyeDetectionLogic = EyeDetectionLogic()
        var driverService = DriverService()

        val detectOnclick = findViewById<Button>(R.id.button_eye_detection)




        detectOnclick.setOnClickListener {

            //val timestamp = Timestamp(System.currentTimeMillis())
            val dateStart = eyeDetectionLogic.getTimeStamp()
            Toast.makeText(baseContext, dateStart.toString(), Toast.LENGTH_SHORT).show()

            val endSessionOnClick = findViewById<Button>(R.id.button_eye_detection_end)
            endSessionOnClick.setOnClickListener {

                val dateEnd = eyeDetectionLogic.getTimeStamp()
                Toast.makeText(baseContext, dateEnd.toString(), Toast.LENGTH_SHORT).show()

                val session = Session(dateStart, dateEnd)
                driverService.addAlertToSessionById(Firebase.auth.currentUser, FirebaseFirestore.getInstance(), UUID.randomUUID().toString(), session)

            }



        }
    }
}