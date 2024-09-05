package com.example.contener

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.webkit.WebView
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import java.lang.Exception
import java.net.URL


class HomePrincipal : AppCompatActivity() {

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_principal)

        getSupportActionBar()?.hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen

        val myPreferences = PreferenceManager.getDefaultSharedPreferences(this@HomePrincipal)
        //Toast.makeText(this, myPreferences.getString("p1", "unknown"), Toast.LENGTH_SHORT).show()

        // Evitar la rotacion
        if (this.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED
        }

        val perfil = findViewById<ImageView>(R.id.perfilimage)
        auth = Firebase.auth
        val user = auth.currentUser
        if (user != null && user.photoUrl != null) {
            val policy = ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
            val txt = user.photoUrl
            val newurl = URL(txt.toString())
            val mIcon_val = BitmapFactory.decodeStream(newurl.openConnection().getInputStream())
            perfil.setImageBitmap(mIcon_val)
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        val formButton = findViewById<Button>(R.id.formButton)
        formButton.setOnClickListener {
            val intent = Intent(this, formularioHome::class.java)
            startActivity(intent)
            finish()
        }

        val webView : WebView = findViewById(R.id.web);
        val video = "<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/V2KCAfHjySQ?si=qkP3WXXxZEh9EDk-\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" referrerpolicy=\"strict-origin-when-cross-origin\" allowfullscreen></iframe>"
        webView.loadData(video, "text/html", "utf-8")
        webView.settings.javaScriptEnabled = true

        val btnMaternidad : Button = findViewById(R.id.MATERNIDADBTN)
        btnMaternidad.setOnClickListener {
            val intent = Intent(this, Modulo::class.java)
            intent.putExtra("modulo", "Maternidad")
            startActivity(intent)
            finish()
        }

        val btnPosparto : Button = findViewById(R.id.POSPARTOBTN)
        btnPosparto.setOnClickListener {
            val intent = Intent(this, Modulo::class.java)
            intent.putExtra("modulo", "Posparto")
            startActivity(intent)
            finish()
        }

        val btnParto : Button = findViewById(R.id.PARTOBTN)
        btnParto.setOnClickListener {
            val intent = Intent(this, Modulo::class.java)
            intent.putExtra("modulo", "Parto")
            startActivity(intent)
            finish()
        }

        val btnEmbarazo : Button = findViewById(R.id.EMBARAZOBTN)
        btnEmbarazo.setOnClickListener {
            val intent = Intent(this, Modulo::class.java)
            intent.putExtra("modulo", "Embarazo")
            startActivity(intent)
            finish()
        }

        val editData = findViewById<TextView>(R.id.editTXT)
        editData.setOnClickListener {
            val intent = Intent(this, formularioHome::class.java)
            startActivity(intent)
            finish()
        }

        /*val mapsView = findViewById<Button>(R.id.mapsView)
        mapsView.setOnClickListener {

            //https://www.google.com/maps/d/edit?mid=1pZZDnvrZIT2iNzL1qvwJpTzehRkEPeo&usp=sharing
            var url = "https://www.google.com/maps/d/edit?mid=1pZZDnvrZIT2iNzL1qvwJpTzehRkEPeo&usp=sharing"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            this.startActivity(intent)

            // Crear un Intent para abrir Google Maps con la búsqueda predefinida
            val gmmIntentUri = Uri.parse("geo:0,0?q=centros+de+salud")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")

            try {
                startActivity(mapIntent)
            }catch (e : Exception){
                Log.e("error", e.toString() + " e")
                Toast.makeText(this, "No pudimos localizar centros de salud cercanos", Toast.LENGTH_SHORT).show()
            }
        }*/

        val options = findViewById<ImageView>(R.id.optionsBTN)
        options.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val Inflater = LayoutInflater.from(this)
            val view: View = Inflater.inflate(R.layout.basic_alert, null)
            view.isFocusable = true
            builder.setView(view)
            //builder.setCancelable(false)
            val dialog = builder.create()
            dialog.show()

            val mapa = view.findViewById<ConstraintLayout>(R.id.EfectoresCercanosBTN)
            mapa.setOnClickListener {
                var url = "https://www.google.com/maps/d/edit?mid=1pZZDnvrZIT2iNzL1qvwJpTzehRkEPeo&usp=sharing"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                this.startActivity(intent)
            }

            val data_personal = view.findViewById<ConstraintLayout>(R.id.DatosPersonalesBTN)
            data_personal.setOnClickListener {
                val intent = Intent(this, formularioHome::class.java)
                startActivity(intent)
                dialog.dismiss()
                finish()
            }

            val closesesion = view.findViewById<ConstraintLayout>(R.id.CerrarSesionBTN)
            closesesion.setOnClickListener {

                val dataUser : ContentValues = ContentValues()
                dataUser.put("UID", auth.uid)
                dataUser.put("ACTIVE", false)
                var adminBDData: BDData? = null
                //Base de datos
                adminBDData = BDData(baseContext, "BDData", null, 1)
                adminBDData!!.updateDataUser(dataUser)

                signOutAndStartSignInActivity()

            }
        }
    }

    private fun signOutAndStartSignInActivity() {
        auth.signOut()

        mGoogleSignInClient.signOut().addOnCompleteListener(this) {
            // Optional: Update UI or show a message to the user
            val intent = Intent(this@HomePrincipal, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, Home::class.java)
        startActivity(intent)
        finish()
    }
}