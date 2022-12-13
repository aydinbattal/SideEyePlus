package sheridan.czuberad.sideeye

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import sheridan.czuberad.sideeye.Services.CompanyService
import sheridan.czuberad.sideeye.databinding.FragmentAddDriverDialogBinding
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
            driversAdapter.notifyDataSetChanged()
            Toast.makeText(context, "Successfully removed!", Toast.LENGTH_LONG).show()
            dismiss()
            val intent = Intent(context, HomeCompanyActivity::class.java)
            startActivity(intent)
        }

        return view



        //return inflater.inflate(R.layout.fragment_add_driver_dialog, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}