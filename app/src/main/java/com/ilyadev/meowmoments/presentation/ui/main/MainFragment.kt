package com.ilyadev.meowmoments.presentation.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import coil.load
import com.ilyadev.meowmoments.R
import com.ilyadev.meowmoments.databinding.FragmentMainBinding
import com.ilyadev.meowmoments.domain.model.CatFact
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainFragment : Fragment() {

    private val viewModel: MainViewModel by viewModels()
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnNextFact.setOnClickListener {
            viewModel.refreshFact()
        }

        // Наблюдение за состоянием UI с использованием repeatOnLifecycle
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is MainUiState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.contentScrollview.visibility = View.GONE
                        }
                        is MainUiState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            binding.contentScrollview.visibility = View.VISIBLE
                            bindFact(state.fact)
                        }
                        is MainUiState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            binding.contentScrollview.visibility = View.GONE
                            Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    private fun bindFact(fact: CatFact) {
        binding.tvFactDate.text = "Факт на ${fact.dateReceived}"
        binding.tvFactCategory.text = "#${fact.category}"
        binding.tvFactText.text = fact.text

        // Загружаем изображение через Coil
        binding.ivFactImage.load(fact.imageUrl) {
            crossfade(true)
            placeholder(R.drawable.placeholder_cat)
            error(R.drawable.error_cat)
        }

        // Получаем количество собранных фактов
        viewModel.getCollectedCount().observe(viewLifecycleOwner) { count ->
            binding.tvCollectionProgress.text = "Факт $count из ${viewModel.getTotalFactsCount()}"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}