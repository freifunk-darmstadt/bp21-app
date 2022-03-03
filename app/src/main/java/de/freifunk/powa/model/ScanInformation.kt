package de.freifunk.powa.model

/**
 * This Class stores all additional information retrieved from a scan.
 * The [id] of the scan, the actual [data] of the Scan and the [timestamp] at which the scan was made
 */
data class ScanInformation(val id: Int, val data: ByteArray, val timestamp: String) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ScanInformation

        if (id != other.id) return false
        if (!data.contentEquals(other.data)) return false
        if (timestamp != other.timestamp) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + data.contentHashCode()
        result = 31 * result + timestamp.hashCode()
        return result
    }
}
