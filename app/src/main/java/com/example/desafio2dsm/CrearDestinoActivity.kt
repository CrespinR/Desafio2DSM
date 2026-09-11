package com.example.desafio2dsm

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class CrearDestinoActivity : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var spPais: Spinner
    private lateinit var etPrecio: EditText
    private lateinit var etDescripcion: EditText
    private lateinit var imgVistaPrevia: ImageView
    private lateinit var btnSeleccionarImagen: Button
    private lateinit var btnGuardar: Button

    private var imagenUri: Uri? = null

    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

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
            seleccionarImagen()
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

    private fun seleccionarImagen() {

        val intent = Intent(Intent.ACTION_PICK)

        intent.type = "image/*"

        startActivityForResult(
            intent,
            100
        )
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {

        super.onActivityResult(
            requestCode,
            resultCode,
            data
        )

        if (
            requestCode == 100 &&
            resultCode == Activity.RESULT_OK
        ) {

            imagenUri = data?.data

            imgVistaPrevia.setImageURI(
                imagenUri
            )
        }
    }

    private fun guardarDestino() {

        val nombre = etNombre.text.toString().trim()
        val pais = spPais.selectedItem.toString()
        val precioTexto =
            etPrecio.text.toString().trim()
        val descripcion =
            etDescripcion.text.toString().trim()

        if (nombre.isEmpty()) {
            etNombre.error = "Ingrese el nombre"
            return
        }

        if (spPais.selectedItemPosition == 0) {
            Toast.makeText(
                this,
                "Seleccione un país",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (precioTexto.isEmpty()) {
            etPrecio.error = "Ingrese el precio"
            return
        }

        val precio = precioTexto.toDoubleOrNull()

        if (precio == null || precio <= 0) {
            etPrecio.error =
                "El precio debe ser mayor que 0"
            return
        }

        if (descripcion.length < 20) {
            etDescripcion.error =
                "La descripción debe tener mínimo 20 caracteres"
            return
        }

        if (imagenUri == null) {
            Toast.makeText(
                this,
                "Debe seleccionar una imagen",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        subirImagen(
            nombre,
            pais,
            precio,
            descripcion
        )
    }

    private fun subirImagen(
        nombre: String,
        pais: String,
        precio: Double,
        descripcion: String
    ) {

        val nombreImagen =
            "destinos/${UUID.randomUUID()}.jpg"

        val referencia =
            storage.reference.child(nombreImagen)

        referencia.putFile(imagenUri!!)
            .addOnSuccessListener {

                referencia.downloadUrl
                    .addOnSuccessListener { url ->

                        guardarEnFirestore(
                            nombre,
                            pais,
                            precio,
                            descripcion,
                            url.toString()
                        )
                    }
            }
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Error al subir imagen",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun guardarEnFirestore(
        nombre: String,
        pais: String,
        precio: Double,
        descripcion: String,
        imagenUrl: String
    ) {

        val datos = hashMapOf(
            "nombre" to nombre,
            "pais" to pais,
            "precio" to precio,
            "descripcion" to descripcion,
            "imagenUrl" to imagenUrl
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
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Error al guardar destino",
                    Toast.LENGTH_LONG
                ).show()
            }
    }
}