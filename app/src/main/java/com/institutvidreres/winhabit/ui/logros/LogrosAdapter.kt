package com.institutvidreres.winhabit.ui.logros

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.institutvidreres.winhabit.R

class LogrosAdapter(private val logrosList: MutableList<LogrosItem>) :
    RecyclerView.Adapter<LogrosAdapter.LogrosViewHolder>() {

    var onLogroClickListener: ((LogrosItem) -> Unit)? = null

    class LogrosViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageInsignia: ImageView = itemView.findViewById(R.id.image_insignia)
        val tituloTextView: TextView = itemView.findViewById(R.id.text_titulo)
        val descripcionTextView: TextView = itemView.findViewById(R.id.text_descripcion)
        val btnReclamar: Button = itemView.findViewById(R.id.btnCambiarImagen)
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

        if (logro.completado && !logro.reclamado) {
            holder.btnReclamar.visibility = View.VISIBLE
        } else {
            holder.btnReclamar.visibility = View.GONE
        }

        holder.btnReclamar.setOnClickListener {
            onLogroClickListener?.invoke(logro)
            holder.btnReclamar.visibility = View.GONE // Ocultar el botón después de reclamar la recompensa
            logro.toggleImagen() // Cambiar la imagen al reclamar la recompensa
            logro.reclamado = true // Marcar el logro como reclamado
            holder.imageInsignia.setImageResource(logro.imagenResource) // Actualizar la imagen en el ImageView
        }

        holder.itemView.setOnClickListener {
            // Mostrar el diálogo
            showLogroDialog(holder.itemView.context, logro.titulo, logro.descripcion)
        }
    }

    override fun getItemCount(): Int {
        return logrosList.size
    }

    private fun showLogroDialog(context: Context, titulo: String, descripcion: String) {
        val dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder.setTitle(titulo)
        dialogBuilder.setMessage(descripcion)
        dialogBuilder.setPositiveButton("Cerrar") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = dialogBuilder.create()
        dialog.show()
    }
}
