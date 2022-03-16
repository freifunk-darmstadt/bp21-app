package de.freifunk.powa.activity.adapter

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.text.InputType
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import de.freifunk.powa.R
import de.freifunk.powa.model.InternalStorageImage
import de.freifunk.powa.storeIntern.deleteFileFromInternalStorage
import de.freifunk.powa.storeIntern.renameFileInInternalStorage
import de.freifunk.powa.utils.ScanDBHelper
import java.io.IOException
import java.util.regex.Pattern

class MapListAdapter : ArrayAdapter<InternalStorageImage> {
    var listContext: Context
    var listResources: Int = 0
    var listOfImages: List<InternalStorageImage> = listOf()
    var newMapName: String = ""
    var activity: Activity
    constructor(context: Context, resource: Int, objects: List<InternalStorageImage>, act: Activity) : super(context, resource, objects) {
        listContext = context
        listResources = resource
        listOfImages = objects
        this.activity = act
    }

    /**
     * This Method defines all the views inside the listview
     * @param position is the position of the view from 0 to n
     * @param convertView the converted view
     * @param parent the parentview of the defined view
     * @return the defined view at the given position
     */
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
        menuBtn.setOnClickListener {

            var drawable = imageView.drawable as BitmapDrawable
            var bitmap = drawable.bitmap
            showPopup(mapView, textView.text.toString(), bitmap, position)
        }

        return mapView
    }

    /**
     * This Method creates a popupmenu
     * @param view the view in which the popupmenu is created
     * @param name name of listelement to reference a map
     * @param bitmap map in the listview as a bitmap
     * @param pos position of the view in the listview
     */
    fun showPopup(view: View, name: String, bitmap: Bitmap, pos: Int) {
        val popup = PopupMenu(listContext, view)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.list_row_menu, popup.menu)
        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.delete_option -> deleteMap(name)

                R.id.rewrite_option -> createRenameDialog(name, bitmap)

                else -> false
            }
        }
        popup.show()
    }

    /**
     * This Method deletes a entry with the given name in the listview
     * @param name name of the map in the listview to be deleted
     * @return true if the entry was successfully deleted
     */
    fun deleteMap(name: String): Boolean {
        var db = ScanDBHelper(listContext)

        val returnVal = deleteFileFromInternalStorage(listContext, name + ".jpg")
        db.deleteMap(name)
        Toast.makeText(listContext, "Karte wurde gelöscht", Toast.LENGTH_SHORT).show()
        activity.recreate()
        return returnVal
    }

    /**
     * Creates a AlertDialog to ask the User for a new name for selected map
     * @param oldName old name of the map which will be renamed
     * @param bitmap  bitmap to the corresponding name   // TODO loeschen von bitmap wenn funktion von StoreImageUtilities zu renamen exisitiert
     * @return true if the map is successfully renamed, false if not
     */
    private fun createRenameDialog(oldName: String, bitmap: Bitmap): Boolean {
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
                returnValue = checkAndUpdateName(oldName, newMapName, bitmap, db, mapEditText)
                if (returnValue) {
                    Toast.makeText(
                        listContext,
                        "Name wurde geändert",
                        Toast.LENGTH_SHORT
                    ).show()
                    activity.recreate()
                    mapNameDialog.dismiss()
                }
            }
            negBtn.setOnClickListener {
                mapNameDialog.dismiss()
            }
        }
        mapNameDialog.show()

        return returnValue
    }

    /**
     * This Method checks if the given name is valid, already exists and a new map can be saved with parameter name
     * @param oldName old name of the map which will be renamed
     * @param name new name for the map
     * @param bitmap  bitmap to the corresponding name
     * @param db database in which the new data are saved
     * @param mapEditText textfield in which the new name is given
     * @return false if name doesn't match the pattern, already exists or the map couldn't be saved
     */
    private fun checkAndUpdateName(oldName: String, name: String, bitmap: Bitmap, db: ScanDBHelper, mapEditText: EditText): Boolean {
        var pattern = Pattern.compile("[^a-zA-Z0-9_\\-]")
        if (pattern.matcher(name).find()) {
            mapEditText.setError("Bitte gib einen gültigen Namen ein")
            return false
        }
        var returnValue = db.updateMapName(oldName, name)
        if (!(returnValue)) {
            mapEditText.setError("Name existiert bereits!")
            return false
        }
        try {
            renameFileInInternalStorage(listContext, oldName, name)
        } catch (e: IOException) {
            Toast.makeText(listContext, "Bild konnte nicht gespeichert werden", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}
