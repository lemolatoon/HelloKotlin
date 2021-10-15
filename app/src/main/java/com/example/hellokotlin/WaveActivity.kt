package com.example.hellokotlin

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class WaveActivity : AppCompatActivity() {

    private var mPaint: Paint = Paint();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_wave)
        var myView = MyView(this);
        setContentView(myView);
    }

    internal inner class MyView(context: Context): View(context) {
        private val paint = Paint();
        private val path = Path();

        private val lineStrokeWidth = 20f;

        override fun onDraw(canvas: Canvas) {
            paint.color = Color.argb(255, 255, 0, 255);

            paint.strokeWidth = lineStrokeWidth;

//            paint.style = Paint.Style.STROKE;
            paint.style = Paint.Style.STROKE;

            val _x = 40000 until 90000;
            val x = _x.map {x -> (0.001 * x).toFloat()};
            val a = 1;
            val y = x.map {x -> a * (kotlin.math.atan(x)).toFloat()};
            for (i in x.indices) {
                canvas.drawPoint(x[i], y[i], paint);
            }
        }
    }
}

