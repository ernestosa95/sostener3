package com.example.contener

import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader


class formularioHome : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    private val DB_PATH = "/app/src/main/assets/database/"
    private val DB_NAME = "data.sqlite"
    @RequiresApi(Build.VERSION_CODES.O)
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
        val sexOptions : Spinner = findViewById<Spinner>(R.id.SPDGralMultipleSelection)
        val siHijos : RadioButton = findViewById(R.id.siHijos)
        val noHijos : RadioButton = findViewById(R.id.noHijos)
        val cantidadHijos : EditText = findViewById(R.id.hijosCuantos)
        cantidadHijos.setEnabled(false)
        val dateLastSon : TextView = findViewById(R.id.dateLastSon)

        val siEmbarazo : RadioButton = findViewById(R.id.siEmbarazo)
        val noEmbarazo : RadioButton = findViewById(R.id.noEmbarazo)
        val noloseEmbarazo : RadioButton = findViewById(R.id.noloseEmbarazo)

        noHijos.setOnClickListener {
            cantidadHijos.isEnabled = false
        }

        bdDate.setOnClickListener(View.OnClickListener {
            FechaPicker(bdDate)
        })

        dateLastSon.setOnClickListener(View.OnClickListener {
            if (siHijos.isChecked){
                FechaPicker(dateLastSon)
            }else{
                Toast.makeText(this, "Debe indicar que tiene hijos", Toast.LENGTH_SHORT).show()
            }
        })

        //Opciones del spiner

        //Opciones del spiner
        val Opc = ArrayList<String>()
        Opc.add("- seleccionar -")
        Opc.add("FEMENINO")
        Opc.add("MASCULINO")
        Opc.add("X")

        // Cargo el spinner con los datos

        // Cargo el spinner con los datos
        val comboAdapterCocinar = ArrayAdapter<String>(this, R.layout.spiner_personalizado, Opc)
        sexOptions.adapter = comboAdapterCocinar

        //Nombre y apellido
        var email : String = ""
        var uid : String = ""
        var names : String = ""
        var birthdate : String = ""
        var sex : String = ""
        var hijos : String = ""
        var canHijos : String = "0"
        var fechaUltimoNacimiento = "DD/MM/AAAA"
        var embarazo : String = ""
        var depto : String = ""
        var localidad : String = ""
        var otraLocalidad : String = ""

        siHijos.setOnClickListener {
            cantidadHijos.setEnabled(true)
        }

        noHijos.setOnClickListener {
            fechaUltimoNacimiento = "DD/MM/AAAA"
            dateLastSon.setText("DD/MM/AAAA")
            canHijos = "0"
            cantidadHijos.setText("0")
        }

        if (user != null) {
            npText.setText(user.displayName)
            names = user.displayName.toString()
            email = user.email.toString()
            uid = user.uid

            val txt = user.photoUrl
        }

        //Localidades
        val valores = leerCSV()
        var deptos_data = arrayOf<String?>()
        var localidades_data = arrayOf<String?>()

        val aux = deptos_data.toMutableList()
        val aux_loca = localidades_data.toMutableList()
        for (valo in valores){

            aux.add(valo[1])
            aux_loca.add(valo[0])

        }
        val unique = HashSet(aux)
        deptos_data = unique.toTypedArray()

        // Deptos
        val deptos = findViewById<AutoCompleteTextView>(R.id.autoDeptos)

        val adapter: ArrayAdapter<*> = ArrayAdapter<Any?>(
            this,
            com.google.android.material.R.layout.support_simple_spinner_dropdown_item,
            deptos_data
        )
        deptos.setAdapter(adapter)
        deptos.threshold = 1

        // Localidades
        val localidades = findViewById<AutoCompleteTextView>(R.id.autoLocalidades)
        deptos.setOnItemClickListener { adapterView, view, i, l ->
            localidades.setEnabled (true)
        }

        localidades.setOnFocusChangeListener{ view: View, b: Boolean ->
            if ( deptos.text.isNotEmpty()){
                localidades.setEnabled (true)
                //TODO: filtrar por departamentos
                //Toast.makeText(this, "localidades ok", Toast.LENGTH_SHORT).show()
                val aux_loca_new = localidades_data.toMutableList()
                for (valo in valores){
                    //Log.e(valo[1], valo[0] +"-"+deptos.text)
                    if(valo[1] == deptos.text.toString()) {
                        aux_loca_new.add(valo[0])
                        //Log.e(valo[1], valo[0])
                    }
                }
                val adapter_localidades : ArrayAdapter<*> = ArrayAdapter<Any?>(
                    this,
                    com.google.android.material.R.layout.support_simple_spinner_dropdown_item,
                    aux_loca_new.toTypedArray()
                )
                localidades.setAdapter(adapter_localidades)
                localidades.threshold = 1
            }else{
                localidades.setEnabled (false)
                Toast.makeText(this, "Debe seleccionar el departamento", Toast.LENGTH_SHORT).show()
            }
        }

        // Siguiente Button
        val siguienteButton = findViewById<Button>(R.id.siguienteFormHomeButton)
        siguienteButton.setOnClickListener {

            //Obtengo los valores de la fecha
            birthdate = bdDate.text.toString()
            sex = "M"
            if (siHijos.isChecked){
                hijos = "SI"
                if (cantidadHijos.text.toString() != "") {canHijos = cantidadHijos.text.toString()}
                if (dateLastSon.text.toString() != "DD/MM/AAAA"){fechaUltimoNacimiento = dateLastSon.text.toString()}
            }else if (noHijos.isChecked){
                hijos = "NO"
            }
            sex = sexOptions.selectedItem.toString()

            if (siEmbarazo.isChecked){embarazo = "SI"}else if (noEmbarazo.isChecked){embarazo = "NO"}else if (noloseEmbarazo.isChecked){embarazo = "NO LO SE"}

            if (birthdate!="DD/MM/AAAA"){
                if (sexOptions.selectedItem.toString() != "- seleccionar -") {
                    if (!hijos.equals("")) {
                        if (hijos == "SI" && canHijos != "0" && fechaUltimoNacimiento != "DD/MM/AAAA" || hijos == "NO") {
                            if (embarazo != "") {
                                db.collection("users").document(email).set(
                                    hashMapOf(
                                        "uid" to uid,
                                        "names" to names,
                                        "birthdate" to birthdate,
                                        "sex" to sex,
                                        "hijos" to hijos,
                                        "cantidad_hijos" to canHijos,
                                        "ultimo_hijo" to fechaUltimoNacimiento,
                                        "embarazo" to embarazo,
                                        "departamento" to deptos.text.toString(),
                                        "localidad" to localidades.text.toString(),
                                        "otraLocalidad" to otraLocalidad
                                    )
                                )

                                val myPreferences = PreferenceManager.getDefaultSharedPreferences(this@formularioHome)
                                val myEditor = myPreferences.edit()
                                myEditor.putString("uid", uid);
                                myEditor.putString("names", names);
                                myEditor.putString("birthdate", birthdate);
                                myEditor.putString("sex", sex);
                                myEditor.putString("hijos", hijos);
                                myEditor.putString("cantidad_hijos", canHijos);
                                myEditor.putString("ultimo_hijo", fechaUltimoNacimiento);
                                myEditor.putString("embarazo", embarazo);
                                myEditor.putString("departamento", deptos.text.toString());
                                myEditor.putString("localidad", localidades.text.toString());
                                myEditor.putString("otralocalidad", otraLocalidad);
                                myEditor.commit();

                                val intent = Intent(this, interesesHome::class.java)
                                startActivity(intent)
                                finish()
                            }else{Toast.makeText(this, "Indique situacion de embarazo", Toast.LENGTH_SHORT).show()}
                        }else{
                            if (canHijos == "0"){Toast.makeText(this, "Indique la cantidad de hijos", Toast.LENGTH_SHORT).show()}
                            if (fechaUltimoNacimiento == "DD/MM/AAAA"){Toast.makeText(this, "Indique la fecha de nacimiento del ultimo hijo", Toast.LENGTH_SHORT).show()}
                        }
                    }else{Toast.makeText(this, "Debe indicar si tiene hijos", Toast.LENGTH_SHORT).show()}
                }else{Toast.makeText(this, "Seleccione el sexo", Toast.LENGTH_SHORT).show()}
            }else{Toast.makeText(this, "Falta la fecha de nacimiento", Toast.LENGTH_SHORT).show()}

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

    fun leerCSV(): List<List<String>> {
        val lineas = mutableListOf<List<String>>()
        //val lector = BufferedReader(FileReader(this.packageResourcePath.))
        val inputStream: InputStream = this.resources.openRawResource(R.raw.data)
        val lector = BufferedReader(InputStreamReader(inputStream, Charsets.UTF_8))

        try {
            var linea: String? = null
            while (lector.readLine().also { linea = it } != null) {
                val valoresLinea = linea!!.split(",")
                //Log.e("marcador data", linea.toString())
                lineas.add(valoresLinea.map { it.trim() })
            }
        } finally {
            lector.close()
        }

        return lineas
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, Home::class.java)
        startActivity(intent)
        finish()
    }
}