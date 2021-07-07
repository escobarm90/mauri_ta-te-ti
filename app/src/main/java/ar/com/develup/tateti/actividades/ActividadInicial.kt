package ar.com.develup.tateti.actividades

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ar.com.develup.tateti.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import kotlinx.android.synthetic.main.actividad_inicial.*

class ActividadInicial : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseAnalitycs: FirebaseAnalytics
    private lateinit var firebaseCrashlitycs: FirebaseCrashlytics
    private lateinit var remoteConfig: FirebaseRemoteConfig

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actividad_inicial)
        firebaseAnalitycs = Firebase.analytics
        firebaseCrashlitycs = Firebase.crashlytics
        remoteConfig = Firebase.remoteConfig
        iniciarSesion.setOnClickListener { iniciarSesion() }
        auth = Firebase.auth
        registrate.setOnClickListener { registrate() }
        olvideMiContrasena.setOnClickListener { olvideMiContrasena() }

        if (usuarioEstaLogueado()) {
            // Si el usuario esta logueado, se redirige a la pantalla
            // de partidas
            verPartidas()
            finish()
        }
        actualizarRemoteConfig()
    }

    private fun usuarioEstaLogueado(): Boolean {
        // TODO-05-AUTHENTICATION
        // Validar que currentUser sea != null
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            AppCompatActivity();
        }
        return false
    }

    private fun verPartidas() {
        val intent = Intent(this, ActividadPartidas::class.java)
        startActivity(intent)
    }

    private fun registrate() {
        val intent = Intent(this, ActividadRegistracion::class.java)
        startActivity(intent)
    }

    private fun actualizarRemoteConfig() {
        configurarDefaultsRemoteConfig()
        configurarOlvideMiContrasena()
    }

    private fun configurarDefaultsRemoteConfig() {
        // TODO-04-REMOTECONFIG
        // Configurar los valores por default para remote config,
        // ya sea por codigo o por XML
    }

    private fun configurarOlvideMiContrasena() {
        // TODO-04-REMOTECONFIG
        // Obtener el valor de la configuracion para saber si mostrar
        // o no el boton de olvide mi contraseña
        val botonOlvideHabilitado = false
        if (botonOlvideHabilitado) {
            olvideMiContrasena.visibility = View.VISIBLE
        } else {
            olvideMiContrasena.visibility = View.GONE
        }
    }

    private fun olvideMiContrasena() {
        // Obtengo el mail
        val email = email.text.toString()

        // Si no completo el email, muestro mensaje de error
        if (email.isEmpty()) {
            Snackbar.make(rootView!!, "Completa el email", Snackbar.LENGTH_SHORT).show()
        } else {
            // TODO-05-AUTHENTICATION
            // Si completo el mail debo enviar un mail de reset
            // Para ello, utilizamos sendPasswordResetEmail con el email como parametro
            // Agregar el siguiente fragmento de codigo como CompleteListener, que notifica al usuario
            // el resultado de la operacion

            FirebaseAuth.getInstance().sendPasswordResetEmail(email!!)
              .addOnCompleteListener { task ->
                  if (task.isSuccessful) {
                     Snackbar.make(rootView, "Email enviado", Snackbar.LENGTH_SHORT).show()
                  } else {
                      Snackbar.make(rootView, "Error " + task.exception, Snackbar.LENGTH_SHORT).show()
                  }
              }
        }
    }

    private fun iniciarSesion() {
        val email = email.text.toString()
        val password = password.text.toString()
        if(email.isBlank() || password.isBlank()){
            Snackbar.make(rootView!!, "Campos vacios", Snackbar.LENGTH_SHORT)
                .show()
        }else
        // TODO-05-AUTHENTICATION
        // IMPORTANTE: Eliminar  la siguiente linea cuando se implemente authentication
        // TODO-05-AUTHENTICATION
        // hacer signInWithEmailAndPassword con los valores ingresados de email y password
        // Agregar en addOnCompleteListener el campo authenticationListener definido mas abajo
        FirebaseAuth.getInstance()
            .signInWithEmailAndPassword(email!!, password!!)
            .addOnCompleteListener(authenticationListener)
    }

    private val authenticationListener: OnCompleteListener<AuthResult?> =
        OnCompleteListener<AuthResult?> { task ->
            if (task.isSuccessful) {
                if (usuarioVerificoEmail()) {
                    verPartidas()
                } else {
                    desloguearse()
                    Snackbar.make(
                        rootView!!,
                        "Verifica tu email para continuar",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            } else {
                if (task.exception is FirebaseAuthInvalidUserException) {
                    Snackbar.make(rootView!!, "El usuario no existe", Snackbar.LENGTH_SHORT).show()
                } else if (task.exception is FirebaseAuthInvalidCredentialsException) {
                    Snackbar.make(rootView!!, "Credenciales inválidas", Snackbar.LENGTH_SHORT)
                        .show()
                }
            }
        }



    private fun usuarioVerificoEmail(): Boolean {
        // TODO-05-AUTHENTICATION
        // Preguntar al currentUser si verifico email
        if (FirebaseAuth.getInstance().currentUser?.isEmailVerified!!) {
            return true
        }
        return false
    }



    fun desloguearse() {
        // TODO-05-AUTHENTICATION
        Firebase.auth.signOut()
    }
}