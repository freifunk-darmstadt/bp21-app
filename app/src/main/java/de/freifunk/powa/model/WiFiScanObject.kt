package de.freifunk.powa.model

import android.net.wifi.ScanResult

class WiFiScanObject (
    var bssid: String,
    var ssid: String,
    var capabilities: String,
    var centerFreq0: Int,
    var centerFreq1: Int,
    var channelWidth: Int,
    var frequency: Int,
    var level: Int,
    var operatorFriendlyName: String,
    var venueName: String,
    var xCoordinate: Float? = null,
    var yCoordinate: Float? = null,
    var scanInformation: List<ScanInformation>,
    var timestamp: String // timestamp should have the format "YYYY-MM-DD hh:mm:ss.SSSSSS"
){
    var informationID: Int? = null
}
