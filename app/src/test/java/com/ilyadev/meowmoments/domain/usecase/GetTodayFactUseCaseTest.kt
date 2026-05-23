package com.ilyadev.meowmoments.domain.usecase

import com.ilyadev.meowmoments.domain.model.CatFact
import com.ilyadev.meowmoments.domain.repository.CatFactsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever // Импортируем whenever из mockito-kotlin


/**
 * @RunWith(MockitoJUnitRunner::class): Позволяет использовать аннотации @Mock и автоматически инициализирует моки.
 * @Mock private lateinit var mockRepository: CatFactsRepository: Создаёт "пустышку" объекта CatFactsRepository.
 * whenever(mockRepository.getFactForToday()).thenReturn(...): Определяет, какой результат должен вернуть мок, когда вызывается getFactForToday().
 * runTest: Обертка для тестов, работающих с Coroutines, предоставляемая kotlinx-coroutines-test.
 * assertEquals, assertNull, assertThrows: Стандартные JUnit ассерты для проверки результата.
 */


@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class) // Используем Mockito runner для автоматической инициализации моков
class GetTodayFactUseCaseTest {

    @Mock
    private lateinit var mockRepository: CatFactsRepository

    private lateinit var useCase: GetTodayFactUseCaseImpl

    @Before
    fun setUp() {
        // Создаём реализацию UseCase, передавая ему замоканный репозиторий
        useCase = GetTodayFactUseCaseImpl(mockRepository)
    }

    @Test
    fun `invoke returns fact when repository returns fact`() = runTest {
        // Arrange
        val expectedFact = CatFact(
            id = 1L,
            text = "Кошки спят в среднем 12-16 часов в сутки.",
            category = "Интересный",
            imageUrl = null,
            dateReceived = "2026-04-21"
        )
        whenever(mockRepository.getFactForToday()).thenReturn(expectedFact)

        // Act
        val result = useCase()

        // Assert
        assertEquals(expectedFact, result)
    }

    @Test
    fun `invoke returns null when repository returns null`() = runTest {
        // Arrange
        whenever(mockRepository.getFactForToday()).thenReturn(null)

        // Act
        val result = useCase()

        // Assert
        assertNull(result)
    }

    // Можно добавить тесты на исключения, если репозиторий их бросает
    @Test
    fun `invoke throws exception when repository throws exception`() = runTest {
        // Arrange
        val exceptionMessage = "Network error"
        whenever(mockRepository.getFactForToday()).thenThrow(RuntimeException(exceptionMessage))

        // Act & Assert
        try {
            useCase()
            fail("Expected RuntimeException to be thrown")
        } catch (e: RuntimeException) {
            assertEquals(exceptionMessage, e.message)
        }
    }
}