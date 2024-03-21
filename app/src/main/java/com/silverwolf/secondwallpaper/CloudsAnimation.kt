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
    private val bitmaps: List<Bitmap>
) {
    private val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val utilities = Utilities()
    private val maxy = utilities.getScreenSize(context).y
    private val clouds = mutableListOf<Cloud>()
    private var cloudsOutOfScreenCount = 0

    init {
        initClouds()
    }

    fun initClouds() {
        clouds.clear()
        val baseSpeed = sharedPreferences.getInt("speed", 5)

        for (i in 0 until MAX_CLOUDS) {
            val bitmap = bitmaps.random()
            val x = -Random.nextInt(0, INITIAL_OFFSET)
            val y = Random.nextInt(0, maxy)
            val speedVariation = Random.nextInt(0, 4)
            val dx = baseSpeed + speedVariation
            clouds.add(Cloud(bitmap, x, y, dx))
        }
    }

    private fun reorderClouds() {
        clouds.shuffle()
        cloudsOutOfScreenCount = 0
    }

    fun draw(canvas: Canvas) {
        val iterator = clouds.iterator()
        while (iterator.hasNext()) {
            val cloud = iterator.next()
            cloud.x += cloud.dx

            if (cloud.x > canvas.width) {
                cloud.x = -cloud.bitmap.width
                cloud.y = Random.nextInt(0, maxy)
            }

            if (isCloudVisible(cloud, canvas)) {
                canvas.drawBitmap(cloud.bitmap, cloud.x.toFloat(), cloud.y.toFloat(), Paint())
            } else if (cloud.x + cloud.bitmap.width < 0) {
                iterator.remove() // Remove clouds that have passed the screen
                cloudsOutOfScreenCount++
            }
        }

        // Check if all clouds have passed the screen and reorder them
        if (cloudsOutOfScreenCount == MAX_CLOUDS) {
            reorderClouds()
            cloudsOutOfScreenCount = 0
        }
    }

    private fun isCloudVisible(cloud: Cloud, canvas: Canvas): Boolean {
        return cloud.x < canvas.width && cloud.x + cloud.bitmap.width > 0 &&
                cloud.y < canvas.height && cloud.y + cloud.bitmap.height > 0
    }

    fun setSpeed(newSpeed: Int) {
        for (cloud in clouds) {
            val speedVariation = Random.nextInt(0, 4)
            cloud.dx = newSpeed + speedVariation
        }
    }

    data class Cloud(val bitmap: Bitmap, var x: Int, var y: Int, var dx: Int)

    companion object {
        const val MAX_CLOUDS = 10
        const val INITIAL_OFFSET = 100 // Offset inicial desde el borde izquierdo
    }
}


