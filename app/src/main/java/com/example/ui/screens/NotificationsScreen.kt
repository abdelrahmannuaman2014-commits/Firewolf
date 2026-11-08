package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GameVaultRepository
import com.example.model.NotificationCategory
import com.example.ui.components.CyberCard
import com.example.ui.theme.*
import com.example.util.Strings

@Composable
fun NotificationsScreen(
    repository: GameVaultRepository,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val language by repository.language.collectAsState()
    val notifications by repository.notifications.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("notifications_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Top Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = Strings.get("notif_title", language),
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                TextButton(
                    onClick = {
                        notifications.forEach { repository.markNotificationRead(it.id) }
                    }
                ) {
                    Text(Strings.get("notif_mark_all", language), color = CyberCyan, fontSize = 12.sp)
                }
            }
        }

        if (notifications.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = Strings.get("notif_empty", language),
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                }
            }
        }

        items(notifications) { notif ->
            val title = if (language == com.example.util.AppLanguage.ARABIC) notif.titleAr else notif.titleEn
            val msg = if (language == com.example.util.AppLanguage.ARABIC) notif.messageAr else notif.messageEn
            val time = if (language == com.example.util.AppLanguage.ARABIC) notif.timeAgoAr else notif.timeAgoEn

            val icon = when (notif.category) {
                NotificationCategory.DAILY_CHALLENGE -> Icons.Default.EmojiEvents
                NotificationCategory.BOOST_REMINDER -> Icons.Default.Speed
                NotificationCategory.SECURITY_ALERT -> Icons.Default.Shield
                NotificationCategory.EVENT_BROADCAST -> Icons.Default.Campaign
            }

            val iconColor = when (notif.category) {
                NotificationCategory.DAILY_CHALLENGE -> GoldVip
                NotificationCategory.BOOST_REMINDER -> CyberCyan
                NotificationCategory.SECURITY_ALERT -> NeonGreen
                NotificationCategory.EVENT_BROADCAST -> NeonPurple
            }

            CyberCard(
                borderColor = if (!notif.isRead) iconColor.copy(alpha = 0.5f) else SurfaceBorder,
                onClick = { repository.markNotificationRead(notif.id) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(iconColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = title,
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = time,
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = msg,
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}
