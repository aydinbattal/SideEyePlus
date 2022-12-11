package sheridan.czuberad.sideeye.Camera
import android.content.ContentValues.TAG
import android.graphics.Color
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
import java.sql.Timestamp

class EyeDetectionUtils(
    eyeDetectionText: TextView,
    endSessionOnClick: Button,
    startSessionOnClick: Button
) :ImageAnalyzer<List<Face>>() {
    private var counter = 0

    private val realTimeOpts = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_NONE)
        .build()

    private val det = FaceDetection.getClient(realTimeOpts)
    private var text = eyeDetectionText
    private var isSessionStart = false
    private var isSessionEnd = false
    private var endSession = endSessionOnClick
    private var startSession = startSessionOnClick
    private var alertList = arrayListOf<Alert>()
    override fun detectFace(image: InputImage): Task<List<Face>> {
        return det.process(image)
    }

    override fun onSuccess(results: List<Face>){
        startSession.setOnClickListener {
            alertList.clear()
            isSessionStart = true
            isSessionEnd = false
            val timestamp = Timestamp(System.currentTimeMillis())
            Log.d(TAG, " POP: Start press$timestamp")

            endSession.setOnClickListener {
                if(isSessionEnd == false){
                    //Log.d(TAG, "POP: End press$timestamp")
                    Log.d(TAG, "ALERTEND $alertList")
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
                    var eyeLogic = EyeDetectionLogic()

                    alertList.add(Alert(alertSeverity = "low",eyeLogic.getTimeStamp()))
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