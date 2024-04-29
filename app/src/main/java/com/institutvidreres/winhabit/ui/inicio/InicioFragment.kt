package com.institutvidreres.winhabit.ui.inicio

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
import com.institutvidreres.winhabit.model.InicioPerfil
import com.institutvidreres.winhabit.tareas.Tarea
import com.institutvidreres.winhabit.tareas.TareasAdapter
import kotlin.random.Random

class InicioFragment : Fragment(), TareasAdapter.OnClickListener {

    private lateinit var tareasAdapter: TareasAdapter
    private lateinit var inicioViewModel: InicioViewModel
    private lateinit var sharedViewModel: SharedViewModel
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var firestoreDB: FirebaseFirestore

    private lateinit var userId: String // ID del usuario actual
    private lateinit var inicioPerfil: InicioPerfil // Perfil del usuario

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
    val totalVidas = 11

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

        userId = auth.currentUser?.uid.toString() // Obtener el ID del usuario actual

        // Obtener el total de vidas desde el ViewModel compartido
        sharedViewModel.totalVidas.value = totalVidas

        cargarPerfilUsuario()

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

        // Guardar los valores del perfil al cerrar sesión
        guardarPerfilUsuario()
    }

    private fun cargarPerfilUsuario() {
        // Obtener referencia al documento de perfil del usuario actual
        val perfilRef = firestoreDB.collection("profiles").document(userId)

        // Obtener los datos del documento de perfil
        perfilRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                // El documento existe, cargar los valores del perfil en la aplicación
                inicioPerfil = document.toObject(InicioPerfil::class.java) ?: InicioPerfil()
                cargarValoresPerfil(inicioPerfil)
            } else {
                // El documento no existe, crear un nuevo perfil para el usuario
                crearNuevoPerfil()
            }
        }.addOnFailureListener { e ->
            // Manejar el error al obtener el documento
            Log.e(TAG, "Error al obtener el documento de perfil: $e")
        }
    }

    private fun cargarValoresPerfil(inicioPerfil: InicioPerfil) {
        // Cargar los valores del perfil en la aplicación
        nivel = inicioPerfil.nivel
        monedas = inicioPerfil.monedas
        vidasPerdidas = inicioPerfil.vidasPerdidas
        // Otros campos del perfil...

        // Actualizar la interfaz de usuario con los valores del perfil
        // (por ejemplo, TextViews, ProgressBar, etc.)
        actualizarInterfazUsuario(inicioPerfil)
    }

    private fun crearNuevoPerfil() {
        // Crear un nuevo documento de perfil para el usuario actual
        val nuevoInicioPerfil = InicioPerfil(userId = userId)

        // Añadir el documento a la colección "profiles" con el ID del usuario actual
        firestoreDB.collection("profiles").document(userId)
            .set(nuevoInicioPerfil)
            .addOnSuccessListener {
                Log.d(TAG, "Documento de perfil creado correctamente")
                // Cargar el nuevo perfil
                cargarValoresPerfil(nuevoInicioPerfil)
            }
            .addOnFailureListener { e ->
                // Manejar el error al crear el documento
                Log.e(TAG, "Error al crear el documento de perfil: $e")
            }
    }

    private fun guardarPerfilUsuario() {
        // Actualizar el documento de perfil con los valores actuales del perfil
        val perfilRef = firestoreDB.collection("profiles").document(userId)

        val datosPerfil = hashMapOf(
            "nivel" to nivel,
            "monedas" to monedas,
            "vidasPerdidas" to vidasPerdidas
            // Otros campos del perfil...
        )

        perfilRef.set(datosPerfil)
            .addOnSuccessListener {
                Log.d(TAG, "Perfil actualizado correctamente")
            }
            .addOnFailureListener { e ->
                // Manejar el error al actualizar el documento
                Log.e(TAG, "Error al actualizar el perfil: $e")
            }
    }

    private fun actualizarInterfazUsuario(inicioPerfil: InicioPerfil) {
        // Actualizar TextViews
        binding.textViewNivel.text = "Nivel ${inicioPerfil.nivel}"
        binding.textViewMonedas.text = inicioPerfil.monedas.toString()
        binding.textViewPorcentajeNivel.text = "$progresoActual / ${porcentajeNecesario.value}"
        // Otros TextViews...

        // Actualizar ProgressBar
        val totalVidas = 11 // Total de vidas
        val vidasRestantes = totalVidas - inicioPerfil.vidasPerdidas
        val porcentajeVidasRestantes = vidasRestantes.toFloat() / totalVidas.toFloat() * 100 // Calcular porcentaje
        healthBar.progress = porcentajeVidasRestantes.toInt()

        // Otros elementos de la interfaz de usuario...
    }


    private fun obtenerTareasDesdeFirestore() {

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

            // Actualizar los datos en Firestore después del incremento
            actualizarDatosEnFirestore()
        }
    }

    // Función para actualizar los datos en Firestore después del incremento
    private fun actualizarDatosEnFirestore() {
        // Obtener la referencia al documento de perfil del usuario actual
        val perfilRef = firestoreDB.collection("profiles").document(userId)

        // Crear un mapa con los nuevos valores a actualizar
        val datosActualizados = hashMapOf(
            "nivel" to nivel,
            "monedas" to progresoActualMonedas,
            "exp" to inicioViewModel.expUser.value,
            // Otros campos del perfil...
        )

        // Actualizar los datos en Firestore
        perfilRef.update(datosActualizados as Map<String, Any>)
            .addOnSuccessListener {
                Log.d(TAG, "Datos actualizados correctamente en Firestore")
                // Obtener los datos actualizados del perfil del usuario
                obtenerDatosPerfilDesdeFirestore()
            }
            .addOnFailureListener { e ->
                // Manejar el error al actualizar los datos
                Log.e(TAG, "Error al actualizar los datos en Firestore: $e")
            }
    }

    // Función para obtener los datos actualizados del perfil del usuario desde Firestore
    private fun obtenerDatosPerfilDesdeFirestore() {
        // Obtener referencia al documento de perfil del usuario actual
        val perfilRef = firestoreDB.collection("profiles").document(userId)

        // Obtener los datos del documento de perfil
        perfilRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                // El documento existe, cargar los nuevos valores del perfil en la aplicación
                val nuevoInicioPerfil = document.toObject(InicioPerfil::class.java)
                nuevoInicioPerfil?.let {
                    cargarValoresPerfil(it)
                }
            } else {
                // Manejar el caso en que el documento no exista
                Log.e(TAG, "El documento de perfil no existe en Firestore")
            }
        }.addOnFailureListener { e ->
            // Manejar el error al obtener el documento
            Log.e(TAG, "Error al obtener el documento de perfil desde Firestore: $e")
        }
    }

// Dentro de la función onDecrementClick()

    override fun onDecrementClick(position: Int) { // Total de vidas
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

        // Actualizar los datos en Firestore después del decremento
        actualizarBarraDeVidaEnFirestore()
    }

    // Función para actualizar la barra de vida en Firestore después del decremento
    private fun actualizarBarraDeVidaEnFirestore() {
        // Obtener la referencia al documento de perfil del usuario actual
        val perfilRef = firestoreDB.collection("profiles").document(userId)

        // Crear un mapa con el nuevo valor de vidas perdidas
        val datosActualizados = hashMapOf(
            "vidasPerdidas" to vidasPerdidas
            // Otros campos del perfil...
        )

        // Actualizar los datos en Firestore
        perfilRef.update(datosActualizados as Map<String, Any>)
            .addOnSuccessListener {
                Log.d(TAG, "Datos actualizados correctamente en Firestore")
            }
            .addOnFailureListener { e ->
                // Manejar el error al actualizar los datos
                Log.e(TAG, "Error al actualizar los datos en Firestore: $e")
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
