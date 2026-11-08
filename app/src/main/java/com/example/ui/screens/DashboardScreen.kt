package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GameVaultRepository
import com.example.model.GameItem
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.util.AppLanguage
import com.example.util.Strings

@Composable
fun DashboardScreen(
    repository: GameVaultRepository,
    onNavigateToBooster: () -> Unit,
    onNavigateToOrganizer: () -> Unit,
    onNavigateToSubscription: () -> Unit,
    onNavigateToChallenges: () -> Unit,
    modifier: Modifier = Modifier
) {
    val language by repository.language.collectAsState()
    val telemetry by repository.telemetry.collectAsState()
    val isBoosting by repository.isBoosting.collectAsState()
    val profile by repository.profile.collectAsState()
    val games by repository.games.collectAsState()
    val selectedGame by repository.selectedGame.collectAsState()
    val gameCodes by repository.gameCodes.collectAsState()
    val cloudSync by repository.cloudSync.collectAsState()

    val clipboard = LocalClipboardManager.current
    var copiedCodeId by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Player Greeting Card
        item {
            CyberCard(
                borderColor = if (profile.isVipMember) GoldVip.copy(alpha = 0.5f) else SurfaceBorder,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PlayerAvatarWithBorder(
                        avatarId = profile.avatarId,
                        borderId = profile.selectedBorderId,
                        size = 56.dp
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = profile.displayName,
                                color = TextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            VipBadge(isVip = profile.isVipMember)
                        }
                        Text(
                            text = "${Strings.get("prof_level", language)} ${profile.level} • ${profile.rankTitle}",
                            color = CyberCyan,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    CoinsPill(
                        coins = profile.coins,
                        onClick = onNavigateToChallenges
                    )
                }
            }
        }

        // Live Turbo Booster Hero Card
        item {
            val infiniteTransition = rememberInfiniteTransition(label = "pulse")
            val pulseAlpha by infiniteTransition.animateFloat(
                initialValue = 0.4f,
                targetValue = 0.9f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "pulse"
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFF0F1E36),
                                SurfaceDark
                            )
                        )
                    )
                    .border(
                        1.5.dp,
                        if (isBoosting) NeonGreen else CyberCyan.copy(alpha = pulseAlpha),
                        RoundedCornerShape(20.dp)
                    )
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                tint = if (isBoosting) NeonGreen else CyberCyan,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = Strings.get("dash_quick_boost", language),
                                color = TextPrimary,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                        if (telemetry.isBoostActive) {
                            Text(
                                text = "TURBO ON",
                                color = NeonGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(NeonGreen.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Telemetry Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatBadge(
                            label = Strings.get("dash_fps_status", language),
                            value = "${telemetry.fps} FPS",
                            icon = Icons.Default.Speed,
                            color = NeonGreen,
                            modifier = Modifier.weight(1f)
                        )
                        StatBadge(
                            label = Strings.get("dash_ping_status", language),
                            value = "${telemetry.pingMs} ms",
                            icon = Icons.Default.Wifi,
                            color = CyberCyan,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        val usedPercent = if (telemetry.ramTotalMb > 0) {
                            ((telemetry.ramUsedMb.toFloat() / telemetry.ramTotalMb.toFloat()) * 100).toInt()
                        } else 45
                        StatBadge(
                            label = Strings.get("dash_ram_usage", language),
                            value = "$usedPercent% (${telemetry.ramUsedMb}M)",
                            icon = Icons.Default.Memory,
                            color = NeonPurple,
                            modifier = Modifier.weight(1f)
                        )
                        StatBadge(
                            label = Strings.get("dash_temp_status", language),
                            value = "${telemetry.batteryTempC} °C",
                            icon = Icons.Default.Thermostat,
                            color = if (telemetry.batteryTempC > 40) NeonCoral else CyberCyan,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    NeonButton(
                        text = if (isBoosting) Strings.get("boost_running", language) else Strings.get("boost_start_btn", language),
                        onClick = { repository.triggerBooster() },
                        enabled = !isBoosting,
                        icon = Icons.Default.RocketLaunch,
                        color = if (telemetry.isBoostActive) NeonGreen else CyberCyan,
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "quick_boost_action"
                    )
                }
            }
        }

        // Dedicated Cloud Sync & Anti-Tamper Card
        item {
            CyberCard(
                borderColor = CyberCyanDark.copy(alpha = 0.4f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CloudSync,
                            contentDescription = null,
                            tint = CyberCyan,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = Strings.get("dash_cloud_server", language),
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(
                        onClick = { repository.syncWithCloudServer() },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sync,
                            contentDescription = "Sync",
                            tint = CyberCyan
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "${cloudSync.serverRegion} • ${cloudSync.encryptionType}",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = null,
                        tint = NeonGreen,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = Strings.get("dash_cloud_secure", language),
                        color = NeonGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = cloudSync.lastSyncTime,
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }
        }

        // Active Game Selector Row
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = Strings.get("dash_active_game", language),
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = onNavigateToOrganizer) {
                        Text(
                            text = Strings.get("nav_organizer", language),
                            color = CyberCyan,
                            fontSize = 13.sp
                        )
                    }
                }

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(games) { game ->
                        val isSelected = selectedGame?.id == game.id
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) CyberCyan.copy(alpha = 0.2f) else SurfaceVariantDark)
                                .border(
                                    1.dp,
                                    if (isSelected) CyberCyan else SurfaceBorder,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { repository.setSelectedGame(game) }
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.SportsEsports,
                                    contentDescription = null,
                                    tint = if (isSelected) CyberCyan else TextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = game.title,
                                    color = if (isSelected) TextPrimary else TextSecondary,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }
        }

        // Recent Verified Game Codes (نقل الرمز)
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = Strings.get("org_tab_codes", language),
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = onNavigateToOrganizer) {
                        Text(
                            text = Strings.get("org_transfer_code_btn", language),
                            color = CyberCyan,
                            fontSize = 13.sp
                        )
                    }
                }

                gameCodes.take(2).forEach { codeItem ->
                    CyberCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        borderColor = SurfaceBorder
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = codeItem.title,
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${codeItem.gameTitle} • ${codeItem.antiFraudHash}",
                                    color = NeonGreen,
                                    fontSize = 11.sp
                                )
                            }
                            IconButton(
                                onClick = {
                                    clipboard.setText(AnnotatedString(codeItem.code))
                                    copiedCodeId = codeItem.id
                                }
                            ) {
                                Icon(
                                    imageVector = if (copiedCodeId == codeItem.id) Icons.Default.Check else Icons.Default.ContentCopy,
                                    contentDescription = "Copy Code",
                                    tint = if (copiedCodeId == codeItem.id) NeonGreen else CyberCyan
                                )
                            }
                        }
                    }
                }
            }
        }

        // VIP Subscription Upgrade Prompt (if not VIP)
        if (!profile.isVipMember) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFF2C1E0A),
                                    Color(0xFF161005)
                                )
                            )
                        )
                        .border(1.dp, GoldVip.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .clickable(onClick = onNavigateToSubscription)
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.WorkspacePremium,
                                contentDescription = null,
                                tint = GoldVip,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = Strings.get("sub_title", language),
                                    color = GoldVipLight,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = Strings.get("dash_upgrade_vip", language),
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = GoldVip
                        )
                    }
                }
            }
        }

        // Google Play Compliance Footer
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = Strings.get("policy_verified", language),
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }
        }
    }
}
