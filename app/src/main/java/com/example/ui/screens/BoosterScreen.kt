package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GameVaultRepository
import com.example.model.GameMode
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.util.Strings

@Composable
fun BoosterScreen(
    repository: GameVaultRepository,
    onNavigateToSubscription: () -> Unit,
    modifier: Modifier = Modifier
) {
    val language by repository.language.collectAsState()
    val telemetry by repository.telemetry.collectAsState()
    val isBoosting by repository.isBoosting.collectAsState()
    val profile by repository.profile.collectAsState()

    val infiniteTransition = rememberInfiniteTransition(label = "booster_ring")
    val ringRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing)
        ),
        label = "booster_rotation"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("booster_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Hero Turbo Dial
        item {
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                if (telemetry.isBoostActive) NeonGreen.copy(alpha = 0.25f) else CyberCyan.copy(alpha = 0.15f),
                                SurfaceDark
                            )
                        )
                    )
                    .border(
                        3.dp,
                        if (isBoosting) NeonGreen else if (telemetry.isBoostActive) NeonGreen else CyberCyan,
                        CircleShape
                    )
                    .clickable(enabled = !isBoosting) {
                        repository.triggerBooster()
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        tint = if (telemetry.isBoostActive) NeonGreen else CyberCyan,
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (isBoosting) Strings.get("boost_running", language)
                        else if (telemetry.isBoostActive) "BOOST ACTIVE"
                        else Strings.get("boost_start_btn", language),
                        color = if (telemetry.isBoostActive) NeonGreen else TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${telemetry.fps} FPS / ${telemetry.maxRefreshRate}Hz",
                        color = CyberCyan,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Cleaned Memory Alert (if boosted)
        if (telemetry.isBoostActive) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(NeonGreen.copy(alpha = 0.15f))
                        .border(1.dp, NeonGreen.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = NeonGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = Strings.get("boost_completed", language),
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "تم تحرير ${telemetry.lastCleanedMb} MB من الذاكرة العشوائية وتثبيت الإطارات",
                                color = NeonGreen,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        // Live Telemetry Cards Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // RAM
                    val ramPct = if (telemetry.ramTotalMb > 0) ((telemetry.ramUsedMb.toFloat() / telemetry.ramTotalMb.toFloat()) * 100).toInt() else 45
                    CyberCard(modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.Memory, contentDescription = null, tint = NeonPurple)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(Strings.get("dash_ram_usage", language), color = TextSecondary, fontSize = 11.sp)
                        Text("$ramPct% (${telemetry.ramUsedMb} MB)", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        LinearProgressIndicator(
                            progress = { ramPct / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            color = if (ramPct > 70) NeonCoral else NeonPurple,
                            trackColor = SurfaceVariantDark
                        )
                    }

                    // Ping
                    CyberCard(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { repository.runPingTest() }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Icon(Icons.Default.Wifi, contentDescription = null, tint = CyberCyan)
                            Icon(Icons.Default.Refresh, contentDescription = "Retest", tint = CyberCyan, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(Strings.get("dash_ping_status", language), color = TextSecondary, fontSize = 11.sp)
                        Text("${telemetry.pingMs} ms", color = if (telemetry.pingMs < 50) NeonGreen else StatusWarning, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Text(Strings.get("boost_ping_good", language), color = NeonGreen, fontSize = 10.sp)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Frame Rate
                    CyberCard(modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.Speed, contentDescription = null, tint = NeonGreen)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(Strings.get("dash_fps_status", language), color = TextSecondary, fontSize = 11.sp)
                        Text("${telemetry.fps} FPS / ${telemetry.maxRefreshRate} Hz", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Text("معدل التحديث الحقيقي للشاشة", color = TextMuted, fontSize = 10.sp)
                    }

                    // Temperature
                    CyberCard(modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.Thermostat, contentDescription = null, tint = if (telemetry.batteryTempC > 40) NeonCoral else CyberCyan)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(Strings.get("dash_temp_status", language), color = TextSecondary, fontSize = 11.sp)
                        Text("${telemetry.batteryTempC} °C", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Text(if (telemetry.batteryTempC <= 38) "حرارة معتدلة ومستقرة" else "حرارة مرتفعة", color = TextMuted, fontSize = 10.sp)
                    }
                }
            }
        }

        // Performance Modes Selector
        item {
            CyberCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = Strings.get("boost_mode_title", language),
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                val modes = listOf(
                    GameMode.PERFORMANCE to Strings.get("boost_mode_perf", language),
                    GameMode.ULTRA_CYBER to Strings.get("boost_mode_ultra", language),
                    GameMode.NORMAL to Strings.get("boost_mode_normal", language),
                    GameMode.BATTERY_SAVER to Strings.get("boost_mode_battery", language)
                )

                modes.forEach { (mode, title) ->
                    val isSelected = telemetry.gameMode == mode
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) CyberCyan.copy(alpha = 0.15f) else Color.Transparent)
                            .clickable {
                                if (mode == GameMode.ULTRA_CYBER && !profile.isVipMember) {
                                    onNavigateToSubscription()
                                } else {
                                    repository.setGameMode(mode)
                                }
                            }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = isSelected,
                                onClick = {
                                    if (mode == GameMode.ULTRA_CYBER && !profile.isVipMember) {
                                        onNavigateToSubscription()
                                    } else {
                                        repository.setGameMode(mode)
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = title,
                                color = if (isSelected) CyberCyan else TextPrimary,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 14.sp
                            )
                        }
                        if (mode == GameMode.ULTRA_CYBER && !profile.isVipMember) {
                            VipBadge(isVip = true)
                        }
                    }
                }
            }
        }

        // Do Not Disturb (DND) Switch
        item {
            CyberCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = Strings.get("boost_dnd_toggle", language),
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "منع النوافذ المنبثقة والمكالمات العشوائية أثناء المباريات الحساسة",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                    Switch(
                        checked = telemetry.dndEnabled,
                        onCheckedChange = { repository.toggleDnd(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = CyberCyan,
                            checkedTrackColor = SurfaceVariantDark
                        )
                    )
                }
            }
        }
    }
}
