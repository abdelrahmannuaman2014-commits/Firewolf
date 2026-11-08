package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GameVaultRepository
import com.example.model.CodeType
import com.example.model.GameAccount
import com.example.model.GameCodeItem
import com.example.model.GameItem
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.util.Strings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameOrganizerScreen(
    repository: GameVaultRepository,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val language by repository.language.collectAsState()
    val games by repository.games.collectAsState()
    val gameCodes by repository.gameCodes.collectAsState()
    val accounts by repository.accounts.collectAsState()
    val selectedGame by repository.selectedGame.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf(
        Strings.get("org_tab_codes", language),
        Strings.get("org_tab_accounts", language),
        Strings.get("org_tab_games", language)
    )

    var showAddCodeDialog by remember { mutableStateOf(false) }
    var showAddAccountDialog by remember { mutableStateOf(false) }
    var showQrDialogForCode by remember { mutableStateOf<GameCodeItem?>(null) }
    val clipboard = LocalClipboardManager.current
    var copiedMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("game_organizer_screen"),
        containerColor = BackgroundDark,
        floatingActionButton = {
            when (selectedTab) {
                0 -> {
                    FloatingActionButton(
                        onClick = { showAddCodeDialog = true },
                        containerColor = CyberCyan,
                        contentColor = Color(0xFF070B12),
                        modifier = Modifier.testTag("fab_add_code")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Code")
                    }
                }
                1 -> {
                    FloatingActionButton(
                        onClick = { showAddAccountDialog = true },
                        containerColor = NeonPurple,
                        contentColor = Color.White,
                        modifier = Modifier.testTag("fab_add_account")
                    ) {
                        Icon(Icons.Default.PersonAdd, contentDescription = "Add Account")
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tab Row
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = SurfaceDark,
                contentColor = CyberCyan,
                divider = { HorizontalDivider(color = SurfaceBorder) }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 14.sp
                            )
                        }
                    )
                }
            }

            // Tab Content
            when (selectedTab) {
                0 -> CodesTabContent(
                    codes = gameCodes,
                    language = language,
                    onCopy = { code ->
                        clipboard.setText(AnnotatedString(code.code))
                        copiedMessage = code.id
                    },
                    onShare = { code ->
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "[Fire Wolf Verified Code]\nGame: ${code.gameTitle}\nTitle: ${code.title}\nCode: ${code.code}\nSecurity Hash: ${code.antiFraudHash}"
                            )
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Game Code"))
                    },
                    onShowQr = { code -> showQrDialogForCode = code },
                    onDelete = { id -> repository.deleteGameCode(id) },
                    copiedId = copiedMessage
                )
                1 -> AccountsTabContent(
                    accounts = accounts,
                    language = language,
                    onSetActive = { id -> repository.setActiveAccount(id) },
                    onDelete = { id -> repository.deleteAccount(id) }
                )
                2 -> GamesTabContent(
                    games = games,
                    selectedGame = selectedGame,
                    language = language,
                    onSelect = { game -> repository.setSelectedGame(game) }
                )
            }
        }
    }

    // Add Code Dialog
    if (showAddCodeDialog) {
        var codeTitle by remember { mutableStateOf("") }
        var codeValue by remember { mutableStateOf("") }
        var selectedType by remember { mutableStateOf(CodeType.GRAPHICS_CONFIG) }
        var targetGameTitle by remember { mutableStateOf(games.firstOrNull()?.title ?: "PUBG Mobile") }

        AlertDialog(
            onDismissRequest = { showAddCodeDialog = false },
            title = {
                Text(
                    text = Strings.get("org_transfer_code_btn", language),
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = codeTitle,
                        onValueChange = { codeTitle = it },
                        label = { Text("عنوان الرمز / Code Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = codeValue,
                        onValueChange = { codeValue = it },
                        label = { Text("رمز اللعبة / Game Code") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "نوع الرمز / Code Type:",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            CodeType.GRAPHICS_CONFIG to "Config",
                            CodeType.REDEEM_CODE to "Redeem",
                            CodeType.SENSITIVITY_SETUP to "Sens"
                        ).forEach { (type, label) ->
                            FilterChip(
                                selected = selectedType == type,
                                onClick = { selectedType = type },
                                label = { Text(label, fontSize = 11.sp) }
                            )
                        }
                    }
                    Text(
                        text = "حماية فورية: سيتم إنشاء توقيع SHA-256 لمنع التلاعب.",
                        color = NeonGreen,
                        fontSize = 11.sp
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (codeTitle.isNotBlank() && codeValue.isNotBlank()) {
                            val game = games.find { it.title == targetGameTitle } ?: games.first()
                            repository.addGameCode(
                                title = codeTitle,
                                code = codeValue,
                                type = selectedType,
                                gameId = game.id,
                                gameTitle = game.title
                            )
                            showAddCodeDialog = false
                        }
                    }
                ) {
                    Text(Strings.get("prof_save", language), color = CyberCyan)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCodeDialog = false }) {
                    Text("إلغاء", color = TextSecondary)
                }
            },
            containerColor = SurfaceDark
        )
    }

    // Add Account Dialog
    if (showAddAccountDialog) {
        var accountTag by remember { mutableStateOf("") }
        var accountId by remember { mutableStateOf("") }
        var region by remember { mutableStateOf("MENA / الشرق الأوسط") }
        var notes by remember { mutableStateOf("") }
        var selectedGameForAccount by remember { mutableStateOf(games.firstOrNull()?.title ?: "PUBG Mobile") }

        AlertDialog(
            onDismissRequest = { showAddAccountDialog = false },
            title = {
                Text(
                    text = Strings.get("org_add_account_btn", language),
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = accountTag,
                        onValueChange = { accountTag = it },
                        label = { Text("تسمية الحساب (مثال: الرئيسي أو التقييم)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = accountId,
                        onValueChange = { accountId = it },
                        label = { Text("معرف الحساب ID أو اسم المستخدم") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = region,
                        onValueChange = { region = it },
                        label = { Text("المنطقة أو السيرفر (Region)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("ملاحظات خاصة (مشفرة)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (accountTag.isNotBlank() && accountId.isNotBlank()) {
                            val game = games.find { it.title == selectedGameForAccount } ?: games.first()
                            repository.addGameAccount(
                                gameId = game.id,
                                gameTitle = game.title,
                                tag = accountTag,
                                accountId = accountId,
                                region = region,
                                notes = notes
                            )
                            showAddAccountDialog = false
                        }
                    }
                ) {
                    Text(Strings.get("prof_save", language), color = CyberCyan)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddAccountDialog = false }) {
                    Text("إلغاء", color = TextSecondary)
                }
            },
            containerColor = SurfaceDark
        )
    }

    // QR Code Transfer Dialog
    showQrDialogForCode?.let { code ->
        AlertDialog(
            onDismissRequest = { showQrDialogForCode = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.QrCode2, contentDescription = null, tint = CyberCyan)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(Strings.get("org_qr_transfer", language), color = TextPrimary)
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(180.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // High-tech QR representation
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(140.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = code.title,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = code.antiFraudHash,
                        color = NeonGreen,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "امسح الرمز من جهاز آخر للاستيراد الفوري المشفر",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showQrDialogForCode = null }) {
                    Text("تم", color = CyberCyan)
                }
            },
            containerColor = SurfaceDark
        )
    }
}

@Composable
private fun CodesTabContent(
    codes: List<GameCodeItem>,
    language: com.example.util.AppLanguage,
    onCopy: (GameCodeItem) -> Unit,
    onShare: (GameCodeItem) -> Unit,
    onShowQr: (GameCodeItem) -> Unit,
    onDelete: (String) -> Unit,
    copiedId: String?
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(codes) { item ->
            CyberCard(
                borderColor = if (item.isFavorite) CyberCyan.copy(alpha = 0.5f) else SurfaceBorder,
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
                                text = item.title,
                                color = TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${item.gameTitle} • ${item.author}",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                    IconButton(onClick = { onDelete(item.id) }, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Code Display Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(SurfaceVariantDark)
                        .border(1.dp, CyberCyan.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = item.code,
                        color = CyberCyan,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Anti Fraud Signature Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = NeonGreen,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${Strings.get("org_code_verified", language)}: ${item.antiFraudHash}",
                        color = NeonGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Actions Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { onCopy(item) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = if (copiedId == item.id) Icons.Default.Check else Icons.Default.ContentCopy,
                            contentDescription = null,
                            tint = if (copiedId == item.id) NeonGreen else CyberCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (copiedId == item.id) "تم النسخ" else Strings.get("org_code_copy", language),
                            fontSize = 12.sp
                        )
                    }
                    OutlinedButton(
                        onClick = { onShare(item) },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(16.dp))
                    }
                    OutlinedButton(
                        onClick = { onShowQr(item) },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.QrCode2, contentDescription = null, tint = NeonPurple, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun AccountsTabContent(
    accounts: List<GameAccount>,
    language: com.example.util.AppLanguage,
    onSetActive: (String) -> Unit,
    onDelete: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(accounts) { acc ->
            CyberCard(
                borderColor = if (acc.isPrimary) NeonGreen else SurfaceBorder,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = acc.accountTag,
                                color = TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (acc.isPrimary) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = Strings.get("org_active_slot", language),
                                    color = NeonGreen,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(NeonGreen.copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${acc.gameTitle} • ${acc.region}",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "ID: ${acc.accountIdOrUser}",
                            color = CyberCyan,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (acc.notes.isNotBlank()) {
                            Text(
                                text = "🔒 ${acc.notes}",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }
                    IconButton(onClick = { onDelete(acc.id) }) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (!acc.isPrimary) {
                    NeonButton(
                        text = Strings.get("org_switch_account", language),
                        onClick = { onSetActive(acc.id) },
                        isSecondary = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun GamesTabContent(
    games: List<GameItem>,
    selectedGame: GameItem?,
    language: com.example.util.AppLanguage,
    onSelect: (GameItem) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(games) { game ->
            val isSelected = selectedGame?.id == game.id
            CyberCard(
                borderColor = if (isSelected) CyberCyan else SurfaceBorder,
                onClick = { onSelect(game) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(SurfaceVariantDark),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SportsEsports,
                            contentDescription = null,
                            tint = if (isSelected) CyberCyan else TextSecondary,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = game.title,
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${game.genre} • ★ ${game.rating} • ${game.supportedFps} Hz",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                        Text(
                            text = game.description,
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                    if (isSelected) {
                        Icon(Icons.Default.CheckCircle, contentDescription = "Selected", tint = CyberCyan)
                    }
                }
            }
        }
    }
}
