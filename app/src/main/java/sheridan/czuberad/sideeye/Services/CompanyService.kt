package sheridan.czuberad.sideeye.Services

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase
import sheridan.czuberad.sideeye.Domain.Company
import sheridan.czuberad.sideeye.Domain.Driver
import sheridan.czuberad.sideeye.Domain.Session
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
    private val _sessionsLiveData = MutableLiveData<List<Session>>()
    val sessionsLiveData: LiveData<List<Session>> get() = _sessionsLiveData
    private var selectedDriver: String? = null


    init {
        Log.d("ABC", "vm is initializing")
        driversList.value = ArrayList()
    }

    fun getCurrentOwner(onCompanyLoaded: (Company) -> Unit, onCompanyLoadFailed: (Exception) -> Unit) {
        currentUser?.uid?.let { userId ->
            db.collection("Owners")
                .document(userId)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val companyData = documentSnapshot.toObject(Company::class.java)
                        val currentOwner = Company().apply {
                            companyName = companyData?.companyName
                            email = companyData?.email
                            phoneNumber = companyData?.phoneNumber
                            name = companyData?.name
                        }
                        Log.d("CompanyService", "Current owner: $currentOwner")
                        onCompanyLoaded(currentOwner)
                    } else {
                        Log.d("CompanyService", "Company data not found for user: $userId")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("CompanyService", "Error fetching company data: $exception")
                    onCompanyLoadFailed(exception)
                }
        }
    }

    fun setSelectedDriverId(email: String, callback: (String?) -> Unit) {
        db.collection("Drivers").whereEqualTo("email", email).get()
            .addOnSuccessListener { driverQuerySnapshot ->
                if (!driverQuerySnapshot.isEmpty) {
                    val driverDocument = driverQuerySnapshot.documents.first()
                    val driverId = driverDocument.id
                    selectedDriver = driverId
                    callback(driverId)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener {
                Log.d("CompanyService", "selectedDriver is null")
                callback(null)
            }
    }

    fun fetchAllSessionsByCurrentID(callback: (List<Session>?) -> Unit) {

        if (currentUser != null) {
            selectedDriver?.let {
                db.collection("Drivers").document(it).collection("Sessions").get()
                    .addOnSuccessListener {
                        val sessionList = it.mapNotNull { document ->
                            document.toObject(Session::class.java)
                        }
                        callback(sessionList)
                    }.addOnFailureListener {
                        callback(null)
                    }
            }
        }
    }

    fun fetchDriverSessionById(sessionId: String, callback: (Session?) ->Unit) {
        val result = MutableLiveData<Session?>()
        Log.d("CompanyService", "$selectedDriver")
        if (currentUser != null) {
            selectedDriver?.let {
                db.collection("Drivers").document(it).collection("Sessions").document(sessionId).get()
                    .addOnSuccessListener { documentSnapshot ->
                        Log.d("CompanyService", "CAME")

                        if (documentSnapshot.exists()) {
                            val session = documentSnapshot.toObject(Session::class.java)
                            callback(session)
                        } else {
                            callback(null)
                        }
                    }
                    .addOnFailureListener {
                        callback(null)
                    }
            }
        }

    }


    fun getAllSessionsOfSelectedDriver(email: String): LiveData<List<Session>?> {
        val result = MutableLiveData<List<Session>?>()

        db.collection("Drivers").whereEqualTo("email", email).get()
            .addOnSuccessListener { driverQuerySnapshot ->
                if (!driverQuerySnapshot.isEmpty) {
                    val driverDocument = driverQuerySnapshot.documents.first()

                    db.collection("Drivers").document(driverDocument.id).collection("Sessions").get()
                        .addOnSuccessListener { sessionQuerySnapshot ->
                            val sessionList = sessionQuerySnapshot.mapNotNull { sessionDocument ->
                                sessionDocument.toObject(Session::class.java)
                            }
                            result.postValue(sessionList)
                        }
                        .addOnFailureListener {
                            result.postValue(null)
                        }
                } else {
                    result.postValue(null)
                }
            }
            .addOnFailureListener {
                result.postValue(null)
            }

        return result
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