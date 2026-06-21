package com.woowa.nureongi.ui.speech

import com.woowa.nureongi.ui.model.CurrentLocationItemUiModel

internal data class VoiceLocationMatch(
    val locationId: String,
    val locationName: String,
    val recognizedText: String,
)

internal fun findBestVoiceLocationMatch(
    recognizedTexts: List<String>,
    locations: List<CurrentLocationItemUiModel>,
): VoiceLocationMatch? {
    return recognizedTexts
        .flatMap { recognizedText ->
            locations.map { location ->
                VoiceLocationMatchCandidate(
                    recognizedText = recognizedText,
                    location = location,
                    score = recognizedText.scoreWith(location),
                )
            }
        }
        .maxByOrNull(VoiceLocationMatchCandidate::score)
        ?.takeIf { it.score > 0f }
        ?.let { candidate ->
            VoiceLocationMatch(
                locationId = candidate.location.id,
                locationName = candidate.location.place.name,
                recognizedText = candidate.recognizedText,
            )
        }
}

private data class VoiceLocationMatchCandidate(
    val recognizedText: String,
    val location: CurrentLocationItemUiModel,
    val score: Float,
)

private fun String.scoreWith(location: CurrentLocationItemUiModel): Float {
    val input = normalizeForVoiceMatch()
    if (input.isBlank()) {
        return 0f
    }

    return location.matchKeywords()
        .map(String::normalizeForVoiceMatch)
        .filter(String::isNotBlank)
        .maxOfOrNull { keyword ->
            when {
                input.contains(keyword) || keyword.contains(input) -> 1f
                else -> normalizedSimilarity(input, keyword)
            }
        }
        ?: 0f
}

private fun CurrentLocationItemUiModel.matchKeywords(): List<String> {
    val name = place.name
    return buildList {
        add(name)
        name.split("/", "·", ",")
            .map(String::trim)
            .filter(String::isNotBlank)
            .forEach(::add)
        add(name.replace(" ", ""))
    }.distinct()
}

private fun String.normalizeForVoiceMatch(): String {
    return lowercase()
        .replace(Regex("""[\s/·,().\-]"""), "")
}

private fun normalizedSimilarity(
    first: String,
    second: String,
): Float {
    val maxLength = maxOf(first.length, second.length)
    if (maxLength == 0) {
        return 1f
    }

    return 1f - levenshteinDistance(first, second).toFloat() / maxLength
}

private fun levenshteinDistance(
    first: String,
    second: String,
): Int {
    if (first == second) {
        return 0
    }
    if (first.isEmpty()) {
        return second.length
    }
    if (second.isEmpty()) {
        return first.length
    }

    var previous = IntArray(second.length + 1) { it }
    var current = IntArray(second.length + 1)

    first.forEachIndexed { firstIndex, firstChar ->
        current[0] = firstIndex + 1
        second.forEachIndexed { secondIndex, secondChar ->
            val cost = if (firstChar == secondChar) 0 else 1
            current[secondIndex + 1] = minOf(
                current[secondIndex] + 1,
                previous[secondIndex + 1] + 1,
                previous[secondIndex] + cost,
            )
        }
        val temp = previous
        previous = current
        current = temp
    }

    return previous[second.length]
}
