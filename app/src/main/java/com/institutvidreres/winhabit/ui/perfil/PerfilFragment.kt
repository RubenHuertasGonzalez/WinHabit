package com.institutvidreres.winhabit.ui.perfil

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.institutvidreres.winhabit.MainActivity
import com.institutvidreres.winhabit.SharedViewModel
import com.institutvidreres.winhabit.adapter.PerfilAdapter
import com.institutvidreres.winhabit.databinding.FragmentPerfilBinding
import kotlinx.coroutines.launch

class PerfilFragment : Fragment() {

    private var _binding: FragmentPerfilBinding? = null
    private val binding get() = _binding!!

    private lateinit var perfilViewModel: PerfilViewModel
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var recyclerViewPersonajes: RecyclerView
    private lateinit var recyclerViewBanners: RecyclerView
    private lateinit var personajesAdapter: RecyclerView.Adapter<*>
    private lateinit var bannersAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManagerPersonajes: RecyclerView.LayoutManager
    private lateinit var viewManagerBanner: RecyclerView.LayoutManager
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPerfilBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()

        // Llama al método de actualización en MainActivity
        (requireActivity() as MainActivity).updateNavigationDrawerEmail()
        (requireActivity() as MainActivity).updateNavigationDrawerUsername()

        perfilViewModel = ViewModelProvider(this).get(PerfilViewModel::class.java)
        sharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)

        viewManagerPersonajes = GridLayoutManager(context, 2)
        viewManagerBanner = GridLayoutManager(context, 2)

        // RecyclerView para mostrar la lista de personajes
        recyclerViewPersonajes = binding.recyclerViewPerfilPersonajes.apply {
            setHasFixedSize(true)
            layoutManager = viewManagerPersonajes
        }

        // RecyclerView para mostrar la lista de banners
        recyclerViewBanners = binding.recyclerViewPerfilBanners.apply {
            setHasFixedSize(true)
            layoutManager = viewManagerBanner
        }

        // Observar los datos del usuario y actualizar la interfaz de usuario
        actualizarDatosUsuario()

        // Observa los cambios en la lista de personajes y actualiza el RecyclerView
        firebaseAuth.currentUser?.uid?.let { userId ->
            perfilViewModel.getPersonajes(requireContext(), userId)?.observe(viewLifecycleOwner, Observer { personajesList ->
                personajesList?.let {
                    personajesAdapter = PerfilAdapter(it, requireContext(), sharedViewModel) { selectedItem ->
                        perfilViewModel.setSelectedItem(selectedItem)
                        // Guardar el valor en las preferencias compartidas
                        val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                        sharedPreferences.edit().putInt("user_character", selectedItem.firebaseId).apply()

                        // Actualizar la ID del personaje en MainActivity
                        (requireActivity() as MainActivity).updateFirebaseIdCharacter(selectedItem.firebaseId)

                        // Actualizar la ID del personaje en Firestore
                        lifecycleScope.launch {
                            perfilViewModel.actualizarCharacter(selectedItem.firebaseId)
                        }
                    }
                    recyclerViewPersonajes.adapter = personajesAdapter
                }
            })

            // Observa los cambios en la lista de banners y actualiza el RecyclerView
            perfilViewModel.getBanners(requireContext(), userId)?.observe(viewLifecycleOwner, Observer { bannersList ->
                bannersList?.let {
                    bannersAdapter = PerfilAdapter(it, requireContext(), sharedViewModel) { selectedItem ->
                        perfilViewModel.setSelectedItem(selectedItem)
                        // Guardar el valor en las preferencias compartidas
                        val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                        sharedPreferences.edit().putInt("user_banner", selectedItem.firebaseId).apply()

                        // Actualizar la ID del banner en MainActivity
                        (requireActivity() as MainActivity).updateFirebaseIdBanner(selectedItem.firebaseId)

                        // Actualizar la ID del banner en Firestore
                        lifecycleScope.launch {
                            perfilViewModel.actualizarBanner(selectedItem.firebaseId)
                        }
                    }
                    recyclerViewBanners.adapter = bannersAdapter
                }
            })
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun actualizarDatosUsuario() {
        // Utilizar coroutines para llamar a las funciones suspendidas del ViewModel
        lifecycleScope.launch {
            // Obtener el nivel del usuario
            val nivel = perfilViewModel.obtenerNivelUsuario()
            // Actualizar la interfaz de usuario con el nivel obtenido
            binding.textViewNivel.text = nivel.toString()

            // Obtener las monedas del usuario
            val monedas = perfilViewModel.obtenerMonedasUsuario()
            // Actualizar la interfaz de usuario con las monedas obtenidas
            binding.textViewMonedas.text = monedas.toString()

            // Obtener las tareas completadas del usuario
            val tareasCompletadas = perfilViewModel.obtenerTareasCompletadas()
            // Actualizar la interfaz de usuario con las tareas completadas obtenidas
            binding.textViewTareasCompletadas.text = tareasCompletadas.toString()

            // Obtener las recompensas compradas del usuario
            val recompensasCompradas = perfilViewModel.obtenerCantidadRecompensasCompradas()
            binding.textViewRecompensasDesbloqueadas.text = "$recompensasCompradas / 19"

            // Obtener username
            val user = perfilViewModel.obtenerNombreUsuario()
            binding.textViewUsername.text = user

            // Obtener username
            val email = perfilViewModel.obtenerCorreoUsuario()
            binding.textViewEmail.text = email
        }
    }
}
