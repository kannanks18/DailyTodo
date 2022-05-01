package com.machine.dailytodo.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.machine.dailytodo.utils.SharedPreferenceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel
@Inject constructor(
    private val sharedPreferenceRepository: SharedPreferenceRepository
) : ViewModel() {


    fun mobileNumber() = sharedPreferenceRepository.mobileNumber()
    fun logout() = sharedPreferenceRepository.logout()

}