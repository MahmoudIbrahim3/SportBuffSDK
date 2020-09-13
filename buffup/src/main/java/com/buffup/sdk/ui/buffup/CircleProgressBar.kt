/*
 * Copyright (C) 2015 Pedramrn@gmail.com
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.buffup.sdk.ui.buffup

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import com.buffup.sdk.R

class CircleProgressBar(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private var strokeWidth = 4f
    private var progress = 0f
    private var min = 0
    private var max = 100
    private val startAngle = -90
    private var color = Color.DKGRAY
    private var backColor = Color.GRAY
    private var rectF: RectF? = null
    private var backgroundPaint: Paint? = null
    private var foregroundPaint: Paint? = null

    fun getStrokeWidth(): Float {
        return strokeWidth
    }

    fun setStrokeWidth(strokeWidth: Float) {
        this.strokeWidth = strokeWidth
        backgroundPaint!!.strokeWidth = strokeWidth
        foregroundPaint!!.strokeWidth = strokeWidth
        invalidate()
        requestLayout() //Because it should recalculate its bounds
    }

    fun getProgress(): Float {
        return progress
    }

    fun setProgress(progress: Float) {
        this.progress = progress
        invalidate()
    }

    fun getMin(): Int {
        return min
    }

    fun setMin(min: Int) {
        this.min = min
        invalidate()
    }

    fun getMax(): Int {
        return max
    }

    fun setMax(max: Int) {
        this.max = max
        invalidate()
    }

    fun getColor(): Int {
        return color
    }

    fun setColor(color: Int) {
        this.color = color
        backColor = ContextCompat.getColor(context, R.color.count_down_border_color)
        //        backgroundPaint.setColor(adjustAlpha(color, 0.3f));
        backgroundPaint!!.color = backColor
        foregroundPaint!!.color = color
        invalidate()
        requestLayout()
    }

    private fun init(context: Context, attrs: AttributeSet) {
        rectF = RectF()
        val typedArray = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.CircleProgressBar,
            0, 0
        )
        try {
            strokeWidth = typedArray.getDimension(
                R.styleable.CircleProgressBar_progressBarThickness,
                strokeWidth
            )
            progress = typedArray.getFloat(R.styleable.CircleProgressBar_progress, progress)
            color = typedArray.getInt(R.styleable.CircleProgressBar_progressbarColor, color)
            backColor =
                typedArray.getInt(R.styleable.CircleProgressBar_backgroundColor, backColor)
            min = typedArray.getInt(R.styleable.CircleProgressBar_min, min)
            max = typedArray.getInt(R.styleable.CircleProgressBar_max, max)
        } finally {
            typedArray.recycle()
        }
        backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        //        backgroundPaint.setColor(adjustAlpha(color, 0.3f));
        backgroundPaint!!.color = backColor
        backgroundPaint!!.style = Paint.Style.STROKE
        backgroundPaint!!.strokeWidth = strokeWidth
        foregroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        foregroundPaint!!.color = color
        foregroundPaint!!.style = Paint.Style.STROKE
        foregroundPaint!!.strokeWidth = strokeWidth
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawOval(rectF!!, backgroundPaint!!)
        val angle = 360 * progress / max
        canvas.drawArc(rectF!!, startAngle.toFloat(), angle, false, foregroundPaint!!)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val height =
            getDefaultSize(suggestedMinimumHeight, heightMeasureSpec)
        val width =
            getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)
        val min = Math.min(width, height)
        setMeasuredDimension(min, min)
        rectF!![0 + strokeWidth / 2, 0 + strokeWidth / 2, min - strokeWidth / 2] =
            min - strokeWidth / 2
    }

    fun lightenColor(color: Int, factor: Float): Int {
        val r = Color.red(color) * factor
        val g = Color.green(color) * factor
        val b = Color.blue(color) * factor
        val ir = Math.min(255, r.toInt())
        val ig = Math.min(255, g.toInt())
        val ib = Math.min(255, b.toInt())
        val ia = Color.alpha(color)
        return Color.argb(ia, ir, ig, ib)
    }

    fun adjustAlpha(color: Int, factor: Float): Int {
        val alpha = Math.round(Color.alpha(color) * factor)
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)
        return Color.argb(alpha, red, green, blue)
    }

    fun setProgressWithAnimation(progress: Float) {
        val objectAnimator = ObjectAnimator.ofFloat(this, "progress", progress)
        objectAnimator.duration = 1500
        objectAnimator.interpolator = DecelerateInterpolator()
        objectAnimator.start()
    }

    init {
        init(context, attrs)
    }
}