package com.institutvidreres.winhabit.ui.inicio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.institutvidreres.winhabit.MainActivity
import com.institutvidreres.winhabit.R
import com.institutvidreres.winhabit.SharedViewModel
import com.institutvidreres.winhabit.databinding.FragmentInicioBinding
import com.institutvidreres.winhabit.tareas.Tarea
import com.institutvidreres.winhabit.tareas.TareasAdapter
import kotlin.random.Random

class InicioFragment : Fragment(), TareasAdapter.OnClickListener {

    private lateinit var tareasAdapter: TareasAdapter
    private lateinit var inicioViewModel: InicioViewModel
    private lateinit var sharedViewModel: SharedViewModel
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var firestoreDB: FirebaseFirestore

    private lateinit var binding: FragmentInicioBinding

    private var progresoActual = 0
    private var nivel = 1
    private val nivelMaximo = 20
    private var nivelMaximoAlcanzado = false
    private var porcentajeNecesario = MutableLiveData(20)
    private val incrementoPorcentaje = 20

    private var progresoActualMonedas = 0
    private var monedas = 0

    private lateinit var healthBar: ProgressBar
    private var vidasPerdidas = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInicioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        inicioViewModel = ViewModelProvider(requireActivity()).get(InicioViewModel::class.java)

        firestoreDB = FirebaseFirestore.getInstance()

        // Obtiene las tareas de Firestore
        obtenerTareasDesdeFirestore()

        // Observa los cambios en la URL de la imagen
        sharedViewModel.selectedImageUri.observe(viewLifecycleOwner) { imageUrl ->
            // Cargar la imagen en el ImageView utilizando Glide
            Glide.with(this)
                .load(imageUrl)
                .into(binding.imageView2)
        }

        sharedViewModel.selectedBannerId.observe(viewLifecycleOwner) { bannerId ->
            (requireActivity() as MainActivity).updateFirebaseIdBanner(bannerId)
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

        inicioViewModel.healthBarWidth.observe(viewLifecycleOwner, Observer { nuevaAnchura ->
            // Actualiza la anchura de la barra de vida en la interfaz de usuario
            actualizarAnchuraBarraVidaEnInterfaz(nuevaAnchura)
        })

        inicioViewModel.coinsUser.observe(viewLifecycleOwner) { coins ->
            // Actualizar las monedas en la interfaz de usuario
            binding.textViewMonedas.text = coins.toString()
        }

        inicioViewModel.levelUser.observe(viewLifecycleOwner) { level ->
            // Actualizar el nivel en la interfaz de usuario
            binding.textViewNivel.text = "Nivel $level"
        }

        inicioViewModel.expUser.observe(viewLifecycleOwner) { exp ->
            // Puedes realizar acciones adicionales aquí si es necesario
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        // Guardar el estado actual de las monedas, la experiencia y el nivel
        inicioViewModel.actualizarMonedas(binding.textViewMonedas.text.toString().toInt())
        inicioViewModel.actualizarNivel(binding.textViewNivel.text.toString().split(" ")[1].toInt())

        // Obtener la experiencia actual del ViewModel y guardarla
        val experienciaActual = inicioViewModel.expUser.value ?: 0
        inicioViewModel.actualizarExperiencia(experienciaActual)
    }

    private fun obtenerTareasDesdeFirestore() {
        val userId = auth.currentUser?.uid // Obtener el ID del usuario actual

        userId?.let { uid ->
            firestoreDB.collection("tasks")
                .whereEqualTo("userId", uid) // Filtrar por userId igual al ID del usuario actual
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val tareas = mutableListOf<Tarea>()
                    for (document in querySnapshot.documents) {
                        val tarea = document.toObject(Tarea::class.java)
                        tarea?.let { tareas.add(it) }
                    }
                    // Verificar si la lista de tareas está vacía antes de actualizar el adaptador
                    if (tareas.isEmpty()) {
                        Toast.makeText(context, "No hay tareas disponibles", Toast.LENGTH_SHORT).show()
                    } else {
                        // Actualiza el adaptador con las nuevas tareas obtenidas de Firestore
                        tareasAdapter.actualizarLista(tareas)
                    }
                }
                .addOnFailureListener { e ->
                    // Manejar la excepción mostrando un mensaje de error
                    Toast.makeText(context, "Error al obtener tareas: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }


    override fun onDecrementClick(position: Int) {
        val totalVidas = 11 // Total de vidas
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

    override fun onIncrementClick(position: Int) {
        // Verificar si se ha alcanzado el nivel máximo
        if (!nivelMaximoAlcanzado) {
            // Incrementar la experiencia
            val nuevaExperiencia = (inicioViewModel.expUser.value ?: 0) + 1 // Incremento de experiencia

            // Actualizar la experiencia en el ViewModel
            inicioViewModel.actualizarExperiencia(nuevaExperiencia)

            // Generar un número aleatorio entre 1 y 5 para el progreso
            val incrementoProgreso = Random.nextInt(1, 6)

            // Generar un número aleatorio entre 5 y 10 para las monedas
            val incrementoMonedas = Random.nextInt(5, 11)

            // Sumar los números aleatorios al progreso actual y a las monedas
            progresoActual += incrementoProgreso
            progresoActualMonedas += incrementoMonedas

            // Verificar si se alcanzó o superó el porcentaje necesario
            if (progresoActual >= porcentajeNecesario.value!!) {
                // Incrementar el nivel y reiniciar el progreso
                nivel++
                progresoActual = 0

                // Aumentar la dificultad para el próximo nivel
                porcentajeNecesario.value = porcentajeNecesario.value!! + incrementoPorcentaje

                // Verificar si el nivel alcanzó el nivel máximo después de la actualización
                if (nivel >= nivelMaximo) {
                    // Establecer la bandera de nivel máximo alcanzado
                    nivelMaximoAlcanzado = true
                    // Establecer el nivel en el nivel máximo
                    nivel = nivelMaximo

                    // Mostrar el mensaje de nivel máximo en el TextView correspondiente
                    binding.textViewPorcentajeNivel.text = "¡NIVEL MAXIMO!"
                } else {
                    // Actualizar el texto del nivel
                    binding.textViewNivel.text = "Nivel $nivel"
                    // Actualizar el texto del progreso (textViewLvl)
                    binding.textViewPorcentajeNivel.text = "$progresoActual / ${porcentajeNecesario.value}"
                }
            } else {
                // Actualizar el texto del progreso (textViewLvl) si no se ha alcanzado el porcentaje necesario
                binding.textViewPorcentajeNivel.text = "$progresoActual / ${porcentajeNecesario.value}"
            }

            // Mostrar un mensaje de tarea completada
            Toast.makeText(context, "¡Tarea completada!", Toast.LENGTH_SHORT).show()

            // Actualizar y mostrar las monedas
            binding.textViewMonedas.text = progresoActualMonedas.toString()
        }
    }


    private fun actualizarBarraDeVida(vidasRestantes: Int, totalVidas: Int) {
        val porcentajeVidasRestantes = vidasRestantes.toFloat() / totalVidas.toFloat() * 100 // Calcular porcentaje
        healthBar.progress = porcentajeVidasRestantes.toInt() // Actualizar el progreso de la ProgressBar
    }

    private fun actualizarAnchuraBarraVidaEnInterfaz(nuevaAnchura: Float) {
        healthBar.scaleX = nuevaAnchura
    }

}
