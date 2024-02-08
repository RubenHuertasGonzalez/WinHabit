package com.institutvidreres.winhabit.ui.logros

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.institutvidreres.winhabit.databinding.FragmentLogrosBinding

class LogrosFragment : Fragment() {

    private var _binding: FragmentLogrosBinding? = null
    private val binding get() = _binding!!

    private lateinit var logrosViewModel: LogrosViewModel
    private lateinit var logrosAdapter: LogrosAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLogrosBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Inicializa el ViewModel y el RecyclerView
        logrosViewModel = ViewModelProvider(this).get(LogrosViewModel::class.java)
        logrosAdapter = LogrosAdapter(logrosViewModel.logrosList)

        // Configura el RecyclerView
        val recyclerView: RecyclerView = binding.recyclerViewLogros
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = logrosAdapter

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
