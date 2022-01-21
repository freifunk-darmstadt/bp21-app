package de.freifunk.powa.scan

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.util.Log

const val MAX_LEVEL = -1
const val MIN_LEVEL = -90

fun scan(
    context: Context,
    onSuccess: (List<ScanResult>) -> Unit,
    onFailure: () -> Unit,
    filter: (List<ScanResult>) -> List<ScanResult> = (::filterData)
) {

    // wifi manager
    val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager

    // scan receiver
    val wifiScanReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            if (intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false))
                onSuccess(filter(wifiManager.scanResults))

            // unregister scan receiver
            context.unregisterReceiver(this)
        }
    }

    // register scan receiver
    val intentFilter = IntentFilter()
    intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
    context.registerReceiver(wifiScanReceiver, intentFilter)

    // start scan
    val scanStarted = wifiManager.startScan()
    if (!scanStarted) {
        onFailure()

        // unregister scan receiver
        context.unregisterReceiver(wifiScanReceiver)
    }
}

fun handleScanResults(scanResults: List<ScanResult>) {
    val filteredScanResults = filterData(scanResults)

    for (result in filteredScanResults) {
        Log.d("ScanTest", "SSID: ${result.SSID}, Level: ${result.level}")
    }
}

fun handleScanFailure() {
    Log.d("ScanTest", "Scan failed successfully")
}

fun filterData(scanResults: List<ScanResult>): List<ScanResult> {

    return scanResults.filter {
        // only select scan results with an appropriate level
        it.level in MIN_LEVEL..MAX_LEVEL &&

        // do not select scan results with BSSID's 00..0 and ff..f
        it.BSSID != "00:00:00:00:00:00" && it.BSSID != "ff:ff:ff:ff:ff:ff"
    }
}
