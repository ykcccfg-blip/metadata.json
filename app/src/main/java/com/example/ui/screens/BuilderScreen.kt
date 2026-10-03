package com.example.ui.screens

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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ChatMessage
import com.example.model.GeneratedProject
import com.example.ui.viewmodel.AppCreatorViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuilderScreen(
    initialProjectType: String,
    initialPrompt: String? = null,
    viewModel: AppCreatorViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToSandbox: () -> Unit
) {
    var projectType by remember { mutableStateOf(initialProjectType) }
    var promptInput by remember { mutableStateOf(initialPrompt ?: "") }

    val messages by viewModel.messages.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val generationStep by viewModel.generationStep.collectAsState()
    val quotaState by viewModel.quotaState.collectAsState()

    val servers by viewModel.servers.collectAsState()
    val selectedServerId by viewModel.selectedServerId.collectAsState()
    val isTurboEnabled by viewModel.isTurboEnabled.collectAsState()
    val activeServer = servers.find { it.id == selectedServerId } ?: servers.first()
    var showServerDialog by remember { mutableStateOf(false) }

    if (showServerDialog) {
        GlobalServersDialog(
            viewModel = viewModel,
            onDismiss = { showServerDialog = false }
        )
    }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Scroll to bottom when new messages arrive
    LaunchedEffect(messages.size, isGenerating) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    // Auto-send if initial prompt was provided
    LaunchedEffect(Unit) {
        if (!initialPrompt.isNullOrBlank()) {
            if (viewModel.canGenerateApp()) {
                viewModel.sendUserPrompt(initialPrompt, projectType)
                promptInput = ""
            } else {
                viewModel.openSubscriptionModal()
            }
        }
    }

    val appSuggestions = listOf(
        "تطبيق متجر إلكتروني مع سلة تسوق ومحاسبة ودفع فوري 🛍️",
        "تطبيق تنظيم مهام يومية وعادات مع تتبع نسب الإنجاز 📋",
        "تطبيق لياقة بدنية لحساب السعرات ومتابعة شرب الماء 💧",
        "تطبيق محفظة مالية ذكية وتسجيل المصروفات الشهرية 💰",
        "تطبيق حجز عيادات طبية وجدول مواعيد الأطباء 🩺",
        "تطبيق مساعد محادثة ذكي متعدد التخصصات 🤖"
    )

    val currentSuggestions = appSuggestions

    Scaffold(
        containerColor = Color(0xFF0F172A),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "صانع التطبيقات الذكية الفائقة",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
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
                actions = {
                    // Global Server Node Chip
                    Surface(
                        onClick = { showServerDialog = true },
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF1E293B),
                        border = BorderStroke(1.dp, Color(0xFF06B6D4).copy(alpha = 0.5f)),
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(activeServer.flag, fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("${activeServer.pingMs}ms", color = Color(0xFF06B6D4), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            if (isTurboEnabled) {
                                Spacer(modifier = Modifier.width(2.dp))
                                Text("⚡", fontSize = 9.sp)
                            }
                        }
                    }

                    // App Only Badge
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF0F172A),
                        border = BorderStroke(1.dp, Color(0xFF6366F1).copy(alpha = 0.5f)),
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Text(
                            text = "📱 تطبيقات فقط",
                            color = Color(0xFF818CF8),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    // 5$ Multi-Platform Publish Button
                    Button(
                        onClick = { viewModel.openPublishDialog() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(end = 4.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.CloudUpload, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("نشر (5$) 🚀", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                    }

                    // Live Sandbox Preview Button (Green Play Circle matching Flutter spec)
                    IconButton(
                        onClick = onNavigateToSandbox,
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayCircle,
                            contentDescription = "معاينة حية",
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(32.dp)
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
            // Quota Status Strip: Free creation & preview, $5 to publish on all stores
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1E293B).copy(alpha = 0.85f))
                    .clickable { viewModel.openPublishDialog() }
                    .padding(horizontal = 14.dp, vertical = 7.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CloudDone,
                        contentDescription = null,
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "صناعة ومعاينة التطبيقات مجانية • نشر في كل المنصات بـ 5$",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF10B981).copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "5$ لكل المنصات 🚀",
                        color = Color(0xFF10B981),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Chat Messages Feed
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(messages, key = { it.id }) { msg ->
                    ChatBubble(
                        message = msg,
                        onOpenSandbox = { artifact ->
                            viewModel.setSandboxProject(artifact)
                            onNavigateToSandbox()
                        },
                        onPublishApp = { artifact ->
                            viewModel.openPublishDialog(artifact)
                        }
                    )
                }

                // Generating animation state
                if (isGenerating) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    CircularProgressIndicator(
                                        color = Color(0xFF6366F1),
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.5.dp
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = generationStep.ifBlank { "الذكاء الاصطناعي يكتب الأكواد وينشئ التصاميم..." },
                                        color = Color(0xFFCBD5E1),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                LinearProgressIndicator(
                                    modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape),
                                    color = Color(0xFF10B981),
                                    trackColor = Color(0xFF334155)
                                )
                            }
                        }
                    }
                }
            }

            // Quick suggestion chips bar
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F172A))
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(currentSuggestions) { sug ->
                    SuggestionChip(
                        onClick = {
                            promptInput = sug
                        },
                        label = { Text(sug, fontSize = 11.sp, color = Color(0xFF94A3B8)) },
                        colors = SuggestionChipDefaults.suggestionChipColors(containerColor = Color(0xFF1E293B)),
                        border = SuggestionChipDefaults.suggestionChipBorder(enabled = true, borderColor = Color(0xFF334155))
                    )
                }
            }

            // Bottom Input bar matching Flutter spec
            ContainerBottomInput(
                prompt = promptInput,
                isGenerating = isGenerating,
                onPromptChanged = { promptInput = it },
                onSend = {
                    if (promptInput.isNotBlank()) {
                        if (!viewModel.canGenerateApp()) {
                            viewModel.openSubscriptionModal()
                        } else {
                            val text = promptInput.trim()
                            promptInput = ""
                            viewModel.sendUserPrompt(text, projectType)
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun ChatBubble(
    message: ChatMessage,
    onOpenSandbox: (GeneratedProject) -> Unit,
    onPublishApp: (GeneratedProject) -> Unit
) {
    val isUser = message.sender == "user"

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF6366F1).copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.SmartToy, contentDescription = "AI", tint = Color(0xFF818CF8), modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            modifier = Modifier.widthIn(max = 300.dp),
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topStart = 14.dp,
                            topEnd = 14.dp,
                            bottomStart = if (isUser) 14.dp else 2.dp,
                            bottomEnd = if (isUser) 2.dp else 14.dp
                        )
                    )
                    .background(if (isUser) Color(0xFF6366F1) else Color(0xFF1E293B))
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Text(
                    text = message.text,
                    color = Color.White,
                    fontSize = 13.sp,
                    lineHeight = 19.sp
                )
            }

            // Artifact Action Button if AI delivered a project
            if (message.isArtifact && message.artifact != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(12.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF10B981))),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "التطبيق جاهز للمعاينة والنشر!",
                                color = Color(0xFF10B981),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Button(
                                onClick = { onOpenSandbox(message.artifact) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF06B6D4)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.PlayCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("معاينة 📱", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.Black)
                            }

                            Button(
                                onClick = { onPublishApp(message.artifact) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1.2f),
                                contentPadding = PaddingValues(vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Black)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("نشر (5$) 🚀", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.Black)
                            }
                        }
                    }
                }
            }
        }

        if (isUser) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF334155)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = "User", tint = Color.White, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun ContainerBottomInput(
    prompt: String,
    isGenerating: Boolean,
    onPromptChanged: (String) -> Unit,
    onSend: () -> Unit
) {
    val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current

    Surface(
        color = Color(0xFF1E293B),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = prompt,
                onValueChange = onPromptChanged,
                placeholder = {
                    Text(
                        text = "اكتب تفاصيل فكرة تطبيقك الذكي هنا...",
                        color = Color(0xFF94A3B8),
                        fontSize = 13.sp
                    )
                },
                trailingIcon = {
                    if (prompt.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
                                onPromptChanged("")
                            },
                            modifier = Modifier.minimumInteractiveComponentSize()
                        ) {
                            Icon(Icons.Default.Clear, contentDescription = "مسح", tint = Color(0xFF94A3B8))
                        }
                    }
                },
                modifier = Modifier.weight(1f),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color(0xFF6366F1),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                maxLines = 3
            )

            IconButton(
                onClick = {
                    haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                    onSend()
                },
                enabled = prompt.isNotBlank() && !isGenerating,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(if (prompt.isNotBlank() && !isGenerating) Color(0xFF6366F1) else Color(0xFF334155))
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}
