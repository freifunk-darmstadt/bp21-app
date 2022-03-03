package de.freifunk.powa.api

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import de.freifunk.powa.R
import de.freifunk.powa.adapter.ExporterListAdapter

class ExportActivity : AppCompatActivity() {
    lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_of_maps)
        listView = findViewById(R.id.listView)
        var mapListContext = this

        val api = PowaApi.getInstance(this)
        var listOfExporters = api.exporter

        var mapListadapter = ExporterListAdapter(mapListContext, R.layout.exporter_list_row, listOfExporters)
        listView.adapter = mapListadapter
        listView.setOnItemClickListener { _, _, pos, _ ->
            val result = Intent()
            result.putExtra("$packageName.ExportID", pos)

            setResult(Activity.RESULT_OK, result)
            finish()
        }
    }
}
