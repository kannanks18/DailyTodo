package com.machine.dailytodo.utils
import android.content.SharedPreferences
import javax.inject.Inject


class SharedPreferenceRepository
@Inject constructor(private val sharedPreferences: SharedPreferences) {

    fun saveUser(mobile: String?) {
        sharedPreferences.edit().putBoolean(USER_KEY, true).apply()
        sharedPreferences.edit().putString(Mobile, mobile).apply()
    }
    fun isLoggedIn() =sharedPreferences.getBoolean(USER_KEY,false)
    fun mobileNumber() =sharedPreferences.getString(Mobile,"0")


    fun logout() {
        sharedPreferences.edit().putBoolean(USER_KEY, false).apply()
        sharedPreferences.edit().putString(Mobile, null).apply()
    }
    companion object {
        const val USER_KEY = "user_key"
        const val Mobile = "Mobile"
    }

}