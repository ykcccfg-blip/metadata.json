package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.GeminiNetwork
import com.example.data.LanguageManager
import com.example.ui.viewmodel.AppCreatorViewModel

@Composable
fun SettingsDialog(
    viewModel: AppCreatorViewModel,
    onDismiss: () -> Unit,
    onOpenSubscription: () -> Unit,
    onOpenDeveloperContact: () -> Unit,
    onOpenTutorial: () -> Unit = {},
    onOpenCounters: () -> Unit = {}
) {
    val haptic = LocalHapticFeedback.current
    val quotaState by viewModel.quotaState.collectAsState()
    var showLanguagePicker by remember { mutableStateOf(false) }

    var hapticsEnabled by remember { mutableStateOf(true) }
    var soundEnabled by remember { mutableStateOf(true) }
    val hasApiKey = GeminiNetwork.hasValidApiKey()

    val currentLang = LanguageManager.supportedLanguages.find { it.code == quotaState.currentLanguage }
        ?: LanguageManager.supportedLanguages.first()

    if (showLanguagePicker) {
        LanguageSelectorDialog(
            viewModel = viewModel,
            onDismiss = { showLanguagePicker = false }
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            shape = RoundedCornerShape(22.dp),
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.88f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "الإعدادات والعدادات ⚙️",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                    IconButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onDismiss()
                        },
                        modifier = Modifier.minimumInteractiveComponentSize()
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF94A3B8))
                    }
                }

                HorizontalDivider(color = Color(0xFF334155))

                // Language Selector Card (All World Languages)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            showLanguagePicker = true
                        },
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    border = BorderStroke(1.dp, Color(0xFF6366F1).copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(currentLang.flag, fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "لغة التطبيق (Language)",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${currentLang.nativeName} (${currentLang.nameEn})",
                                    color = Color(0xFF818CF8),
                                    fontSize = 11.sp
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF6366F1).copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("تغيير 🌐", color = Color(0xFF818CF8), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Global Server Node Selector Card
                var showServerDialogInSettings by remember { mutableStateOf(false) }
                val servers by viewModel.servers.collectAsState()
                val selectedServerId by viewModel.selectedServerId.collectAsState()
                val isTurboEnabled by viewModel.isTurboEnabled.collectAsState()
                val activeServer = servers.find { it.id == selectedServerId } ?: servers.first()

                if (showServerDialogInSettings) {
                    GlobalServersDialog(
                        viewModel = viewModel,
                        onDismiss = { showServerDialogInSettings = false }
                    )
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            showServerDialogInSettings = true
                        },
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    border = BorderStroke(1.dp, Color(0xFF06B6D4).copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(activeServer.flag, fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("السيرفر العالمي النشط 🌐", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    if (isTurboEnabled) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("⚡ Turbo", color = Color(0xFF06B6D4), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Text("${activeServer.nameAr} • ${activeServer.pingMs}ms (${activeServer.bandwidthGbps} Gbps)", color = Color(0xFF94A3B8), fontSize = 11.sp)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF06B6D4).copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("تغيير ⚡", color = Color(0xFF06B6D4), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Complete App Telemetry Counters (العدادات الشاملة للتطبيق)
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("📈 عدادات الاستخدام والأداء:", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text(
                                text = if (quotaState.isSubscribed) "باقة غير محدودة ⚡" else "${quotaState.appsGeneratedCount} / 3 مجاناً",
                                color = if (quotaState.isSubscribed) Color(0xFF10B981) else Color(0xFFF59E0B),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))

                        // 4 Metrics Grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            MetricCounterItem(
                                title = "تطبيقات منشأة",
                                value = "${quotaState.totalAppsCreated}",
                                icon = Icons.Default.PhoneAndroid,
                                color = Color(0xFF6366F1),
                                modifier = Modifier.weight(1f)
                            )
                            MetricCounterItem(
                                title = "تطبيقات منشورة",
                                value = "${quotaState.publishedAppsCount}",
                                icon = Icons.Default.CloudDone,
                                color = Color(0xFF10B981),
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            MetricCounterItem(
                                title = "أسطر كود أصلي",
                                value = "${quotaState.totalLinesOfCode}+",
                                icon = Icons.Default.Code,
                                color = Color(0xFFF59E0B),
                                modifier = Modifier.weight(1f)
                            )
                            MetricCounterItem(
                                title = "تشغيل المحاكي",
                                value = "${quotaState.totalSandboxRuns}",
                                icon = Icons.Default.PlayArrow,
                                color = Color(0xFFA855F7),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Tutorial Academy Button
                Button(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onDismiss()
                        onOpenTutorial()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38BDF8)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 46.dp)
                ) {
                    Icon(Icons.Default.School, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("دليل وأكاديمية إنشاء التطبيق بالكامل 🎓", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                // Full Counters & Telemetry Dashboard Button
                Button(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onDismiss()
                        onOpenCounters()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 46.dp)
                ) {
                    Icon(Icons.Default.Analytics, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("عدادات وإحصائيات التطبيق الشاملة 📊", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                // Subscription Management Quick Button ($5 / $50)
                Button(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onOpenSubscription()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 46.dp)
                ) {
                    Icon(Icons.Default.WorkspacePremium, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("إدارة الاشتراكات وباقات الـ 5$ والـ 50$ ⚡", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                // Contact Real Developer Button ($50)
                OutlinedButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onDismiss()
                        onOpenDeveloperContact()
                    },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFF59E0B)),
                    border = BorderStroke(1.dp, Color(0xFFF59E0B)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 46.dp)
                ) {
                    Icon(Icons.Default.Engineering, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("تواصل مع صاحب التطبيق (مبرمج حقيقي 50$) 👨‍💻", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }

                // Gemini API Status Box
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (hasApiKey) Icons.Default.CheckCircle else Icons.Default.Info,
                                contentDescription = null,
                                tint = if (hasApiKey) Color(0xFF10B981) else Color(0xFFF59E0B),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (hasApiKey) "مفتاح Gemini API متصل بنجاح ⚡" else "محرك التوليد الذكي نشط (Offline Ready)",
                                color = if (hasApiKey) Color(0xFF10B981) else Color(0xFFF59E0B),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Toggles
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("الاهتزاز اللمسي (Haptic Feedback)", color = Color.White, fontSize = 12.sp)
                    Switch(
                        checked = hapticsEnabled,
                        onCheckedChange = { hapticsEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF10B981),
                            uncheckedTrackColor = Color(0xFF334155)
                        )
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("المؤثرات الصوتية للألعاب", color = Color.White, fontSize = 12.sp)
                    Switch(
                        checked = soundEnabled,
                        onCheckedChange = { soundEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF10B981),
                            uncheckedTrackColor = Color(0xFF334155)
                        )
                    )
                }

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("إغلاق", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun MetricCounterItem(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.height(2.dp))
            Text(value, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
            Text(title, color = Color(0xFF94A3B8), fontSize = 9.sp, maxLines = 1)
        }
    }
}
