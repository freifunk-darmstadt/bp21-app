package de.freifunk.powa.scan

import android.content.Context
import android.net.wifi.ScanResult
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import de.freifunk.powa.R
import de.freifunk.powa.database.ScanDBHelper
import de.freifunk.powa.model.WiFiScanObject
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class ScanActivity {
    lateinit var tableMapName: String // should be set before scan is invoked
    lateinit var timeStamp: String // time should be set before scan is invoked
    var xCoordinate: Float = 0f // should be set before scan is invoked
    var yCoordinate: Float = 0f // should be set before scan is invoked
    lateinit var scanContext: Context
    constructor(context: Context, name: String, x: Float, y: Float){
        scanContext = context
        tableMapName = name
        xCoordinate = x
        yCoordinate = y
        timeStamp = getTime()
    }
    @RequiresApi(Build.VERSION_CODES.R)
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
            if(false)
            it.informationElements.forEach {
                var bytes = ByteArray(it.bytes.capacity())
                db.insertInformation(it.id, bytes)
            }
            Toast.makeText(scanContext, "Scan was a success", Toast.LENGTH_SHORT).show()
        }
    }
    fun onFailure() {
        Toast.makeText(scanContext, "Scan failed", Toast.LENGTH_SHORT).show()
    }

    fun getTime(): String {
        val formatter = DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneOffset.UTC)
            .format(Instant.now())

        return formatter
    }
    fun startScan(){
        scan(scanContext,::onSuccess, ::onFailure)
    }
}
