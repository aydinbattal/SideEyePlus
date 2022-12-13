package sheridan.czuberad.sideeye

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import sheridan.czuberad.sideeye.Domain.Driver
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
    private lateinit var binding: ActivityHomeCompanyBinding

    private lateinit var companyService:CompanyService

//    override fun onResume(){
//        super.onResume()
//        companyService.driversList.observe(this, Observer {
//            Log.d("ABC", "Observed a change in the drivers list")
//            Log.d("ABCDE", it.toString())
//            driversAdapter.submitList(it)
//            driversAdapter.notifyDataSetChanged()
//        })
//    }
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_company)

        binding = ActivityHomeCompanyBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        companyService = CompanyService()

        val driversAdapter = DriversAdapter()

        // configure the layout manager for the rv
        binding.rvDriversList.layoutManager = LinearLayoutManager(this)
        // associate the rv with the adapter we created
        binding.rvDriversList.adapter = driversAdapter

        binding.btnAddNewDriver.setOnClickListener{
            var dialog = AddDriverDialogFragment()

            dialog.show(supportFragmentManager, "addDriverDialog")
        }

        companyService.getAllDrivers()

        // observer on the vm's games List
        companyService.driversList.observe(this, Observer {
            Log.d("ABC", "Observed a change in the drivers list")
            Log.d("ABCDE", it.toString())
            driversAdapter.submitList(it)
            driversAdapter.notifyDataSetChanged()
        })

        driversAdapter.setOnItemClickListener(object : DriversAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {
                val intent = Intent(this@HomeCompanyActivity, DriverDetailsActivity::class.java)
                intent.putExtra("driverName", driversAdapter.currentList[position].name)
                intent.putExtra("driverEmail", driversAdapter.currentList[position].email)
                intent.putExtra("driverCompany", driversAdapter.currentList[position].companyName)
                intent.putExtra("driverPhone", driversAdapter.currentList[position].phoneNumber)
                intent.putExtra("driverStatus", driversAdapter.currentList[position].status)

                startActivity(intent)
            }

        })

    }

}