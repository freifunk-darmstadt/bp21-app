package de.freifunk.powa.activity.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class MarkerView : View {
    constructor(context: Context?) :
        super(context)
    constructor(context: Context?, attrs: AttributeSet?) :
        super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) :
        super(context, attrs, defStyleAttr)
    lateinit var canvas: Canvas
    var initX: Float = 0f
    var initY: Float = 0f
    var rad = 20f
    var circleShouldDraw = false
    var linePaint: Paint = Paint().apply {
        isAntiAlias = true
        color = Color.RED
        style = Paint.Style.FILL
        strokeWidth = 5f
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            this.canvas = canvas

            if (circleShouldDraw) {
                drawCircle()
            }
        }
    }

    fun drawCircle() {
        canvas.drawCircle(initX, initY, rad, linePaint)
    }
}
