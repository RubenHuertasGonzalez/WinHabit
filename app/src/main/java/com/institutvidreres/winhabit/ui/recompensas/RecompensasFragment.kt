package com.institutvidreres.winhabit.ui.recompensas

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        setupRecyclerView(binding.recyclerViewPersonajes, viewModel.personajesList)
        setupRecyclerView(binding.recyclerViewPersonajesPremium, viewModel.personajesPremiumList)
        setupRecyclerView(binding.recyclerViewBannersPerfil, viewModel.bannersPerfil)
        setupRecyclerView(binding.recyclerViewBannersMulticolorPerfil, viewModel.bannersMulticolorPerfil)

        return root
    }

    private fun setupRecyclerView(recyclerView: RecyclerView, dataList: List<Recompensa>) {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = RecompensasAdapter(dataList, requireContext())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
