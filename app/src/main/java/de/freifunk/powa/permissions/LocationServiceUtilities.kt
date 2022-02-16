package de.freifunk.powa.permissions

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.lang.Exception
import androidx.core.content.ContextCompat.getSystemService




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

@SuppressLint("MissingPermission")
fun getGpsLocation(context: Context,locationListener: LocationListener, minTimeIntervalMS: Long = 5000, minDistanceM: Float = 10.0f) {
    if (!isGPSEnabled(context)){
        enableLocationServices(context)
    }

    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    checkPermissions(context, arrayOf("Manifest.permission.ACCESS_FINE_LOCATION"))
    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTimeIntervalMS, minDistanceM, locationListener)
}

const val LOCATION_STRING_SEPARATOR = "-"

fun locationToString(location: Location): String{
    return "${location.longitude}$LOCATION_STRING_SEPARATOR${location.latitude}"
}