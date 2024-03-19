package com.silverwolf.secondwallpaper

import android.annotation.SuppressLint
import android.graphics.Movie
import android.os.Handler
import android.service.wallpaper.WallpaperService
import android.util.Log
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
    }

}