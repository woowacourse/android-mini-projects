package a4.dogsignal.ui.common

import a4.dogsignal.model.RecordType
import a4.dogsignal.ui.theme.AccentOrange
import a4.dogsignal.ui.theme.AccentPurple
import a4.dogsignal.ui.theme.BrandPrimary
import androidx.compose.ui.graphics.Color

internal fun RecordType.toColor(): Color =
    when (this) {
        RecordType.URINE -> AccentOrange
        RecordType.PAD -> BrandPrimary
        RecordType.STOOL -> AccentPurple
    }

internal fun RecordType.toLabel(): String =
    when (this) {
        RecordType.URINE -> "소변"
        RecordType.PAD -> "패드 방문"
        RecordType.STOOL -> "대변"
    }
