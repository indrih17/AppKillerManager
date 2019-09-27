package com.thelittlefireman.appkillermanager.utils

import android.annotation.TargetApi
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Process
import android.os.UserManager
import androidx.annotation.RequiresApi
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

object SystemUtils {
    val defaultDebugInformation: String = """
            Manufacturer: ${Build.MANUFACTURER},
            Model: ${Build.MODEL},
            Display id: ${Build.DISPLAY},
            Product: ${Build.PRODUCT},
            Release: ${Build.VERSION.RELEASE},
            Sdk: ${Build.VERSION.SDK_INT}
        """.trimIndent()

    val emuiRomName: String? = try {
        getSystemProperty("ro.build.version.emui")
    } catch (e: Exception) {
        null
    }

    fun getApplicationName(context: Context): String =
        try {
            val packageManager = context.packageManager
            val appInfo = packageManager.getApplicationInfo(context.applicationInfo.packageName, 0)
            packageManager
                .getApplicationLabel(appInfo)
                .toString()
        } catch (e: PackageManager.NameNotFoundException) {
            LogUtils.e<SystemUtils>(e)
            "Unknown"
        }

    fun getSystemProperty(propName: String): String? {
        try {
            val process = Runtime.getRuntime().exec("getprop $propName")
            BufferedReader(InputStreamReader(process.inputStream), 1024).use { input ->
                return input.readLine()
            }
        } catch (ex: IOException) {
            LogUtils.e<SystemUtils>(
                ex,
                "Unable to read system property $propName"
            )
            return null
        }
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
        val cmd = StringBuilder("am start -n $packageName/$activityPackage")
        val um = context.getSystemService(Context.USER_SERVICE) as UserManager
        cmd.append(" --user ${um.getSerialNumberForUser(Process.myUserHandle())}")
        Runtime.getRuntime().exec(cmd.toString())
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
        val cmd = StringBuilder("am start -a $intentAction")
        val um = context.getSystemService(Context.USER_SERVICE) as UserManager
        cmd.append(" --user ${um.getSerialNumberForUser(Process.myUserHandle())}")
        Runtime.getRuntime().exec(cmd.toString())
    }
}
