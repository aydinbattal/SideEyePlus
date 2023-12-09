package sheridan.czuberad.sideeye

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import sheridan.czuberad.sideeye.Domain.Company
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

    @Composable
    fun OwnerInfoCard(companyOwner: Company) {
        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp.dp
        val screenHeight = configuration.screenHeightDp.dp

        Card(modifier = Modifier
            .width(screenWidth)
            .height(screenHeight / 4),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF39AFEA)
            ),
            shape = RectangleShape
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Hi, ${companyOwner.name}",
                    color = Color.White,
                    fontSize = 35.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${companyOwner.companyName}",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Column(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 0.dp)
                    .fillMaxWidth()
            ) {

                Text(
                    text = "Welcome back to your company dashboard!",
                    color = Color.White,
                    fontSize = 16.sp
                )
                // Add more details or customization based on your CompanyOwner data model
            }
        }
    }

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
        val swipeRefreshLayout: SwipeRefreshLayout = binding.swipeRefreshLayout


        val companyService = CompanyService()
        companyService.getCurrentOwner(
            onCompanyLoaded = { companyOwner ->
                // Handle the loaded company data
                Log.d("HomeCompanyActivity", "Received company data: $companyOwner")
                val composeOwnerInfoCard = findViewById<ComposeView>(R.id.composeOwnerInfoCard)
                composeOwnerInfoCard.setContent {
                    OwnerInfoCard(companyOwner = companyOwner)
                }
            },
            onCompanyLoadFailed = { exception ->
                // Handle the failed company data load
                Log.e("HomeCompanyActivity", "Failed to load company data: $exception")
            }
        )

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
            //driversAdapter.notifyDataSetChanged()
        })

        swipeRefreshLayout.setOnRefreshListener {
            companyService.getAllDrivers()
            swipeRefreshLayout.isRefreshing = false
        }

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