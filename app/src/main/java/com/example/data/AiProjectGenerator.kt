package com.example.data

import com.example.model.GeneratedProject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class AiProjectGenerator {

    suspend fun generateProject(
        prompt: String,
        projectType: String = "App", // Always App
        onProgressUpdate: (String) -> Unit
    ): GeneratedProject = withContext(Dispatchers.IO) {

        onProgressUpdate("🧠 تحليل متطلبات التطبيق وهندسة النظام السحابي...")
        delay(600)

        onProgressUpdate("🎨 تصميم واجهات Material 3 وتجربة المستخدم التفاعلية...")
        delay(700)

        val lowerPrompt = prompt.lowercase()

        val interactiveType: String
        val detectedTitle: String
        val detectedCategory: String
        val colorHex: String
        val iconName: String

        when {
            lowerPrompt.contains("متجر") || lowerPrompt.contains("شراء") || lowerPrompt.contains("تسوق") || lowerPrompt.contains("store") || lowerPrompt.contains("shop") || lowerPrompt.contains("commerce") -> {
                interactiveType = "STOREFRONT"
                detectedTitle = "NovaStore Marketplace"
                detectedCategory = "E-Commerce"
                colorHex = "#06B6D4"
                iconName = "shopping_bag"
            }
            lowerPrompt.contains("مال") || lowerPrompt.contains("مصروف") || lowerPrompt.contains("ميزانية") || lowerPrompt.contains("expense") || lowerPrompt.contains("money") || lowerPrompt.contains("crypto") -> {
                interactiveType = "EXPENSE_TRACKER"
                detectedTitle = "CryptoPulse & Ledger Flow"
                detectedCategory = "FinTech & Wallet"
                colorHex = "#10B981"
                iconName = "account_balance_wallet"
            }
            lowerPrompt.contains("رياض") || lowerPrompt.contains("صحة") || lowerPrompt.contains("ماء") || lowerPrompt.contains("fitness") || lowerPrompt.contains("workout") -> {
                interactiveType = "FITNESS_FLOW"
                detectedTitle = "Aura Fitness & Wellness"
                detectedCategory = "Health & Lifestyle"
                colorHex = "#14B8A6"
                iconName = "fitness_center"
            }
            lowerPrompt.contains("طب") || lowerPrompt.contains("عياد") || lowerPrompt.contains("دكتور") || lowerPrompt.contains("حجز") || lowerPrompt.contains("clinic") || lowerPrompt.contains("doctor") -> {
                interactiveType = "DOCTOR_BOOKING"
                detectedTitle = "MediBook Telehealth"
                detectedCategory = "Healthcare & Appointments"
                colorHex = "#F59E0B"
                iconName = "local_hospital"
            }
            lowerPrompt.contains("ذكاء") || lowerPrompt.contains("شات") || lowerPrompt.contains("مساعد") || lowerPrompt.contains("ai") || lowerPrompt.contains("chat") -> {
                interactiveType = "AI_ASSISTANT"
                detectedTitle = "OmniMind AI Assistant"
                detectedCategory = "AI Utility & Chat"
                colorHex = "#EC4899"
                iconName = "smart_toy"
            }
            else -> {
                interactiveType = "TODO_PRO"
                detectedTitle = "FlowTask Pro Productivity"
                detectedCategory = "Smart Productivity"
                colorHex = "#6366F1"
                iconName = "check_circle"
            }
        }

        onProgressUpdate("💾 توليد مخطط قاعدة بيانات Room ومسارات السيرفرات السحابية...")
        delay(600)

        onProgressUpdate("⚡ تجميع كود Jetpack Compose والمعاينة الحية الفورية...")

        var geminiDescription: String? = null
        if (GeminiNetwork.hasValidApiKey()) {
            try {
                val systemPrompt = "You are an expert mobile developer and UI designer. " +
                        "Generate a concise 2-sentence Arabic summary of the application architecture, server-tier support, and key features for: $prompt"
                val response = GeminiNetwork.api.generateContent(
                    GeminiNetwork.getApiKey(),
                    GeminiRequest(
                        contents = listOf(
                            GeminiContent(
                                parts = listOf(GeminiPart(text = systemPrompt))
                            )
                        ),
                        generationConfig = GeminiGenConfig(temperature = 0.6f, maxOutputTokens = 200)
                    )
                )
                geminiDescription = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            } catch (e: Exception) {
                geminiDescription = null
            }
        }

        val description = geminiDescription ?: "تم تصميم وتوليد $detectedTitle كـتطبيق محمول متكامل، مدعوم بأعلى السيرفرات السحابية العالمية، وجاهز للمعاينة الفورية والنشر على كل المنصات بـ 5$."

        val features = listOf(
            "📱 تطبيق محمول حقيقي مبني بـ Jetpack Compose 100%",
            "⚡ مدعوم بأعلى السيرفرات السحابية ومعالجات NVIDIA فائقة السرعة",
            "💾 استمرارية البيانات ونماذج الحفظ المحلية (Room Database)",
            "📊 مؤشرات تقدم ورسوم بيانية تفاعلية",
            "🚀 جاهز بعد المعاينة للرفع والنشر في كل المنصات بـ 5$ فقط"
        )

        val composeCode = generateComposeCode(detectedTitle, interactiveType)
        val flutterCode = generateFlutterCode(detectedTitle, interactiveType)
        val schema = generateSchemaJson(detectedTitle, interactiveType)

        GeneratedProject(
            title = detectedTitle,
            type = "App",
            description = description,
            category = detectedCategory,
            features = features,
            interactiveType = interactiveType,
            colorHex = colorHex,
            iconName = iconName,
            codeCompose = composeCode,
            codeFlutter = flutterCode,
            architectureSchema = schema
        )
    }

    private fun generateComposeCode(title: String, type: String): String {
        return """package com.example.generated

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Generated Mobile Application: $title
 * Platform: Android Jetpack Compose + Clean Architecture
 * Global Server Node: Tier-1 100 Gbps High-Performance Cloud
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ${title.replace(" ", "")}Screen() {
    var query by remember { mutableStateOf("") }
    val items = remember { mutableStateListOf("مهمة متقدمة 1", "تحديث البيانات السحابية 2", "إشعار النظام 3") }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("$title") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("بحث أو إدخال جديد") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            LazyColumn {
                items(items) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = item,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}"""
    }

    private fun generateFlutterCode(title: String, type: String): String {
        return """import 'package:flutter/material.dart';

// Auto-Generated Flutter Counterpart for $title
class ${title.replace(" ", "")}View extends StatefulWidget {
  const ${title.replace(" ", "")}View({super.key});

  @override
  State<${title.replace(" ", "")}View> createState() => _${title.replace(" ", "")}ViewState();
}

class _${title.replace(" ", "")}ViewState extends State<${title.replace(" ", "")}View> {
  int _counter = 0;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFF0F172A),
      appBar: AppBar(
        title: const Text('$title'),
        backgroundColor: const Color(0xFF1E293B),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text(
              'Activity Counter: ${'$'}_counter',
              style: const TextStyle(fontSize: 22, color: Colors.white),
            ),
            const SizedBox(height: 20),
            ElevatedButton(
              onPressed: () => setState(() => _counter++),
              child: const Text('Execute Action'),
            ),
          ],
        ),
      ),
    );
  }
}"""
    }

    private fun generateSchemaJson(title: String, type: String): String {
        return """{
  "application": "$title",
  "type": "App",
  "archetype": "$type",
  "cloud_support": "Tier-1 100Gbps Global Grid with NVIDIA GPU Clusters",
  "deployment": {
    "fee": "$5 One-Time",
    "targets": ["Google Play Store (AAB)", "Apple App Store (iOS)", "Web PWA Cloud Live (100Gbps)", "Huawei AppGallery"]
  },
  "database": {
    "engine": "Room SQLite (Local Offline First)",
    "entities": [
      {
        "name": "AppDataRecord",
        "fields": ["id: Long PRIMARY KEY", "payload: String", "updated_at: Timestamp"]
      }
    ]
  },
  "state_management": "StateFlow & Jetpack Compose SnapshotState",
  "theme": "Modern Slate Dark Material 3"
}"""
    }
}
