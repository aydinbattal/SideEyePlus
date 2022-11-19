package sheridan.czuberad.sideeye

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.ktx.Firebase
import sheridan.czuberad.sideeye.Domain.Driver
import sheridan.czuberad.sideeye.DriversAdapter
import sheridan.czuberad.sideeye.R
import sheridan.czuberad.sideeye.Services.CompanyService
import sheridan.czuberad.sideeye.databinding.ActivityHomeCompanyBinding

/**
 * SideEye+ created by aydin
 * student ID : 991521740
 * on 2022-11-18 */

class HomeCompanyActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var driversList:ArrayList<Driver>
    //private lateinit var driversAdapter:DriversAdapter
    private lateinit var db: FirebaseFirestore
    private lateinit var binding: ActivityHomeCompanyBinding

    private lateinit var companyService:CompanyService

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_company)

        binding = ActivityHomeCompanyBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        db = FirebaseFirestore.getInstance()
        //driversAdapter = DriversAdapter()
        companyService = CompanyService(db)

        val driversAdapter = DriversAdapter()

        // configure the layout manager for the rv
        binding.rvDriversList.layoutManager = LinearLayoutManager(this)
        // associate the rv with the adapter we created
        binding.rvDriversList.adapter = driversAdapter



        companyService.getAllDrivers()

        // observer on the vm's games List
        companyService.driversList.observe(this, Observer {
            Log.d("ABC", "Observed a change in the game list")
            Log.d("ABCDE", it.toString())
            driversAdapter.submitList(it)
        })

    }

//    private fun getAllDrivers(){
//        db = FirebaseFirestore.getInstance()
//
//        db.collection("Drivers").addSnapshotListener(object : EventListener<QuerySnapshot>{
//            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
//                if (error != null){
//                    Log.e("firestore error", error.message.toString())
//                    return
//                }
//
//                for (dc:DocumentChange in value?.documentChanges!!){
//                    if(dc.type == DocumentChange.Type.ADDED){
//                        driversList.add(dc.document.toObject(Driver::class.java))
//                        Log.e("CHECK THIS", driversList.toString())
//
//                    }
//                }
//            }
//
//        })
//
//
//    }
}