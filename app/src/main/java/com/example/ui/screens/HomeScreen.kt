package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.LanguageManager
import com.example.model.ShowcaseItem
import com.example.ui.viewmodel.AppCreatorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: AppCreatorViewModel,
    onNavigateToBuilder: (type: String, initialPrompt: String?) -> Unit,
    onNavigateToSandbox: () -> Unit,
    onNavigateToProjects: () -> Unit,
    onNavigateToStore: () -> Unit,
    onNavigateToDeveloperContact: () -> Unit,
    onOpenSubscriptionModal: () -> Unit,
    onOpenSettings: () -> Unit,
    onNavigateToTutorial: () -> Unit = {},
    onNavigateToCounters: () -> Unit = {}
) {
    val haptic = LocalHapticFeedback.current
    val quotaState by viewModel.quotaState.collectAsState()
    val langCode = quotaState.currentLanguage

    val servers by viewModel.servers.collectAsState()
    val selectedServerId by viewModel.selectedServerId.collectAsState()
    val isTurboEnabled by viewModel.isTurboEnabled.collectAsState()
    val activeServer = servers.find { it.id == selectedServerId } ?: servers.first()

    var quickPromptText by remember { mutableStateOf("") }
    val selectedType = "App" // Exclusively applications
    var showLanguagePicker by remember { mutableStateOf(false) }
    var showServerDialog by remember { mutableStateOf(false) }

    val currentLang = LanguageManager.supportedLanguages.find { it.code == langCode }
        ?: LanguageManager.supportedLanguages.first()

    if (showLanguagePicker) {
        LanguageSelectorDialog(
            viewModel = viewModel,
            onDismiss = { showLanguagePicker = false }
        )
    }

    if (showServerDialog) {
        GlobalServersDialog(
            viewModel = viewModel,
            onDismiss = { showServerDialog = false }
        )
    }

    val quickChips = listOf(
        "🛒 متجر إلكتروني وسلة تسوق Pro" to "App",
        "📋 تطبيق مهام وعادات ذكية" to "App",
        "💰 محفظة مصروفات واستثمار AI" to "App",
        "💧 متتبع لياقة وصحة شرب الماء" to "App",
        "🩺 حجز عيادات واستشارات طبية" to "App",
        "🤖 مساعد ذكاء اصطناعي ودردشة" to "App"
    )

    val engines = listOf(
        Triple("STOREFRONT", "🛒 متجر وسلة تسوق", Color(0xFF06B6D4)),
        Triple("TODO_PRO", "📋 مهام Pro", Color(0xFF6366F1)),
        Triple("FITNESS_FLOW", "💧 لياقة وصحة", Color(0xFF14B8A6)),
        Triple("EXPENSE_TRACKER", "💰 محفظة ذكية", Color(0xFF10B981)),
        Triple("AI_ASSISTANT", "🤖 مساعد ذكي", Color(0xFFEC4899)),
        Triple("DOCTOR_BOOKING", "🩺 عيادات وطب", Color(0xFFF59E0B))
    )

    Scaffold(
        containerColor = Color(0xFF0F172A),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(
                                    Brush.linearGradient(listOf(Color(0xFF6366F1), Color(0xFF10B981))),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = LanguageManager.getString(langCode, "app_name"),
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp,
                                maxLines = 1
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(Color(0xFF10B981), CircleShape)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (quotaState.isSubscribed) "اشتراك غير محدود ⚡" else "تجربة مجانية: ${quotaState.remainingFree} متبقية",
                                    color = if (quotaState.isSubscribed) Color(0xFF10B981) else Color(0xFFF59E0B),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                },
                actions = {
                    // Global Server Hub Chip
                    Surface(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            showServerDialog = true
                        },
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF1E293B),
                        border = BorderStroke(1.dp, Color(0xFF06B6D4).copy(alpha = 0.6f)),
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(activeServer.flag, fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("${activeServer.pingMs}ms", color = Color(0xFF06B6D4), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            if (isTurboEnabled) {
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("⚡", fontSize = 10.sp)
                            }
                        }
                    }

                    // 5$ Multi-Platform Publish Button
                    Button(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            viewModel.openPublishDialog()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.padding(end = 4.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.CloudUpload, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("نشر (5$) 🚀", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                    }

                    // Tutorial / Education Screen Button
                    IconButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onNavigateToTutorial()
                        },
                        modifier = Modifier.minimumInteractiveComponentSize()
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = "دليل إنشاء التطبيق بالكامل",
                            tint = Color(0xFF38BDF8)
                        )
                    }

                    // Complete App Counters Dashboard Button
                    IconButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onNavigateToCounters()
                        },
                        modifier = Modifier.minimumInteractiveComponentSize()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Analytics,
                            contentDescription = "عدادات التطبيق الشاملة",
                            tint = Color(0xFF10B981)
                        )
                    }

                    // Language Switcher in Top Bar
                    IconButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            showLanguagePicker = true
                        },
                        modifier = Modifier.minimumInteractiveComponentSize()
                    ) {
                        Text(currentLang.flag, fontSize = 20.sp)
                    }

                    // VIP Subscription button
                    IconButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onOpenSubscriptionModal()
                        },
                        modifier = Modifier.minimumInteractiveComponentSize()
                    ) {
                        Icon(
                            imageVector = Icons.Default.WorkspacePremium,
                            contentDescription = "الاشتراكات",
                            tint = Color(0xFFF59E0B)
                        )
                    }

                    // Settings
                    IconButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onOpenSettings()
                        },
                        modifier = Modifier.minimumInteractiveComponentSize()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "الإعدادات",
                            tint = Color(0xFFCBD5E1)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Futuristic Global Servers Hero Banner (Audience Magnet & Real-time Node)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .border(
                            1.5.dp,
                            Brush.horizontalGradient(
                                listOf(Color(0xFF06B6D4), Color(0xFF6366F1), Color(0xFF10B981))
                            ),
                            RoundedCornerShape(20.dp)
                        ),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Box(modifier = Modifier.fillMaxWidth().height(180.dp)) {
                        Image(
                            painter = painterResource(id = R.drawable.img_global_servers_1790541070157),
                            contentDescription = "سيرفرات الذكاء الاصطناعي العالمية",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        // Dark gradient scrim overlay for readability
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color(0xFF0F172A).copy(alpha = 0.35f),
                                            Color(0xFF0F172A).copy(alpha = 0.70f),
                                            Color(0xFF0F172A).copy(alpha = 0.96f)
                                        )
                                    )
                                )
                        )

                        // Content inside the hero card
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Top Row: Active Node Chip & Turbo Mode Chip
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        showServerDialog = true
                                    },
                                    shape = RoundedCornerShape(20.dp),
                                    color = Color(0xFF0F172A).copy(alpha = 0.85f),
                                    border = BorderStroke(1.dp, Color(0xFF06B6D4))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(7.dp)
                                                .background(Color(0xFF10B981), CircleShape)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "${activeServer.flag} ${activeServer.nameAr} (${activeServer.pingMs}ms)",
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            Icons.Default.Tune,
                                            contentDescription = null,
                                            tint = Color(0xFF06B6D4),
                                            modifier = Modifier.size(13.dp)
                                        )
                                    }
                                }

                                Surface(
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        viewModel.toggleTurbo()
                                    },
                                    shape = RoundedCornerShape(20.dp),
                                    color = if (isTurboEnabled) Color(0xFF06B6D4).copy(alpha = 0.85f) else Color(0xFF1E293B).copy(alpha = 0.85f),
                                    border = BorderStroke(1.dp, if (isTurboEnabled) Color(0xFF38BDF8) else Color(0xFF64748B))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.Bolt,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = if (isTurboEnabled) "TURBO ⚡" else "ECO",
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    }
                                }
                            }

                            // Bottom Hero Title & Specs
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "شبكة السيرفرات السحابية الأقوى عالمياً",
                                        color = Color(0xFF38BDF8),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color(0xFF10B981).copy(alpha = 0.25f))
                                            .padding(horizontal = 5.dp, vertical = 2.dp)
                                    ) {
                                        Text("100 Gbps", color = Color(0xFF10B981), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "حوسبة كمومية ومعالجات NVIDIA Blackwell فائقة السرعة تدعم بناء أضخم التطبيقات والألعاب.",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    lineHeight = 17.sp,
                                    maxLines = 2
                                )
                            }
                        }
                    }
                }
            }

            // Live Audience Magnet Ticker (مباشر • نبض المطورين والجمهور الحي)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    border = BorderStroke(1.dp, Color(0xFF334155))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(Color(0xFF10B981), CircleShape)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "نبض المطورين والجمهور الحي 🌍",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = "مباشر الآن 🟢",
                                color = Color(0xFF10B981),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            StatPill("14,892", "متصل الآن 🌐", Color(0xFF06B6D4))
                            StatPill("128.4k", "تطبيق اليوم 🚀", Color(0xFF10B981))
                            StatPill("${activeServer.pingMs} ms", "زمن الاستجابة ⚡", Color(0xFFF59E0B))
                            StatPill("99.99%", "استقرار العقد 🛡️", Color(0xFFA855F7))
                        }
                    }
                }
            }

            // Quota and Free Trial Counter Card (3 Free Apps / $5 Unlimited / $50 Dev)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    border = BorderStroke(
                        1.dp,
                        if (quotaState.isSubscribed) Color(0xFF10B981).copy(alpha = 0.6f) else Color(0xFF6366F1).copy(alpha = 0.6f)
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Timer,
                                    contentDescription = null,
                                    tint = if (quotaState.isSubscribed) Color(0xFF10B981) else Color(0xFF818CF8),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = LanguageManager.getString(langCode, "quota_title"),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }

                            Text(
                                text = if (quotaState.isSubscribed)
                                    LanguageManager.getString(langCode, "quota_unlimited")
                                else
                                    "${quotaState.appsGeneratedCount} / 3 مجاناً",
                                color = if (quotaState.isSubscribed) Color(0xFF10B981) else if (quotaState.appsGeneratedCount >= 3) Color(0xFFEF4444) else Color(0xFF818CF8),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        val progress = if (quotaState.isSubscribed) 1f else (quotaState.appsGeneratedCount / 3f).coerceIn(0f, 1f)
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = if (quotaState.isSubscribed) Color(0xFF10B981) else if (quotaState.appsGeneratedCount >= 3) Color(0xFFEF4444) else Color(0xFF6366F1),
                            trackColor = Color(0xFF0F172A)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Fast Plan Switch Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onOpenSubscriptionModal()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1.1f)
                                    .heightIn(min = 40.dp)
                            ) {
                                Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("اشتراك غير محدود (5$) ⚡", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onNavigateToDeveloperContact()
                                },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFF59E0B)),
                                border = BorderStroke(1.dp, Color(0xFFF59E0B)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(0.9f)
                                    .heightIn(min = 40.dp)
                            ) {
                                Icon(Icons.Default.Engineering, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("مبرمج حقيقي (50$)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Full App Counters & Telemetry Dashboard Card (عدادات كاملة للتطبيق)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onNavigateToCounters()
                        }
                        .border(
                            1.5.dp,
                            Brush.horizontalGradient(listOf(Color(0xFF10B981), Color(0xFF06B6D4), Color(0xFF6366F1))),
                            RoundedCornerShape(16.dp)
                        ),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFF10B981).copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Analytics,
                                        contentDescription = null,
                                        tint = Color(0xFF10B981),
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "عدادات وإحصائيات التطبيق الشاملة 📊",
                                        color = Color.White,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = "مؤشرات حية للأداء، الأكواد، السيرفرات، والمنصات",
                                        color = Color(0xFF94A3B8),
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFF10B981).copy(alpha = 0.2f))
                                    .padding(horizontal = 7.dp, vertical = 3.dp)
                            ) {
                                Text("مباشر 🟢", color = Color(0xFF10B981), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Quick 4 Counters Preview Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Card(
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("${quotaState.totalAppsCreated}", color = Color(0xFF6366F1), fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                                    Text("تطبيقات", color = Color(0xFF94A3B8), fontSize = 9.sp)
                                }
                            }
                            Card(
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("${quotaState.publishedAppsCount}", color = Color(0xFF10B981), fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                                    Text("منشورة", color = Color(0xFF94A3B8), fontSize = 9.sp)
                                }
                            }
                            Card(
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("${quotaState.totalLinesOfCode}", color = Color(0xFFF59E0B), fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                                    Text("أسطر كود", color = Color(0xFF94A3B8), fontSize = 9.sp)
                                }
                            }
                            Card(
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("${activeServer.pingMs}ms", color = Color(0xFF06B6D4), fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                                    Text("استجابة", color = Color(0xFF94A3B8), fontSize = 9.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onNavigateToCounters()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 40.dp)
                        ) {
                            Icon(Icons.Default.Analytics, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("فتح لوحة العدادات والإحصائيات الكاملة 📊", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
            }

            // Comprehensive App Creation Tutorial Academy Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onNavigateToTutorial()
                        }
                        .border(
                            1.5.dp,
                            Brush.horizontalGradient(listOf(Color(0xFF38BDF8), Color(0xFF6366F1), Color(0xFF10B981))),
                            RoundedCornerShape(16.dp)
                        ),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFF38BDF8).copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.School,
                                        contentDescription = null,
                                        tint = Color(0xFF38BDF8),
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "دليل إنشاء التطبيق بالكامل 🎓",
                                        color = Color.White,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = "تعلم صياغة الفكرة، هندسة الواجهات، ونشر تطبيقك بـ 5$",
                                        color = Color(0xFF94A3B8),
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFF10B981).copy(alpha = 0.2f))
                                    .padding(horizontal = 7.dp, vertical = 3.dp)
                            ) {
                                Text("مجاني بالكامل ✓", color = Color(0xFF10B981), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "دليل تفاعلي خطوة بخطوة: كتابة الأوامر الذكية (Prompts)، معمارية Jetpack Compose، المحاكي المباشر، ورفع التطبيق على Google Play و App Store و Web PWA.",
                            color = Color(0xFFCBD5E1),
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onNavigateToTutorial()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38BDF8)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 40.dp)
                        ) {
                            Icon(Icons.Default.MenuBook, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("عرض دليل التعليم الشامل خطوة بخطوة 📖", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
            }

            // High Precision Fast AI Creation Box
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(
                        1.5.dp,
                        Brush.horizontalGradient(listOf(Color(0xFF6366F1), Color(0xFF10B981)))
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Bolt,
                                    contentDescription = null,
                                    tint = Color(0xFF818CF8),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = LanguageManager.getString(langCode, "instant_generate"),
                                    color = Color(0xFF818CF8),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Apps Only Badge supported by servers
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = Color(0xFF6366F1).copy(alpha = 0.2f),
                                border = BorderStroke(1.dp, Color(0xFF6366F1).copy(alpha = 0.5f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Apps,
                                        contentDescription = null,
                                        tint = Color(0xFF818CF8),
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "تطبيقات فقط 📱 مدعومة بالسيرفرات",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Text Field
                        OutlinedTextField(
                            value = quickPromptText,
                            onValueChange = { quickPromptText = it },
                            placeholder = {
                                Text(
                                    "مثال: تطبيق متجر إلكتروني مع سلة مشتريات وتتبع شحنات ودفع فوري...",
                                    color = Color(0xFF64748B),
                                    fontSize = 12.sp
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = {
                                if (quickPromptText.isNotEmpty()) {
                                    IconButton(
                                        onClick = {
                                            quickPromptText = ""
                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        },
                                        modifier = Modifier.minimumInteractiveComponentSize()
                                    ) {
                                        Icon(
                                            Icons.Default.Clear,
                                            contentDescription = "مسح النص",
                                            tint = Color(0xFF94A3B8)
                                        )
                                    }
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF6366F1),
                                unfocusedBorderColor = Color(0xFF334155),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedContainerColor = Color(0xFF0F172A),
                                unfocusedContainerColor = Color(0xFF0F172A)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            maxLines = 3
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Large high-precision Action Button (48dp min height)
                        Button(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                if (!viewModel.canGenerateApp()) {
                                    onOpenSubscriptionModal()
                                } else {
                                    val finalType = if (quickPromptText.contains("لعبة") || quickPromptText.contains("game")) "Game" else selectedType
                                    onNavigateToBuilder(finalType, quickPromptText.ifBlank { null })
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedType == "Game") Color(0xFF10B981) else Color(0xFF6366F1)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 48.dp)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = LanguageManager.getString(langCode, "generate_btn"),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            // 1-Tap Prompt Suggestions
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = LanguageManager.getString(langCode, "suggestions_title"),
                        color = Color(0xFFCBD5E1),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(vertical = 2.dp)
                    ) {
                        items(quickChips) { (chipText, chipType) ->
                            AssistChip(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    quickPromptText = chipText
                                },
                                label = {
                                    Text(
                                        chipText,
                                        fontSize = 11.sp,
                                        color = Color(0xFFE2E8F0),
                                        fontWeight = FontWeight.Medium
                                    )
                                },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = Color(0xFF1E293B)
                                ),
                                border = AssistChipDefaults.assistChipBorder(
                                    enabled = true,
                                    borderColor = Color(0xFF334155)
                                ),
                                modifier = Modifier.heightIn(min = 40.dp)
                            )
                        }
                    }
                }
            }

            // Magnetic Trending Community Showcase (معرض التطبيقات والألعاب الأكثر رواجاً)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Whatshot,
                                contentDescription = null,
                                tint = Color(0xFFF97316),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "تطبيقات وألعاب النخبة الرائجة عالمياً 🏆",
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.sp
                            )
                        }

                        TextButton(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onNavigateToStore()
                            },
                            modifier = Modifier.minimumInteractiveComponentSize()
                        ) {
                            Text("المتجر الكامل", color = Color(0xFF06B6D4), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        items(viewModel.showcaseItems) { item ->
                            ShowcaseCard(
                                item = item,
                                onPlay = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    viewModel.launchShowcaseItem(item)
                                    onNavigateToSandbox()
                                },
                                onBuild = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onNavigateToBuilder(item.type, item.prompt)
                                }
                            )
                        }
                    }
                }
            }

            // Direct 1-Tap Sandbox Engines Launcher Row
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color(0xFF334155)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.PhoneAndroid,
                                    contentDescription = null,
                                    tint = Color(0xFF10B981),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = LanguageManager.getString(langCode, "sandbox_launch_title"),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                            TextButton(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    onNavigateToSandbox()
                                },
                                modifier = Modifier.minimumInteractiveComponentSize()
                            ) {
                                Text(LanguageManager.getString(langCode, "open_all"), color = Color(0xFF10B981), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Fast horizontal row of direct launcher buttons
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(vertical = 4.dp)
                        ) {
                            items(engines) { (engineType, engineLabel, engineColor) ->
                                Button(
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        viewModel.launchEngineDirectly(engineType)
                                        onNavigateToSandbox()
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = engineColor.copy(alpha = 0.2f),
                                        contentColor = engineColor
                                    ),
                                    border = BorderStroke(1.dp, engineColor.copy(alpha = 0.6f)),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.heightIn(min = 44.dp)
                                ) {
                                    Text(
                                        text = engineLabel,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Real Programmer VIP Card (Prominent Call to Action)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onNavigateToDeveloperContact()
                        }
                        .border(
                            1.5.dp,
                            Brush.horizontalGradient(listOf(Color(0xFFF59E0B), Color(0xFFEC4899))),
                            RoundedCornerShape(16.dp)
                        ),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .background(Color(0xFFF59E0B).copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Engineering, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(28.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = LanguageManager.getString(langCode, "contact_dev_btn"),
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFF10B981).copy(alpha = 0.2f))
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Text("ليس روبوت", color = Color(0xFF10B981), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "تطوير خاص، استشارات معمارية، ومراجعة كود عبر واتساب أو إيميل.",
                                color = Color(0xFF94A3B8),
                                fontSize = 10.sp
                            )
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFFF59E0B))
                    }
                }
            }

            // Core Platform Creation Cards
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = LanguageManager.getString(langCode, "core_tools"),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CreationCard(
                            title = LanguageManager.getString(langCode, "tool_app_title"),
                            subtitle = LanguageManager.getString(langCode, "tool_app_sub"),
                            icon = Icons.Default.PhoneAndroid,
                            color = Color(0xFF6366F1),
                            modifier = Modifier.weight(1f),
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onNavigateToBuilder("App", null)
                            }
                        )

                        CreationCard(
                            title = LanguageManager.getString(langCode, "tool_game_title"),
                            subtitle = LanguageManager.getString(langCode, "tool_game_sub"),
                            icon = Icons.Default.SportsEsports,
                            color = Color(0xFF10B981),
                            modifier = Modifier.weight(1f),
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onNavigateToBuilder("Game", null)
                            }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CreationCard(
                            title = LanguageManager.getString(langCode, "tool_projects_title"),
                            subtitle = LanguageManager.getString(langCode, "tool_projects_sub"),
                            icon = Icons.Default.FolderSpecial,
                            color = Color(0xFFF59E0B),
                            modifier = Modifier.weight(1f),
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onNavigateToProjects()
                            }
                        )

                        CreationCard(
                            title = LanguageManager.getString(langCode, "tool_store_title"),
                            subtitle = LanguageManager.getString(langCode, "tool_store_sub"),
                            icon = Icons.Default.Storefront,
                            color = Color(0xFFA855F7),
                            modifier = Modifier.weight(1f),
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onNavigateToStore()
                            }
                        )
                    }
                }
            }

            // Platform Quality and Speed Badges
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B).copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        QualityBadge("⚡ 60 FPS", "استجابة فورية")
                        QualityBadge("🌐 14 لغة", "دعم عالمي")
                        QualityBadge("👨‍💻 50$ VIP", "مبرمج حقيقي")
                        QualityBadge("✨ 3 مجاناً", "تجربة فورية")
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun QualityBadge(title: String, subtitle: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(title, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Text(subtitle, color = Color(0xFF94A3B8), fontSize = 9.sp)
    }
}

@Composable
fun CreationCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .border(
                width = 1.5.dp,
                color = color.copy(alpha = 0.45f),
                shape = RoundedCornerShape(16.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .background(color.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = title, tint = color, modifier = Modifier.size(32.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = subtitle,
                color = Color(0xFF94A3B8),
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}

@Composable
fun ShowcaseCard(
    item: ShowcaseItem,
    onPlay: () -> Unit,
    onBuild: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(260.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(
                1.dp,
                Color(android.graphics.Color.parseColor(item.colorHex)).copy(alpha = 0.5f),
                RoundedCornerShape(16.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Category & Rating
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(android.graphics.Color.parseColor(item.colorHex)).copy(alpha = 0.2f))
                        .padding(horizontal = 7.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = item.categoryAr,
                        color = Color(android.graphics.Color.parseColor(item.colorHex)),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFF59E0B),
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(text = "${item.rating}", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "• ${item.activeUsers}", color = Color(0xFF94A3B8), fontSize = 9.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Title
            Text(
                text = item.titleAr,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 13.sp,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = item.descriptionAr,
                color = Color(0xFF94A3B8),
                fontSize = 10.sp,
                lineHeight = 15.sp,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onPlay,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(android.graphics.Color.parseColor(item.colorHex))
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 36.dp),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("تشغيل 🎮", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onBuild,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFF475569)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 36.dp),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Icon(Icons.Default.AutoFixHigh, contentDescription = null, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("تطوير 🚀", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun StatPill(value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            color = color,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 13.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = Color(0xFF94A3B8),
            fontSize = 9.sp
        )
    }
}
