package com.example.contener

import android.content.ContentValues
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.WindowManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.random.Random

class Home : AppCompatActivity() {

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var mAuth: FirebaseAuth

    var adminBDData: BDData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        getSupportActionBar()?.hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen


        // Evitar la rotacion
        if (this.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED
        }

        val myPreferences = PreferenceManager.getDefaultSharedPreferences(this@Home)

        mAuth = FirebaseAuth.getInstance()

        //Base de datos
        adminBDData = BDData(baseContext, "BDData", null, 1)
        val dataUser = adminBDData!!.getDataUser(mAuth.uid)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        val textView = findViewById<TextView>(R.id.binvenidoHome)
        textView.text = "Hola, "+dataUser.get("NAMES")+"!"

        val auth = Firebase.auth
        val user = auth.currentUser

        val db = FirebaseFirestore.getInstance()
        val collectionRef = db.collection("users")

        var userName : String = ""
        var sex : String = ""
        val checkTerminos = findViewById<CheckBox>(R.id.terminoscondiciones)

        //Epecemos Boton
        val empecemosButton = findViewById<Button>(R.id.empecemosButton)
        empecemosButton.setOnClickListener {
            if (checkTerminos.isChecked) {

                if (user != null) {
                    //Toast.makeText(this, adminBDData!!.getDataUser(user.uid)["BIRTHDATE"] + "666", Toast.LENGTH_SHORT ).show()
                    if (adminBDData!!.getDataUser(user.uid)["BIRTHDATE"]?.length == 0 || adminBDData!!.getDataUser(
                            user.uid
                        )["BIRTHDATE"].isNullOrBlank()
                    ) {
                        //if (myPreferences.getString("sex", "unknown").equals("unknown") || myPreferences.getString("p1", "unknown").equals("unknown") || sex == "") {
                        //val myEditor = myPreferences.edit()
                        //myEditor.putString("names", userName);
                        //myEditor.commit();
                        val intent = Intent(this, formularioHome::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        val intent = Intent(this, HomePrincipal::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }else{
                Toast.makeText(this, "Debe aceptar los terminos y condiciones para avanzar", Toast.LENGTH_LONG).show()
            }
        }

        // Lógica para mostrar el pop-up del video de bienvenida
        showWelcomeVideoPopup()
    }

    private fun signOutAndStartSignInActivity() {
        mAuth.signOut()

        mGoogleSignInClient.signOut().addOnCompleteListener(this) {
            // Optional: Update UI or show a message to the user
            val intent = Intent(this@Home, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun showWelcomeVideoPopup() {
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val hasShownWelcomeVideo = preferences.getBoolean("hasShownWelcomeVideo", false)

        // Solo muestra el video si no se ha mostrado antes
        if (!hasShownWelcomeVideo) {
            val dialogBuilder = AlertDialog.Builder(this)
            val inflater = this.layoutInflater
            val dialogView = inflater.inflate(R.layout.dialog_welcome_video, null)
            dialogBuilder.setView(dialogView)

            val videoView = dialogView.findViewById<VideoView>(R.id.welcome_video_view)
            val btnClose = dialogView.findViewById<Button>(R.id.btn_close_video)

            // --- Lógica para seleccionar un video aleatorio ---
            val videoResources = listOf(R.raw.welcome_video_1, R.raw.welcome_video_2) // Lista de tus videos
            val randomIndex = Random.nextInt(videoResources.size) // Genera un índice aleatorio (0 o 1)
            val selectedVideoResId = videoResources[randomIndex] // Obtiene el ID del recurso del video seleccionado

            val videoPath = "android.resource://" + packageName + "/" + selectedVideoResId
            val uri = Uri.parse(videoPath)
            // --- Fin de la lógica de selección aleatoria ---

            videoView.setVideoURI(uri)
            videoView.start() // Inicia la reproducción automáticamente

            // Opcional: Para que el video se repita
            videoView.setOnPreparedListener { mp ->
                mp.isLooping = true
            }

            val alertDialog = dialogBuilder.create()
            alertDialog.setCancelable(false) // Evita que se cierre al tocar fuera

            btnClose.setOnClickListener {
                videoView.stopPlayback() // Detiene la reproducción del video
                alertDialog.dismiss() // Cierra el pop-up
            }

            alertDialog.show()

            // Marca que el video de bienvenida ya ha sido mostrado
            preferences.edit().putBoolean("hasShownWelcomeVideo", true).apply()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        val dataUser : ContentValues = ContentValues()
        dataUser.put("UID", mAuth.uid)
        dataUser.put("ACTIVE", false)
        adminBDData!!.updateDataUser(dataUser)

        signOutAndStartSignInActivity()
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        preferences.edit().clear().apply()


    }
}