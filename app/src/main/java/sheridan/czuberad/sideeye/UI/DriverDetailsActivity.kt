package sheridan.czuberad.sideeye.UI

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import sheridan.czuberad.sideeye.ApplicationLogic.DriverDetailsLogic
import sheridan.czuberad.sideeye.Services.CompanyService
import sheridan.czuberad.sideeye.databinding.ActivityDriverDetailsBinding
import sheridan.czuberad.sideeye.Domain.Questionnaire
import sheridan.czuberad.sideeye.Domain.ReactionTest
import sheridan.czuberad.sideeye.Domain.Session
import java.text.SimpleDateFormat
import java.util.*

class DriverDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDriverDetailsBinding
    private lateinit var companyService: CompanyService
//    lateinit var email: String
//    lateinit var phone: String
    //private val sessions = mutableListOf<Session>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDriverDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        companyService = CompanyService()
        var email = intent.getStringExtra("driverEmail")!!
        val name = intent.getStringExtra("driverName")
        val phone = intent.getStringExtra("driverPhone")!!
        val status = intent.getStringExtra("driverStatus")

//        binding.edtEmail.setText(email)
//        binding.tvName.text = name
//        binding.edtPhone.setText(phone)
//        binding.tvStatus.text = status

        //todo: send driver object instead of individual parameters
        //val driver = Driver(name, email, phone, true)

//        binding.btnSave.setOnClickListener{
//            val newEmail = binding.edtEmail.text.toString()
//            val newPhone = binding.edtPhone.text.toString()
//            if (newEmail != email || newPhone != phone){
//                companyService.updateDriverData(email, newEmail, newPhone)
//
//            }
//            email = newEmail
//        }
//
//        binding.btnRemove.setOnClickListener{
//            var dialog = RemoveDriverDialogFragment(email)
//
//            dialog.show(supportFragmentManager, "removeDriverDialog")
//
//        }





        setContent {
            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = "driverDetails") {
                composable("driverDetails") { DriverDetailsContent(navController, email = email,
                    name = name ?: "",
                    phone = phone,
                    status = status ?: "",
                    onSaveClick = { newEmail, newPhone ->
                        if (newEmail != email || newPhone != phone) {
                            companyService.updateDriverData(email, newEmail, newPhone)
                            //Toast.makeText(context, "Successfully removed!", Toast.LENGTH_LONG).show()
                        }
                        email = newEmail
                    },
                    onRemoveClick = { email ->
                        val dialog = RemoveDriverDialogFragment(email)
                        dialog.show(supportFragmentManager, "removeDriverDialog")
                    }) }
                composable("sessionListView") { SessionHistoryForCompany(navController, email) }
                composable("sessionDetail/{sessionID}") { backStackEntry ->
                    val sessionID = backStackEntry.arguments?.getString("sessionID")
                    SessionDetailForCompany(sessionID, email)
                }
            }

        }


    }

    @Composable
    fun DriverDetailsContent(
        navController: NavController,
        email: String,
        name: String,
        phone: String,
        status: String,
        onSaveClick: (String, String) -> Unit,
        onRemoveClick: (String) -> Unit
    ) {
        // State for email and phone
        var newEmail by remember { mutableStateOf(email) }
        var newPhone by remember { mutableStateOf(phone) }

        // Column for layout
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp)
                    .background(color = Color(0xFF39AFEA)),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = "Driver Details",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(8.dp)
                )
            }


            Column {
                // TextView for name
                Text(text = "Name", fontWeight = FontWeight.Bold, fontSize = 16.sp,
                    modifier = Modifier
                        .padding(top = 16.dp, start = 16.dp, end = 16.dp),)
                BasicTextField(
                    value = name,
                    onValueChange = {},
                    textStyle = TextStyle.Default,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                        .background(Color.Gray.copy(alpha = 0.2f))
                        .padding(8.dp),
                    enabled = false
                )
            }

            Column {
                Text(text = "Status", fontWeight = FontWeight.Bold, fontSize = 16.sp,
                    modifier = Modifier
                        .padding(top = 16.dp, start = 16.dp, end = 16.dp),)
                BasicTextField(
                    value = status,
                    onValueChange = {},
                    textStyle = TextStyle.Default,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                        .background(Color.Gray.copy(alpha = 0.2f))
                        .padding(8.dp),
                    enabled = false
                )
            }



            Column {
                Text("Email", fontWeight = FontWeight.Bold, fontSize = 16.sp,
                    modifier = Modifier
                        .padding(top = 16.dp, start = 16.dp, end = 16.dp),)
                BasicTextField(
                    value = newEmail,
                    onValueChange = { newEmail = it },
                    textStyle = TextStyle.Default,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                        .background(Color.Gray.copy(alpha = 0.2f))
                        .padding(8.dp),
                    enabled = false

                )
            }

            Column{
                Text("Phone", fontWeight = FontWeight.Bold, fontSize = 16.sp,
                    modifier = Modifier
                        .padding(top = 16.dp, start = 16.dp, end = 16.dp),)
                BasicTextField(
                    value = newPhone,
                    onValueChange = { newPhone = it },
                    textStyle = TextStyle.Default,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                        .padding(8.dp)
                        .background(Color.White)

                )
            }

            // Button to save changes
            Button(
                onClick = { onSaveClick(newEmail, newPhone) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF39AFEA)),

                ) {
                Text("Save Changes", color = Color.White)
            }

            // Button to remove
            Button(
                onClick = { onRemoveClick(email) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),

                ) {
                Text("Remove This Driver", color = Color.White)
            }

            // Observe sessionsLiveData and set Compose content
//            val sessionsLiveData = companyService.getAllSessionsOfSelectedDriver(email)
//            val sessions by sessionsLiveData.observeAsState(initial = emptyList())

            val viewModel: DriverDetailsLogic = viewModel()
            val sessionList = viewModel.sessions.value ?: emptyList()

            // Update sessions when LiveData changes
            LaunchedEffect(Unit) {
                viewModel.getSessionCardInfoList(email)
            }

            SessionCardListView(navController = navController, sessionList = sessionList)
        }
    }





    @Composable
    fun SessionCardListView(navController: NavController, sessionList: List<Session>?) {
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
                                .height(170.dp)
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

                                Column {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(
                                            text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(session.startSession),
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(session.startSession))
                                    }

                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(
                                            text = session.endSession?.let {
                                                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it)
                                            } ?: "Session is still running...",
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = session.endSession?.let {
                                                SimpleDateFormat("HH:mm", Locale.getDefault()).format(it)
                                            } ?: "",
                                        )
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

                                Column() {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(text = "Reaction Tests: ", fontWeight = FontWeight.Bold)

                                        var reactionTestResult by remember { mutableStateOf<ReactionTest?>(null) }

                                        LaunchedEffect(session.reactionTestUUID) {
                                            // Fetch ReactionTest asynchronously
                                            companyService.fetchReactionTestById(session.reactionTestUUID ?: "") { result ->
                                                reactionTestResult = result
                                            }
                                        }

                                        Text(
                                            text = buildAnnotatedString {
                                                val result = reactionTestResult?.isPassed
                                                val textColor = when {
                                                    result == true -> Color.Green
                                                    result == false -> Color.Red
                                                    else -> Color.Black
                                                }

                                                withStyle(
                                                    style = SpanStyle(color = textColor)
                                                ) {
                                                    append(result?.let { if (it) "PASSED" else "FAILED" } ?: "N/A")
                                                }
                                            }
                                        )


                                    }

                                }

                                Column() {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(text = "Questionnaire: ", fontWeight = FontWeight.Bold)

                                        var questionnaireResult by remember { mutableStateOf<Questionnaire?>(null) }

                                        LaunchedEffect(session.questionnaireUUID) {
                                            // Fetch ReactionTest asynchronously
                                            companyService.fetchQuestionnaireById(session.questionnaireUUID ?: "") { result ->
                                                questionnaireResult = result
                                            }
                                        }

                                        Text(
                                            text = buildAnnotatedString {
                                                val result = questionnaireResult?.isPassed
                                                val textColor = when {
                                                    result == true -> Color.Green
                                                    result == false -> Color.Red
                                                    else -> Color.Black
                                                }

                                                withStyle(
                                                    style = SpanStyle(color = textColor)
                                                ) {
                                                    append(result?.let { if (it) "PASSED" else "FAILED" } ?: "N/A")
                                                }
                                            }
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

}