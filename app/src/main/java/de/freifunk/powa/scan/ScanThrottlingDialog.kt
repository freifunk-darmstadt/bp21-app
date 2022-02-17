package de.freifunk.powa.scan

import android.content.Context
import android.os.Build
import android.text.Html
import androidx.appcompat.app.AlertDialog
import androidx.preference.PreferenceManager

fun createThrottlingDialog(context: Context) {
    if (Build.VERSION.SDK_INT < 28 || PreferenceManager.getDefaultSharedPreferences(context).getBoolean("scanThrottle", false)) {
        return
    }

    var scanDialog = AlertDialog.Builder(context)
        .setView(null)
        .setTitle("Scan Limit")
        .setMessage(
            Html.fromHtml(
                """Ab Android 9 existiert eine Drosselung des Wlan-Scans.
                <br/><br/>
                Durch diese können nur 4 Scans innerhalb von 2 Minuten durchgeführt werden.
                <br/><br/>
                Android 10 verfügt über eine neue Entwickleroption,
                mit der Sie die Drosslung unter<br/>
                <font color='#FF7F27'>
                    (Einstellungen > Entwickleroptionen > Netzwerk > Drosselung des Wlan-scans)
                </font>deaktivieren können.<br/>
                Für diese Option muss der Entwicklermodus aktiv sein.<br/><br/>
                """,
                Html.FROM_HTML_MODE_COMPACT
            )
        )
        .setPositiveButton("Nichtmehr anzeigen", null)
        .setNegativeButton("OK", null)
        .create()

    scanDialog.setOnShowListener {
        var posBtn = scanDialog.getButton(AlertDialog.BUTTON_POSITIVE)
        var negBtn = scanDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
        posBtn.setOnClickListener {
            val edit = PreferenceManager.getDefaultSharedPreferences(context).edit()
            edit.putBoolean("scanThrottle", true)
            edit.apply()
            scanDialog.dismiss()
        }
        negBtn.setOnClickListener {
            scanDialog.dismiss()
        }
    }

    scanDialog.show()
}
