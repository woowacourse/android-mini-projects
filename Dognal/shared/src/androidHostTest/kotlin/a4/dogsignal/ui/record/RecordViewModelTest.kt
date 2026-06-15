@file:Suppress("NonAsciiCharacters")

package a4.dogsignal.ui.record

import a4.dogsignal.data.network.dto.RecordDto
import a4.dogsignal.data.repository.RecordRepository
import a4.dogsignal.fake.FakeRecordDataSource
import a4.dogsignal.model.RecordType
import a4.dogsignal.ui.common.component.DognalTab
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

import kotlinx.datetime.LocalDateTime
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Clock

@OptIn(ExperimentalCoroutinesApi::class)
class RecordViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var fakeDataSource: FakeRecordDataSource
    private lateinit var viewModel: RecordViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeDataSource = FakeRecordDataSource()
        viewModel = RecordViewModel(RecordRepository(fakeDataSource), "device-1")
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `초기 상태는 RECORD 탭이고 recordList는 비어있다`() {
        assertEquals(DognalTab.RECORD, viewModel.uiState.value.selectedTab)
        assertTrue(viewModel.uiState.value.recordList.isEmpty())
    }

    @Test
    fun `저장 중에 saveManualRecord를 재호출하면 무시된다`() = runTest {
        val deferred = CompletableDeferred<Unit>()
        fakeDataSource.createDeferred = deferred

        val testDateTime = LocalDateTime(2026, 6, 15, 10, 0)
        viewModel.saveManualRecord(RecordType.URINE, testDateTime, "첫 번째")
        // 여기서 createRecord는 deferred.await()에서 블로킹 중
        // → isSavingManualRecord = true

        viewModel.saveManualRecord(RecordType.URINE, testDateTime, "두 번째") // 무시됨

        deferred.complete(Unit)

        assertEquals(1, fakeDataSource.createCallCount)
    }

    @Test
    fun `저장 중에 updateManualRecord를 호출하면 무시된다`() = runTest {
        val deferred = CompletableDeferred<Unit>()
        fakeDataSource.createDeferred = deferred

        val testDateTime = LocalDateTime(2026, 6, 15, 10, 0)
        viewModel.saveManualRecord(RecordType.URINE, testDateTime, "저장 중")
        viewModel.updateManualRecord("id-1", RecordType.STOOL, testDateTime, "수정 시도")

        deferred.complete(Unit)

        assertEquals(1, fakeDataSource.createCallCount)
        assertNull(fakeDataSource.capturedUpdateNote) // update는 실행되지 않음
    }

    @Test
    fun `저장 중에 deleteRecord를 호출하면 무시된다`() = runTest {
        val deferred = CompletableDeferred<Unit>()
        fakeDataSource.createDeferred = deferred

        val testDateTime = LocalDateTime(2026, 6, 15, 10, 0)
        viewModel.saveManualRecord(RecordType.URINE, testDateTime, "저장 중")
        viewModel.deleteRecord("id-1")

        deferred.complete(Unit)

        assertEquals(1, fakeDataSource.createCallCount)
    }

    @Test
    fun `saveManualRecord 성공 후 isSavingManualRecord는 false로 초기화된다`() = runTest {
        viewModel.saveManualRecord(RecordType.URINE, LocalDateTime(2026, 6, 15, 10, 0), "메모")

        assertFalse(viewModel.uiState.value.isSavingManualRecord)
    }

    @Test
    fun `saveManualRecord 성공 후 manualRecordErrorMessage는 null이다`() = runTest {
        viewModel.saveManualRecord(RecordType.URINE, LocalDateTime(2026, 6, 15, 10, 0), "메모")

        assertNull(viewModel.uiState.value.manualRecordErrorMessage)
    }

    @Test
    fun `saveManualRecord 성공 후 recordList가 갱신된다`() = runTest {
        val now = Clock.System.now()
        fakeDataSource.records = listOf(RecordDto("new-id", "URINE", now, null))

        viewModel.saveManualRecord(RecordType.URINE, LocalDateTime(2026, 6, 15, 10, 0), "메모")

        assertEquals(1, viewModel.uiState.value.recordList.size)
        assertEquals("new-id", viewModel.uiState.value.recordList.first().id)
    }

    @Test
    fun `saveManualRecord 실패 후 isSavingManualRecord는 false로 초기화된다`() = runTest {
        fakeDataSource.shouldThrowOnCreate = true

        viewModel.saveManualRecord(RecordType.URINE, LocalDateTime(2026, 6, 15, 10, 0), "메모")

        assertFalse(viewModel.uiState.value.isSavingManualRecord)
    }

    @Test
    fun `saveManualRecord 실패 후 manualRecordErrorMessage가 설정된다`() = runTest {
        fakeDataSource.shouldThrowOnCreate = true

        viewModel.saveManualRecord(RecordType.URINE, LocalDateTime(2026, 6, 15, 10, 0), "메모")

        assertNotNull(viewModel.uiState.value.manualRecordErrorMessage)
    }

    @Test
    fun `clearManualRecordError 호출 후 manualRecordErrorMessage는 null이 된다`() = runTest {
        fakeDataSource.shouldThrowOnCreate = true
        viewModel.saveManualRecord(RecordType.URINE, LocalDateTime(2026, 6, 15, 10, 0), "메모")

        viewModel.clearManualRecordError()

        assertNull(viewModel.uiState.value.manualRecordErrorMessage)
    }
}
