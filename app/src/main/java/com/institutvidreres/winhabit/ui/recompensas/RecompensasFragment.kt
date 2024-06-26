package com.institutvidreres.winhabit.ui.recompensas

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.institutvidreres.winhabit.databinding.FragmentRecompensasBinding


class RecompensasFragment : Fragment() {

    private var _binding: FragmentRecompensasBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: RecompenasViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecompensasBinding.inflate(inflater, container, false)
        val root: View = binding.root

        viewModel = ViewModelProvider(this).get(RecompenasViewModel::class.java)

        // Paso el contexto al instanciar los adaptadores
        setupRecyclerView(binding.recyclerViewPersonajes, viewModel.personajesList, viewModel)
        setupRecyclerView(binding.recyclerViewPersonajesPremium, viewModel.personajesPremiumList, viewModel)
        setupRecyclerView(binding.recyclerViewBannersPerfil, viewModel.bannersPerfil, viewModel)
        setupRecyclerView(binding.recyclerViewBannersMulticolorPerfil, viewModel.bannersMulticolorPerfil, viewModel)
        setupRecyclerView(binding.recyclerViewVidas, viewModel.vidasList, viewModel)

        val textViewTitulo = binding.textViewTitulo
        val textViewSubtitulo = binding.subtituloRecompensas


        // Mostrar el TextView nuevamente con una animación de desvanecimiento
        val animTitle = AnimationUtils.loadAnimation(context, com.institutvidreres.winhabit.R.anim.slide_from_top)
        textViewTitulo.startAnimation(animTitle)

        // Mostrar el TextView nuevamente con una animación de desvanecimiento
        val animSubTitle = AnimationUtils.loadAnimation(context, com.institutvidreres.winhabit.R.anim.animation_aumento)
        textViewSubtitulo.startAnimation(animSubTitle)

        return root
    }

    private fun setupRecyclerView(recyclerView: RecyclerView, dataList: List<Recompensa>, viewModel: RecompenasViewModel) {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = RecompensasAdapter(dataList, requireContext(), viewModel)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
