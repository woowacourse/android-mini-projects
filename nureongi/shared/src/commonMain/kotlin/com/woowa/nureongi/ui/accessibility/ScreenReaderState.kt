package com.woowa.nureongi.ui.accessibility

import androidx.compose.runtime.Composable

/**
 * 현재 스크린 리더(Android: TalkBack, iOS: VoiceOver)가 활성화되어 있는지 여부를 반환합니다.
 * 스크린 리더 상태가 변경되면 자동으로 recomposition이 발생합니다.
 */
@Composable
expect fun rememberScreenReaderEnabled(): Boolean
