package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GameVaultRepository
import com.example.ui.components.CoinsPill
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.util.AppLanguage
import com.example.util.Strings

enum class AppScreen {
    DASHBOARD,
    ORGANIZER,
    BOOSTER,
    CHALLENGES,
    PROFILE,
    SUBSCRIPTION,
    NOTIFICATIONS
}

class MainActivity : ComponentActivity() {

    private lateinit var repository: GameVaultRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        repository = GameVaultRepository(applicationContext)

        setContent {
            MyApplicationTheme {
                val language by repository.language.collectAsState()
                val profile by repository.profile.collectAsState()
                val notifications by repository.notifications.collectAsState()
                val unreadNotifsCount = notifications.count { !it.isRead }

                var currentScreen by remember { mutableStateOf(AppScreen.DASHBOARD) }
                val screenBackStack = remember { mutableStateListOf<AppScreen>() }

                fun navigateTo(screen: AppScreen) {
                    if (currentScreen != screen) {
                        screenBackStack.add(currentScreen)
                        currentScreen = screen
                    }
                }

                fun navigateBack() {
                    if (screenBackStack.isNotEmpty()) {
                        currentScreen = screenBackStack.removeAt(screenBackStack.size - 1)
                    } else {
                        currentScreen = AppScreen.DASHBOARD
                    }
                }

                BackHandler(enabled = currentScreen != AppScreen.DASHBOARD) {
                    navigateBack()
                }

                val layoutDirection = if (language == AppLanguage.ARABIC) LayoutDirection.Rtl else LayoutDirection.Ltr

                CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                    Scaffold(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(BackgroundDark),
                        containerColor = BackgroundDark,
                        topBar = {
                            GameVaultTopBar(
                                language = language,
                                coins = profile.coins,
                                unreadCount = unreadNotifsCount,
                                onLanguageToggle = { repository.toggleLanguage() },
                                onCoinsClick = { navigateTo(AppScreen.CHALLENGES) },
                                onNotificationsClick = { navigateTo(AppScreen.NOTIFICATIONS) }
                            )
                        },
                        bottomBar = {
                            if (currentScreen != AppScreen.SUBSCRIPTION && currentScreen != AppScreen.NOTIFICATIONS) {
                                GameVaultBottomNavigation(
                                    currentScreen = currentScreen,
                                    language = language,
                                    onScreenSelected = { targetScreen ->
                                        if (currentScreen != targetScreen) {
                                            screenBackStack.clear()
                                            currentScreen = targetScreen
                                        }
                                    }
                                )
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            when (currentScreen) {
                                AppScreen.DASHBOARD -> DashboardScreen(
                                    repository = repository,
                                    onNavigateToBooster = { navigateTo(AppScreen.BOOSTER) },
                                    onNavigateToOrganizer = { navigateTo(AppScreen.ORGANIZER) },
                                    onNavigateToSubscription = { navigateTo(AppScreen.SUBSCRIPTION) },
                                    onNavigateToChallenges = { navigateTo(AppScreen.CHALLENGES) }
                                )
                                AppScreen.ORGANIZER -> GameOrganizerScreen(
                                    repository = repository
                                )
                                AppScreen.BOOSTER -> BoosterScreen(
                                    repository = repository,
                                    onNavigateToSubscription = { navigateTo(AppScreen.SUBSCRIPTION) }
                                )
                                AppScreen.CHALLENGES -> ChallengesScreen(
                                    repository = repository
                                )
                                AppScreen.PROFILE -> ProfileScreen(
                                    repository = repository,
                                    onNavigateToSubscription = { navigateTo(AppScreen.SUBSCRIPTION) }
                                )
                                AppScreen.SUBSCRIPTION -> SubscriptionScreen(
                                    repository = repository,
                                    onBack = { navigateBack() }
                                )
                                AppScreen.NOTIFICATIONS -> NotificationsScreen(
                                    repository = repository,
                                    onBack = { navigateBack() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GameVaultTopBar(
    language: AppLanguage,
    coins: Int,
    unreadCount: Int,
    onLanguageToggle: () -> Unit,
    onCoinsClick: () -> Unit,
    onNotificationsClick: () -> Unit
) {
    Surface(
        color = SurfaceDark,
        tonalElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // App Branding
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(CyberCyan.copy(alpha = 0.2f))
                        .border(1.dp, CyberCyan, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = null,
                        tint = NeonCoral,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = Strings.get("app_title", language),
                        color = TextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "FIRE WOLF GAMING HUB",
                        color = NeonCoral,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }

            // Top Action Controls
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Language Switcher
                Surface(
                    onClick = onLanguageToggle,
                    shape = RoundedCornerShape(20.dp),
                    color = SurfaceVariantDark,
                    border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
                    modifier = Modifier.testTag("lang_toggle_btn")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = null,
                            tint = CyberCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (language == AppLanguage.ARABIC) "EN" else "عربي",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Coins Pill
                CoinsPill(coins = coins, onClick = onCoinsClick)

                // Notification Bell with Badge
                Box {
                    IconButton(
                        onClick = onNotificationsClick,
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("notification_bell_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = if (unreadCount > 0) CyberCyan else TextSecondary
                        )
                    }
                    if (unreadCount > 0) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = (-2).dp, y = 2.dp)
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(NeonCoral),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$unreadCount",
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GameVaultBottomNavigation(
    currentScreen: AppScreen,
    language: AppLanguage,
    onScreenSelected: (AppScreen) -> Unit
) {
    NavigationBar(
        containerColor = SurfaceDark,
        tonalElevation = 8.dp,
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .testTag("bottom_nav_bar")
    ) {
        val navItems = listOf(
            Triple(AppScreen.DASHBOARD, Strings.get("nav_dashboard", language), Icons.Default.Dashboard),
            Triple(AppScreen.ORGANIZER, Strings.get("nav_organizer", language), Icons.Default.SportsEsports),
            Triple(AppScreen.BOOSTER, Strings.get("nav_booster", language), Icons.Default.Bolt),
            Triple(AppScreen.CHALLENGES, Strings.get("nav_challenges", language), Icons.Default.EmojiEvents),
            Triple(AppScreen.PROFILE, Strings.get("nav_profile", language), Icons.Default.Person)
        )

        navItems.forEach { (screen, label, icon) ->
            val isSelected = currentScreen == screen
            NavigationBarItem(
                selected = isSelected,
                onClick = { onScreenSelected(screen) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF04210D),
                    selectedTextColor = CyberCyan,
                    indicatorColor = CyberCyan,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary
                ),
                modifier = Modifier.testTag("nav_item_${screen.name.lowercase()}")
            )
        }
    }
}
