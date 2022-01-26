package de.freifunk.powa.image

import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import de.freifunk.powa.R
import de.freifunk.powa.R.drawable
import de.freifunk.powa.adapter.MapListAdapter
import de.freifunk.powa.store_intern.InternalStorageImage
import de.freifunk.powa.store_intern.loadListOfInternalStorageImages
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MapListActivity: AppCompatActivity() {
    lateinit var listView: ListView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_of_maps)
        listView = findViewById(R.id.listView)
        var mapListContext = this

        var listOfMaps: List<InternalStorageImage> = listOf()
        runBlocking {

            listOfMaps = loadListOfInternalStorageImages(mapListContext)

        }
        var mapListadapter = MapListAdapter(mapListContext,R.layout.list_row, listOfMaps)
        listView.adapter = mapListadapter

    }
}