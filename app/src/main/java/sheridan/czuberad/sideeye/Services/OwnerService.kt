package sheridan.czuberad.sideeye.Services

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class OwnerService {

    private var db = FirebaseFirestore.getInstance()

    fun checkIsCompanyInDB(emailText: String): Task<QuerySnapshot> {

        return db.collection("Owners").whereEqualTo("email",emailText).get()

    }
}