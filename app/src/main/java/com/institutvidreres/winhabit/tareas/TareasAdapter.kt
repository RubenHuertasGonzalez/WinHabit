package com.institutvidreres.winhabit.tareas

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.institutvidreres.winhabit.R

class TareasAdapter(private var tareas: List<Tarea>, private val listener: OnClickListener) :
    RecyclerView.Adapter<TareasAdapter.TareaViewHolder>() {

    private lateinit var context: Context
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    inner class TareaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombre: TextView = itemView.findViewById(R.id.tvNombreTarea)
        val descripcion: TextView = itemView.findViewById(R.id.tvDescripcionTarea)
        val dificultad: TextView = itemView.findViewById(R.id.tvDificultadTarea)
        val duracion: TextView = itemView.findViewById(R.id.tvDuracionTarea)
        val btnIncrementar: Button = itemView.findViewById(R.id.btnIncrementar)
        val btnDecrementar: Button = itemView.findViewById(R.id.btnDecrementar)
        val tvClose: TextView = itemView.findViewById(R.id.tvClose)

        init {
            btnDecrementar.setOnClickListener {
                listener.onDecrementClick(adapterPosition)
            }
            btnIncrementar.setOnClickListener {
                listener.onIncrementClick(adapterPosition)
            }
            tvClose.setOnClickListener {
                mostrarDialogoBorrarTarea(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TareaViewHolder {
        context = parent.context
        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
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

    private fun mostrarDialogoBorrarTarea(position: Int) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("¿Quieres borrar la tarea?")
        builder.setPositiveButton("Sí") { dialog, which ->
            val tareaABorrar = tareas[position]
            val userId = firebaseAuth.currentUser?.uid
            if (userId != null) {
                firestore.collection("tasks")
                    .whereEqualTo("nombre", tareaABorrar.nombre)
                    .whereEqualTo("descripcion", tareaABorrar.descripcion)
                    .whereEqualTo("dificultad", tareaABorrar.dificultad)
                    .whereEqualTo("duracion", tareaABorrar.duracion)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        for (document in querySnapshot.documents) {
                            document.reference.delete()
                                .addOnSuccessListener {
                                    Toast.makeText(context, "TAREA BORRADA CORRECTAMENTE", Toast.LENGTH_SHORT).show()
                                    // Si quieres también puedes actualizar la lista después de borrar la tarea
                                    actualizarLista(tareas.filterIndexed { index, _ -> index != position })
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(context, "ERROR AL BORRAR LA TAREA", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(context, "ERROR AL BORRAR LA TAREA", Toast.LENGTH_SHORT).show()
                    }
            }
        }
        builder.setNegativeButton("No") { dialog, which ->
            // No hacer nada
        }
        val dialog = builder.create()
        dialog.show()
    }

    interface OnClickListener {
        fun onDecrementClick(position: Int)
        fun onIncrementClick(position: Int)
    }
}

