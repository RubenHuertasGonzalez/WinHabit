package com.institutvidreres.winhabit.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.institutvidreres.winhabit.R
import com.institutvidreres.winhabit.SharedViewModel
import com.institutvidreres.winhabit.ui.recompensas.Recompensa

class PerfilAdapter(private val recompensas: List<Recompensa>, private val sharedViewModel: SharedViewModel, private val onItemClick: (Recompensa) -> Unit) :
    RecyclerView.Adapter<PerfilAdapter.AlumneViewHolder>() {

    // ViewHolder que conté les vistes de cada element
    class AlumneViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val imagenImageView: ImageView = itemView.findViewById(R.id.imagen_perfil_recompensa)
        val descripcionTextView: TextView = itemView.findViewById(R.id.descripcion_perfil_recompensa)
        val botonRecompensa: Button = itemView.findViewById(R.id.boton_perfil_recompensa)
    }

    // Crea una nova vista (invocada per el layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlumneViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_perfil_recompensas, parent, false) // importem cardview
        return AlumneViewHolder(view)
    }

    // Canvia el contingut de una vista del cardview (invocada per el layout manager)
    override fun onBindViewHolder(holder: AlumneViewHolder, position: Int) {
        val recompensa = recompensas[position]

        holder.imagenImageView.setImageResource(recompensa.imagenResId)
        holder.descripcionTextView.text = recompensa.descripcion
        holder.botonRecompensa.setOnClickListener {
            onItemClick.invoke(recompensa)
            sharedViewModel.setSelectedImage(recompensa.imagenResId)
        }
    }

    // Retorna el tamany del conjunt de dades (invocada per el layout manager)
    override fun getItemCount() = recompensas.size
}