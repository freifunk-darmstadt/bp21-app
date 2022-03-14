package de.freifunk.powa.model

/**
 * This Class stores all additional information retrieved from a scan.
 * The [id] of the scan, the actual [data] of the Scan and the [timestamp] at which the scan was made
 */
data class ScanInformation(val scandid: Int, val id: Int, val extendedID: Int,val data: ByteArray, val timestamp: String) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ScanInformation

        if (scandid != other.scandid) return false
        if (id != other.id) return false
        if (extendedID != other.extendedID) return false
        if (!data.contentEquals(other.data)) return false
        if (timestamp != other.timestamp) return false

        return true
    }

    override fun hashCode(): Int {
        var result = scandid
        result = 31 * result + id
        result = 31 * result + extendedID
        result = 31 * result + data.contentHashCode()
        result = 31 * result + timestamp.hashCode()
        return result
    }
}
