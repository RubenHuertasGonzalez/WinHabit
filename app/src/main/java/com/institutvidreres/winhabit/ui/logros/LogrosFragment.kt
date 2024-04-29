package com.institutvidreres.winhabit.ui.logros

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.institutvidreres.winhabit.databinding.FragmentLogrosBinding
import kotlinx.coroutines.launch

class LogrosFragment : Fragment() {
    private var _binding: FragmentLogrosBinding? = null
    private val binding get() = _binding!!

    private lateinit var logrosViewModel: LogrosViewModel
    private lateinit var logrosAdapter: LogrosAdapter
    private lateinit var userId: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLogrosBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Inicializa el ViewModel y el RecyclerView
        logrosViewModel = ViewModelProvider(this).get(LogrosViewModel::class.java)
        logrosAdapter = LogrosAdapter(logrosViewModel.logrosList)

        // Configura el RecyclerView
        val recyclerView: RecyclerView = binding.recyclerViewLogros
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = logrosAdapter

        // Obtenemos el ID del usuario actual
        userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        // Observador para la lista de logros
        logrosAdapter.onLogroClickListener = { logro ->
            reclamarRecompensa(logro)
        }

        cargarLogrosYVerificarNivel()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun cargarLogrosYVerificarNivel() {
        lifecycleScope.launch {
            try {
                logrosViewModel.obtenerEstadoLogrosUsuario()
                logrosViewModel.verificarLogrosCompletados()
                logrosAdapter.notifyDataSetChanged()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error al cargar los logros", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }

    private fun reclamarRecompensa(logro: LogrosItem) {
        lifecycleScope.launch {
            try {
                if (logro.completado) {
                    logrosViewModel.reclamarRecompensa(logro)
                    Toast.makeText(requireContext(), "Recompensa reclamada: +50 monedas", Toast.LENGTH_SHORT).show()
                    cargarLogrosYVerificarNivel() // Actualizar lista después de reclamar
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error al reclamar la recompensa", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }
}
