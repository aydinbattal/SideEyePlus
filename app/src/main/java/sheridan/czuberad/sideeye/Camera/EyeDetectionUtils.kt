package sheridan.czuberad.sideeye.Camera
import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Color
import android.media.MediaPlayer
import android.util.Log
import android.view.View
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
import com.google.mlkit.vision.face.FaceLandmark
import sheridan.czuberad.sideeye.Domain.Alert
import sheridan.czuberad.sideeye.ApplicationLogic.EyeDetectionLogic
import sheridan.czuberad.sideeye.Domain.Session
import sheridan.czuberad.sideeye.UI.EyeDetectionActivity
import sheridan.czuberad.sideeye.Services.DriverService
import sheridan.czuberad.sideeye.Utils.SharedPreferencesUtils
import java.util.Date
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
    alertText: TextView,
    fatigueText: TextView,
    fatigueNotText: TextView,
    fatigueBeep: MediaPlayer
) :ImageAnalyzer<List<Face>>() {
    private var counter = 0
    private var fatigueCounter = 0

    private val realTimeOpts = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_NONE)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
        .build()

    private val det = FaceDetection.getClient(realTimeOpts)
    private var text = eyeDetectionText
    private var sessionT = sessionText
    private var isSessionStart = false
    private var isSessionEnd = false
    private var isAbove = false
    private var sessionEndToast = sessionToast
    private var endSession = endSessionOnClick
    private var startSession = startSessionOnClick
    private var mediaPlayer = media
    private var beepFatigue = fatigueBeep
    private var session = Session()
    private var eyeLogic = EyeDetectionLogic()
    private var driverService = DriverService()
    private var alertList = arrayListOf<Alert>()
    private lateinit var sessionUuid:String
    private val contextAct = eyeDetectionActivity
    private val alertText = alertText
    private val fatigueText = fatigueText
    private val fatigueNotification = fatigueNotText
    private var fatigueC = 0
    private val timeQueue: Queue<Long> = LinkedList()
    private val fatigueTimeStampList = ArrayList<Timestamp>()
    private val timeInterval = 30000
    private var lastOpenTime: Long = System.currentTimeMillis()

    override fun detectFace(image: InputImage): Task<List<Face>> {
        return det.process(image)
    }

    override fun onSuccess(results: List<Face>){
        val questionnaireUUID =
            SharedPreferencesUtils.getQuestionnaireId(contextAct)
        val questionnaireStatus =
            SharedPreferencesUtils.getQuestionnaireStatus(contextAct)
        val reactionTestUUID =
            SharedPreferencesUtils.getReactionTestId(contextAct)
        val reactionTestStatus =
            SharedPreferencesUtils.getReactionTestStatus(contextAct)

        if (!reactionTestUUID.isNullOrEmpty() && !questionnaireUUID.isNullOrEmpty() && (questionnaireStatus || reactionTestStatus)) {
            startSession.setOnClickListener {
                startSession.isEnabled = false
                sessionT.text = "Press End Session to End Session"
                session.sessionUUID = UUID.randomUUID().toString()
                alertList.clear()
                sendMessage(contextAct, "SESSION_START", "/SESSION_STATUS")
                session.startSession = Date(System.currentTimeMillis())
                isSessionStart = true
                isSessionEnd = false



                val timestamp = Timestamp(System.currentTimeMillis())
                Log.d(TAG, " POP: Start press$timestamp")

                session.questionnaireUUID =
                    questionnaireUUID
                session.reactionTestUUID =
                    reactionTestUUID

                //SEND SESSION TO DATABASE WITH ONLY STARTDATE, SESSION UUID, REACTION TEST AND QUESTIONAIRE UUIDS, ALERTLIST.
                driverService.addInitialSession(session)

                endSession.setOnClickListener {
                    if (isSessionEnd == false) {
                        session.fatigueList = arrayListOf()
                        session.alertUUIDList = arrayListOf()


                        sessionT.text = "Press Start To Start Session"
                        session.endSession = Date(System.currentTimeMillis())

                        Log.d("SessionManager", "button clicked")

                        sendMessage(contextAct, "SESSION_END", "/SESSION_STATUS")

                        session.fatigueList = fatigueTimeStampList

                        alertList.forEach {
                            it.alertUUID?.let { it1 -> session.alertUUIDList?.add(it1) }
                        }

                        Log.d(TAG, "ALERTEND Session: $session")
                        Log.d(TAG, "ALERTEND Session: ${session.alertUUIDList}")
                        Log.d(TAG, "ALERTEND Session: ${session.fatigueList}")
                        Log.d(TAG, "ALERTEND AlertList: $alertList")

                        //driverService.addSession(session, alertList)
                        //driverService.updateDriverStatus(alertList.last().alertSeverity)

                        //END SESSION FIREBASE CALL ADD endSessionTime and FatigueTime list
                        driverService.addEndSession(session)
                        alertText.text = "0"
                        fatigueText.text = "0"

                        fatigueC = 0
                        timeQueue.clear()
                        fatigueTimeStampList.clear()

                    }

                    isSessionStart = false
                    isSessionEnd = true

                    // Close the current activity
                    contextAct.finish()
                    startSession.isEnabled = true
                }
            }
        }
        var fatigueCounterStartTime = System.currentTimeMillis()
        results.forEach{

            val mouthBottom = it.getLandmark(FaceLandmark.MOUTH_BOTTOM)?.position
            val noseBase = it.getLandmark(FaceLandmark.NOSE_BASE)?.position

            Log.d(TAG, "MOUTH values mouthBottom: $mouthBottom noseBase: $noseBase")
            if (mouthBottom != null && noseBase != null) {
                val openY = mouthBottom.y - noseBase.y
                Log.d(TAG, "MOUTH DISTANCE: $openY")
            }
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

                if(isSessionStart){
                    var checkRange = (counter/16)/2
                    if(checkRange in 1 .. 2){
                        //LOW SEVERITY
                        val duration = (counter/16)/2
                        val alert = Alert(alertUUID = UUID.randomUUID().toString(),alertSeverity = "Low",Date(System.currentTimeMillis()), alertDuration = duration )
                        alertList.add(alert)
                        alertText.text = alertList.size.toString()
                        val alertSize = alertList.size
                        sendMessage(contextAct, alertSize.toString(), "/SESSION_ALERT")
                        session.sessionUUID?.let { it1 ->
                            driverService.addAlertToSession(alert,
                                it1
                            )
                        }
                    }
                    else if(checkRange in 2..3){
                        //MILD SEVERITY
                        val duration = (counter/16)/2
                        val alert = Alert(alertUUID = UUID.randomUUID().toString(),alertSeverity = "Mild",Date(System.currentTimeMillis()), alertDuration = duration )
                        alertList.add(alert)
                        alertText.text = alertList.size.toString()
                        val alertSize = alertList.size
                        sendMessage(contextAct, alertSize.toString(), "/SESSION_ALERT")
                        session.sessionUUID?.let { it1 ->
                            driverService.addAlertToSession(alert,
                                it1
                            )
                        }

                    }
                    else if(checkRange >3){
                        //HIGH SEVERITY
                        val duration = (counter/16)/2
                        val alert = Alert(alertUUID = UUID.randomUUID().toString(),alertSeverity = "High",Date(System.currentTimeMillis()),alertDuration = duration )
                        alertList.add(alert)
                        alertText.text = alertList.size.toString()
                        val alertSize = alertList.size
                        sendMessage(contextAct, alertSize.toString(), "/SESSION_ALERT")
                        session.sessionUUID?.let { it1 ->
                            driverService.addAlertToSession(alert,
                                it1
                            )
                        }
                    }
                    driverService.updateDriverStatus(alertList)
                }

                counter = 0
                fatigueCounter = 0
                text.text = "EYES DETECTED"
                sendMessage(contextAct,"ALERT_INACTIVE", "/SESSION_CURRENT_ALERT")
                text.setTextColor(Color.parseColor("#00FF0A"))
            }

            if(isSessionStart){

                while (!timeQueue.isEmpty() && currentTimeMs - timeQueue.peek() as Long > timeInterval){
                    timeQueue.poll()
                }

                if(timeQueue.size >=5){
                    fatigueNotification.visibility = View.VISIBLE
                    beepFatigue.start()
                    if(!isAbove){
                        isAbove = true
                        fatigueC++
                        fatigueText.text = fatigueC.toString()
                        fatigueTimeStampList.add(Timestamp(System.currentTimeMillis()))
                        sendMessage(contextAct, fatigueTimeStampList.size.toString(), "/SESSION_FATIGUE")

                    }
                    Log.d(TAG, "PPP QUEUE REACHED 5")
                }
                else{
                    isAbove = false
                    fatigueNotification.visibility = View.INVISIBLE
                }
                var checkRange = (counter/16)/2
                if(checkRange>= 1){

                    eyeLogic = EyeDetectionLogic()
                    sendMessage(contextAct,"ALERT_ACTIVE", "/SESSION_CURRENT_ALERT")
                    //alertList.add(Alert(alertUUID = UUID.randomUUID().toString(),alertSeverity = "low",Date(System.currentTimeMillis()) ))
                    //alertText.text = alertList.size.toString()
                    //sendMessage(contextAct, alertList.size.toString(), "/SESSION_ALERT")
                    mediaPlayer.start()
                    //counter = 0
                    Log.d(TAG, "ALERT:$alertList")
                }

            }
            Log.d(TAG, "ALERTLIST: $alertList")
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