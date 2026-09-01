package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.HazariSectionCard
import com.example.ui.components.SikoSectionCard
import com.example.viewmodel.CardGameViewModel
import com.example.viewmodel.ResetTarget

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardGameScreen(
    viewModel: CardGameViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showInfoDialog by remember { mutableStateOf(false) }

    val hazariLeader = uiState.hazariPlayers.maxByOrNull { it.totalScore }
    val hazariWinner = uiState.hazariPlayers.find { it.totalScore >= uiState.hazariTargetScore }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Casino,
                            contentDescription = "Card Game",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Hazari & Siko Calc",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showInfoDialog = true },
                        modifier = Modifier.testTag("info_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Rules & Info",
                            tint = Color.White
                        )
                    }
                    IconButton(
                        onClick = { viewModel.promptReset(ResetTarget.ALL) },
                        modifier = Modifier.testTag("reset_all_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset All Games",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF1B5E20)
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            // Optional winner / leader banner if someone has points
            if (hazariWinner != null) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFFB300))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = "Trophy",
                                tint = Color(0xFFF57F17),
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "👑 ${hazariWinner.name} won Hazari with ${hazariWinner.totalScore} pts!",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp,
                                color = Color(0xFF5D4037)
                            )
                        }
                    }
                }
            }

            // Hazari Section (6 players)
            item {
                HazariSectionCard(
                    players = uiState.hazariPlayers,
                    isVisible = uiState.isHazariVisible,
                    targetScore = uiState.hazariTargetScore,
                    onToggleVisibility = { viewModel.toggleHazariVisibility() },
                    onResetSection = { viewModel.promptReset(ResetTarget.HAZARI) },
                    onNameChange = { index, name -> viewModel.onHazariNameChange(index, name) },
                    onInputChange = { index, input -> viewModel.onHazariInputChange(index, input) },
                    onAddPoints = { index -> viewModel.addHazariPoints(index) },
                    onDeletePoints = { index -> viewModel.deleteHazariPoints(index) }
                )
            }

            // Siko Section (3 players)
            item {
                SikoSectionCard(
                    players = uiState.sikoPlayers,
                    isVisible = uiState.isSikoVisible,
                    onToggleVisibility = { viewModel.toggleSikoVisibility() },
                    onResetSection = { viewModel.promptReset(ResetTarget.SIKO) },
                    onNameChange = { index, name -> viewModel.onSikoNameChange(index, name) },
                    onInputChange = { index, input -> viewModel.onSikoInputChange(index, input) },
                    onAddPoints = { index -> viewModel.addSikoPoints(index) },
                    onDeletePoints = { index -> viewModel.deleteSikoPoints(index) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // Reset Confirmation Dialog
        if (uiState.showResetDialog) {
            val sectionName = when (uiState.resetTargetSection) {
                ResetTarget.ALL -> "all scores (Hazari & Siko)"
                ResetTarget.HAZARI -> "Hazari scores"
                ResetTarget.SIKO -> "Siko scores"
                null -> "scores"
            }

            AlertDialog(
                onDismissRequest = { viewModel.dismissResetDialog() },
                title = {
                    Text(
                        text = "Reset Scores?",
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(text = "Are you sure you want to clear $sectionName? This will reset all round history and totals to zero.")
                },
                confirmButton = {
                    TextButton(
                        onClick = { viewModel.confirmReset() },
                        colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFD32F2F)),
                        modifier = Modifier.testTag("confirm_reset_button")
                    ) {
                        Text(text = "Reset", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { viewModel.dismissResetDialog() }
                    ) {
                        Text(text = "Cancel")
                    }
                }
            )
        }

        // Rules / Info Dialog
        if (showInfoDialog) {
            AlertDialog(
                onDismissRequest = { showInfoDialog = false },
                title = {
                    Text(
                        text = "Card Game Calculator",
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "♠️ Hazari Calc (6 Players):",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFF1B5E20)
                        )
                        Text(
                            text = "• Enter player name and points scored in each round.\n• Press 'Add' to automatically add round points.\n• Previous rounds appear as '00 + 00 + 00' and total score is calculated on the right.\n• Press 'Delete' to clear current input or undo last round.\n• Target: 1000 points to win.",
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "♥️ Siko Calc (3 Players):",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFFF57F17)
                        )
                        Text(
                            text = "• 3-player score calculator with round history and auto-sum.",
                            fontSize = 12.sp
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showInfoDialog = false }) {
                        Text(text = "Got It", fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    }
}
