package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.model.Challenge
import com.example.model.ChallengeType
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.util.Strings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChallengesScreen(
    repository: GameVaultRepository,
    modifier: Modifier = Modifier
) {
    val language by repository.language.collectAsState()
    val challenges by repository.challenges.collectAsState()
    val profile by repository.profile.collectAsState()

    var showAdminDialog by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf<ChallengeType?>(null) }

    val filteredChallenges = remember(challenges, selectedFilter) {
        if (selectedFilter == null) challenges else challenges.filter { it.type == selectedFilter }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("challenges_screen"),
        containerColor = BackgroundDark,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAdminDialog = true },
                containerColor = NeonPurple,
                contentColor = Color.White,
                icon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = null) },
                text = { Text(Strings.get("chall_admin_btn", language), fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("fab_admin_challenges")
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Hero
            item {
                CyberCard(
                    borderColor = NeonPurple.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = Strings.get("chall_title", language),
                                color = TextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "أكمل التحديات واكسب عملات GameCoins وعناصر تجميلية حصرية",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                        CoinsPill(coins = profile.coins)
                    }
                }
            }

            // Filter Chips Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedFilter == null,
                        onClick = { selectedFilter = null },
                        label = { Text("الكل / All", fontSize = 12.sp) }
                    )
                    FilterChip(
                        selected = selectedFilter == ChallengeType.DAILY,
                        onClick = { selectedFilter = ChallengeType.DAILY },
                        label = { Text(Strings.get("chall_daily", language), fontSize = 12.sp) }
                    )
                    FilterChip(
                        selected = selectedFilter == ChallengeType.WEEKLY,
                        onClick = { selectedFilter = ChallengeType.WEEKLY },
                        label = { Text(Strings.get("chall_weekly", language), fontSize = 12.sp) }
                    )
                }
            }

            // Challenges List
            items(filteredChallenges) { challenge ->
                val title = if (language == com.example.util.AppLanguage.ARABIC) challenge.titleAr else challenge.titleEn
                val desc = if (language == com.example.util.AppLanguage.ARABIC) challenge.descAr else challenge.descEn
                val isCompleted = challenge.progress >= challenge.maxProgress || challenge.isCompleted

                CyberCard(
                    borderColor = when {
                        challenge.isClaimed -> SurfaceBorder
                        isCompleted -> NeonGreen
                        else -> SurfaceBorder
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (challenge.type == ChallengeType.DAILY) "يومي" else "أسبوعي",
                                    color = if (challenge.type == ChallengeType.DAILY) CyberCyan else NeonPurple,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(
                                            (if (challenge.type == ChallengeType.DAILY) CyberCyan else NeonPurple).copy(alpha = 0.15f)
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                                if (challenge.isCustomAdminCreated) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "مشرف / Admin",
                                        color = GoldVip,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(GoldVip.copy(alpha = 0.15f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = title,
                                color = TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = desc,
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }

                        if (challenge.isCustomAdminCreated) {
                            IconButton(
                                onClick = { repository.deleteChallenge(challenge.id) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = TextMuted)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Progress Bar
                    val progressFraction = (challenge.progress.toFloat() / challenge.maxProgress.toFloat()).coerceIn(0f, 1f)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LinearProgressIndicator(
                            progress = { progressFraction },
                            modifier = Modifier
                                .weight(1f)
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = if (isCompleted) NeonGreen else CyberCyan,
                            trackColor = SurfaceVariantDark
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "${challenge.progress}/${challenge.maxProgress}",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Rewards & Claim Button Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = GoldVip, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("+${challenge.rewardCoins}", color = GoldVipLight, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(10.dp))
                            Icon(Icons.Default.Star, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("+${challenge.rewardXp} XP", color = CyberCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold)

                            if (challenge.rewardCosmeticId != null) {
                                Spacer(modifier = Modifier.width(10.dp))
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = NeonPurple, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("إطار مميز", color = NeonPurple, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        when {
                            challenge.isClaimed -> {
                                Text(
                                    text = Strings.get("chall_claimed", language),
                                    color = TextMuted,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            isCompleted -> {
                                Button(
                                    onClick = { repository.claimChallenge(challenge.id) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = NeonGreen,
                                        contentColor = Color(0xFF04210D)
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.testTag("claim_btn_${challenge.id}")
                                ) {
                                    Text(Strings.get("chall_claim", language), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                            else -> {
                                Text(
                                    text = Strings.get("chall_in_progress", language),
                                    color = TextMuted,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Admin Dialog for Creating Challenges (واجهة سهلة للمشرفين)
    if (showAdminDialog) {
        var adminTitleAr by remember { mutableStateOf("") }
        var adminTitleEn by remember { mutableStateOf("") }
        var adminDescAr by remember { mutableStateOf("") }
        var adminCoins by remember { mutableStateOf("300") }
        var adminXp by remember { mutableStateOf("500") }
        var adminProgress by remember { mutableStateOf("3") }
        var adminType by remember { mutableStateOf(ChallengeType.DAILY) }

        AlertDialog(
            onDismissRequest = { showAdminDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Shield, contentDescription = null, tint = NeonPurple)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(Strings.get("chall_create_title", language), color = TextPrimary, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = Strings.get("chall_admin_desc", language),
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    OutlinedTextField(
                        value = adminTitleAr,
                        onValueChange = { adminTitleAr = it },
                        label = { Text("عنوان التحدي (بالعربية)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = adminTitleEn,
                        onValueChange = { adminTitleEn = it },
                        label = { Text("Challenge Title (English)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = adminDescAr,
                        onValueChange = { adminDescAr = it },
                        label = { Text("وصف التحدي ومهمة اللاعب") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = adminCoins,
                            onValueChange = { adminCoins = it },
                            label = { Text("العملات") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = adminXp,
                            onValueChange = { adminXp = it },
                            label = { Text("الخبرة XP") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = adminProgress,
                            onValueChange = { adminProgress = it },
                            label = { Text("المطلوب") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = adminType == ChallengeType.DAILY,
                            onClick = { adminType = ChallengeType.DAILY },
                            label = { Text("تحدي يومي") }
                        )
                        FilterChip(
                            selected = adminType == ChallengeType.WEEKLY,
                            onClick = { adminType = ChallengeType.WEEKLY },
                            label = { Text("تحدي أسبوعي") }
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (adminTitleAr.isNotBlank()) {
                            repository.addAdminChallenge(
                                titleAr = adminTitleAr,
                                titleEn = if (adminTitleEn.isNotBlank()) adminTitleEn else adminTitleAr,
                                descAr = adminDescAr,
                                descEn = adminDescAr,
                                type = adminType,
                                coins = adminCoins.toIntOrNull() ?: 200,
                                xp = adminXp.toIntOrNull() ?: 300,
                                maxProgress = adminProgress.toIntOrNull() ?: 3
                            )
                            showAdminDialog = false
                        }
                    }
                ) {
                    Text(Strings.get("chall_add_btn", language), color = NeonPurple, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAdminDialog = false }) {
                    Text("إلغاء", color = TextSecondary)
                }
            },
            containerColor = SurfaceDark
        )
    }
}
