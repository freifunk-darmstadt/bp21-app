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
import de.freifunk.powa.scan.ScanActivity
import de.freifunk.powa.store_intern.saveBitmapToInternalStorage
import java.util.regex.Pattern
import kotlin.math.max
import kotlin.math.min

class LoadImageActivity : AppCompatActivity() {

    private lateinit var showImgIv: ImageView
    private lateinit var loadImgBtn: Button
    private lateinit var scaleGesture: ScaleGestureDetector
    private var scaleFactor: Float = 1.0f
    private lateinit var markerView: MarkerView
    private lateinit var markerGesture: GestureDetector
    private var scrollHistoryX: Int = 0
    private var scrollHistoryY: Int = 0
    private var minZoomFactor: Float = 0.25f
    private var maxZoomFactor: Float = 20.0f
    private lateinit var mapName: String
    private lateinit var scanBtn: Button
    var scanIsReady: Boolean = true
    // create ComponentActivity to load and handle loading the image
    // A Dialog pops up after the User selects a map
    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            setImageVisibility(false)

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
        loadImgBtn = findViewById(R.id.loadImageBtn)
        markerView = findViewById(R.id.marker_view)
        scanBtn = findViewById(R.id.mapScanBtn)
        scaleGesture = ScaleGestureDetector(this, ScaleListener())
        markerGesture = GestureDetector(this, MarkerGestureListener())
        supportActionBar!!.hide()

        showImgIv.isInvisible = true
        scanBtn.isInvisible = true

        // request permissions on Button press and open system image selector
        loadImgBtn.setOnClickListener {
            getContent.launch("image/*")
        }
        scanBtn.setOnClickListener {
            var scanAct = ScanActivity(this, mapName, markerView.initX, markerView.initY)
            scanAct.scanBtn = scanBtn
            scanBtn.isVisible = false
            createScanDialog(scanAct)
        }
    }

    /**
     * Creates a AlertDialog to ask the User for a name for selected map
     */
    private fun createDialog() {
        var mapEditText = EditText(this)
        var mapNameDialog = AlertDialog.Builder(this)
            .setView(mapEditText)
            .setTitle("Bennene Karte")
            .setMessage("Bitte gib einen Kartennamen ein")
            .setPositiveButton("Ok", null)
            .setNegativeButton("Abbrechen", null)
            .create()

        mapEditText.inputType = InputType.TYPE_CLASS_TEXT
        mapNameDialog.setCanceledOnTouchOutside(false)
        var db = ScanDBHelper(this)
        mapNameDialog.setOnShowListener {
            var posBtn = mapNameDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            var negBtn = mapNameDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            posBtn.setOnClickListener {
                mapName = mapEditText.text.toString()
                var pattern = Pattern.compile("[^a-zA-Z0-9_\\-]")
                if (pattern.matcher(mapName).find()) {
                    mapEditText.setError("Bitte gib einen gültigen Namen ein")
                } else {

                    if (db.insertMaps(mapName)) {
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
        var drawable = showImgIv.drawable as BitmapDrawable
        var bitmap = drawable.bitmap
        return saveBitmapToInternalStorage(this, imageName, bitmap)
    }

    /**
     * Creates a AlertDialog to ask the User if he/she wants to start a scan
     */
    private fun createScanDialog(scanAct: ScanActivity) {

        var scanDialog = AlertDialog.Builder(this)
            .setView(null)
            .setTitle("Starte Scan")
            .setMessage("Möchtest du den Scan starten bei: \n " + "x:" + markerView.initX + ";y:" + markerView.initY + "?")
            .setPositiveButton("Ok", null)
            .setNegativeButton("Abbrechen", null)
            .create()
        scanDialog.setCanceledOnTouchOutside(false)
        scanDialog.setOnShowListener {
            var posBtn = scanDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            var negBtn = scanDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            posBtn.setOnClickListener {
                scanAct.startScan()
                scanDialog.dismiss()
            }
            negBtn.setOnClickListener {
                scanBtn.isVisible = true
                scanDialog.dismiss()
            }
        }
        scanDialog.show()
    }
    /**
     * set the visibility of the imageView and the load image button
     * @param value the value to set the visibility of the imageView to
     */
    private fun setImageVisibility(value: Boolean = false) {
        loadImgBtn.isVisible = value
        showImgIv.isInvisible = value
    }

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
                showImgIv.scrollBy(distanceX, distanceY)
            }
        }
        markerGesture.onTouchEvent(event)
        return super.onTouchEvent(event)
    }

    fun getHeight(): Int {
        val rectangle = Rect()
        window.decorView.getWindowVisibleDisplayFrame(rectangle)
        return rectangle.top
    }

    inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector?): Boolean {
            scaleFactor *= detector!!.scaleFactor
            scaleFactor = max(minZoomFactor, min(scaleFactor, maxZoomFactor))
            showImgIv.scaleX = scaleFactor
            showImgIv.scaleY = scaleFactor
            markerView.scaleX = scaleFactor
            markerView.scaleY = scaleFactor
            return true
        }
    }

    inner class MarkerGestureListener : GestureDetector.SimpleOnGestureListener() {
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
            return super.onDoubleTap(e)
        }

        fun dpFromPx(px: Float): Float {
            return px / resources.displayMetrics.density
        }
    }
}
