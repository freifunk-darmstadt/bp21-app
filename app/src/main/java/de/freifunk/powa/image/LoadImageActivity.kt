package de.freifunk.powa.image

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Switch
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import de.freifunk.powa.MainActivity
import de.freifunk.powa.MarkerView
import de.freifunk.powa.R
import de.freifunk.powa.database.ScanDBHelper
import de.freifunk.powa.permissions.getGpsLocation
import de.freifunk.powa.permissions.locationToString
import de.freifunk.powa.scan.ScanActivity
import de.freifunk.powa.scan.createThrottlingDialog
import de.freifunk.powa.storeIntern.InternalStorageImage
import de.freifunk.powa.storeIntern.loadListOfInternalStorageImages
import de.freifunk.powa.storeIntern.saveBitmapToInternalStorage
import kotlinx.coroutines.runBlocking
import java.util.regex.Pattern
import kotlin.math.max
import kotlin.math.min

class LoadImageActivity : AppCompatActivity() {
    protected lateinit var showImgIv: ImageView
    protected lateinit var scaleGesture: ScaleGestureDetector
    private var scaleFactor: Float = 1.0f
    protected lateinit var markerView: MarkerView
    protected lateinit var markerGesture: GestureDetector
    private var scrollHistoryX: Int = 0
    private var scrollHistoryY: Int = 0
    private var minZoomFactor: Float = 1f
    private var maxZoomFactor: Float = 20.0f
    private lateinit var mapName: String
    protected lateinit var scanBtn: Button
    lateinit var oldMarkers: SavedMarkerView
    lateinit var markerSwitch: Switch
    lateinit var multiScanToggle: Switch

    // create ComponentActivity to load and handle loading the image
    // A Dialog pops up after the User selects a map
    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            showImgIv.isVisible = true

            // loads the image from the URI and stores it to the imageview
            val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(uri))
            showImgIv.setImageBitmap(bitmap)
            createDialog()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_load_image)

        showImgIv = findViewById(R.id.showImgIv)
        markerView = findViewById(R.id.marker_view)
        scanBtn = findViewById(R.id.ScanBtn)
        scaleGesture = ScaleGestureDetector(this, ScaleListener())
        markerGesture = GestureDetector(this, MarkerGestureListener())
        oldMarkers = findViewById(R.id.old_markers_view)
        markerSwitch = findViewById(R.id.switchMarkers)
        multiScanToggle = findViewById(R.id.multiScanToggle)
        createThrottlingDialog(this)

        var name = intent.getStringExtra("mapName")

        if (name != null) {
            // load old image
            var list: List<InternalStorageImage>
            val loadContext = this
            var internStorage: InternalStorageImage? = null
            runBlocking {
                list = loadListOfInternalStorageImages(loadContext)
            }
            for (it in list) {
                if (it.name == (name)) {
                    internStorage = it
                    break
                }
            }
            name = name.removeSuffix(".jpg")
            val db = ScanDBHelper(this)
            val crdOfMarkers = db.readCoordinates(name)

            if (crdOfMarkers != null) {
                oldMarkers.coordinates = crdOfMarkers
            }

            mapName = name
            showImgIv.setImageBitmap(internStorage!!.bitmap)
            oldMarkers.invalidate()
            scanBtn.isInvisible = true
        } else {
            scanBtn.text = resources.getString(R.string.load_image)
            showImgIv.isInvisible = true
        }

        multiScanToggle.isInvisible = true
        supportActionBar!!.hide()
        scanBtn.setOnClickListener {
            if (scanBtn.text == resources.getString(R.string.start_scan)) {
                var msCounter = 1
                if(multiScanToggle.isChecked)
                    msCounter = 4
                val scanAct = ScanActivity(this, mapName, markerView.initX, markerView.initY, scanBtn, msCounter)
                scanAct.scanBtn = scanBtn
                scanBtn.isVisible = false
                createScanDialog(scanAct)

            } else {
                getContent.launch("image/*")
                scanBtn.text = resources.getString(R.string.start_scan)
                multiScanToggle.isVisible = false
                scanBtn.isVisible = false
            }
        }

        markerSwitch.setOnClickListener {
            oldMarkers.isInvisible = markerSwitch.isChecked
        }
    }

    /**
     * Creates a AlertDialog to ask the User if he/she wants to start a scan
     * @param scanAct to start the scan
     */
    protected fun createScanDialog(scanAct: ScanActivity) {

        val scanDialog = AlertDialog.Builder(this)
            .setView(null)
            .setTitle("Starte Scan")
            .setMessage("Möchtest du den Scan starten bei: \n " + "x:" + markerView.initX + ";y:" + markerView.initY + "?")
            .setPositiveButton("Ok", null)
            .setNegativeButton("Abbrechen", null)
            .create()
        scanDialog.setCanceledOnTouchOutside(false)
        scanDialog.setOnShowListener {
            val posBtn = scanDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            val negBtn = scanDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            posBtn.setOnClickListener {
                scanAct.startScan()
                scanDialog.dismiss()
            }
            negBtn.setOnClickListener {
                scanBtn.isVisible = true
                multiScanToggle.isVisible = true
                scanDialog.dismiss()
            }
        }

        scanDialog.show()
    }

    /**
     * Method for scrolling only
     */
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val historySize: Int
        val startX: Float
        val endX: Float
        val startY: Float
        val endY: Float
        val distanceX: Int
        val distanceY: Int
        if (event?.pointerCount == 2) {
            scaleGesture.onTouchEvent(event)
        }
        if (event?.action == MotionEvent.ACTION_MOVE && event.pointerCount == 1) {
            historySize = event.historySize
            if (historySize > 0) {
                startX = event.getHistoricalX(0)
                endX = event.getHistoricalX(historySize - 1)
                startY = event.getHistoricalY(0)
                endY = event.getHistoricalY(historySize - 1)
                distanceX = ((startX - endX) / scaleFactor).toInt()
                distanceY = ((startY - endY) / scaleFactor).toInt()
                scrollHistoryX += distanceX
                scrollHistoryY += distanceY

                markerView.scrollBy(distanceX, distanceY)
                oldMarkers.scrollBy(distanceX, distanceY)
                showImgIv.scrollBy(distanceX, distanceY)
            }
        }
        markerGesture.onTouchEvent(event)
        return super.onTouchEvent(event)
    }

    /**
     * @return return the height of the statusbar in px
     */
    fun getHeight(): Int {
        val rectangle = Rect()
        window.decorView.getWindowVisibleDisplayFrame(rectangle)
        return rectangle.top
    }

    inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        /**
         * Method for scaling the views
         * @return true
         */
        override fun onScale(detector: ScaleGestureDetector?): Boolean {
            scaleFactor *= detector!!.scaleFactor
            scaleFactor = max(minZoomFactor, min(scaleFactor, maxZoomFactor))
            showImgIv.scaleX = scaleFactor
            showImgIv.scaleY = scaleFactor
            markerView.scaleX = scaleFactor
            markerView.scaleY = scaleFactor
            oldMarkers.scaleX = scaleFactor
            oldMarkers.scaleY = scaleFactor
            return true
        }
    }

    inner class MarkerGestureListener : GestureDetector.SimpleOnGestureListener() {
        /**
         * Marks the position where the view was doubletapped
         * @return return from super.onDoubleTap
         */
        override fun onDoubleTap(e: MotionEvent?): Boolean {
            val statusBarHeight = getHeight()
            markerView.circleShouldDraw = true
            val middleX = showImgIv.width / 2
            val middleY = showImgIv.height / 2
            val vectorX: Float
            val vectorY: Float
            val onlyViewHeight = e!!.getY() - statusBarHeight
            if (scaleFactor == 1.0f) {
                markerView.initX = ((e.getX()) + scrollHistoryX) - showImgIv.x
                markerView.initY = (onlyViewHeight + scrollHistoryY) - showImgIv.y
                // subtraction with the position of showImgIv because it is not positioned in the origin
            } else {
                vectorX = (e.getX()) - middleX
                vectorY = onlyViewHeight - middleY
                markerView.initX = middleX + vectorX / scaleFactor + scrollHistoryX - showImgIv.x / scaleFactor
                markerView.initY = middleY + vectorY / scaleFactor + scrollHistoryY - showImgIv.y / scaleFactor
            }
            markerView.invalidate()
            scanBtn.isInvisible = false
            multiScanToggle.isInvisible = false

            return super.onDoubleTap(e)
        }
    }

    /**
     * Creates a AlertDialog to ask the User for a name for selected map
     */
    private fun createDialog() {
        val mapEditText = EditText(this)
        val mapNameDialog = AlertDialog.Builder(this)
            .setView(mapEditText)
            .setTitle("Bennene Karte")
            .setMessage("Bitte gib einen Kartennamen ein")
            .setPositiveButton("Ok", null)
            .setNegativeButton("Abbrechen", null)
            .create()

        mapEditText.inputType = InputType.TYPE_CLASS_TEXT
        mapNameDialog.setCanceledOnTouchOutside(false)
        val db = ScanDBHelper(this)
        mapNameDialog.setOnShowListener {
            val posBtn = mapNameDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            val negBtn = mapNameDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            posBtn.setOnClickListener {
                mapName = mapEditText.text.toString()
                val pattern = Pattern.compile("[^a-zA-Z0-9_\\-]")
                if (pattern.matcher(mapName).find()) {
                    mapEditText.setError("Bitte gib einen gültigen Namen ein")
                } else {

                    if (db.insertMaps(mapName)) {
                        getGpsLocation(this) { location ->
                            db.updateLocationInTableMap(mapName, locationToString(location))
                        }

                        mapNameDialog.dismiss()
                        if (saveImage(mapName))
                            Toast.makeText(this, "Bild wurde erfolgreich gespeichert", Toast.LENGTH_SHORT).show()
                        else
                            Toast.makeText(this, "Bild konnte nicht gespeichert werden", Toast.LENGTH_SHORT).show()
                    } else {
                        mapEditText.setError("Name existiert bereits!")
                    }
                }
            }

            negBtn.setOnClickListener {
                mapNameDialog.dismiss()
                startActivity(Intent(this, MainActivity::class.java))
            }
        }
        mapNameDialog.show()
    }

    /**
     * @param imageName gets the name of the map and saves the image in the imageview under this name
     * @return true if the image is saved successfully
     *          false if the image couldn't be saved
     */
    private fun saveImage(imageName: String): Boolean {
        val drawable = showImgIv.drawable as BitmapDrawable
        val bitmap = drawable.bitmap
        return saveBitmapToInternalStorage(this, imageName, bitmap)
    }
}
