package de.freifunk.powa.scan

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import de.freifunk.powa.R

class SingleScanActivity : AppCompatActivity() {

    //gui elements
    private lateinit var scanBtn: Button
    private lateinit var ssidTv: TextView

    //wifimanager
    private lateinit var wifiManager: WifiManager

    //scan receiver / broadcast listener
    private val wifiScanReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)
            if (success) {
                scanSuccess()
            } else {
                scanFailure()
            }
        }
    }

    //location permissions
    val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                Toast.makeText(this@SingleScanActivity, "precise location granted", Toast.LENGTH_SHORT).show()
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                Toast.makeText(this@SingleScanActivity, "approximate location granted", Toast.LENGTH_SHORT).show()
            } else -> {
            Toast.makeText(this@SingleScanActivity, "no location granted", Toast.LENGTH_SHORT).show()
        }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_scan)

        //wifiManager
        wifiManager = this@SingleScanActivity.getSystemService(Context.WIFI_SERVICE) as WifiManager

        //gui elements
        scanBtn = findViewById(R.id.scanBtn)
        ssidTv = findViewById(R.id.showSSIDTv)

        scanBtn.setOnClickListener {
            onClickScan()
        }

        //permissions
        isPermissionAlreadyGranted()
    }

    //scan methods

    private fun onClickScan(){
        val intentFilter = IntentFilter()
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        this@SingleScanActivity.registerReceiver(wifiScanReceiver, intentFilter)


        var success = wifiManager.startScan()
        if(!success){
            scanFailure()
        }
    }

    private fun scanSuccess(){
        ssidTv.text = "scan success"
        val results = wifiManager.scanResults
        for (res in results){
            Log.d("ScanResult", "ssid: ${res.SSID}, bssid: ${res.BSSID}, empty:${res.SSID.isEmpty()}")
        }

    }

    private fun scanFailure(){
        Toast.makeText(this@SingleScanActivity, "Scan failed", Toast.LENGTH_SHORT).show()
    }

    //permission methods

    private fun isPermissionAlreadyGranted(){
        locationPermissionRequest.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION))
    }


}