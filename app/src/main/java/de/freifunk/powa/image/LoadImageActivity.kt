package de.freifunk.powa.image

import android.content.Intent
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.Button
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import de.freifunk.powa.R
import kotlin.math.max
import kotlin.math.min

class LoadImageActivity : AppCompatActivity() {

    private lateinit var showImgIv: ImageView
    private lateinit var loadImgBtn: Button
    private lateinit var scaleGesture: ScaleGestureDetector
    private var Factor: Float = 1.0f
    private lateinit var markerView: ImageView
    private lateinit var myGesture: GestureDetector
    private var scrollHistoryX: Int = 0
    private  var scrollHistoryY: Int = 0
    private var markerPosX = 0f
    private var markerPosY = 0f
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_load_image)

        showImgIv = findViewById(R.id.showImgIv)
        loadImgBtn = findViewById(R.id.loadImageBtn)
        markerView = findViewById(R.id.marker_view)
        scaleGesture = ScaleGestureDetector(this, ScaleListener())
        myGesture = GestureDetector(this, MyGestureListener())
        supportActionBar!!.hide()

        loadImgBtn.setOnClickListener {

        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        var historySize: Int
        var startX: Float
        var endX: Float
        var startY: Float
        var endY: Float


        if(event?.pointerCount == 2){
            scaleGesture?.onTouchEvent(event)

        }
        if(event?.action == MotionEvent.ACTION_MOVE && event.pointerCount == 1) {
            historySize = event.historySize
            if(historySize >0){
                startX = event.getHistoricalX(0, 0)
                endX = event.getHistoricalX(0,historySize-1)
                startY = event.getHistoricalY(0,0)
                endY = event.getHistoricalY(0,historySize -1)


                markerView?.x = markerView!!.x + (endX - startX)
                markerView?.y = markerView!!.y + (endY - startY)
                scrollHistoryX += ((startX - endX)/Factor).toInt()
                scrollHistoryY += ((startY - endY)/Factor).toInt()
                markerPosX = markerView!!.x
                markerPosY = markerView!!.y

                showImgIv?.scrollBy(((startX - endX)/Factor).toInt(), ((startY - endY)/Factor).toInt())

            }

        }

        myGesture?.onTouchEvent(event)






        return super.onTouchEvent(event)
    }

    fun getHeight():Int{
        var rectangle = Rect()
        window.decorView.getWindowVisibleDisplayFrame(rectangle)
        return rectangle.top
    }


    inner class ScaleListener: ScaleGestureDetector.SimpleOnScaleGestureListener(){
        override fun onScale(detector: ScaleGestureDetector?): Boolean {
            var width = showImgIv?.width
            var height = showImgIv?.height

            var vectorX = markerView!!.x - width!!/2
            var vectorY = markerView!!.y - height!!/2

            Factor *= detector!!.scaleFactor
            Factor = max(0.1f, min(Factor, 5.0f))
            showImgIv?.scaleX = Factor
            showImgIv?.scaleY = Factor
            markerView?.scaleX = Factor
            markerView?.scaleY = Factor

            if(Factor < 5.0 && Factor > 0.5) {
                markerView?.x = width!! / 2 + vectorX * detector!!.scaleFactor
                markerView?.y = height!! / 2 + vectorY * detector!!.scaleFactor
            }



            return true
        }
    }

    inner class MyGestureListener: GestureDetector.SimpleOnGestureListener(){
        override fun onDoubleTap(e: MotionEvent?): Boolean {

            var statusBarHeight = getHeight()


            markerView!!.x = e!!.getX() - markerView!!.width/2
            markerView!!.y = e!!.getY() - statusBarHeight.toFloat() - markerView!!.height/2



            return super.onDoubleTap(e)
        }

        fun dpFromPx(px:Float): Float{
            return px/ resources.displayMetrics.density
        }

    }



}