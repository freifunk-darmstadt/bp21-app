package de.freifunk.powa.scan

import android.content.Context
import android.net.wifi.ScanResult
import android.os.Build
import android.widget.Button
import android.widget.Toast
import androidx.core.view.isVisible
import de.freifunk.powa.database.ScanDBHelper
import de.freifunk.powa.image.LoadImageActivity
import de.freifunk.powa.model.WiFiScanObject
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class ScanActivity {
    var tableMapName: String // should be set before scan is invoked
    var timeStamp: String // time should be set before scan is invoked
    var xCoordinate: Float = 0f // should be set before scan is invoked
    var yCoordinate: Float = 0f // should be set before scan is invoked
    var scanContext: Context
    lateinit var scanBtn: Button
    constructor(context: Context, name: String, x: Float, y: Float) {
        scanContext = context
        tableMapName = name
        xCoordinate = x
        yCoordinate = y
        timeStamp = getTime()
    }

    /**
     * This Method saves the given results in the Sqlite-Database
     * @param results the List of the ScanResults. Results should be filtered first
     */

    fun onSuccess(results: List<ScanResult>) {
        results.forEach {
            var db = ScanDBHelper(scanContext)
            var scanResults = WiFiScanObject()
            scanResults.bssid = it.BSSID
            scanResults.ssid = it.SSID
            scanResults.venueName = it.venueName as String
            scanResults.operatorFriendlyName = it.operatorFriendlyName as String
            scanResults.level = it.level
            scanResults.frequency = it.frequency
            scanResults.channelWidth = it.channelWidth
            scanResults.centerFreq0 = it.centerFreq0
            scanResults.centerFreq1 = it.centerFreq1
            scanResults.capabilities = it.capabilities
            scanResults.timestamp = timeStamp
            scanResults.xCoordinate = xCoordinate
            scanResults.yCoordinate = yCoordinate
            db.insertScans(tableMapName, scanResults)
            if (Build.VERSION.SDK_INT >= 30)
            // only available in android API Level 30
                it.informationElements.forEach {
                    var bytes = ByteArray(it.bytes.capacity())
                    db.insertInformation(it.id, bytes)
                }
        }
        Toast.makeText(scanContext, "Scan war erfolgreich", Toast.LENGTH_SHORT).show()
        scanBtn.isVisible = true
    }

    /**
     * Gives out a Toast if the scan was not successful
     */
    fun onFailure() {
        Toast.makeText(scanContext, "Scan fehlgeschlagen", Toast.LENGTH_SHORT).show()
        scanBtn.isVisible = true
    }

    /**
     * @return Returns the current Timestamp in the format: yyyy-MM-dd HH:mm:ss
     */
    fun getTime(): String {
        val formatter = DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneOffset.UTC)
            .format(Instant.now())

        return formatter
    }

    /**
     * This Method starts the scan.
     * It depends on the Methods: onSuccess() and onFailure()
     */
    fun startScan() {
        scan(scanContext, ::onSuccess, ::onFailure)
    }
}
