package com.institutvidreres.winhabit.tareas

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.institutvidreres.winhabit.R
import com.institutvidreres.winhabit.databinding.FragmentCrearTareaBinding
import com.institutvidreres.winhabit.tareas.Tarea
import com.institutvidreres.winhabit.tareas.TareasAdapter
import com.institutvidreres.winhabit.tareas.TareasViewModel

class CrearTareaFragment : Fragment() {

    private val TAG = "CrearTareaFragment"
    private var _binding: FragmentCrearTareaBinding? = null
    private val binding get() = _binding!!

    private lateinit var tareasViewModel: TareasViewModel
    private lateinit var tareasAdapter: TareasAdapter
    private val firestoreDB = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCrearTareaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tareasViewModel = ViewModelProvider(requireActivity()).get(TareasViewModel::class.java)

        tareasAdapter = TareasAdapter(emptyList())
        val recyclerView: RecyclerView? = view.findViewById(R.id.RecyclerViewTareas)
        recyclerView?.let {
            it.layoutManager = LinearLayoutManager(context)
            it.adapter = tareasAdapter
        }

        val buttonCrearTarea: Button = binding.buttonAcabarDeCrear
        buttonCrearTarea.setOnClickListener {
            agregarTarea()
        }
    }

    private fun agregarTarea() {
        val nombre = binding.editTextNombreTarea.text.toString()
        val descripcion = binding.editTextDescription.text.toString()
        val dificultad = obtenerDificultadSeleccionada()
        val duracion = obtenerDuracionSeleccionada()

        if (nombre.isNotEmpty() && descripcion.isNotEmpty() && dificultad != null && duracion != null) {
            // Obtener ID de usuario actual
            val userId = auth.currentUser?.uid

            if (userId != null) {
                // Crear objeto Tarea con información de usuario
                val nuevaTarea = Tarea(nombre, descripcion, dificultad, duracion, userId)

                // Agregar tarea al ViewModel compartido
                tareasViewModel.agregarTarea(nuevaTarea)

                // Guardar en Firestore
                guardarEnFirestore(nuevaTarea)

                // Limpiar los campos después de agregar la tarea
                binding.editTextNombreTarea.text.clear()
                binding.editTextDescription.text.clear()

                // Opcional: Deseleccionar los RadioButtons
                binding.dificultad.clearCheck()
                binding.duracion.clearCheck()

                findNavController().navigate(R.id.action_crearTareaFragment_to_inicioFragment)
            } else {
                Log.e(TAG, "Error: No se pudo obtener el ID de usuario actual.")
            }
        } else {
            Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun guardarEnFirestore(tarea: Tarea) {
        firestoreDB.collection("tasks")
            .add(tarea)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "Documento agregado con ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error al agregar documento", e)
                Toast.makeText(context, "Error al agregar tarea", Toast.LENGTH_SHORT).show()
            }
    }

    private fun obtenerDificultadSeleccionada(): String? {
        val radioButtonId = binding.dificultad.checkedRadioButtonId
        return if (radioButtonId != -1) {
            val radioButton = view?.findViewById<RadioButton>(radioButtonId)
            radioButton?.text.toString()
        } else {
            null
        }
    }

    private fun obtenerDuracionSeleccionada(): String? {
        val radioButtonId = binding.duracion.checkedRadioButtonId
        return if (radioButtonId != -1) {
            val radioButton = view?.findViewById<RadioButton>(radioButtonId)
            radioButton?.text.toString()
        } else {
            null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

