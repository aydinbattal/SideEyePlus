package sheridan.czuberad.sideeye.Camera

import android.content.ContentValues.TAG
import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
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

    fun openCameraPreview(
        eyeDetectionText: TextView,
        endSessionOnClick: Button,
        startSessionOnClick: Button,
        media: MediaPlayer
    ) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener(
            Runnable {
                processcameraProvider = cameraProviderFuture.get()
                preview = Preview.Builder().build()

                imageAnalysis = ImageAnalysis.Builder().setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(executorService,analyzer(eyeDetectionText, endSessionOnClick, startSessionOnClick, media))
                    }
                val cameraS = CameraSelector.Builder().requireLensFacing(cameraSelector).build()
                configCamera(processcameraProvider,cameraS)

            }, ContextCompat.getMainExecutor(context)
        )
    }

    private fun analyzer(
        eyeDetectionText: TextView,
        endSessionOnClick: Button,
        startSessionOnClick: Button,
        media: MediaPlayer
    ): ImageAnalysis.Analyzer {
        return EyeDetectionUtils(eyeDetectionText, endSessionOnClick, startSessionOnClick, media)
    }
    private fun configCamera(processCameraProvider: ProcessCameraProvider?,cameraSelector: CameraSelector){
        try{
            processCameraProvider?.unbindAll()
            camera = processCameraProvider?.bindToLifecycle(lifecycleOwner,cameraSelector,preview,imageAnalysis)
            preview?.setSurfaceProvider(previewView.createSurfaceProvider())
        }catch (e: Exception){
            Log.e(TAG,"CAMERA CONFIG FAILED")
        }
    }



}