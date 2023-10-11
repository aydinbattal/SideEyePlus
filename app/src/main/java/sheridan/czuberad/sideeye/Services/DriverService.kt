package sheridan.czuberad.sideeye.Services

import android.os.Debug
import android.util.Log
import androidx.compose.ui.layout.LookaheadLayout
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import sheridan.czuberad.sideeye.Domain.Alert
import sheridan.czuberad.sideeye.Domain.Driver
import sheridan.czuberad.sideeye.Domain.Session

class DriverService {
    private var db = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser?.uid
    fun addAlertToSessionById(
        currentUser: FirebaseUser?,
        db: FirebaseFirestore,
        uid: String,
        session: Session
    ) {
        var AlertList = arrayListOf<Alert>()
        AlertList.add(Alert("Eye"))
        AlertList.add(Alert("Face"))
        if (currentUser != null) {
            db.collection("Drivers").document(currentUser.uid).collection("Sessions").document(uid).set(session).addOnCompleteListener {
                if (it.isSuccessful){
                    AlertList.forEach{ alert ->
                        db.collection("Drivers").document(currentUser.uid).collection("Sessions").document(uid).collection("Alerts").document().set(alert)
                    }
                }
            }
        }

    }

    fun checkIsDriverInDB(emailText: String): Task<QuerySnapshot> {

        return db.collection("Drivers").whereEqualTo("email",emailText).get()

    }

//    fun fetchCurrentUser(): Driver {
//
//        val driver = Driver()
//        if(currentUser != null){
//            db.collection("Drivers").document(currentUser).get()
//                .addOnSuccessListener {
//                    if(it != null && it.exists()){
//                        Log.d("YOO", ("Inside fetchCurrentUser: " + it.getString("name")) ?: "")
//                        driver.companyName = it.getString("companyName") ?: ""
//                        driver.email = it.getString("email") ?: ""
//                        driver.name = it.getString("name") ?: ""
//                        driver.phoneNumber = it.getString("phoneNumber") ?: ""
//                        driver.status = it.getBoolean("status") ?: false
//                    }
//                }
//        }
//        Log.d("YOO", "Inside fetchCurrentUser before return " + driver.name)
//
//        return driver
//
//
//    }

    fun fetchCurrentUser(callback: (Driver?) -> Unit) {
        if (currentUser != null) {
            db.collection("Drivers").document(currentUser).get()
                .addOnSuccessListener {
                    if (it != null && it.exists()) {
                        val driver = Driver().apply {
                            companyName = it.getString("companyName") ?: ""
                            email = it.getString("email") ?: ""
                            name = it.getString("name") ?: ""
                            phoneNumber = it.getString("phoneNumber") ?: ""
                            status = it.getBoolean("status") ?: false
                        }
                        callback(driver)
                    } else {
                        callback(null)
                    }
                }
                .addOnFailureListener {
                    callback(null)
                }
        } else {
            callback(null)
        }
    }

}