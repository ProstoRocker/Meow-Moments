package com.ilyadev.meowmoments.presentation.ui.recentlyviewed

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.ilyadev.meowmoments.R
import com.ilyadev.meowmoments.databinding.FragmentRecentlyViewedBinding
import com.ilyadev.meowmoments.presentation.ui.collection.FactAdapter
import com.ilyadev.meowmoments.presentation.ui.my_facts.MyFactsFragmentDirections
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RecentlyViewedFragment : Fragment() {

    private val viewModel: RecentlyViewedViewModel by viewModels()
    private var _binding: FragmentRecentlyViewedBinding? = null
    private val binding get() = _binding!!

    private lateinit var factAdapter: FactAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecentlyViewedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()
    }

    private fun setupRecyclerView() {
        // Используем тот же адаптер, что и в CollectionFragment
        factAdapter = FactAdapter(
            onFactClick = { clickedFact ->
                // Навигация к экрану детализации
                val action = MyFactsFragmentDirections.actionMyFactsFragmentToFactDetailFragment(
                    fact = clickedFact
                )
                findNavController().navigate(action)
            },
            onFavoriteClick = { fact ->
                // Можно добавить переключение избранного, если нужно
                // viewModel.toggleFavorite(fact.id, fact.isFavorite)
            }
        )
        binding.rvRecentlyViewed.adapter = factAdapter
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is RecentlyViewedUiState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.rvRecentlyViewed.visibility = View.GONE
                            binding.tvEmpty.visibility = View.GONE
                            binding.tvError.visibility = View.GONE
                        }

                        is RecentlyViewedUiState.Empty -> {
                            binding.progressBar.visibility = View.GONE
                            binding.rvRecentlyViewed.visibility = View.GONE
                            binding.tvEmpty.visibility = View.VISIBLE
                            binding.tvError.visibility = View.GONE
                        }

                        is RecentlyViewedUiState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            binding.rvRecentlyViewed.visibility = View.VISIBLE
                            binding.tvEmpty.visibility = View.GONE
                            binding.tvError.visibility = View.GONE
                            factAdapter.submitList(state.facts)
                        }

                        is RecentlyViewedUiState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            binding.rvRecentlyViewed.visibility = View.GONE
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