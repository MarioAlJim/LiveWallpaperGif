package com.silverwolf.secondwallpaper

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.silverwolf.secondwallpaper.databinding.ActivityLiveSettingsBinding

class LiveSettingsActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding : ActivityLiveSettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLiveSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar SharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        // Configurar el valor predeterminado de la velocidad y mostrarlo
        val defaultSpeed = 5 // Velocidad predeterminada
        binding.sbSpeed.progress = sharedPreferences.getInt("speed", defaultSpeed)
        binding.tvCloudSpeed.text = "${binding.sbSpeed.progress}"

        // Escuchar cambios en el SeekBar
        binding.sbSpeed.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Actualizar el TextView con el valor actual
                binding.tvCloudSpeed.text = "$progress"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // No es necesario implementar esto
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Guardar el valor de la velocidad en SharedPreferences
                sharedPreferences.edit().putInt("speed", binding.sbSpeed.progress).apply()
            }
        })
    }


    override fun onStop() {
        super.onStop()

        // Guardar el valor de la velocidad en SharedPreferences
        val speed = binding.sbSpeed.progress
        sharedPreferences.edit().putInt("speed", speed).apply()

        // Enviar un broadcast con la nueva velocidad
        val intent = Intent("com.silverwolf.secondwallpaper.ACTION_SPEED_CHANGED")
        intent.putExtra("speed", speed)
        sendBroadcast(intent)
    }
}
