package com.example.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.PlayerScore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

data class GameUiState(
    val hazariPlayers: List<PlayerScore> = defaultHazariPlayers(),
    val sikoPlayers: List<PlayerScore> = defaultSikoPlayers(),
    val isHazariVisible: Boolean = true,
    val isSikoVisible: Boolean = true,
    val hazariTargetScore: Int = 1000,
    val showResetDialog: Boolean = false,
    val resetTargetSection: ResetTarget? = null
)

enum class ResetTarget {
    ALL, HAZARI, SIKO
}

private fun defaultHazariPlayers(): List<PlayerScore> {
    return List(6) { index ->
        PlayerScore(
            id = "hazari_${index + 1}",
            name = "Player ${index + 1}",
            scoreHistory = emptyList(),
            currentInput = ""
        )
    }
}

private fun defaultSikoPlayers(): List<PlayerScore> {
    return List(3) { index ->
        PlayerScore(
            id = "siko_${index + 1}",
            name = "Player ${index + 1}",
            scoreHistory = emptyList(),
            currentInput = ""
        )
    }
}

class CardGameViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("card_game_calc_prefs", Context.MODE_PRIVATE)

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    init {
        loadSavedState()
    }

    // --- HAZARI ACTIONS ---

    fun onHazariNameChange(index: Int, newName: String) {
        _uiState.update { state ->
            val updated = state.hazariPlayers.toMutableList()
            if (index in updated.indices) {
                updated[index] = updated[index].copy(name = newName)
            }
            state.copy(hazariPlayers = updated)
        }
        saveState()
    }

    fun onHazariInputChange(index: Int, newInput: String) {
        // Filter input to allow digits and optional leading minus
        val filtered = newInput.filterIndexed { i, c ->
            c.isDigit() || (c == '-' && i == 0)
        }
        _uiState.update { state ->
            val updated = state.hazariPlayers.toMutableList()
            if (index in updated.indices) {
                updated[index] = updated[index].copy(currentInput = filtered)
            }
            state.copy(hazariPlayers = updated)
        }
    }

    fun addHazariPoints(index: Int) {
        _uiState.update { state ->
            val updated = state.hazariPlayers.toMutableList()
            if (index in updated.indices) {
                val player = updated[index]
                val points = player.currentInput.trim().toIntOrNull()
                if (points != null) {
                    val newHistory = player.scoreHistory + points
                    updated[index] = player.copy(
                        scoreHistory = newHistory,
                        currentInput = ""
                    )
                }
            }
            state.copy(hazariPlayers = updated)
        }
        saveState()
    }

    fun deleteHazariPoints(index: Int) {
        _uiState.update { state ->
            val updated = state.hazariPlayers.toMutableList()
            if (index in updated.indices) {
                val player = updated[index]
                if (player.currentInput.isNotEmpty()) {
                    // Clear the current uncommitted input
                    updated[index] = player.copy(currentInput = "")
                } else if (player.scoreHistory.isNotEmpty()) {
                    // Undo last added score round
                    val newHistory = player.scoreHistory.dropLast(1)
                    updated[index] = player.copy(scoreHistory = newHistory)
                }
            }
            state.copy(hazariPlayers = updated)
        }
        saveState()
    }

    fun quickAddHazariPoints(index: Int, points: Int) {
        _uiState.update { state ->
            val updated = state.hazariPlayers.toMutableList()
            if (index in updated.indices) {
                val player = updated[index]
                val newHistory = player.scoreHistory + points
                updated[index] = player.copy(
                    scoreHistory = newHistory,
                    currentInput = ""
                )
            }
            state.copy(hazariPlayers = updated)
        }
        saveState()
    }

    fun toggleHazariVisibility() {
        _uiState.update { it.copy(isHazariVisible = !it.isHazariVisible) }
        saveState()
    }

    fun resetHazariGame() {
        _uiState.update { state ->
            state.copy(
                hazariPlayers = state.hazariPlayers.map {
                    it.copy(scoreHistory = emptyList(), currentInput = "")
                }
            )
        }
        saveState()
    }

    // --- SIKO ACTIONS ---

    fun onSikoNameChange(index: Int, newName: String) {
        _uiState.update { state ->
            val updated = state.sikoPlayers.toMutableList()
            if (index in updated.indices) {
                updated[index] = updated[index].copy(name = newName)
            }
            state.copy(sikoPlayers = updated)
        }
        saveState()
    }

    fun onSikoInputChange(index: Int, newInput: String) {
        val filtered = newInput.filterIndexed { i, c ->
            c.isDigit() || (c == '-' && i == 0)
        }
        _uiState.update { state ->
            val updated = state.sikoPlayers.toMutableList()
            if (index in updated.indices) {
                updated[index] = updated[index].copy(currentInput = filtered)
            }
            state.copy(sikoPlayers = updated)
        }
    }

    fun addSikoPoints(index: Int) {
        _uiState.update { state ->
            val updated = state.sikoPlayers.toMutableList()
            if (index in updated.indices) {
                val player = updated[index]
                val points = player.currentInput.trim().toIntOrNull()
                if (points != null) {
                    val newHistory = player.scoreHistory + points
                    updated[index] = player.copy(
                        scoreHistory = newHistory,
                        currentInput = ""
                    )
                }
            }
            state.copy(sikoPlayers = updated)
        }
        saveState()
    }

    fun deleteSikoPoints(index: Int) {
        _uiState.update { state ->
            val updated = state.sikoPlayers.toMutableList()
            if (index in updated.indices) {
                val player = updated[index]
                if (player.currentInput.isNotEmpty()) {
                    updated[index] = player.copy(currentInput = "")
                } else if (player.scoreHistory.isNotEmpty()) {
                    val newHistory = player.scoreHistory.dropLast(1)
                    updated[index] = player.copy(scoreHistory = newHistory)
                }
            }
            state.copy(sikoPlayers = updated)
        }
        saveState()
    }

    fun quickAddSikoPoints(index: Int, points: Int) {
        _uiState.update { state ->
            val updated = state.sikoPlayers.toMutableList()
            if (index in updated.indices) {
                val player = updated[index]
                val newHistory = player.scoreHistory + points
                updated[index] = player.copy(
                    scoreHistory = newHistory,
                    currentInput = ""
                )
            }
            state.copy(sikoPlayers = updated)
        }
        saveState()
    }

    fun toggleSikoVisibility() {
        _uiState.update { it.copy(isSikoVisible = !it.isSikoVisible) }
        saveState()
    }

    fun resetSikoGame() {
        _uiState.update { state ->
            state.copy(
                sikoPlayers = state.sikoPlayers.map {
                    it.copy(scoreHistory = emptyList(), currentInput = "")
                }
            )
        }
        saveState()
    }

    // --- GLOBAL ACTIONS ---

    fun promptReset(target: ResetTarget) {
        _uiState.update { it.copy(showResetDialog = true, resetTargetSection = target) }
    }

    fun dismissResetDialog() {
        _uiState.update { it.copy(showResetDialog = false, resetTargetSection = null) }
    }

    fun confirmReset() {
        when (_uiState.value.resetTargetSection) {
            ResetTarget.ALL -> {
                resetHazariGame()
                resetSikoGame()
            }
            ResetTarget.HAZARI -> resetHazariGame()
            ResetTarget.SIKO -> resetSikoGame()
            null -> Unit
        }
        dismissResetDialog()
    }

    // --- PERSISTENCE ---

    private fun saveState() {
        viewModelScope.launch {
            try {
                val state = _uiState.value
                val hazariJson = serializePlayers(state.hazariPlayers)
                val sikoJson = serializePlayers(state.sikoPlayers)
                prefs.edit()
                    .putString("hazari_players", hazariJson)
                    .putString("siko_players", sikoJson)
                    .putBoolean("is_hazari_visible", state.isHazariVisible)
                    .putBoolean("is_siko_visible", state.isSikoVisible)
                    .apply()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun loadSavedState() {
        try {
            val hazariStr = prefs.getString("hazari_players", null)
            val sikoStr = prefs.getString("siko_players", null)
            val isHazariVis = prefs.getBoolean("is_hazari_visible", true)
            val isSikoVis = prefs.getBoolean("is_siko_visible", true)

            val hazariList = if (hazariStr != null) deserializePlayers(hazariStr, 6, "hazari") else defaultHazariPlayers()
            val sikoList = if (sikoStr != null) deserializePlayers(sikoStr, 3, "siko") else defaultSikoPlayers()

            _uiState.update {
                it.copy(
                    hazariPlayers = hazariList,
                    sikoPlayers = sikoList,
                    isHazariVisible = isHazariVis,
                    isSikoVisible = isSikoVis
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun serializePlayers(players: List<PlayerScore>): String {
        val array = JSONArray()
        for (p in players) {
            val obj = JSONObject()
            obj.put("id", p.id)
            obj.put("name", p.name)
            val historyArr = JSONArray()
            p.scoreHistory.forEach { historyArr.put(it) }
            obj.put("history", historyArr)
            array.put(obj)
        }
        return array.toString()
    }

    private fun deserializePlayers(json: String, expectedCount: Int, prefix: String): List<PlayerScore> {
        val list = mutableListOf<PlayerScore>()
        val array = JSONArray(json)
        for (i in 0 until minOf(array.length(), expectedCount)) {
            val obj = array.getJSONObject(i)
            val id = obj.optString("id", "${prefix}_${i + 1}")
            val name = obj.optString("name", "Player ${i + 1}")
            val historyArr = obj.optJSONArray("history") ?: JSONArray()
            val history = mutableListOf<Int>()
            for (j in 0 until historyArr.length()) {
                history.add(historyArr.getInt(j))
            }
            list.add(PlayerScore(id = id, name = name, scoreHistory = history, currentInput = ""))
        }
        while (list.size < expectedCount) {
            val i = list.size
            list.add(PlayerScore(id = "${prefix}_${i + 1}", name = "Player ${i + 1}", scoreHistory = emptyList(), currentInput = ""))
        }
        return list
    }
}
