package com.ilyadev.meowmoments.presentation.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView // Не Material SearchView!
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ilyadev.meowmoments.databinding.FragmentSearchBinding
import com.ilyadev.meowmoments.presentation.ui.collection.FactAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private val viewModel: SearchViewModel by viewModels()
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var factAdapter: FactAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearchView()
        setupObservers()
    }

    private fun setupRecyclerView() {
        factAdapter = FactAdapter(
            onFactClick = { clickedFact ->
                // Переход в детализацию
                val action = SearchFragmentDirections.actionSearchFragmentToFactDetailFragment(
                    fact = clickedFact
                )
                findNavController().navigate(action)
            },
            onFavoriteClick = { fact ->
                // Переключение избранного (реализация зависит от вашей логики)
                // viewModel.toggleFavorite(fact.id, fact.isFavorite)
            }
        )
        binding.rvSearchResults.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSearchResults.adapter = factAdapter
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { viewModel.updateSearchQuery(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { viewModel.updateSearchQuery(it) }
                return true
            }
        })
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        SearchUiState.Idle -> {
                            binding.progressBar.visibility = View.GONE
                            binding.rvSearchResults.visibility = View.GONE
                            binding.tvNoResults.visibility = View.GONE
                            binding.tvError.visibility = View.GONE
                        }

                        SearchUiState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.rvSearchResults.visibility = View.GONE
                            binding.tvNoResults.visibility = View.GONE
                            binding.tvError.visibility = View.GONE
                        }

                        SearchUiState.NoResults -> {
                            binding.progressBar.visibility = View.GONE
                            binding.rvSearchResults.visibility = View.GONE
                            binding.tvNoResults.visibility = View.VISIBLE
                            binding.tvError.visibility = View.GONE
                        }

                        is SearchUiState.Results -> {
                            binding.progressBar.visibility = View.GONE
                            binding.rvSearchResults.visibility = View.VISIBLE
                            binding.tvNoResults.visibility = View.GONE
                            binding.tvError.visibility = View.GONE
                            factAdapter.submitList(state.facts)
                        }

                        is SearchUiState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            binding.rvSearchResults.visibility = View.GONE
                            binding.tvNoResults.visibility = View.GONE
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