package edu.cit.velasco.paystream.core

import android.app.Activity
import android.content.Intent
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import edu.cit.velasco.paystream.R
import edu.cit.velasco.paystream.features.auth.LoginActivity
import edu.cit.velasco.paystream.features.employee.AdminDashboardActivity
import edu.cit.velasco.paystream.features.payroll.PayrollProcessingActivity
import edu.cit.velasco.paystream.features.payroll.PayslipsActivity
import edu.cit.velasco.paystream.features.rates.RatesActivity

object HeaderManager {

    fun setupHeader(activity: Activity, currentActivityName: String) {
        val btnMenu = activity.findViewById<ImageView>(R.id.btnMenu)
        val btnProfile = activity.findViewById<ImageView>(R.id.btnProfile)

        // 1. Setup Hamburger Menu
        btnMenu.setOnClickListener { view ->
            val popup = PopupMenu(activity, view)
            popup.menuInflater.inflate(R.menu.menu_navigation, popup.menu)

            // "Highlighting" logic: Disable the menu item for the screen we are already on
            when (currentActivityName) {
                "Dashboard" -> popup.menu.findItem(R.id.nav_dashboard).isEnabled = false
                "Payroll" -> popup.menu.findItem(R.id.nav_payroll).isEnabled = false
                "Payslips" -> popup.menu.findItem(R.id.nav_payslips).isEnabled = false
                "Rates" -> popup.menu.findItem(R.id.nav_rates).isEnabled = false
            }

            // Handle Navigation Clicks
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.nav_dashboard -> navigateTo(activity, AdminDashboardActivity::class.java)
                    R.id.nav_payroll -> navigateTo(activity, PayrollProcessingActivity::class.java)
                    R.id.nav_payslips -> navigateTo(activity, PayslipsActivity::class.java)
                    R.id.nav_rates -> navigateTo(activity, RatesActivity::class.java)
                }
                true
            }
            popup.show()
        }

        // 2. Setup Profile Logout Menu
        btnProfile.setOnClickListener { view ->
            val popup = PopupMenu(activity, view)
            popup.menuInflater.inflate(R.menu.menu_profile, popup.menu)

            popup.setOnMenuItemClickListener { item ->
                if (item.itemId == R.id.action_logout) {
                    // MVP Logout Logic: Clear backstack and go to Login
                    Toast.makeText(activity, "Logging out...", Toast.LENGTH_SHORT).show()
                    val intent = Intent(activity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    activity.startActivity(intent)
                    activity.finish()
                }
                true
            }
            popup.show()
        }
    }

    // Helper function to prevent crashing if they click quickly
    private fun navigateTo(currentActivity: Activity, targetClass: Class<*>) {
        val intent = Intent(currentActivity, targetClass)
        currentActivity.startActivity(intent)
    }
}