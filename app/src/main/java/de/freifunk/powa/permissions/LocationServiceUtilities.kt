package de.freifunk.powa.permissions

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.provider.Settings
import java.lang.Exception

/**
 * This function creates and displays a dialog that asks the user to activate the location services
 *
 * @param context the context to create the request in
 */
fun enableLocationServices(context: Context) {
    if (isGPSEnabled(context)) {
        return
    }

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

/**
 * This function creates and displays a dialog that asks the user to activate wifi
 *
 * @param context the context to create the request in
 */
fun enableWifiServices(context: Context) {
    if (isWIFIEnabled(context)) {
        return
    }

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

/**
 * this function checks whether GPS is enabled or not
 *
 * @param context the context to create the request in
 * @return true if the device has gps services enabled false otherwise
 */
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

/**
 * this function checks whether WIFI is enabled or not
 *
 * @param context the context to create the request in
 * @return true if the device has wifi services enabled false otherwise
 */
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
