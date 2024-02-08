package com.institutvidreres.winhabit.ui.logros

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.institutvidreres.winhabit.R

class LogrosAdapter(private val logrosList: List<LogrosItem>) :
    RecyclerView.Adapter<LogrosAdapter.LogrosViewHolder>() {

    class LogrosViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageInsignia: ImageView = itemView.findViewById(R.id.image_insignia)
        val tituloTextView: TextView = itemView.findViewById(R.id.text_titulo)
        val descripcionTextView: TextView = itemView.findViewById(R.id.text_descripcion)
        val btnCambiarImagen: Button = itemView.findViewById(R.id.btnCambiarImagen)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogrosViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_logros, parent, false)
        return LogrosViewHolder(view)
    }

    override fun onBindViewHolder(holder: LogrosViewHolder, position: Int) {
        val logro = logrosList[position]
        holder.tituloTextView.text = logro.titulo
        holder.descripcionTextView.text = logro.descripcion
        holder.imageInsignia.setImageResource(logro.imagenResource)

        holder.btnCambiarImagen.setOnClickListener {
            // Cambiar la imagen al hacer clic en el botón
            logro.toggleImagen()
            holder.imageInsignia.setImageResource(logro.imagenResource)
        }
    }

    override fun getItemCount(): Int {
        return logrosList.size
    }
}
