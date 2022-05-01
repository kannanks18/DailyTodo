package com.machine.dailytodo.ui.splash

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.machine.dailytodo.R
import com.machine.dailytodo.databinding.FragmentSplashBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*

@AndroidEntryPoint
class SplashFragment : Fragment(R.layout.fragment_splash) {
    lateinit var binding: FragmentSplashBinding
    val handler = Handler()
    private val viewModel: SplashViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSplashBinding.bind(view)
        Log.d("okhttp", "onViewCreated: "+viewModel.checkLogin())
        if (!viewModel.checkLogin()) {
            handler.postDelayed({
                findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToLoginFragment())
            }, 500)
        } else {
            handler.postDelayed({
                findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToDashboardFragment())
            }, 500)
        }

    }
}