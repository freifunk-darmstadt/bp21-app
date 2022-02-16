package de.freifunk.powa.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import de.freifunk.powa.R
import de.freifunk.powa.api.ExportConsumer
import de.freifunk.powa.store_intern.InternalStorageImage

class ExporterListAdapter(context: Context, resource: Int, objects: List<ExportConsumer>) :
    ArrayAdapter<ExportConsumer>(context, resource, objects) {

    var listContext: Context = context
    var listResources: Int = resource
    private val listOfExporter: List<ExportConsumer> = objects

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater = LayoutInflater.from(listContext)
        val mapView = layoutInflater.inflate(listResources, parent, false)
        val nameView = mapView.findViewById<TextView>(R.id.exporterName)
        val descriptionView = mapView.findViewById<TextView>(R.id.exporterDescription)
        val name = listOfExporter.get(position).exportName
        val description = listOfExporter.get(position).description

        nameView.text = name
        descriptionView.text = description
        return mapView
    }
}