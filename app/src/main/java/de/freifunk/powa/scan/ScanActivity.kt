package de.freifunk.powa.scan

import android.net.wifi.ScanResult
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import de.freifunk.powa.R
import de.freifunk.powa.database.ScanDBHelper
import de.freifunk.powa.model.WiFiScanObject

class ScanActivity : AppCompatActivity(){
    lateinit var tableMapName: String
    lateinit var timeStamp: String
    var xCoordinate: Float = 0f
    var yCoordinate: Float = 0f
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        var scanButton = findViewById<Button>(R.id.scanBtn)
        scanButton.setOnClickListener{
            scan(this, ::onSuccess,::onFailure )

        }
    }
    @RequiresApi(Build.VERSION_CODES.R)
    fun onSuccess(results: List<ScanResult>){
        results.forEach{
            var db = ScanDBHelper(this)
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

            it.informationElements.forEach{
                var bytes = ByteArray(it.bytes.capacity())
                db.insertInformation(it.id,bytes)
            }
            Toast.makeText(this,"Scan was a success", Toast.LENGTH_SHORT).show()
        }
    }
    fun onFailure(){
        Toast.makeText(this, "Scan failed", Toast.LENGTH_SHORT).show()
    }

}