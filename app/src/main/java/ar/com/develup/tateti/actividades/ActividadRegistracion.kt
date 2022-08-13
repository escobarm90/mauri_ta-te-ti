package ar.com.develup.tateti.actividades

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ar.com.develup.tateti.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.actividad_registracion.*

class ActividadRegistracion : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actividad_registracion)
        registrar.setOnClickListener { registrarse() }
        auth = Firebase.auth
    }

    fun registrarse() {
        val passwordIngresada = password.text.toString()
        val confirmarPasswordIngresada = confirmarPassword.text.toString()
        val email = email.text.toString()

        if (email.isEmpty()) {
            Snackbar.make(rootView, "Email requerido", Snackbar.LENGTH_SHORT).show()
        } else if (passwordIngresada == confirmarPasswordIngresada) {
            registrarUsuarioEnFirebase(email, passwordIngresada)
        } else {
            Snackbar.make(rootView, "Las contraseñas no coinciden", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun registrarUsuarioEnFirebase(email: String, passwordIngresada: String) {
              FirebaseAuth.getInstance()
            .createUserWithEmailAndPassword(email, passwordIngresada)
            .addOnCompleteListener(this, registracionCompletaListener)
    }

   private val registracionCompletaListener: OnCompleteListener<AuthResult?> = OnCompleteListener { task ->
        if (task.isSuccessful) {
            Snackbar.make(rootView, "Registro exitoso", Snackbar.LENGTH_SHORT).show()
            enviarEmailDeVerificacion()
        } else if (task.exception is FirebaseAuthUserCollisionException) {
            Snackbar.make(rootView, "El usuario ya existe", Snackbar.LENGTH_SHORT).show()
        } else {
            Snackbar.make(rootView, "El registro fallo: " + task.exception, Snackbar.LENGTH_LONG).show()
        }
    }

    private fun enviarEmailDeVerificacion() {
        FirebaseAuth.getInstance().currentUser?.sendEmailVerification()
    }
}