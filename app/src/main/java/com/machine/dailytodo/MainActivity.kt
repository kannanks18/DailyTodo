package com.machine.dailytodo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.machine.dailytodo.databinding.ActivityMainBinding
import com.machine.dailytodo.utils.NetworkCheckUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    var bottomSheetDialog: BottomSheetDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.findNavController()
        val networkCheckUtil = NetworkCheckUtil(applicationContext)
        networkCheckUtil.observe(this) { b ->
            if (b) {
                if (navController.currentDestination?.id == R.id.internetFragment) {
                    navController.navigateUp()
                }
            } else {
                navController.navigate(MobileNavigationDirections.actionGlobalToInternetFragment())
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        when (navController.currentDestination?.id) {
            R.id.dashboardFragment -> {
               bottomSheetDialog("Exit", "Do You Want to Exit ")
            }
            R.id.loginFragment -> {
               bottomSheetDialog("Exit", "Do You Want to Exit ")
            }
            else -> super.onBackPressed()
        }
    }

    private fun bottomSheetDialog(message: String, header: String) {
        val view: View = LayoutInflater.from(this).inflate(R.layout.layout_warning_msg, null)
        val bottomyes: AppCompatButton = view.findViewById(R.id.btnYess)
        val bottomNo: AppCompatButton = view.findViewById(R.id.btnNo)
        val warningMsg: TextView = view.findViewById(R.id.warningmsg)
        val messageHeader: TextView = view.findViewById(R.id.header_title)
        messageHeader.text = message
        warningMsg.text = header
        bottomyes.setOnClickListener {
            finish()
            bottomSheetDialog!!.cancel()
        }
        bottomNo.setOnClickListener {

            bottomSheetDialog!!.cancel()
        }
        bottomSheetDialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        bottomSheetDialog!!.setContentView(view)
        bottomSheetDialog!!.setCancelable(false)
        bottomSheetDialog!!.show()
    }

}