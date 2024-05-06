package com.example.contener

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class formularioHome : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario_home)

        getSupportActionBar()?.hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen

        // pellenado del formulario

        val auth = Firebase.auth
        val user = auth.currentUser

        //Nombre y apellido
        val npText : EditText = findViewById<EditText>(R.id.npET)
        if (user != null) {
            npText.setText(user.displayName)
            val txt = user.photoUrl
            // Check if user's email is verified
            val emailVerified = user.isEmailVerified

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            val uid = user.uid
            Log.e("meta", uid.toString())
        }

        // Siguiente Button
        val siguienteButton = findViewById<Button>(R.id.siguienteFormHomeButton)
        siguienteButton.setOnClickListener {
            val intent = Intent(this, interesesHome::class.java)
            startActivity(intent)
            finish()
        }
    }
}