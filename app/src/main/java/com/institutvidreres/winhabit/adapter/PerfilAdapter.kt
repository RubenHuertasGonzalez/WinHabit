package com.institutvidreres.winhabit.adapter

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.institutvidreres.winhabit.R
import com.institutvidreres.winhabit.SharedViewModel
import com.institutvidreres.winhabit.ui.recompensas.Recompensa

class PerfilAdapter(private val recompensas: List<Recompensa>, private val context: Context, private val sharedViewModel: SharedViewModel, private val onItemClick: (Recompensa) -> Unit) :
    RecyclerView.Adapter<PerfilAdapter.PerfilViewHolder>() {

    // ViewHolder que conté les vistes de cada element
    class PerfilViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val imagenImageView: ImageView = itemView.findViewById(R.id.imagen_perfil_recompensa)
        val descripcionTextView: TextView = itemView.findViewById(R.id.descripcion_perfil_recompensa)
    }

    // Crea una nova vista (invocada per el layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PerfilViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_perfil_recompensas, parent, false) // importem cardview
        return PerfilViewHolder(view)
    }

    // Canvia el contingut de una vista del cardview (invocada per el layout manager)
    override fun onBindViewHolder(holder: PerfilViewHolder, position: Int) {
        val recompensa = recompensas[position]

        holder.imagenImageView.setImageResource(recompensa.imagenResId)
        holder.descripcionTextView.text = recompensa.descripcion
        holder.itemView.setOnClickListener {
            mostrarDialogoEquipar(recompensa)
            sharedViewModel.setSelectedImage(recompensa.imagenResId)
        }
    }

    // Retorna el tamany del conjunt de dades (invocada per el layout manager)
    override fun getItemCount() = recompensas.size

    private fun mostrarDialogoEquipar(recompensa: Recompensa) {
        Log.d("RecompensasAdapter", "mostrarDialogoCompra ejecutado")  // Agregar este log
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Equipar al Perfil")
        builder.setMessage("¿Quieres equipar '${recompensa.descripcion}' al perfil?")
        builder.setPositiveButton("CONFIRMAR") { _, _ ->
            // Añadir un Toast cuando se confirme la compra
            val mensaje = "${recompensa.nombre} equipado!"
            Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
            onItemClick(recompensa)
        }
        builder.setNegativeButton("CANCELAR", null)
        builder.show()
    }
}