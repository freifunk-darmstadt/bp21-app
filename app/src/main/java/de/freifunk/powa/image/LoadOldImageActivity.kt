package de.freifunk.powa.image

import android.graphics.Rect
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import de.freifunk.powa.MarkerView
import de.freifunk.powa.R
import de.freifunk.powa.database.ScanDBHelper
import de.freifunk.powa.scan.ScanActivity
import de.freifunk.powa.scan.createThrottlingDialog
import de.freifunk.powa.store_intern.InternalStorageImage
import de.freifunk.powa.store_intern.loadListOfInternalStorageImages
import kotlinx.coroutines.runBlocking
import kotlin.math.max
import kotlin.math.min

class LoadOldImageActivity : AppCompatActivity() {
    protected lateinit var showImgIv: ImageView
    protected lateinit var scaleGesture: ScaleGestureDetector
    private var scaleFactor: Float = 1.0f
    protected lateinit var markerView: MarkerView
    protected lateinit var markerGesture: GestureDetector
    private var scrollHistoryX: Int = 0
    private var scrollHistoryY: Int = 0
    private var minZoomFactor: Float = 0.25f
    private var maxZoomFactor: Float = 20.0f
    private lateinit var mapName: String
    protected lateinit var scanBtn: Button
    lateinit var oldMarkers: SavedMarkerView
    var scanIsReady = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createThrottlingDialog(this)

        var name = intent.getStringExtra("mapName")
        var list: List<InternalStorageImage>
        var loadContext = this
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
        name = name?.removeSuffix(".jpg")
        var db = ScanDBHelper(this)
        var crdOfMarkers = db.readCoordinates(name!!)

        setContentView(R.layout.activity_load_old_image)
        showImgIv = findViewById(R.id.showOldImgIv)
        markerView = findViewById(R.id.old_marker_view)
        oldMarkers = findViewById(R.id.markerViewOfOldMarkers)
        scanBtn = findViewById(R.id.oldMapScanBtn)
        scaleGesture = ScaleGestureDetector(this, ScaleListener())
        markerGesture = GestureDetector(this, MarkerGestureListener())
        supportActionBar!!.hide()
        if (crdOfMarkers != null) {
            oldMarkers.coordinates = crdOfMarkers
        }
        scanBtn.isInvisible = true
        scanBtn.setOnClickListener {
            var scanAct = ScanActivity(this, mapName, markerView.initX, markerView.initY)
            scanAct.scanBtn = scanBtn
            scanBtn.isVisible = false
            createScanDialog(scanAct)
        }

        mapName = name
        showImgIv.setImageBitmap(internStorage!!.bitmap)
        oldMarkers.invalidate()
    }
    /**
     * Creates a AlertDialog to ask the User if he/she wants to start a scan
     */
    protected fun createScanDialog(scanAct: ScanActivity) {

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
                oldMarkers.scrollBy(distanceX, distanceY)
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
            oldMarkers.scaleX = scaleFactor
            oldMarkers.scaleY = scaleFactor
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
