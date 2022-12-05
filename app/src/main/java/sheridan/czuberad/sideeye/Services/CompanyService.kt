package sheridan.czuberad.sideeye.Services

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase
import sheridan.czuberad.sideeye.Domain.Company
import sheridan.czuberad.sideeye.Domain.Driver


/**
 * SideEye+ created by aydin
 * student ID : 991521740
 * on 2022-11-18 */


class CompanyService(val db: FirebaseFirestore) {

    val driversList: MutableLiveData<List<Driver>> = MutableLiveData<List<Driver>>(listOf())

    init {
        Log.d("ABC", "vm is initializing")
    }


    fun getAllDrivers(){
        //db = FirebaseFirestore.getInstance()
        val driversFromDb:ArrayList<Driver> = arrayListOf()
        val currentUser = Firebase.auth.currentUser
        //var company = ""

        db.collection("Owners").document(currentUser!!.uid).addSnapshotListener(object : EventListener<DocumentSnapshot> {
            override fun onEvent(value: DocumentSnapshot?, error: FirebaseFirestoreException?) {
                if (error != null) {
                    Log.e("firestore error", error.message.toString())
                    return
                }

                //todo: this gets data but doesnt assign to company variable
                //company = value!!.data!!["companyName"].toString()

                db.collection("Drivers").whereEqualTo("companyName",value!!.data!!["companyName"].toString()).addSnapshotListener(object : EventListener<QuerySnapshot> {
                    override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                        if (error != null){
                            Log.e("firestore error", error.message.toString())
                            return
                        }

                        for (dc: DocumentChange in value?.documentChanges!!){
                            if(dc.type == DocumentChange.Type.ADDED){
                                driversFromDb.add(dc.document.toObject(Driver::class.java))
                                //Log.d("CHECK THIS", driversFromDb.toString())
                                driversList.postValue(driversFromDb)
                                Log.d("CHECK THIS AFTER", "${driversList.value}")
                            }
                        }
                    }

                })
            }
            })
        //Log.d("companyName", company)






    }
}