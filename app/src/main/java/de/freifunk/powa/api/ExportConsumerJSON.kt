package de.freifunk.powa.api

import de.freifunk.powa.model.Map
import de.freifunk.powa.model.ScanInformation
import de.freifunk.powa.model.WiFiScanObject
import java.io.File

class ExportConsumerJSON : ExportConsumer("JSON Exporter", "no description") {

    //file is empty
    override fun export(file: File, maps: List<Map>) {
        if(!file.exists())
            return
        file.writeText(mapToString(maps[0])) //TODO: export to JSON

    }

    private fun mapToString(map: Map): String{
        return "{" +
                "\"name\": \"${map.name}\"," +
                "\"location\": \"${map.name}\"," +
                "\"scans:\": ${listOfScansToString(map.scans)}" +
                "}"
    }

    private fun listOfScansToString(scans: List<WiFiScanObject>): String {
        var retString = "{"
        for (wso in scans){
            retString = "$retString${wiFiScanObjectToString(wso)},"
        }
        retString.removeSuffix(",")
        retString = "$retString}"
        return retString
    }

    private fun wiFiScanObjectToString(wso: WiFiScanObject): String {
        return "{"+
                "\"bssid\": \"${wso.bssid}\"," +
                "\"ssid\": \"${wso.ssid}\"," +
                "\"capabilities\": \"${wso.capabilities}\"," +
                "\"centerFreq0\": ${wso.centerFreq0}," +
                "\"centerFreq1\": ${wso.centerFreq1}," +
                "\"channelWidth\": ${wso.channelWidth}," +
                "\"frequency\": ${wso.frequency}," +
                "\"level\": ${wso.level}," +
                "\"operatorFriendlyName\": \"${wso.operatorFriendlyName}\"," +
                "\"venueName\": ${wso.venueName}," +
                "\"xCoordinate\": ${wso.xCoordinate}," +
                "\"yCoordinate\": ${wso.yCoordinate}," +
                "\"informationID\": ${wso.informationID}," +
                "\"scanInformation\": ${scanInformationToString(wso.scanInformation)}," +
                "\"timestamp\": \"${wso.timestamp}\"" +
                "}"
    }

    private fun scanInformationToString(si: ScanInformation?): String {
        if (si == null)
            return "null"

        val data = "ByteArray" //TODO: transform a ByteArray to a String
        return "{" +
                "\"id\": ${si.id}," +
                "\"data\": $data" +
        "}"
    }
}