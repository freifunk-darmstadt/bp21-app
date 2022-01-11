package de.freifunk.powa.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import de.freifunk.powa.model.WiFiScanObject
import java.util.*

val DATABASE_NAME = "ScansDB"
val DATABASE_VERSION = 1
val DATABASE_FACTORY = null

class ScanDBHelper(context: Context):
    SQLiteOpenHelper(context, DATABASE_NAME, DATABASE_FACTORY, DATABASE_VERSION ) {

    val MAP_TABLE_NAME = "Maps"
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
    val COLUMN_SCANS_X = "xCoordinate"
    val COLUMN_SCANS_Y = "yCoordinate"
    val COLUMN_SCANS_TIMESTAMP = "timestamp"
    val PRIMARY_KEY_NAME = "Pk_Scan"

    override fun onCreate(db: SQLiteDatabase?) {

        db?.execSQL("CREATE TABLE " + MAP_TABLE_NAME + " (" +
                    COLUMN_MAP_NAME + " VARCHAR(256) PRIMARY KEY); ")
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("Not yet implemented")
    }

    /**
     * This Method create a new Entry in the Map table and creates a new Table if it not exist
     * Timestamp: TIMESTAMP Primary Key
     * xcoordinate: FLOAT
     * ycoordinate: FLOAT
     * bssid: VARCHAR(256) Primary Key
     * ssid: VARCHAR(256)
     * capabilities: TEXT
     * centerfreq0: INTEGER
     * centerfreq1: INTEGER
     * channel width: INTEGER
     * frequency: INTEGER
     * level: INTEGER
     * operator friendly name: VARCHAR(256)
     * venue name: VARCHAR(256)
     */
    fun createNewTable(name: String): Boolean{
        var db = this.writableDatabase
        var value = ContentValues()
        var query = "SELECT * FROM " + MAP_TABLE_NAME +
                    " WHERE " + COLUMN_MAP_NAME + " = '" + name + "' ;"
        var cursor = db.rawQuery(query, null)
        if(cursor.count == 0) {
            value.put(COLUMN_MAP_NAME, name)
            db.insert(MAP_TABLE_NAME, null, value)

            db?.execSQL(
                " CREATE TABLE IF NOT EXISTS" + name + " (" +
                        COLUMN_SCANS_TIMESTAMP + " TIMESTAMP NOT NULL," + //timeformat is: "YYYY-MM-DD hh:mm:ss"
                        COLUMN_SCANS_X + " FLOAT NOT NULL," +
                        COLUMN_SCANS_Y + " FLOAT NOT NULL," +
                        COLUMN_SCANS_BSSID + " VARCHAR(256) NOT NULL," +  // exact length is still unknown
                        COLUMN_SCANS_SSID + " VARCHAR(256) NOT NULL," +
                        COLUMN_SCANS_CAPABILITIES + " TEXT NOT NULL," +
                        COLUMN_SCANS_CENTERFREQ0 + " INTEGER NOT NULL," +
                        COLUMN_SCANS_CENTERFREQ1 + " INTEGER NOT NULL," +
                        COLUMN_SCANS_CHANNEL_WIDTH + " INTEGER NOT NULL," +
                        COLUMN_SCANS_FREQUENCY + " INTEGER NOT NULL," +
                        COLUMN_SCANS_LEVEL + " INTEGER NOT NULL," +
                        COLUMN_SCANS_OPERATOR_FRIENDLY_NAME + " VARCHAR(256) NOT NULL," +
                        COLUMN_SCANS_VENUE_NAME + " VARCHAR(256) NOT NULL," +
                        " CONSTRAINT" + PRIMARY_KEY_NAME + " " +
                        "PRIMARY KEY ( " + COLUMN_SCANS_TIMESTAMP + "," + COLUMN_SCANS_BSSID + "));"
            )

            db.close()
            return true
        }
        db.close()
        return false

    }


    /**
     * Only invoke this method after validation of scan-attributes
     */
    fun insertInMapsTable(tableName: String, scan: WiFiScanObject ){
        var db = this.writableDatabase
        var value = ContentValues()
        value.put(COLUMN_SCANS_BSSID, scan.bssid)
        value.put(COLUMN_SCANS_SSID, scan.ssid)
        value.put(COLUMN_SCANS_CAPABILITIES, scan.capabilities)
        value.put(COLUMN_SCANS_CENTERFREQ0, scan.centerFreq0)
        value.put(COLUMN_SCANS_CENTERFREQ1, scan.centerfreq1)
        value.put(COLUMN_SCANS_CHANNEL_WIDTH, scan.channelWidth)
        value.put(COLUMN_SCANS_FREQUENCY, scan.frequency)
        value.put(COLUMN_SCANS_LEVEL, scan.level)
        value.put(COLUMN_SCANS_OPERATOR_FRIENDLY_NAME, scan.operatorFriendlyName)
        value.put(COLUMN_SCANS_TIMESTAMP, scan.timestamp)
        value.put(COLUMN_SCANS_VENUE_NAME, scan.venueName)
        value.put(COLUMN_SCANS_X, scan.xCoordinate)
        value.put(COLUMN_SCANS_Y, scan.yCoordinate)
        db.insert(tableName, null, value)

        db.close()

    }

    /**
     * Get a specific entry to given timestamp
     * If null returned then there are none entries for the timestamp
     */

    @SuppressLint("Range")
    fun readSpecificScan(tableName: String, timeStamp: String): List<WiFiScanObject>?{
        var db = this.writableDatabase
        var query = " SELECT * FROM " + tableName +
                    " WHERE " + COLUMN_SCANS_TIMESTAMP + " = '" + timeStamp + "' ;"
        var cursor = db.rawQuery(query, null)
        var scanLinkedList = LinkedList<WiFiScanObject>()
        if(cursor.count > 0){
            cursor.moveToFirst()
            do {
                var scan = WiFiScanObject()

                scan.bssid = cursor.getString(cursor.getColumnIndex(COLUMN_SCANS_BSSID))
                scan.ssid = cursor.getString(cursor.getColumnIndex(COLUMN_SCANS_SSID))
                scan.capabilities = cursor.getString(cursor.getColumnIndex(COLUMN_SCANS_CAPABILITIES))
                scan.centerFreq0 = cursor.getInt(cursor.getColumnIndex(COLUMN_SCANS_CENTERFREQ0))
                scan.centerfreq1 = cursor.getInt(cursor.getColumnIndex(COLUMN_SCANS_CENTERFREQ1))
                scan.channelWidth = cursor.getInt(cursor.getColumnIndex(COLUMN_SCANS_CHANNEL_WIDTH))
                scan.frequency = cursor.getInt(cursor.getColumnIndex(COLUMN_SCANS_FREQUENCY))
                scan.level = cursor.getInt(cursor.getColumnIndex(COLUMN_SCANS_LEVEL))
                scan.operatorFriendlyName = cursor.getString(cursor.getColumnIndex(COLUMN_SCANS_OPERATOR_FRIENDLY_NAME))
                scan.timestamp = cursor.getString(cursor.getColumnIndex(COLUMN_SCANS_TIMESTAMP))
                scan.venueName = cursor.getString(cursor.getColumnIndex(COLUMN_SCANS_VENUE_NAME))
                scan.xCoordinate = cursor.getFloat(cursor.getColumnIndex(COLUMN_SCANS_X))
                scan.yCoordinate = cursor.getFloat(cursor.getColumnIndex(COLUMN_SCANS_Y))

                scanLinkedList.add(scan)
            }while(cursor.moveToNext())
        }
        else{
            db.close()
            return null
        }
        db.close()
        return scanLinkedList
    }


}