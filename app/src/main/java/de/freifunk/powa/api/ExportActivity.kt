package de.freifunk.powa.api

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ListView
import de.freifunk.powa.R
import de.freifunk.powa.adapter.ExporterListAdapter
import de.freifunk.powa.adapter.MapListAdapter
import de.freifunk.powa.image.LoadOldImageActivity
import de.freifunk.powa.store_intern.InternalStorageImage
import de.freifunk.powa.store_intern.loadListOfInternalStorageImages
import kotlinx.coroutines.runBlocking

class ExportActivity : AppCompatActivity() {
    lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_of_maps)
        listView = findViewById(R.id.listView)
        var mapListContext = this

        val api = PowaApi(this)
        var listOfExporters = api.exporter

        var mapListadapter = ExporterListAdapter(mapListContext, R.layout.list_row, listOfExporters)
        listView.adapter = mapListadapter
        listView.setOnItemClickListener{ _, _, pos, _ ->
            api.exportData(api.maps, api.exporter[pos])
        }
    }
}