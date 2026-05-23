package com.ilyadev.meowmoments.data.repository

import android.content.SharedPreferences
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.verify

// Тесты для Repository

class SettingsRepositoryImplTest {

    private lateinit var repository: SettingsRepositoryImpl

    @Mock
    private lateinit var sharedPreferences: SharedPreferences

    @Mock
    private lateinit var editor: SharedPreferences.Editor

    private lateinit var closeable: AutoCloseable

    @Before
    fun setUp() {
        closeable = MockitoAnnotations.openMocks(this)
        `when`(sharedPreferences.edit()).thenReturn(editor)
        `when`(editor.putBoolean(any(), any())).thenReturn(editor)
        `when`(editor.putString(any(), any())).thenReturn(editor)
        repository = SettingsRepositoryImpl(sharedPreferences)
    }

    @Test
    fun `getUseSystemTheme returns correct value from preferences`() = runTest {
        // Given
        `when`(sharedPreferences.getBoolean("pref_use_system_theme", true)).thenReturn(false)

        // When
        val result = repository.getUseSystemTheme().first()

        // Then
        assertEquals(false, result)
    }

    @Test
    fun `setUseSystemTheme stores value in preferences`() = runTest {
        // Given
        val useSystemTheme = false

        // When
        repository.setUseSystemTheme(useSystemTheme)

        // Then
        verify(editor).putBoolean("pref_use_system_theme", useSystemTheme)
        verify(editor).apply()
    }

    @Test
    fun `setNotificationTime stores value in preferences`() = runTest {
        // Given
        val time = "10:00"

        // When
        repository.setNotificationTime(time)

        // Then
        verify(editor).putString("pref_notification_time", time)
        verify(editor).apply()
    }
}