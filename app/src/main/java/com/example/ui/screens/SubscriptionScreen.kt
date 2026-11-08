package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.util.Strings

@Composable
fun SubscriptionScreen(
    repository: GameVaultRepository,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val language by repository.language.collectAsState()
    val profile by repository.profile.collectAsState()
    var selectedPlanIsYearly by remember { mutableStateOf(true) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("subscription_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Back Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = Strings.get("sub_title", language),
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Hero VIP Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFF3B2807),
                                Color(0xFF1B1305)
                            )
                        )
                    )
                    .border(1.5.dp, GoldVip, RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Icon(
                        imageVector = Icons.Default.WorkspacePremium,
                        contentDescription = null,
                        tint = GoldVip,
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "FIRE WOLF VIP PASS",
                        color = GoldVipLight,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = Strings.get("sub_desc", language),
                        color = TextSecondary,
                        fontSize = 12.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }

        // Plan Switcher (Monthly vs Yearly)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceDark)
                    .border(1.dp, SurfaceBorder, RoundedCornerShape(12.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Button(
                    onClick = { selectedPlanIsYearly = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (!selectedPlanIsYearly) GoldVip else Color.Transparent,
                        contentColor = if (!selectedPlanIsYearly) Color(0xFF1B1305) else TextSecondary
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("شهري / $2.99", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                Button(
                    onClick = { selectedPlanIsYearly = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedPlanIsYearly) GoldVip else Color.Transparent,
                        contentColor = if (selectedPlanIsYearly) Color(0xFF1B1305) else TextSecondary
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("سنوي / $24.99 (-40%)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }

        // Perks List
        item {
            CyberCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "مميزات الاشتراك الذهبي VIP:",
                    color = GoldVipLight,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                val perks = listOf(
                    "معزز تيربو غير محدود مع دعم شاشات 120Hz و 144Hz",
                    "إدارة غير محدودة لحسابات الألعاب والتبديل الفوري",
                    "أولوية قصوى على خوادم المزامنة السحابية السريعة",
                    "فتح حصري لإطار 'التاج الذهبي VIP' الملكي",
                    "مكافأة فورية 2500 عملة GameCoins للملف الشخصي",
                    "تجربة لعب نظيفة بدون أي إعلانات مزعجة",
                    "دعم فني مخصص للاعبين والبطولات التنافسية"
                )

                perks.forEach { perk ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = GoldVip,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = perk,
                            color = TextPrimary,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // Subscribe Action Button
        item {
            NeonButton(
                text = if (profile.isVipMember) "الاشتراك نشط بالفعل ✓" else "${Strings.get("sub_subscribe_btn", language)} (${if (selectedPlanIsYearly) "$24.99/yr" else "$2.99/mo"})",
                onClick = {
                    repository.activateVipPlan(selectedPlanIsYearly)
                    showSuccessDialog = true
                },
                enabled = !profile.isVipMember,
                color = GoldVip,
                icon = Icons.Default.LockOpen,
                modifier = Modifier.fillMaxWidth(),
                testTag = "btn_subscribe_vip"
            )
        }

        // Google Play Notice
        item {
            Text(
                text = Strings.get("sub_cancel_anytime", language),
                color = TextMuted,
                fontSize = 11.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Celebration, contentDescription = null, tint = GoldVip)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("تهانينا! أنت الآن عضو VIP", color = GoldVipLight, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Text(
                    text = "تم تفعيل اشتراك فاير ولف VIP بنجاح وإضافة 2500 عملة وفتح إطار التاج الذهبي في ملفك الشخصي!",
                    color = TextPrimary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showSuccessDialog = false
                    onBack()
                }) {
                    Text("ابدأ اللعب الآن", color = GoldVip, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = SurfaceDark
        )
    }
}
