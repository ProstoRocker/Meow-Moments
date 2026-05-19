package com.ilyadev.meowmoments.presentation.ui.settings

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ilyadev.meowmoments.databinding.FragmentSettingsBinding
import com.ilyadev.meowmoments.presentation.push.NotificationScheduler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private val viewModel: SettingsViewModel by viewModels()
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var notificationScheduler: NotificationScheduler

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupClickListeners()
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.useSystemTheme.collect { useSystemTheme ->
                    binding.switchUseSystemTheme.isChecked = useSystemTheme
                    binding.switchDarkTheme.isEnabled = !useSystemTheme
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.darkThemeEnabled.collect { darkThemeEnabled ->
                    binding.switchDarkTheme.isChecked = darkThemeEnabled
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.dailyNotificationEnabled.collect { isEnabled ->
                    binding.switchDailyNotification.isChecked = isEnabled
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.notificationTime.collect { time ->
                    binding.tvNotificationTime.text = time
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.animationsEnabled.collect { enabled ->
                    binding.switchAnimations.isChecked = enabled
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.switchUseSystemTheme.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setUseSystemTheme(isChecked)
            applyTheme(isChecked, viewModel.darkThemeEnabled.value)
        }

        binding.switchDarkTheme.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setDarkThemeEnabled(isChecked)
            applyTheme(viewModel.useSystemTheme.value, isChecked)
        }

        binding.switchDailyNotification.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setDailyNotificationEnabled(isChecked)
            if (isChecked) {
                viewLifecycleOwner.lifecycleScope.launch {
                    notificationScheduler.scheduleDailyFactNotification()
                }
            } else {
                notificationScheduler.cancelDailyFactNotification()
            }
        }

        binding.btnSetNotificationTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            TimePickerDialog(
                requireContext(),
                { _, selectedHour, selectedMinute ->
                    val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                    viewModel.setNotificationTime(formattedTime)
                },
                hour,
                minute,
                true
            ).show()
        }

        binding.switchAnimations.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setAnimationsEnabled(isChecked)
        }
    }

    /**
     * Применяет тему приложения в зависимости от настроек.
     */
    private fun applyTheme(useSystemTheme: Boolean, darkThemeEnabled: Boolean) {
        val mode = when {
            useSystemTheme -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            darkThemeEnabled -> AppCompatDelegate.MODE_NIGHT_YES
            else -> AppCompatDelegate.MODE_NIGHT_NO
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}