package de.freifunk.powa.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import de.freifunk.powa.model.ScanInformation
import de.freifunk.powa.model.WiFiScanObject
import java.util.LinkedList

val DATABASE_NAME = "ScansDB"
val DATABASE_VERSION = 1

class ScanDBHelper(val context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    val MAP_TABLE_NAME = "maps"
    val COLUMN_MAP_NAME = "name"
    val COLUMN_MAP_LOCATION = "location"
    val COLUMN_SCANS_BSSID = "bssid"
    val COLUMN_SCANS_SSID = "ssid"
    val COLUMN_SCANS_CAPABILITIES = "capabilities"
    val COLUMN_SCANS_CENTERFREQ0 = "centerfreq0"
    val COLUMN_SCANS_CENTERFREQ1 = "centerfreq1"
    val COLUMN_SCANS_CHANNEL_WIDTH = "channelwidth"
    val COLUMN_SCANS_FREQUENCY = "frequency"
    val COLUMN_SCANS_LEVEL = "level"
    val COLUMN_SCANS_OPERATOR_FRIENDLY_NAME = "operator"
    val COLUMN_SCANS_VENUE_NAME = "venueName"
    val COLUMN_SCANS_X = "xcoordinate"
    val COLUMN_SCANS_Y = "ycoordinate"
    val COLUMN_SCANS_TIMESTAMP = "timestamp"
    val PRIMARY_KEY_NAME = "pk_scan"
    val INFORMATION_TABLE = "informationtable"
    val COLUMN_INFORMTION_TABLE_TIMESTAMP = "timestamp"
    val COLUMN_INFORMATION_TABLE_ID = "id"
    val COLUMN_INFORMATION_TABLE_BYTES = "bytes"
    val COLUMN_INFORMATION_TABLE_PK = "pk"
    val COLUMN_INFORMATION_TABLE_SCAN_ID = "scanid"
    val COLUMN_INFORMATION_TABLE_EXT_ID = "extid"
    val SCAN_TABLE = "scans"
    val COLUMN_SCANS_MAP_NAME = "mapname"
    val COLUMN_SCANS_INFORMATION_ID = "informationid"
    val COLUMN_SCANS_WIFISTANDARD = "wifistandard"
    val COLUMN_SCANS_GPS_LONGITUDE = "longitude"
    val COLUMN_SCANS_GPS_LATITUDE = "latitude"
    override fun onCreate(db: SQLiteDatabase?) {
        // Create first table in which the mapnames are stored
        db?.execSQL(
            "CREATE TABLE " + MAP_TABLE_NAME + " (" +

                COLUMN_MAP_NAME + " VARCHAR(256) PRIMARY KEY, " +
                COLUMN_MAP_LOCATION + " VARCHAR(256)); "

        )
        // Create second table in which the scanresults to a map are stored
        db?.execSQL(
            " CREATE TABLE IF NOT EXISTS " + SCAN_TABLE + " (" +
                COLUMN_SCANS_MAP_NAME + " VARCHAR(256)," +
                COLUMN_SCANS_TIMESTAMP + " TIMESTAMP NOT NULL," + // timeformat is: "YYYY-MM-DD hh:mm:ss.SSSSSS"
                COLUMN_SCANS_X + " FLOAT ," +
                COLUMN_SCANS_Y + " FLOAT ," +
                COLUMN_SCANS_BSSID + " VARCHAR(256) NOT NULL," + // exact length is still unknown
                COLUMN_SCANS_SSID + " VARCHAR(256) NOT NULL," +
                COLUMN_SCANS_CAPABILITIES + " TEXT NOT NULL," +
                COLUMN_SCANS_CENTERFREQ0 + " INTEGER NOT NULL," +
                COLUMN_SCANS_CENTERFREQ1 + " INTEGER NOT NULL," +
                COLUMN_SCANS_CHANNEL_WIDTH + " INTEGER NOT NULL," +
                COLUMN_SCANS_FREQUENCY + " INTEGER NOT NULL," +
                COLUMN_SCANS_LEVEL + " INTEGER NOT NULL," +
                COLUMN_SCANS_OPERATOR_FRIENDLY_NAME + " VARCHAR(256) NOT NULL," +
                COLUMN_SCANS_VENUE_NAME + " VARCHAR(256) NOT NULL," +
                COLUMN_SCANS_WIFISTANDARD + " INTEGER, " +
                COLUMN_SCANS_GPS_LONGITUDE + " FLOAT ," +
                COLUMN_SCANS_GPS_LATITUDE + " FLOAT ," +
                COLUMN_SCANS_INFORMATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                " CONSTRAINT " + PRIMARY_KEY_NAME + " " +
                //   " PRIMARY KEY ( " + COLUMN_SCANS_TIMESTAMP + "," + COLUMN_SCANS_BSSID + ")" + Could also be a primary key
                " FOREIGN KEY (" + COLUMN_SCANS_MAP_NAME + ") " +
                " REFERENCES " + MAP_TABLE_NAME + " (" + COLUMN_MAP_NAME + ")" +
                " ON DELETE CASCADE " +
                " ON UPDATE CASCADE );"
        )
        // Create third table in which the Information Elements to a existing scanresult are stored
        db?.execSQL(
            "CREATE TABLE " + INFORMATION_TABLE + " (" +
                COLUMN_INFORMATION_TABLE_ID + " INTEGER ," +
                COLUMN_INFORMATION_TABLE_EXT_ID + " INTEGER ," +
                COLUMN_INFORMATION_TABLE_BYTES + " BLOB ," +
                COLUMN_INFORMTION_TABLE_TIMESTAMP + " TIMESTAMP NOT NULL," +
                COLUMN_INFORMATION_TABLE_SCAN_ID + " INTEGER NOT NULL," +
                COLUMN_INFORMATION_TABLE_PK + " INTEGER PRIMARY KEY AUTOINCREMENT ," +
                "FOREIGN KEY (" + COLUMN_INFORMATION_TABLE_SCAN_ID + ") " +
                "REFERENCES " + SCAN_TABLE + " (" + COLUMN_SCANS_INFORMATION_ID + ")" +
                " ON DELETE CASCADE " +
                " ON UPDATE CASCADE );"
        )
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("Not yet implemented")
    }

    /**
     * This Method create a new Entry in the Map table
     * @param name name of the map to be inserted
     * @return true if the mapname did not exist and was succuessfully stored
     */
    fun insertMaps(name: String): Boolean {
        var db = this.writableDatabase
        var value = ContentValues()
        var query = "SELECT * FROM " + MAP_TABLE_NAME +
            " WHERE " + COLUMN_MAP_NAME + " = '" + name + "' ;"
        var cursor = db.rawQuery(query, null)
        if (cursor.count == 0) {
            value.put(COLUMN_MAP_NAME, name)
            db.insert(MAP_TABLE_NAME, null, value)
            db.close()
            return true
        }
        db.close()
        return false
    }

    /**
     * This Method adds a location to a Map in the Map table
     * @param name name of the map to be updated
     * @param location new location for the map. Format: (longitude:latitude)
     * @return true if the location was successfully updated
     */
    fun updateLocationInTableMap(name: String, location: String): Boolean {
        var db = this.writableDatabase
        var value = ContentValues()
        var query = "SELECT * FROM " + MAP_TABLE_NAME +
            " WHERE " + COLUMN_MAP_NAME + " = '" + name + "' ;"
        var cursor = db.rawQuery(query, null)
        if (cursor.count != 0) {
            value.put(COLUMN_MAP_LOCATION, location)
            db.update(MAP_TABLE_NAME, value, "$COLUMN_MAP_NAME = ?", arrayOf(name))
            cursor.close()
            db.close()
            return true
        }
        cursor.close()
        db.close()
        return false
    }

    /**
     * Only invoke this method after validation of scan-attributes
     * This method insert data with given map into the database
     * @param mapName the name of the map where the scan was started
     * @param scan Scanresult of the scan which should be stored
     */
    fun insertScans(mapName: String, scan: WiFiScanObject) {
        var db = this.writableDatabase
        var value = ContentValues()
        value.put(COLUMN_SCANS_MAP_NAME, mapName)
        value.put(COLUMN_SCANS_BSSID, scan.bssid)
        value.put(COLUMN_SCANS_SSID, scan.ssid)
        value.put(COLUMN_SCANS_CAPABILITIES, scan.capabilities)
        value.put(COLUMN_SCANS_CENTERFREQ0, scan.centerFreq0)
        value.put(COLUMN_SCANS_CENTERFREQ1, scan.centerFreq1)
        value.put(COLUMN_SCANS_CHANNEL_WIDTH, scan.channelWidth)
        value.put(COLUMN_SCANS_FREQUENCY, scan.frequency)
        value.put(COLUMN_SCANS_LEVEL, scan.level)
        value.put(COLUMN_SCANS_OPERATOR_FRIENDLY_NAME, scan.operatorFriendlyName)
        value.put(COLUMN_SCANS_TIMESTAMP, scan.timestamp)
        value.put(COLUMN_SCANS_VENUE_NAME, scan.venueName)
        value.put(COLUMN_SCANS_X, scan.xCoordinate)
        value.put(COLUMN_SCANS_Y, scan.yCoordinate)
        value.put(COLUMN_SCANS_WIFISTANDARD, scan.wifiStandard)
        value.put(COLUMN_SCANS_GPS_LONGITUDE, scan.longitude)
        value.put(COLUMN_SCANS_GPS_LATITUDE, scan.latitude)
        // InformationID will be automatically inserted
        db.insert(SCAN_TABLE, null, value)

        db.close()
    }

    /**
     * This Method insert information elements into the database
     * @param id id of the Informationlement
     * @param byte the data of the Informationelement
     * @param timeStamp timestamp of the corresponding scanresult
     */
    @SuppressLint("Range")
    fun insertInformation(id: Int, extid: Int,byte: ByteArray, timeStamp: String) {
        var db = this.writableDatabase
        var value = ContentValues()
        var scanID: Int?
        var query = " SELECT MAX(" + COLUMN_SCANS_INFORMATION_ID + ")" +
            " FROM " + SCAN_TABLE + " ;"
        var cursor = db.rawQuery(query, null)
        cursor.moveToNext()
        var index = cursor.getColumnIndex("MAX(" + COLUMN_SCANS_INFORMATION_ID + ")")
        scanID = cursor.getInt(index)
        value.put(COLUMN_INFORMATION_TABLE_SCAN_ID, scanID)
        value.put(COLUMN_INFORMTION_TABLE_TIMESTAMP, timeStamp)
        value.put(COLUMN_INFORMATION_TABLE_ID, id)
        value.put(COLUMN_INFORMATION_TABLE_BYTES, byte)
        value.put(COLUMN_INFORMATION_TABLE_EXT_ID, extid)
        db.insert(INFORMATION_TABLE, null, value)
        db.close()
    }

    /**
     * Get a specific entry to given timestamp
     * If null returned then there are none entries for the timestamp
     * The entries are sorted in relation of index column
     * @param mapName mapname for a scan performed on it
     * @param timeStamp time when the scan was started
     * @return returns all the Scanresults for the given name and timestamp
     */
    @SuppressLint("Range")
    fun readSpecificScan(mapName: String, timeStamp: String): List<WiFiScanObject>? {
        val db = this.writableDatabase
        val query = " SELECT * FROM " + SCAN_TABLE +
            " WHERE " + COLUMN_SCANS_TIMESTAMP + " = '" + timeStamp + "' " +
            "AND " + COLUMN_SCANS_MAP_NAME + " = '" + mapName + "';"
        val cursor = db.rawQuery(query, null)
        val scanLinkedList = LinkedList<WiFiScanObject>()
        if (cursor.count > 0) {
            cursor.moveToFirst()
            do {
                val scan = WiFiScanObject()

                scan.bssid = cursor.getString(cursor.getColumnIndex(COLUMN_SCANS_BSSID))
                scan.ssid = cursor.getString(cursor.getColumnIndex(COLUMN_SCANS_SSID))
                scan.capabilities = cursor.getString(cursor.getColumnIndex(COLUMN_SCANS_CAPABILITIES))
                scan.centerFreq0 = cursor.getInt(cursor.getColumnIndex(COLUMN_SCANS_CENTERFREQ0))
                scan.centerFreq1 = cursor.getInt(cursor.getColumnIndex(COLUMN_SCANS_CENTERFREQ1))
                scan.channelWidth = cursor.getInt(cursor.getColumnIndex(COLUMN_SCANS_CHANNEL_WIDTH))
                scan.frequency = cursor.getInt(cursor.getColumnIndex(COLUMN_SCANS_FREQUENCY))
                scan.level = cursor.getInt(cursor.getColumnIndex(COLUMN_SCANS_LEVEL))
                scan.operatorFriendlyName = cursor.getString(cursor.getColumnIndex(COLUMN_SCANS_OPERATOR_FRIENDLY_NAME))
                scan.timestamp = cursor.getString(cursor.getColumnIndex(COLUMN_SCANS_TIMESTAMP))
                scan.venueName = cursor.getString(cursor.getColumnIndex(COLUMN_SCANS_VENUE_NAME))
                scan.xCoordinate = cursor.getFloat(cursor.getColumnIndex(COLUMN_SCANS_X))
                scan.yCoordinate = cursor.getFloat(cursor.getColumnIndex(COLUMN_SCANS_Y))
                scan.informationID = cursor.getInt(cursor.getColumnIndex(COLUMN_SCANS_INFORMATION_ID))
                scan.wifiStandard = cursor.getInt(cursor.getColumnIndex(COLUMN_SCANS_WIFISTANDARD))
                scan.longitude = cursor.getFloat(cursor.getColumnIndex(COLUMN_SCANS_GPS_LONGITUDE))
                scan.latitude = cursor.getFloat(cursor.getColumnIndex(COLUMN_SCANS_GPS_LATITUDE))
                scanLinkedList.add(scan)
            } while (cursor.moveToNext())
        } else {
            db.close()
            return null
        }
        db.close()
        return scanLinkedList
    }

    /**
     * Get all entries to given map
     * If null returned then there are none entries for the timestamp
     * The entries are sorted in relation of index column
     * @param mapName mapname for a scan performed on it
     * @return returns all the Scanresults for the given name
     */
    @SuppressLint("Range")
    fun readScans(mapName: String): List<WiFiScanObject>? {
        val db = this.writableDatabase
        val query = " SELECT * FROM " + SCAN_TABLE +
            " WHERE " + COLUMN_SCANS_MAP_NAME + " = '" + mapName + "';"
        val cursor = db.rawQuery(query, null)
        val scanLinkedList = LinkedList<WiFiScanObject>()
        if (cursor.count > 0) {
            cursor.moveToFirst()
            do {
                val scan = WiFiScanObject()

                scan.bssid = cursor.getString(cursor.getColumnIndex(COLUMN_SCANS_BSSID))
                scan.ssid = cursor.getString(cursor.getColumnIndex(COLUMN_SCANS_SSID))
                scan.capabilities = cursor.getString(cursor.getColumnIndex(COLUMN_SCANS_CAPABILITIES))
                scan.centerFreq0 = cursor.getInt(cursor.getColumnIndex(COLUMN_SCANS_CENTERFREQ0))
                scan.centerFreq1 = cursor.getInt(cursor.getColumnIndex(COLUMN_SCANS_CENTERFREQ1))
                scan.channelWidth = cursor.getInt(cursor.getColumnIndex(COLUMN_SCANS_CHANNEL_WIDTH))
                scan.frequency = cursor.getInt(cursor.getColumnIndex(COLUMN_SCANS_FREQUENCY))
                scan.level = cursor.getInt(cursor.getColumnIndex(COLUMN_SCANS_LEVEL))
                scan.operatorFriendlyName = cursor.getString(cursor.getColumnIndex(COLUMN_SCANS_OPERATOR_FRIENDLY_NAME))
                scan.timestamp = cursor.getString(cursor.getColumnIndex(COLUMN_SCANS_TIMESTAMP))
                scan.venueName = cursor.getString(cursor.getColumnIndex(COLUMN_SCANS_VENUE_NAME))
                scan.xCoordinate = cursor.getFloat(cursor.getColumnIndex(COLUMN_SCANS_X))
                scan.yCoordinate = cursor.getFloat(cursor.getColumnIndex(COLUMN_SCANS_Y))
                scan.informationID = cursor.getInt(cursor.getColumnIndex(COLUMN_SCANS_INFORMATION_ID))
                scan.wifiStandard = cursor.getInt(cursor.getColumnIndex(COLUMN_SCANS_WIFISTANDARD))
                scan.longitude = cursor.getFloat(cursor.getColumnIndex(COLUMN_SCANS_GPS_LONGITUDE))
                scan.latitude = cursor.getFloat(cursor.getColumnIndex(COLUMN_SCANS_GPS_LATITUDE))
                scanLinkedList.add(scan)
            } while (cursor.moveToNext())
        } else {
            db.close()
            return null
        }
        db.close()
        return scanLinkedList
    }

    /**
     * Read specific Informationelement
     * @param informationID ID of Scantable (COLUMN_SCANS_INFORMATION_ID) which the informationelement references
     * @return list of Informationelements
     */
    @SuppressLint("Range")
    fun readInformationElement(informationID: Int): List<ScanInformation> {
        val db = this.writableDatabase
        val query = " SELECT * FROM " + INFORMATION_TABLE +
            " WHERE " + COLUMN_INFORMATION_TABLE_SCAN_ID + " = '" + informationID + "';"
        val cursor = db.rawQuery(query, null)
        val rtn = mutableListOf<ScanInformation>()

        if (cursor.count > 0) {
            cursor.moveToFirst()
            do {
                rtn.add(
                    ScanInformation(
                        cursor.getInt(cursor.getColumnIndex(COLUMN_INFORMATION_TABLE_SCAN_ID)),
                        cursor.getInt(cursor.getColumnIndex(COLUMN_INFORMATION_TABLE_ID)),
                        cursor.getInt(cursor.getColumnIndex(COLUMN_INFORMATION_TABLE_EXT_ID)),
                        cursor.getBlob(cursor.getColumnIndex(COLUMN_INFORMATION_TABLE_BYTES)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_INFORMTION_TABLE_TIMESTAMP))
                    )
                )
            } while (cursor.moveToNext())
        }

        db.close()
        cursor.close()
        return rtn
    }

    /**
     * Get the location of a map
     * @param mapName name of the map to get the location
     * @return location of the map as a string
     */
    @SuppressLint("Range")
    fun readMapLocation(mapName: String): String? {
        val db = this.writableDatabase
        val query = " SELECT $COLUMN_MAP_LOCATION FROM " + MAP_TABLE_NAME +
            " WHERE " + COLUMN_MAP_NAME + " = '" + mapName + "';"
        val cursor = db.rawQuery(query, null)
        var rtn: String? = null

        if (cursor.count > 0) {
            cursor.moveToFirst()
            rtn = cursor.getString(cursor.getColumnIndex(COLUMN_MAP_LOCATION))
        }

        db.close()
        cursor.close()
        return rtn
    }

    /**
     * Scan the Database to the given text for the coordinates of the markers
     * @param mapName name of the Map to get the positions of the markers
     * @return list of pairs which represents (x,y)-coordinates of the map
     */
    @SuppressLint("Range")
    fun readCoordinates(mapName: String): MutableList<Pair<Float, Float>>? {
        var db = this.writableDatabase
        var query = " SELECT " + COLUMN_SCANS_X + "," + COLUMN_SCANS_Y + " FROM " + SCAN_TABLE +
            " WHERE " + COLUMN_SCANS_MAP_NAME + " = '" + mapName + "';"
        var cursor = db.rawQuery(query, null)
        var scanList = mutableListOf<Pair<Float, Float>>()
        if (cursor.count > 0) {
            cursor.moveToFirst()
            do {
                var pair = Pair(
                    cursor.getFloat(cursor.getColumnIndex(COLUMN_SCANS_X)),
                    cursor.getFloat(cursor.getColumnIndex(COLUMN_SCANS_Y))
                )

                scanList.add(pair)
            } while (cursor.moveToNext())
        } else {
            db.close()
            return null
        }
        db.close()
        return scanList
    }

    /**
     * This Method updates the MAP
     * @param oldName the old value to be updated
     * @param newName the new value that replace oldname
     * @return true if the name of the map was successfully updated
     */
    fun updateMapName(oldName: String, newName: String): Boolean {
        var db = this.writableDatabase
        // checking for existence of oldName and newName
        // oldName should exist while newName should not exist
        var query = "SELECT * FROM " + MAP_TABLE_NAME +
            " WHERE " + COLUMN_MAP_NAME + " = '" + newName + "' ;"
        var cursor = db.rawQuery(query, null)
        if (cursor.count > 0) {
            db.close()
            return false
        }
        var values = ContentValues()
        values.put(COLUMN_MAP_NAME, newName)
        var whereargs = arrayOf(oldName)
        db?.update(MAP_TABLE_NAME, values, COLUMN_MAP_NAME + "=?", whereargs)

        var scanValues = ContentValues()
        scanValues.put(COLUMN_SCANS_MAP_NAME, newName)
        db?.update(SCAN_TABLE, scanValues, COLUMN_SCANS_MAP_NAME + "=?", whereargs)
        db.close()
        return true
    }

    /**
     * This Method deletes a map from the database
     * @param mapName the name of the map to be deleted
     */
    @SuppressLint("Range")
    fun deleteMap(mapName: String) {
        var db = writableDatabase
        var query = "SELECT " + COLUMN_SCANS_INFORMATION_ID + " FROM " + SCAN_TABLE +
            " WHERE " + COLUMN_SCANS_MAP_NAME + " = '" + mapName + "' ;"
        var cursor = db.rawQuery(query, null)
        // deleting the information elements of the deleted scanresults
        if (cursor.count > 0) {
            cursor.moveToFirst()
            do {
                var scanResultID = cursor.getInt(cursor.getColumnIndex(COLUMN_SCANS_INFORMATION_ID))
                db?.delete(INFORMATION_TABLE, "$COLUMN_INFORMATION_TABLE_SCAN_ID=?", arrayOf(scanResultID.toString()))
            } while (cursor.moveToNext())
        }
        // deleting the scanresults of the deleted maps
        db?.delete(SCAN_TABLE, "$COLUMN_SCANS_MAP_NAME=?", arrayOf(mapName))
        // deleting the map to the given mapname
        db?.delete(MAP_TABLE_NAME, "$COLUMN_MAP_NAME=?", arrayOf(mapName))
        db.close()
    }
}
