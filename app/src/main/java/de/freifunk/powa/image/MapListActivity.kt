package de.freifunk.powa.image

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView.OnItemClickListener
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import de.freifunk.powa.R
import de.freifunk.powa.adapter.MapListAdapter
import de.freifunk.powa.storeIntern.InternalStorageImage
import de.freifunk.powa.storeIntern.loadListOfInternalStorageImages
import kotlinx.coroutines.runBlocking

class MapListActivity : AppCompatActivity() {
    lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_of_maps)
        listView = findViewById(R.id.listView)
        var mapListContext = this

        var listOfMaps: List<InternalStorageImage>
        runBlocking {

            listOfMaps = loadListOfInternalStorageImages(mapListContext)
        }
        var mapListadapter = MapListAdapter(mapListContext, R.layout.list_row, listOfMaps,this)
        listView.adapter = mapListadapter
        listView.onItemClickListener = OnItemClickListener { _, _, pos, _ ->
            var storageImage = mapListadapter.getItem(pos)
            var intent = Intent(this, LoadImageActivity::class.java)
            var name = storageImage?.name

            intent.putExtra("mapName", name)
            startActivity(intent)
        }
    }
}
