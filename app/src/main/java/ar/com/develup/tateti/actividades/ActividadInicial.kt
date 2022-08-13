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
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
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
            verPartidas()
            finish()
        }
        actualizarRemoteConfig()
    }

    private fun usuarioEstaLogueado(): Boolean {
        super.onStart()
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
        val configuracion = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 10
            fetchTimeoutInSeconds = 10
        }
        remoteConfig.setConfigSettingsAsync(configuracion)
        val defaultsSettings = mapOf("olvidecontrasenia" to false)
        Firebase.remoteConfig.setDefaultsAsync(defaultsSettings)
    }

    private fun configurarOlvideMiContrasena() {
        Firebase.remoteConfig.fetchAndActivate().addOnCompleteListener {
            val botonOlvideHabilitado = Firebase.remoteConfig.getBoolean("olvidecontrasenia")
            if (botonOlvideHabilitado) {
                olvideMiContrasena.visibility = View.VISIBLE
            } else {
                olvideMiContrasena.visibility = View.GONE
            }
        }
    }

    private fun olvideMiContrasena() {
        val email = email.text.toString()
        if (email.isEmpty()) {
            Snackbar.make(rootView!!, "Completa el email", Snackbar.LENGTH_SHORT).show()
        } else {
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
        if (FirebaseAuth.getInstance().currentUser?.isEmailVerified!!) {
            return true
        }
        return false
    }



    fun desloguearse() {
        Firebase.auth.signOut()
    }
}