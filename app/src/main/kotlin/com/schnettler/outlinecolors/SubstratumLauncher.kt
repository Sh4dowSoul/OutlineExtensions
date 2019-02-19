@file:Suppress("ConstantConditionIf")

package com.schnettler.outlinecolors

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.schnettler.outlinecolors.AdvancedConstants.SHOW_DIALOG_REPEATEDLY
import com.schnettler.outlinecolors.AdvancedConstants.SHOW_LAUNCH_DIALOG
import com.schnettler.outlinecolors.ThemeFunctions.getSelfSignature
import com.schnettler.outlinecolors.ThemeFunctions.isCallingPackageAllowed

class SubstratumLauncher : Activity() {

    private val substratumIntentData = "projekt.substratum.THEME"
    private val getKeysIntent = "projekt.substratum.GET_KEYS"
    private val receiveKeysIntent = "projekt.substratum.RECEIVE_KEYS"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /* STEP 3: Do da thang */
        if (SHOW_LAUNCH_DIALOG) run {
            val sharedPref = getPreferences(Context.MODE_PRIVATE)
            if (SHOW_DIALOG_REPEATEDLY) {
                showDialog()
                sharedPref.edit().remove("dialog_showed").apply()
            } else if (!sharedPref.getBoolean("dialog_showed", false)) {
                showDialog()
                sharedPref.edit().putBoolean("dialog_showed", true).apply()
            } else {
                launchSubstratum()
            }
        } else {
            launchSubstratum()
        }
    }

    private fun launchSubstratum() {
        val returnIntent = if (intent.action == getKeysIntent) {
            Intent(receiveKeysIntent)
        } else {
            Intent()
        }
        val themeName = getString(R.string.ThemeName)
        val themeAuthor = getString(R.string.ThemeAuthor)
        val themePid = packageName
        returnIntent.putExtra("theme_name", themeName)
        returnIntent.putExtra("theme_author", themeAuthor)
        returnIntent.putExtra("theme_pid", themePid)
        returnIntent.putExtra("theme_debug", BuildConfig.DEBUG)
        returnIntent.putExtra("theme_piracy_check", false)
        returnIntent.putExtra("encryption_key", BuildConfig.DECRYPTION_KEY)
        returnIntent.putExtra("iv_encrypt_key", BuildConfig.IV_KEY)

        val callingPackage = intent.getStringExtra("calling_package_name")
        if (!isCallingPackageAllowed(callingPackage)) {
            finish()
        } else {
            returnIntent.`package` = callingPackage
        }
        if (intent.action == substratumIntentData) {
            setResult(getSelfSignature(applicationContext), returnIntent)
        } else if (intent.action == getKeysIntent) {
            returnIntent.action = receiveKeysIntent
            sendBroadcast(returnIntent)
        }
        finish()
    }

    private fun showDialog() {
        val dialog = AlertDialog.Builder(this, R.style.DialogStyle)
                .setCancelable(false)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(R.string.launch_dialog_title)
                .setMessage(R.string.launch_dialog_content)
                .setPositiveButton(R.string.launch_dialog_positive) { _, _ -> launchSubstratum() }
        if (getString(R.string.launch_dialog_negative).isNotEmpty()) {
            if (getString(R.string.launch_dialog_negative_url).isNotEmpty()) {
                dialog.setNegativeButton(R.string.launch_dialog_negative) { _, _ ->
                    startActivity(Intent(Intent.ACTION_VIEW,
                            Uri.parse(getString(R.string.launch_dialog_negative_url))))
                    finish()
                }
            } else {
                dialog.setNegativeButton(R.string.launch_dialog_negative) { _, _ -> finish() }
            }
        }
        dialog.show()
    }
}