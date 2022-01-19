package de.freifunk.powa.model

class WiFiScanObject {
    lateinit var bssid: String
    lateinit var ssid: String
    lateinit var capabilities: String
    var centerFreq0: Int? = null
    var centerFreq1: Int? = null
    var channelWidth: Int? = null
    var frequency: Int? = null
    var level: Int? = null
    lateinit var operatorFriendlyName: String
    lateinit var venueName: String
    var xCoordinate: Float? = null
    var yCoordinate: Float? = null
    lateinit var timestamp: String // timestamp should have the format "YYYY-MM-DD hh:mm:ss"

    constructor(){

    }

}