package sheridan.czuberad.sideeye

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import sheridan.czuberad.sideeye.Services.CompanyService
import sheridan.czuberad.sideeye.databinding.ActivityDriverDetailsBinding
import sheridan.czuberad.sideeye.databinding.ActivityHomeCompanyBinding
import sheridan.czuberad.sideeye.Domain.Driver

class DriverDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDriverDetailsBinding
    private lateinit var companyService: CompanyService
    lateinit var email: String
    lateinit var phone: String

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
        }

        binding.btnRemove.setOnClickListener{
            var dialog = RemoveDriverDialogFragment(email)

            dialog.show(supportFragmentManager, "removeDriverDialog")
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