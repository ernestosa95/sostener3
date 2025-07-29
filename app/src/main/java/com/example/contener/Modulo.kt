package com.example.contener

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.imageview.ShapeableImageView

// ... otras importaciones

class Modulo : AppCompatActivity() {

    var modulo : String = ""

    // Usaremos un Map para almacenar los MediaPlayer y su estado, facilitando la gestión
    // val audiosMap: MutableMap<String, MediaPlayer> = mutableMapOf()
    // Si solo necesitas el MediaPlayer, un Set está bien, pero para controlar el estado es mejor un Map
    val activeMediaPlayers: MutableSet<MediaPlayer> = mutableSetOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modulo)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        // Evitar la rotacion
        if (this.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED
        }

        supportActionBar?.hide() // hide the title bar
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN) //enable full screen

        modulo = intent.getStringExtra("modulo").toString()
        val title : TextView = findViewById(R.id.textView38)
        title.text = modulo

        //Declaracion de recursos para los modulos
        // val recursos = resources // No se usa directamente aquí, se accede a través de 'resources'

        // MODULO DERECHOS
        val Derechos = arrayOf("derechos_durante_el_nacimiento.mp4")

        // MODULO EMBARAZO
        val Embarazo = arrayOf("embarazo_y_lactancia.mp4")

        //MODULO PARTO
        val Parto = arrayOf("") // ¿Está vacío intencionadamente?

        // MODULO POSTPARTO
        val Posparto = arrayOf("podcast_depresion_postparto.mp3")

        //MODULO LACTANCIA
        val Lactancia = arrayOf("extraccion_de_leche.mp4", "primeros_dias.mp4")

        val contenido = mapOf("Derechos" to Derechos,
            "Posparto" to Posparto,
            "Parto" to Parto,
            "Embarazo" to Embarazo,
            "Lactancia" to Lactancia)

        //Declaro el linearLayout
        val ly = findViewById<LinearLayout>(R.id.CONTENIDO)

        val elements = contenido[modulo]
        elements?.let { // Usar 'let' para un manejo seguro de nulos
            for (element in it){
                when {
                    element.contains("mp4") -> ly.addView(CreateButtonView(element.replace(".mp4", "")))
                    element.contains("txt") -> ly.addView(CreateButtonViewTxt(element.replace(".txt", "")))
                    element.contains(".mp3") -> ly.addView(CreateButtonViewAudio(element.replace(".mp3", "")))
                }
            }
        }

        if (modulo == "Embarazo") { // Usar '==' para comparación de contenido de String
            val inflater = LayoutInflater.from(this)
            val view: View = inflater.inflate(R.layout.button_dowdonald, null)

            val plandeparto = view.findViewById<ConstraintLayout>(R.id.planparto)
            plandeparto.setOnClickListener {
                val url = "https://docs.google.com/document/d/1ngX69WftCLy02mDWwFHrlkMvnxP4Vh3kulZsEEkZk3A/edit?usp=sharing"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            }

            ly.addView(view)
        }

        // Este bloque parece ser un botón global para video y audio en la actividad.
        // Si es un reproductor fijo que siempre aparece, está bien.
        // Si el video y audio aquí son ejemplos o deberían ser parte del contenido dinámico,
        // entonces esto podría duplicar funcionalidades o confundir.
        // Por ahora, asumiré que son reproductores fijos.

        val videoA : ConstraintLayout = findViewById(R.id.video1)
        videoA.setOnClickListener {
            val intent = Intent(this, video_landscape_reproductor::class.java)
            intent.putExtra("modulo", modulo) // Ya es un String, no necesita .toString()
            startActivity(intent)
            finish()
        }

        val audio : ConstraintLayout = findViewById(R.id.audioCL)
        val mediaPlayerAudio = MediaPlayer.create(this, R.raw.audio) // Reproducir audio desde la carpeta raw
        activeMediaPlayers.add(mediaPlayerAudio) // Añadirlo al conjunto para gestionarlo
        val imageAudio : ImageView = findViewById(R.id.imageAudio)

        var status = "p" // 'p' de paused, 's' de started
        audio.setOnClickListener {
            if (mediaPlayerAudio.isPlaying) { // Es mejor verificar el estado actual del MediaPlayer
                mediaPlayerAudio.pause()
                imageAudio.setImageResource(android.R.drawable.ic_media_play)
            } else {
                mediaPlayerAudio.start()
                imageAudio.setImageResource(android.R.drawable.ic_media_pause)
            }
        }
    }

    // Estas funciones toggleFullscreen, enterFullscreen, exitFullscreen no se están usando
    // en el código proporcionado. Si no se usan, pueden ser eliminadas.
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
        // Cuando se presiona atrás, liberamos todos los reproductores de audio
        releaseAllMediaPlayers()
        val intent = Intent(this, HomePrincipal::class.java)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        // ¡Importante! Liberar los recursos de MediaPlayer aquí
        releaseAllMediaPlayers()
    }

    // Función auxiliar para liberar todos los MediaPlayers
    private fun releaseAllMediaPlayers() {
        for (player in activeMediaPlayers) {
            if (player.isPlaying) {
                player.stop() // Detener si está reproduciendo
            }
            player.release() // Liberar recursos
        }
        activeMediaPlayers.clear() // Limpiar el conjunto
    }

    private fun CreateButtonView(title: String): View {
        val inflater = LayoutInflater.from(this)
        val view: View = inflater.inflate(R.layout.button_video, null)

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
            Log.w("Modulo", "No se encontró imagen para el video: $title")
            // image.setImageResource(R.drawable.default_video_thumbnail) // Ejemplo de default
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
        val inflater = LayoutInflater.from(this)
        val view: View = inflater.inflate(R.layout.button_txt, null)

        val textoShow = view.findViewById<TextView>(R.id.textoShowButton)
        textoShow.text = txt.replace("_", " ") // Asumiendo que el nombre del archivo es el texto

        return view
    }

    private fun CreateButtonViewAudio(audioString: String): View {
        val inflater = LayoutInflater.from(this)
        val view: View = inflater.inflate(R.layout.button_audio, null)

        val resourceId = resources.getIdentifier(audioString, "raw", packageName)
        if (resourceId == 0) {
            Log.e("Modulo", "Recurso de audio no encontrado para: $audioString")
            // Puedes retornar una vista vacía o con un mensaje de error si el audio no existe
            val errorTextView = TextView(this)
            errorTextView.text = "Error: Audio '$audioString' no encontrado."
            return errorTextView
        }

        val mediaPlayerAudio = MediaPlayer.create(this, resourceId)
        activeMediaPlayers.add(mediaPlayerAudio) // Añadirlo al conjunto para gestión
        val imageAudio : ImageView = view.findViewById(R.id.imageAudio)
        val audio = view.findViewById<ConstraintLayout>(R.id.buttonAudio)
        val audioTitle = view.findViewById<TextView>(R.id.txtTitleAudio)
        audioTitle.text = audioString.replace(".mp3","").replace("_", " ")

        audio.setOnClickListener {
            if (mediaPlayerAudio.isPlaying) {
                mediaPlayerAudio.pause()
                imageAudio.setImageResource(android.R.drawable.ic_media_play)
            } else {
                // Reiniciar o preparar el MediaPlayer si ya ha terminado
                if (mediaPlayerAudio.currentPosition == mediaPlayerAudio.duration) {
                    mediaPlayerAudio.seekTo(0)
                }
                mediaPlayerAudio.start()
                imageAudio.setImageResource(android.R.drawable.ic_media_pause)
            }
        }

        // Es importante liberar el MediaPlayer cuando termina de reproducirse para no mantenerlo activo
        mediaPlayerAudio.setOnCompletionListener {
            imageAudio.setImageResource(android.R.drawable.ic_media_play) // Volver al icono de play
            // Opcional: podrías considerar removerlo del activeMediaPlayers si sabes que no se volverá a usar
            // activeMediaPlayers.remove(mediaPlayerAudio)
            // mediaPlayerAudio.release() // Si se libera aquí, no se podrá volver a reproducir
            // Generalmente, es mejor liberarlos todos en onDestroy
        }

        return view
    }
}

