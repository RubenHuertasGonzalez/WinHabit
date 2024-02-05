// RecompensasAdapter.kt
package com.institutvidreres.winhabit.ui.recompensas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.institutvidreres.winhabit.R

class RecompensasAdapter(private val recompensasList: List<Recompensa>) :
    RecyclerView.Adapter<RecompensasAdapter.RecompensaViewHolder>() {

    class RecompensaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imagenImageView: ImageView = itemView.findViewById(R.id.imagen_recompensa)
        val descripcionTextView: TextView = itemView.findViewById(R.id.descripcion_recompensa)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecompensaViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recompensa, parent, false)
        return RecompensaViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecompensaViewHolder, position: Int) {
        val recompensa = recompensasList[position]
        holder.imagenImageView.setImageResource(recompensa.imagenResId)
        holder.descripcionTextView.text = recompensa.descripcion

    }

    override fun getItemCount(): Int {
        return recompensasList.size
    }
}
