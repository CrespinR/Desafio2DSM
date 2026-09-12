package com.example.desafio2dsm

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class CrearDestinoActivity : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var spPais: Spinner
    private lateinit var etPrecio: EditText
    private lateinit var etDescripcion: EditText
    private lateinit var imgVistaPrevia: ImageView
    private lateinit var btnSeleccionarImagen: Button
    private lateinit var btnGuardar: Button

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_crear_destino)

        etNombre = findViewById(R.id.etNombre)
        spPais = findViewById(R.id.spPais)
        etPrecio = findViewById(R.id.etPrecio)
        etDescripcion = findViewById(R.id.etDescripcion)
        imgVistaPrevia = findViewById(R.id.imgVistaPrevia)
        btnSeleccionarImagen = findViewById(R.id.btnSeleccionarImagen)
        btnGuardar = findViewById(R.id.btnGuardar)

        configurarSpinner()

        btnSeleccionarImagen.setOnClickListener {

            if (spPais.selectedItemPosition == 0) {

                Toast.makeText(
                    this,
                    "Primero seleccione un país",
                    Toast.LENGTH_SHORT
                ).show()

            } else {

                val pais = spPais.selectedItem.toString()

                mostrarImagen(pais)
            }
        }

        btnGuardar.setOnClickListener {
            guardarDestino()
        }
    }

    private fun configurarSpinner() {

        val paises = arrayOf(
            "Seleccionar país",
            "El Salvador",
            "Guatemala",
            "Honduras",
            "México",
            "Costa Rica",
            "Panamá",
            "Colombia",
            "España",
            "Estados Unidos"
        )

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            paises
        )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spPais.adapter = adapter
    }

    private fun obtenerImagenPais(pais: String): Int {

        return when (pais) {

            "El Salvador" ->
                R.drawable.destino_sv

            "Guatemala" ->
                R.drawable.destino_guatemala

            "Honduras" ->
                R.drawable.destino_honduras

            "México" ->
                R.drawable.destino_mexico

            "Costa Rica" ->
                R.drawable.destino_costa_rica

            "Panamá" ->
                R.drawable.destino_panama

            "Colombia" ->
                R.drawable.destino_colombia

            "España" ->
                R.drawable.destino_espana

            "Estados Unidos" ->
                R.drawable.destino_usa

            else ->
                R.drawable.destino_default
        }
    }

    private fun mostrarImagen(pais: String) {

        val imagen = obtenerImagenPais(pais)

        imgVistaPrevia.setImageResource(imagen)
    }

    private fun guardarDestino() {

        val nombre = etNombre.text.toString().trim()

        val pais =
            spPais.selectedItem.toString()

        val precioTexto =
            etPrecio.text.toString().trim()

        val descripcion =
            etDescripcion.text.toString().trim()

        // Validar nombre
        if (nombre.isEmpty()) {

            etNombre.error =
                "Ingrese el nombre del destino"

            etNombre.requestFocus()

            return
        }

        // Validar país
        if (spPais.selectedItemPosition == 0) {

            Toast.makeText(
                this,
                "Seleccione un país",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        // Validar precio
        if (precioTexto.isEmpty()) {

            etPrecio.error =
                "Ingrese el precio"

            etPrecio.requestFocus()

            return
        }

        val precio =
            precioTexto.toDoubleOrNull()

        if (precio == null || precio <= 0) {

            etPrecio.error =
                "El precio debe ser mayor que 0"

            etPrecio.requestFocus()

            return
        }

        // Validar descripción
        if (descripcion.length < 20) {

            etDescripcion.error =
                "La descripción debe tener mínimo 20 caracteres"

            etDescripcion.requestFocus()

            return
        }

        val nombreImagen =
            obtenerNombreImagen(pais)

        val datos = hashMapOf(
            "nombre" to nombre,
            "pais" to pais,
            "precio" to precio,
            "descripcion" to descripcion,
            "imagen" to nombreImagen
        )


        db.collection("destinos")
            .add(datos)
            .addOnSuccessListener {

                Toast.makeText(
                    this,
                    "Destino guardado correctamente",
                    Toast.LENGTH_SHORT
                ).show()

                finish()
            }
            .addOnFailureListener { error ->

                Toast.makeText(
                    this,
                    "Error al guardar destino:\n${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun obtenerNombreImagen(pais: String): String {

        return when (pais) {

            "El Salvador" ->
                "destino_sv"

            "Guatemala" ->
                "destino_guatemala"

            "Honduras" ->
                "destino_honduras"

            "México" ->
                "destino_mexico"

            "Costa Rica" ->
                "destino_costa_rica"

            "Panamá" ->
                "destino_panama"

            "Colombia" ->
                "destino_colombia"

            "España" ->
                "destino_espana"

            "Estados Unidos" ->
                "destino_usa"

            else ->
                "destino_default"
        }
    }
}