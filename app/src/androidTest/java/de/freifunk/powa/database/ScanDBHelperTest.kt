package de.freifunk.powa.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.test.platform.app.InstrumentationRegistry
import de.freifunk.powa.TextScanner
import de.freifunk.powa.model.WiFiScanObject
import org.junit.After
import org.junit.Assert.*
import org.junit.Before

import org.junit.Test

class ScanDBHelperTest {
    lateinit var thisContext: Context
    lateinit var dataBase: ScanDBHelper
    lateinit var scanner: TextScanner
    @Before
    fun setup(){
        scanner = TextScanner()
        thisContext = InstrumentationRegistry.getInstrumentation().targetContext
        dataBase = ScanDBHelper(thisContext)
        thisContext.deleteDatabase(dataBase.databaseName)
    }
    @After
    fun cleanup(){
        thisContext.deleteDatabase(dataBase.databaseName)
    }
    @Test
    fun onCreate() {
        var db = dataBase.writableDatabase
        assertEquals("ScansDB", dataBase.databaseName)
        var res = checkExistence(dataBase.MAP_TABLE_NAME, db)
        assert(res)

    }

    @Test
    fun createNewTable() {
        var listOflines = scanner.scan(thisContext, "createNewTableTestCase.txt")
        listOflines.forEach{
            var line = scanner.decomposeString(it,";")
            var mapName = line[0]
            var expectedValue = line[1].toBoolean()
            var actualValue = dataBase.createNewTable(mapName)
            var db = dataBase.writableDatabase
            var res = checkExistence(mapName, db)
            var query = " SELECT * FROM " + dataBase.MAP_TABLE_NAME +
                        " WHERE " + dataBase.COLUMN_MAP_NAME + "='" + mapName + "';"
            var cursor = db.rawQuery(query, null)

            assertEquals(1, cursor.count)
            assert(res,{mapName + " does not exists"})
            assertEquals(expectedValue,actualValue )
        }

    }

    @Test
    fun insertInMapsTable() {
        var listOfLines = scanner.scan(thisContext, "insertDataTestCase.txt")
        var tableName = "TestTable"
        dataBase.createNewTable(tableName)

        listOfLines.forEach{
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
            dataBase.insertInMapsTable(tableName, wifiScanner)
        }
        var db = dataBase.writableDatabase
        var query = " SELECT * FROM " + tableName
        var cursor = db.rawQuery(query, null)
        assertEquals(listOfLines.size, cursor.count)
    }

    @Test
    fun readSpecificScan() {
        var listOfLines = scanner.scan(thisContext, "insertDataTestCase.txt")
        var tableName = "TestTable"
        dataBase.createNewTable(tableName)

        listOfLines.forEach{
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
            dataBase.insertInMapsTable(tableName, wifiScanner)
        }
        var db = dataBase.writableDatabase
        var query = " SELECT * FROM " + tableName
        var cursor = db.rawQuery(query, null)
        assertEquals(listOfLines.size, cursor.count)
        var timeStampScans = scanner.scan(thisContext, "readDataTestCase.txt")
        var line = scanner.decomposeString(timeStampScans[0], ";")
        var listOfScans = dataBase.readSpecificScan(tableName, line[0])
        var index = 0
        timeStampScans.forEach{
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
            index = index + 1
        }


    }

    /**
     * Checks the existence of the table to a given Database
     */
    fun checkExistence(name: String, db: SQLiteDatabase): Boolean{
        val query =
            "select DISTINCT tbl_name from sqlite_master where tbl_name = '" + name + "'"

        db.rawQuery(query, null).use { cursor ->
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    return true
                }
            }
        }

        return false

    }
}