package com.silverwolf.secondwallpaper

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.os.Handler
import android.os.HandlerThread
import android.os.SystemClock
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder

class WeatherWallpaperService : WallpaperService() {
    override fun onCreateEngine(): Engine {
        return CloudsEngine()
    }

    inner class CloudsEngine : Engine() {
        private val handlerThread = HandlerThread("AnimationThread").apply { start() }
        private val handler = Handler(handlerThread.looper)
        private lateinit var surfaceHolder: SurfaceHolder
        private var isVisible = false
        private lateinit var cloudsAnimation: CloudsAnimation
        private val frameRate = 8L
        private var lastFrameTime = 0L

        private val speedChangedReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                intent?.let {
                    if (it.action == "com.silverwolf.secondwallpaper.ACTION_SPEED_CHANGED") {
                        val newSpeed = it.getIntExtra("speed", 5)
                        cloudsAnimation.setSpeed(newSpeed)
                    }
                }
            }
        }

        override fun onCreate(surfaceHolder: SurfaceHolder?) {
            super.onCreate(surfaceHolder)
            this.surfaceHolder = surfaceHolder!!
            val filter = IntentFilter("com.silverwolf.secondwallpaper.ACTION_SPEED_CHANGED")
            applicationContext.registerReceiver(speedChangedReceiver, filter)

            val cloudImagesArray = resources.obtainTypedArray(R.array.cloud_images)
            val bitmaps = ArrayList<Bitmap>()
            for (i in 0 until cloudImagesArray.length()) {
                val resourceId = cloudImagesArray.getResourceId(i, 0)
                val bitmap = BitmapFactory.decodeResource(resources, resourceId)
                bitmaps.add(bitmap)
            }
            cloudImagesArray.recycle()

            cloudsAnimation = CloudsAnimation(applicationContext, bitmaps)
            cloudsAnimation.initClouds()
        }

        private val drawRunnable = object : Runnable {
            override fun run() {
                val currentTime = SystemClock.elapsedRealtime()
                val elapsedTime = currentTime - lastFrameTime

                if (elapsedTime >= frameRate) {
                    lastFrameTime = currentTime
                    draw()
                    handler.postDelayed(this, frameRate)
                } else {
                    handler.postDelayed(this, frameRate - elapsedTime)
                }
            }
        }


        override fun onVisibilityChanged(visible: Boolean) {
            isVisible = visible
            handler.apply {
                if (visible) {
                    post(drawRunnable)
                } else {
                    removeCallbacks(drawRunnable)
                }
            }
        }

        private fun draw() {
            val canvas: Canvas? = surfaceHolder.lockCanvas()
            if (canvas != null) {
                // Clear canvas
                canvas.drawColor(0, android.graphics.PorterDuff.Mode.CLEAR)
                cloudsAnimation.draw(canvas)
                // Release the canvas
                surfaceHolder.unlockCanvasAndPost(canvas)
            }
            // Schedule the next frame
            handler.post(drawRunnable)
        }

        override fun onDestroy() {
            super.onDestroy()
            applicationContext.unregisterReceiver(speedChangedReceiver)
            handlerThread.quitSafely()
            handler.removeCallbacks(drawRunnable)
        }

    }
}

