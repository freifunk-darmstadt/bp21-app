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
import de.freifunk.powa.MarkerView
import de.freifunk.powa.R
import kotlin.math.max
import kotlin.math.min

class LoadImageActivity : AppCompatActivity() {

    private lateinit var showImgIv: ImageView
    private lateinit var loadImgBtn: Button
    private lateinit var scaleGesture: ScaleGestureDetector
    private var factor: Float = 1.0f
    private lateinit var markerView: MarkerView
    private lateinit var myGesture: GestureDetector
    private var scrollHistoryX: Int = 0
    private  var scrollHistoryY: Int = 0

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
        var distanceX: Int
        var distanceY: Int
        if(event?.pointerCount == 2){
            scaleGesture?.onTouchEvent(event)
        }
        if(event?.action == MotionEvent.ACTION_MOVE && event.pointerCount == 1) {
            historySize = event.historySize
            if(historySize >0){
                startX = event.getHistoricalX(0)
                endX = event.getHistoricalX(historySize-1)
                startY = event.getHistoricalY(0)
                endY = event.getHistoricalY(historySize -1)
                distanceX = ((startX - endX)/factor).toInt()
                distanceY = ((startY - endY)/factor).toInt()
                scrollHistoryX += distanceX
                scrollHistoryY += distanceY

                markerView?.scrollBy(distanceX, distanceY)
                showImgIv?.scrollBy(distanceX, distanceY)
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
            factor *= detector!!.scaleFactor
            factor = max(0.1f, min(factor, 10.0f))
            showImgIv?.scaleX = factor
            showImgIv?.scaleY = factor
            markerView?.scaleX = factor
            markerView?.scaleY = factor
            return true
        }
    }

    inner class MyGestureListener: GestureDetector.SimpleOnGestureListener(){
        override fun onDoubleTap(e: MotionEvent?): Boolean {
            var statusBarHeight = getHeight()
            markerView?.circleShouldDraw = true
            var middleX = showImgIv!!.width/2
            var middleY = showImgIv!!.height/2
            var vectorX: Float
            var vectorY: Float
            var onlyViewHeight = e!!.getY() - statusBarHeight
            if(factor == 1.0f){
                markerView!!.initX = ((e!!.getX()) + scrollHistoryX )
                markerView!!.initY = (onlyViewHeight + scrollHistoryY )
            }
            else {
                vectorX = (e!!.getX()) - middleX
                vectorY =  onlyViewHeight - middleY
                markerView?.initX = middleX + vectorX / factor + scrollHistoryX
                markerView?.initY = middleY + vectorY / factor + scrollHistoryY
            }
            markerView!!.invalidate()
            return super.onDoubleTap(e)
        }

        fun dpFromPx(px:Float): Float{
            return px/ resources.displayMetrics.density
        }

    }



}