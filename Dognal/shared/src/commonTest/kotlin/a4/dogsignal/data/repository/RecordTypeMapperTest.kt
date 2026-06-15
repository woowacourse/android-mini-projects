@file:Suppress("NonAsciiCharacters")

package a4.dogsignal.data.repository

import a4.dogsignal.model.RecordType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class RecordTypeMapperTest {
    @Test
    fun `VISIT 문자열은 VISIT enum으로 변환된다`() {
        assertEquals(RecordType.VISIT, "VISIT".toRecordType())
    }

    @Test
    fun `URINE 문자열은 URINE enum으로 변환된다`() {
        assertEquals(RecordType.URINE, "URINE".toRecordType())
    }

    @Test
    fun `STOOL 문자열은 STOOL enum으로 변환된다`() {
        assertEquals(RecordType.STOOL, "STOOL".toRecordType())
    }

    @Test
    fun `알 수 없는 문자열은 예외를 던진다`() {
        assertFailsWith<IllegalStateException> { "PAD".toRecordType() }
    }

    @Test
    fun `소문자 문자열은 예외를 던진다`() {
        assertFailsWith<IllegalStateException> { "visit".toRecordType() }
    }

    @Test
    fun `VISIT enum은 VISIT 문자열로 변환된다`() {
        assertEquals("VISIT", RecordType.VISIT.toRecordTypeColumn())
    }

    @Test
    fun `URINE enum은 URINE 문자열로 변환된다`() {
        assertEquals("URINE", RecordType.URINE.toRecordTypeColumn())
    }

    @Test
    fun `STOOL enum은 STOOL 문자열로 변환된다`() {
        assertEquals("STOOL", RecordType.STOOL.toRecordTypeColumn())
    }

    @Test
    fun `toRecordType과 toRecordTypeColumn은 왕복 변환 시 원래 값을 반환한다`() {
        RecordType.entries.forEach { type ->
            assertEquals(type, type.toRecordTypeColumn().toRecordType())
        }
    }
}
