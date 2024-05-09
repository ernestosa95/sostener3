package com.example.contener

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.WindowManager
import android.widget.Toast

class HomePrincipal : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_principal)

        getSupportActionBar()?.hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen

        val myPreferences = PreferenceManager.getDefaultSharedPreferences(this@HomePrincipal)
        Toast.makeText(this, myPreferences.getString("p1", "unknown"), Toast.LENGTH_SHORT).show()
    }
}