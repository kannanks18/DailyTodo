package com.machine.dailytodo.ui.internet

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.machine.dailytodo.R
import com.machine.dailytodo.databinding.FragmentInternetBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InternetFragment : Fragment(R.layout.fragment_internet) {
    lateinit var binding: FragmentInternetBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding= FragmentInternetBinding.bind(view)
    }

}