package com.silverwolf.secondwallpaper

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.preference.PreferenceManager
import kotlin.random.Random

class CloudsAnimation(
    private val context: Context,
    private val bitmaps: List<Bitmap>,
) {
    private lateinit var sharedPreferences: SharedPreferences
    private data class Cloud(val bitmap: Bitmap, var x: Int, var y: Int, var dx: Int)
    private val clouds = mutableListOf<Cloud>()
    private val utilities = Utilities()
    private val maxy = utilities.getScreenSize(context).y
    var cloudsOutOfScreenCount = 0
    fun initClouds() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val baseSpeed = sharedPreferences.getInt("speed", 5)

        for (i in 0 until MAX_CLOUDS) {
            val bitmap = bitmaps[Random.nextInt(bitmaps.size)]
            val x = -Random.nextInt(0, INITIAL_OFFSET)
            val y = Random.nextInt(0, maxy)
            val speedVariation = Random.nextInt(0, 4)
            val dx = baseSpeed + speedVariation
            clouds.add(Cloud(bitmap, x, y, dx))
        }
    }

    fun reorderClouds() {
        clouds.shuffle()
        initClouds()
        cloudsOutOfScreenCount = 0
    }

    fun draw(canvas: Canvas) {
        for (cloud in clouds) {
            cloud.x += cloud.dx

            if (cloud.x > canvas.width) {
                cloud.x = -cloud.bitmap.width
                cloud.y = Random.nextInt(0, maxy)
            }

            if (isCloudVisible(cloud, canvas, clouds.size)) {
                canvas.drawBitmap(cloud.bitmap, cloud.x.toFloat(), cloud.y.toFloat(), Paint())
            }
        }
    }


    private fun isCloudVisible(cloud: Cloud, canvas: Canvas, clouds: Int): Boolean {
        val isVisible = cloud.x < canvas.width && cloud.x + cloud.bitmap.width > 0 &&
                cloud.y < canvas.height && cloud.y + cloud.bitmap.height > 0

        val isOnEdgeOfScreen = cloud.x + cloud.bitmap.width >= canvas.width
        val isAlmostOutOfScreen = cloud.x + cloud.bitmap.width >= canvas.width - cloud.dx

        if (!isVisible && isOnEdgeOfScreen && isAlmostOutOfScreen) {
            cloudsOutOfScreenCount++
            if (cloudsOutOfScreenCount == clouds) {
                reorderClouds()
            }
        }


        return isVisible
    }


    fun setSpeed(newSpeed: Int) {
        val speedVariation = Random.nextInt(0, 4)
        for (cloud in clouds) {
            cloud.dx =  newSpeed + speedVariation
        }
    }
    companion object {
        const val MAX_CLOUDS = 10
        const val INITIAL_OFFSET = 100 // Offset inicial desde el borde izquierdo
    }
}

