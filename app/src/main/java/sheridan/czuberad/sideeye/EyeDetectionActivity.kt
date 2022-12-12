package sheridan.czuberad.sideeye

import android.annotation.SuppressLint
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
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
        var media = MediaPlayer.create(this,R.raw.warningsound)
        val previewCameraX = findViewById<PreviewView>(R.id.cameraXpreview)
        var eyeDetectionText = findViewById<TextView>(R.id.eyedetectionText)
        val startSessionOnClick = findViewById<Button>(R.id.button_eye_detection)
        val endSessionOnClick = findViewById<Button>(R.id.button_eye_detection_end)
        cameraXUtils = CameraXUtils(this,previewCameraX,this)
        //checkPermissions()
        cameraXUtils.openCameraPreview(eyeDetectionText, endSessionOnClick, startSessionOnClick, media)






//        detectOnclick.setOnClickListener {
//
//
//            //val timestamp = Timestamp(System.currentTimeMillis())
//            val dateStart = eyeDetectionLogic.getTimeStamp()
//            Toast.makeText(baseContext, dateStart.toString(), Toast.LENGTH_SHORT).show()
//
//            endSessionOnClick.setOnClickListener {
//
//                val dateEnd = eyeDetectionLogic.getTimeStamp()
//                Toast.makeText(baseContext, dateEnd.toString(), Toast.LENGTH_SHORT).show()
//
//                val session = Session(dateStart, dateEnd)
//                driverService.addAlertToSessionById(Firebase.auth.currentUser, FirebaseFirestore.getInstance(), UUID.randomUUID().toString(), session)
//
//            }
//
//
//
//        }
    }

//    private fun isPermissionsAllowed() = REQUIRED_PERMISSIONS.all {
//        ContextCompat.checkSelfPermission(baseContext,it) == PackageManager.PERMISSION_GRANTED
//    }
//
//    private fun checkPermissions(){
//        if(isPermissionsAllowed()){
//            cameraXUtils.openCameraPreview()
//        }
//        else{
//            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
//        }
//    }


//    companion object {
//        private const val REQUEST_CODE_PERMISSIONS = 10
//        private val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA)
//    }



}