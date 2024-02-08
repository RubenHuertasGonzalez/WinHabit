package com.institutvidreres.winhabit.ui.recompensas

import android.app.AlertDialog
import android.content.Context
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
    private val context: Context
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
        holder.botonRecompensa.text = "${recompensa.precio * recompensa.cantidad} monedas"
        holder.botonRecompensa.setOnClickListener {
            mostrarDialogoCompra(recompensa)
        }
        holder.imagenMoneda.setImageResource(R.drawable.moneda)

        // Actualizar la cantidad al hacer clic en el botón
        holder.botonRecompensa.setOnLongClickListener {
            // Incrementar la cantidad al mantener presionado el botón
            recompensa.cantidad++
            // Actualizar la descripción con la nueva cantidad
            holder.descripcionTextView.text = "${recompensa.descripcion} (x${recompensa.cantidad})"
            true
        }
    }

    override fun getItemCount(): Int {
        return recompensasList.size
    }

    private fun mostrarDialogoCompra(recompensa: Recompensa) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Confirmar Compra")
        builder.setMessage("¿Te gustaría comprar ${recompensa.cantidad} '${recompensa.descripcion}' por ${recompensa.precio * recompensa.cantidad} monedas?")
        builder.setPositiveButton("CONFIRMAR") { _, _ ->
            // Lógica para procesar la compra
            val mensajeCompra = "¡${recompensa.cantidad} '${recompensa.descripcion}' comprado por ${recompensa.precio * recompensa.cantidad} monedas!"
            Toast.makeText(context, mensajeCompra, Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton("CANCELAR") { _, _ ->
            Toast.makeText(context, "COMPRA CANCELADA", Toast.LENGTH_SHORT).show()
        }
        builder.show()
    }
}
