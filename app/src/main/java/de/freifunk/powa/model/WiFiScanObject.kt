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
    var scanInformation: List<ScanInformation> = listOf()
    var informationID: Int? = null
    lateinit var timestamp: String // timestamp should have the format "YYYY-MM-DD hh:mm:ss.SSSSSS"
    var wifiStandard: Int? = null
    var longitude: Float ? = null
    var latitude: Float ? = null
    constructor() {
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WiFiScanObject

        if (bssid != other.bssid) return false
        if (ssid != other.ssid) return false
        if (capabilities != other.capabilities) return false
        if (centerFreq0 != other.centerFreq0) return false
        if (centerFreq1 != other.centerFreq1) return false
        if (channelWidth != other.channelWidth) return false
        if (frequency != other.frequency) return false
        if (level != other.level) return false
        if (operatorFriendlyName != other.operatorFriendlyName) return false
        if (venueName != other.venueName) return false
        if (xCoordinate != other.xCoordinate) return false
        if (yCoordinate != other.yCoordinate) return false
        if (scanInformation != other.scanInformation) return false
        if (informationID != other.informationID) return false
        if (timestamp != other.timestamp) return false
        if (wifiStandard != other.wifiStandard) return false
        if (longitude != other.longitude) return false
        if (latitude != other.latitude) return false
        return true
    }
    override fun hashCode(): Int {
        var result = bssid.hashCode()
        result = 31 * result + ssid.hashCode()
        result = 31 * result + capabilities.hashCode()
        result = 31 * result + (centerFreq0 ?: 0)
        result = 31 * result + (centerFreq1 ?: 0)
        result = 31 * result + (channelWidth ?: 0)
        result = 31 * result + (frequency ?: 0)
        result = 31 * result + (level ?: 0)
        result = 31 * result + operatorFriendlyName.hashCode()
        result = 31 * result + venueName.hashCode()
        result = 31 * result + (xCoordinate?.hashCode() ?: 0)
        result = 31 * result + (yCoordinate?.hashCode() ?: 0)
        result = 31 * result + scanInformation.hashCode()
        result = 31 * result + (informationID ?: 0)
        result = 31 * result + timestamp.hashCode()
        result = 31 * result + (wifiStandard ?: 0)
        result = 31 * result + (longitude?.hashCode() ?: 0)
        result = 31 * result + (latitude?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "WiFiScanObject(bssid='$bssid', ssid='$ssid', capabilities='$capabilities', centerFreq0=$centerFreq0, centerFreq1=$centerFreq1, channelWidth=$channelWidth, frequency=$frequency, level=$level, operatorFriendlyName='$operatorFriendlyName', venueName='$venueName', xCoordinate=$xCoordinate, yCoordinate=$yCoordinate, scanInformation=$scanInformation, informationID=$informationID, timestamp='$timestamp', wifiStandard=$wifiStandard, longitude=$longitude, latitude=$latitude)"
    }
}
