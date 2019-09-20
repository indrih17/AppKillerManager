package com.thelittlefireman.appkillermanager.utils

import android.annotation.TargetApi
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Process
import android.os.UserManager
import android.util.Log
import androidx.annotation.RequiresApi
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

object SystemUtils {
    val defaultDebugInformation: String
        get() = "Display_id:" + Build.DISPLAY +
                "MODEL:" + Build.MODEL +
                "MANUFACTURER:" + Build.MANUFACTURER +
                "PRODUCT:" + Build.PRODUCT

    val emuiRomName: String?
        get() = try {
            getSystemProperty("ro.build.version.emui")
        } catch (e: Exception) {
            ""
        }

    fun getApplicationName(context: Context): String {
        val packageManager = context.packageManager
        var applicationInfo: ApplicationInfo? = null
        try {
            applicationInfo =
                packageManager.getApplicationInfo(context.applicationInfo.packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            LogUtils.e(SystemUtils::class.java.name, e.message)
        }

        return (if (applicationInfo != null)
            packageManager.getApplicationLabel(applicationInfo) else "Unknown") as String
    }

    fun getSystemProperty(propName: String): String? {
        var line: String? = null

        try {
            val process = Runtime.getRuntime().exec("getprop $propName")
            BufferedReader(InputStreamReader(process.inputStream), 1024).use { input ->
                line = input.readLine()
            }
        } catch (ex: IOException) {
            Log.e(
                SystemUtils::class.java.javaClass.name,
                "Unable to read system property $propName",
                ex
            )
            return null
        }
        return line
    }

    // INFO http://imsardine.simplbug.com/note/android/adb/commands/am-start.html
    /**
     * Open an Activity by using Application Manager System (prevent from crash permission exception)
     *
     * @param context current application Context
     * @param packageName  pacakge name of the target application (exemple: com.huawei.systemmanager)
     * @param activityPackage activity name of the target application (exemple: .optimize.process.ProtectActivity)
     */
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Throws(IOException::class)
    fun startActivityByAMSystem(context: Context, packageName: String, activityPackage: String) {
        var cmd = "am start -n $packageName/$activityPackage"
        val um = context.getSystemService(Context.USER_SERVICE) as UserManager
        cmd += " --user " + um.getSerialNumberForUser(Process.myUserHandle())
        Runtime.getRuntime().exec(cmd)
    }

    /**
     * Open an KillerManagerActionType by using Application Manager System (prevent from crash permission exception)
     *
     * @param context current application Context
     * @param intentAction  action of the target application (exemple: com.huawei.systemmanager)
     */
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Throws(IOException::class)
    fun startActionByAMSystem(context: Context, intentAction: String) {
        var cmd = "am start -a $intentAction"
        val um = context.getSystemService(Context.USER_SERVICE) as UserManager
        cmd += " --user " + um.getSerialNumberForUser(Process.myUserHandle())
        Runtime.getRuntime().exec(cmd)
    }
}
