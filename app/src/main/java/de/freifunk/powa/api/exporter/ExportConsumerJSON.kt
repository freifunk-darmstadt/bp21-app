package de.freifunk.powa.api.exporter

import de.freifunk.powa.api.ExportConsumer
import de.freifunk.powa.model.Map
import de.freifunk.powa.model.ScanInformation
import de.freifunk.powa.model.WiFiScanObject
import java.io.File

class ExportConsumerJSON : ExportConsumer("JSON Exporter", "json", "no description") {

    // file is empty && file ends with ".json"
    override fun export(file: File, maps: MutableList<Map>) {
        if (!file.exists())
            return
        getJsonFile(file, maps)
    }

    /**
     * @return the JSON-Object-Representation of a JSON-File, which encapsulates a list of MapObjects as String
     */
    private fun getJsonFile(file: File, maps: MutableList<Map>) {
        val it = maps.iterator()
        file.appendText("{\"maps\": [")
        while (it.hasNext()) {
            val map = it.next()
            it.remove()

            mapToString(file, map)
            if (it.hasNext()) {
                file.appendText(",")
            }
        }
        file.appendText("]}")
    }

    /**
     * @return the JSON-Object-Representation of a MapObject as String
     */
    private fun mapToString(file: File, map: Map) {
        file.appendText(
            "{" +
                "\"name\": \"${map.name}\"," +
                "\"location\": \"${map.name}\"," +
                "\"scans:\": "
        )
        getJSONArrayOfScans(file, map.scans.toMutableList())
        file.appendText("}")
    }

    /**
     * @return the JSON-List-Representation of a List of WifiScanObjects as String
     */
    private fun getJSONArrayOfScans(file: File, scans: MutableList<WiFiScanObject>) {
        file.appendText("[")
        val it = scans.iterator()
        while (it.hasNext()) {
            val scan = it.next()
            it.remove()

            getWifiScanJSONObject(file, scan)
            if (it.hasNext()) {
                file.appendText(",")
            }
        }
        file.appendText("]")
    }

    /**
     * @return the JSON-Object-Representation of a WifiScanObject as String
     */
    private fun getWifiScanJSONObject(file: File, wso: WiFiScanObject) {
        file.appendText(
            "{" +
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
                "\"scanInformation\": "
        )
        getJSONArrayOfScanInformationList(file, wso.scanInformation.toMutableList())
        file.appendText(
            "," +
                "\"timestamp\": \"${wso.timestamp}\"" +
                "}"
        )
    }

    /**
     * @return the JSON-List-Representation of a List of ScanInformation-Objects as String
     */
    private fun getJSONArrayOfScanInformationList(file: File, sis: MutableList<ScanInformation>) {
        file.appendText("[")
        val it = sis.iterator()
        while (it.hasNext()) {
            val inf = it.next()
            it.remove()

            getScanInformationJSONObject(file, inf)
            if (it.hasNext()) {
                file.appendText(",")
            }
        }
        file.appendText("]")
    }

    /**
     * @return the JSON-Object-Representation of an ScanInformationObject as String
     */
    private fun getScanInformationJSONObject(file: File, si: ScanInformation?) {
        if (si == null) {
            file.appendText("null")
            return
        }

        val data = si.data.contentToString()
        file.appendText(
            "{" +
                "\"id\": ${si.id}," +
                "\"data\": \"$data\"" +
                "}"
        )
    }
}
