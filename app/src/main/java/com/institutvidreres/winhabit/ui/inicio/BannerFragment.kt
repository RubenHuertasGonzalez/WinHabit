package com.institutvidreres.winhabit.ui.inicio

import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.institutvidreres.winhabit.MainActivity
import com.institutvidreres.winhabit.SharedViewModel
import com.institutvidreres.winhabit.databinding.FragmentBannerBinding

class BannerFragment : Fragment() {

    private var _binding: FragmentBannerBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBannerBinding.inflate(inflater, container, false)
        val root: View = binding.root

        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        // Observa los cambios en la URL de la imagen
        sharedViewModel.selectedImageUri.observe(viewLifecycleOwner) { imageUrl ->
            // Cargar la imagen en el ImageView utilizando Glide
            Glide.with(this)
                .load(imageUrl)
                .into(binding.imageView2)
        }

        return root
    }
}