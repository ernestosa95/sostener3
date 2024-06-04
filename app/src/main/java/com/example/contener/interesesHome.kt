package com.example.contener

import android.content.Intent
import android.graphics.drawable.ShapeDrawable
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.view.WindowManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.LinearLayout.SHOW_DIVIDER_MIDDLE
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.contener.Home
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore


class interesesHome : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intereses_home)

        getSupportActionBar()?.hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen

        val auth = Firebase.auth
        val user = auth.currentUser
        var email = ""
        if (user != null) {

            email = user.email.toString()

        }

        val myPreferences = PreferenceManager.getDefaultSharedPreferences(this@interesesHome)
        val name: String? = myPreferences.getString("names", "unknown")
        Toast.makeText(this, name, Toast.LENGTH_SHORT).show()

        //Creo las opciones
        val options: MutableList<String> = mutableListOf<String>()
        options.add("Embarazo")
        options.add("Parto")
        options.add("Puerperio o postparto")
        options.add("Lactancia")
        options.add("Ansiedad o estres")
        options.add("Leyes")
        options.add("Emociones")
        options.add("Ejercicios")
        options.add("Respiracion")
        options.add("Cuidados del bebe")
        options.add("Crianza")

        val CLopts: LinearLayout = findViewById<LinearLayout>(R.id.interesesLinearLayout)

        val cheks: MutableList<CheckBox> = mutableListOf<CheckBox>()

        for (opt in options) {

            val divider = View(this)
            divider.id = R.id.divider  // Set the id
            divider.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                15  // Set height to 1dp (using dp extension)
            )
            divider.setBackgroundColor(this.getColor(R.color.background1))  // Set background color

            // Add the divider to your view hierarchy (replace CLopts with your actual ViewGroup)
            CLopts.addView(divider)

            val aux_opt = CheckBox(this)
            cheks.add(aux_opt)
            aux_opt.setBackgroundColor(getColor(R.color.background2))
            aux_opt.setTextSize(18F)
            aux_opt.text = opt
            aux_opt.setOnClickListener{
                var i = 0
                for (check in cheks){
                    if (check.isChecked){
                        i += 1
                    }
                }
                if (i<3){
                    for (check in cheks){
                        if (!check.isChecked){
                            check.isEnabled = true
                        }
                    }
                }else if (i == 3){
                    for (check in cheks){
                        if (!check.isChecked){
                            check.isEnabled = false
                        }
                    }
                }
            }
            CLopts.addView(aux_opt)

        }

        val cierre = TextView(this)
        cierre.text = ""
        CLopts.addView(cierre)

        val guardar = findViewById<Button>(R.id.guardarBoton)
        guardar.setOnClickListener{

            val preferences: MutableList<String> = mutableListOf<String>()
            var i = 0
            for (check in cheks){
                if (check.isChecked){
                    i += 1
                    preferences.add(check.text.toString())
                }
            }

            if (i==3) {
                db.collection("preferences").document(email).set(
                    hashMapOf(
                        "1" to preferences[0],
                        "2" to preferences[1],
                        "3" to preferences[2],
                    )
                )

                val myPreferences = PreferenceManager.getDefaultSharedPreferences(this@interesesHome)
                val myEditor = myPreferences.edit()
                myEditor.putString("p1", preferences[0]);
                myEditor.putString("p2", preferences[1]);
                myEditor.putString("p3", preferences[2]);
                myEditor.commit();

                val intent = Intent(this, HomePrincipal::class.java)
                startActivity(intent)
                finish()
            }else{
                Toast.makeText(this, "Debes seleccionar 3 intereses", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, Home::class.java)
        startActivity(intent)
        finish()
    }
}