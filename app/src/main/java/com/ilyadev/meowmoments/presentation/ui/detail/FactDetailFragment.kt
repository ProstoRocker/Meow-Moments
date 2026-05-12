package com.ilyadev.meowmoments.presentation.ui.detail

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import coil.load
import com.ilyadev.meowmoments.R
import com.ilyadev.meowmoments.databinding.FragmentFactDetailBinding
import com.ilyadev.meowmoments.domain.model.CatFact
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FactDetailFragment : Fragment() {

    private val viewModel: FactDetailViewModel by viewModels()
    private var _binding: FragmentFactDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFactDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Получаем факт из аргументов
        val fact = arguments?.getParcelable<CatFact>("fact") ?: return
        viewModel.setFact(fact)

        setupObservers()
        setupClickListeners()
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is FactDetailUiState.Loading -> {
                            // Можно показать ProgressBar, если нужно
                        }
                        is FactDetailUiState.Success -> {
                            bindFact(state.fact)
                        }
                    }
                }
            }
        }
    }

    private fun bindFact(fact: CatFact) {
        binding.tvFactDate.text = "Факт от ${fact.dateReceived}"
        binding.tvFactCategory.text = "#${fact.category}"
        binding.tvFactText.text = fact.text

        // Загружаем изображение
        binding.ivFactImage.load(fact.imageUrl) {
            crossfade(true)
            placeholder(R.drawable.placeholder_cat)
            error(R.drawable.error_cat)
        }

        // --- ОБНОВЛЕНИЕ КНОПКИ ИЗБРАННОГО ---
        binding.btnFavorite.setIconResource(
            if (fact.isFavorite) R.drawable.ic_star_filled else R.drawable.ic_star_outline
        )
        binding.btnFavorite.text = if (fact.isFavorite) "Удалить из избранного" else "В избранное"
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.btnFavorite.setOnClickListener {
            viewModel.toggleFavorite() // Вызываем метод в VM
        }

        binding.btnShare.setOnClickListener {
            // Получаем текущий текст факта из состояния ViewModel
            val currentFactText = (viewModel.uiState.value as? FactDetailUiState.Success)?.fact?.text ?: ""
            shareFact(currentFactText)
        }
    }

    private fun shareFact(factText: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "🐱 $factText")
        }
        startActivity(Intent.createChooser(shareIntent, "Поделиться через"))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}