package com.institutvidreres.winhabit.ui.config

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.institutvidreres.winhabit.SharedViewModel
import com.institutvidreres.winhabit.databinding.FragmentConfBinding
import com.institutvidreres.winhabit.ui.login.AuthActivity
import com.institutvidreres.winhabit.ui.login.RegisterActivity

class ConfFragment : Fragment() {

    private var _binding: FragmentConfBinding? = null
    // Esta propiedad solo es válida entre onCreateView y onDestroyView.
    private val binding get() = _binding!!

    private lateinit var viewModel: ConfViewModel
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConfBinding.inflate(inflater, container, false)
        val view = binding.root

        viewModel = ViewModelProvider(this).get(ConfViewModel::class.java)
        sharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)

        binding.changePasswordButton.setOnClickListener {
            val newPassword = binding.newPasswordEditText.text.toString()
            viewModel.changePassword(newPassword)
        }

        binding.deleteAccountButton.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Confirmar eliminación de cuenta")
                .setMessage("¿Estás seguro de que quieres eliminar tu cuenta?")
                .setPositiveButton("Sí") { _, _ ->
                    viewModel.deleteUser()

                    sharedViewModel.signOut()

                    // Redirige al usuario a AuthActivity
                    val intent = Intent(activity, AuthActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                }
                .setNegativeButton("No", null)
                .show()
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
