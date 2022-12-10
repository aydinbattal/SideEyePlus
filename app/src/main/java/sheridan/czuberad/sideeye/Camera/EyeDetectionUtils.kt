package sheridan.czuberad.sideeye.Camera
import android.content.ContentValues.TAG
import android.nfc.Tag
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions

class EyeDetectionUtils :ImageAnalyzer<List<Face>>() {

    private val realTimeOpts = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_NONE)
        .build()

    private val det = FaceDetection.getClient(realTimeOpts)

    override fun detectFace(image: InputImage): Task<List<Face>> {
        return det.process(image)
    }

    override fun onSuccess(results: List<Face>){
        results.forEach {
            Log.d(TAG,it.rightEyeOpenProbability.toString())
        }
    }
}