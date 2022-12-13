package sheridan.czuberad.sideeye.Services

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase
import sheridan.czuberad.sideeye.Domain.Company
import sheridan.czuberad.sideeye.Domain.Driver
import java.security.acl.Owner


/**
 * SideEye+ created by aydin
 * student ID : 991521740
 * on 2022-11-18 */


class CompanyService() {

    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    val driversList = MutableLiveData<MutableList<Driver>>()
    private val currentUser = Firebase.auth.currentUser
    val alertTimes = MutableLiveData<MutableList<String>>()


    init {
        Log.d("ABC", "vm is initializing")
        driversList.value = ArrayList()
    }

    fun getDriverSessions(email: String) {
        val alertsFromDb:ArrayList<String> = arrayListOf()

        db.collection("Drivers").whereEqualTo("email",email).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    db.collection("Drivers").document(document.id).collection("Sessions").get().addOnSuccessListener { sessions ->
                        sessions.query.orderBy("endSession", Query.Direction.DESCENDING).limit(1).get().addOnSuccessListener { session ->
                            if(session.documents.isNotEmpty())
                            {
                                val lastAlert = session.documents[0]?.data?.get("endSession") as com.google.firebase.Timestamp

                                alertsFromDb.add(lastAlert.toDate().toString())
                                alertTimes.postValue(alertsFromDb)
                                Log.d("lastAlert", "${lastAlert}")
                            }

//                            db.collection("Drivers").document(document.id).collection("Sessions").document(session.documents[0].id).get().addOnSuccessListener { alerts ->
//                                Log.d("lastAlert", "${alerts.data["time"]}")
//
//                            }
                        }


//                        for (session in sessions) {
//                            session.data["endSession"]
//                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w("getDriverSessions", "Error getting documents: ", exception)
            }

        //return alertTimes

    }

    fun removeDriverFromCompany(email: String)
    {
        db.collection("Drivers").whereEqualTo("email",email).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    db.collection("Drivers").document(document.id).update("companyName", "")
                    Log.d("removeDriverFromCompany", "${document.id} => ${document.data}")
                    val driver = document.toObject(Driver::class.java)
                    driversList.value?.remove(driver)
                    driversList.value = driversList.value
                    Log.d("removeDriverFromCompany", "${driversList.value} ")

                }
            }
            .addOnFailureListener { exception ->
                Log.w("removeDriverFromCompany", "Error getting documents: ", exception)
            }
    }

    fun updateDriverData(email: String, newEmail: String, newPhone: String){
        db.collection("Drivers").whereEqualTo("email",email).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    db.collection("Drivers").document(document.id).update("email", newEmail, "phoneNumber", newPhone)

                    Log.d("updateDriverData", "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("updateDriverData", "Error getting documents: ", exception)
            }
    }

//    fun getDriverByEmail(email: String): Driver {
//        db.collection("Drivers").whereEqualTo("email",email).get()
//            .addOnSuccessListener { documents ->
//                for (document in documents) {
//                    val driver = document.toObject(Driver::class.java)
//                    return driver
//                }
//            }
//            .addOnFailureListener { exception ->
//                Log.w("getDriverByEmail", "Error getting documents: ", exception)
//            }
//    }

    fun addNewDriver(email: String){
        var company = ""
        db.collection("Owners").document(currentUser!!.uid).get().addOnSuccessListener { document ->
            if (document != null) {
                val owner = document.toObject(Company::class.java)
                company = owner!!.companyName.toString()
                Log.d(TAG, "DocumentSnapshot data: ${document.data}")
            } else {
                Log.d(TAG, "No such document")
            }
        }.addOnFailureListener { exception ->
            Log.d(TAG, "get failed with ", exception)
        }

        db.collection("Drivers").whereEqualTo("email",email).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val driver = document.toObject(Driver::class.java)
//                    driver.companyName = company
                    db.collection("Drivers").document(document.id).update("companyName", company)
                    Log.d("addNewDriver", "${company}")
                    Log.d("addNewDriver", "${document.id} => ${document.data}")
                    driversList.value?.add(driver)
                    driversList.value = driversList.value
                }
            }
            .addOnFailureListener { exception ->
                Log.w("addNewDriver", "Error getting documents: ", exception)
            }

    }

    fun getAllDrivers(){
        //db = FirebaseFirestore.getInstance()
        val driversFromDb:ArrayList<Driver> = arrayListOf()
        //val currentUser = Firebase.auth.currentUser
        //var company = ""

        db.collection("Owners").document(currentUser!!.uid).addSnapshotListener(object : EventListener<DocumentSnapshot> {
            override fun onEvent(value: DocumentSnapshot?, error: FirebaseFirestoreException?) {
                if (error != null) {
                    Log.e("firestore error", error.message.toString())
                    return
                }

                val company = value!!.data!!["companyName"].toString()

                db.collection("Drivers").whereEqualTo("companyName",company).addSnapshotListener(object : EventListener<QuerySnapshot> {
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