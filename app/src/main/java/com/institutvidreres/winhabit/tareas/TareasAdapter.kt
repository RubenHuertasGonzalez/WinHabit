package com.institutvidreres.winhabit.tareas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.institutvidreres.winhabit.R

class TareasAdapter(private var tareas: List<Tarea>, private val listener: OnDecrementClickListener) :
    RecyclerView.Adapter<TareasAdapter.TareaViewHolder>() {

    inner class TareaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombre: TextView = itemView.findViewById(R.id.tvNombreTarea)
        val descripcion: TextView = itemView.findViewById(R.id.tvDescripcionTarea)
        val dificultad: TextView = itemView.findViewById(R.id.tvDificultadTarea)
        val duracion: TextView = itemView.findViewById(R.id.tvDuracionTarea)
        val btnDecrementar: Button = itemView.findViewById(R.id.btnDecrementar)

        init {
            btnDecrementar.setOnClickListener {
                listener.onDecrementClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TareaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_inicio_recycler, parent, false)
        return TareaViewHolder(view)
    }

    override fun onBindViewHolder(holder: TareaViewHolder, position: Int) {
        val tarea = tareas[position]
        holder.nombre.text = tarea.nombre
        holder.descripcion.text = tarea.descripcion
        holder.dificultad.text = tarea.dificultad.toString()
        holder.duracion.text = tarea.duracion.toString()
    }

    override fun getItemCount(): Int = tareas.size

    fun actualizarLista(nuevasTareas: List<Tarea>) {
        tareas = nuevasTareas
        notifyDataSetChanged()
    }

    interface OnDecrementClickListener {
        fun onDecrementClick(position: Int)
    }
}

