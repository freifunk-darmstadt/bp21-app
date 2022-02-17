package de.freifunk.powa.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import de.freifunk.powa.model.WiFiScanObject
import java.util.LinkedList

val DATABASE_NAME = "ScansDB"
val DATABASE_VERSION = 1

class ScanDBHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    val MAP_TABLE_NAME = "maps"
    val COLUMN_MAP_NAME = "name"
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
    val INFORMATION_TABLE_ID = "id"
    val INFORMATION_TABLE_BYTES = "bytes"
    val INFORMATION_TABLE_PK = "pk"
    val COLUMN_INFORMTION_TABLE_TIMESTAMP = "timestamp"
    val SCAN_TABLE = "scans"
    val COLUMN_SCANS_MAP_NAME = "mapname"
    val COLUMN_SCANS_INFORMATION_ID = "informationid"
    override fun onCreate(db: SQLiteDatabase?) {

        db?.execSQL(
            "CREATE TABLE " + MAP_TABLE_NAME + " (" +
                COLUMN_MAP_NAME + " VARCHAR(256) PRIMARY KEY); "
        )
        db?.execSQL(
            "CREATE TABLE " + INFORMATION_TABLE + " (" +
                INFORMATION_TABLE_ID + " INTEGER ," +
                INFORMATION_TABLE_BYTES + " BLOB ," +
                COLUMN_INFORMTION_TABLE_TIMESTAMP + " TIMESTAMP NOT NULL," +
                INFORMATION_TABLE_PK + " AUTO_INCREMENT PRIMARY KEY," +
                "FOREIGN KEY (" + INFORMATION_TABLE_ID + ") " +
                "REFERENCES " + SCAN_TABLE + " (" + COLUMN_SCANS_INFORMATION_ID + "));"
        )
        db?.execSQL(
            " CREATE TABLE IF NOT EXISTS " + SCAN_TABLE + " (" +
                COLUMN_SCANS_MAP_NAME + " VARCHAR(256)," +
                COLUMN_SCANS_TIMESTAMP + " TIMESTAMP NOT NULL," + // timeformat is: "YYYY-MM-DD hh:mm:ss"
                COLUMN_SCANS_X + " FLOAT NOT NULL," +
                COLUMN_SCANS_Y + " FLOAT NOT NULL," +
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
                COLUMN_SCANS_INFORMATION_ID + " INTEGER ," +
                " CONSTRAINT " + PRIMARY_KEY_NAME + " " +
                " PRIMARY KEY ( " + COLUMN_SCANS_TIMESTAMP + "," + COLUMN_SCANS_BSSID + ")" +
                " FOREIGN KEY (" + COLUMN_SCANS_MAP_NAME + ") " +
                " REFERENCES " + MAP_TABLE_NAME + " (" + COLUMN_MAP_NAME + "));"
        )
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("Not yet implemented")
    }

    /**
     * This Method create a new Entry in the Map table
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
     * Only invoke this method after validation of scan-attributes
     * This method insert data with given map into the database
     */
    fun insertScans(scanTableName: String, scan: WiFiScanObject) {
        var db = this.writableDatabase
        var value = ContentValues()
        value.put(COLUMN_SCANS_MAP_NAME, scanTableName)
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
        value.put(COLUMN_SCANS_INFORMATION_ID, scan.informationID)
        db.insert(SCAN_TABLE, null, value)

        db.close()
    }

    /**
     *
     */
    fun insertInformation(id: Int, byte: ByteArray, timeStamp: String) {
        var db = this.writableDatabase
        var value = ContentValues()
        value.put(INFORMATION_TABLE_ID, id)
        value.put(INFORMATION_TABLE_BYTES, byte)
        value.put(COLUMN_INFORMTION_TABLE_TIMESTAMP, timeStamp)
        db.insert(INFORMATION_TABLE, null, value)
        db.close()
    }

    /**
     * Get a specific entry to given timestamp
     * If null returned then there are none entries for the timestamp
     * The entries are sorted in relation of index column
     */
    @SuppressLint("Range")
    fun readSpecificScan(scanTableName: String, timeStamp: String): List<WiFiScanObject>? {
        var db = this.writableDatabase
        var query = " SELECT * FROM " + SCAN_TABLE +
            " WHERE " + COLUMN_SCANS_TIMESTAMP + " = '" + timeStamp + "' " +
            "AND " + COLUMN_SCANS_MAP_NAME + " = '" + scanTableName + "';"
        var cursor = db.rawQuery(query, null)
        var scanLinkedList = LinkedList<WiFiScanObject>()
        if (cursor.count > 0) {
            cursor.moveToFirst()
            do {
                var scan = WiFiScanObject()

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
     * Scan the Database to the given text for the coordinates of the markers
     * @param mapName name of the Map
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
}
