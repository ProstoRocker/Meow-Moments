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
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ilyadev.meowmoments.databinding.FragmentCollectionBinding
import com.ilyadev.meowmoments.presentation.ui.my_facts.MyFactsFragmentDirections
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CollectionFragment : Fragment() {

    private val viewModel: CollectionViewModel by viewModels()
    private var _binding: FragmentCollectionBinding? = null
    private val binding get() = _binding!!

    private lateinit var pagingAdapter: PagingFactAdapter

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
        setupLoadStateListener()

        // Заменяем старую логику наблюдения за uiState на пагинацию
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.pagedFacts.collectLatest { pagingData ->
                    pagingAdapter.submitData(pagingData)
                }
            }
        }
    }

    private fun setupRecyclerView() {
        pagingAdapter = PagingFactAdapter(
            onFactClick = { clickedFact ->
                val action = MyFactsFragmentDirections.actionMyFactsFragmentToFactDetailFragment(
                    fact = clickedFact
                )
                findNavController().navigate(action)
            },
            onFavoriteClick = { fact ->
                // Для пагинации, переключение избранного нужно обновить
                // через репозиторий и обновить список (или использовать callback)
                viewModel.toggleFavorite(fact.id, fact.isFavorite)
            }
        )

        // Добавляем адаптер загрузки (optional)
        binding.rvFacts.adapter = pagingAdapter.withLoadStateFooter(
            footer = object : LoadStateAdapter<RecyclerView.ViewHolder>() {
                override fun onBindViewHolder(
                    holder: RecyclerView.ViewHolder,
                    loadState: LoadState
                ) {
                    // Показать/скрыть прогресс
                    binding.progressBar.visibility = when (loadState) {
                        is LoadState.Loading -> View.VISIBLE
                        else -> View.GONE
                    }
                }

                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    loadState: LoadState
                ): RecyclerView.ViewHolder {
                    // Создаём ViewHolder для индикатора загрузки
                    // Можно использовать отдельный layout для прогресса
                    return object : RecyclerView.ViewHolder(
                        LayoutInflater.from(parent.context)
                            .inflate(android.R.layout.simple_list_item_1, parent, false)
                    ) {}
                }
            }
        )
    }

    private fun setupLoadStateListener() {
        pagingAdapter.addLoadStateListener { loadStates ->
            binding.progressBar.visibility = when (loadStates.refresh) {
                is LoadState.Loading -> View.VISIBLE
                else -> View.GONE
            }

            // Проверяем ошибки
            if (loadStates.refresh is LoadState.Error) {
                binding.tvError.visibility = View.VISIBLE
                binding.tvError.text = (loadStates.refresh as LoadState.Error).error.message
            } else {
                binding.tvError.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}