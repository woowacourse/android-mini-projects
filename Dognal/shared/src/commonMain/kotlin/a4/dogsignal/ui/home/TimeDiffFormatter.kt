package a4.dogsignal.ui.home

internal fun formatTimeDiff(diffMinutes: Long): String =
    when {
        diffMinutes < 1 -> "방금 전"
        diffMinutes < 60 -> "마지막 감지 ${diffMinutes}분 전"
        diffMinutes < 1440 -> "마지막 감지 ${diffMinutes / 60}시간 전"
        else -> "마지막 감지 ${diffMinutes / 1440}일 전"
    }
