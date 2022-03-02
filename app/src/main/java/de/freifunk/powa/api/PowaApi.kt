package de.freifunk.powa.api

import android.content.Context
import android.content.Intent
import android.net.wifi.ScanResult
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.content.FileProvider
import de.freifunk.powa.BuildConfig
import de.freifunk.powa.database.ScanDBHelper
import de.freifunk.powa.image.LoadImageActivity
import de.freifunk.powa.model.Map
import de.freifunk.powa.scan.filterData
import de.freifunk.powa.scan.scan
import de.freifunk.powa.storeIntern.loadListOfInternalStorageImages
import de.freifunk.powa.storeIntern.saveBitmapToInternalStorage
import java.io.File
import java.net.URLConnection

class PowaApi private constructor(context: Context){

    companion object{
        @Volatile
        internal var instance: PowaApi? = null

        fun getInstance(context: Context): PowaApi =
            instance ?: synchronized(this) {
                instance ?: PowaApi(context).also { instance = it }
            }
    }

    val maps = mutableListOf<Map>()
    val exporter = mutableListOf<ExportConsumer>()

    init{
        val dbHelper = ScanDBHelper(context)
        loadListOfInternalStorageImages(context).forEach{
            val scanData = dbHelper.readScans(it.name)

            maps.add(Map(scanData ?: listOf(), it.name, dbHelper.readMapLocation(it.name), it.bitmap))
        }
    }

    fun getMapByName(mapName: String): Map? {
        return maps.firstOrNull { it.name == mapName }
    }

    fun addMap(context: Context, mapToAdd: Map) : Boolean{
        val dbHelper = ScanDBHelper(context)
        if (!dbHelper.insertMaps(mapToAdd.name)){
            return false
        }
        mapToAdd.scans.forEach{
            dbHelper.insertScans(mapToAdd.name, it)
        }
        saveBitmapToInternalStorage(context, mapToAdd.name, mapToAdd.image)

        mapToAdd.location?.let { dbHelper.updateLocationInTableMap(mapToAdd.name, it) }
        maps.add(mapToAdd)
        return true
    }

    fun runScan(context: Context,
                onSuccess: (List<ScanResult>) -> Unit,
                onFailure: () -> Unit,
                filter: (List<ScanResult>) -> List<ScanResult> = (::filterData)){
        scan(context, onSuccess, onFailure,filter)
    }

    fun openMap(context: Context, mapToOpen: Map){
        var intent = Intent(context, LoadImageActivity::class.java)
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
                return Intent(context, ExportActivity::class.java)
            }

            override fun parseResult(resultCode: Int, intent: Intent?): Int {
                if (intent != null){
                    return intent.getIntExtra("${context.packageName}.ExportID", -1)
                }
                return -1
            }
        }) {
            if (it != -1){
                callback(exporter[it])
            }
        }.launch(Unit)
    }

    fun exportData(context: Context, consumer: ExportConsumer, maps : List<Map> = this.maps): File {
        val suffix = consumer.fileType
        val tempFile = File(context.filesDir, "exports" + File.separator + "exportedData.$suffix")

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

        val uri = FileProvider.getUriForFile(context.applicationContext,
            BuildConfig.APPLICATION_ID + ".provider", file)

        intent.putExtra(Intent.EXTRA_STREAM, uri)

        intent.setType(URLConnection.guessContentTypeFromName(file.getName()))

        context.startActivity(Intent.createChooser(intent, "Daten Teilen"))
    }
}