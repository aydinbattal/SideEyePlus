package sheridan.czuberad.sideeye.Services

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

class FirebaseAdministration {

    private var auth = FirebaseAuth.getInstance()
    fun loginIn(emailText: String, passwordText: String): Task<AuthResult> {

        return auth.signInWithEmailAndPassword(emailText,passwordText)
    }



}