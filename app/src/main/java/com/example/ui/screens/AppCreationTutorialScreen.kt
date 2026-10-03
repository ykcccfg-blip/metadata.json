package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class TutorialStage(
    val stageNumber: Int,
    val titleAr: String,
    val subtitleAr: String,
    val durationMin: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color,
    val keyPoints: List<String>,
    val deepExplanation: String,
    val samplePrompt: String? = null,
    val techSpecs: String? = null
)

data class TutorialFaq(
    val question: String,
    val answer: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppCreationTutorialScreen(
    onNavigateBack: () -> Unit,
    onStartBuildingWithPrompt: (String) -> Unit,
    onNavigateToSandbox: () -> Unit,
    onNavigateToDeveloper: () -> Unit
) {
    BackHandler(onBack = onNavigateBack)

    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val listState = rememberLazyListState()

    var selectedStageIndex by remember { mutableIntStateOf(0) }
    var expandedStageId by remember { mutableIntStateOf(1) }

    // Interactive Prompt Builder state
    var selectedAppCategory by remember { mutableStateOf("متجر إلكتروني") }
    var featurePayment by remember { mutableStateOf(true) }
    var featureDatabase by remember { mutableStateOf(true) }
    var featureDarkTheme by remember { mutableStateOf(true) }
    var featureTracking by remember { mutableStateOf(true) }
    var customAppName by remember { mutableStateOf("Nova Shop") }

    // Interactive Checklist state
    var checkIdea by remember { mutableStateOf(true) }
    var checkPrompt by remember { mutableStateOf(true) }
    var checkPreview by remember { mutableStateOf(false) }
    var checkCode by remember { mutableStateOf(false) }
    var checkPublishReady by remember { mutableStateOf(false) }

    val stages = remember {
        listOf(
            TutorialStage(
                stageNumber = 1,
                titleAr = "صياغة فكرة التطبيق والأمر الذكي (Prompt)",
                subtitleAr = "كيف تحول الفكرة في ذهنك إلى مواصفات واضحة يفهمها الذكاء الاصطناعي",
                durationMin = "3 دقائق",
                icon = Icons.Default.Lightbulb,
                color = Color(0xFFF59E0B),
                keyPoints = listOf(
                    "تحديد اسم التطبيق والفئة الرئيسية بدقة (متجر، عيادات، مالية، إنتاجية)",
                    "سرد الشاشات الأساسية (الرئيسية، التفاصيل، السلة، الحساب، الإحصائيات)",
                    "تحديد نوع التفاعل وعمليات الإدخال والأزرار (حفظ، حذف، تعديل، حساب)",
                    "ذكر نمط الألوان والهوية البصرية المفضلة (Material 3، نمط داكن/فاتح)"
                ),
                deepExplanation = "صناعة التطبيق تبدأ دائماً بـ Prompt متماسك ودقيق. بدلاً من كتابة أمر مبهم مثل 'اصنع لي تطبيق'، اكتب أمراً متكاملاً يحدد هوية التطبيق ومكوناته: 'اصنع لي تطبيق متجر إلكتروني باسم NovaStore يحتوي على سلة مشتريات ديناميكية، تصنيف للمنتجات، بوابات دفع إلكتروني، تتبع حالة الشحنات، ونظام إشعارات مع دعم الوضع الليلي وتصميم Material 3 باللون النيلي'.",
                samplePrompt = "اصنع تطبيق متجر إلكتروني ذكي وسلة مشتريات متكاملة مع تصنيفات للمنتجات، بوابات دفع فوري، شاشة تفاصيل المنتج، وتتبع حالة الشحنات مع واجهات Material 3 حديثة",
                techSpecs = "Prompt Framework: [App Name] + [Primary Domain] + [Core Features & Screens] + [Data Management] + [Visual Theme & Haptics]"
            ),
            TutorialStage(
                stageNumber = 2,
                titleAr = "هندسة الواجهات والمعمارية (Jetpack Compose)",
                subtitleAr = "بناء واجهات تفاعلية 60 FPS وإدارة البيانات محلياً بـ Room",
                durationMin = "5 دقائق",
                icon = Icons.Default.Layers,
                color = Color(0xFF6366F1),
                keyPoints = listOf(
                    "اعتماد مكونات Material 3 الرسمية (Scaffold, TopAppBar, Card, NavigationBar)",
                    "إدارة الحالة برمجياً باستخدام ViewModel و StateFlow و remember",
                    "حفظ وتخزين بيانات المستخدم محلياً داخل قاعدة بيانات SQLite عبر Room",
                    "دعم التجاوب مع مختلف أحجام الشاشات وتفعيل ردود الفعل اللمسية (Haptics)"
                ),
                deepExplanation = "تطبيقات أندرويد الحديثة تُبنى بواسطة Jetpack Compose، وهي مكتبة إعلانية (Declarative) فائقة القوة. يقوم النظام بتوليد شاشات منفصلة مرتبطة بـ ViewModel واحد يدير الحالة، مع دعم قاعدة بيانات Room لتخزين المشاريع والبيانات حتى لا تفقد مدخلاتك عند إغلاق التطبيق.",
                samplePrompt = "تطبيق مهام وإنتاجية ذكي مع نسب إنجاز بيانية وتصنيف أولويات المهام وحفظ تلقائي في Room Database",
                techSpecs = "Stack: Kotlin 2.0+ | Jetpack Compose M3 | Room Database | StateFlow Reactive Architecture"
            ),
            TutorialStage(
                stageNumber = 3,
                titleAr = "الربط بالسيرفرات السحابية العالمية الفائقة",
                subtitleAr = "تشغيل التطبيق بأعلى سرعة ونطاق ترددي 100 Gbps",
                durationMin = "2 دقيقة",
                icon = Icons.Default.CloudSync,
                color = Color(0xFF06B6D4),
                keyPoints = listOf(
                    "شبكة تضم 8 مراكز بيانات وسيرفرات حوسبية حول العالم",
                    "زمن استجابة فائق (Ultra-Low Ping أقل من 30ms)",
                    "تفعيل وضع التوربو (Turbo Mode) لتسريع معالجة العمليات السحابية",
                    "مزامنة مستمرة تضمن عمل التطبيق بكفاءة متناهية"
                ),
                deepExplanation = "التطبيق مدعوم بشبكة عالمية من السيرفرات السحابية (Global Server Nodes) في فرانكفورت، طوكيو، لندن، سيليكون فالي، ودبي وغيرها. يتم تحسين الاتصال تلقائياً باختيار السيرفر الأقرب إليك لتقديم تجربة تشغيل فائقة السرعة والاستجابة.",
                samplePrompt = "تطبيق محفظة مصروفات واستثمار ذكي متصل بسيرفر سحابي لتحليل النفقات والميزانية الشهرية",
                techSpecs = "Network: Global CDN Grid | Tier-1 Server Nodes | Bandwidth: 100 Gbps | Low-Latency TLS 1.3"
            ),
            TutorialStage(
                stageNumber = 4,
                titleAr = "المعاينة والتجربة الحية في المحاكي (Live Sandbox)",
                subtitleAr = "فحص كافة الشاشات والأزرار واستعراض الأكواد النظيفة مجاناً",
                durationMin = "4 دقائق",
                icon = Icons.Default.PlayCircle,
                color = Color(0xFF10B981),
                keyPoints = listOf(
                    "تجربة تفاعلية حية كاملة داخل محاكي الهاتف الحقيقي بمعدل 60 إطاراً في الثانية",
                    "اختبار الضغط على الأزرار، إضافة المنتجات للسلة، وإجراء الحسابات",
                    "استعراض كود Kotlin Jetpack Compose النظيف القابل للتصدير",
                    "استعراض كود Flutter المكافئ الجاهز للتشغيل على iOS و Android",
                    "التجربة والمعاينة مجانية 100% بدون أي رسوم مسبقة"
                ),
                deepExplanation = "شاشة المحاكي الحي (Live Sandbox Screen) تتيح لك تشغيل تطبيقك في بيئة محاكاة فورية. يمكنك إضافة عناصر، حذفها، واختبار سلوك الواجهات. كما يمكنك الانتقال لتبويب 'كود Kotlin' لمشاهدة الكود الأصلي وتنزيله والتحقق من جودته.",
                samplePrompt = "تطبيق حجز عيادات طبية وجدول مواعيد الأطباء مع تأكيد الحجز وإشعارات التنبيهات",
                techSpecs = "Sandbox: Real-time UI Emulator | 60 FPS Native Rendering | Interactive State Machine"
            ),
            TutorialStage(
                stageNumber = 5,
                titleAr = "دفع 5$ ونشر التطبيق فورياً على كافة المنصات",
                subtitleAr = "رفع التطبيق لـ Google Play و Apple App Store و Web PWA و Huawei",
                durationMin = "3 دقائق",
                icon = Icons.Default.RocketLaunch,
                color = Color(0xFFEC4899),
                keyPoints = listOf(
                    "لا يوجد اشتراك شهري؛ الدفع (5$ فقط) لمرة واحدة عند اكتمال التطبيق ومعاينته",
                    "توليد حزمة Google Play الرسمية (Android App Bundle - AAB) و APK جاهز",
                    "توليد شهادة التوقيع الرقمية الخاصة بتطبيقك (Keystore SHA-256)",
                    "توليد حزمة Apple App Store (iOS Package) للاختبار والنشر الرسمي",
                    "رابط استضافة سحابي فوري لنشر التطبيق كـ Web PWA يعمل على أي متصفح",
                    "دعم مباشر لمتاجر Huawei AppGallery و Amazon Appstore"
                ),
                deepExplanation = "بمجرد أن تعاين تطبيقك وتتأكد من مطابقته الكاملة لرغبتك، تضغط على زر 'دفع 5$ ورفع التطبيق في كل المنصات'. يقوم النظام السحابي فوراً ببناء الحزم النهائية وتوقيعها بالمفاتيح الرقمية، ثم يمنحك روابط التحميل وملفات النشر المباشرة للمتاجر العالمية.",
                samplePrompt = "تطبيق تتبع لياقة وصحة شرب الماء وحساب السعرات مع إحصائيات يومية وأسبوعية",
                techSpecs = "Publishing Pipeline: Android AAB + APK (v1/v2/v3 Signing) | iOS Bundle | Web PWA Instant CDN | Multi-Store Compliance"
            )
        )
    }

    val faqs = remember {
        listOf(
            TutorialFaq(
                question = "هل أحتاج لمعرفة لغات البرمجة لصناعة تطبيق كامل؟",
                answer = "لا على الإطلاق! النظام يقوم بكتابة الأكواد بالكامل (Kotlin و Flutter)، وهندسة الواجهات، وقواعد البيانات بناءً على وصفك في الـ Prompt. كل ما تحتاجه هو صياغة فكرتك بوضوح."
            ),
            TutorialFaq(
                question = "هل إنشاء التطبيق ومعاينته يكلف أي رسوم مسبقة؟",
                answer = "لا! الإنشاء والمعاينة والتجربة داخل المحاكي مجانية 100% لجميع المستخدمين. لا يوجد أي اشتراك شهري مسبق. الرسوم الوحيدة هي 5$ تُدفع فقط عندما يكتمل تطبيقك وتقرر نشره على المتاجر العالمية."
            ),
            TutorialFaq(
                question = "ما المنصات التي يمكن رفع التطبيق عليها؟",
                answer = "التطبيق يتم تجهيزه للنشر الفوري على: متجر Google Play (ملف AAB رسمي)، متجر Apple App Store (حزمة iOS)، تطبيق ويب فوري PWA يعمل على المتصفحات، بالإضافة إلى متجر Huawei AppGallery ومتجر Amazon."
            ),
            TutorialFaq(
                question = "كيف أضمن قبول تطبيقي في متجر Google Play و App Store؟",
                answer = "يقوم النظام بتوليد أكواد متوافقة 100% مع أحدث سياسات أمان جوجل (Google Play Policy) وواجهات Material 3 الحديثة، مع توليد مفاتيح توقيع آمنة Keystore وتوفير سياسة خصوصية مدمجة."
            ),
            TutorialFaq(
                question = "هل يمكنني تعديل التطبيق بعد إنشائه؟",
                answer = "نعم بكل سهولة! يمكنك استنساخ المشروع، إعادة توليد ميزات جديدة عبر المنشئ، أو الاستعانة بمهندس مبرمج حقيقي للتطوير المخصص."
            )
        )
    }

    // Generated prompt dynamically built from interactive builder
    val generatedPrompt = remember(selectedAppCategory, featurePayment, featureDatabase, featureDarkTheme, featureTracking, customAppName) {
        val features = mutableListOf<String>()
        if (featurePayment) features.add("بوابات دفع إلكتروني وسداد فوري")
        if (featureDatabase) features.add("حفظ البيانات محلياً في Room Database")
        if (featureDarkTheme) features.add("دعم الوضعين الداكن والفاتح بتصميم Material 3")
        if (featureTracking) features.add("إحصائيات ورسوم بيانية تفاعلية")

        "اصنع لي تطبيق $selectedAppCategory متكامل باسم '$customAppName' مع واجهات عصرية تتضمن: " +
                features.joinToString("، ") +
                "، مع ردود فعل لمسية وتجربة مستخدم سلسة بمعدل 60 FPS جاهز للنشر على كافة المتاجر"
    }

    Scaffold(
        containerColor = Color(0xFF0F172A),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🎓", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "دليل إنشاء التطبيق بالكامل",
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp
                            )
                        }
                        Text(
                            text = "من الفكرة إلى النشر العالمي بـ 5$ • خطوة بخطوة",
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
                    Button(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onStartBuildingWithPrompt(generatedPrompt)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(end = 8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.RocketLaunch, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("ابدأ البناء 🚀", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1E293B))
            )
        }
    ) { innerPadding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Academy Banner
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .border(
                            1.5.dp,
                            Brush.horizontalGradient(listOf(Color(0xFF6366F1), Color(0xFF10B981), Color(0xFF06B6D4))),
                            RoundedCornerShape(20.dp)
                        ),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF10B981).copy(alpha = 0.2f))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("دليل شامل معتمد 📚", color = Color(0xFF10B981), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            Text("الوقت الإجمالي: 15 دقيقة", color = Color(0xFF94A3B8), fontSize = 11.sp)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "كيف تصنع تطبيقك الأول وترفعه للمتاجر العالمية بنجاح؟",
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            lineHeight = 24.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "تعلم المنهجية الكاملة المتبعة من كبار مهندسي البرمجيات: من صياغة الأمر الذكي (Prompt)، مروراً بهندسة الواجهات وقواعد البيانات، وحتى المعاينة المجانية في المحاكي والدفع (5$) للنشر على Google Play و App Store.",
                            color = Color(0xFFCBD5E1),
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    onNavigateToSandbox()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.PhoneAndroid, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("المحاكي المباشر 📱", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    onNavigateToDeveloper()
                                },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f),
                                border = BorderStroke(1.dp, Color(0xFFF59E0B)),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFF59E0B))
                            ) {
                                Icon(Icons.Default.SupportAgent, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("مبرمج حقيقي 👨‍💻", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Quick Stage Selector Tabs
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "📌 مراحل دورة حياة التطبيق الـ 5:",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(vertical = 2.dp)
                    ) {
                        items(stages) { stage ->
                            val isSelected = selectedStageIndex == stage.stageNumber - 1
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    selectedStageIndex = stage.stageNumber - 1
                                    expandedStageId = stage.stageNumber
                                },
                                label = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("${stage.stageNumber}. ", fontWeight = FontWeight.Bold)
                                        Text(stage.titleAr, maxLines = 1)
                                    }
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = stage.color,
                                    selectedLabelColor = if (stage.color == Color(0xFFF59E0B) || stage.color == Color(0xFF10B981)) Color.Black else Color.White,
                                    containerColor = Color(0xFF1E293B),
                                    labelColor = Color(0xFFCBD5E1)
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    borderColor = if (isSelected) stage.color else Color(0xFF334155),
                                    enabled = true,
                                    selected = isSelected
                                ),
                                modifier = Modifier.heightIn(min = 40.dp)
                            )
                        }
                    }
                }
            }

            // The 5 Detailed Learning Modules (Expandable Cards)
            items(stages) { stage ->
                val isExpanded = expandedStageId == stage.stageNumber
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(
                            1.dp,
                            if (isExpanded) stage.color else Color(0xFF334155),
                            RoundedCornerShape(16.dp)
                        ),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        // Header of the Stage Card
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    expandedStageId = if (isExpanded) 0 else stage.stageNumber
                                },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(stage.color.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = stage.icon,
                                        contentDescription = null,
                                        tint = stage.color,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "المرحلة ${stage.stageNumber}:",
                                            color = stage.color,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = stage.durationMin,
                                            color = Color(0xFF94A3B8),
                                            fontSize = 10.sp
                                        )
                                    }
                                    Text(
                                        text = stage.titleAr,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            }

                            IconButton(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    expandedStageId = if (isExpanded) 0 else stage.stageNumber
                                },
                                modifier = Modifier.minimumInteractiveComponentSize()
                            ) {
                                Icon(
                                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = null,
                                    tint = Color(0xFFCBD5E1)
                                )
                            }
                        }

                        // Subtitle
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stage.subtitleAr,
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )

                        // Expanded Content
                        AnimatedVisibility(
                            visible = isExpanded,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            Column(modifier = Modifier.padding(top = 12.dp)) {
                                HorizontalDivider(color = Color(0xFF334155))
                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = "📖 الشرح والتطبيق العملي:",
                                    color = stage.color,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = stage.deepExplanation,
                                    color = Color(0xFFCBD5E1),
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp
                                )

                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "🎯 أهم النقاط والخطوات التنفيذية:",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                stage.keyPoints.forEach { point ->
                                    Row(
                                        modifier = Modifier.padding(vertical = 2.dp),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Text("✓ ", color = stage.color, fontWeight = FontWeight.Bold)
                                        Text(text = point, color = Color(0xFFCBD5E1), fontSize = 11.sp, lineHeight = 16.sp)
                                    }
                                }

                                if (stage.samplePrompt != null) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                                        shape = RoundedCornerShape(10.dp),
                                        border = BorderStroke(1.dp, stage.color.copy(alpha = 0.5f))
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text("💬 مثال لأمر ذكي جاهز للاستخدام:", color = stage.color, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                                TextButton(
                                                    onClick = {
                                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                        clipboard.setPrimaryClip(ClipData.newPlainText("Sample Prompt", stage.samplePrompt))
                                                        Toast.makeText(context, "تم نسخ الأمر! 📋", Toast.LENGTH_SHORT).show()
                                                    },
                                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                                    modifier = Modifier.heightIn(min = 28.dp)
                                                ) {
                                                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp), tint = stage.color)
                                                    Spacer(modifier = Modifier.width(3.dp))
                                                    Text("نسخ", color = stage.color, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                            Text(
                                                text = stage.samplePrompt,
                                                color = Color(0xFFE2E8F0),
                                                fontSize = 11.sp,
                                                lineHeight = 16.sp
                                            )

                                            Spacer(modifier = Modifier.height(8.dp))

                                            Button(
                                                onClick = {
                                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                    onStartBuildingWithPrompt(stage.samplePrompt)
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = stage.color),
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .heightIn(min = 36.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.AutoFixHigh,
                                                    contentDescription = null,
                                                    tint = if (stage.color == Color(0xFFF59E0B) || stage.color == Color(0xFF10B981)) Color.Black else Color.White,
                                                    modifier = Modifier.size(15.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "تطبيق هذا الأمر فوراً في المنشئ ⚡",
                                                    color = if (stage.color == Color(0xFFF59E0B) || stage.color == Color(0xFF10B981)) Color.Black else Color.White,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }

                                if (stage.techSpecs != null) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "⚙️ المعمارية التقنية: ${stage.techSpecs}",
                                        color = Color(0xFF64748B),
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Interactive Prompt Generator Tool (أداة تفاعلية لتوليد الأوامر)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .border(
                            1.5.dp,
                            Brush.horizontalGradient(listOf(Color(0xFF10B981), Color(0xFF06B6D4))),
                            RoundedCornerShape(18.dp)
                        ),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🛠️", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "أداة صانع الأوامر الذكية التفاعلية (Prompt Generator)",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "اختر المواصفات، وسنصيغ لك الأمر البرمجي الاحترافي فوراً",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 10.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Category Selection
                        Text("1. نوع التطبيق:", color = Color(0xFFCBD5E1), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        val categories = listOf("متجر إلكتروني", "حجز عيادات", "محفظة مالية", "مهام وإنتاجية", "لياقة وصحة", "مساعد ذكي")
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(categories) { cat ->
                                val isCatSelected = selectedAppCategory == cat
                                FilterChip(
                                    selected = isCatSelected,
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        selectedAppCategory = cat
                                    },
                                    label = { Text(cat, fontSize = 10.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFF10B981),
                                        selectedLabelColor = Color.Black,
                                        containerColor = Color(0xFF0F172A),
                                        labelColor = Color(0xFF94A3B8)
                                    ),
                                    modifier = Modifier.heightIn(min = 34.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Features checkboxes
                        Text("2. الميزات المطلوبة في التطبيق:", color = Color(0xFFCBD5E1), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { featurePayment = !featurePayment },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(checked = featurePayment, onCheckedChange = { featurePayment = it })
                                Text("💳 بوابات دفع فوري", color = Color.White, fontSize = 10.sp)
                            }
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { featureDatabase = !featureDatabase },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(checked = featureDatabase, onCheckedChange = { featureDatabase = it })
                                Text("🗄️ حفظ Room محلي", color = Color.White, fontSize = 10.sp)
                            }
                        }

                        Row(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { featureDarkTheme = !featureDarkTheme },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(checked = featureDarkTheme, onCheckedChange = { featureDarkTheme = it })
                                Text("🎨 Material 3 وداكن", color = Color.White, fontSize = 10.sp)
                            }
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { featureTracking = !featureTracking },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(checked = featureTracking, onCheckedChange = { featureTracking = it })
                                Text("📊 رسوم وإحصائيات", color = Color.White, fontSize = 10.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Live Result Box
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("📝 الأمر البرمجي الناتج:", color = Color(0xFF10B981), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = generatedPrompt,
                                    color = Color(0xFFE2E8F0),
                                    fontSize = 11.sp,
                                    lineHeight = 16.sp
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            onStartBuildingWithPrompt(generatedPrompt)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1.3f)
                                    ) {
                                        Icon(Icons.Default.RocketLaunch, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("إنشاء هذا التطبيق الآن 🚀", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            clipboard.setPrimaryClip(ClipData.newPlainText("Generated Prompt", generatedPrompt))
                                            Toast.makeText(context, "تم نسخ الأمر إلى الحافظة! 📋", Toast.LENGTH_SHORT).show()
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(0.7f),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFCBD5E1))
                                    ) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text("نسخ", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Interactive Pre-Launch Checklist (قائمة التحقق قبل النشر)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    border = BorderStroke(1.dp, Color(0xFF334155))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("📋", fontSize = 18.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("قائمة التحقق قبل النشر (Pre-Launch Checklist)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            val completedCount = listOf(checkIdea, checkPrompt, checkPreview, checkCode, checkPublishReady).count { it }
                            Text("$completedCount / 5 مكتمل", color = Color(0xFF10B981), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        val checkItems = listOf(
                            Triple("1. صياغة الفكرة وتحديد اسم ووظيفة التطبيق", checkIdea) { checkIdea = !checkIdea },
                            Triple("2. توليد المشروع في المنشئ الذكي", checkPrompt) { checkPrompt = !checkPrompt },
                            Triple("3. تجربة الأزرار والمحاكي التفاعلي والتأكد من الاستجابة", checkPreview) { checkPreview = !checkPreview },
                            Triple("4. مراجعة كود Kotlin و Flutter والتأكد من خلوه من الأخطاء", checkCode) { checkCode = !checkCode },
                            Triple("5. الاستعداد للدفع (5$) ورفع التطبيق لكافة المتاجر الرسمية", checkPublishReady) { checkPublishReady = !checkPublishReady }
                        )

                        checkItems.forEach { (text, isChecked, onToggle) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onToggle() }
                                    .padding(vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = { onToggle() },
                                    colors = CheckboxDefaults.colors(checkedColor = Color(0xFF10B981))
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = text,
                                    color = if (isChecked) Color(0xFFE2E8F0) else Color(0xFF94A3B8),
                                    fontSize = 11.sp,
                                    fontWeight = if (isChecked) FontWeight.Medium else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }

            // FAQ Accordion
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "❓ الأسئلة الشائعة حول صناعة التطبيقات:",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    faqs.forEach { faq ->
                        var isExpanded by remember { mutableStateOf(false) }
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    isExpanded = !isExpanded
                                },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                            border = BorderStroke(1.dp, if (isExpanded) Color(0xFF6366F1).copy(alpha = 0.5f) else Color(0xFF334155))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = faq.question,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Icon(
                                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                        contentDescription = null,
                                        tint = Color(0xFF94A3B8)
                                    )
                                }

                                AnimatedVisibility(visible = isExpanded) {
                                    Column(modifier = Modifier.padding(top = 8.dp)) {
                                        HorizontalDivider(color = Color(0xFF334155))
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = faq.answer,
                                            color = Color(0xFFCBD5E1),
                                            fontSize = 11.sp,
                                            lineHeight = 16.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Bottom CTA Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "جاهز لتطبيق ما تعلمته وبناء تطبيقك الآن؟ 🚀",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "ابدأ بكتابة فكرتك في المنشئ الذكي مجاناً، وعاينها مباشرة في المحاكي!",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onStartBuildingWithPrompt(generatedPrompt)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 46.dp)
                        ) {
                            Icon(Icons.Default.RocketLaunch, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("فتح المنشئ والبدء في بناء تطبيقي 📱", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
