package sheridan.czuberad.sideeye.Services

import com.google.firebase.auth.FirebaseAuth

class FirebaseAdministration {

    var sign: Boolean = false
    fun signupDriver(email: String, password: String, auth: FirebaseAuth){

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener{
            if(it.isComplete){
                if(it.isSuccessful){
                    signupSuccess()
                }
                else{
                    signupFail()
                }
            }


        }

    }

    private fun signupSuccess() {
        sign = true
    }
    private fun signupFail(){
        sign = false
    }
}