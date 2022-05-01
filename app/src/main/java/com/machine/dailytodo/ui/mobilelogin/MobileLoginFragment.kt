package com.machine.dailytodo.ui.mobilelogin

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.machine.dailytodo.R
import com.machine.dailytodo.databinding.FragmentMobileLoginBinding

import com.machine.dailytodo.utils.showProgressDialog
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MobileLoginFragment : Fragment(R.layout.fragment_mobile_login) {
    lateinit var binding: FragmentMobileLoginBinding

    private lateinit var auth: FirebaseAuth
    var loading: Dialog? = null

    private var storedVerificationId: String? = ""
    private var mobile: String? = ""
    private var otpValue: String? = "0"
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private val viewModel: MobileLoginViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth


        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {

                Log.d(TAG, "onVerificationCompleted:$credential")
                binding.otpLayout.visibility = View.VISIBLE
                binding.mobileLayout.visibility = View.GONE
                otpValue = credential.smsCode
                cancelLoading()
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.w(TAG, "onVerificationFailed", e)
                binding.otpLayout.visibility = View.GONE
                binding.mobileLayout.visibility = View.VISIBLE
                cancelLoading()
                if (e is FirebaseAuthInvalidCredentialsException) {
                } else if (e is FirebaseTooManyRequestsException) {
                }
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                Log.d(TAG, "onCodeSent:$verificationId")
                storedVerificationId = verificationId
                resendToken = token
                binding.otpLayout.visibility = View.VISIBLE
                binding.mobileLayout.visibility = View.GONE
                otpValue = "0"
                cancelLoading()
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMobileLoginBinding.bind(view)
        binding.btnSubmit.setOnClickListener {
            if (TextUtils.isEmpty(binding.mobileNumber.text) && binding.mobileNumber.text?.length == 0) {
                Toast.makeText(
                    requireContext(),
                    "Please Enter Valid Mobile Number",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                mobile = binding.mobileNumber.text.toString()
                initLoading()
                if(mobile=="1234567890"){
                    startPhoneNumberVerification("+1$mobile")
                }else{
                    startPhoneNumberVerification("+91$mobile")
                }

            }
        }
        binding.btnVerify.setOnClickListener {
            if (TextUtils.isEmpty(binding.pinview.text) && binding.pinview.text?.length != 6) {
                Toast.makeText(
                    requireContext(),
                    "Please Enter Valid Mobile Number",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                var otp = binding.pinview.text.toString()
                initLoading()
                verifyPhoneNumberWithCode(storedVerificationId, otp)
            }
        }
    }

    private fun startPhoneNumberVerification(phoneNumber: String) {

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)

    }

    private fun verifyPhoneNumberWithCode(verificationId: String?, code: String) {
        if (otpValue.equals("0")) {
            val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
            signInWithPhoneAuthCredential(credential)

        } else {
            if (otpValue.equals(code)) {
                cancelLoading()
                viewModel.saveUser(mobile)
                findNavController().navigate(MobileLoginFragmentDirections.actionMobileLoginFragmentToDashboardFragment())
            } else {
                Toast.makeText(requireContext(), "INVALID OTP", Toast.LENGTH_LONG).show()
                cancelLoading()
            }
        }

    }
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    cancelLoading()
                    val user = task.result?.user
                    viewModel.saveUser(mobile)
                    findNavController().navigate(MobileLoginFragmentDirections.actionMobileLoginFragmentToDashboardFragment())

                } else {
                    Toast.makeText(requireContext(), "INVALID OTP", Toast.LENGTH_LONG).show()
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {

                    }
                    cancelLoading()


                }
            }
    }

    private fun initLoading() {
        loading = showProgressDialog(requireContext())
    }

    private fun cancelLoading() {
        if (loading != null) {
            loading?.cancel()
            loading = null
        }
    }

    companion object {
        private const val TAG = "OKHTTP"
    }
}