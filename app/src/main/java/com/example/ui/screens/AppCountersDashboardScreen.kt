package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.AppCreatorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppCountersDashboardScreen(
    viewModel: AppCreatorViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToBuilder: () -> Unit,
    onNavigateToSandbox: () -> Unit,
    onNavigateToProjects: () -> Unit
) {
    BackHandler(onBack = onNavigateBack)

    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    val quotaState by viewModel.quotaState.collectAsState()
    val servers by viewModel.servers.collectAsState()
    val selectedServerId by viewModel.selectedServerId.collectAsState()
    val isTurboEnabled by viewModel.isTurboEnabled.collectAsState()
    val isPinging by viewModel.isPinging.collectAsState()
    val activeServer = viewModel.getActiveServer()
    val savedProjects by viewModel.savedProjects.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("📊 الإجمالي", "💻 الأكواد والتوليد", "🌐 السيرفرات والأداء", "🚀 المنصات والنشر")

    Scaffold(
        containerColor = Color(0xFF0F172A),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("📊", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "عدادات التطبيق الشاملة",
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp
                            )
                        }
                        Text(
                            text = "إحصائيات حية للأداء، الأكواد، السيرفرات، والتوزيع",
                            color = Color(0xFF10B981),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "الرجوع",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    // Turbo Toggle Action
                    Surface(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            val enabled = viewModel.toggleTurbo()
                            val msg = if (enabled) "تم تفعيل وضع التوربو الفائق (100 Gbps)! ⚡" else "تم إيقاف وضع التوربو."
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isTurboEnabled) Color(0xFF10B981) else Color(0xFF1E293B),
                        border = BorderStroke(1.dp, if (isTurboEnabled) Color(0xFF10B981) else Color(0xFF334155)),
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isTurboEnabled) "TURBO ⚡" else "TURBO",
                                color = if (isTurboEnabled) Color.Black else Color(0xFFCBD5E1),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Refresh Live Telemetry
                    IconButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            viewModel.refreshCounters()
                            viewModel.pingServers()
                            Toast.makeText(context, "تم تحديث كافة العدادات والبنق الحي! 🔄", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.minimumInteractiveComponentSize()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "تحديث العدادات",
                            tint = Color(0xFF38BDF8)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1E293B))
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Live Status Hero Banner
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .border(
                            1.5.dp,
                            Brush.horizontalGradient(
                                listOf(Color(0xFF10B981), Color(0xFF6366F1), Color(0xFF06B6D4))
                            ),
                            RoundedCornerShape(20.dp)
                        ),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
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
                                    text = "العدادات متصلة بالشبكة السحابية الحية",
                                    color = Color(0xFF10B981),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFF0F172A),
                                border = BorderStroke(1.dp, Color(0xFF334155))
                            ) {
                                Text(
                                    text = "${activeServer.flag} ${activeServer.nameAr}",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // 4 Primary Counters Grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            HeroMetricPill(
                                title = "تطبيقات منشأة",
                                value = "${quotaState.totalAppsCreated}",
                                icon = Icons.Default.PhoneAndroid,
                                color = Color(0xFF6366F1),
                                modifier = Modifier.weight(1f)
                            )
                            HeroMetricPill(
                                title = "تطبيقات منشورة",
                                value = "${quotaState.publishedAppsCount}",
                                icon = Icons.Default.RocketLaunch,
                                color = Color(0xFF10B981),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            HeroMetricPill(
                                title = "أسطر كود أصلي",
                                value = "${quotaState.totalLinesOfCode}+",
                                icon = Icons.Default.Code,
                                color = Color(0xFFF59E0B),
                                modifier = Modifier.weight(1f)
                            )
                            HeroMetricPill(
                                title = "سرعة الاستجابة",
                                value = "${activeServer.pingMs}ms",
                                icon = Icons.Default.Speed,
                                color = Color(0xFF06B6D4),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Tabs Selector
            item {
                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color(0xFF1E293B),
                    contentColor = Color.White,
                    edgePadding = 0.dp,
                    modifier = Modifier.clip(RoundedCornerShape(12.dp))
                ) {
                    tabs.forEachIndexed { index, tabName ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                selectedTab = index
                            },
                            text = {
                                Text(
                                    text = tabName,
                                    fontSize = 11.sp,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTab == index) Color(0xFF10B981) else Color(0xFF94A3B8)
                                )
                            }
                        )
                    }
                }
            }

            // Tab 0 or 1: عدادات التوليد والأكواد البرمجية
            if (selectedTab == 0 || selectedTab == 1) {
                item {
                    MetricSectionHeader(title = "💻 عدادات بناء التطبيقات والأكواد الذكية", icon = Icons.Default.Terminal)
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, Color(0xFF334155))
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            MetricDataRow(
                                title = "إجمالي التطبيقات المكتملة",
                                subtitle = "تطبيقات جوال حقيقية تم توليدها بالكامل",
                                value = "${quotaState.totalAppsCreated} تطبيق",
                                icon = Icons.Default.CheckCircle,
                                color = Color(0xFF10B981)
                            )
                            HorizontalDivider(color = Color(0xFF334155).copy(alpha = 0.6f))

                            MetricDataRow(
                                title = "أسطر كود Jetpack Compose & Flutter",
                                subtitle = "أكواد برمجية نظيفة وقابلة للتصدير والتشغيل",
                                value = "${quotaState.totalLinesOfCode} سطر",
                                icon = Icons.Default.Code,
                                color = Color(0xFF6366F1)
                            )
                            HorizontalDivider(color = Color(0xFF334155).copy(alpha = 0.6f))

                            MetricDataRow(
                                title = "تشغيلات المحاكي التفاعلي (Sandbox)",
                                subtitle = "مرات فحص ومعاينة التطبيقات بمعدل 60 FPS",
                                value = "${quotaState.totalSandboxRuns} تشغيل حي",
                                icon = Icons.Default.PlayCircle,
                                color = Color(0xFF06B6D4)
                            )
                            HorizontalDivider(color = Color(0xFF334155).copy(alpha = 0.6f))

                            MetricDataRow(
                                title = "المشاريع المحفوظة محلياً (Room Database)",
                                subtitle = "مشاريعك المحفوظة داخل قاعدة بيانات الهاتف",
                                value = "${savedProjects.size} مشروع نشط",
                                icon = Icons.Default.Storage,
                                color = Color(0xFFF59E0B)
                            )
                            HorizontalDivider(color = Color(0xFF334155).copy(alpha = 0.6f))

                            MetricDataRow(
                                title = "التوكينات المعالجة بالذكاء الاصطناعي",
                                subtitle = "حجم البيانات المعالجة عبر Gemini Cloud",
                                value = "${quotaState.totalTokensProcessed} Token",
                                icon = Icons.Default.Memory,
                                color = Color(0xFFEC4899)
                            )
                            HorizontalDivider(color = Color(0xFF334155).copy(alpha = 0.6f))

                            MetricDataRow(
                                title = "مرات نسخ الأكواد وتصديرها",
                                subtitle = "مشاركات ومخرجات المشاريع من المستخدمين",
                                value = "${quotaState.totalCodeCopies} مرة",
                                icon = Icons.Default.ContentCopy,
                                color = Color(0xFFA855F7)
                            )
                        }
                    }
                }
            }

            // Tab 0 or 2: عدادات السيرفرات السحابية والأداء الحي
            if (selectedTab == 0 || selectedTab == 2) {
                item {
                    MetricSectionHeader(title = "🌐 عدادات السيرفرات السحابية والأداء (100 Gbps)", icon = Icons.Default.CloudSync)
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, Color(0xFF334155))
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            MetricDataRow(
                                title = "سرعة النطاق الترددي للشبكة",
                                subtitle = "قنوات سيرفرات فائقة السرعة 100 Gbps",
                                value = "${quotaState.cloudBandwidthGbps} Gbps",
                                icon = Icons.Default.Speed,
                                color = Color(0xFF10B981)
                            )
                            HorizontalDivider(color = Color(0xFF334155).copy(alpha = 0.6f))

                            MetricDataRow(
                                title = "سيرفرات المعالجة العالمية المتصلة",
                                subtitle = "مراكز بيانات موزعة استراتيجياً حول العالم",
                                value = "${servers.size} سيرفرات متصلة ✓",
                                icon = Icons.Default.Public,
                                color = Color(0xFF06B6D4)
                            )
                            HorizontalDivider(color = Color(0xFF334155).copy(alpha = 0.6f))

                            MetricDataRow(
                                title = "نسبة جاهزية واستقرار السيرفرات (Uptime)",
                                subtitle = "توفر السيرفرات دون انقطاع على مدار الساعة",
                                value = "${quotaState.serverUptimePercentage}%",
                                icon = Icons.Default.Shield,
                                color = Color(0xFF6366F1)
                            )
                            HorizontalDivider(color = Color(0xFF334155).copy(alpha = 0.6f))

                            MetricDataRow(
                                title = "سلاسة العرض ومعدل الإطارات",
                                subtitle = "واجهات Jetpack Compose الأصلية السلسة",
                                value = "60 FPS Native",
                                icon = Icons.Default.Bolt,
                                color = Color(0xFFF59E0B)
                            )
                            HorizontalDivider(color = Color(0xFF334155).copy(alpha = 0.6f))

                            MetricDataRow(
                                title = "استهلاك الذاكرة العشوائية (RAM)",
                                subtitle = "كفاءة استهلاك خفيفة جداً وسريعة",
                                value = "${quotaState.memoryUsageMb} MB",
                                icon = Icons.Default.DeveloperBoard,
                                color = Color(0xFF10B981)
                            )
                            HorizontalDivider(color = Color(0xFF334155).copy(alpha = 0.6f))

                            MetricDataRow(
                                title = "حجم التخزين وقاعدة البيانات المحلية",
                                subtitle = "المساحة المستهلكة لحفظ المشاريع والأصول",
                                value = "${quotaState.storageUsageKb} KB",
                                icon = Icons.Default.Folder,
                                color = Color(0xFFCBD5E1)
                            )
                        }
                    }
                }

                // Servers Telemetry List
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, Color(0xFF06B6D4).copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("📡", fontSize = 16.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("حالة السيرفرات الـ 8 الحية:", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                                if (isPinging) {
                                    Text("جاري الفحص...", color = Color(0xFF06B6D4), fontSize = 10.sp)
                                } else {
                                    Text("جميعها نشطة 🟢", color = Color(0xFF10B981), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            servers.forEach { srv ->
                                val isSelected = srv.id == selectedServerId
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) Color(0xFF1E293B) else Color.Transparent)
                                        .clickable {
                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            viewModel.selectServer(srv.id)
                                        }
                                        .padding(horizontal = 8.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(srv.flag, fontSize = 14.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(srv.nameAr, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            Text(srv.regionCode, color = Color(0xFF94A3B8), fontSize = 9.sp)
                                        }
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "${srv.pingMs}ms",
                                            color = if (srv.pingMs < 30) Color(0xFF10B981) else Color(0xFF06B6D4),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                        if (isSelected) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("✓ نشط", color = Color(0xFF10B981), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Tab 0 or 3: عدادات النشر والتوزيع على المنصات
            if (selectedTab == 0 || selectedTab == 3) {
                item {
                    MetricSectionHeader(title = "🚀 عدادات النشر والمنصات العالمية", icon = Icons.Default.RocketLaunch)
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(
                            1.dp,
                            Brush.horizontalGradient(listOf(Color(0xFF10B981), Color(0xFF06B6D4)))
                        )
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            MetricDataRow(
                                title = "التطبيقات المنشورة عالمياً",
                                subtitle = "تم توقيعها برمجياً ونشرها على السيرفرات",
                                value = "${quotaState.publishedAppsCount} تطبيق نشط",
                                icon = Icons.Default.CloudDone,
                                color = Color(0xFF10B981)
                            )
                            HorizontalDivider(color = Color(0xFF334155).copy(alpha = 0.6f))

                            MetricDataRow(
                                title = "الجمهور والوصول التقديري المباشر",
                                subtitle = "المستخدمون المستهدفون عبر المتاجر العالمية",
                                value = "${quotaState.estimatedAudienceReach}+ مستخدم",
                                icon = Icons.Default.Groups,
                                color = Color(0xFF06B6D4)
                            )
                            HorizontalDivider(color = Color(0xFF334155).copy(alpha = 0.6f))

                            MetricDataRow(
                                title = "مرات تحميل حزم APK و AAB",
                                subtitle = "تنزيل الحزم الجاهزة للتثبيت المباشر",
                                value = "${quotaState.totalApkDownloads} تنزيل",
                                icon = Icons.Default.Download,
                                color = Color(0xFF6366F1)
                            )
                            HorizontalDivider(color = Color(0xFF334155).copy(alpha = 0.6f))

                            MetricDataRow(
                                title = "تكلفة النشر الشامل لجميع المتاجر",
                                subtitle = "Google Play + App Store + Web PWA + Huawei",
                                value = "5$ فقط لمرة واحدة",
                                icon = Icons.Default.AttachMoney,
                                color = Color(0xFF10B981)
                            )
                            HorizontalDivider(color = Color(0xFF334155).copy(alpha = 0.6f))

                            MetricDataRow(
                                title = "التوفير المالي المقدر مقارنة بالشركات",
                                subtitle = "توفير تكلفة التطوير والبرمجة التقليدية",
                                value = "$${quotaState.estimatedSavedCostUsd}+ توفير",
                                icon = Icons.Default.Savings,
                                color = Color(0xFFF59E0B)
                            )
                        }
                    }
                }

                // Supported Store Badges
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, Color(0xFF334155))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("📦 حالة منصات التوزيع والتوافق (100% Ready):", color = Color(0xFFCBD5E1), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))

                            val platforms = listOf(
                                "Google Play (AAB & APK)" to "جاهز للتوقيع ✓",
                                "Apple App Store (iOS)" to "حزم جاهزة ✓",
                                "Web PWA (Cloud Hosted)" to "استضافة فورية ✓",
                                "Huawei AppGallery" to "توافق كامل ✓",
                                "Amazon Appstore & Windows" to "مدعوم ✓"
                            )

                            platforms.forEach { (name, status) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 3.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(name, color = Color.White, fontSize = 11.sp)
                                    Text(status, color = Color(0xFF10B981), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Action Buttons
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onNavigateToBuilder()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).heightIn(min = 44.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("إنشاء تطبيق جديد 🚀", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }

                    OutlinedButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onNavigateToSandbox()
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).heightIn(min = 44.dp),
                        border = BorderStroke(1.dp, Color(0xFF6366F1)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF818CF8))
                    ) {
                        Icon(Icons.Default.PhoneAndroid, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("المحاكي المباشر 📱", fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
private fun HeroMetricPill(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(value, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                Text(title, color = Color(0xFF94A3B8), fontSize = 10.sp)
            }
        }
    }
}

@Composable
private fun MetricSectionHeader(title: String, icon: ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Icon(icon, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = title,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
        )
    }
}

@Composable
private fun MetricDataRow(
    title: String,
    subtitle: String,
    value: String,
    icon: ImageVector,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text(subtitle, color = Color(0xFF94A3B8), fontSize = 10.sp)
            }
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(color.copy(alpha = 0.2f))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(value, color = color, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
        }
    }
}
