package com.example.contener

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.imageview.ShapeableImageView

class Modulo : AppCompatActivity() {

    var modulo : String = ""

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

        modulo = intent.getStringExtra("modulo").toString()
        var title : TextView = findViewById(R.id.textView38)
        title.setText(modulo)

        //Declaracion de recursos para los modulos
        val recursos = resources
        // MODULO DERECHOS
        val Derechos = arrayOf("derechos1.mp4")

        // MODULO EMBARAZO
        val Embarazo = arrayOf("${recursos.getString(R.string.consejo_embarazo)}.txt")

        //MODULO PARTO
        val Parto = arrayOf("audio.mp3",
            "${recursos.getString(R.string.consejo_embarazo)}.txt",
            "derechos1.mp4")

        // MODULO POSTPARTO
        val Posparto = arrayOf("entrevista_hospital.mp4",
            "derechos1.mp4",
            "${recursos.getString(R.string.consejo_embarazo)}.txt")

        //MODULO LACTANCIA
        val Lactancia = arrayOf("entrevista_hospital.mp4",
            "entrevista_lactancia.mp4")

        val contenido = mapOf("Derechos" to Derechos,
            "Posparto" to Posparto,
            "Parto" to Parto,
            "Embarazo" to Embarazo,
            "Lactancia" to Lactancia)

        //Declaro el linearLayout
        var ly = findViewById<LinearLayout>(R.id.CONTENIDO)

        val elements = contenido.get(modulo)
        for (element in elements!!){
            if (element.contains("mp4")){
                ly.addView(CreateButtonView(element.replace(".mp4", "")))
            }else if (element.contains("txt")){
                ly.addView(CreateButtonViewTxt(element.replace(".txt", "")))
            }else if (element.contains(".mp3")){
                ly.addView(CreateButtonViewAudio(element.replace(".mp3", "")))
            }
        }

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

    private fun CreateButtonView(title: String): View {
        val Inflater = LayoutInflater.from(this)
        val view: View = Inflater.inflate(R.layout.button_video, null)

        val txtVideo = view.findViewById<TextView>(R.id.titleButtonVideo)
        txtVideo.text = title.replace("_", " ")

        val image = view.findViewById<ShapeableImageView>(R.id.imageButtonVideo)

        // Check if a matching image resource exists based on title
        val resourceId = resources.getIdentifier(title, "drawable", packageName)
        if (resourceId != 0) {
            image.setImageResource(resourceId)
        } else {
            // Handle case where there's no matching image
            // (e.g., set a default image or log a warning)
        }

        view.setOnClickListener {
            val intent = Intent(this, video_landscape_reproductor::class.java)
            intent.putExtra("modulo", modulo)
            intent.putExtra("name_video", title)
            startActivity(intent)
            finish()
        }

        return view
    }

    private fun CreateButtonViewTxt(txt: String): View {
        val Inflater = LayoutInflater.from(this)
        val view: View = Inflater.inflate(R.layout.button_txt, null)

        val textoShow = view.findViewById<TextView>(R.id.textoShowButton)
        textoShow.text = txt

        return view
    }

    private fun CreateButtonViewAudio(audio: String): View {
        val Inflater = LayoutInflater.from(this)
        val view: View = Inflater.inflate(R.layout.button_audio, null)

        //val textoShow = view.findViewById<TextView>(R.id.textoShowButton)
        //textoShow.text = txt
        val resourceId = resources.getIdentifier(audio, "raw", packageName)
        val mediaPlayerAudio = MediaPlayer.create(this, resourceId) // Reproducir audio desde la carpeta raw
        val imageAudio : ImageView = view.findViewById(R.id.imageAudio)
        val audio = view.findViewById<ConstraintLayout>(R.id.buttonAudio)
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

        return view
    }
}

