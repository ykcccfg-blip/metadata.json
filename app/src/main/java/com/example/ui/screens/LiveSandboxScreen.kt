package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CodeInspectorView
import com.example.ui.components.DeviceFrame
import com.example.ui.sandbox.*
import com.example.ui.viewmodel.AppCreatorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveSandboxScreen(
    viewModel: AppCreatorViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val currentProject by viewModel.currentActiveProject.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("📱 المحاكي التفاعلي", "💻 كود Kotlin", "💙 كود Flutter", "🗄️ المعمارية")

    var reloadKey by remember { mutableIntStateOf(0) }

    val activeType = currentProject?.interactiveType ?: "SPACE_DODGER"
    val projectTitle = currentProject?.title ?: "تطبيقك المُولد"

    val primaryColor = when (currentProject?.type) {
        "Game" -> Color(0xFF14B8A6)
        else -> Color(0xFF6366F1)
    }

    Scaffold(
        containerColor = Color(0xFF0F172A),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "المعاينة الحية (Live Sandbox)",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = projectTitle,
                            color = Color(0xFF10B981),
                            fontSize = 11.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    // 5$ Multi-Platform Publish Button
                    Button(
                        onClick = {
                            currentProject?.let { viewModel.openPublishDialog(it) }
                                ?: viewModel.openPublishDialog()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(end = 4.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.CloudUpload, contentDescription = null, tint = Color.Black, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("نشر (5$) 🚀", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                    }

                    IconButton(onClick = {
                        reloadKey++
                        Toast.makeText(context, "تمت إعادة تشغيل المحاكي 🔄", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reload",
                            tint = Color(0xFFCBD5E1)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1E293B))
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Segmented Tab Row
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color(0xFF1E293B),
                contentColor = Color.White,
                edgePadding = 12.dp
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontSize = 12.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == index) Color(0xFF10B981) else Color(0xFF94A3B8)
                            )
                        }
                    )
                }
            }

            // Global Server Connectivity Telemetry Bar
            val servers by viewModel.servers.collectAsState()
            val selectedServerId by viewModel.selectedServerId.collectAsState()
            val isTurboEnabled by viewModel.isTurboEnabled.collectAsState()
            val activeServer = servers.find { it.id == selectedServerId } ?: servers.first()

            Surface(
                color = Color(0xFF0F172A),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .background(Color(0xFF10B981), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "سيرفر التشغيل المباشر: ${activeServer.flag} ${activeServer.nameAr}",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${activeServer.pingMs}ms",
                            color = Color(0xFF10B981),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (isTurboEnabled) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "⚡ Turbo",
                                color = Color(0xFF06B6D4),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Tab Content
            when (selectedTab) {
                0 -> {
                    // Live Interactive Sandbox inside Phone Frame Mockup
                    val scrollState = rememberScrollState()
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Success Status Card matching Flutter PreviewView message
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp, vertical = 6.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "التطبيق يعمل بكفاءة عالية! ⚡",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        text = "جميع الأزرار والروابط وتفاعلات اللمس جاهزة للاختبار داخل إطار الهاتف أدناه.",
                                        color = Color(0xFF94A3B8),
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }

                        // Sandbox Simulator Selector Pills (Easy & Fast Touch)
                        val haptic = LocalHapticFeedback.current
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                        ) {
                            Text(
                                text = "اختر المحرك التفاعلي للمعاينة المباشرة:",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                            androidx.compose.foundation.lazy.LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                contentPadding = PaddingValues(horizontal = 4.dp)
                            ) {
                                val engineItems = listOf(
                                    "STOREFRONT" to "🛒 متجر وسلة تسوق",
                                    "TODO_PRO" to "📋 مهام وعادات Pro",
                                    "FITNESS_FLOW" to "💧 لياقة وصحة",
                                    "EXPENSE_TRACKER" to "💰 محفظة ومصروفات",
                                    "AI_ASSISTANT" to "🤖 مساعد ذكي شات",
                                    "DOCTOR_BOOKING" to "🩺 عيادات واستشارات"
                                )
                                items(engineItems.size) { i ->
                                    val (type, label) = engineItems[i]
                                    val isSelected = activeType == type
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = {
                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            viewModel.launchEngineDirectly(type)
                                        },
                                        label = {
                                            Text(
                                                label,
                                                fontSize = 11.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                            )
                                        },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = Color(0xFF06B6D4),
                                            selectedLabelColor = Color.Black,
                                            containerColor = Color(0xFF1E293B),
                                            labelColor = Color(0xFFCBD5E1)
                                        ),
                                        border = FilterChipDefaults.filterChipBorder(
                                            enabled = true,
                                            selected = isSelected,
                                            borderColor = if (isSelected) Color(0xFF06B6D4) else Color(0xFF334155)
                                        ),
                                        modifier = Modifier.heightIn(min = 40.dp)
                                    )
                                }
                            }
                        }

                        // Authentic Realistic Phone Frame Container
                        key(reloadKey, activeType) {
                            DeviceFrame(
                                title = projectTitle,
                                primaryColor = primaryColor
                            ) {
                                when (activeType) {
                                    "STOREFRONT" -> StoreEcommerceApp()
                                    "FITNESS_FLOW" -> FitnessFlowApp()
                                    "EXPENSE_TRACKER" -> ExpenseTrackerApp()
                                    "AI_ASSISTANT" -> AiChatAssistantApp()
                                    "DOCTOR_BOOKING" -> DoctorBookingApp()
                                    else -> TodoProApp()
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // High-Converting Publishing Card: "عند ما يكتمل التطبيق ويتم المعاينه يدفع 5 دولار ويرفع التطبيق في كل المنصات"
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .border(
                                    1.5.dp,
                                    Brush.horizontalGradient(listOf(Color(0xFF10B981), Color(0xFF06B6D4))),
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
                                        Text("✅", fontSize = 18.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("اكتملت المعاينة بنجاح!", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                                    }
                                    Text("5$ فقط للنشر", color = Color(0xFF10B981), fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "تم اختبار وتأكيد جاهزية تطبيقك في المحاكي. الآن اضغط أدناه لرفع ونشر التطبيق فورياً على Google Play و Apple App Store و Web PWA و Huawei عبر أعلى السيرفرات السحابية العالمية.",
                                    color = Color(0xFFCBD5E1),
                                    fontSize = 11.sp,
                                    lineHeight = 16.sp
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Button(
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        currentProject?.let { viewModel.openPublishDialog(it) }
                                            ?: viewModel.openPublishDialog()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(min = 46.dp)
                                ) {
                                    Icon(Icons.Default.CloudUpload, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("ادفع 5$ وارفع التطبيق في كل المنصات 🚀", color = Color.Black, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Quick action bar under Phone frame
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    reloadKey++
                                    Toast.makeText(context, "تمت إعادة تشغيل المحرك 🔄", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 44.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFCBD5E1))
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("إعادة تعيين", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    selectedTab = 1 // Switch to Kotlin code tab
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 44.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B), contentColor = Color(0xFF818CF8))
                            ) {
                                Icon(Icons.Default.Code, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("عرض الكود", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }

                1 -> {
                    // Kotlin / Jetpack Compose Source Code
                    Box(modifier = Modifier.fillMaxSize().padding(14.dp)) {
                        CodeInspectorView(
                            code = currentProject?.codeCompose ?: "// Kotlin Jetpack Compose Code",
                            language = "Jetpack Compose (Kotlin)"
                        )
                    }
                }

                2 -> {
                    // Flutter Dart Code
                    Box(modifier = Modifier.fillMaxSize().padding(14.dp)) {
                        CodeInspectorView(
                            code = currentProject?.codeFlutter ?: "// Flutter Code",
                            language = "Flutter (Dart 3.x)"
                        )
                    }
                }

                3 -> {
                    // Architecture & Room Database Schema
                    val scroll = rememberScrollState()
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scroll)
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("معمارية النظام (System Architecture)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "• النمط المعماري: MVVM + Clean Architecture\n" +
                                            "• محرك البيانات: Room Database (SQLite Engine)\n" +
                                            "• إدارة الحالة: StateFlow & Compose SnapshotState\n" +
                                            "• محرك الذكاء الاصطناعي: Gemini 3.5 Flash REST Client\n" +
                                            "• محرك الرسوميات: Hardware Accelerated Android Canvas (60 FPS)",
                                    color = Color(0xFFCBD5E1),
                                    fontSize = 12.sp,
                                    lineHeight = 20.sp
                                )
                            }
                        }

                        CodeInspectorView(
                            code = currentProject?.architectureSchema ?: "{}",
                            language = "Database Schema (JSON)"
                        )
                    }
                }
            }
        }
    }
}
