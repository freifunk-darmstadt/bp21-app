package de.freifunk.powa.permissions

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.provider.Settings
import java.lang.Exception

fun enableLocationServices(context: Context) {
    val alertDialog = AlertDialog.Builder(context)

    // Setting Dialog Title
    alertDialog.setTitle("Standort aktivieren?")
    // Setting Dialog Message
    alertDialog.setMessage("Standort ist ausgeschaltet! Möchtest du den Standortdienst einschalten?")

    // On pressing Settings button
    alertDialog.setPositiveButton(
        "Einstellungen"
    ) { _, _ ->
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        context.startActivity(intent)
    }

    // on pressing cancel button
    alertDialog.setNegativeButton(
        "Abbrechen"
    ) { dialog, _ -> dialog.cancel(); }

    // Showing Alert Message
    alertDialog.show()
}

fun enableWifiServices(context: Context) {
    val alertDialog = AlertDialog.Builder(context)

    // Setting Dialog Title
    alertDialog.setTitle("Wlan aktivieren?")
    // Setting Dialog Message
    alertDialog.setMessage("Wlan ist ausgeschaltet! Möchtest du Wlan einschalten?")

    // On pressing Settings button
    alertDialog.setPositiveButton(
        "Einstellungen"
    ) { _, _ ->
        val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
        context.startActivity(intent)
    }

    // on pressing cancel button
    alertDialog.setNegativeButton(
        "Abbrechen"
    ) { dialog, _ -> dialog.cancel(); }

    // Showing Alert Message
    alertDialog.show()
}

fun isGPSEnabled(context: Context): Boolean {
    val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    // check if the device has gps
    if (!lm.allProviders.contains(LocationManager.GPS_PROVIDER))
        return false

    var gps_enabled = false

    try {
        gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
    } catch (ex: Exception) {
    }

    return gps_enabled
}

fun isWIFIEnabled(context: Context): Boolean {
    val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    // check if the device has network-access
    if (!lm.allProviders.contains(LocationManager.NETWORK_PROVIDER))
        return false

    var network_enabled = false

    try {
        network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    } catch (ex: Exception) {
    }

    return network_enabled
}
