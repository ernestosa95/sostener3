package com.example.contener

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class formularioHome : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario_home)

        getSupportActionBar()?.hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen

        // pellenado del formulario

        val auth = Firebase.auth
        val user = auth.currentUser

        // Definicion formularios
        val npText : EditText = findViewById<EditText>(R.id.npET)
        val bdDate : TextView = findViewById(R.id.date)
        val siHijos : CheckBox = findViewById(R.id.siHijos)
        val noHijos : CheckBox = findViewById(R.id.noHijos)
        val cantidadHijos : EditText = findViewById(R.id.hijosCuantos)

        bdDate.setOnClickListener(View.OnClickListener {
            FechaPicker(bdDate)
        })

        //Nombre y apellido
        var email : String = ""
        var uid : String = ""
        var names : String = ""
        var birthdate : String = ""
        var sex : String = ""
        var hijos : String = ""
        var canHijos : String = "0"
        var fechaUltimoNacimiento = ""
        var embarazo : String = "NO"
        var depto : String = ""
        var localidad : String = ""
        var otraLocalidad : String = ""


        if (user != null) {
            npText.setText(user.displayName)
            names = user.displayName.toString()
            email = user.email.toString()
            uid = user.uid

            val txt = user.photoUrl
        }

        // Siguiente Button

        val siguienteButton = findViewById<Button>(R.id.siguienteFormHomeButton)
        siguienteButton.setOnClickListener {

            //Obtengo los valores de la fecha
            birthdate = bdDate.text.toString()
            sex = "M"
            if (siHijos.isChecked){
                hijos = "SI"
                if (cantidadHijos.text.toString().isNotEmpty()) {canHijos = cantidadHijos.text.toString()}
            }else if (noHijos.isChecked){hijos = "NO"}else{
                Toast.makeText(this, "Debe indicar si tiene hijos o no", Toast.LENGTH_SHORT).show()
            }



            db.collection("users").document(email).set(
                hashMapOf(
                    "uid" to uid,
                    "names" to names,
                    "bithdate" to birthdate,
                    "sex" to sex,
                    "hijos" to hijos,
                    "cantidad_hijos" to canHijos,
                    "ultimo_hijo" to fechaUltimoNacimiento,
                    "embarazo" to embarazo,
                    "departamento" to depto,
                    "localidad" to localidad,
                    "otraLocalidad" to otraLocalidad
                )
            )

            /*val intent = Intent(this, interesesHome::class.java)
            startActivity(intent)
            finish()*/
        }
    }

    private fun FechaPicker(date: TextView) {
        val builder = AlertDialog.Builder(this)
        val Inflater = LayoutInflater.from(this)
        val view1: View = Inflater.inflate(R.layout.basic_dialog_date, null)
        builder.setView(view1)
        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.show()
        val calendarView = view1.findViewById<DatePicker>(R.id.calendarViewDate)
        val save = view1.findViewById<Button>(R.id.GUARDARDATE)
        save.setOnClickListener {
            val year = calendarView.year
            val month = calendarView.month + 1
            val day = calendarView.dayOfMonth
            //ultimoControl.setText(Integer.toString(day)+"/"+Integer.toString(month)+"/"+Integer.toString(year));
            date.text =
                Integer.toString(day) + "/" + Integer.toString(month) + "/" + Integer.toString(
                    year
                )
            //Toast.makeText(getBaseContext(), auxFecha[0] , Toast.LENGTH_SHORT).show();
            dialog.dismiss()
        }
        val cancel = view1.findViewById<Button>(R.id.CANCELARDATE)
        cancel.setOnClickListener { dialog.dismiss() }
    }
}