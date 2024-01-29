package com.institutvidreres.winhabit.ui.inicio

import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.institutvidreres.winhabit.MainActivity
import com.institutvidreres.winhabit.databinding.FragmentBannerBinding

class BannerFragment : Fragment() {

    private var _binding: FragmentBannerBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //TODO: Duplicado de fragments
        _binding = FragmentBannerBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Obtén la imagen de perfil de MainActivity
        val mainActivity = activity as MainActivity
        val profileDrawable: Drawable? = mainActivity.importImage()

        // Establece la imagen de perfil en la ImageView de tu fragmento
        profileDrawable?.let {
            binding.imageView2.setImageDrawable(it)
        }

        return root
    }
}