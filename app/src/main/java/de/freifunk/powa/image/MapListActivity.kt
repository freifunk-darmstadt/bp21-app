package de.freifunk.powa.image

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView.OnItemClickListener
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import de.freifunk.powa.R
import de.freifunk.powa.adapter.MapListAdapter
import de.freifunk.powa.store_intern.InternalStorageImage
import de.freifunk.powa.store_intern.loadListOfInternalStorageImages
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
        var mapListadapter = MapListAdapter(mapListContext, R.layout.list_row, listOfMaps)
        listView.adapter = mapListadapter
        listView.setOnItemClickListener(
            OnItemClickListener { _, _, pos, _ ->
                var storageImage = mapListadapter.getItem(pos)
                var intent = Intent(this, LoadOldImageActivity::class.java)
                var name = storageImage?.name

                intent.putExtra("mapName", name)
                startActivity(intent)
            }
        )
    }
}
