package com.institutvidreres.winhabit.ui.recompensas

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

class RecompensasAdapter(
    private val recompensasList: List<Recompensa>,
    private val context: Context,
    private val viewModel: RecompenasViewModel
) : RecyclerView.Adapter<RecompensasAdapter.RecompensaViewHolder>() {

    class RecompensaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imagenImageView: ImageView = itemView.findViewById(R.id.imagen_recompensa)
        val descripcionTextView: TextView = itemView.findViewById(R.id.descripcion_recompensa)
        val botonRecompensa: Button = itemView.findViewById(R.id.boton_recompensa)
        val imagenMoneda: ImageView = itemView.findViewById(R.id.imagen_moneda)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecompensaViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recompensa, parent, false)
        return RecompensaViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecompensaViewHolder, position: Int) {
        val recompensa = recompensasList[position]
        holder.imagenImageView.setImageResource(recompensa.imagenResId)
        holder.descripcionTextView.text = "${recompensa.descripcion}"

        // Configurar el botón y la moneda
        holder.botonRecompensa.text = "${recompensa.precio} monedas"
        holder.botonRecompensa.setOnClickListener {
            mostrarDialogoCompra(recompensa)
        }
        holder.imagenMoneda.setImageResource(R.drawable.moneda)

        // Actualizar la cantidad al hacer clic en el botón
        holder.botonRecompensa.setOnLongClickListener {
            // Actualizar la descripción con la nueva cantidad
            holder.descripcionTextView.text = "${recompensa.descripcion}"
            true
        }
    }

    override fun getItemCount(): Int {
        return recompensasList.size
    }

    private fun mostrarDialogoCompra(recompensa: Recompensa) {
        Log.d("RecompensasAdapter", "mostrarDialogoCompra ejecutado")  // Agregar este log
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Confirmar Compra")
        builder.setMessage("¿Te gustaría comprar '${recompensa.descripcion}' por ${recompensa.precio} monedas?")
        builder.setPositiveButton("CONFIRMAR") { _, _ ->
            viewModel.newRecompensa(context, recompensa.nombre, recompensa.firebaseId, recompensa.imagenResId, recompensa.descripcion, recompensa.precio)
            // Añadir un Toast cuando se confirme la compra
            val mensaje = "${recompensa.descripcion} comprado por ${recompensa.precio} monedas!"
            Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton("CANCELAR") { _, _ ->
            // Añadir un Toast cuando se cancele la compra
            val mensaje = "Compra de ${recompensa.descripcion} cancelada!"
            Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
        }
        builder.show()
    }
}
