package com.institutvidreres.winhabit.ui.inicio

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
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
    private var porcentajeNecesario = MutableLiveData(10)
    private val incrementoPorcentaje = 5

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

        observarNivelMaximoAlcanzado()

    }

    override fun onDestroyView() {
        super.onDestroyView()

        // Guardar el estado actual de las monedas, la experiencia y el nivel
        inicioViewModel.actualizarMonedas(binding.textViewMonedas.text.toString().toInt())
        inicioViewModel.actualizarNivel(binding.textViewNivel.text.toString().split(" ")[1].toInt())

        val tareasCompletadas = inicioViewModel.tareasUser.value ?: 0
        inicioViewModel.contadorTareas(tareasCompletadas)

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
        nivel = inicioPerfil.nivel
        monedas = inicioPerfil.monedas
        vidasPerdidas = inicioPerfil.vidasPerdidas
        progresoActual = inicioPerfil.experiencia
        porcentajeNecesario.value = inicioPerfil.porcentajeNecesario // Cargar experiencia
        nivelMaximoAlcanzado = inicioPerfil.nivelMaximoAlcanzado
        // Otros campos del perfil...
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

    private fun actualizarInterfazUsuario(inicioPerfil: InicioPerfil) {
        binding.textViewNivel.text = "Nivel ${inicioPerfil.nivel}"
        binding.textViewMonedas.text = inicioPerfil.monedas.toString()
        if (nivelMaximoAlcanzado) {
            binding.textViewPorcentajeNivel.text = "¡NIVEL MÁXIMO!"
        } else {
            binding.textViewPorcentajeNivel.text = "${inicioPerfil.experiencia} / ${porcentajeNecesario.value}"
        }
        // Otros elementos de la interfaz de usuario...
        val totalVidas = 11
        val vidasRestantes = totalVidas - inicioPerfil.vidasPerdidas
        val porcentajeVidasRestantes = vidasRestantes.toFloat() / totalVidas.toFloat() * 100
        healthBar.progress = porcentajeVidasRestantes.toInt()
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
        val incrementoProgreso = Random.nextInt(1, 6)
        val incrementoMonedas = Random.nextInt(5, 11)

        if (!nivelMaximoAlcanzado) {
            progresoActual += incrementoProgreso

            if (progresoActual >= porcentajeNecesario.value!!) {
                nivel++
                progresoActual = 0
                porcentajeNecesario.value = porcentajeNecesario.value!! + incrementoPorcentaje

                if (nivel >= nivelMaximo) {
                    nivelMaximoAlcanzado = true
                    nivel = nivelMaximo

                    // Actualiza el valor directamente en Firestore
                    val perfilRef = firestoreDB.collection("profiles").document(userId)
                    perfilRef.update("nivelMaximoAlcanzado", true)
                        .addOnSuccessListener {
                            Log.d(TAG, "¡Nivel máximo alcanzado! Actualizado correctamente en Firestore")
                        }
                        .addOnFailureListener { e ->
                            // Manejar el error al actualizar los datos
                            Log.e(TAG, "Error al actualizar el nivel máximo en Firestore: $e")
                        }

                    binding.textViewPorcentajeNivel.text = "¡NIVEL MÁXIMO!"
                    actualizarDatosEnFirestore()
                } else {
                    binding.textViewNivel.text = "Nivel $nivel"
                    binding.textViewPorcentajeNivel.text = "$progresoActual / ${porcentajeNecesario.value}"
                    // Actualiza los datos en Firestore solo si el nivel no ha alcanzado el máximo
                    inicioViewModel.actualizarMonedas(progresoActualMonedas)
                    actualizarDatosEnFirestore()
                }
            } else {
                binding.textViewPorcentajeNivel.text = "$progresoActual / ${porcentajeNecesario.value}"
            }
        }

        // Esta parte siempre debe ejecutarse
        inicioPerfil.tareasCompletadas += 1 // Tareas completadas
        progresoActualMonedas += incrementoMonedas // Incremento de monedas
        monedas += incrementoMonedas

        val fadeOutAnimation = AnimationUtils.loadAnimation(context, R.anim.fade_out)
        binding.textViewMonedas.startAnimation(fadeOutAnimation)

        val animTareaCompletada = AnimationUtils.loadAnimation(context, R.anim.animation_on_increment)
        binding.imageView2.startAnimation(animTareaCompletada)

        fadeOutAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
                binding.textViewMonedas.text = "..."
            }

            override fun onAnimationEnd(animation: Animation?) {
                binding.textViewMonedas.text = monedas.toString()
                val fadeInAnimation = AnimationUtils.loadAnimation(context, R.anim.fade_in)
                binding.textViewMonedas.startAnimation(fadeInAnimation)
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })
        actualizarDatosEnFirestore()
    }



    private fun observarNivelMaximoAlcanzado() {
        val perfilRef = firestoreDB.collection("profiles").document(userId)
        perfilRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e(TAG, "Error al observar el nivel máximo alcanzado: $error")
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                nivelMaximoAlcanzado = snapshot.getBoolean("nivelMaximoAlcanzado") ?: false

                // Actualizar la interfaz de usuario si el valor de nivelMaximoAlcanzado es true
                if (nivelMaximoAlcanzado) {
                    binding.textViewPorcentajeNivel.text = "¡NIVEL MÁXIMO!"
                }
            }
        }
    }


    // Función para actualizar los datos en Firestore después del incremento
    private fun actualizarDatosEnFirestore() {
        val perfilRef = firestoreDB.collection("profiles").document(userId)
        val datosActualizados = hashMapOf(
            "nivel" to nivel,
            "monedas" to monedas,
            "tareasCompletadas" to inicioPerfil.tareasCompletadas,
            "experiencia" to progresoActual, // Actualizar experiencia
            "porcentajeNecesario" to porcentajeNecesario.value,
            "nivelMaximoAlcanzado" to nivelMaximoAlcanzado
            // Otros campos del perfil...
        )
        perfilRef.update(datosActualizados as Map<String, Any>)
            .addOnSuccessListener {
                Log.d(TAG, "Datos actualizados correctamente en Firestore")
                obtenerDatosPerfilDesdeFirestore()
            }
            .addOnFailureListener { e ->
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

    override fun onDecrementClick(position: Int) {
        if (vidasPerdidas < totalVidas) { // Verificar si aún hay vidas restantes
            vidasPerdidas++

            animationRestarVida()

            val vidasRestantes = totalVidas - vidasPerdidas
            if (vidasRestantes >= 0) {
                actualizarBarraDeVida(vidasRestantes, totalVidas)
                Toast.makeText(context, "Vidas restantes: $vidasRestantes", Toast.LENGTH_SHORT).show()
                if (vidasRestantes == 0) {
                    mostrarDialogoSinVidas()
                }
            } else {
                Toast.makeText(context, "Ya no quedan más vidas", Toast.LENGTH_SHORT).show()
            }

            // Actualizar los datos en Firestore después del decremento
            actualizarBarraDeVidaEnFirestore()
        } else {
            Toast.makeText(context, "Ya no quedan más vidas", Toast.LENGTH_SHORT).show()
        }
    }

    private fun mostrarDialogoSinVidas() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("¡Te has quedado sin vidas!")
        builder.setMessage("Recibirás 3 vidas adicionales pero perderás todas tus monedas.")
        builder.setPositiveButton("Aceptar") { dialog, _ ->
        }
        builder.setOnDismissListener {
        }
        reiniciarVidasYMonedas()
        builder.show()
    }

    private fun reiniciarVidasYMonedas() {
        // Reiniciar vidas perdidas y monedas a 0
        vidasPerdidas = 8
        monedas = 0

        // Actualizar los datos en Firestore
        actualizarDatosEnFirestore()

        actualizarBarraDeVidaEnFirestore()
        // Actualizar la interfaz de usuario
        actualizarInterfazUsuario(inicioPerfil)
    }


    fun animationRestarVida(){
        // Crear una interpolación de aceleración
        val interpolator = AccelerateInterpolator()

        val moveLeft = ObjectAnimator.ofFloat(binding.imageView2, "translationX", 0f, -20f)
        moveLeft.duration = 100
        moveLeft.interpolator = interpolator

        val moveRightBack = ObjectAnimator.ofFloat(binding.imageView2, "translationX", -20f, 20f, 0f)
        moveRightBack.duration = 200
        moveRightBack.startDelay = 100 // Retraso para que comience después de la primera animación
        moveRightBack.interpolator = interpolator

        val animatorSet = AnimatorSet()
        animatorSet.playSequentially(moveLeft, moveRightBack)

        animatorSet.start()
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
