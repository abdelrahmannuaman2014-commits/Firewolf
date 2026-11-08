package com.example.model

enum class GameGenre {
    BATTLE_ROYALE, FPS, MOBA, SANDBOX, RPG, RACING, STRATEGY
}

enum class CodeType {
    REDEEM_CODE,
    GRAPHICS_CONFIG,
    SENSITIVITY_SETUP,
    TOURNAMENT_ROOM_KEY
}

enum class GameMode {
    NORMAL,
    PERFORMANCE,
    BATTERY_SAVER,
    ULTRA_CYBER
}

data class GameItem(
    val id: String,
    val title: String,
    val packageName: String,
    val genre: GameGenre,
    val rating: Float,
    val playTimeMinutes: Int,
    val isFavorite: Boolean = false,
    val supportedFps: Int = 120,
    val description: String = ""
)

data class GameCodeItem(
    val id: String,
    val gameId: String,
    val gameTitle: String,
    val title: String,
    val code: String,
    val codeType: CodeType,
    val antiFraudHash: String,
    val author: String,
    val isVerified: Boolean = true,
    val usageCount: Int = 0,
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

data class GameAccount(
    val id: String,
    val gameId: String,
    val gameTitle: String,
    val accountTag: String,
    val accountIdOrUser: String,
    val region: String,
    val notes: String = "",
    val isEncrypted: Boolean = true,
    val isPrimary: Boolean = false,
    val lastUsedTimestamp: Long = System.currentTimeMillis()
)

data class CosmeticBorder(
    val id: String,
    val nameAr: String,
    val nameEn: String,
    val minLevel: Int,
    val colorHex: Long,
    val isVipOnly: Boolean = false
)

data class GamerProfile(
    val displayName: String = "CyberGhost",
    val gamerTag: String = "GHOST#7749",
    val bio: String = "Pro Battle Royale Player & GameVault Tester",
    val avatarId: String = "avatar_cyber_1",
    val customAvatarUri: String? = null,
    val level: Int = 14,
    val xp: Int = 3450,
    val maxXpForNextLevel: Int = 4000,
    val coins: Int = 1250,
    val rankTitle: String = "Apex Master",
    val selectedBorderId: String = "border_neon_cyan",
    val unlockedBorderIds: List<String> = listOf("border_default", "border_neon_cyan"),
    val isGoogleLinked: Boolean = true,
    val googleEmail: String = "gamer.player@gmail.com",
    val isVipMember: Boolean = false,
    val vipExpiryDate: String? = null
)

enum class ChallengeType {
    DAILY,
    WEEKLY,
    SPECIAL_EVENT
}

data class Challenge(
    val id: String,
    val titleAr: String,
    val titleEn: String,
    val descAr: String,
    val descEn: String,
    val type: ChallengeType,
    val rewardCoins: Int,
    val rewardXp: Int,
    val rewardCosmeticId: String? = null,
    val progress: Int,
    val maxProgress: Int,
    val isCompleted: Boolean = false,
    val isClaimed: Boolean = false,
    val isCustomAdminCreated: Boolean = false
)

data class SubscriptionPlan(
    val id: String,
    val titleAr: String,
    val titleEn: String,
    val priceText: String,
    val durationTextAr: String,
    val durationTextEn: String,
    val perksAr: List<String>,
    val perksEn: List<String>,
    val isPopular: Boolean = false,
    val isCurrent: Boolean = false
)

enum class NotificationCategory {
    DAILY_CHALLENGE,
    BOOST_REMINDER,
    SECURITY_ALERT,
    EVENT_BROADCAST
}

data class SmartNotification(
    val id: String,
    val titleAr: String,
    val titleEn: String,
    val messageAr: String,
    val messageEn: String,
    val category: NotificationCategory,
    val timeAgoAr: String,
    val timeAgoEn: String,
    val isRead: Boolean = false
)

data class BoosterTelemetry(
    val fps: Int = 60,
    val maxRefreshRate: Int = 120,
    val ramUsedMb: Long = 2840,
    val ramTotalMb: Long = 6144,
    val pingMs: Int = 32,
    val batteryTempC: Int = 36,
    val isBoostActive: Boolean = false,
    val gameMode: GameMode = GameMode.PERFORMANCE,
    val dndEnabled: Boolean = true,
    val lastCleanedMb: Long = 640
)

data class CloudSyncStatus(
    val isConnected: Boolean = true,
    val serverRegion: String = "Frankfurt / Riyadh Cloud Node",
    val encryptionType: String = "TLS 1.3 + AES-256 GCM",
    val antiFraudSignature: String = "SHA256:8f4c3b2a99e1...",
    val lastSyncTime: String = "منذ دقيقتين / 2 mins ago"
)
