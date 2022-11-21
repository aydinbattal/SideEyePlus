package sheridan.czuberad.sideeye.Services

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import sheridan.czuberad.sideeye.Domain.Alert
import sheridan.czuberad.sideeye.Domain.Driver
import sheridan.czuberad.sideeye.Domain.Session

class DriverService {
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

}