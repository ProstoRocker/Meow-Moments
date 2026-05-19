package com.ilyadev.meowmoments.presentation.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
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

        // --- НОВОЕ: Настройка SwipeRefreshLayout ---
        setupSwipeRefresh()

        // --- ДОБАВЛЕНЫ ПЕРЕХОДЫ ---
        binding.btnNextFact.setOnClickListener {
            viewModel.refreshFact()
        }

        // Клик по счётчику коллекции -> переход в MyFactsFragment
        binding.tvCollectionProgress.setOnClickListener {
            Log.d(
                "MainFragment",
                "Clicked on collection progress, navigating to MyFactsFragment"
            )
            val action = MainFragmentDirections.actionMainFragmentToMyFactsFragment()
            findNavController().navigate(action)
        }

        // Клик по изображению -> переход в MyFactsFragment
        binding.ivFactImage.setOnClickListener {
            Log.d("MainFragment", "Clicked on fact image, navigating to MyFactsFragment")
            val action = MainFragmentDirections.actionMainFragmentToMyFactsFragment()
            findNavController().navigate(action)
        }

        // Клик по тексту факта -> переход в MyFactsFragment
        binding.tvFactText.setOnClickListener {
            Log.d(
                "MainFragment",
                "Clicked on fact text, attempting to navigate to MyFactsFragment"
            )
            val action = MainFragmentDirections.actionMainFragmentToMyFactsFragment()
            findNavController().navigate(action)
        }

        // Клик по категории -> переход в MyFactsFragment
        binding.tvFactCategory.setOnClickListener {
            Log.d("MainFragment", "Clicked on fact category, navigating to MyFactsFragment")
            val action = MainFragmentDirections.actionMainFragmentToMyFactsFragment()
            findNavController().navigate(action)
        }

        // Клик по дате -> переход в CalendarFragment
        binding.tvFactDate.setOnClickListener {
            Log.d("MainFragment", "Clicked on fact date, navigating to CalendarFragment")
            val action = MainFragmentDirections.actionMainFragmentToCalendarFragment()
            findNavController().navigate(action)
        }

        // Наблюдение за состоянием UI с использованием repeatOnLifecycle
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        MainUiState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.contentScrollview.visibility = View.GONE
                            // --- НОВОЕ: Останавливаем анимацию refresh при загрузке ---
                            binding.swipeRefresh.isRefreshing = false
                        }

                        is MainUiState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            binding.contentScrollview.visibility = View.VISIBLE
                            bindFact(state.fact)
                            // --- НОВОЕ: Останавливаем анимацию refresh при успехе ---
                            binding.swipeRefresh.isRefreshing = false
                        }

                        is MainUiState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            binding.contentScrollview.visibility = View.GONE
                            binding.swipeRefresh.isRefreshing = false // --- НОВОЕ ---
                            Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    // --- НОВОЕ: Настройка обработки свайпа вниз ---
    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            Log.d("MainFragment", "Swipe refresh triggered, refreshing fact")
            viewModel.refreshFact()
        }

        // Убедимся, что анимация не включена, если данные не загружаются
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.swipeRefresh.isRefreshing = state is MainUiState.Loading
                }
            }
        }
    }

    private fun bindFact(fact: CatFact) {
        binding.tvFactDate.text = "Факт на ${fact.dateReceived}"
        binding.tvFactCategory.text = "#${fact.category}"
        binding.tvFactText.text = fact.text

        // 🔥 КРИТИЧЕСКАЯ ПРОВЕРКА: Убедимся, что imageUrl не null и не пустой
        val imageUrl = fact.imageUrl
        Log.d("MainFragment", "Binding fact with imageUrl: '$imageUrl'")

        if (imageUrl.isNullOrEmpty()) {
            // Если URL пустой или null, показываем placeholder и логируем
            binding.ivFactImage.setImageResource(R.drawable.placeholder_cat)
            Log.w("MainFragment", "Warning: imageUrl is null or empty! Fact text: '${fact.text}'")
        } else {
            // Загружаем изображение через Coil
            binding.ivFactImage.load(imageUrl) {
                crossfade(true)
                placeholder(R.drawable.placeholder_cat)
                error(R.drawable.error_cat)
                // Добавляем логирование загрузки
                listener(
                    onSuccess = { _, _ ->
                        Log.d("MainFragment", "Image loaded successfully from: $imageUrl")
                    },
                    onError = { _, _ ->
                        Log.e("MainFragment", "Image loading failed for URL: $imageUrl")
                    }
                )
            }
        }

        // Получаем количество собранных фактов
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getCollectedCount().collect { count ->
                    binding.tvCollectionProgress.text =
                        "Факт $count из ${viewModel.getTotalFactsCount()}"
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}