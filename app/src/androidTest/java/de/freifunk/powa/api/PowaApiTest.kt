package de.freifunk.powa.api

import android.content.ClipDescription
import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import de.freifunk.powa.model.Map
import org.junit.After
import org.junit.Assert.*
import org.junit.Before

import org.junit.Test
import java.io.File
import kotlin.math.exp

class PowaApiTest {

    lateinit var thisContext: Context
    lateinit var api: PowaApi

    @Before
    fun setup() {
        thisContext = InstrumentationRegistry.getInstrumentation().targetContext
        api = PowaApi.getInstance(thisContext)
    }

    @After
    fun tearDown() {
        PowaApi.instance = null
    }

    @Test
    fun getMapByName() {
    }

    @Test
    fun addMap() {
    }

    @Test
    fun runScan() {
    }

    @Test
    fun openMap() {
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
    fun selectExporter() {
    }

    @Test
    fun exportData() {
        var exported = false
        val exporter = generateExporter("testName", "", "") { _, _ -> exported = true}

        api.exportData(thisContext, exporter)

        assertTrue("Exporter was not Called", exported)
    }

    @Test
    fun shareData() {
    }

    fun generateExporter(
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