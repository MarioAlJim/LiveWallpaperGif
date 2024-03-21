package com.silverwolf.secondwallpaper

import android.content.Context
import android.graphics.Point
import android.view.WindowManager

class Utilities {

    fun getScreenSize(contexto: Context): Point {
        val windowManager = contexto.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val screenSize = Point()
        display.getSize(screenSize)
        return screenSize
    }

}