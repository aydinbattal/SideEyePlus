package sheridan.czuberad.sideeye

import android.annotation.SuppressLint
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.camera.view.PreviewView
import com.google.firebase.firestore.FirebaseFirestore
import sheridan.czuberad.sideeye.Camera.CameraXUtils
import sheridan.czuberad.sideeye.Services.DriverService
import sheridan.czuberad.sideeye.`Application Logic`.EyeDetectionLogic

class EyeDetectionActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var cameraXUtils: CameraXUtils
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eye_detection)
        var eyeDetectionLogic = EyeDetectionLogic()
        var driverService = DriverService()
        var sessionToast =
            Toast.makeText(baseContext, "Session Has Been Added", Toast.LENGTH_SHORT).show()
        var media = MediaPlayer.create(this, R.raw.warningsound)
        val previewCameraX = findViewById<PreviewView>(R.id.cameraXpreview)
        var eyeDetectionText = findViewById<TextView>(R.id.eyedetectionText)
        var sessionText = findViewById<TextView>(R.id.sessionTextView)
        var alertText = findViewById<TextView>(R.id.alertCountTextView)
        var fatigueText = findViewById<TextView>(R.id.fatigueCountTextView)
        val startSessionOnClick = findViewById<Button>(R.id.button_eye_detection)
        val endSessionOnClick = findViewById<Button>(R.id.button_eye_detection_end)
        cameraXUtils = CameraXUtils(this, previewCameraX, this)
        //checkPermissions()
        cameraXUtils.openCameraPreview(
            eyeDetectionText,
            endSessionOnClick,
            startSessionOnClick,
            media,
            sessionText,
            sessionToast,
            this,
            alertText,
            fatigueText
        )

    }
}