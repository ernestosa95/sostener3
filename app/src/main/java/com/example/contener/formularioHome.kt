package com.example.contener

import android.app.AlertDialog
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.RadioButton
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.marginBottom
import com.example.contener.Home
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader


class formularioHome : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    var adminBDData: BDData? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario_home)

        getSupportActionBar()?.hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen

        // pellenado del formulario

        // Evitar la rotacion
        if (this.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED
        }

        val auth = Firebase.auth
        val user = auth.currentUser

        //Base de datos
        adminBDData = BDData(baseContext, "BDData", null, 1)

        // Definicion formularios
        val npText : EditText = findViewById(R.id.npET)
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
        val deptos = findViewById<AutoCompleteTextView>(R.id.autoDeptos)
        val localidades = findViewById<AutoCompleteTextView>(R.id.autoLocalidades)

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
        Opc.add("MUJER")
        Opc.add("VARON")
        Opc.add("SIN DEFINIR")
        Opc.add("PREFIERO NO DECIRLO")

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

        //
        val dataUserCache = adminBDData!!.getDataUser(auth.uid)

        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val auxs : String = "Nombre y Apellido"
        npText.setText(auxs)
        if (dataUserCache["NAMES"] !=""){
            npText.setText(dataUserCache["NAMES"])
        }
        //Toast.makeText(this, preferences.getString("names", "").toString(), Toast.LENGTH_SHORT).show()
        if (dataUserCache["BIRTHDATE"]!="" && dataUserCache["BIRTHDATE"]!=null){
            bdDate.text = dataUserCache["BIRTHDATE"]
        }
        if (dataUserCache["CHILDRENQUANTITY"]!="" && dataUserCache["CHILDRENQUANTITY"]!=null){
            cantidadHijos.setText(dataUserCache["CHILDRENQUANTITY"].toString())
        }
        if (dataUserCache["STATE"]!=""){
            deptos.setText(dataUserCache["STATE"])
        }
        if (dataUserCache["CITY"]!=""){
            localidades.setText(dataUserCache["CITY"])
        }

        if (dataUserCache["DATEBORNLASTSON"]!=""){
            dateLastSon.text = dataUserCache["DATEBORNLASTSON"]
        }
        if (dataUserCache["PREGNANT"]!=""){
            if (dataUserCache["PREGNANT"]=="SI"){
                siEmbarazo.isChecked = true
            }else if (dataUserCache["PREGNANT"]=="SI"){
                noEmbarazo.isChecked = true
            }else{
                noloseEmbarazo.isChecked = true
            }
        }
        if (dataUserCache["HAVECHILDRENS"]!=""){
            if (dataUserCache["HAVECHILDRENS"]=="SI"){
                siHijos.isChecked = true
            }else{
                noHijos.isChecked = true
            }
        }
        if (dataUserCache["SEX"]!=""){
            if (dataUserCache["SEX"]=="FEMENINO"){
                sexOptions.setSelection(1)
            }else if (dataUserCache["SEX"]=="MASCULINO"){
                sexOptions.setSelection(2)
            }else{
                sexOptions.setSelection(3)
            }
        }

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
            if (user.displayName!=null) {
                npText.setText(user.displayName)
                names = user.displayName.toString()
            }
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


        val adapter: ArrayAdapter<*> = ArrayAdapter<Any?>(
            this,
            com.google.android.material.R.layout.support_simple_spinner_dropdown_item,
            deptos_data
        )
        deptos.setAdapter(adapter)
        deptos.threshold = 1

        // Localidades

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

            if (birthdate!="DD/MM/AAAA" && birthdate.length!=0){
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
                                        "otraLocalidad" to otraLocalidad,
                                        "TERM" to "acepto"
                                    )
                                ).addOnSuccessListener { documentReference ->
                                    Log.d(TAG, "DocumentSnapshot added with ID:${documentReference}")
                                    // Aquí puedes mostrar un mensaje de éxito al usuario
                                    Toast.makeText(this, "Datos guardados correctamente", Toast.LENGTH_SHORT).show()
                                }
                                    .addOnFailureListener { e ->
                                        Log.w(TAG, "Error adding document", e)
                                        // Aquí puedes mostrar un mensaje de error al usuario
                                        Toast.makeText(this, "Error al guardar los datos", Toast.LENGTH_SHORT).show()
                                    }


                                val dataUser : ContentValues = ContentValues()
                                dataUser.put("NAMES", names)
                                dataUser.put("UID", uid)
                                dataUser.put("BIRTHDATE", birthdate)
                                dataUser.put("CHILDRENQUANTITY", canHijos)
                                dataUser.put("STATE", deptos.text.toString())
                                dataUser.put("PREGNANT", embarazo)
                                dataUser.put("HAVECHILDRENS", hijos)
                                dataUser.put("CITY", localidades.text.toString())
                                dataUser.put("OHTERCITY", "")
                                dataUser.put("SEX", sex)
                                dataUser.put("DATEBORNLASTSON", fechaUltimoNacimiento)
                                dataUser.put("TERM", "acepto")
                                adminBDData!!.updateDataUser(dataUser)

                                val intent = Intent(this, HomePrincipal::class.java)
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