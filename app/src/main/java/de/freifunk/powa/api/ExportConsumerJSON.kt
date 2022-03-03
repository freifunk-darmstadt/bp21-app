package de.freifunk.powa.api

import de.freifunk.powa.model.Map
import de.freifunk.powa.model.ScanInformation
import de.freifunk.powa.model.WiFiScanObject
import java.io.File

class ExportConsumerJSON : ExportConsumer("JSON Exporter", "json", "no description") {

    // file is empty && file ends with ".json"
    override fun export(file: File, maps: List<Map>) {
        if (!file.exists())
            return
        file.writeText(getJsonFile(maps))
    }

    /**
     * @return the JSON-Object-Representation of a JSON-File, which encapsulates a list of MapObjects as String
     */
    private fun getJsonFile(maps: List<Map>): String {
        return "{\"maps\": [${maps.map { mapToString(it) }.joinToString(separator = ",") { it }}]}"
    }

    /**
     * @return the JSON-Object-Representation of a MapObject as String
     */
    private fun mapToString(map: Map): String {
        return "{" +
            "\"name\": \"${map.name}\"," +
            "\"location\": \"${map.name}\"," +
            "\"scans:\": ${getJSONArrayOfScans(map.scans)}" +
            "}"
    }

    /**
     * @return the JSON-List-Representation of a List of WifiScanObjects as String
     */
    private fun getJSONArrayOfScans(scans: List<WiFiScanObject>): String {
        return "[${scans.map { getWifiScanJSONObject(it) }.joinToString(separator = ",") { it }}]"
    }

    /**
     * @return the JSON-Object-Representation of a WifiScanObject as String
     */
    private fun getWifiScanJSONObject(wso: WiFiScanObject): String {
        return "{" +
            "\"bssid\": \"${wso.bssid}\"," +
            "\"ssid\": \"${wso.ssid}\"," +
            "\"capabilities\": \"${wso.capabilities}\"," +
            "\"centerFreq0\": ${wso.centerFreq0}," +
            "\"centerFreq1\": ${wso.centerFreq1}," +
            "\"channelWidth\": ${wso.channelWidth}," +
            "\"frequency\": ${wso.frequency}," +
            "\"level\": ${wso.level}," +
            "\"operatorFriendlyName\": \"${wso.operatorFriendlyName}\"," +
            "\"venueName\": \"${wso.venueName}\"," +
            "\"xCoordinate\": ${wso.xCoordinate}," +
            "\"yCoordinate\": ${wso.yCoordinate}," +
            "\"informationID\": ${wso.informationID}," +
            "\"scanInformation\": ${getJSONArrayOfScanInformationList(wso.scanInformation)}," +
            "\"timestamp\": \"${wso.timestamp}\"" +
            "}"
    }

    /**
     * @return the JSON-List-Representation of a List of ScanInformation-Objects as String
     */
    private fun getJSONArrayOfScanInformationList(sis: List<ScanInformation>): String {
        return "[${sis.map { getScanInformationJSONObject(it) }.joinToString(separator = ",") { it }}]"
    }

    /**
     * @return the JSON-Object-Representation of an ScanInformationObject as String
     */
    private fun getScanInformationJSONObject(si: ScanInformation?): String {
        if (si == null)
            return "null"

        val data = si.data.contentToString()
        return "{" +
            "\"id\": ${si.id}," +
            "\"data\": \"$data\"" +
            "}"
    }
}
