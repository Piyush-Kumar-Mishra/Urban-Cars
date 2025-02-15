package com.example.urbancars.viewmodels

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.example.urbancars.Utils
import com.example.urbancars.models.Users
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.concurrent.TimeUnit

class AuthViewModel: ViewModel() {

        private val _verificationId = MutableStateFlow("null")
        private val  _otpSent= MutableStateFlow(false)
        val otpSent = _otpSent
        private val _isSignedInSuccessfully = MutableStateFlow(false)
        val isSignedInSuccessfully = _isSignedInSuccessfully
        private val _alreadySignedIn = MutableStateFlow(false)
        val alreadySignedIn = _alreadySignedIn

        init{
            Utils.getAuthInstance().currentUser?.let{
                _alreadySignedIn.value=true
            }
        }

    fun sendOTP(userNumber: String,activity:Activity) {

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {

            }

            override fun onVerificationFailed(e: FirebaseException) {


            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken,
            ) {
                // Called when OTP is successfully sent
                _verificationId.value = verificationId
                _otpSent.value=true

            }
        }

        //This configures how Firebase should send the OTP.
        val options = PhoneAuthOptions.newBuilder(Utils.getAuthInstance())
            .setPhoneNumber("+91$userNumber")      // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun signInWithPhoneAuthCredential(otp: String, userNumber: String, user: Users) {
        val credential=PhoneAuthProvider.getCredential(_verificationId.value.toString(),otp)
        Utils.getAuthInstance().signInWithCredential(credential)
            .addOnCompleteListener{ task ->
                user.uid=Utils.getCurrentUserId()
                if (task.isSuccessful) {
                    FirebaseDatabase.getInstance().getReference("AllUsers").child("Users").child(user.uid!!).setValue(user)
                    _isSignedInSuccessfully.value=true

                } else {

                    }
                }
            }
    }
