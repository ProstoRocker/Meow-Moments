package com.ilyadev.meowmoments.presentation.ui.collection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ilyadev.meowmoments.databinding.FragmentCollectionBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CollectionFragment : Fragment() {

    private val viewModel: CollectionViewModel by viewModels()
    private var _binding: FragmentCollectionBinding? = null
    private val binding get() = _binding!!

    private lateinit var factAdapter: FactAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCollectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()
    }

    private fun setupRecyclerView() {
        factAdapter = FactAdapter()
        binding.rvFacts.adapter = factAdapter
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is CollectionUiState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.rvFacts.visibility = View.GONE
                            binding.tvEmpty.visibility = View.GONE
                            binding.tvError.visibility = View.GONE
                        }

                        is CollectionUiState.Empty -> {
                            binding.progressBar.visibility = View.GONE
                            binding.rvFacts.visibility = View.GONE
                            binding.tvEmpty.visibility = View.VISIBLE
                            binding.tvError.visibility = View.GONE
                        }

                        is CollectionUiState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            binding.rvFacts.visibility = View.VISIBLE
                            binding.tvEmpty.visibility = View.GONE
                            binding.tvError.visibility = View.GONE
                            factAdapter.submitList(state.facts)
                        }

                        is CollectionUiState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            binding.rvFacts.visibility = View.GONE
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