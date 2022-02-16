package de.freifunk.powa.api

import de.freifunk.powa.model.Map
import de.freifunk.powa.model.WiFiScanObject
import java.io.File

class ExportConsumerJSON : ExportConsumer("JSON Exporter", "no description") {

    //file is empty
    override fun export(file: File, maps: List<Map>) {
        if(!file.exists())
            return

    }

    private fun exportMap(map: Map): String {
        //TODO(finish exportMap())
       return """
            {
                "Name" : "${map.name}",
                "Location" : "${map.location}",
                "Image" : "Bitmap",
                "Scans" : [ScanObject.JSON]
            }
        """.trimIndent()
    }

    private fun exportWifiScanObject(wso: WiFiScanObject): String{
        //TODO(finish exportWifiScanObject())
        return """
            {
                "BSSID" : "${wso.bssid}",
                "Capabilities" : "${wso.capabilities}",
                "Center Freq 0" : ${wso.centerFreq0},
                "Center Freq 1" : ${wso.centerFreq1},
                "Channel Width" : ${wso.channelWidth},
                "Frequency" : ${wso.frequency},
                "Information ID" : ${wso.informationID},
                "Level" : ${wso.level},
                "Operator Friendly Name" : ${wso.operatorFriendlyName},
                "Scan Information" : "ScanInformation?",
                "SSID" : "${wso.ssid}",
                "Timestamp" : "${wso.timestamp}",
                "Venue Name" : "${wso.timestamp}",
                "X Coordinate" : ${wso.xCoordinate},
                "Y Coordinate" : ${wso.yCoordinate}
            }
        """.trimIndent()
    }
}