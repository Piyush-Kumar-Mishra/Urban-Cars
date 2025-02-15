package com.example.urbancars.auth

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.urbancars.R
import com.example.urbancars.Utils
import com.example.urbancars.databinding.FragmentSignInBinding

class SignInFragment : Fragment() {
    private  lateinit var binding :FragmentSignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignInBinding.inflate(inflater, container, false)
        changeStatusBarColor()
        getPhoneNumber()
        onContinueButtonClick()
        return binding.root
    }


    private fun changeStatusBarColor() {
        activity?.window?.apply {
            // Set the status bar color
            statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)
            // Check if the device supports changing status bar appearance
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }

    private fun getPhoneNumber() {
        binding.etUserNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(number: CharSequence?, start: Int, before: Int, count: Int) {
                val len = number?.length
                if (len == 10) {
                    binding.btnContinue.setBackgroundColor(
                        ContextCompat.getColor(requireContext(), R.color.yellow)
                    )
                } else {
                    binding.btnContinue.setBackgroundColor(
                        ContextCompat.getColor(requireContext(), R.color.black)
                    )
                }
            }
            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    private fun onContinueButtonClick(){
        binding.btnContinue.setOnClickListener {
            val number=binding.etUserNumber.text.toString()
            if(number.isEmpty() || number.length!=10){
                Utils.showToast(requireContext(),"Enter valid number")
            }
            else{
                val bundle =Bundle()
                bundle.putString("number",number)
                findNavController().navigate(R.id.action_signInFragment_to_OTPFragment,bundle)
            }
        }
    }
}