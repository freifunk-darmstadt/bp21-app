package de.freifunk.powa.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.util.*

val DATABASE_NAME = "ScansDB"
val DATABASE_VERSION = 1
val DATABASE_FACTORY = null

class ScanDBHelper(context: Context):
    SQLiteOpenHelper(context, DATABASE_NAME, DATABASE_FACTORY, DATABASE_VERSION ) {

    val MAP_TABLE_NAME = "Maps"
    val COLUMN_NAME = "name"

    val COLUMN_SCANS_BSSID = "bssid"
    val COLUMN_SCANS_SSID = "ssid"
    val COLUMN_SCANS_CAPABILITIES = "capabilities"
    val COLUMN_SCANS_CENTERFREQ0 = "centerfreq0"
    val COLUMN_SCANS_CENTERFREQ1 = "centerfreq1"
    val COLUMN_SCANS_CHANNEL_WIDTH = "channelwidth"
    val COLUMN_SCANS_FREQUENCY = "frequency"
    val COLUMN_SCANS_LEVEL = "level"
    val COLUMN_SCANS_OPERATOR_FRIENDLY_NAME = "operator"
    val COLUMN_SCANS_TIMESTAMP = "timestamp"
    val COLUMN_SCANS_VENUE_NAME = "venueName"

    var dataBase: SQLiteDatabase? = null
    override fun onCreate(db: SQLiteDatabase?) {
        dataBase = db
        db?.execSQL("CREATE TABLE " + MAP_TABLE_NAME + " (" +
                    COLUMN_NAME + " VARCHAR(256) PRIMARY KEY); ")
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("Not yet implemented")
    }

    fun createNewTable(name: String){
        dataBase?.execSQL(" CREATE TABLE IF NOT EXISTS" + name + " (" +
                 COLUMN_SCANS_BSSID + " " +
                 COLUMN_SCANS_SSID + " " +
                 COLUMN_SCANS_CAPABILITIES + " " +
                 COLUMN_SCANS_CENTERFREQ0 + " " +
                 COLUMN_SCANS_CENTERFREQ1 + " " +
                 COLUMN_SCANS_CHANNEL_WIDTH + " " +
                 COLUMN_SCANS_FREQUENCY + " " +
                 COLUMN_SCANS_LEVEL + " " +
                 COLUMN_SCANS_OPERATOR_FRIENDLY_NAME + " " +
                 COLUMN_SCANS_TIMESTAMP + " " +
                 COLUMN_SCANS_VENUE_NAME + " );"    )
    }

    fun insertInMapsTable(){

    }

}