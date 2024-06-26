package com.institutvidreres.winhabit.ui.config

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.institutvidreres.winhabit.R
import com.institutvidreres.winhabit.SharedViewModel
import com.institutvidreres.winhabit.databinding.FragmentConfBinding
import com.institutvidreres.winhabit.ui.login.AuthActivity

class ConfFragment : Fragment() {

    private var _binding: FragmentConfBinding? = null
    // Esta propiedad solo es válida entre onCreateView y onDestroyView.
    private val binding get() = _binding!!

    private lateinit var viewModel: ConfViewModel
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var modeSpinner: Spinner
    private var isInitialSelection = true
    private var lastSelectedMode: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConfBinding.inflate(inflater, container, false)
        val view = binding.root

        modeSpinner = binding.modeSpinner

        (activity as AppCompatActivity).supportActionBar?.title = "CONFIGURACIÓN"

        viewModel = ViewModelProvider(this).get(ConfViewModel::class.java)
        sharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)

        binding.changePasswordButton.setOnClickListener {
            val currentPassword = binding.currentPasswordEditText.text.toString()
            val newPassword = binding.newPasswordEditText.text.toString()
            val confirmPassword = binding.confirmPasswordEditText.text.toString()

            if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(requireContext(), "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.changePassword(currentPassword, newPassword, confirmPassword)
            // Borrar los campos de entrada
            binding.currentPasswordEditText.text.clear()
            binding.newPasswordEditText.text.clear()
            binding.confirmPasswordEditText.text.clear()
        }

        binding.deleteAccountButton.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Confirmar eliminación de cuenta")
                .setMessage("¿Estás seguro de que quieres eliminar tu cuenta?")
                .setPositiveButton("Sí") { _, _ ->
                    viewModel.deleteUser()
                    sharedViewModel.signOut()
                    val intent = Intent(activity, AuthActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                }
                .setNegativeButton("No", null)
                .show()
        }

        // Observa el LiveData del ViewModel para mostrar mensajes
        viewModel.messageLiveData.observe(viewLifecycleOwner, Observer { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        })

        val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        lastSelectedMode = sharedPreferences.getInt("LastSelectedMode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

        val adapter = CustomArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            resources.getStringArray(R.array.modes_array)
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        modeSpinner.adapter = adapter

        // Establecer el texto del spinner según el último modo seleccionado
        val defaultText = if (lastSelectedMode == AppCompatDelegate.MODE_NIGHT_YES) {
            "DARK"
        } else {
            "LIGHT"
        }
        modeSpinner.setSelection(if (defaultText == "LIGHT") 0 else 1)

        modeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (!isInitialSelection) {
                    val selectedMode = when (position) {
                        0 -> AppCompatDelegate.MODE_NIGHT_NO
                        else -> AppCompatDelegate.MODE_NIGHT_YES
                    }
                    AppCompatDelegate.setDefaultNightMode(selectedMode)
                    lastSelectedMode = selectedMode
                    sharedPreferences.edit().putInt("LastSelectedMode", lastSelectedMode).apply()
                }
                isInitialSelection = false
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Aplicar colores basados en el último modo seleccionado
        if (lastSelectedMode == AppCompatDelegate.MODE_NIGHT_YES) {
            binding.newPasswordEditText.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            binding.currentPasswordEditText.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            binding.confirmPasswordEditText.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            binding.titleTemaApp.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            binding.titleConfSeguridad.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
