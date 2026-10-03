package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AppCreatorViewModel

sealed class ScreenDestination {
    data object Home : ScreenDestination()
    data class Builder(val projectType: String = "App", val initialPrompt: String? = null) : ScreenDestination()
    data object Sandbox : ScreenDestination()
    data object Projects : ScreenDestination()
    data object Store : ScreenDestination()
    data object Developer : ScreenDestination()
    data object Tutorial : ScreenDestination()
    data object Counters : ScreenDestination()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val viewModel: AppCreatorViewModel = viewModel()
                var currentScreen by remember { mutableStateOf<ScreenDestination>(ScreenDestination.Home) }
                var showSettings by remember { mutableStateOf(false) }
                val showSubscriptionModal by viewModel.showSubscriptionModal.collectAsState()
                val showPublishDialog by viewModel.showPublishDialog.collectAsState()
                val projectToPublish by viewModel.projectToPublish.collectAsState()
                val haptic = LocalHapticFeedback.current

                val selectedIndex = when (currentScreen) {
                    is ScreenDestination.Home -> 0
                    is ScreenDestination.Builder -> 1
                    is ScreenDestination.Sandbox -> 2
                    is ScreenDestination.Projects -> 3
                    is ScreenDestination.Developer -> 4
                    is ScreenDestination.Store -> -1
                    is ScreenDestination.Tutorial -> -1
                    is ScreenDestination.Counters -> -1
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = Color(0xFF0F172A),
                    bottomBar = {
                        NavigationBar(
                            containerColor = Color(0xFF1E293B),
                            tonalElevation = 8.dp,
                            modifier = Modifier.heightIn(min = 68.dp)
                        ) {
                            // 1. Home
                            NavigationBarItem(
                                selected = selectedIndex == 0,
                                onClick = {
                                    if (selectedIndex != 0) {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        currentScreen = ScreenDestination.Home
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.Home,
                                        contentDescription = "الرئيسية",
                                        modifier = Modifier.size(22.dp)
                                    )
                                },
                                label = {
                                    Text(
                                        "الرئيسية",
                                        fontSize = 11.sp,
                                        fontWeight = if (selectedIndex == 0) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.White,
                                    selectedTextColor = Color(0xFF818CF8),
                                    indicatorColor = Color(0xFF6366F1).copy(alpha = 0.35f),
                                    unselectedIconColor = Color(0xFF94A3B8),
                                    unselectedTextColor = Color(0xFF64748B)
                                )
                            )

                            // 2. Builder
                            NavigationBarItem(
                                selected = selectedIndex == 1,
                                onClick = {
                                    if (selectedIndex != 1) {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        currentScreen = ScreenDestination.Builder("App", null)
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = "المنشئ",
                                        modifier = Modifier.size(22.dp)
                                    )
                                },
                                label = {
                                    Text(
                                        "المنشئ",
                                        fontSize = 11.sp,
                                        fontWeight = if (selectedIndex == 1) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.White,
                                    selectedTextColor = Color(0xFF818CF8),
                                    indicatorColor = Color(0xFF6366F1).copy(alpha = 0.35f),
                                    unselectedIconColor = Color(0xFF94A3B8),
                                    unselectedTextColor = Color(0xFF64748B)
                                )
                            )

                            // 3. Sandbox
                            NavigationBarItem(
                                selected = selectedIndex == 2,
                                onClick = {
                                    if (selectedIndex != 2) {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        currentScreen = ScreenDestination.Sandbox
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.PhoneAndroid,
                                        contentDescription = "المحاكي",
                                        modifier = Modifier.size(22.dp)
                                    )
                                },
                                label = {
                                    Text(
                                        "المحاكي",
                                        fontSize = 11.sp,
                                        fontWeight = if (selectedIndex == 2) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.White,
                                    selectedTextColor = Color(0xFF10B981),
                                    indicatorColor = Color(0xFF10B981).copy(alpha = 0.35f),
                                    unselectedIconColor = Color(0xFF94A3B8),
                                    unselectedTextColor = Color(0xFF64748B)
                                )
                            )

                            // 4. Projects
                            NavigationBarItem(
                                selected = selectedIndex == 3,
                                onClick = {
                                    if (selectedIndex != 3) {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        currentScreen = ScreenDestination.Projects
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.Folder,
                                        contentDescription = "المشاريع",
                                        modifier = Modifier.size(22.dp)
                                    )
                                },
                                label = {
                                    Text(
                                        "المشاريع",
                                        fontSize = 11.sp,
                                        fontWeight = if (selectedIndex == 3) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.White,
                                    selectedTextColor = Color(0xFFF59E0B),
                                    indicatorColor = Color(0xFFF59E0B).copy(alpha = 0.35f),
                                    unselectedIconColor = Color(0xFF94A3B8),
                                    unselectedTextColor = Color(0xFF64748B)
                                )
                            )

                            // 5. Real Developer 👨‍💻 ($50)
                            NavigationBarItem(
                                selected = selectedIndex == 4,
                                onClick = {
                                    if (selectedIndex != 4) {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        currentScreen = ScreenDestination.Developer
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.Engineering,
                                        contentDescription = "المبرمج",
                                        modifier = Modifier.size(22.dp)
                                    )
                                },
                                label = {
                                    Text(
                                        "المبرمج 👨‍💻",
                                        fontSize = 11.sp,
                                        fontWeight = if (selectedIndex == 4) FontWeight.ExtraBold else FontWeight.Normal
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color(0xFFF59E0B),
                                    selectedTextColor = Color(0xFFF59E0B),
                                    indicatorColor = Color(0xFFF59E0B).copy(alpha = 0.25f),
                                    unselectedIconColor = Color(0xFFF59E0B).copy(alpha = 0.7f),
                                    unselectedTextColor = Color(0xFFF59E0B).copy(alpha = 0.7f)
                                )
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = innerPadding.calculateBottomPadding())
                    ) {
                        when (val screen = currentScreen) {
                            is ScreenDestination.Home -> {
                                HomeScreen(
                                    viewModel = viewModel,
                                    onNavigateToBuilder = { type, prompt ->
                                        currentScreen = ScreenDestination.Builder(type, prompt)
                                    },
                                    onNavigateToSandbox = {
                                        currentScreen = ScreenDestination.Sandbox
                                    },
                                    onNavigateToProjects = {
                                        currentScreen = ScreenDestination.Projects
                                    },
                                    onNavigateToStore = {
                                        currentScreen = ScreenDestination.Store
                                    },
                                    onNavigateToDeveloperContact = {
                                        currentScreen = ScreenDestination.Developer
                                    },
                                    onOpenSubscriptionModal = {
                                        viewModel.openSubscriptionModal()
                                    },
                                    onOpenSettings = {
                                        showSettings = true
                                    },
                                    onNavigateToTutorial = {
                                        currentScreen = ScreenDestination.Tutorial
                                    },
                                    onNavigateToCounters = {
                                        currentScreen = ScreenDestination.Counters
                                    }
                                )
                            }

                            is ScreenDestination.Counters -> {
                                AppCountersDashboardScreen(
                                    viewModel = viewModel,
                                    onNavigateBack = {
                                        currentScreen = ScreenDestination.Home
                                    },
                                    onNavigateToBuilder = {
                                        currentScreen = ScreenDestination.Builder("App", null)
                                    },
                                    onNavigateToSandbox = {
                                        currentScreen = ScreenDestination.Sandbox
                                    },
                                    onNavigateToProjects = {
                                        currentScreen = ScreenDestination.Projects
                                    }
                                )
                            }

                            is ScreenDestination.Tutorial -> {
                                AppCreationTutorialScreen(
                                    onNavigateBack = {
                                        currentScreen = ScreenDestination.Home
                                    },
                                    onStartBuildingWithPrompt = { prompt ->
                                        currentScreen = ScreenDestination.Builder("App", prompt)
                                    },
                                    onNavigateToSandbox = {
                                        currentScreen = ScreenDestination.Sandbox
                                    },
                                    onNavigateToDeveloper = {
                                        currentScreen = ScreenDestination.Developer
                                    }
                                )
                            }

                            is ScreenDestination.Builder -> {
                                BuilderScreen(
                                    initialProjectType = screen.projectType,
                                    initialPrompt = screen.initialPrompt,
                                    viewModel = viewModel,
                                    onNavigateBack = {
                                        currentScreen = ScreenDestination.Home
                                    },
                                    onNavigateToSandbox = {
                                        currentScreen = ScreenDestination.Sandbox
                                    }
                                )
                            }

                            is ScreenDestination.Sandbox -> {
                                LiveSandboxScreen(
                                    viewModel = viewModel,
                                    onNavigateBack = {
                                        currentScreen = ScreenDestination.Home
                                    }
                                )
                            }

                            is ScreenDestination.Projects -> {
                                ProjectsScreen(
                                    viewModel = viewModel,
                                    onNavigateBack = {
                                        currentScreen = ScreenDestination.Home
                                    },
                                    onOpenInSandbox = {
                                        currentScreen = ScreenDestination.Sandbox
                                    }
                                )
                            }

                            is ScreenDestination.Store -> {
                                AssetStoreScreen(
                                    viewModel = viewModel,
                                    onNavigateBack = {
                                        currentScreen = ScreenDestination.Home
                                    },
                                    onUseTemplateInBuilder = { type, prompt ->
                                        currentScreen = ScreenDestination.Builder(type, prompt)
                                    },
                                    onLaunchSandbox = {
                                        currentScreen = ScreenDestination.Sandbox
                                    }
                                )
                            }

                            is ScreenDestination.Developer -> {
                                DeveloperContactScreen(
                                    viewModel = viewModel,
                                    onNavigateBack = {
                                        currentScreen = ScreenDestination.Home
                                    },
                                    onOpenSubscriptionModal = {
                                        viewModel.openSubscriptionModal()
                                    }
                                )
                            }
                        }

                        // Settings Dialog
                        if (showSettings) {
                            SettingsDialog(
                                viewModel = viewModel,
                                onDismiss = { showSettings = false },
                                onOpenSubscription = {
                                    showSettings = false
                                    viewModel.openSubscriptionModal()
                                },
                                onOpenDeveloperContact = {
                                    showSettings = false
                                    currentScreen = ScreenDestination.Developer
                                },
                                onOpenTutorial = {
                                    showSettings = false
                                    currentScreen = ScreenDestination.Tutorial
                                },
                                onOpenCounters = {
                                    showSettings = false
                                    currentScreen = ScreenDestination.Counters
                                }
                            )
                        }

                        // Subscription & Quota Dialog
                        if (showSubscriptionModal) {
                            SubscriptionDialog(
                                viewModel = viewModel,
                                onDismiss = { viewModel.closeSubscriptionModal() },
                                onNavigateToDeveloperContact = {
                                    currentScreen = ScreenDestination.Developer
                                }
                            )
                        }

                        // Multi-Platform Cloud Publishing Dialog ($5 on Preview Completion)
                        if (showPublishDialog) {
                            val activeProj = projectToPublish
                                ?: viewModel.currentActiveProject.collectAsState().value
                                ?: com.example.model.GeneratedProject(
                                    title = "Nova App",
                                    type = "App",
                                    description = "تطبيق حقيقي متكامل مدعوم بأقوى السيرفرات السحابية",
                                    category = "التطبيقات الذكية",
                                    features = listOf("جاهز للنشر على كافة المتاجر", "سيرفرات فائقة السرعة 100Gbps"),
                                    interactiveType = "STOREFRONT",
                                    colorHex = "#6366F1",
                                    iconName = "phone_android",
                                    codeCompose = "// Production App",
                                    codeFlutter = "// Flutter App",
                                    architectureSchema = "{}"
                                )
                            PublishMultiPlatformDialog(
                                viewModel = viewModel,
                                project = activeProj,
                                onDismiss = { viewModel.closePublishDialog() }
                            )
                        }
                    }
                }
            }
        }
    }
}
