// PerfilFragment.kt
package com.institutvidreres.winhabit.ui.perfil

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

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

        viewManager = LinearLayoutManager(context)

        recyclerView = binding.recyclerViewPerfilPersonajes.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
        }

        perfilViewModel.getPersonajes(requireContext())?.observe(viewLifecycleOwner, Observer { personajesList ->
            personajesList?.let {
                viewAdapter = PerfilAdapter(it, sharedViewModel) { selectedItem ->
                    perfilViewModel.setSelectedItem(selectedItem)
                    Toast.makeText(requireContext(), "$selectedItem", Toast.LENGTH_SHORT).show()

                    // Guardar el valor en las preferencias compartidas
                    val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", MODE_PRIVATE)
                    sharedPreferences.edit().putInt("user_character", selectedItem.firebaseId).apply()

                    (requireActivity() as MainActivity).updateFirebaseIdRecompensas(selectedItem.firebaseId)
                }
                recyclerView.adapter = viewAdapter
            }
        })

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
