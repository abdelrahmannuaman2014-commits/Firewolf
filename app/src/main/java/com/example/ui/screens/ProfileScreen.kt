package com.example.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GameVaultRepository
import com.example.model.CosmeticBorder
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.util.Strings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    repository: GameVaultRepository,
    onNavigateToSubscription: () -> Unit,
    modifier: Modifier = Modifier
) {
    val language by repository.language.collectAsState()
    val profile by repository.profile.collectAsState()
    val cosmeticBorders by repository.cosmeticBorders.collectAsState()

    var isEditingProfile by remember { mutableStateOf(false) }
    var editName by remember(profile.displayName) { mutableStateOf(profile.displayName) }
    var editBio by remember(profile.bio) { mutableStateOf(profile.bio) }
    var selectedAvatar by remember(profile.avatarId) { mutableStateOf(profile.avatarId) }
    var selectedBorder by remember(profile.selectedBorderId) { mutableStateOf(profile.selectedBorderId) }

    var showPolicyDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("profile_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Hero Avatar & Level
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                PlayerAvatarWithBorder(
                    avatarId = selectedAvatar,
                    borderId = selectedBorder,
                    size = 96.dp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = profile.displayName,
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    VipBadge(isVip = profile.isVipMember)
                }
                Text(
                    text = profile.gamerTag,
                    color = CyberCyan,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                if (profile.bio.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = profile.bio,
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Level & XP Progress Card
        item {
            CyberCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${Strings.get("prof_level", language)} ${profile.level}",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${profile.xp} / ${profile.maxXpForNextLevel} XP",
                        color = CyberCyan,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                val xpFraction = (profile.xp.toFloat() / profile.maxXpForNextLevel.toFloat()).coerceIn(0f, 1f)
                LinearProgressIndicator(
                    progress = { xpFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = CyberCyan,
                    trackColor = SurfaceVariantDark
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "الرتبة: ${profile.rankTitle} • استمر بإكمال التحديات لفتح المزيد من الإطارات",
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }
        }

        // Unlockable Cosmetic Borders Section (عناصر تجميلية قابلة للفتح)
        item {
            CyberCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = Strings.get("prof_cosmetics", language),
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "إطارات ملف شخصي مميزة تُفتح عند وصولك لمستويات أعلى أو ترقية VIP",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(14.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(cosmeticBorders) { borderItem ->
                        val isUnlocked = profile.unlockedBorderIds.contains(borderItem.id) ||
                                (borderItem.isVipOnly && profile.isVipMember) ||
                                profile.level >= borderItem.minLevel

                        val isEquipped = selectedBorder == borderItem.id

                        Box(
                            modifier = Modifier
                                .width(120.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isEquipped) CyberCyan.copy(alpha = 0.15f) else SurfaceVariantDark)
                                .border(
                                    2.dp,
                                    if (isEquipped) Color(borderItem.colorHex) else SurfaceBorder,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable(enabled = isUnlocked) {
                                    selectedBorder = borderItem.id
                                    repository.updateProfile(
                                        profile.displayName,
                                        profile.bio,
                                        profile.avatarId,
                                        borderItem.id
                                    )
                                }
                                .padding(10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .border(2.5.dp, Color(borderItem.colorHex), CircleShape)
                                        .padding(4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (borderItem.isVipOnly) Icons.Default.WorkspacePremium else Icons.Default.Shield,
                                        contentDescription = null,
                                        tint = Color(borderItem.colorHex),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = if (language == com.example.util.AppLanguage.ARABIC) borderItem.nameAr else borderItem.nameEn,
                                    color = TextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                if (isEquipped) {
                                    Text("مُفعل ✓", color = CyberCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                } else if (isUnlocked) {
                                    Text("مفتوح", color = NeonGreen, fontSize = 10.sp)
                                } else {
                                    Text(
                                        if (borderItem.isVipOnly) "VIP حصري" else "مستوى ${borderItem.minLevel}",
                                        color = TextMuted,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Edit Profile Dialog / Trigger
        item {
            NeonButton(
                text = Strings.get("prof_edit", language),
                onClick = { isEditingProfile = true },
                icon = Icons.Default.Edit,
                isSecondary = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Google Sign-In & Cloud Sync Card
        item {
            CyberCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AccountCircle, contentDescription = null, tint = CyberCyan)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (profile.isGoogleLinked) Strings.get("prof_google_linked", language) else Strings.get("prof_google_btn", language),
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (profile.isGoogleLinked) profile.googleEmail else "سجّل الدخول بجوجل لمزامنة الأكواد والحسابات سحابياً",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                    Switch(
                        checked = profile.isGoogleLinked,
                        onCheckedChange = { repository.toggleGoogleSignIn() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = CyberCyan,
                            checkedTrackColor = SurfaceVariantDark
                        )
                    )
                }
            }
        }

        // VIP Pass Status / Upgrade
        item {
            CyberCard(
                borderColor = if (profile.isVipMember) GoldVip else SurfaceBorder,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = Strings.get("sub_title", language),
                            color = if (profile.isVipMember) GoldVipLight else TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (profile.isVipMember) "الاشتراك نشط: ${profile.vipExpiryDate}" else "احصل على معزز 120Hz وإطارات حصرية ومزامنة فورية",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                    if (!profile.isVipMember) {
                        Button(
                            onClick = onNavigateToSubscription,
                            colors = ButtonDefaults.buttonColors(containerColor = GoldVip, contentColor = Color(0xFF161005))
                        ) {
                            Text("ترقية VIP", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Privacy Policy & Google Play Compliance
        item {
            TextButton(onClick = { showPolicyDialog = true }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.PrivacyTip, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(Strings.get("policy_title", language), color = TextMuted, fontSize = 12.sp)
                }
            }
        }
    }

    // Edit Profile Modal
    if (isEditingProfile) {
        val avatarOptions = listOf("avatar_cyber_1", "avatar_cyber_2", "avatar_cyber_3", "avatar_cyber_4")

        AlertDialog(
            onDismissRequest = { isEditingProfile = false },
            title = {
                Text(Strings.get("prof_edit", language), color = TextPrimary, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("اختر الصورة الرمزية / Avatar:", color = TextSecondary, fontSize = 12.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        avatarOptions.forEach { avId ->
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .border(
                                        2.dp,
                                        if (selectedAvatar == avId) CyberCyan else SurfaceBorder,
                                        CircleShape
                                    )
                                    .clickable { selectedAvatar = avId }
                                    .padding(6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when (avId) {
                                        "avatar_cyber_2" -> Icons.Default.SportsEsports
                                        "avatar_cyber_3" -> Icons.Default.Shield
                                        "avatar_cyber_4" -> Icons.Default.MilitaryTech
                                        else -> Icons.Default.Person
                                    },
                                    contentDescription = null,
                                    tint = if (selectedAvatar == avId) CyberCyan else TextSecondary
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text(Strings.get("prof_display_name", language)) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = editBio,
                        onValueChange = { editBio = it },
                        label = { Text(Strings.get("prof_bio", language)) },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        repository.updateProfile(
                            displayName = editName,
                            bio = editBio,
                            avatarId = selectedAvatar,
                            selectedBorderId = selectedBorder
                        )
                        isEditingProfile = false
                    }
                ) {
                    Text(Strings.get("prof_save", language), color = CyberCyan, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { isEditingProfile = false }) {
                    Text("إلغاء", color = TextSecondary)
                }
            },
            containerColor = SurfaceDark
        )
    }

    // Policy Dialog
    if (showPolicyDialog) {
        AlertDialog(
            onDismissRequest = { showPolicyDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = NeonGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(Strings.get("policy_title", language), color = TextPrimary)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = Strings.get("policy_verified", language),
                        color = NeonGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = Strings.get("policy_text", language),
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "1. لا يتم جمع أو مشاركة أي بيانات حساسة.\n2. يتم تشفير كافة رموز الألعاب والحسابات محلياً.\n3. عمليات المعزز تستند إلى قياسات ذاكرة النظام الحقيقية دون أي ادعاءات مضللة.",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showPolicyDialog = false }) {
                    Text("حسناً", color = CyberCyan)
                }
            },
            containerColor = SurfaceDark
        )
    }
}
