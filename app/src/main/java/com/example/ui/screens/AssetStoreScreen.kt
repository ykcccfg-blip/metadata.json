package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.model.AppTemplate
import com.example.ui.viewmodel.AppCreatorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetStoreScreen(
    viewModel: AppCreatorViewModel,
    onNavigateBack: () -> Unit,
    onUseTemplateInBuilder: (type: String, prompt: String) -> Unit,
    onLaunchSandbox: () -> Unit
) {
    val templates = remember {
        listOf(
            AppTemplate(
                id = "t1",
                title = "NovaStore E-Commerce & Pay",
                titleAr = "متجر التجارة الإلكترونية وسلة الشراء 🛒",
                type = "App",
                description = "Full mobile shop with real catalog, cart items, delivery tracker, and instant checkout.",
                descriptionAr = "تطبيق متجر إلكتروني ذكي متكامل مع سلة مشتريات وتتبع شحنات وبوابات دفع إلكتروني.",
                category = "E-Commerce & Retail",
                prompt = "تطبيق متجر إلكتروني وسلة مشتريات وتتبع الشحنات والدفع الإلكتروني",
                interactiveType = "STOREFRONT",
                iconName = "shopping_cart",
                badge = "الأعلى طلباً ⭐"
            ),
            AppTemplate(
                id = "t2",
                title = "NovaAI Chat & Assistant",
                titleAr = "مساعد المحادثة والذكاء الاصطناعي 🤖",
                type = "App",
                description = "Conversational AI assistant with specialized modes, quick prompt chips, and history logs.",
                descriptionAr = "تطبيق دردشة ذكية متعدد التخصصات مع نماذج ردود سريعة وحفظ تلقائي للمحادثات.",
                category = "AI & Chatbot",
                prompt = "تطبيق مساعد دردشة ذكي ومحادثات تفاعلية مع حفظ الرسائل",
                interactiveType = "AI_ASSISTANT",
                iconName = "smart_toy",
                badge = "فائق الذكاء ⚡"
            ),
            AppTemplate(
                id = "t3",
                title = "Smart Habit & Task Flow",
                titleAr = "منظم المهام والعادات الذكي 📋",
                type = "App",
                description = "Complete task manager with priorities, progress gauge, and instant filtering.",
                descriptionAr = "تطبيق إنتاجي متكامل لتنظيم المهام اليومية مع مؤشر إنجاز بياني وتصنيف الأولويات.",
                category = "Productivity",
                prompt = "تطبيق إنتاجية لتتبع المهام اليومية مع إحصائيات بصرية وتصفية ذكية",
                interactiveType = "TODO_PRO",
                iconName = "check_circle",
                badge = "الأكثر شعبية 🔥"
            ),
            AppTemplate(
                id = "t4",
                title = "FitTrack Pro Flow",
                titleAr = "متتبع اللياقة والصحة الشامل 💧",
                type = "App",
                description = "Daily hydration logger, workout register, and calorie burn counters.",
                descriptionAr = "تطبيق تتبع اللياقة البدنية وشرب الماء اليومي وتسجيل النشاطات الرياضية وحرق السعرات.",
                category = "Health & Fitness",
                prompt = "تطبيق لياقة وصحة ذكي لتسجيل كمية الماء والتمارين اليومية",
                interactiveType = "FITNESS_FLOW",
                iconName = "fitness_center",
                badge = "صحي ورياضي 🏃"
            ),
            AppTemplate(
                id = "t5",
                title = "MedCare Doctor & Clinic Booking",
                titleAr = "حجز العيادات والاستشارات الطبية 🩺",
                type = "App",
                description = "Doctor appointment reservation, clinic schedule, patient records, and emergency alerts.",
                descriptionAr = "تطبيق حجز المواعيد والعيادات الطبية مع ملفات المرضى وتنبيهات الطوارئ وسجلات الاستشارات.",
                category = "Medical & Health",
                prompt = "تطبيق حجز عيادات طبية ومواعيد دكاترة مع ملفات المرضى",
                interactiveType = "DOCTOR_BOOKING",
                iconName = "local_hospital",
                badge = "طبي معتمد 🏥"
            ),
            AppTemplate(
                id = "t6",
                title = "Smart Ledger & Wallet",
                titleAr = "محفظة تتبع المصروفات 💰",
                type = "App",
                description = "Personal finance tracker with income/expense logs and balance stats.",
                descriptionAr = "تطبيق مالي لإدارة الميزانية الشخصية، تتبع المصروفات والإيرادات اليومية.",
                category = "Personal Finance",
                prompt = "تطبيق إدارة مصاريف وميزانية شخصية مع تصنيفات وحساب رصيد",
                interactiveType = "EXPENSE_TRACKER",
                iconName = "account_balance_wallet",
                badge = "مال وأعمال 💼"
            )
        )
    }

    Scaffold(
        containerColor = Color(0xFF0F172A),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "متجر قوالب التطبيقات الذكية",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                        Text(
                            text = "تطبيقات حقيقية مدعومة بالسيرفرات • نشر بـ 5$ عند المعاينة",
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1E293B))
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(templates, key = { it.id }) { template ->
                TemplateCard(
                    template = template,
                    onUseInBuilder = {
                        onUseTemplateInBuilder("App", template.prompt)
                    },
                    onInstantPlay = {
                        val gen = com.example.model.GeneratedProject(
                            title = template.title,
                            type = "App",
                            description = template.descriptionAr,
                            category = template.category,
                            features = listOf("جاهز للتجربة والمعاينة الحية", "مدعوم بالسيرفرات السحابية", "جاهز للنشر بـ 5$ لجميع المنصات"),
                            interactiveType = template.interactiveType,
                            colorHex = "#6366F1",
                            iconName = template.iconName,
                            codeCompose = "// Jetpack Compose code for ${template.title}",
                            codeFlutter = "// Flutter code for ${template.title}",
                            architectureSchema = "{\n  \"template\": \"${template.title}\",\n  \"status\": \"PRODUCTION_READY\"\n}"
                        )
                        viewModel.setSandboxProject(gen)
                        onLaunchSandbox()
                    }
                )
            }
        }
    }
}

@Composable
private fun TemplateCard(
    template: AppTemplate,
    onUseInBuilder: () -> Unit,
    onInstantPlay: () -> Unit
) {
    val accentColor = Color(0xFF6366F1)

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        shape = RoundedCornerShape(16.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF334155))),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(accentColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PhoneAndroid,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = template.titleAr,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                    Text(
                        text = template.category,
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF10B981).copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(template.badge, color = Color(0xFF10B981), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = template.descriptionAr,
                color = Color(0xFFCBD5E1),
                fontSize = 12.sp,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            val haptic = LocalHapticFeedback.current
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onInstantPlay()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 44.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("معاينة حية 📱", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onUseInBuilder()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 44.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFCBD5E1))
                ) {
                    Icon(Icons.Default.Build, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("تخصيص وبناء", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
