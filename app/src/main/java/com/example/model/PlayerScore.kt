package com.example.model

data class PlayerScore(
    val id: String,
    val name: String,
    val scoreHistory: List<Int> = emptyList(),
    val currentInput: String = ""
) {
    val totalScore: Int
        get() = scoreHistory.sum()

    val historyText: String
        get() = if (scoreHistory.isEmpty()) {
            "00"
        } else {
            scoreHistory.joinToString(" + ") { score ->
                if (score < 0) "($score)" else score.toString()
            }
        }
}
