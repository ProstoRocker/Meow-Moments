package com.ilyadev.meowmoments.presentation.ui.my_facts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.ilyadev.meowmoments.R
import com.ilyadev.meowmoments.databinding.FragmentMyFactsBinding
import com.ilyadev.meowmoments.presentation.ui.collection.CollectionFragment
import com.ilyadev.meowmoments.presentation.ui.favorites.FavoritesFragment
import com.ilyadev.meowmoments.presentation.ui.recentlyviewed.RecentlyViewedFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyFactsFragment : Fragment() {

    private var _binding: FragmentMyFactsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MyFactsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyFactsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Настройка ViewPager2
        val adapter = MyFactsPagerAdapter(this)
        binding.viewPager.adapter = adapter

        // Привязка TabLayout к ViewPager2 с помощью TabLayoutMediator
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Все"
                1 -> "Последние"
                2 -> "Избранное"
                else -> null
            }
        }.attach()

        // --- НОВОЕ: Настройка Toolbar и обработка нажатия на поиск ---
        binding.toolbar.inflateMenu(R.menu.menu_my_facts)
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_search -> {
                    val action = MyFactsFragmentDirections.actionMyFactsFragmentToSearchFragment()
                    findNavController().navigate(action)
                    true
                }

                else -> false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // --- Внутренний адаптер для ViewPager2 ---
    private class MyFactsPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int = 3

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> CollectionFragment() // Все факты
                1 -> RecentlyViewedFragment() // Последние просмотренные
                2 -> FavoritesFragment() // Избранное
                else -> CollectionFragment()
            }
        }
    }
}