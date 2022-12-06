package sheridan.czuberad.sideeye

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import sheridan.czuberad.sideeye.Services.CompanyService
import sheridan.czuberad.sideeye.databinding.FragmentAddDriverDialogBinding

class AddDriverDialogFragment : DialogFragment() {
    private var _binding: FragmentAddDriverDialogBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var companyService: CompanyService = CompanyService()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAddDriverDialogBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.btnCancelAddDriver.setOnClickListener{
            dismiss()
        }

        binding.btnAddDriver.setOnClickListener{
            val email = binding.edtEmail.text.toString()
            companyService.addNewDriver(email)
            Toast.makeText(context, "Successfully added!", Toast.LENGTH_LONG).show()
            dismiss()
        }

        return view



        //return inflater.inflate(R.layout.fragment_add_driver_dialog, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}