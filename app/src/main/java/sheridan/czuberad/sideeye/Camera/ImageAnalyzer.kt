package sheridan.czuberad.sideeye.Camera

import android.annotation.SuppressLint
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage

abstract class ImageAnalyzer<T> : ImageAnalysis.Analyzer {

    @SuppressLint("UnsafeExperimentalUsageError", "UnsafeOptInUsageError")
    override fun analyze(image: ImageProxy) {
        val imageM = image.image
        imageM?.let {

        }

    }

    protected abstract fun detectFace(image: InputImage): Task<T>

    protected abstract fun onSuccess(
        results: T
    )

}