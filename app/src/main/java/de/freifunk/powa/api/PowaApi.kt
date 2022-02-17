package de.freifunk.powa.api

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.net.wifi.ScanResult
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import de.freifunk.powa.database.ScanDBHelper
import de.freifunk.powa.image.LoadOldImageActivity
import de.freifunk.powa.model.Map
import de.freifunk.powa.scan.filterData
import de.freifunk.powa.scan.scan
import de.freifunk.powa.storeIntern.loadListOfInternalStorageImages
import de.freifunk.powa.storeIntern.saveBitmapToInternalStorage
import java.io.File
import java.io.FileInputStream
import java.net.URLConnection

object PowaApi{

    val maps = mutableListOf<Map>()
    val exporter = mutableListOf<ExportConsumer>()
    val exportDir = "exports"

    var initialized = false

    fun initialize(context: Context) {
        val dbHelper = ScanDBHelper(context)
        loadListOfInternalStorageImages(context).forEach{
            val scanData = dbHelper.readScans(it.name)

            maps.add(Map(scanData ?: listOf(), it.name, TODO(), it.bitmap))
        }
        initialized = true
    }

    fun getMapByName(mapName: String): Map? {
        return maps.firstOrNull { it.name == mapName }
    }

    fun addMap(context: Context, mapToAdd: Map) {
        val dbHelper = ScanDBHelper(context)
        if (!dbHelper.insertMaps(mapToAdd.name)){
            throw IllegalArgumentException("Map ${mapToAdd.name} already exists")
        }
        mapToAdd.scans.forEach{
            dbHelper.insertScans(mapToAdd.name, it)
        }
        saveBitmapToInternalStorage(context, mapToAdd.name, mapToAdd.image)
        TODO("Add map coordinates")
    }

    fun runScan(context: Context,
                onSuccess: (List<ScanResult>) -> Unit,
                onFailure: () -> Unit,
                filter: (List<ScanResult>) -> List<ScanResult> = (::filterData)){
        scan(context, onSuccess, onFailure,filter)
    }

    fun openMap(context: Context, mapToOpen: Map){
        var intent = Intent(context, LoadOldImageActivity::class.java)
        var name = mapToOpen.name

        intent.putExtra("mapName", name)
        context.startActivity(intent)
    }

    fun registerExporter(exporter: ExportConsumer){
        this.exporter.add(exporter)
    }

    fun unRegisterExporter(exporterName: String){
        this.exporter.remove(exporter.filter { it.exportName == exporterName }.getOrNull(0))
    }

    fun selectExporter(context: ComponentActivity, callback: (ExportConsumer) -> Unit) {
        context.registerForActivityResult(object :
            ActivityResultContract<Unit, Int>() {
            override fun createIntent(context: Context, input: Unit?): Intent {
                var intent = Intent(context, ExportActivity::class.java)
                return intent
            }

            override fun parseResult(resultCode: Int, intent: Intent?): Int {
                if (intent != null){
                    return intent.getIntExtra("${context.packageName}.ExportID", -1)
                }
                return -1
            }
        }) {
            if (it != 1){
                callback(exporter[it])
            }
        }.launch(Unit)
    }

    fun exportData(context: Context, maps : List<Map> = this.maps, consumer: ExportConsumer): File {
        val suffix = "json"
        val tempFile = File(context.filesDir, exportDir + File.separator + "exportedData.$suffix")

        tempFile.mkdirs()

        if (tempFile.exists()) {
            tempFile.delete()
        }
        tempFile.createNewFile()

        consumer.export(tempFile, maps)

        tempFile.readLines().forEach{
            Toast.makeText(context, "Api Lines: $it", Toast.LENGTH_SHORT).show()
        }

        return tempFile
    }

    fun shareData(context: Context, file: File){
        val intent = Intent(Intent.ACTION_SEND)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        TODO("Use Content Provider")
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file))

        intent.setType(URLConnection.guessContentTypeFromName(file.getName()))

        context.startActivity(Intent.createChooser(intent, "Daten Teilen"))
    }
}