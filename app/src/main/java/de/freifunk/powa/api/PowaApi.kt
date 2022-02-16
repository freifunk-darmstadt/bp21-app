package de.freifunk.powa.api

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.net.wifi.ScanResult
import de.freifunk.powa.database.ScanDBHelper
import de.freifunk.powa.model.Map
import de.freifunk.powa.scan.filterData
import de.freifunk.powa.scan.scan
import de.freifunk.powa.storeIntern.loadListOfInternalStorageImages
import de.freifunk.powa.storeIntern.saveBitmapToInternalStorage
import kotlin.io.path.createTempDirectory

class PowaApi (context: Context){

    val maps = mutableListOf<Map>()
    val exporter = mutableListOf<ExportConsumer>()

    private val context: Context = context
    private val dbHelper = ScanDBHelper(context)

    init {
        loadListOfInternalStorageImages(context).forEach{
            val scanData = dbHelper.readScans(it.name)

            maps.add(Map(scanData ?: listOf(), it.name, TODO(), it.bitmap))
        }
    }

    fun getMapByName(mapName: String): Map? {
        return maps.firstOrNull { it.name == mapName }
    }

    fun addMap(mapToAdd: Map) {
        if (!dbHelper.insertMaps(mapToAdd.name)){
            throw IllegalArgumentException("Map ${mapToAdd.name} already exists")
        }
        mapToAdd.scans.forEach{
            dbHelper.insertScans(mapToAdd.name, it)
        }
        saveBitmapToInternalStorage(context, mapToAdd.name, mapToAdd.image)
        TODO("Add map coordinates")
    }

    fun runScan(onSuccess: (List<ScanResult>) -> Unit,
                onFailure: () -> Unit,
                filter: (List<ScanResult>) -> List<ScanResult> = (::filterData)){
        scan(context, onSuccess, onFailure,filter)
    }

    fun openMap(mapToOpen: Map){
        TODO("open map view for map")
    }

    fun registerExporter(exporter: ExportConsumer){
        this.exporter.add(exporter)
    }

    fun unRegisterExporter(exporterName: String){
        this.exporter.remove(exporter.filter { it.exportName == exporterName }.getOrNull(0))
    }

    fun selectExporter(): ExportConsumer {
        //TODO properly implement function
        return exporter[0]
    }

    fun exportData(maps : List<Map> = this.maps, consumer: ExportConsumer){
        val dir = createTempDirectory()
        val tempFile = kotlin.io.path.createTempFile(dir).toFile()

        consumer.export(tempFile, maps)

        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(tempFile))

        context.startActivity(Intent.createChooser(intent, "Daten Teilen"))
    }
}