package com.silverwolf.secondwallpaper

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Movie
import android.os.Handler
import android.service.wallpaper.WallpaperService
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import com.silverwolf.secondwallpaper.R.drawable.elisyan
import java.io.IOException
import java.io.InputStream

class GIFWallpaperService : WallpaperService() {
    @SuppressLint("ResourceType")
    override fun onCreateEngine(): Engine? {
        return try {
            val inputStream: InputStream = resources.openRawResource(elisyan)
            val movie = Movie.decodeStream(inputStream)
            GIFWallpaperEngine(movie)
        } catch (e : IOException) {
            Log.e("GIF", "Could not load asset")
            null
        }
    }

    inner class GIFWallpaperEngine (movie: Movie) : WallpaperService.Engine() {
        private final var frameDuration : Long = 100
        private lateinit var holder: SurfaceHolder
        private var movie = movie
        private var handler = Handler()
        private var isVisible = false


        override fun onCreate(surfaceHolder: SurfaceHolder?) {
            super.onCreate(surfaceHolder)
            holder = surfaceHolder!!
        }

        val drawGif = Runnable {
            kotlin.run { draw() }
        }

        fun draw() {
            if (isVisible) {
                val canvas = holder.lockCanvas()
                canvas.save()
                canvas.scale(5f, 9f)
                movie.draw(canvas, 0f, 0f)
                canvas.restore()
                movie.setTime((System.currentTimeMillis() % movie.duration()).toInt())

                holder.unlockCanvasAndPost(canvas)

                handler.removeCallbacks(drawGif)
                handler.postDelayed(drawGif, frameDuration)
            }
        }

        override fun onVisibilityChanged(visible: Boolean) {
            isVisible = visible
            if (isVisible) {
                handler.post(drawGif)
            } else {
                handler.removeCallbacks(drawGif)
            }
        }

        override fun onDestroy() {
            super.onDestroy()
            handler.removeCallbacks(drawGif)
        }

        override fun onTouchEvent(event: MotionEvent?) {
            if (event?.action == MotionEvent.ACTION_DOWN) {
                performFlashEffect()
            }
        }

        private fun performFlashEffect() {
            val flashColor = Color.WHITE // Color para el destello, puedes ajustarlo según tus preferencias
            val flashDuration = 200L // Duración del destello en milisegundos

            // Cambia el color de fondo del lienzo temporalmente para simular el destello
            val flashCanvas = holder.lockCanvas()
            flashCanvas.drawColor(flashColor)
            holder.unlockCanvasAndPost(flashCanvas)

            // Después del tiempo de destello, restaura el fondo al color original
            handler.postDelayed({
                val canvas = holder.lockCanvas()
                canvas.drawColor(Color.BLACK) // Cambia el color de fondo al original (en este caso, negro)
                movie.draw(canvas, 0f, 0f) // Dibuja la imagen del GIF nuevamente
                holder.unlockCanvasAndPost(canvas)
            }, flashDuration)
        }
    }
}