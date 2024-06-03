package com.example.contener

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.widget.MediaController
import android.widget.VideoView

class Modulo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modulo)

        val webView : WebView = findViewById(R.id.web);
        val video = "<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/V2KCAfHjySQ?si=qkP3WXXxZEh9EDk-\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" referrerpolicy=\"strict-origin-when-cross-origin\" allowfullscreen></iframe>"
        webView.loadData(video, "text/html", "utf-8")
        webView.settings.javaScriptEnabled = true

        //video raw
        val videoview : VideoView = findViewById(R.id.videoView)
        val media : MediaController = MediaController(this)
        media.setAnchorView(videoview)
        val path = Uri.parse("android.resource://com.example.contener/"+R.raw.videoejemplo)
        videoview.setMediaController(media)
        videoview.setVideoURI(path)
        videoview.start()

    }
}