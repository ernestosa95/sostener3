package com.example.contener

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore


enum class ProviderType {
    GOOGLE
}
class MainActivity : AppCompatActivity() {

    companion object {
        private const val RC_SIGN_IN = 9001
    }

    private lateinit var auth: FirebaseAuth
    private lateinit var mauth: FirebaseAuth

    private var callbackManager = CallbackManager.Factory.create()

    private lateinit var signInFacebook: ConstraintLayout

    var adminBDData: BDData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        // Splash

        //setTheme(R.style.AppTheme)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        getSupportActionBar()?.hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        //Base de datos
        adminBDData = BDData(baseContext, "BDData", null, 1)


        val iniciaSesion = findViewById<Button>(R.id.IniciaSesion)
        iniciaSesion.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val Inflater = LayoutInflater.from(this)
            val view: View = Inflater.inflate(R.layout.init_alert, null)
            view.isFocusable = true
            builder.setView(view)
            //builder.setCancelable(false)
            val dialog = builder.create()
            dialog.show()

            val iniciarBtn = view.findViewById<Button>(R.id.ingresarBTN)
            val user = view.findViewById<EditText>(R.id.userEDT)
            val pass = view.findViewById<EditText>(R.id.passEDT)
            iniciarBtn.setOnClickListener {
                if (user.text.isNotEmpty() && pass.text.isNotEmpty()){
                    initMailandPass(user.text.toString() , pass.text.toString())
                }
            }

            //Reset pssw
            val reset = view.findViewById<TextView>(R.id.resetPssw)
            reset.setOnClickListener {
                pass.visibility = View.GONE
                reset.visibility = View.GONE
                iniciarBtn.text = "Resetear contraseña"
                Toast.makeText(this, "Debe ingresar su correo electrónico", Toast.LENGTH_SHORT).show()

                iniciarBtn.setOnClickListener {
                    if (user.text.isNotEmpty()) {
                        resetPassword(user.text.toString(), dialog)
                    }else{
                        Toast.makeText(this, "Ingrese su email", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            val cancel = view.findViewById<ConstraintLayout>(R.id.cancelInit)
            cancel.setOnClickListener {
                dialog.dismiss()
            }
        }

        val register = findViewById<Button>(R.id.registrate)
        register.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val Inflater = LayoutInflater.from(this)
            val view: View = Inflater.inflate(R.layout.register_alert, null)
            view.isFocusable = true
            builder.setView(view)
            //builder.setCancelable(false)
            val dialog = builder.create()
            dialog.show()

            val registerBtn = view.findViewById<Button>(R.id.registerBTN)
            val correo = view.findViewById<EditText>(R.id.correoEDT)
            val user = view.findViewById<EditText>(R.id.userEDT)
            val pass = view.findViewById<EditText>(R.id.passEDT)
            registerBtn.setOnClickListener {
                if (user.text.isNotEmpty() && pass.text.isNotEmpty() && correo.text.isNotEmpty()){
                    registerMailandPass(user.text.toString() , pass.text.toString(), correo.text.toString(), dialog)
                }
            }

            val cancel = view.findViewById<ConstraintLayout>(R.id.cancelRegistro)
            cancel.setOnClickListener {
                dialog.dismiss()
            }
        }

        if (currentUser != null) {
            // The user is already signed in, navigate to MainActivity
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
            finish() // finish the current activity to prevent the user from coming back to the SignInActivity using the back button
        }

        val signInButton = findViewById<ConstraintLayout>(R.id.googleButton)
        signInButton.setOnClickListener {
            signIn()
        }

        //Login Facebook
        signInFacebook = findViewById(R.id.facebookButton)
        signInFacebook.setOnClickListener {

            LoginManager.getInstance().logInWithReadPermissions(this, listOf("email"))

            LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onCancel() {
                    Log.e("A", "4")
                }

                override fun onError(error: FacebookException) {
                    Log.e("A", "2")
                }

                override fun onSuccess(result: LoginResult) {

                    result?.let {
                        val token = it.accessToken

                        val credential = FacebookAuthProvider.getCredential(token.token)

                        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener {

                            if (it.isSuccessful){

                                val user = auth.currentUser
                                val dataUser : ContentValues = ContentValues()
                                dataUser.put("UID", auth.uid)
                                dataUser.put("NAMES", user!!.displayName)
                                dataUser.put("PROVIDER", "FACEBOOK")
                                dataUser.put("ACTIVE", true)
                                if (adminBDData!!.existsUser(auth.uid)){
                                    adminBDData!!.updateDataUser(dataUser)
                                }else{
                                    adminBDData!!.setDataUser(dataUser)
                                }

                                val intent = Intent(this@MainActivity, Home::class.java)
                                startActivity(intent)
                                finish()
                            }else{
                                signIn()
                                //Toast.makeText(this@MainActivity, "No pudimos iniciar sesion con Facebook, es probable que ya tengas una cuenta", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }

            })

        }



    }

    fun resetPassword(email: String, dialog: AlertDialog ) {

        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful)
                {
                    // Se envió el correo electrónico de restablecimiento de contraseña
                    println("Se envió un correo electrónico de restablecimiento a $email")
                    Toast.makeText(this, "Se envió un correo electrónico de restablecimiento a $email", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                } else {
                    val exception = task.exception
                    when (exception) {
                        is FirebaseAuthInvalidCredentialsException -> {
                            // La dirección de correo electrónico no está formateada correctamente
                            println("Dirección de correo electrónico no válida.")
                        }
                        else -> {
                            // Otro error
                            println("Error al enviar el correo electrónico de restablecimiento: ${exception?.message}")
                        }
                    }
                }
            }
    }




    private fun signIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        callbackManager.onActivityResult(requestCode, resultCode, data)

        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Toast.makeText(this, "Google sign in failed: ${e.toString()}", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    Toast.makeText(this, "Bienvenid@ ${user?.displayName}", Toast.LENGTH_SHORT).show()

                    val dataUser : ContentValues = ContentValues()
                    dataUser.put("UID", auth.uid)
                    dataUser.put("NAMES", user!!.displayName)
                    dataUser.put("PROVIDER", "GOOGLE")
                    dataUser.put("ACTIVE", true)
                    if (adminBDData!!.existsUser(auth.uid)){
                        adminBDData!!.updateDataUser(dataUser)
                    }else{
                        adminBDData!!.setDataUser(dataUser)
                    }

                    startActivity(Intent(this, Home:: class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun registerMailandPass(user : String, pass : String, correo: String, dialog: AlertDialog) {

        auth.createUserWithEmailAndPassword(correo, pass)
            .addOnCompleteListener(this) {
                if (it.isSuccessful()) {

                    //DB local
                    //Pregunto si ya existe el usuario
                    if(adminBDData!!.existsUser(auth.uid)){
                        val dataUser : ContentValues = ContentValues()
                        dataUser.put("NAMES", user)
                        dataUser.put("UID", auth.uid)
                        dataUser.put("PROVIDER", "USER")
                        dataUser.put("ACTIVE", true)
                        adminBDData!!.updateDataUser(dataUser)
                    }else{
                        val dataUser : ContentValues = ContentValues()
                        dataUser.put("NAMES", user)
                        dataUser.put("UID", auth.uid)
                        dataUser.put("PROVIDER", "USER")
                        dataUser.put("ACTIVE", true)
                        adminBDData!!.setDataUser(dataUser)
                    }
                    val myPreferences = PreferenceManager.getDefaultSharedPreferences(this)
                    val myEditor = myPreferences.edit()
                    myEditor.putString("names", user);
                    myEditor.commit();

                    val db = FirebaseFirestore.getInstance()
                    db.collection("users").document(correo).set(
                        hashMapOf(
                            "names" to user
                        )
                    )

                    Toast.makeText(this, "register in as ${user}", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                    startActivity(Intent(this, Home:: class.java))
                    finish()
                } else {
                    // Error al crear el usuario
                    Toast.makeText(this, "Ya existe un usuario con este mail, Inicie sesion", Toast.LENGTH_SHORT).show()
                    //TODO: abrir la ventana de inicio de sesion
                    dialog.dismiss()
                    val builder = AlertDialog.Builder(this)
                    val Inflater = LayoutInflater.from(this)
                    val view: View = Inflater.inflate(R.layout.init_alert, null)
                    view.isFocusable = true
                    builder.setView(view)
                    //builder.setCancelable(false)
                    val dialog = builder.create()
                    dialog.show()

                    val iniciarBtn = view.findViewById<Button>(R.id.ingresarBTN)
                    val user = view.findViewById<EditText>(R.id.userEDT)
                    val pass = view.findViewById<EditText>(R.id.passEDT)
                    iniciarBtn.setOnClickListener {
                        if (user.text.isNotEmpty() && pass.text.isNotEmpty()){
                            initMailandPass(user.text.toString() , pass.text.toString())
                        }
                    }
                } }

    }

    private fun initMailandPass(correo: String, pass : String){

        auth.signInWithEmailAndPassword(correo, pass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val dataUser = ContentValues()
                    dataUser.put("UID", auth.uid)
                    dataUser.put("ACTIVE", true)
                    adminBDData!!.updateDataUser(dataUser)
                    startActivity(Intent(this, Home::class.java))
                } else {
                    // Handle login failure
                    val exception = task.exception
                    if (exception != null) {
                        val errorMessage = exception.localizedMessage ?: "Authentication failed."
                        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                    } else {
                        // This shouldn't happen, but handle unexpected errors
                        Toast.makeText(this, "An unknown error occurred.", Toast.LENGTH_SHORT).show()
                    }
                }
            }

    }
}




/*// Guardado de datos
val prefs : SharedPreferences.Editor? = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
//prefs.putString("email", email)
//prefs.putString("provider", provider)
//prefs.apply()
}

private fun setup() {

title = "autenticacion"
val singUpButton = findViewById<Button>(R.id.singUpButton)
val emailEditText = findViewById<EditText>(R.id.emailEditText)
val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
singUpButton.setOnClickListener {
    if (emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty()){

        FirebaseAuth.getInstance()
            .createUserWithEmailAndPassword(emailEditText.text.toString(), passwordEditText.text.toString())
            .addOnCompleteListener{
                if (it.isSuccessful){
                    //showHome(account.email, ProviderType.GOOGLE)
                }else{
                    showAlert()
                }
            }

    }
}

googleButton.setOnClickListener {

    val googleConf : GoogleSignInOptions =
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
    val googleClient : GoogleSignInClient = GoogleSignIn.getClient(this, googleConf)
    googleClient.signOut()

    startActivityForResult(googleClient.signInIntent, GOOGLE_SING_IN)
}
}

override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
super.onActivityResult(requestCode, resultCode, data)

if (requestCode == GOOGLE_SING_IN){
    val task : Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)

    try {

        val account: GoogleSignInAccount = task.getResult(ApiException::class.java)

        val credential: AuthCredential =
            GoogleAuthProvider.getCredential(account.idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    showHome(account.email, ProviderType.GOOGLE)
                } else {
                    showAlert()
                }
            }
    }catch (e: ApiException){
        showAlert()
        Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
        Log.e("mensaje",e.toString())
    }
}else{
    Toast.makeText(this, "ni entro", Toast.LENGTH_SHORT).show()
}
}
private fun showAlert(){
val builder = AlertDialog.Builder(this)
builder.setTitle("Error")
builder.setMessage("Se ha produciodo un error atenticando al usuario")
builder.setPositiveButton("Aceptar", null)
val dialog: AlertDialog = builder.create()
dialog.show()
}

private fun showHome(email: String?, google: ProviderType) {
val homeIntent = Intent(this, Home::class.java).apply {
    putExtra("email", "email")
    putExtra("provider", "provider.name")
}
startActivity(homeIntent)
}

}*/



