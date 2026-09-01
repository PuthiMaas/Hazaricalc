package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PlayerScore

@Composable
fun SikoSectionCard(
    players: List<PlayerScore>,
    isVisible: Boolean,
    onToggleVisibility: () -> Unit,
    onResetSection: () -> Unit,
    onNameChange: (Int, String) -> Unit,
    onInputChange: (Int, String) -> Unit,
    onAddPoints: (Int) -> Unit,
    onDeletePoints: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .testTag("siko_section_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFFDE7)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFF59D))
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header Bar (Matches yellow/gold banner in sketchware: "Hide | Siko Calc v1.0")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(Color(0xFFE4EB8C))
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Hide / Show Button
                FilledTonalButton(
                    onClick = onToggleVisibility,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = Color(0xFF5CA36E),
                        contentColor = Color.White
                    ),
                    modifier = Modifier.testTag("siko_toggle_visibility_button"),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = if (isVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (isVisible) "Hide Siko" else "Show Siko",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isVisible) "Hide" else "Show",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                // Title + Subtitle
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Siko Calc",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF333333),
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "v1.0",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD32F2F)
                    )
                }

                // Section Reset Action
                IconButton(
                    onClick = onResetSection,
                    modifier = Modifier.size(36.dp).testTag("siko_reset_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset Siko Scores",
                        tint = Color(0xFF795548),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Collapsible 3 Player Rows
            AnimatedVisibility(
                visible = isVisible,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    players.forEachIndexed { index, player ->
                        PlayerRowItem(
                            playerNumber = index + 1,
                            player = player,
                            sectionPrefix = "siko",
                            onNameChange = { onNameChange(index, it) },
                            onInputChange = { onInputChange(index, it) },
                            onAdd = { onAddPoints(index) },
                            onDelete = { onDeletePoints(index) }
                        )
                    }
                }
            }
        }
    }
}
