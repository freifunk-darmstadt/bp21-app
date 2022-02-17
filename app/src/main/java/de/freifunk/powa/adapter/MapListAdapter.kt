package de.freifunk.powa.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.text.InputType
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import de.freifunk.powa.MainActivity
import de.freifunk.powa.R
import de.freifunk.powa.database.ScanDBHelper
import de.freifunk.powa.store_intern.InternalStorageImage
import de.freifunk.powa.store_intern.deleteFileFromInternalStorage
import de.freifunk.powa.store_intern.saveBitmapToInternalStorage
import java.util.regex.Pattern

class MapListAdapter : ArrayAdapter<InternalStorageImage> {
    var listContext: Context
    var listResources: Int = 0
    var listOfImages: List<InternalStorageImage> = listOf()
    var newMapName: String = ""
    constructor(context: Context, resource: Int, objects: List<InternalStorageImage>) : super(context, resource, objects) {
        listContext = context
        listResources = resource
        listOfImages = objects
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var layoutInflater = LayoutInflater.from(listContext)
        var mapView = layoutInflater.inflate(listResources, parent, false)
        var imageView = mapView.findViewById<ImageView>(R.id.mapImageInList)
        var textView = mapView.findViewById<TextView>(R.id.mapNameInList)
        var menuBtn = mapView.findViewById<ImageButton>(R.id.threeDotMenuBtn)
        var name = listOfImages.get(position).name
        imageView.setImageBitmap(listOfImages.get(position).bitmap)
        name = name.removeSuffix(".jpg")

        textView.setText(name) // subsequence ".jpg" is excluded
        menuBtn.setOnClickListener{

            var drawable = imageView.drawable as BitmapDrawable
            var bitmap = drawable.bitmap
            showPopup(mapView, textView.text.toString(), bitmap, position)

       }

        return mapView
    }
    fun showPopup(view: View, name: String, bitmap: Bitmap, pos: Int) {
        val popup = PopupMenu(listContext ,view)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.list_row_menu, popup.menu)
        popup.setOnMenuItemClickListener{
            when(it.itemId){
                R.id.delete_option -> deleteMap(name, view, pos)

                R.id.rewrite_option -> createRenameDialog(name, bitmap)

                else -> false
            }
        }
        popup.show()
    }

    fun deleteMap(name: String, view: View, pos: Int): Boolean {
        var db = ScanDBHelper(listContext)

        deleteFileFromInternalStorage(listContext, name + ".jpg")
        db.deleteMap(name)
        Toast.makeText(listContext, "Karte wurde gelöscht", Toast.LENGTH_SHORT).show()
        return true
    }

    /**
     * Creates a AlertDialog to ask the User for a name for selected map
     */
    private fun createRenameDialog(oldName: String, bitmap: Bitmap): Boolean{
        var mapEditText = EditText(listContext)
        var mapNameDialog = AlertDialog.Builder(listContext)
            .setView(mapEditText)
            .setTitle("Kartenname ändern")
            .setMessage("Bitte gib einen Kartennamen ein")
            .setPositiveButton("Ok", null)
            .setNegativeButton("Abbrechen", null)
            .create()

        mapEditText.inputType = InputType.TYPE_CLASS_TEXT

        var db = ScanDBHelper(listContext)
        var returnValue = false
        mapNameDialog.setOnShowListener {
            var posBtn = mapNameDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            var negBtn = mapNameDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            posBtn.setOnClickListener {
                newMapName = mapEditText.text.toString()
                var pattern = Pattern.compile("[^a-zA-Z0-9_\\-]")
                if (pattern.matcher(newMapName).find()) {
                    mapEditText.setError("Bitte gib einen gültigen Namen ein")
                } else {
                    returnValue = db.updateMapName(oldName, newMapName)
                    if (returnValue) {
                        mapNameDialog.dismiss()
                        if (saveBitmapToInternalStorage(listContext, newMapName, bitmap)) {
                            Toast.makeText(
                                listContext,
                                "Name wurde geändert",
                                Toast.LENGTH_SHORT
                            ).show()
                            deleteFileFromInternalStorage(listContext, oldName+".jpg")
                        }
                        else
                            Toast.makeText(listContext, "Bild konnte nicht gespeichert werden", Toast.LENGTH_SHORT).show()

                    } else {
                        mapEditText.setError("Name existiert bereits!")
                    }
                }
                notifyDataSetChanged()
            }
            negBtn.setOnClickListener {
                mapNameDialog.dismiss()
            }
        }
        mapNameDialog.show()

        return returnValue
    }





}
