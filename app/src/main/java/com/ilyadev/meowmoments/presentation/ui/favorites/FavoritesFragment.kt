package com.ilyadev.meowmoments.presentation.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.ilyadev.meowmoments.databinding.FragmentFavoritesBinding
import com.ilyadev.meowmoments.presentation.ui.collection.FactAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavoritesFragment : Fragment() {

    private val viewModel: FavoritesViewModel by viewModels()
    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private lateinit var factAdapter: FactAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()
    }

    private fun setupRecyclerView() {
        // --- Передаём обработчики кликов в адаптер ---
        factAdapter = FactAdapter(
            onFactClick = { clickedFact ->
                // Навигация к экрану детализации
                val action = FavoritesFragmentDirections.actionFavoritesFragmentToFactDetailFragment(
                    fact = clickedFact // Передаём факт как аргумент
                )
                findNavController().navigate(action)
            },
            onFavoriteClick = { fact ->
                // Переключаем статус избранного через ViewModel
                viewModel.toggleFavorite(fact.id, fact.isFavorite)
            }
        )
        binding.rvFavorites.adapter = factAdapter
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is FavoritesUiState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.rvFavorites.visibility = View.GONE
                            binding.tvEmpty.visibility = View.GONE
                            binding.tvError.visibility = View.GONE
                        }
                        is FavoritesUiState.Empty -> {
                            binding.progressBar.visibility = View.GONE
                            binding.rvFavorites.visibility = View.GONE
                            binding.tvEmpty.visibility = View.VISIBLE
                            binding.tvError.visibility = View.GONE
                        }
                        is FavoritesUiState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            binding.rvFavorites.visibility = View.VISIBLE
                            binding.tvEmpty.visibility = View.GONE
                            binding.tvError.visibility = View.GONE
                            factAdapter.submitList(state.facts)
                        }
                        is FavoritesUiState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            binding.rvFavorites.visibility = View.GONE
                            binding.tvEmpty.visibility = View.GONE
                            binding.tvError.visibility = View.VISIBLE
                            binding.tvError.text = state.message
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}