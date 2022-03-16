package de.freifunk.powa.utils

import android.content.Context
import android.net.wifi.ScanResult
import android.os.Build
import android.widget.Button
import android.widget.Toast
import androidx.core.view.isVisible
import de.freifunk.powa.activity.view.SavedMarkerView
import de.freifunk.powa.model.ScanInformation
import de.freifunk.powa.model.WiFiScanObject
import de.freifunk.powa.scan.scan
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class ScanWrapper {
    private var tableMapName: String // should be set before scan is invoked
    private var timeStamp: String // time should be set before scan is invoked
    private var xCoordinate: Float? = 0f // should be set before scan is invoked
    private var yCoordinate: Float? = 0f // should be set before scan is invoked
    private var scanContext: Context
    private var multiScanCounter: Int = 0
    private var longitude: Float = 0f
    private var latitude: Float = 0f

    private val scans = mutableListOf<WiFiScanObject>()

    var scanBtn: Button?
    var view: SavedMarkerView?
    constructor(context: Context, name: String, x: Float?, y: Float?, btn: Button?, msCounter: Int, view: SavedMarkerView?) {
        scanContext = context
        tableMapName = name
        xCoordinate = x
        yCoordinate = y
        timeStamp = getTime()
        scanBtn = btn
        multiScanCounter = msCounter
        this.view = view
    }

    /**
     * This Method saves the given results in the Sqlite-Database
     * @param results the List of the ScanResults. Results should be filtered first
     */
    fun onSuccess(results: List<ScanResult>) {
        results.forEach {
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

            if (Build.VERSION.SDK_INT >= 30) {
                // only available in android API Level 30
                it.informationElements.forEach {
                    var bytes = ByteArray(it.bytes.capacity())
                    it.bytes.get(bytes)

                    val informations = mutableListOf<ScanInformation>()
                    informations.add(ScanInformation(-1, it.id, it.idExt, bytes, timeStamp))
                }
            }

            if (longitude == 0f && latitude == 0f){
                scanResults.longitude = longitude
                scanResults.latitude = latitude

                insertDataBase(scanResults)
                for (inf in scanResults.scanInformation){
                    insertInformation(inf)
                }
            } else {
                scans.add(scanResults)
            }

            if (Build.VERSION.SDK_INT >= 30) // only available in android API Level 30
                scanResults.wifiStandard = it.wifiStandard // this order is important because of autoincrement in Scantable
        }
        multiScanCounter--
        if (multiScanCounter > 0) {
            startScan()
        } else if (multiScanCounter == 0) {
            if (scanBtn != null) {
                scanBtn!!.isVisible = true
                if (view != null) {
                    val db = ScanDBHelper(this.scanContext)
                    val crdOfMarkers = db.readCoordinates(this.tableMapName)

                    if (crdOfMarkers != null) {
                        view!!.coordinates = crdOfMarkers
                    }
                    view!!.invalidate()
                }
            }
        }
        Toast.makeText(scanContext, "Scan war erfolgreich", Toast.LENGTH_SHORT).show()
    }

    fun updateLocation(longitude: Float = 0f, latitude: Float = 0f){
        this.longitude = longitude
        this.latitude = latitude

        for (scan in scans){
            scan.latitude = latitude
            scan.longitude = longitude
            insertDataBase(scan)
            for (inf in scan.scanInformation){
                insertInformation(inf)
            }
        }
        scans.clear()
    }

    private fun insertDataBase(scanResult: WiFiScanObject){
        var db = ScanDBHelper(scanContext)
        db.insertScans(tableMapName, scanResult)
    }

    private fun insertInformation(scanResult: ScanInformation){
        var db = ScanDBHelper(scanContext)
        db.insertInformation(scanResult.id, scanResult.extendedID, scanResult.data, scanResult.timestamp)
    }

    /**
     * Gives out a Toast if the scan was not successful
     */
    fun onFailure() {
        Toast.makeText(scanContext, "Scan fehlgeschlagen", Toast.LENGTH_SHORT).show()
        scanBtn?.isVisible = true
        this.view?.invalidate()
    }

    /**
     * @return Returns the current Timestamp in the format: yyyy-MM-dd HH:mm:ss
     */
    fun getTime(): String {
        val formatter = DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")
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
