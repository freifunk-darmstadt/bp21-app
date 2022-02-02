package de.freifunk.powa.image

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class SavedMarkerView : View {
    constructor(context: Context?) :
        super(context)
    constructor(context: Context?, attrs: AttributeSet?) :
        super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) :
        super(context, attrs, defStyleAttr)
    lateinit var canvas: Canvas
    var coordinates: MutableList<Pair<Float, Float>> = mutableListOf()

    var rad = 20f

    var linePaint: Paint = Paint().apply {
        isAntiAlias = true
        color = Color.BLUE
        style = Paint.Style.FILL
        strokeWidth = 5f
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            this.canvas = canvas

            coordinates.forEach {
                drawCircle(it.first, it.second)
            }
        }
    }

    fun drawCircle(x: Float, y: Float) {
        canvas.drawCircle(x, y, rad, linePaint)
    }
}
