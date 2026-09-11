package com.example.desafio2dsm

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.desafio2dsm.adapters.DestinoAdapter
import com.example.desafio2dsm.models.Destino
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerDestinos: RecyclerView
    private lateinit var btnAgregarDestino: Button
    private lateinit var btnCerrarSesion: Button

    private lateinit var adapter: DestinoAdapter

    private val listaDestinos = mutableListOf<Destino>()

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerDestinos = findViewById(R.id.recyclerDestinos)
        btnAgregarDestino =
            findViewById(R.id.btnAgregarDestino)
        btnCerrarSesion =
            findViewById(R.id.btnCerrarSesion)

        adapter = DestinoAdapter(
            listaDestinos,
            onEditar = { destino ->
                val intent = Intent(
                    this,
                    EditarDestinoActivity::class.java
                )

                intent.putExtra("id", destino.id)
                intent.putExtra("nombre", destino.nombre)
                intent.putExtra("pais", destino.pais)
                intent.putExtra("precio", destino.precio)
                intent.putExtra(
                    "descripcion",
                    destino.descripcion
                )
                intent.putExtra(
                    "imagenUrl",
                    destino.imagenUrl
                )

                startActivity(intent)
            },
            onEliminar = { destino ->
                confirmarEliminar(destino)
            }
        )

        recyclerDestinos.layoutManager =
            LinearLayoutManager(this)

        recyclerDestinos.adapter = adapter

        btnAgregarDestino.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    CrearDestinoActivity::class.java
                )
            )
        }

        btnCerrarSesion.setOnClickListener {
            auth.signOut()

            startActivity(
                Intent(this, LoginActivity::class.java)
            )

            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        cargarDestinos()
    }

    private fun cargarDestinos() {

        db.collection("destinos")
            .get()
            .addOnSuccessListener { resultado ->

                listaDestinos.clear()

                for (documento in resultado) {

                    val destino = documento.toObject(
                        Destino::class.java
                    )

                    // Asignar el ID del documento de Firestore
                    destino.id = documento.id

                    listaDestinos.add(destino)
                }

                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Error al cargar destinos",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun confirmarEliminar(destino: Destino) {

        AlertDialog.Builder(this)
            .setTitle("Eliminar destino")
            .setMessage(
                "¿Está seguro de eliminar ${destino.nombre}?"
            )
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Eliminar") { _, _ ->

                db.collection("destinos")
                    .document(destino.id)
                    .delete()
                    .addOnSuccessListener {

                        Toast.makeText(
                            this,
                            "Destino eliminado",
                            Toast.LENGTH_SHORT
                        ).show()

                        cargarDestinos()
                    }
                    .addOnFailureListener {

                        Toast.makeText(
                            this,
                            "No se pudo eliminar",
                            Toast.LENGTH_LONG
                        ).show()
                    }
            }
            .show()
    }
}