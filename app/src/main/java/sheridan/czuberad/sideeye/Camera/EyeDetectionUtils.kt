package sheridan.czuberad.sideeye.Camera
import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Color
import android.media.MediaPlayer
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.google.android.gms.tasks.Task
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.Wearable
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import sheridan.czuberad.sideeye.Domain.Alert
import sheridan.czuberad.sideeye.`Application Logic`.EyeDetectionLogic
import sheridan.czuberad.sideeye.Domain.Session
import sheridan.czuberad.sideeye.EyeDetectionActivity
import sheridan.czuberad.sideeye.Services.DriverService
import java.sql.Timestamp
import java.util.LinkedList
import java.util.Queue
import java.util.UUID

class EyeDetectionUtils(
    eyeDetectionText: TextView,
    endSessionOnClick: Button,
    startSessionOnClick: Button,
    media: MediaPlayer,
    sessionText: TextView,
    sessionToast: Unit,
    eyeDetectionActivity: EyeDetectionActivity,
    alertText: TextView
) :ImageAnalyzer<List<Face>>() {
    private var counter = 0
    private var fatigueCounter = 0

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
    private val contextAct = eyeDetectionActivity
    private val alertText = alertText

    private val timeQueue: Queue<Long> = LinkedList()
    private val timeInterval = 30000

    override fun detectFace(image: InputImage): Task<List<Face>> {
        return det.process(image)
    }

    override fun onSuccess(results: List<Face>){
        startSession.setOnClickListener {
            sessionT.text = "Press End Session to End Session"
            sessionUuid = UUID.randomUUID().toString()
            alertList.clear()
            sendMessage(contextAct, "SESSION_START", "/SESSION_STATUS")
            session.startSession = eyeLogic.getTimeStamp()
            isSessionStart = true
            isSessionEnd = false

            val timestamp = Timestamp(System.currentTimeMillis())
            Log.d(TAG, " POP: Start press$timestamp")

            endSession.setOnClickListener {
                if(isSessionEnd == false){
                    sessionT.text = "Press Start To Start Session"
                    session.endSession = eyeLogic.getTimeStamp()
                    //Commented out to not waste session reads
                    //driverService.addAlertToSessionById(sessionUuid,session,alertList)
                    sendMessage(contextAct, "SESSION_END", "/SESSION_STATUS")
                    Log.d(TAG, "ALERTEND $session")

                }

                isSessionStart = false
                isSessionEnd = true
            }
        }
        var fatigueCounterStartTime = System.currentTimeMillis()
        results.forEach{

            val currentTimeMs = System.currentTimeMillis()


            if((it.leftEyeOpenProbability!! < 0.5) && (it.rightEyeOpenProbability!! < 0.5)){
                counter++
                fatigueCounter++

                if(isSessionStart){
                    if(fatigueCounter >=15){
                        timeQueue.add(currentTimeMs)
                        fatigueCounter = 0
                        Log.d(TAG, "PPP QUEUE ADD $timeQueue")
                    }
                }

                Log.d(TAG, "FatigueCounter: $fatigueCounter")
                text.text = "EYES NOT DETECTED"

                text.setTextColor(Color.parseColor("#FF0000"))

            }
            else if((it.leftEyeOpenProbability!! > 0.5) && (it.rightEyeOpenProbability!! > 0.5)){
                counter = 0
                fatigueCounter = 0
                text.text = "EYES DETECTED"
                sendMessage(contextAct,"ALERT_INACTIVE", "/SESSION_CURRENT_ALERT")
                text.setTextColor(Color.parseColor("#00FF0A"))
            }
            else{
                counter = 0
                fatigueCounter = 0
                text.text = "EYES DETECTED"
                text.setTextColor(Color.parseColor("#00FF0A"))
            }
            if(isSessionStart){

                while (!timeQueue.isEmpty() && currentTimeMs - timeQueue.peek() as Long > timeInterval){
                    timeQueue.poll()
                }

                if(timeQueue.size >=5){
                    Log.d(TAG, "PPP QUEUE REACHED 5")
                }

                if(counter>=50){

                    eyeLogic = EyeDetectionLogic()
                    sendMessage(contextAct,"ALERT_ACTIVE", "/SESSION_CURRENT_ALERT")
                    alertList.add(Alert(alertSeverity = "low",eyeLogic.getTimeStamp()))
                    alertText.text = alertList.size.toString()
                    sendMessage(contextAct, alertList.size.toString(), "/SESSION_ALERT")
                    mediaPlayer.start()
                    counter = 0
                    Log.d(TAG, "ALERT:$alertList")
                }

                Log.d(TAG, "PPP QUEUE END OF LOOP $timeQueue")


            }
            Log.d(TAG, "YOO COUNTER: $counter")
            Log.d(TAG,"YOO LEFT EYE" + it.rightEyeOpenProbability.toString())
            Log.d(TAG,"YOO RIGHT EYE" + it.leftEyeOpenProbability.toString())

        }

    }
    fun sendMessage(context: Context, alertCount: String, messagePath: String) {
        val messageClient: MessageClient = Wearable.getMessageClient(context)

        val nodes: Task<List<Node>> = Wearable.getNodeClient(context).connectedNodes
        nodes.addOnCompleteListener { task ->
            if (task.isSuccessful && task.result != null) {
                val connectedNode = task.result?.firstOrNull()
                connectedNode?.let {
                    Log.d("Yoo","Message being sent")
                    messageClient.sendMessage(it.id, messagePath, alertCount.toByteArray()).addOnSuccessListener {
                        Log.d("Yoo","Message sent")
                    }
                }
            }
        }
    }
}