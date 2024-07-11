package com.example.contener

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.view.WindowManager
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
            /*val builder = AlertDialog.Builder(this)
            val Inflater = LayoutInflater.from(this)
            val view: View = Inflater.inflate(R.layout.basic_alert_video, null)
            view.isFocusable = true
            builder.setView(view)
            builder.setCancelable(false)
            val dialog = builder.create()
            dialog.show()

            val videoview : VideoView = view.findViewById(R.id.videoAlert)
            val media : MediaController = MediaController(this)
            media.setAnchorView(videoview)

            val path = Uri.parse("android.resource://com.example.contener/"+R.raw.videoejemplo)
            videoview.setMediaController(media)
            videoview.setVideoURI(path)
            //videoview.rotation = 90F
            videoview.start()

            videoview.setOnClickListener {
                //toggleFullscreen()
            }*/




            val intent = Intent(this, video_landscape_reproductor::class.java)
            intent.putExtra("modulo", modulo.toString())
            startActivity(intent)
            finish()
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