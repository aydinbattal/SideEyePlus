package sheridan.czuberad.sideeye.UI

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import sheridan.czuberad.sideeye.Adapters.DriversAdapter
import sheridan.czuberad.sideeye.Services.CompanyService
import sheridan.czuberad.sideeye.databinding.FragmentRemoveDriverDialogBinding


class RemoveDriverDialogFragment(private val email: String) : DialogFragment() {
    private var _binding: FragmentRemoveDriverDialogBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var companyService: CompanyService = CompanyService()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRemoveDriverDialogBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.btnCancelRemoveDriver.setOnClickListener{
            dismiss()
        }

        binding.btnRemoveDriver.setOnClickListener{
            companyService.removeDriverFromCompany(email)
            val driversAdapter = DriversAdapter()
            //driversAdapter.notifyDataSetChanged()
            Toast.makeText(context, "Successfully removed!", Toast.LENGTH_LONG).show()

            //todo: find out why its not updating previously added drivers
            //todo: currently it updates if you remove user after just adding it
            companyService.driversList.observe(this, Observer {
                Log.d("ABC", "Observed a change in the drivers list")
                Log.d("ABCDE", it.toString())
                driversAdapter.notifyDataSetChanged()
                dismiss()

                // Create an intent to navigate to HomeCompanyActivity
                val intent = Intent(context, HomeCompanyActivity::class.java).apply {
                    // Set flags to clear the existing task and create a new one
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                }
                startActivity(intent)
            })
        }

        return view



        //return inflater.inflate(R.layout.fragment_add_driver_dialog, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}