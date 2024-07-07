package com.example.contener

import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.MediaController
import android.widget.VideoView
import androidx.constraintlayout.widget.ConstraintLayout

class video_landscape_reproductor : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_landscape_reproductor)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        getSupportActionBar()?.hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen
        var modulo = intent.getStringExtra("modulo")

        val videoview : VideoView = findViewById(R.id.videoView2)
            videoview.visibility = View.VISIBLE
            val media : MediaController = MediaController(this)
            media.setAnchorView(videoview)

            val path = Uri.parse("android.resource://com.example.contener/"+R.raw.videosostener1)
            videoview.setMediaController(media)
            videoview.setVideoURI(path)
            videoview.start()

            videoview.setOnClickListener {
                //toggleFullscreen()
                //videoview.visibility = View.GONE
            }

        val cancel : ConstraintLayout = findViewById(R.id.cancelBTN)
        cancel.setOnClickListener {
            val intent = Intent(this, Modulo::class.java)
            intent.putExtra("modulo", modulo.toString())
            startActivity(intent)
            finish()
        }
    }
}