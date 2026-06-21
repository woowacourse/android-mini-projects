package com.woowa.nureongi.ui.speech

import com.woowa.nureongi.ui.model.CurrentLocationItemUiModel
import com.woowa.nureongi.ui.model.PlaceUiModel
import kotlin.test.Test
import kotlin.test.assertEquals

class VoiceLocationMatcherTest {
    @Test
    fun `음성 인식 결과와 가장 유사한 출발지를 찾는다`() {
        val locations = listOf(
            location("a", "옆 강의실"),
            location("e", "우물가"),
            location("r", "여자화장실"),
        )

        val result = findBestVoiceLocationMatch(
            recognizedTexts = listOf("우물까"),
            locations = locations,
        )

        assertEquals("e", result?.locationId)
    }

    @Test
    fun `슬래시로 묶인 장소명은 개별 이름으로도 찾는다`() {
        val locations = listOf(
            location("o", "수성 / 화성"),
            location("p", "금성 / 지구"),
        )

        val result = findBestVoiceLocationMatch(
            recognizedTexts = listOf("화성"),
            locations = locations,
        )

        assertEquals("o", result?.locationId)
    }

    private fun location(
        id: String,
        name: String,
    ): CurrentLocationItemUiModel {
        return CurrentLocationItemUiModel(
            id = id,
            place = PlaceUiModel(name = name, location = ""),
        )
    }
}
