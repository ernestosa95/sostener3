package com.example.contener

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.WindowManager
import android.widget.Button
import android.widget.MediaController
import android.widget.Toast
import android.widget.VideoView

class HomePrincipal : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_principal)

        getSupportActionBar()?.hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen

        val myPreferences = PreferenceManager.getDefaultSharedPreferences(this@HomePrincipal)
        Toast.makeText(this, myPreferences.getString("p1", "unknown"), Toast.LENGTH_SHORT).show()

        val formButton = findViewById<Button>(R.id.formButton)
        formButton.setOnClickListener {
            val intent = Intent(this, formularioHome::class.java)
            startActivity(intent)
            finish()
        }

        /*val mVideoView : VideoView = findViewById(R.id.videoView);
        //de forma alternativa si queremos un streaming usar
        mVideoView.setVideoURI(Uri.parse("https://www.youtube.com/watch?v=KBo1pyugG88"));
        //mVideoView.setVideoPath("/mnt/sdcard/video.mp4");

        val mediaController = MediaController(this)
        mVideoView.setMediaController(mediaController);*/
    }
}