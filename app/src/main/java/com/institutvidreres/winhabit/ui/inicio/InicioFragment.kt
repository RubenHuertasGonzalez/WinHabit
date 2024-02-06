package com.institutvidreres.winhabit.ui.inicio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.institutvidreres.winhabit.R
import com.institutvidreres.winhabit.databinding.FragmentInicioBinding
import com.institutvidreres.winhabit.tareas.TareasAdapter
import com.institutvidreres.winhabit.tareas.TareasViewModel

class InicioFragment : Fragment() {

    private var _binding: FragmentInicioBinding? = null
    private val binding get() = _binding!!

    private lateinit var tareasViewModel: TareasViewModel
    private lateinit var tareasAdapter: TareasAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInicioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tareasViewModel = ViewModelProvider(requireActivity()).get(TareasViewModel::class.java)

        tareasAdapter = TareasAdapter(tareasViewModel.tareasList.value ?: emptyList())
        val recyclerView: RecyclerView = binding.RecyclerViewTareas
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = tareasAdapter

        val buttonCreateTask: Button = binding.buttonCreateTask

        buttonCreateTask.setOnClickListener {
            // Obtén el NavController
            val navController = findNavController()

            // Navega al fragmento CrearTareaFragment
            navController.navigate(R.id.action_inicioFragment_to_crearTareaFragment)
        }

        tareasViewModel.tareasList.observe(viewLifecycleOwner) { tareas ->
            // Actualizar el adaptador con las nuevas tareas
            tareasAdapter.actualizarLista(tareas)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

