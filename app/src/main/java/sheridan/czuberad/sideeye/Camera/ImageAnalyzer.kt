package sheridan.czuberad.sideeye.Camera

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage

abstract class ImageAnalyzer<T> : ImageAnalysis.Analyzer {

    @SuppressLint("UnsafeExperimentalUsageError")
    override fun analyze(image: ImageProxy) {
        val imageM = image.image
        imageM?.let {

            detectFace(InputImage.fromMediaImage(it,image.imageInfo.rotationDegrees)).addOnSuccessListener { results ->
                onSuccess(results)
                image.close()
            }.addOnFailureListener {
                Log.d(TAG, "ImageAnalyzer, failed listener")
                image.close()
            }
        }

    }

    protected abstract fun detectFace(image: InputImage): Task<T>

    protected abstract fun onSuccess(
        results: T
    )

}