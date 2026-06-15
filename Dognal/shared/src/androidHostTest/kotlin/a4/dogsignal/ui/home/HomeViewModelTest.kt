@file:Suppress("NonAsciiCharacters")

package a4.dogsignal.ui.home

import a4.dogsignal.data.network.dto.RecordDto
import a4.dogsignal.data.repository.RecordRepository
import a4.dogsignal.fake.FakeRecordDataSource
import a4.dogsignal.model.RecordType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Clock

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var fakeDataSource: FakeRecordDataSource
    private lateinit var viewModel: HomeViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeDataSource = FakeRecordDataSource()
        viewModel = HomeViewModel(RecordRepository(fakeDataSource), "device-1")
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `초기 상태는 isLoading이 true이다`() {
        assertTrue(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `초기 상태는 lastRecordDateTime이 null이다`() {
        assertNull(viewModel.uiState.value.lastRecordDateTime)
    }

    @Test
    fun `초기 상태는 summaryCards가 비어있다`() {
        assertTrue(viewModel.uiState.value.summaryCards.isEmpty())
    }

    @Test
    fun `refreshTodayRecords 후 타입별 카운트가 정확하다`() = runTest {
        val now = Clock.System.now()
        fakeDataSource.records = listOf(
            RecordDto("1", "URINE", now, null),
            RecordDto("2", "URINE", now, null),
            RecordDto("3", "STOOL", now, null),
            RecordDto("4", "VISIT", now, null),
        )

        viewModel.refreshTodayRecords()

        val cards = viewModel.uiState.value.summaryCards
        assertEquals(2, cards.first { it.recordType == RecordType.URINE }.count)
        assertEquals(1, cards.first { it.recordType == RecordType.STOOL }.count)
        assertEquals(1, cards.first { it.recordType == RecordType.VISIT }.count)
    }

    @Test
    fun `기록이 없을 때 모든 타입 카운트는 0이다`() = runTest {
        fakeDataSource.records = emptyList()

        viewModel.refreshTodayRecords()

        val cards = viewModel.uiState.value.summaryCards
        cards.forEach { assertEquals(0, it.count) }
    }

    @Test
    fun `동일한 타입만 있을 때 해당 타입 카운트만 증가한다`() = runTest {
        val now = Clock.System.now()
        fakeDataSource.records = listOf(
            RecordDto("1", "URINE", now, null),
            RecordDto("2", "URINE", now, null),
            RecordDto("3", "URINE", now, null),
        )

        viewModel.refreshTodayRecords()

        val cards = viewModel.uiState.value.summaryCards
        assertEquals(3, cards.first { it.recordType == RecordType.URINE }.count)
        assertEquals(0, cards.first { it.recordType == RecordType.STOOL }.count)
        assertEquals(0, cards.first { it.recordType == RecordType.VISIT }.count)
    }

    @Test
    fun `refreshTodayRecords 성공 후 isLoading은 false가 된다`() = runTest {
        viewModel.refreshTodayRecords()

        assertTrue(!viewModel.uiState.value.isLoading)
    }

    @Test
    fun `기록이 있을 때 lastRecordDateTime이 설정된다`() = runTest {
        val now = Clock.System.now()
        fakeDataSource.records = listOf(
            RecordDto("1", "URINE", now, null),
            RecordDto("2", "STOOL", now, null),
        )

        viewModel.refreshTodayRecords()

        assertTrue(viewModel.uiState.value.lastRecordDateTime != null)
    }

    @Test
    fun `기록이 없을 때 lastRecordDateTime은 null이다`() = runTest {
        fakeDataSource.records = emptyList()

        viewModel.refreshTodayRecords()

        assertNull(viewModel.uiState.value.lastRecordDateTime)
    }
}
