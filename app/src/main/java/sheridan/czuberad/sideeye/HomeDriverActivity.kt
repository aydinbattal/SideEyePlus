package sheridan.czuberad.sideeye

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import sheridan.czuberad.sideeye.Domain.Driver
import sheridan.czuberad.sideeye.UI.DriverHome
import sheridan.czuberad.sideeye.UI.SessionDetail
import sheridan.czuberad.sideeye.UI.SessionHistory


class HomeDriverActivity : AppCompatActivity() {
    private lateinit var uid: String
    private lateinit var db: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = "driverHome"){
                composable("driverHome"){ DriverHome(navController)}
                composable("sessionListView"){ SessionHistory(navController)}
                composable("sessionDetail/{sessionID}"){backStackEntry->
                    val sessionID = backStackEntry.arguments?.getString("sessionID")
                    SessionDetail(sessionID)
                }
            }

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

    @Composable
    fun App(){
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