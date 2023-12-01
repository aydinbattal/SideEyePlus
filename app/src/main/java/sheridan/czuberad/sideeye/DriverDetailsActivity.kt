package sheridan.czuberad.sideeye

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import sheridan.czuberad.sideeye.Services.CompanyService
import sheridan.czuberad.sideeye.databinding.ActivityDriverDetailsBinding
import sheridan.czuberad.sideeye.databinding.ActivityHomeCompanyBinding
import sheridan.czuberad.sideeye.Domain.Driver
import sheridan.czuberad.sideeye.Domain.Session
import sheridan.czuberad.sideeye.Services.DriverService
import sheridan.czuberad.sideeye.`Application Logic`.IndependentDriverLogic
import java.text.SimpleDateFormat
import java.util.*

class DriverDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDriverDetailsBinding
    private lateinit var companyService: CompanyService
    lateinit var email: String
    lateinit var phone: String
    private val sessions = mutableListOf<Session>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_details)

        binding = ActivityDriverDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        companyService = CompanyService()
        email = intent.getStringExtra("driverEmail")!!
        val name = intent.getStringExtra("driverName")
        phone = intent.getStringExtra("driverPhone")!!
        val status = intent.getStringExtra("driverStatus")

        binding.edtEmail.setText(email)
        binding.tvName.text = name
        binding.edtPhone.setText(phone)
        binding.tvStatus.text = status

        //todo: send driver object instead of individual parameters
        //val driver = Driver(name, email, phone, true)

        binding.btnSave.setOnClickListener{
            val newEmail = binding.edtEmail.text.toString()
            val newPhone = binding.edtPhone.text.toString()
            if (newEmail != email || newPhone != phone){
                companyService.updateDriverData(email, newEmail, newPhone)

            }
            email = newEmail
        }

        binding.btnRemove.setOnClickListener{
            var dialog = RemoveDriverDialogFragment(email)

            dialog.show(supportFragmentManager, "removeDriverDialog")

        }


        val sessionsLiveData = companyService.getAllSessionsOfSelectedDriver(email)

        sessionsLiveData.observe(this, Observer { sessionsList ->
            if (sessionsList != null) {
                Log.d("DriverDetails", "Sessions: $sessionsList")

                val sortedSessionList = sessionsList.sortedByDescending { it.startSession }

                sessions.clear()
                sessions.addAll(sortedSessionList)

                // Set the Compose content or perform any other actions here
                val composeView = findViewById<ComposeView>(R.id.composeSessionList)
                composeView.setContent {
                    SessionCardListView(navController = NavHostController(LocalContext.current), sessionList = sessions)
                }
            } else {
                Log.d("DriverDetails", "Sessions else: $sessionsList")
            }
        })



    }


    @Composable
    fun SessionCardListView(navController: NavHostController, sessionList: List<Session>?) {
        //Card(modifier = Modifier.offset(y = (-100).dp),colors = CardDefaults.cardColors(
        Card(colors = CardDefaults.cardColors(
            containerColor = Color.White
        )) {

            Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    modifier = Modifier.padding(start = 7.dp),
                    text = "Latest Sessions",
                    color = Color(0xFF39AFEA),
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp  // Adjust the font size as needed
                    )
                )

                TextButton(onClick = {navController.navigate("sessionListView")}, modifier = Modifier.height(33.dp),
                ) {
                    Text(text = "View All", fontSize = 12.sp, color = Color(0xFF39AFEA))
                }

            }
            LazyRow(
                //modifier = Modifier.padding(bottom = 10.dp)
            ){
                if (sessionList != null) {
                    items(sessionList) { session ->
                        Card(
                            modifier = Modifier
                                .height(150.dp)
                                .width(205.dp)
                                .padding(8.dp)
                                .clickable { navController.navigate("sessionDetail/${session.sessionUUID}") },
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            )
                        ) {

                            Column(modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp), verticalArrangement = Arrangement.SpaceBetween) {

                                Column() {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(session.startSession), fontWeight = FontWeight.Bold)
                                        Text(text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(session.startSession))
                                    }

                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(session.endSession), fontWeight = FontWeight.Bold)
                                        Text(text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(session.endSession))
                                    }
                                }



                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {

                                    Column(horizontalAlignment = Alignment.CenterHorizontally){
                                        Text(text = "Alerts",
                                            color = Color(0xFF39AFEA),
                                            style = TextStyle(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp  // Adjust the font size as needed
                                            )
                                        )
                                        Text(text = session.alertUUIDList?.size.toString(),color = Color(0xFF39AFEA),
                                            style = TextStyle(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 20.sp  // Adjust the font size as needed
                                            )
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally){
                                        Text(text = "Fatigue",
                                            color = Color(0xFF39AFEA),
                                            style = TextStyle(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp  // Adjust the font size as needed
                                            )
                                        )

                                        Text(text = session.fatigueList?.size.toString(),color = Color(0xFF39AFEA),
                                            style = TextStyle(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 20.sp  // Adjust the font size as needed
                                            )
                                        )
                                    }


                                }


                            }

                        }
                    }
                }



            }
        }

    }

//    override fun onResume() {
//        super.onResume()
//        // put your code here...
//        binding.btnSave.setOnClickListener{
//            val newEmail = binding.edtEmail.text.toString()
//            val newPhone = binding.edtPhone.text.toString()
//            if (newEmail != email || newPhone != phone){
//                companyService.updateDriverData(email, newEmail, newPhone)
//
//            }
//        }
//    }
}