package sheridan.czuberad.sideeye.Camera

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy

abstract class ImageAnalyzer<T> : ImageAnalysis.Analyzer {


    override fun analyze(image: ImageProxy) {

    }
}