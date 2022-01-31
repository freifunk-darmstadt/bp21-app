package de.freifunk.powa.image

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import de.freifunk.powa.R
import de.freifunk.powa.R.drawable
import de.freifunk.powa.adapter.MapListAdapter
import de.freifunk.powa.store_intern.InternalStorageImage
import de.freifunk.powa.store_intern.loadListOfInternalStorageImages
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

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
        listView.setOnItemClickListener(OnItemClickListener { list, v, pos, id ->
            var storageImage = mapListadapter.getItem(pos)
            var intent = Intent(this, LoadOldImageActivity::class.java)
            var name = storageImage?.name
            name = name?.removeSuffix(".jpg")
            intent.putExtra("mapName", name)
            startActivity(intent)
        })
    }
}