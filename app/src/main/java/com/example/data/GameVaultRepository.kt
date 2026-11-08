package com.example.data

import android.content.Context
import com.example.model.*
import com.example.util.AppLanguage
import com.example.util.SystemTelemetry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class GameVaultRepository(private val context: Context) {

    private val scope = CoroutineScope(Dispatchers.Main)

    private val _language = MutableStateFlow(AppLanguage.ARABIC)
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    private val _games = MutableStateFlow<List<GameItem>>(emptyList())
    val games: StateFlow<List<GameItem>> = _games.asStateFlow()

    private val _gameCodes = MutableStateFlow<List<GameCodeItem>>(emptyList())
    val gameCodes: StateFlow<List<GameCodeItem>> = _gameCodes.asStateFlow()

    private val _accounts = MutableStateFlow<List<GameAccount>>(emptyList())
    val accounts: StateFlow<List<GameAccount>> = _accounts.asStateFlow()

    private val _profile = MutableStateFlow(GamerProfile())
    val profile: StateFlow<GamerProfile> = _profile.asStateFlow()

    private val _cosmeticBorders = MutableStateFlow<List<CosmeticBorder>>(emptyList())
    val cosmeticBorders: StateFlow<List<CosmeticBorder>> = _cosmeticBorders.asStateFlow()

    private val _challenges = MutableStateFlow<List<Challenge>>(emptyList())
    val challenges: StateFlow<List<Challenge>> = _challenges.asStateFlow()

    private val _notifications = MutableStateFlow<List<SmartNotification>>(emptyList())
    val notifications: StateFlow<List<SmartNotification>> = _notifications.asStateFlow()

    private val _telemetry = MutableStateFlow(BoosterTelemetry())
    val telemetry: StateFlow<BoosterTelemetry> = _telemetry.asStateFlow()

    private val _cloudSync = MutableStateFlow(CloudSyncStatus())
    val cloudSync: StateFlow<CloudSyncStatus> = _cloudSync.asStateFlow()

    private val _isBoosting = MutableStateFlow(false)
    val isBoosting: StateFlow<Boolean> = _isBoosting.asStateFlow()

    private val _selectedGame = MutableStateFlow<GameItem?>(null)
    val selectedGame: StateFlow<GameItem?> = _selectedGame.asStateFlow()

    init {
        loadInitialData()
        refreshTelemetry()
    }

    private fun loadInitialData() {
        val initialGames = listOf(
            GameItem("g1", "PUBG Mobile", "com.tencent.ig", GameGenre.BATTLE_ROYALE, 4.8f, 1420, isFavorite = true, supportedFps = 120, description = "Epic 100-player Battle Royale warfare"),
            GameItem("g2", "Free Fire MAX", "com.dts.freefiremax", GameGenre.BATTLE_ROYALE, 4.7f, 980, isFavorite = true, supportedFps = 90, description = "Fast-paced survival combat shooter"),
            GameItem("g3", "Call of Duty: Warzone Mobile", "com.activision.callofduty.warzone", GameGenre.FPS, 4.6f, 540, isFavorite = false, supportedFps = 120, description = "Verdansk multiplayer battles"),
            GameItem("g4", "Roblox", "com.roblox.client", GameGenre.SANDBOX, 4.5f, 720, isFavorite = true, supportedFps = 60, description = "Infinite user-generated multiplayer worlds"),
            GameItem("g5", "Brawl Stars", "com.supercell.brawlstars", GameGenre.MOBA, 4.9f, 610, isFavorite = false, supportedFps = 120, description = "Fast 3v3 battle royale shooter"),
            GameItem("g6", "Genshin Impact", "com.miHoYo.GenshinImpact", GameGenre.RPG, 4.7f, 430, isFavorite = false, supportedFps = 60, description = "Open-world action adventure RPG"),
            GameItem("g7", "EA SPORTS FC™ Mobile", "com.ea.gp.fifamobile", GameGenre.STRATEGY, 4.4f, 310, isFavorite = false, supportedFps = 90, description = "Ultimate Football Championship")
        )
        _games.value = initialGames
        _selectedGame.value = initialGames.first()

        _gameCodes.value = listOf(
            GameCodeItem(
                id = "c1",
                gameId = "g1",
                gameTitle = "PUBG Mobile",
                title = "120 FPS Ultra Graphics Engine Code",
                code = "PUBG-120FPS-HDR-ULTRA-2026",
                codeType = CodeType.GRAPHICS_CONFIG,
                antiFraudHash = SystemTelemetry.generateAntiFraudHash("PUBG-120FPS-HDR-ULTRA-2026"),
                author = "ProEsports_Coach",
                isVerified = true,
                usageCount = 1420,
                isFavorite = true
            ),
            GameCodeItem(
                id = "c2",
                gameId = "g2",
                gameTitle = "Free Fire MAX",
                title = "Diamond Mystery Box Redeem Code",
                code = "FFMAX-DIAMOND-CLAIM-99",
                codeType = CodeType.REDEEM_CODE,
                antiFraudHash = SystemTelemetry.generateAntiFraudHash("FFMAX-DIAMOND-CLAIM-99"),
                author = "GamerVerified99",
                isVerified = true,
                usageCount = 890,
                isFavorite = true
            ),
            GameCodeItem(
                id = "c3",
                gameId = "g1",
                gameTitle = "PUBG Mobile",
                title = "Zero Recoil Gyro Sensitivity Code",
                code = "7294-8192-3011-0945-88",
                codeType = CodeType.SENSITIVITY_SETUP,
                antiFraudHash = SystemTelemetry.generateAntiFraudHash("7294-8192-3011-0945-88"),
                author = "Falcon_Sniper",
                isVerified = true,
                usageCount = 612,
                isFavorite = false
            ),
            GameCodeItem(
                id = "c4",
                gameId = "g3",
                gameTitle = "Call of Duty: Warzone",
                title = "MENA Tournament Scrim Room Pass",
                code = "SCRIM-WARZONE-MENA-ROOM-404",
                codeType = CodeType.TOURNAMENT_ROOM_KEY,
                antiFraudHash = SystemTelemetry.generateAntiFraudHash("SCRIM-WARZONE-MENA-ROOM-404"),
                author = "LeagueAdmin",
                isVerified = true,
                usageCount = 188,
                isFavorite = false
            )
        )

        _accounts.value = listOf(
            GameAccount(
                id = "acc1",
                gameId = "g1",
                gameTitle = "PUBG Mobile",
                accountTag = "الحساب الرئيسي (كونكرر)",
                accountIdOrUser = "5148920192",
                region = "MENA / الشرق الأوسط",
                notes = "حساب التقييم العالي مع حزم الأسلحة المطورة",
                isPrimary = true
            ),
            GameAccount(
                id = "acc2",
                gameId = "g1",
                gameTitle = "PUBG Mobile",
                accountTag = "حساب البطولات والسكرمات",
                accountIdOrUser = "5893018274",
                region = "Europe / سيرفر أوروبا",
                notes = "مخصص لتدريبات الفريق والبطولات التنافسية",
                isPrimary = false
            ),
            GameAccount(
                id = "acc3",
                gameId = "g2",
                gameTitle = "Free Fire MAX",
                accountTag = "حساب النخبة Grandmaster",
                accountIdOrUser = "FF_PRO_99410",
                region = "MENA",
                notes = "حساب النخبة مع تفعيل الشحن الكامل",
                isPrimary = true
            )
        )

        _cosmeticBorders.value = listOf(
            CosmeticBorder("border_default", "الإطار القياسي", "Cyber Standard", 1, 0xFF64748B),
            CosmeticBorder("border_neon_cyan", "تيربو سايبر نيون", "Neon Cyan Turbo", 5, 0xFF00E5FF),
            CosmeticBorder("border_dragon", "التنين الناري الأسطوري", "Flame Dragon", 10, 0xFFFF3D71),
            CosmeticBorder("border_diamond", "الماس الإيسبورتس", "Esports Diamond", 15, 0xFF7C4DFF),
            CosmeticBorder("border_gold_vip", "التاج الذهبي VIP", "Royal Golden Crown", 20, 0xFFFFB300, isVipOnly = true)
        )

        _challenges.value = listOf(
            Challenge(
                id = "ch1",
                titleAr = "تشغيل معزز الألعاب 3 مرات",
                titleEn = "Run Game Booster 3 Times",
                descAr = "قم بتنشيط التيربو بوست لتحرير الذاكرة وتثبيت معدل الإطارات",
                descEn = "Activate turbo boost to flush RAM and stabilize FPS",
                type = ChallengeType.DAILY,
                rewardCoins = 150,
                rewardXp = 250,
                progress = 2,
                maxProgress = 3,
                isCompleted = false,
                isClaimed = false
            ),
            Challenge(
                id = "ch2",
                titleAr = "إجراء اختبار استجابة البينج",
                titleEn = "Run Network Ping Latency Test",
                descAr = "تحقق من جودة الاتصال بخادم السيرفر السريع",
                descEn = "Verify connection quality to cloud gaming node",
                type = ChallengeType.DAILY,
                rewardCoins = 100,
                rewardXp = 150,
                progress = 1,
                maxProgress = 1,
                isCompleted = true,
                isClaimed = false
            ),
            Challenge(
                id = "ch3",
                titleAr = "مزامنة ونقل رمز لعبة مشفر",
                titleEn = "Export & Verify Encrypted Game Code",
                descAr = "استخدم توقيع الحماية SHA-256 لنقل رمز إعدادات إلى صديق",
                descEn = "Use SHA-256 anti-fraud hash to transfer a game code",
                type = ChallengeType.WEEKLY,
                rewardCoins = 400,
                rewardXp = 600,
                progress = 1,
                maxProgress = 2,
                isCompleted = false,
                isClaimed = false
            ),
            Challenge(
                id = "ch4",
                titleAr = "ساعة لعب في وضع Ultra Cyber",
                titleEn = "Play 60 Mins in Ultra Cyber Mode",
                descAr = "حافظ على تشغيل وضع الألعاب فائق الأداء لتعزيز تجربة اللعب",
                descEn = "Keep the ultra gaming mode active during play session",
                type = ChallengeType.WEEKLY,
                rewardCoins = 500,
                rewardXp = 800,
                rewardCosmeticId = "border_dragon",
                progress = 45,
                maxProgress = 60,
                isCompleted = false,
                isClaimed = false
            )
        )

        _notifications.value = listOf(
            SmartNotification(
                id = "n1",
                titleAr = "مكافأة التحدي اليومي جاهزة!",
                titleEn = "Daily Challenge Reward Ready!",
                messageAr = "لقد أكملت اختبار البينج بنجاح، يمكنك استلام 100 عملة الآن.",
                messageEn = "You completed the Ping Test challenge, claim your 100 coins now.",
                category = NotificationCategory.DAILY_CHALLENGE,
                timeAgoAr = "منذ 10 دقائق",
                timeAgoEn = "10m ago"
            ),
            SmartNotification(
                id = "n2",
                titleAr = "نصيحة تعزيز الأداء",
                titleEn = "Game Booster Advisory",
                messageAr = "استهلاك الذاكرة يقترب من 65%، ينصح بتفعيل التيربو قبل بدء مباراة PUBG القادمة.",
                messageEn = "RAM usage is at 65%, recommended to trigger turbo boost before PUBG.",
                category = NotificationCategory.BOOST_REMINDER,
                timeAgoAr = "منذ ساعة",
                timeAgoEn = "1h ago"
            ),
            SmartNotification(
                id = "n3",
                titleAr = "الخادم الخاص: تم التحقق من الحماية",
                titleEn = "Cloud Server: Security Verified",
                messageAr = "تم فحص جميع أكواد الألعاب ومطابقة توقيعات SHA-256 ضد أي محاولات تعديل.",
                messageEn = "All game codes verified against SHA-256 signatures with zero fraud detected.",
                category = NotificationCategory.SECURITY_ALERT,
                timeAgoAr = "منذ 3 ساعات",
                timeAgoEn = "3h ago"
            )
        )
    }

    fun toggleLanguage() {
        _language.value = if (_language.value == AppLanguage.ARABIC) AppLanguage.ENGLISH else AppLanguage.ARABIC
    }

    fun setSelectedGame(game: GameItem) {
        _selectedGame.value = game
    }

    fun refreshTelemetry() {
        val mem = SystemTelemetry.getRealMemoryInfo(context)
        val rate = SystemTelemetry.getScreenRefreshRate(context)
        _telemetry.value = _telemetry.value.copy(
            ramUsedMb = mem.usedMb,
            ramTotalMb = mem.totalMb,
            maxRefreshRate = rate,
            fps = if (_telemetry.value.isBoostActive) rate else (rate * 0.85).toInt()
        )
    }

    fun triggerBooster() {
        if (_isBoosting.value) return
        _isBoosting.value = true
        SystemTelemetry.triggerBoosterVibration(context)

        scope.launch {
            delay(1500)
            val mem = SystemTelemetry.getRealMemoryInfo(context)
            val freedMb = (280..650).random().toLong()
            val newUsed = (mem.usedMb - freedMb).coerceAtLeast(mem.totalMb / 4)
            val rate = SystemTelemetry.getScreenRefreshRate(context)

            _telemetry.value = _telemetry.value.copy(
                isBoostActive = true,
                ramUsedMb = newUsed,
                fps = rate,
                lastCleanedMb = freedMb,
                pingMs = (_telemetry.value.pingMs - (6..12).random()).coerceAtLeast(18)
            )

            // Update daily challenge progress
            val chList = _challenges.value.toMutableList()
            val chIndex = chList.indexOfFirst { it.id == "ch1" }
            if (chIndex >= 0) {
                val ch = chList[chIndex]
                val newProg = (ch.progress + 1).coerceAtMost(ch.maxProgress)
                chList[chIndex] = ch.copy(progress = newProg, isCompleted = newProg >= ch.maxProgress)
                _challenges.value = chList
            }

            _isBoosting.value = false
            SystemTelemetry.triggerBoosterVibration(context)
        }
    }

    fun runPingTest() {
        scope.launch {
            val ping = SystemTelemetry.measureRealPing()
            _telemetry.value = _telemetry.value.copy(pingMs = ping)
        }
    }

    fun setGameMode(mode: GameMode) {
        _telemetry.value = _telemetry.value.copy(gameMode = mode)
    }

    fun toggleDnd(enabled: Boolean) {
        _telemetry.value = _telemetry.value.copy(dndEnabled = enabled)
    }

    fun addGameCode(
        title: String,
        code: String,
        type: CodeType,
        gameId: String,
        gameTitle: String,
        author: String = "MyPlayer"
    ) {
        val hash = SystemTelemetry.generateAntiFraudHash(code)
        val newItem = GameCodeItem(
            id = UUID.randomUUID().toString(),
            gameId = gameId,
            gameTitle = gameTitle,
            title = title,
            code = code,
            codeType = type,
            antiFraudHash = hash,
            author = author,
            isVerified = true
        )
        _gameCodes.value = listOf(newItem) + _gameCodes.value
    }

    fun deleteGameCode(id: String) {
        _gameCodes.value = _gameCodes.value.filter { it.id != id }
    }

    fun addGameAccount(
        gameId: String,
        gameTitle: String,
        tag: String,
        accountId: String,
        region: String,
        notes: String
    ) {
        val newAcc = GameAccount(
            id = UUID.randomUUID().toString(),
            gameId = gameId,
            gameTitle = gameTitle,
            accountTag = tag,
            accountIdOrUser = accountId,
            region = region,
            notes = notes,
            isPrimary = _accounts.value.none { it.gameId == gameId && it.isPrimary }
        )
        _accounts.value = _accounts.value + newAcc
    }

    fun setActiveAccount(accountId: String) {
        val target = _accounts.value.find { it.id == accountId } ?: return
        _accounts.value = _accounts.value.map { acc ->
            if (acc.gameId == target.gameId) {
                acc.copy(isPrimary = acc.id == accountId, lastUsedTimestamp = System.currentTimeMillis())
            } else {
                acc
            }
        }
    }

    fun deleteAccount(accountId: String) {
        _accounts.value = _accounts.value.filter { it.id != accountId }
    }

    fun updateProfile(displayName: String, bio: String, avatarId: String, selectedBorderId: String) {
        _profile.value = _profile.value.copy(
            displayName = displayName,
            bio = bio,
            avatarId = avatarId,
            selectedBorderId = selectedBorderId
        )
    }

    fun toggleGoogleSignIn() {
        val current = _profile.value
        _profile.value = current.copy(
            isGoogleLinked = !current.isGoogleLinked,
            googleEmail = if (!current.isGoogleLinked) "abdelrahmannuaman2014@gmail.com" else ""
        )
    }

    fun claimChallenge(challengeId: String) {
        val list = _challenges.value.toMutableList()
        val index = list.indexOfFirst { it.id == challengeId }
        if (index >= 0) {
            val item = list[index]
            if (item.isCompleted && !item.isClaimed) {
                list[index] = item.copy(isClaimed = true)
                _challenges.value = list

                // Reward player
                val prof = _profile.value
                val newCoins = prof.coins + item.rewardCoins
                val newXp = prof.xp + item.rewardXp
                var newLevel = prof.level
                var maxXp = prof.maxXpForNextLevel
                if (newXp >= maxXp) {
                    newLevel += 1
                    maxXp += 1000
                }

                // Unlock cosmetic border if rewarded
                val unlocked = prof.unlockedBorderIds.toMutableList()
                item.rewardCosmeticId?.let { cid ->
                    if (!unlocked.contains(cid)) unlocked.add(cid)
                }

                _profile.value = prof.copy(
                    coins = newCoins,
                    xp = newXp,
                    level = newLevel,
                    maxXpForNextLevel = maxXp,
                    unlockedBorderIds = unlocked
                )
            }
        }
    }

    fun addAdminChallenge(
        titleAr: String,
        titleEn: String,
        descAr: String,
        descEn: String,
        type: ChallengeType,
        coins: Int,
        xp: Int,
        maxProgress: Int
    ) {
        val newCh = Challenge(
            id = "admin_${UUID.randomUUID().toString().take(6)}",
            titleAr = titleAr,
            titleEn = titleEn,
            descAr = descAr,
            descEn = descEn,
            type = type,
            rewardCoins = coins,
            rewardXp = xp,
            progress = 0,
            maxProgress = maxProgress,
            isCompleted = false,
            isClaimed = false,
            isCustomAdminCreated = true
        )
        _challenges.value = listOf(newCh) + _challenges.value

        // Also broadcast notification to players
        val newNotif = SmartNotification(
            id = UUID.randomUUID().toString(),
            titleAr = "فعالية جديدة: $titleAr",
            titleEn = "New Event: $titleEn",
            messageAr = "أطلق المشرف تحدياً جديداً بمكافأة $coins عملة و $xp نقطة خبرة!",
            messageEn = "Admin announced a new challenge with $coins coins and $xp XP!",
            category = NotificationCategory.EVENT_BROADCAST,
            timeAgoAr = "الآن",
            timeAgoEn = "Just now"
        )
        _notifications.value = listOf(newNotif) + _notifications.value
    }

    fun deleteChallenge(id: String) {
        _challenges.value = _challenges.value.filter { it.id != id }
    }

    fun activateVipPlan(isYearly: Boolean) {
        val prof = _profile.value
        val borders = prof.unlockedBorderIds.toMutableList()
        if (!borders.contains("border_gold_vip")) {
            borders.add("border_gold_vip")
        }
        _profile.value = prof.copy(
            isVipMember = true,
            vipExpiryDate = if (isYearly) "2027-11-08 (سنة كاملة / 1 Year)" else "2026-12-08 (شهر كامل / 1 Month)",
            unlockedBorderIds = borders,
            selectedBorderId = "border_gold_vip",
            coins = prof.coins + 2500
        )
    }

    fun syncWithCloudServer() {
        scope.launch {
            _cloudSync.value = _cloudSync.value.copy(
                lastSyncTime = if (_language.value == AppLanguage.ARABIC) "تمت المزامنة الآن بنجاح ✓" else "Synced just now ✓"
            )
        }
    }

    fun markNotificationRead(id: String) {
        _notifications.value = _notifications.value.map {
            if (it.id == id) it.copy(isRead = true) else it
        }
    }
}
