package com.silverwolf.secondwallpaper

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder

class GIFWallpaperService : WallpaperService() {
    override fun onCreateEngine(): Engine? {
        return GIFWallpaperEngine()
    }

    inner class GIFWallpaperEngine : WallpaperService.Engine() {
        private lateinit var surfaceHolder: SurfaceHolder
        private val handler = Handler()
        private var isVisible = false
        private var angle = 0f

        override fun onCreate(surfaceHolder: SurfaceHolder?) {
            super.onCreate(surfaceHolder)
            this.surfaceHolder = surfaceHolder!!
        }

        private val drawAnimation = Runnable {
            draw()
        }

        override fun onVisibilityChanged(visible: Boolean) {
            isVisible = visible
            if (visible) {
                handler.post(drawAnimation)
            } else {
                handler.removeCallbacks(drawAnimation)
            }
        }

        private fun draw() {
            val canvas: Canvas? = surfaceHolder.lockCanvas()
            if (canvas != null) {
                // Limpiar el canvas
                canvas.drawColor(Color.BLACK)

                // Dibujar la animación en el canvas
                val paint = Paint()
                paint.color = Color.WHITE
                paint.strokeWidth = 10f
                val centerX = canvas.width / 2f
                val centerY = canvas.height / 2f
                val radius = (canvas.width.coerceAtMost(canvas.height) / 3).toFloat()
                canvas.drawLine(centerX, centerY, centerX + radius * Math.cos(angle.toDouble()).toFloat(), centerY + radius * Math.sin(angle.toDouble()).toFloat(), paint)

                // Actualizar el ángulo para la próxima iteración
                angle += 0.1f

                // Liberar el canvas
                surfaceHolder.unlockCanvasAndPost(canvas)

                // Programar la próxima actualización a 60 FPS
                handler.postDelayed(drawAnimation, 1000L / 60)
            }
        }
    }
}
