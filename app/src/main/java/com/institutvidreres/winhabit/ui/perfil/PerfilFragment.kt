// PerfilFragment.kt
package com.institutvidreres.winhabit.ui.perfil

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.institutvidreres.winhabit.MainActivity
import com.institutvidreres.winhabit.R
import com.institutvidreres.winhabit.databinding.FragmentPerfilBinding

class PerfilFragment : Fragment() {

    private var _binding: FragmentPerfilBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPerfilBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Oculta el banner
        requireActivity().findViewById<View>(R.id.banner).visibility = View.GONE

        // Llama al método de actualización en MainActivity
        (requireActivity() as MainActivity).updateNavigationDrawerEmail()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Muestra el banner cuando el fragmento se destruye
        requireActivity().findViewById<View>(R.id.banner).visibility = View.VISIBLE
        _binding = null
    }
}
