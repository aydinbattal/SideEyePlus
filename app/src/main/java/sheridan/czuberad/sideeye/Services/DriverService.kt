package sheridan.czuberad.sideeye.Services

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import sheridan.czuberad.sideeye.Domain.Driver

class DriverService {

    fun getDriverData(db: FirebaseFirestore, currentUser: FirebaseUser?) {
        if (currentUser != null) {

            db.collection("Drivers").document(currentUser.uid).get()
                .addOnSuccessListener { document ->
                   var driverData = document.toObject(Driver::class.java) ?: Driver()
                }
        }
    }
}