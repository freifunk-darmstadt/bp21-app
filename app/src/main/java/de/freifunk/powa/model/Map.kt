package de.freifunk.powa.model

import android.graphics.Bitmap
import java.nio.ByteBuffer
import java.util.Arrays

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
){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Map

        if (scans != other.scans) return false
        if (name != other.name) return false
        if (location != other.location) return false
        if (bitmapEquals(image, other.image)) return false

        return true
    }

    override fun hashCode(): Int {
        val buffer1: ByteBuffer = ByteBuffer.allocate(image.height * image.rowBytes)
        image.copyPixelsToBuffer(buffer1)

        var result = scans.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + (location?.hashCode() ?: 0)
        result = 31 * result + buffer1.hashCode()
        return result
    }

    private fun bitmapEquals(bitmap1: Bitmap, bitmap2: Bitmap) : Boolean{
        val buffer1: ByteBuffer = ByteBuffer.allocate(bitmap1.height * bitmap1.rowBytes)
        bitmap1.copyPixelsToBuffer(buffer1)
        val buffer2: ByteBuffer = ByteBuffer.allocate(bitmap2.height * bitmap2.rowBytes)
        bitmap2.copyPixelsToBuffer(buffer2)

        return Arrays.equals(buffer1.array(), buffer2.array())
    }
}
