package de.freifunk.powa.api

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import de.freifunk.powa.R
import de.freifunk.powa.adapter.ExporterListAdapter
import de.freifunk.powa.adapter.MapListAdapter
import de.freifunk.powa.image.LoadOldImageActivity
import de.freifunk.powa.model.Map
import de.freifunk.powa.store_intern.InternalStorageImage
import de.freifunk.powa.store_intern.loadListOfInternalStorageImages
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.FileOutputStream

class ExportActivity : AppCompatActivity() {
    lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_of_maps)
        listView = findViewById(R.id.listView)
        var mapListContext = this


        val api = PowaApi.getInstance(this)

        for (i in 0..5){
            api.registerExporter(object: ExportConsumer("test $i", "json","test description $i"){
                override fun export(file: File, maps: List<Map>) {
                    Toast.makeText(this@ExportActivity, "Writing $i to File", Toast.LENGTH_SHORT).show()

                    file.readLines().forEach{
                        Toast.makeText(this@ExportActivity, "ExportLines old: $it", Toast.LENGTH_SHORT).show()
                    }

                    val o = FileOutputStream(file)
                    o.write("$i".toByteArray())
                    o.flush()
                    o.close()

                    file.readLines().forEach{
                        Toast.makeText(this@ExportActivity, "ExportLines: $it", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }

        var listOfExporters = api.exporter

        var mapListadapter = ExporterListAdapter(mapListContext, R.layout.exporter_list_row, listOfExporters)
        listView.adapter = mapListadapter
        listView.setOnItemClickListener{ _, _, pos, _ ->
            val result = Intent()
            result.putExtra("${packageName}.ExportID", pos)

            setResult(Activity.RESULT_OK, result)
            finish()
        }
    }
}