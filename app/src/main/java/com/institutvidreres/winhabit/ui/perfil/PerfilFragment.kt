package com.institutvidreres.winhabit.ui.perfil

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.institutvidreres.winhabit.MainActivity
import com.institutvidreres.winhabit.SharedViewModel
import com.institutvidreres.winhabit.adapter.PerfilAdapter
import com.institutvidreres.winhabit.databinding.FragmentPerfilBinding

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


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPerfilBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Llama al método de actualización en MainActivity
        (requireActivity() as MainActivity).updateNavigationDrawerEmail()

        perfilViewModel = ViewModelProvider(this).get(PerfilViewModel::class.java)
        sharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)

        viewManagerPersonajes = LinearLayoutManager(context)
        viewManagerBanner = LinearLayoutManager(context)

        // RecyclerView para mostrar la lista de personajes
        recyclerViewPersonajes = binding.recyclerViewPerfilPersonajes.apply {
            setHasFixedSize(true)
            layoutManager = viewManagerPersonajes
        }

        // Observa los cambios en la lista de personajes y actualiza el RecyclerView
        perfilViewModel.getPersonajes(requireContext())?.observe(viewLifecycleOwner, Observer { personajesList ->
            personajesList?.let {
                personajesAdapter = PerfilAdapter(it, requireContext(), sharedViewModel) { selectedItem ->
                    perfilViewModel.setSelectedItem(selectedItem)
                    // Toast.makeText(requireContext(), "$selectedItem", Toast.LENGTH_SHORT).show()

                    // Guardar el valor en las preferencias compartidas
                    val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                    sharedPreferences.edit().putInt("user_character", selectedItem.firebaseId).apply()

                    // Actualizar la ID del personaje en MainActivity
                    (requireActivity() as MainActivity).updateFirebaseIdCharacter(selectedItem.firebaseId)
                }
                recyclerViewPersonajes.adapter = personajesAdapter
            }
        })

        // RecyclerView para mostrar la lista de banners
        recyclerViewBanners = binding.recyclerViewPerfilBanners.apply {
            setHasFixedSize(true)
            layoutManager = viewManagerBanner
        }

        // Observa los cambios en la lista de banners y actualiza el RecyclerView
        perfilViewModel.getBanners(requireContext())?.observe(viewLifecycleOwner, Observer { bannersList ->
            bannersList?.let {
                bannersAdapter = PerfilAdapter(it, requireContext(), sharedViewModel) { selectedItem ->
                    perfilViewModel.setSelectedItem(selectedItem)
                    // Toast.makeText(requireContext(), "$selectedItem", Toast.LENGTH_SHORT).show()

                    // Guardar el valor en las preferencias compartidas
                    val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                    sharedPreferences.edit().putInt("user_banner", selectedItem.firebaseId).apply()

                    // Actualizar la ID del banner en MainActivity
                    (requireActivity() as MainActivity).updateFirebaseIdBanner(selectedItem.firebaseId)
                }
                recyclerViewBanners.adapter = bannersAdapter
            }
        })

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
