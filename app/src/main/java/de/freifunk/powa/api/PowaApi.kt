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

class PowaApi private constructor(context: Context) {

    /**
     * Singleton object of the API
     */
    companion object {
        @Volatile
        internal var instance: PowaApi? = null

        /**
         * creates a new instance of the API iff no other instance exists
         */
        fun getInstance(context: Context): PowaApi =
            instance ?: synchronized(this) {
                instance ?: PowaApi(context).also { instance = it }
            }
    }

    /**
     * stores all currently loaded maps with all scan informations
     */
    val maps = mutableListOf<Map>()

    /**
     * stores all data exporters
     */
    val exporter = mutableListOf<ExportConsumer>()

    init {
        val dbHelper = ScanDBHelper(context)
        loadListOfInternalStorageImages(context).forEach {
            val scanData = dbHelper.readScans(it.name)

            maps.add(Map(scanData ?: listOf(), it.name, dbHelper.readMapLocation(it.name), it.bitmap))
        }
    }

    /**
     * @param mapName the name of the map to return
     * @return returns the map with the given name or null if no map with the name exists
     */
    fun getMapByName(mapName: String): Map? {
        return maps.firstOrNull { it.name == mapName }
    }

    /**
     * This function stores a map to the internal storage of the app.
     * All Data including scans will be saved
     *
     * @param context the context of the app that is used to add a new map
     * @param mapToAdd the map that should be added to the internal storage
     */
    fun addMap(context: Context, mapToAdd: Map): Boolean {
        val dbHelper = ScanDBHelper(context)
        if (!dbHelper.insertMaps(mapToAdd.name)) {
            return false
        }
        mapToAdd.scans.forEach {
            dbHelper.insertScans(mapToAdd.name, it)
        }
        saveBitmapToInternalStorage(context, mapToAdd.name, mapToAdd.image)

        mapToAdd.location?.let { dbHelper.updateLocationInTableMap(mapToAdd.name, it) }
        maps.add(mapToAdd)
        return true
    }

    /**
     * this function runs a scan using the given [filter].
     *
     * @param context the context of the app to display various dialogs while the scan runs
     * @param onSuccess a function that should be run on success of the wifi scan. A list of Scan results will be given as parameter
     * @param onFailure a function that should be called on failure of a wifi scan
     * @param filter a function used to filter the data passed to [onSuccess]
     */
    fun runScan(
        context: Context,
        onSuccess: (List<ScanResult>) -> Unit,
        onFailure: () -> Unit,
        filter: (List<ScanResult>) -> List<ScanResult> = (::filterData)
    ) {
        scan(context, onSuccess, onFailure, filter)
    }

    /**
     * opens the given map
     *
     * @param context the context from which this method is called
     * @param mapToOpen the map that should be displayed to the user
     */
    fun openMap(context: Context, mapToOpen: Map) {
        var intent = Intent(context, LoadImageActivity::class.java)
        var name = mapToOpen.name

        intent.putExtra("mapName", name)
        context.startActivity(intent)
    }

    /**
     * this function registered a exporter that should be displayed in the list of available exporters
     *
     * @param exporter the exporter to register
     */
    fun registerExporter(exporter: ExportConsumer) {
        this.exporter.add(exporter)
    }

    /**
     * this function unregistered a exporter that should no longer be displayed in the list of available exporters
     *
     * @param exporterName the name of the exporter that should no longer be stored in the list of available exporters
     */
    fun unRegisterExporter(exporterName: String) {
        this.exporter.remove(exporter.filter { it.exportName == exporterName }.getOrNull(0))
    }

    /**
     * This function launches a activity to select a exporter that should be used to export the stored scan data.
     *
     * @param context the context from which the export activity should be launched
     * @param callback the function that should be called as soon as the user selected a export consumer
     */
    fun selectExporter(context: ComponentActivity, callback: (ExportConsumer) -> Unit) {
        context.registerForActivityResult(object :
                ActivityResultContract<Unit, Int>() {
                //create intent to launch selector
                override fun createIntent(context: Context, input: Unit?): Intent {
                    return Intent(context, ExportActivity::class.java)
                }

                //retrieve information from launched activity
                override fun parseResult(resultCode: Int, intent: Intent?): Int {
                    if (intent != null) {
                        return intent.getIntExtra("${context.packageName}.ExportID", -1)
                    }
                    return -1
                }
            }) {
            //call callback if user selected a valid exporter
            if (it != 1) {
                callback(exporter[it])
            }
        }.launch(Unit)
    }

    /**
     * This function exports the given [maps] using the given [consumer] to a file which is then returned
     *
     * @param context the context this function is called from. Used to store the file.
     */
    fun exportData(context: Context, consumer: ExportConsumer, maps: List<Map> = this.maps): File {
        val suffix = consumer.fileType
        val tempFile = File(context.filesDir, "exports" + File.separator + "exportedData.$suffix")

        tempFile.mkdirs()

        if (tempFile.exists()) {
            tempFile.delete()
        }
        tempFile.createNewFile()

        consumer.export(tempFile, maps)

        tempFile.readLines().forEach {
            Toast.makeText(context, "Api Lines: $it", Toast.LENGTH_SHORT).show()
        }

        return tempFile
    }

    /**
     * this function creates a share file intent to share the given [file] using any app installed on the device.
     *
     * @param context the context from which the share action should be called
     */
    fun shareData(context: Context, file: File) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        val uri = FileProvider.getUriForFile(
            context.applicationContext,
            BuildConfig.APPLICATION_ID + ".provider", file
        )

        intent.putExtra(Intent.EXTRA_STREAM, uri)

        intent.setType(URLConnection.guessContentTypeFromName(file.getName()))

        context.startActivity(Intent.createChooser(intent, "Daten Teilen"))
    }
}
