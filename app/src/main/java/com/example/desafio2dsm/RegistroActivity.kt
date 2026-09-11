package com.example.desafio2dsm

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class RegistroActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var etCorreo: TextInputEditText
    private lateinit var etContrasena: TextInputEditText
    private lateinit var etConfirmarContrasena: TextInputEditText

    private lateinit var btnRegistrar: Button
    private lateinit var btnVolverLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        auth = FirebaseAuth.getInstance()

        etCorreo = findViewById(R.id.etCorreoRegistro)
        etContrasena = findViewById(R.id.etContrasenaRegistro)
        etConfirmarContrasena = findViewById(R.id.etConfirmarContrasena)

        btnRegistrar = findViewById(R.id.btnRegistrar)
        btnVolverLogin = findViewById(R.id.btnVolverLogin)

        btnRegistrar.setOnClickListener {
            registrarUsuario()
        }

        btnVolverLogin.setOnClickListener {
            finish()
        }
    }

    private fun registrarUsuario() {

        val correo = etCorreo.text.toString().trim()
        val contrasena = etContrasena.text.toString().trim()
        val confirmar = etConfirmarContrasena.text.toString().trim()

        if (correo.isEmpty()) {
            etCorreo.error = "Ingrese su correo"
            return
        }

        if (contrasena.isEmpty()) {
            etContrasena.error = "Ingrese una contraseña"
            return
        }

        if (confirmar.isEmpty()) {
            etConfirmarContrasena.error =
                "Confirme su contraseña"
            return
        }

        if (contrasena != confirmar) {
            etConfirmarContrasena.error =
                "Las contraseñas no coinciden"
            return
        }

        auth.createUserWithEmailAndPassword(
            correo,
            contrasena
        ).addOnCompleteListener { tarea ->

            if (tarea.isSuccessful) {

                Toast.makeText(
                    this,
                    "Usuario registrado correctamente",
                    Toast.LENGTH_SHORT
                ).show()

                startActivity(
                    Intent(this, MainActivity::class.java)
                )

                finish()

            } else {

                Toast.makeText(
                    this,
                    tarea.exception?.message
                        ?: "Error al registrar usuario",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}