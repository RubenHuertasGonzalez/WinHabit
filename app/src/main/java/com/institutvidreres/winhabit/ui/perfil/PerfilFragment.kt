package com.institutvidreres.winhabit.ui.perfil

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.navigation.NavigationView
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

        // Recupera el correo desde SharedPreferences
        val sharedPreferences = requireActivity().getSharedPreferences("user_info", MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("email", "")

        // Obtén el header del NavigationView
        val navHeader =
            requireActivity().findViewById<NavigationView>(R.id.nav_view).getHeaderView(0)

        // Establece el correo en el textViewCorreoPerfil de nav_header_main.xml
        navHeader.findViewById<TextView>(R.id.textViewCorreoPerfil).text = userEmail

        return root
    }
}

