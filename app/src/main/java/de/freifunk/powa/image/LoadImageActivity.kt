package de.freifunk.powa.image

import android.content.DialogInterface
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import de.freifunk.powa.MarkerView
import de.freifunk.powa.R
import de.freifunk.powa.database.ScanDBHelper
import de.freifunk.powa.scan.ScanActivity
import de.freifunk.powa.scan.scan
import de.freifunk.powa.store_intern.saveBitmapToInternalStorage
import kotlin.concurrent.timer
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
    // create ComponentActivity to load and handle loading the image
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
        scanBtn.setOnClickListener{
            createScanDialog()
        }
    }
    private fun createDialog(){
        var mapEditText = EditText(this)
        var mapNameDialog =AlertDialog.Builder(this)
            .setView(mapEditText)
            .setTitle("Name Image")
            .setMessage("Please enter a name for your map")
            .setPositiveButton("Confirm",null)
            .setNegativeButton("Cancel",null)
            .create()

        mapEditText.inputType = InputType.TYPE_CLASS_TEXT

        var db = ScanDBHelper(this)
        mapNameDialog.setOnShowListener{
            var posBtn = mapNameDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            var negBtn = mapNameDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            posBtn.setOnClickListener{
                mapName = mapEditText.text.toString()
                if(db.insertMaps(mapName)){
                    mapNameDialog.dismiss()
                    if(saveImage(mapName))
                        Toast.makeText(this,"Image has been successfully saved", Toast.LENGTH_SHORT).show()
                    else
                        Toast.makeText(this,"Image couldn't be saved", Toast.LENGTH_SHORT).show()
                }else{
                    mapEditText.setError("Name already exist!")
                }

            }
            negBtn.setOnClickListener{
                mapNameDialog.dismiss()
            }
        }
        mapNameDialog.show()
    }
    private fun saveImage(imageName: String): Boolean{
        var drawable = showImgIv.drawable as BitmapDrawable
        var bitmap = drawable.bitmap
        return saveBitmapToInternalStorage(this,imageName,bitmap)

    }

    private fun createScanDialog(){

        var scanDialog =AlertDialog.Builder(this)
            .setView(null)
            .setTitle("Scan start")
            .setMessage("Do you want to start a scan at \n "+ "x:" + markerView.initX + ";y:" + markerView.initY+ "?")
            .setPositiveButton("Confirm",null)
            .setNegativeButton("Cancel",null)
            .create()

        scanDialog.setOnShowListener{
            var posBtn = scanDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            var negBtn = scanDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            posBtn.setOnClickListener{
                var scanAct = ScanActivity(this, mapName, markerView.initX, markerView.initY)
                scanAct.startScan()
                scanDialog.dismiss()

            }
            negBtn.setOnClickListener{
                scanDialog.dismiss()
            }
        }
        scanDialog.show()


    }
    /**
     * set the visibility of the imageView and the load image button
     * @param value the value to set the visibility of the imageView to
     */
    private fun setImageVisibility(value: Boolean) {
        loadImgBtn.isVisible = value
        showImgIv.isInvisible = value
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        var historySize: Int
        var startX: Float
        var endX: Float
        var startY: Float
        var endY: Float
        var distanceX: Int
        var distanceY: Int
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
        var rectangle = Rect()
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
            var statusBarHeight = getHeight()
            markerView.circleShouldDraw = true
            var middleX = showImgIv.width / 2
            var middleY = showImgIv.height / 2
            var vectorX: Float
            var vectorY: Float
            var onlyViewHeight = e!!.getY() - statusBarHeight
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
