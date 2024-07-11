package com.example.contener

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout

class Modulo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modulo)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        // Evitar la rotacion
        if (this.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED
        }

        getSupportActionBar()?.hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen

        var modulo = intent.getStringExtra("modulo")
        var title : TextView = findViewById(R.id.textView38)
        title.setText(modulo)


        var videoA : ConstraintLayout = findViewById(R.id.video1)
        videoA.setOnClickListener {
            val intent = Intent(this, video_landscape_reproductor::class.java)
            intent.putExtra("modulo", modulo.toString())
            startActivity(intent)
            finish()
        }

        val audio : ConstraintLayout = findViewById(R.id.audioCL)
        val mediaPlayerAudio = MediaPlayer.create(this, R.raw.audio) // Reproducir audio desde la carpeta raw
        val imageAudio : ImageView = findViewById(R.id.imageAudio)
        //val filePath = Uri.parse("android.resource://com.example.contener/"+R.raw.audio)
        //val mediaPlayerAudio = MediaPlayer.create(this, filePath)
        var status = "p"
        audio.setOnClickListener {
            if (status=="p"){
                mediaPlayerAudio.start()
                status="s"
                imageAudio.setImageResource(android.R.drawable.ic_media_pause)

            }else{
                mediaPlayerAudio.pause()
                status="p"
                imageAudio.setImageResource(android.R.drawable.ic_media_play)
            }
        }
    }

    private fun toggleFullscreen() {
        val isFullscreen = window.attributes.flags and WindowManager.LayoutParams.FLAG_FULLSCREEN != 0

        if (isFullscreen) {
            exitFullscreen()
        } else {
            enterFullscreen()
        }
    }

    private fun enterFullscreen() {
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    private fun exitFullscreen() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, HomePrincipal::class.java)
        startActivity(intent)
        finish()
    }
}