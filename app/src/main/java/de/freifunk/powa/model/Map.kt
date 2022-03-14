package de.freifunk.powa.model

import android.graphics.Bitmap

/**
 * Stores data about a Map with the name [name] that is roughly located at [location].
 * Contains all scans made on this map in Variable [scans].
 * Also Contains a copy of the [image] saved for this map
 */
data class Map(
    val scans: List<WiFiScanObject>,
    val name: String,
    val location: String?,
    val image: Bitmap
)
