package com.example.contener

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class Home : AppCompatActivity() {

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        getSupportActionBar()?.hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen

        val myPreferences = PreferenceManager.getDefaultSharedPreferences(this@Home)

        mAuth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        val textView = findViewById<TextView>(R.id.binvenidoHome)

        val auth = Firebase.auth
        val user = auth.currentUser

        if (user != null) {
            val userName = user.displayName
            textView.text = "Hola, " + userName
        } else {
            // Handle the case where the user is not signed in
            signOutAndStartSignInActivity()
            Toast.makeText(this, "Debemos pedirte que te vuelvas a loguear", Toast.LENGTH_SHORT).show()
        }

        //Epecemos Boton
        val empecemosButton = findViewById<Button>(R.id.empecemosButton)
        empecemosButton.setOnClickListener {
            if (myPreferences.getString("sex", "unknown").equals("unknown") || myPreferences.getString("p1", "unknown").equals("unknown")) {
                val intent = Intent(this, formularioHome::class.java)
                startActivity(intent)
                finish()
            }else{
                val intent = Intent(this, HomePrincipal::class.java)
                startActivity(intent)
                finish()
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
        signOutAndStartSignInActivity()
    }
}