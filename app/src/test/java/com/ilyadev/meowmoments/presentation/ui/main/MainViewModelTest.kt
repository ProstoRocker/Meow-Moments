package com.ilyadev.meowmoments.presentation.ui.main

import app.cash.turbine.test
import com.ilyadev.meowmoments.domain.model.CatFact
import com.ilyadev.meowmoments.domain.usecase.GetTodayFactUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever

/**
 * StandardTestDispatcher и setMain/resetMain: Позволяют тестировать корутины в viewModelScope синхронно.
 * Turbine: Библиотека (dev.zacsweers.turbine:turbine:1.2.0 - не забудь добавить в зависимости, если хочешь использовать именно такую стратегию) для удобного тестирования Flow. test { ... } позволяет "ожидать" (await) элементы из Flow и проверять их.
 * assertTrue, assertEquals: Для проверки типа состояния и его содержимого.
 * cancelAndIgnoreRemainingEvents(): Завершает тест Turbine Flow и игнорирует любые оставшиеся события, чтобы избежать зависания теста.
 */


@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    @Mock
    private lateinit var mockGetTodayFactUseCase: GetTodayFactUseCase

    private lateinit var viewModel: MainViewModel

    private val testDispatcher = StandardTestDispatcher() // Создаём тестовый dispatcher

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher) // Подменяем Main dispatcher на тестовый
        // Создаём ViewModel, передавая мок UseCase
        viewModel = MainViewModel(mockGetTodayFactUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // Возвращаем всё как было
    }

    @Test
    fun `uiState emits Loading then Success when useCase succeeds`() = runTest {
        // Arrange
        val expectedFact = CatFact(
            id = 1L,
            text = "Кошки спят в среднем 12-16 часов в сутки.",
            category = "Интересный",
            imageUrl = null,
            dateReceived = "2026-04-21"
        )
        whenever(mockGetTodayFactUseCase()).thenReturn(expectedFact)

        // Act & Assert
        viewModel.uiState.test {
            // Проверяем, что первым состоянием было Loading
            assertEquals(MainUiState.Loading, awaitItem())

            // Проверяем, что следующим состоянием было Success с ожидаемым фактом
            assertEquals(MainUiState.Success(expectedFact), awaitItem())

            // Завершаем тест (не ждём больше состояний)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `uiState emits Loading then Error when useCase returns null`() = runTest {
        // Arrange
        whenever(mockGetTodayFactUseCase()).thenReturn(null)

        // Act & Assert
        viewModel.uiState.test {
            assertEquals(MainUiState.Loading, awaitItem())
            assertTrue(awaitItem() is MainUiState.Error) // Проверяем, что это Error состояние
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `uiState emits Loading then Error when useCase throws exception`() = runTest {
        // Arrange
        val errorMessage = "Network error"
        whenever(mockGetTodayFactUseCase()).thenThrow(RuntimeException(errorMessage))

        // Act & Assert
        viewModel.uiState.test {
            assertEquals(MainUiState.Loading, awaitItem())
            val errorState = awaitItem() // Получаем следующее состояние
            assertTrue(errorState is MainUiState.Error) // Проверяем тип
            assertEquals(
                errorMessage,
                (errorState as MainUiState.Error).message
            ) // Проверяем сообщение
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `refreshFact re-emits Loading and new state`() = runTest {
        // Arrange
        val firstFact = CatFact(
            id = 1L,
            text = "First",
            category = "A",
            imageUrl = null,
            dateReceived = "2026-04-21"
        )
        val secondFact = CatFact(
            id = 2L,
            text = "Second",
            category = "B",
            imageUrl = null,
            dateReceived = "2026-04-21"
        )

        // На первый вызов возвращаем первый факт, на второй - второй
        whenever(mockGetTodayFactUseCase()).thenReturn(firstFact).thenReturn(secondFact)

        // Act & Assert - проверяем первый вызов (внутри init)
        viewModel.uiState.test {
            assertEquals(MainUiState.Loading, awaitItem())
            assertEquals(MainUiState.Success(firstFact), awaitItem())

            // Вызываем refreshFact
            viewModel.refreshFact()

            // Проверяем, что Loading появился снова
            assertEquals(MainUiState.Loading, awaitItem())

            // И затем новый Success
            assertEquals(MainUiState.Success(secondFact), awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }
}