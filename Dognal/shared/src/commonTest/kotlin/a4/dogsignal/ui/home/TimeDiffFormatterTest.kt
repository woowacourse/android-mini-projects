@file:Suppress("NonAsciiCharacters")

package a4.dogsignal.ui.home

import kotlin.test.Test
import kotlin.test.assertEquals

class TimeDiffFormatterTest {

    @Test
    fun `0분 차이는 방금 전을 반환한다`() {
        assertEquals("방금 전", formatTimeDiff(0))
    }

    @Test
    fun `1분 미만은 방금 전을 반환한다`() {
        // diffMinutes는 Long이므로 0이 최솟값
        assertEquals("방금 전", formatTimeDiff(0))
    }

    @Test
    fun `1분은 1분 전을 반환한다`() {
        assertEquals("마지막 감지 1분 전", formatTimeDiff(1))
    }

    @Test
    fun `59분은 59분 전을 반환한다`() {
        assertEquals("마지막 감지 59분 전", formatTimeDiff(59))
    }

    @Test
    fun `60분은 1시간 전을 반환한다`() {
        assertEquals("마지막 감지 1시간 전", formatTimeDiff(60))
    }

    @Test
    fun `61분은 1시간 전을 반환한다`() {
        assertEquals("마지막 감지 1시간 전", formatTimeDiff(61))
    }

    @Test
    fun `1439분은 23시간 전을 반환한다`() {
        assertEquals("마지막 감지 23시간 전", formatTimeDiff(1439))
    }

    @Test
    fun `1440분은 1일 전을 반환한다`() {
        assertEquals("마지막 감지 1일 전", formatTimeDiff(1440))
    }

    @Test
    fun `2880분은 2일 전을 반환한다`() {
        assertEquals("마지막 감지 2일 전", formatTimeDiff(2880))
    }
}
