package com.example.desafio2dsm

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity

import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class EditarDestinoActivity : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var spPais: Spinner
    private lateinit var etPrecio: EditText
    private lateinit var etDescripcion: EditText
    private lateinit var imgVistaPrevia: ImageView
    private lateinit var btnCambiarImagen: Button
    private lateinit var btnActualizar: Button

    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private var destinoId: String = ""
    private var imagenUrlActual: String = ""
    private var imagenSeleccionada: Uri? = null

    private val REQUEST_IMAGE = 100

    private val paises = arrayOf(
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_destino)

        inicializarComponentes()
        configurarSpinner()
        obtenerDestino()
        configurarBotones()
    }

    private fun inicializarComponentes() {

        etNombre = findViewById(R.id.etNombreEditar)
        spPais = findViewById(R.id.spPaisEditar)
        etPrecio = findViewById(R.id.etPrecioEditar)
        etDescripcion = findViewById(R.id.etDescripcionEditar)
        imgVistaPrevia = findViewById(R.id.imgVistaPreviaEditar)
        btnCambiarImagen = findViewById(R.id.btnCambiarImagen)
        btnActualizar = findViewById(R.id.btnActualizar)
    }

    private fun configurarSpinner() {

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

    private fun obtenerDestino() {

        destinoId = intent.getStringExtra("destinoId") ?: ""

        if (destinoId.isEmpty()) {

            Toast.makeText(
                this,
                "No se encontró el destino",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        db.collection("destinos")
            .document(destinoId)
            .get()
            .addOnSuccessListener { document ->

                if (document.exists()) {

                    val nombre =
                        document.getString("nombre") ?: ""

                    val pais =
                        document.getString("pais") ?: ""

                    val precio =
                        document.getDouble("precio")

                    val descripcion =
                        document.getString("descripcion") ?: ""

                    imagenUrlActual =
                        document.getString("imagenUrl") ?: ""

                    etNombre.setText(nombre)

                    if (precio != null) {
                        etPrecio.setText(precio.toString())
                    }

                    etDescripcion.setText(descripcion)

                    seleccionarPais(pais)

                    if (imagenUrlActual.isNotEmpty()) {

                        Glide.with(this)
                            .load(imagenUrlActual)
                            .placeholder(android.R.drawable.ic_menu_gallery)
                            .error(android.R.drawable.ic_menu_report_image)
                            .into(imgVistaPrevia)
                    }

                } else {

                    Toast.makeText(
                        this,
                        "El destino no existe",
                        Toast.LENGTH_SHORT
                    ).show()

                    finish()
                }
            }
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Error al cargar el destino",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun seleccionarPais(pais: String) {

        val posicion = paises.indexOf(pais)

        if (posicion >= 0) {
            spPais.setSelection(posicion)
        }
    }

    private fun configurarBotones() {

        btnCambiarImagen.setOnClickListener {
            seleccionarImagen()
        }

        btnActualizar.setOnClickListener {
            actualizarDestino()
        }
    }

    private fun seleccionarImagen() {

        val intent = Intent(Intent.ACTION_PICK)

        intent.type = "image/*"

        startActivityForResult(
            intent,
            REQUEST_IMAGE
        )
    }

    @Deprecated("Deprecated in Android API")
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
            requestCode == REQUEST_IMAGE &&
            resultCode == Activity.RESULT_OK &&
            data != null
        ) {

            imagenSeleccionada = data.data

            imgVistaPrevia.setImageURI(
                imagenSeleccionada
            )
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

        // Validar nombre
        if (nombre.isEmpty()) {

            etNombre.error =
                getString(R.string.ingrese_nombre)

            etNombre.requestFocus()

            return
        }

        // Validar país
        if (pais == "Seleccionar país") {

            Toast.makeText(
                this,
                getString(R.string.seleccione_pais),
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        // Validar precio
        if (precioTexto.isEmpty()) {

            etPrecio.error =
                getString(R.string.ingrese_precio)

            etPrecio.requestFocus()

            return
        }

        val precio =
            precioTexto.toDoubleOrNull()

        if (precio == null || precio <= 0) {

            etPrecio.error =
                getString(R.string.precio_mayor_cero)

            etPrecio.requestFocus()

            return
        }

        // Validar descripción
        if (descripcion.length < 20) {

            etDescripcion.error =
                getString(R.string.descripcion_minima)

            etDescripcion.requestFocus()

            return
        }

        // Si el usuario seleccionó una nueva imagen
        if (imagenSeleccionada != null) {

            subirNuevaImagen(
                nombre,
                pais,
                precio,
                descripcion
            )

        } else {

            actualizarFirestore(
                nombre,
                pais,
                precio,
                descripcion,
                imagenUrlActual
            )
        }
    }

    private fun subirNuevaImagen(
        nombre: String,
        pais: String,
        precio: Double,
        descripcion: String
    ) {

        val uri = imagenSeleccionada ?: return

        btnActualizar.isEnabled = false

        val referencia = storage
            .reference
            .child("destinos")
            .child("$destinoId.jpg")

        referencia.putFile(uri)
            .addOnSuccessListener {

                referencia.downloadUrl
                    .addOnSuccessListener { nuevaUrl ->

                        actualizarFirestore(
                            nombre,
                            pais,
                            precio,
                            descripcion,
                            nuevaUrl.toString()
                        )
                    }
            }
            .addOnFailureListener {

                btnActualizar.isEnabled = true

                Toast.makeText(
                    this,
                    getString(R.string.error_subir_imagen),
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun actualizarFirestore(
        nombre: String,
        pais: String,
        precio: Double,
        descripcion: String,
        imagenUrl: String
    ) {

        val datos = hashMapOf<String, Any>(
            "nombre" to nombre,
            "pais" to pais,
            "precio" to precio,
            "descripcion" to descripcion,
            "imagenUrl" to imagenUrl
        )

        db.collection("destinos")
            .document(destinoId)
            .update(datos)
            .addOnSuccessListener {

                Toast.makeText(
                    this,
                    getString(R.string.destino_actualizado),
                    Toast.LENGTH_SHORT
                ).show()

                val intent = Intent(
                    this,
                    MainActivity::class.java
                )

                intent.flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_SINGLE_TOP

                startActivity(intent)

                finish()
            }
            .addOnFailureListener {

                btnActualizar.isEnabled = true

                Toast.makeText(
                    this,
                    getString(R.string.error_actualizar_destino),
                    Toast.LENGTH_LONG
                ).show()
            }
    }
}