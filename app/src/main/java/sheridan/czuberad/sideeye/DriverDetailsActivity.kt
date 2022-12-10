package sheridan.czuberad.sideeye

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import sheridan.czuberad.sideeye.databinding.ActivityDriverDetailsBinding
import sheridan.czuberad.sideeye.databinding.ActivityHomeCompanyBinding

class DriverDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDriverDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_details)

        binding = ActivityDriverDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.edtEmail.setText(intent.getStringExtra("driverEmail"))
        binding.tvName.text = intent.getStringExtra("driverName")
        binding.edtPhone.setText(intent.getStringExtra("driverPhone"))
        binding.tvStatus.text = intent.getStringExtra("driverStatus")

    }
}