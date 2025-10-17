package com.example.todoapp.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object PermissionHelper {

    const val REQUEST_NOTIFICATION_PERMISSION = 100
    const val REQUEST_EXACT_ALARM_PERMISSION = 101
    const val REQUEST_BATTERY_OPTIMIZATION = 102

    /**
     * Check if notification permission is granted (Android 13+)
     */
    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // Not needed for older versions
        }
    }

    /**
     * Request notification permission (Android 13+)
     */
    fun requestNotificationPermission(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                REQUEST_NOTIFICATION_PERMISSION
            )
        }
    }

    /**
     * Check if app is exempt from battery optimization
     */
    fun isBatteryOptimizationDisabled(context: Context): Boolean {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            powerManager.isIgnoringBatteryOptimizations(context.packageName)
        } else {
            true
        }
    }

    /**
     * Request battery optimization exemption with explanation dialog
     */
    fun requestBatteryOptimizationExemption(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!isBatteryOptimizationDisabled(activity)) {
                AlertDialog.Builder(activity)
                    .setTitle("Enable Background Notifications")
                    .setMessage(
                        "To receive notifications when your phone is idle or screen is off, " +
                                "please disable battery optimization for this app.\n\n" +
                                "This ensures your todos notify you on time."
                    )
                    .setPositiveButton("Settings") { _, _ ->
                        val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                            data = Uri.parse("package:${activity.packageName}")
                        }
                        try {
                            activity.startActivityForResult(intent, REQUEST_BATTERY_OPTIMIZATION)
                        } catch (e: Exception) {
                            // Fallback to general battery optimization settings
                            val fallbackIntent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
                            activity.startActivity(fallbackIntent)
                        }
                    }
                    .setNegativeButton("Later", null)
                    .show()
            }
        }
    }

    /**
     * Check if exact alarm permission is granted (Android 12+)
     */
    fun canScheduleExactAlarms(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                alarmManager.canScheduleExactAlarms()
            } else {
                true
            }
        } else {
            true
        }
    }

    /**
     * Request exact alarm permission (Android 12+)
     */
    fun requestExactAlarmPermission(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlertDialog.Builder(activity)
                .setTitle("Enable Exact Alarm Permission")
                .setMessage(
                    "To ensure notifications appear exactly when todos are due, " +
                            "please enable the 'Alarms & reminders' permission.\n\n" +
                            "This allows the app to schedule precise notifications."
                )
                .setPositiveButton("Settings") { _, _ ->
                    val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                        data = Uri.parse("package:${activity.packageName}")
                    }
                    activity.startActivityForResult(intent, REQUEST_EXACT_ALARM_PERMISSION)
                }
                .setNegativeButton("Later", null)
                .show()
        }
    }

    /**
     * Open app notification settings
     */
    fun openNotificationSettings(context: Context) {
        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        }
        context.startActivity(intent)
    }

    /**
     * Check all required permissions for reliable notifications
     */
    fun checkAllNotificationPermissions(activity: Activity): Boolean {
        val hasNotification = hasNotificationPermission(activity)
        val hasBatteryExemption = isBatteryOptimizationDisabled(activity)
        val canScheduleAlarms = canScheduleExactAlarms(activity)

        return hasNotification && hasBatteryExemption && canScheduleAlarms
    }

    /**
     * Request all permissions needed for reliable notifications
     */
    fun requestAllNotificationPermissions(activity: Activity) {
        // Request notification permission first (Android 13+)
        if (!hasNotificationPermission(activity)) {
            requestNotificationPermission(activity)
            return
        }

        // Request exact alarm permission (Android 12+)
        if (!canScheduleExactAlarms(activity)) {
            requestExactAlarmPermission(activity)
            return
        }

        // Request battery optimization exemption
        if (!isBatteryOptimizationDisabled(activity)) {
            requestBatteryOptimizationExemption(activity)
        }
    }
}
