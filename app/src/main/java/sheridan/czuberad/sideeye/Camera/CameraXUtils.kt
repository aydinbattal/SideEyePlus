package sheridan.czuberad.sideeye.Camera

import android.content.Context
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraXUtils(private val context: Context,private val previewView: PreviewView,private val lifecycleOwner: LifecycleOwner) {


    private lateinit var executorService: ExecutorService
    private var imageAnalysis: ImageAnalysis? = null
    private var camera: Camera? = null
    private var preview: Preview? = null
    private var processcameraProvider: ProcessCameraProvider? = null
    private var cameraSelector = CameraSelector.LENS_FACING_FRONT

    init{
        executorService = Executors.newSingleThreadExecutor()
    }

    fun openCameraPreview(){
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
//        cameraProviderFuture.addListener(
//            Runnable {
//                processcameraProvider = cameraProviderFuture.get()
//                preview = Preview.Builder().build()
//
//                imageAnalysis = ImageAnalysis.Builder().setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
//                    .build()
//                    .also {
//                        it.setAnalyzer(executorService,)
//                    }
//            }
//        )
    }

//    private fun analyzer(): ImageAnalysis.Analyzer{
//        return EyeDetectionUtils()
//    }



}