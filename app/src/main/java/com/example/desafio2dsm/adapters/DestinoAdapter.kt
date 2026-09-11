package com.example.desafio2dsm.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.desafio2dsm.R
import com.example.desafio2dsm.models.Destino
import android.content.Intent
import com.example.desafio2dsm.EditarDestinoActivity

class DestinoAdapter(
    private val listaDestinos: MutableList<Destino>,
    private val onEditar: (Destino) -> Unit,
    private val onEliminar: (Destino) -> Unit
) : RecyclerView.Adapter<DestinoAdapter.DestinoViewHolder>() {

    class DestinoViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val imgDestino: ImageView =
            itemView.findViewById(R.id.imgDestino)

        val tvNombre: TextView =
            itemView.findViewById(R.id.tvNombre)

        val tvPais: TextView =
            itemView.findViewById(R.id.tvPais)

        val tvPrecio: TextView =
            itemView.findViewById(R.id.tvPrecio)

        val tvDescripcion: TextView =
            itemView.findViewById(R.id.tvDescripcion)

        val btnEditar: Button =
            itemView.findViewById(R.id.btnEditar)

        val btnEliminar: Button =
            itemView.findViewById(R.id.btnEliminar)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DestinoViewHolder {

        val vista = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_destino,
                parent,
                false
            )

        return DestinoViewHolder(vista)
    }

    override fun onBindViewHolder(
        holder: DestinoViewHolder,
        position: Int
    ) {

        val destino = listaDestinos[position]

        holder.tvNombre.text = destino.nombre
        holder.tvPais.text = destino.pais
        holder.tvPrecio.text =
            String.format("$%.2f", destino.precio)
        holder.tvDescripcion.text = destino.descripcion

        Glide.with(holder.itemView.context)
            .load(destino.imagenUrl)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .into(holder.imgDestino)

        holder.btnEditar.setOnClickListener {

            val intent = Intent(
                holder.itemView.context,
                EditarDestinoActivity::class.java
            )

            intent.putExtra(
                "destinoId",
                destino.id
            )

            holder.itemView.context.startActivity(intent)
        }

        holder.btnEliminar.setOnClickListener {
            onEliminar(destino)
        }
    }

    override fun getItemCount(): Int {
        return listaDestinos.size
    }
}