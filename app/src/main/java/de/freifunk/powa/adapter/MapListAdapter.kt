package de.freifunk.powa.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import de.freifunk.powa.R
import de.freifunk.powa.store_intern.InternalStorageImage

class MapListAdapter : ArrayAdapter<InternalStorageImage> {
    var listContext: Context
    var listResources: Int = 0
    var listOfImages: List<InternalStorageImage> = listOf()
    constructor(context: Context, resource: Int, objects: List<InternalStorageImage>) : super(context,resource,objects) {
        listContext = context
        listResources = resource
        listOfImages = objects

    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var layoutInflater = LayoutInflater.from(listContext)
        var mapView = layoutInflater.inflate(listResources,parent, false)
        var imageView = mapView.findViewById<ImageView>(R.id.mapImageInList)
        var textView = mapView.findViewById<TextView>(R.id.mapNameInList)
        var name = listOfImages.get(position).name
        imageView.setImageBitmap(listOfImages.get(position).bitmap)
        name = name.removeSuffix(".jpg")

        textView.setText(name) // subsequence ".jpg" is excluded
        return mapView
    }

}