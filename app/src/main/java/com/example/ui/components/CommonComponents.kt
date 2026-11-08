package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun CyberCard(
    modifier: Modifier = Modifier,
    borderColor: Color = SurfaceBorder,
    glowColor: Color? = null,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(16.dp)
    val cardModifier = modifier
        .clip(shape)
        .background(SurfaceDark)
        .border(1.dp, borderColor, shape)
        .then(
            if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
        )
        .padding(16.dp)

    Column(modifier = cardModifier, content = content)
}

@Composable
fun NeonButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    isSecondary: Boolean = false,
    color: Color = CyberCyan,
    enabled: Boolean = true,
    testTag: String = "neon_button"
) {
    val gradient = if (isSecondary) {
        Brush.horizontalGradient(listOf(SurfaceVariantDark, SurfaceDark))
    } else {
        Brush.horizontalGradient(listOf(color, color.copy(alpha = 0.8f)))
    }

    val contentColor = if (isSecondary) TextPrimary else Color(0xFF070B12)

    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        color = Color.Transparent,
        modifier = modifier
            .defaultMinSize(minHeight = 48.dp)
            .testTag(testTag)
            .clip(RoundedCornerShape(12.dp))
            .background(gradient)
            .border(
                1.dp,
                if (isSecondary) SurfaceBorder else color.copy(alpha = 0.6f),
                RoundedCornerShape(12.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                color = contentColor,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }
    }
}

@Composable
fun StatBadge(
    label: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceVariantDark)
            .border(1.dp, color.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = label,
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value,
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
fun CoinsPill(
    coins: Int,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(GoldVip.copy(alpha = 0.15f))
            .border(1.dp, GoldVip.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.MonetizationOn,
            contentDescription = "GameCoins",
            tint = GoldVip,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "$coins",
            color = GoldVipLight,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PlayerAvatarWithBorder(
    avatarId: String,
    borderId: String,
    size: Dp = 64.dp,
    modifier: Modifier = Modifier
) {
    val borderColor = when (borderId) {
        "border_gold_vip" -> GoldVip
        "border_dragon" -> NeonCoral
        "border_diamond" -> NeonPurple
        "border_neon_cyan" -> CyberCyan
        else -> SurfaceBorder
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .border(3.dp, borderColor, CircleShape)
            .background(SurfaceVariantDark)
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = when (avatarId) {
                "avatar_cyber_2" -> Icons.Default.SportsEsports
                "avatar_cyber_3" -> Icons.Default.Shield
                "avatar_cyber_4" -> Icons.Default.MilitaryTech
                else -> Icons.Default.Person
            },
            contentDescription = "Player Avatar",
            tint = CyberCyan,
            modifier = Modifier.size(size * 0.6f)
        )
    }
}

@Composable
fun VipBadge(
    isVip: Boolean,
    modifier: Modifier = Modifier
) {
    val bg = if (isVip) GoldVip.copy(alpha = 0.2f) else SurfaceVariantDark
    val text = if (isVip) "VIP PRO" else "FREE"
    val color = if (isVip) GoldVip else TextSecondary

    Text(
        text = text,
        color = color,
        fontSize = 11.sp,
        fontWeight = FontWeight.Black,
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bg)
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    )
}
