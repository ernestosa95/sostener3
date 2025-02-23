package com.example.contener

import android.content.ContentValues
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.WindowManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

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