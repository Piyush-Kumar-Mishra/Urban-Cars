package com.example.urbancars.auth

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.urbancars.R
import com.example.urbancars.Utils
import com.example.urbancars.activity.UsersActivity
import com.example.urbancars.databinding.FragmentOTPBinding
import com.example.urbancars.models.Users
import com.example.urbancars.viewmodels.AuthViewModel
import kotlinx.coroutines.launch

class OTPFragment : Fragment() {
    private val viewModel: AuthViewModel by viewModels()
    private lateinit var binding: FragmentOTPBinding
    private var userNumber: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOTPBinding.inflate(layoutInflater)
        changeStatusBarColor()
        getPhoneNumber()
        customizingEnterOTP()
        onBackButtonClick()
        sendOTP()
        onLoginButtonClick()

        return binding.root
    }

    private fun getPhoneNumber() {
        val bundle = arguments
        userNumber = bundle?.getString("number").toString()
        binding.tvPhoneNumber.text = "+91$userNumber"
    }

    private fun customizingEnterOTP() {
        val editTexts = arrayOf(
            binding.etOtp1,
            binding.etOtp2,
            binding.etOtp3,
            binding.etOtp4,
            binding.etOtp5,
            binding.etOtp6
        )
        for (i in editTexts.indices) {
            editTexts[i].addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s?.length == 1) {
                        if (i < editTexts.size - 1) {
                            editTexts[i + 1].requestFocus()
                        }
                    } else if (s?.length == 0) {
                        if (i > 0) {
                            editTexts[i - 1].requestFocus()
                        }
                    }
                }

                override fun afterTextChanged(s: Editable?) {

                }

            })
        }


    }

    private fun onBackButtonClick() {
        binding.tbOTP.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_OTPFragment_to_signInFragment)
        }
    }

    private fun sendOTP() {
        Utils.showDialog(requireContext(), "Sending OTP...")
        viewModel.apply {
            sendOTP(userNumber!!, requireActivity())
            lifecycleScope.launch {
                otpSent.collect {
                    if (it == true) {
                        Utils.hideDialog()
                        Utils.showToast(requireContext(), "OTP sent successfully")
                    }
                }
            }

        }
    }

    private fun onLoginButtonClick() {
        binding.btnLogin.setOnClickListener {
            Utils.showDialog(requireContext(), "Singing...")
            val editTexts = arrayOf(
                binding.etOtp1,
                binding.etOtp2,
                binding.etOtp3,
                binding.etOtp4,
                binding.etOtp5,
                binding.etOtp6
            )
            val otp = editTexts.joinToString("") {
                it.text.toString()
            }
            if (otp.length < editTexts.size) {
                Utils.showToast(requireContext(), "Please enter valid OTP")
            } else {
                editTexts.forEach { it.text!!.clear();it.clearFocus() }
                verifyOTP(otp)
            }
        }
    }

    private fun verifyOTP(otp: String) {
        val user=Users(uid=null,userPhoneNumber = userNumber)

        viewModel.signInWithPhoneAuthCredential(otp, userNumber.toString(),user)
        lifecycleScope.launch {
            viewModel.isSignedInSuccessfully.collect {
                if (it == true) {
                    Utils.hideDialog()
                    Utils.showToast(requireContext(), "Signed in successfully")
                    startActivity(Intent(requireContext(), UsersActivity::class.java))
                }
            }
        }
    }


    private fun changeStatusBarColor() {
        activity?.window?.apply {
            statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }
}
