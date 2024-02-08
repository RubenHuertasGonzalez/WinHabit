package com.institutvidreres.winhabit.ui.inicio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.institutvidreres.winhabit.R
import com.institutvidreres.winhabit.SharedViewModel
import com.institutvidreres.winhabit.databinding.FragmentInicioBinding
import com.institutvidreres.winhabit.tareas.TareasAdapter
import com.institutvidreres.winhabit.tareas.TareasViewModel

class InicioFragment : Fragment(), TareasAdapter.OnClickListener {

    private val TAG = "InicioFragment"
    private var _binding: FragmentInicioBinding? = null

    private val binding get() = _binding!!

    private lateinit var tareasAdapter: TareasAdapter
    private lateinit var inicioViewModel: InicioViewModel
    private lateinit var sharedViewModel: SharedViewModel

    private lateinit var healthBar: ImageView
    private var vidasPerdidas = 0

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

        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        inicioViewModel = ViewModelProvider(requireActivity()).get(InicioViewModel::class.java)

        // Observa los cambios en la URL de la imagen
        sharedViewModel.selectedImageUri.observe(viewLifecycleOwner) { imageUrl ->
            // Cargar la imagen en el ImageView utilizando Glide
            Glide.with(this)
                .load(imageUrl)
                .into(binding.imageView2)
        }

        healthBar = binding.healthbar
        tareasAdapter = TareasAdapter(sharedViewModel.tareasList.value ?: emptyList(), this)
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

        sharedViewModel.tareasList.observe(viewLifecycleOwner) { tareas ->
            // Actualizar el adaptador con las nuevas tareas
            tareasAdapter.actualizarLista(tareas)
        }

        inicioViewModel.healthBarWidth.observe(viewLifecycleOwner, Observer { nuevaAnchura ->
            // Actualiza la anchura de la barra de vida en la interfaz de usuario
            actualizarAnchuraBarraVidaEnInterfaz(nuevaAnchura)
        })

        //TODO: Arreglar tareas para cada usuario

        // Llamar a esta función al iniciar sesión para cargar las tareas del usuario
        // obtenerTareasDeUsuario()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDecrementClick(position: Int) {
        val totalVidas = 5 // Total de vidas
        vidasPerdidas++

        val vidasRestantes = totalVidas - vidasPerdidas
        if (vidasRestantes >= 0) {
            actualizarBarraDeVida(vidasRestantes, totalVidas)
            Toast.makeText(context, "Vidas restantes: $vidasRestantes", Toast.LENGTH_SHORT).show()
            if (vidasRestantes == 0) {
                Toast.makeText(context, "¡Te has quedado sin vidas!", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Ya no quedan más vidas", Toast.LENGTH_SHORT).show()
        }
    }
    // TODO: Al subir de nivel se reiniciara por completo la vida del jugador y se le subira la vida maxima, tambien se pueden comprar vidas o subir la vida maxima en la sección de recompensas
    private fun actualizarBarraDeVida(vidasRestantes: Int, totalVidas: Int) {
        val porcentajeVidasRestantes = vidasRestantes.toFloat() / totalVidas.toFloat()
        val escala = porcentajeVidasRestantes
        healthBar.scaleX = escala
        inicioViewModel.actualizarAnchuraBarraVida(escala)
    }

    override fun onIncrementClick(position: Int) {
        Toast.makeText(context, "¡Tarea completada!", Toast.LENGTH_SHORT).show()
    }

    private fun actualizarAnchuraBarraVidaEnInterfaz(nuevaAnchura: Float) {
        // Actualiza la anchura de la barra de vida en la interfaz de usuario
        healthBar.scaleX = nuevaAnchura
    }

}

