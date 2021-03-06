package de.freifunk.powa.utils

import android.content.Context
import android.database.Cursor
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
    fun insertMaps() {
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
    fun insertScans() {
        var listOfLines = scanner.scan(thisContext, "insertDataTestCase.txt")
        var mapName = "TestTable"
        dataBase.insertMaps(mapName)
        insertScanData(listOfLines, mapName)

        var db = dataBase.writableDatabase
        var cursor = getCursor(db, dataBase.SCAN_TABLE, "*")
        assertEquals(listOfLines.size, cursor.count)
    }

    @Test
    fun readSpecificScan() {
        var listOfLines = scanner.scan(thisContext, "insertDataTestCase.txt")
        var mapName = "TestTable"
        dataBase.insertMaps(mapName)

        insertScanData(listOfLines, mapName)
        var db = dataBase.writableDatabase
        var cursor = getCursor(db, dataBase.SCAN_TABLE, "*")
        assertEquals(listOfLines.size, cursor.count)
        var timeStampScans = scanner.scan(thisContext, "readDataTestCase.txt")
        var line = scanner.decomposeString(timeStampScans[0], ";")
        var listOfScans = dataBase.readSpecificScan(mapName, line[0])
        assertEqualScanData(timeStampScans, listOfScans)
    }
    @Test
    fun insertInformation() {
        var listOfLinesOfElements = scanner.scan(thisContext, "insertInfTestCase.txt")
        var listOflinesOfScans = scanner.scan(thisContext, "insertDataTestCase.txt")
        var mapName = "TestTable"
        dataBase.insertMaps(mapName)
        insertScanData(listOflinesOfScans, mapName)
        listOfLinesOfElements.forEach {
            var line = scanner.decomposeString(it, ";")
            dataBase.insertInformation(line[0].toInt(), line[1].toInt(), line[2].toByteArray(Charsets.UTF_8), line[3])
        }
        var db = dataBase.writableDatabase
        var cursor = getCursor(db, dataBase.INFORMATION_TABLE, "*")
        assertEquals(listOfLinesOfElements.size, cursor.count)
    }
    @Test
    fun readScans() {
        var listOfLines = scanner.scan(thisContext, "insertDataTestCase.txt")
        var mapName = "TestTable"
        dataBase.insertMaps(mapName)
        insertScanData(listOfLines, mapName)
        var db = dataBase.writableDatabase
        var cursor = getCursor(db, dataBase.SCAN_TABLE, "*")
        assertEquals(listOfLines.size, cursor.count)
        var listOfTestData = scanner.scan(thisContext, "readDataTestCase.txt")
        var listOfScans = dataBase.readScans(mapName)
        assertEqualScanData(listOfTestData, listOfScans)
    }
    @Test
    fun readInformationElement() {
        var listOfLinesOfElements = scanner.scan(thisContext, "insertInfTestCase.txt")
        var listOflinesOfScans = scanner.scan(thisContext, "insertDataTestCase.txt")
        var mapName = "TestTable"
        dataBase.insertMaps(mapName)
        insertScanData(listOflinesOfScans, mapName)
        var db = dataBase.writableDatabase
        var cursor = getCursor(db, dataBase.SCAN_TABLE, "MAX(" + dataBase.COLUMN_SCANS_INFORMATION_ID + ")")
        cursor.moveToNext()
        var index = cursor.getColumnIndex("MAX(" + dataBase.COLUMN_SCANS_INFORMATION_ID + ")")
        var scanID = cursor.getInt(index)

        listOfLinesOfElements.forEach {
            var line = scanner.decomposeString(it, ";")
            dataBase.insertInformation(line[0].toInt(), line[1].toInt(), line[2].toByteArray(Charsets.UTF_8), line[3])
        }

        var listOfElementsFromDatabase = dataBase.readInformationElement(scanID)
        var i = 0
        listOfElementsFromDatabase.forEach {
            var lines = scanner.decomposeString(listOfLinesOfElements[i], ";")
            assertEquals(it.id, lines[0].toInt())
            assertEquals(it.extendedID, lines[1].toInt())
            assertEquals(String(it.data), lines[2])
            assertEquals(it.timestamp, lines[3])
            i++
        }
    }
    @Test
    fun updateLocationInTableMap() {
        var listOfLines = scanner.scan(thisContext, "gpsTestCase.txt")
        updateLocationFromData(listOfLines)
        var db = dataBase.writableDatabase
        var cursor = getCursor(db, dataBase.MAP_TABLE_NAME, "*")
        if (cursor.count > 0) {
            cursor.moveToFirst()
            var i = 0
            do {
                var lines = scanner.decomposeString(listOfLines[i], ";")
                var currentName = cursor.getString(cursor.getColumnIndex(dataBase.COLUMN_MAP_NAME))
                var currentLocation = cursor.getString(cursor.getColumnIndex(dataBase.COLUMN_MAP_LOCATION))
                assertEquals(currentName, lines[0])
                assertEquals(currentLocation, lines[1])
                i++
            } while (cursor.moveToNext())
        }
    }
    @Test
    fun readMapLocation() {
        var listOfLines = scanner.scan(thisContext, "gpsTestCase.txt")
        updateLocationFromData(listOfLines)
        listOfLines.forEach {
            var line = scanner.decomposeString(it, ";")
            var actualLocation = dataBase.readMapLocation(line[0])
            assertEquals(line[1], actualLocation)
        }
    }
    @Test
    fun readCoordinates() {
        var listOflines = scanner.scan(thisContext, "readCoordinatesTestCase.txt")
        var mapName = "TestTable"
        dataBase.insertMaps(mapName)
        insertScanData(listOflines, mapName)
        var db = dataBase.writableDatabase
        var cursor = getCursor(db, dataBase.SCAN_TABLE, "*")
        assertEquals(listOflines.size, cursor.count)
        var listOfPair = dataBase.readCoordinates(mapName)
        var i = 0
        listOflines.forEach {
            var lines = scanner.decomposeString(it, ";")
            assertEquals(lines[1].toFloat(), listOfPair!!.get(i).first)
            assertEquals(lines[2].toFloat(), listOfPair!!.get(i).second)
            i++
        }
    }
    @Test
    fun updateMapName() {
        var listOfLines = scanner.scan(thisContext, "insertDataTestCase.txt")
        var mapName = "TestTable"
        var newMap = "NewMap"
        dataBase.insertMaps(mapName)
        insertScanData(listOfLines, mapName)
        dataBase.updateMapName(mapName, newMap)
        var db = dataBase.writableDatabase
        var cursor = getCursor(db, dataBase.SCAN_TABLE, "*")
        var cursorMap = getCursor(db, dataBase.MAP_TABLE_NAME, "*")
        if (cursorMap.count > 0) {
            cursorMap.moveToFirst()
            var mapInMapTable = cursorMap.getString(cursorMap.getColumnIndex(dataBase.COLUMN_MAP_NAME))
            assertEquals(newMap, mapInMapTable)
        }
        if (cursor.count > 0) {
            cursor.moveToFirst()
            var i = 0
            do {
                var currentName = cursor.getString(cursor.getColumnIndex(dataBase.COLUMN_SCANS_MAP_NAME))
                assertEquals(currentName, newMap)
                i++
            } while (cursor.moveToNext())
        }
    }
    @Test
    fun deleteMap() {
        var listOfLines = scanner.scan(thisContext, "insertDataTestCase.txt")
        var mapName = "TestTable"
        dataBase.insertMaps(mapName)
        insertScanData(listOfLines, mapName)
        dataBase.deleteMap(mapName)
        var db = dataBase.writableDatabase
        var cursor = getCursor(db, dataBase.SCAN_TABLE, "*")
        var cursorMap = getCursor(db, dataBase.MAP_TABLE_NAME, "*")
        var cursorMapInformation = getCursor(db, dataBase.INFORMATION_TABLE, "*")
        assertEquals(0, cursor.count)
        assertEquals(0, cursorMap.count)
        assertEquals(0, cursorMapInformation.count)
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

    /**
     * This Method inserts the TestData into the Database with the given mapname
     * @param listOfLines List of the TestData
     * @param mapName name where the Scandata should be stored with
     */
    fun insertScanData(listOfLines: MutableList<String>, mapName: String) {
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
    }

    /**
     * This Method returns the Cursor to a query
     * @param db writeable Database
     * @param tableName name of the Table for the query
     * @param column specifying the column for the query
     * @return returns the Cursor
     */
    fun getCursor(db: SQLiteDatabase, tableName: String, column: String): Cursor {
        var query = " SELECT " + column + " FROM " + tableName
        var cursor = db.rawQuery(query, null)
        return cursor
    }

    /**
     * This Method updates the Database with the given TestData
     * @param listOfLines TestData which will be inserted into the database
     */
    fun updateLocationFromData(listOfLines: MutableList<String>) {
        listOfLines.forEach {
            var line = scanner.decomposeString(it, ";")
            var mapName = line[0]
            var location = line[1]
            dataBase.insertMaps(mapName)
            dataBase.updateLocationInTableMap(mapName, location)
        }
    }

    /**
     * This Methods checks if the actual Values are equal to the expected Values
     * @param listOflines List of expected Values from the TestData
     * @param listOfScans List of actual Values which can be read from the Database
     */
    fun assertEqualScanData(listOflines: MutableList<String>, listOfScans: List<WiFiScanObject>?) {
        var index = 0
        listOflines.forEach {
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
}
