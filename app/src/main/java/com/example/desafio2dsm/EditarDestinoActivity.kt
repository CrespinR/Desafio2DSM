package com.example.desafio2dsm

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class EditarDestinoActivity : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var spPais: Spinner
    private lateinit var etPrecio: EditText
    private lateinit var etDescripcion: EditText
    private lateinit var imgVistaPrevia: ImageView
    private lateinit var btnGuardar: Button

    private val db =
        FirebaseFirestore.getInstance()

    private var destinoId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_editar_destino
        )

        etNombre =
            findViewById(R.id.etNombre)

        spPais =
            findViewById(R.id.spPais)

        etPrecio =
            findViewById(R.id.etPrecio)

        etDescripcion =
            findViewById(R.id.etDescripcion)

        imgVistaPrevia =
            findViewById(R.id.imgVistaPrevia)

        btnGuardar =
            findViewById(R.id.btnGuardar)

        configurarSpinner()

        cargarDatos()

        btnGuardar.setOnClickListener {
            actualizarDestino()
        }
    }

    private fun configurarSpinner() {

        val paises = arrayOf(
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

    private fun cargarDatos() {

        destinoId =
            intent.getStringExtra("id") ?: ""

        val nombre =
            intent.getStringExtra("nombre") ?: ""

        val pais =
            intent.getStringExtra("pais") ?: ""

        val precio =
            intent.getDoubleExtra(
                "precio",
                0.0
            )

        val descripcion =
            intent.getStringExtra(
                "descripcion"
            ) ?: ""

        val imagen =
            intent.getStringExtra(
                "imagen"
            ) ?: ""

        etNombre.setText(nombre)

        etPrecio.setText(
            precio.toString()
        )

        etDescripcion.setText(
            descripcion
        )

        val posicion =
            (spPais.adapter as ArrayAdapter<String>)
                .getPosition(pais)

        if (posicion >= 0) {

            spPais.setSelection(posicion)
        }

        mostrarImagen(imagen)
    }

    private fun mostrarImagen(
        nombreImagen: String
    ) {

        val resourceId =
            resources.getIdentifier(
                nombreImagen,
                "drawable",
                packageName
            )

        if (resourceId != 0) {

            imgVistaPrevia.setImageResource(
                resourceId
            )

        } else {

            imgVistaPrevia.setImageResource(
                R.drawable.destino_default
            )
        }
    }

    private fun obtenerNombreImagen(
        pais: String
    ): String {

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

    private fun actualizarDestino() {

        val nombre =
            etNombre.text.toString().trim()

        val pais =
            spPais.selectedItem.toString()

        val precioTexto =
            etPrecio.text.toString().trim()

        val descripcion =
            etDescripcion.text.toString().trim()

        if (nombre.isEmpty()) {

            etNombre.error =
                "Ingrese el nombre"

            return
        }

        if (precioTexto.isEmpty()) {

            etPrecio.error =
                "Ingrese el precio"

            return
        }

        val precio =
            precioTexto.toDoubleOrNull()

        if (precio == null || precio <= 0) {

            etPrecio.error =
                "El precio debe ser mayor que 0"

            return
        }

        if (descripcion.length < 20) {

            etDescripcion.error =
                "Mínimo 20 caracteres"

            return
        }

        val imagen =
            obtenerNombreImagen(pais)

        val datos = hashMapOf(
            "nombre" to nombre,
            "pais" to pais,
            "precio" to precio,
            "descripcion" to descripcion,
            "imagen" to imagen
        )

        db.collection("destinos")
            .document(destinoId)
            .update(datos)
            .addOnSuccessListener {

                Toast.makeText(
                    this,
                    "Destino actualizado",
                    Toast.LENGTH_SHORT
                ).show()

                finish()
            }
            .addOnFailureListener { error ->

                Toast.makeText(
                    this,
                    "Error al actualizar:\n${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }
}