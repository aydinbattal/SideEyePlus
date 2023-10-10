package sheridan.czuberad.sideeye

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import sheridan.czuberad.sideeye.Domain.Driver
import sheridan.czuberad.sideeye.HomeDriverActivity.Companion.REQUEST_CODE_PERMISSIONS
import sheridan.czuberad.sideeye.HomeDriverActivity.Companion.REQUIRED_PERMISSIONS
import sheridan.czuberad.sideeye.UI.DriverHome
import kotlin.math.roundToInt


class HomeDriverActivity : AppCompatActivity() {
    private lateinit var uid: String
    private lateinit var db: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContent {
            DriverHome()
        }
//        setContentView(R.layout.activity_home_driver)
//        checkPermissions()
//        val currentUser = Firebase.auth.currentUser
//
//        val nameText = findViewById<TextView>(R.id.textView_driver_home_name)
//        db = FirebaseFirestore.getInstance()
//        getDriverData(currentUser, nameText)
//        //val driverService = DriverService()
//        //driverService.getDriverData(db, currentUser)
//
//
//        val eyeClick = findViewById<Button>(R.id.button_eye)
//        eyeClick.setOnClickListener {
//            val intent = Intent(this, EyeDetectionActivity::class.java)
//            startActivity(intent)
//        }
//
//        val testClick = findViewById<Button>(R.id.button_test)
//        testClick.setOnClickListener {
//            val intent = Intent(this, ReactionTestActivity::class.java)
//            startActivity(intent)
//        }

    }



    private fun isPermissionsAllowed() = HomeDriverActivity.REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext,it) == PackageManager.PERMISSION_GRANTED
    }

    private fun checkPermissions(){
        if(!isPermissionsAllowed()){
            ActivityCompat.requestPermissions(this,
                HomeDriverActivity.REQUIRED_PERMISSIONS,
                HomeDriverActivity.REQUEST_CODE_PERMISSIONS
            )
        }
    }
    private fun getDriverData(currentUser: FirebaseUser?, nameText: TextView) {
        if (currentUser != null) {
            this.db.collection("Drivers").document(currentUser.uid).get()
                .addOnSuccessListener { document ->
                    val driverData = document.toObject(Driver::class.java) ?: Driver()
                    nameText.text = driverData.email
                }
        }

    }
    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA)
    }

}