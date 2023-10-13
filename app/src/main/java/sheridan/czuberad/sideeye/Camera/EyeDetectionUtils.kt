package sheridan.czuberad.sideeye.Camera
import android.content.ContentValues.TAG
import android.graphics.Color
import android.media.MediaPlayer
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import sheridan.czuberad.sideeye.Domain.Alert
import sheridan.czuberad.sideeye.`Application Logic`.EyeDetectionLogic
import sheridan.czuberad.sideeye.Domain.Session
import sheridan.czuberad.sideeye.Services.DriverService
import java.sql.Timestamp
import java.util.UUID

class EyeDetectionUtils(
    eyeDetectionText: TextView,
    endSessionOnClick: Button,
    startSessionOnClick: Button,
    media: MediaPlayer,
    sessionText: TextView,
    sessionToast: Unit
) :ImageAnalyzer<List<Face>>() {
    private var counter = 0

    private val realTimeOpts = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_NONE)
        .build()

    private val det = FaceDetection.getClient(realTimeOpts)
    private var text = eyeDetectionText
    private var sessionT = sessionText
    private var isSessionStart = false
    private var isSessionEnd = false
    private var sessionEndToast = sessionToast
    private var endSession = endSessionOnClick
    private var startSession = startSessionOnClick
    private var mediaPlayer = media
    private var session = Session()
    private var eyeLogic = EyeDetectionLogic()
    private var driverService = DriverService()
    private var alertList = arrayListOf<Alert>()
    private lateinit var sessionUuid:String
    override fun detectFace(image: InputImage): Task<List<Face>> {
        return det.process(image)
    }

    override fun onSuccess(results: List<Face>){
        startSession.setOnClickListener {
            sessionT.text = "Press End Session to End Session"
            sessionUuid = UUID.randomUUID().toString()
            alertList.clear()
            session.startSession = eyeLogic.getTimeStamp()
            isSessionStart = true
            isSessionEnd = false

            val timestamp = Timestamp(System.currentTimeMillis())
            Log.d(TAG, " POP: Start press$timestamp")

            endSession.setOnClickListener {
                if(isSessionEnd == false){
                    sessionT.text = "Press Start To Start Session"
                    //Log.d(TAG, "POP: End press$timestamp")
                    //session.alertList = alertList
                    session.endSession = eyeLogic.getTimeStamp()
                    driverService.addAlertToSessionById(sessionUuid,session,alertList)
                    Log.d(TAG, "ALERTEND $session")



                }

                isSessionStart = false
                isSessionEnd = true


            }
        }
        results.forEach{
            if((it.leftEyeOpenProbability!! < 0.5) && (it.rightEyeOpenProbability!! < 0.5)){
                counter++
                text.text = "EYES NOT DETECTED"
                text.setTextColor(Color.parseColor("#FF0000"))

            }
            else if((it.leftEyeOpenProbability!! > 0.5) && (it.rightEyeOpenProbability!! > 0.5)){
                counter = 0
                text.text = "EYES DETECTED"
                text.setTextColor(Color.parseColor("#00FF0A"))
            }
            else{
                counter = 0
                text.text = "EYES DETECTED"
                text.setTextColor(Color.parseColor("#00FF0A"))
            }
            if(isSessionStart){
                if(counter>=50){
                    eyeLogic = EyeDetectionLogic()

                    alertList.add(Alert(alertSeverity = "low",eyeLogic.getTimeStamp()))
                    mediaPlayer.start()
                    counter = 0
                    Log.d(TAG, "ALERT:$alertList")
                }
            }


            Log.d(TAG, "YOO COUNTER: $counter")
            Log.d(TAG,"YOO LEFT EYE" + it.rightEyeOpenProbability.toString())
            Log.d(TAG,"YOO RIGHT EYE" + it.leftEyeOpenProbability.toString())

        }

    }
}