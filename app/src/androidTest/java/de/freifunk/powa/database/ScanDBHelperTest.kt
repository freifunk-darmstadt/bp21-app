package de.freifunk.powa.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.os.Looper
import androidx.test.platform.app.InstrumentationRegistry
import de.freifunk.powa.TextScanner
import de.freifunk.powa.model.WiFiScanObject
import org.junit.After
import org.junit.Assert.* // ktlint-disable no-wildcard-imports
import org.junit.Before
import org.junit.Test

class ScanDBHelperTest {
    lateinit var thisContext: Context
    lateinit var dataBase: ScanDBHelper
    lateinit var scanner: TextScanner
    @Before
    fun setup() {
        scanner = TextScanner()
        thisContext = InstrumentationRegistry.getInstrumentation().targetContext
        dataBase = ScanDBHelper(thisContext)
        thisContext.deleteDatabase(dataBase.databaseName)
    }
    @After
    fun cleanup() {
        thisContext.deleteDatabase(dataBase.databaseName)
    }
    @Test
    fun onCreate() {
        var db = dataBase.writableDatabase
        assertEquals("ScansDB", dataBase.databaseName)

        assert(checkExistence(dataBase.MAP_TABLE_NAME, db))
        assert(checkExistence(dataBase.SCAN_TABLE, db))
        assert(checkExistence(dataBase.INFORMATION_TABLE, db))
    }

    @Test
    fun createNewTable() {
        var listOflines = scanner.scan(thisContext, "createNewTableTestCase.txt")
        Looper.prepare()
        listOflines.forEach {
            var line = scanner.decomposeString(it, ";")
            var mapName = line[0]
            var expectedValue = line[1].toBoolean()
            var actualValue = dataBase.insertMaps(mapName)
            var db = dataBase.writableDatabase
            var query = " SELECT * FROM " + dataBase.MAP_TABLE_NAME +
                " WHERE " + dataBase.COLUMN_MAP_NAME + "='" + mapName + "';"
            var cursor = db.rawQuery(query, null)

            assertEquals(1, cursor.count)
            assertEquals(expectedValue, actualValue)
        }
    }
    @Test
    fun insertMaps() {
        var listOflines = scanner.scan(thisContext, "gpsTestCase.txt")

        listOflines.forEach {
            var line = scanner.decomposeString(it, ";")
            var mapName = line[0]
            var expectedValue = line[1].toBoolean()
            var location = line[2]
            dataBase.insertMaps(mapName)
            var db = dataBase.writableDatabase
            var query = " SELECT * FROM " + dataBase.MAP_TABLE_NAME +
                " WHERE " + dataBase.COLUMN_MAP_NAME + "='" + mapName + "';"
            var cursor = db.rawQuery(query, null)
            cursor.moveToFirst()
            assertEquals(location, cursor.getString(cursor.getColumnIndex(dataBase.COLUMN_MAP_LOCATION)), cursor.getString(cursor.getColumnIndex(dataBase.COLUMN_MAP_LOCATION)))
        }
    }

    @Test
    fun insertInMapsTable() {
        var listOfLines = scanner.scan(thisContext, "insertDataTestCase.txt")
        var mapName = "TestTable"
        dataBase.insertMaps(mapName)

        listOfLines.forEach {
            var line = scanner.decomposeString(it, ";")
            var wifiScanner = WiFiScanObject()
            wifiScanner.timestamp = line[0]
            wifiScanner.xCoordinate = line[1].toFloat()
            wifiScanner.yCoordinate = line[2].toFloat()
            wifiScanner.bssid = line[3]
            wifiScanner.ssid = line[4]
            wifiScanner.capabilities = line[5]
            wifiScanner.centerFreq0 = line[6].toInt()
            wifiScanner.centerFreq1 = line[7].toInt()
            wifiScanner.channelWidth = line[8].toInt()
            wifiScanner.frequency = line[9].toInt()
            wifiScanner.level = line[10].toInt()
            wifiScanner.operatorFriendlyName = line[11]
            wifiScanner.venueName = line[12]
            wifiScanner.informationID = line[13].toInt()
            dataBase.insertScans(mapName, wifiScanner)
        }
        var db = dataBase.writableDatabase
        var query = " SELECT * FROM " + dataBase.SCAN_TABLE
        var cursor = db.rawQuery(query, null)
        assertEquals(listOfLines.size, cursor.count)
    }

    @Test
    fun readSpecificScan() {
        var listOfLines = scanner.scan(thisContext, "insertDataTestCase.txt")
        var mapName = "TestTable"
        dataBase.insertMaps(mapName)

        listOfLines.forEach {
            var line = scanner.decomposeString(it, ";")
            var wifiScanner = WiFiScanObject()
            wifiScanner.timestamp = line[0]
            wifiScanner.xCoordinate = line[1].toFloat()
            wifiScanner.yCoordinate = line[2].toFloat()
            wifiScanner.bssid = line[3]
            wifiScanner.ssid = line[4]
            wifiScanner.capabilities = line[5]
            wifiScanner.centerFreq0 = line[6].toInt()
            wifiScanner.centerFreq1 = line[7].toInt()
            wifiScanner.channelWidth = line[8].toInt()
            wifiScanner.frequency = line[9].toInt()
            wifiScanner.level = line[10].toInt()
            wifiScanner.operatorFriendlyName = line[11]
            wifiScanner.venueName = line[12]
            wifiScanner.informationID = line[13].toInt()
            dataBase.insertScans(mapName, wifiScanner)
        }
        var db = dataBase.writableDatabase
        var query = " SELECT * FROM " + dataBase.SCAN_TABLE
        var cursor = db.rawQuery(query, null)
        assertEquals(listOfLines.size, cursor.count)
        var timeStampScans = scanner.scan(thisContext, "readDataTestCase.txt")
        var line = scanner.decomposeString(timeStampScans[0], ";")
        var listOfScans = dataBase.readSpecificScan(mapName, line[0])
        var index = 0
        timeStampScans.forEach {
            var line = scanner.decomposeString(it, ";")
            assertEquals(line[0], listOfScans?.get(index)?.timestamp)
            assertEquals(line[1].toFloat(), listOfScans?.get(index)?.xCoordinate)
            assertEquals(line[2].toFloat(), listOfScans?.get(index)?.yCoordinate)
            assertEquals(line[3], listOfScans?.get(index)?.bssid)
            assertEquals(line[4], listOfScans?.get(index)?.ssid)
            assertEquals(line[5], listOfScans?.get(index)?.capabilities)
            assertEquals(line[6].toInt(), listOfScans?.get(index)?.centerFreq0)
            assertEquals(line[7].toInt(), listOfScans?.get(index)?.centerFreq1)
            assertEquals(line[8].toInt(), listOfScans?.get(index)?.channelWidth)
            assertEquals(line[9].toInt(), listOfScans?.get(index)?.frequency)
            assertEquals(line[10].toInt(), listOfScans?.get(index)?.level)
            assertEquals(line[11], listOfScans?.get(index)?.operatorFriendlyName)
            assertEquals(line[12], listOfScans?.get(index)?.venueName)
            assertEquals(line[13].toInt(), listOfScans?.get(index)?.informationID)
            index = index + 1
        }
    }
    @Test
    fun insertInformation() {
        var listOfLines = scanner.scan(thisContext, "insertInfTestCase.txt")
        var listOflines = scanner.scan(thisContext, "insertDataTestCase.txt")
        var mapName = "TestTable"
        dataBase.insertMaps(mapName)

        listOflines.forEach {
            var line = scanner.decomposeString(it, ";")
            var wifiScanner = WiFiScanObject()
            wifiScanner.timestamp = line[0]
            wifiScanner.xCoordinate = line[1].toFloat()
            wifiScanner.yCoordinate = line[2].toFloat()
            wifiScanner.bssid = line[3]
            wifiScanner.ssid = line[4]
            wifiScanner.capabilities = line[5]
            wifiScanner.centerFreq0 = line[6].toInt()
            wifiScanner.centerFreq1 = line[7].toInt()
            wifiScanner.channelWidth = line[8].toInt()
            wifiScanner.frequency = line[9].toInt()
            wifiScanner.level = line[10].toInt()
            wifiScanner.operatorFriendlyName = line[11]
            wifiScanner.venueName = line[12]
            wifiScanner.informationID = line[13].toInt()
            dataBase.insertScans(mapName, wifiScanner)
        }
        listOfLines.forEach {
            var line = scanner.decomposeString(it, ";")

            dataBase.insertInformation(line[0].toInt(), line[1].toByteArray(Charsets.UTF_8), line[2])
        }
        var db = dataBase.writableDatabase
        var query = " SELECT * FROM " + dataBase.INFORMATION_TABLE
        var cursor = db.rawQuery(query, null)
        assertEquals(listOfLines.size, cursor.count)
    }

    /**
     * Checks the existence of the table to a given Database
     */
    fun checkExistence(name: String, db: SQLiteDatabase): Boolean {
        val query =
            "select DISTINCT tbl_name from sqlite_master where tbl_name = '" + name + "'"

        db.rawQuery(query, null).use { cursor ->
            if (cursor != null) {
                if (cursor.getCount() == 1) {
                    return true
                }
            }
        }

        return false
    }
}
