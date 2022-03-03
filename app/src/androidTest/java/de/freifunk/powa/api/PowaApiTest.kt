package de.freifunk.powa.api

import android.content.Context
import android.graphics.Bitmap
import androidx.core.graphics.set
import androidx.test.platform.app.InstrumentationRegistry
import de.freifunk.powa.database.ScanDBHelper
import de.freifunk.powa.model.Map
import de.freifunk.powa.model.ScanInformation
import de.freifunk.powa.model.WiFiScanObject
import org.junit.After
import org.junit.Assert.* // ktlint-disable no-wildcard-imports
import org.junit.Before
import org.junit.Test
import java.io.File
import kotlin.random.Random

class PowaApiTest {

    lateinit var thisContext: Context
    lateinit var api: PowaApi
    lateinit var dataBase: ScanDBHelper

    @Before
    fun setup() {
        thisContext = InstrumentationRegistry.getInstrumentation().targetContext
        api = PowaApi.getInstance(thisContext)
        dataBase = ScanDBHelper(thisContext)
        thisContext.deleteDatabase(dataBase.databaseName)
    }

    @After
    fun tearDown() {
        PowaApi.instance = null
        thisContext.deleteDatabase(dataBase.databaseName)
    }

    @Test
    fun getMapByName() {
        val mapNames = listOf("Map1", "Map2", "Map3", "Map4", "Map5", "Map6", "Map7")
        val maps = HashMap<String, Map>()
        for (name in mapNames) {
            val scans = listOf(generateScan(), generateScan(), generateScan(), generateScan())
            val map = generateMap(name, scans)
            api.addMap(thisContext, map)
            maps[name] = map
        }

        for (name in mapNames) {
            assertEquals(maps[name], api.getMapByName(name))
        }
    }

    @Test
    fun addMap() {
        val mapNames = listOf("addMap1", "addMap2", "addMap3", "addMap4", "addMap5", "addMap6", "addMap7")
        val maps = mutableListOf<Map>()

        assertEquals("Number of stored maps does not match expected", 0, api.maps.size)
        for (name in mapNames) {
            val scans = listOf(generateScan(name.hashCode()), generateScan(name.hashCode() + 1), generateScan(name.hashCode() + 2), generateScan(name.hashCode() + 3))
            val map = generateMap(name, scans)
            maps.add(map)
            assertTrue("Api should return true after successfully adding map", api.addMap(thisContext, map))
        }
        assertEquals("Number of stored maps does not match expected", mapNames.size, api.maps.size)
        assertTrue(maps.all { map -> api.maps.any { it == map } })

        assertFalse("Api should return false when trying to add existing map", api.addMap(thisContext, api.maps[0]))

        for (name in mapNames) {
            val storedScans = dataBase.readScans(name)
            // assertEquals(scans,storedScans)
        }
    }

    @Test
    fun registerExporter() {
        val exporter = generateExporter("", "", "") { _, _ -> }

        assertEquals("API should not have any Exporters registered", 0, api.exporter.size)

        api.registerExporter(exporter)

        assertEquals("API registered the wrong amount of Exporters", 1, api.exporter.size)
        assertEquals("Exporter is not registered correctly", exporter, api.exporter[0])
    }

    @Test
    fun unRegisterExporter() {
        val exporter = generateExporter("testName", "", "") { _, _ -> }

        api.registerExporter(exporter)
        api.unRegisterExporter(exporter.exportName)

        assertEquals("API did not unregister Exporter", 0, api.exporter.size)
    }

    @Test
    fun exportData() {
        var exported = false
        val exporter = generateExporter("testName", "", "") { _, _ -> exported = true }

        api.exportData(thisContext, exporter)

        assertTrue("Exporter was not Called", exported)
    }

    private fun generateMap(
        mapName: String,
        scans: List<WiFiScanObject>,
        location: String? = null,
        picture: Bitmap = createBitmap()
    ): Map {
        return Map(scans, mapName, location, picture)
    }

    private fun generateScan(id: Int = 0): WiFiScanObject {
        val scanObject = WiFiScanObject()

        scanObject.ssid = getRandomString(10, id)
        scanObject.bssid = getRandomString(16, (id * 10) % 8)
        scanObject.operatorFriendlyName = scanObject.ssid
        scanObject.capabilities = "$id"
        scanObject.timestamp = "${System.currentTimeMillis() + id}"
        scanObject.venueName = getRandomString(16, (id * 10) % 7)
        scanObject.scanInformation = listOf(generateInformation(id), generateInformation(id + 1), generateInformation(id + 2))

        return scanObject
    }

    private fun generateInformation(id: Int = 0): ScanInformation {
        return ScanInformation(id, Random(id).nextBytes(5), "${System.currentTimeMillis() + id}")
    }

    private fun getRandomString(length: Int, seed: Int): String {
        var string = ""
        for (i in 0..length) {
            string += Char(Random(seed + i).nextInt(97, 122))
        }
        return string
    }

    private fun createBitmap(): Bitmap {
        val bm = Bitmap.createBitmap(8, 8, Bitmap.Config.RGB_565)
        for (x in 0..7) {
            for (y in 0..7) {
                bm.set(x, y, x + y)
            }
        }
        return bm
    }

    private fun generateExporter(
        name: String,
        type: String,
        description: String,
        operation: (File, List<Map>) -> Unit
    ): ExportConsumer {
        return object : ExportConsumer("exporter", "txt", "text") {
            override fun export(file: File, maps: List<Map>) {
                operation(file, maps)
            }
        }
    }
}
