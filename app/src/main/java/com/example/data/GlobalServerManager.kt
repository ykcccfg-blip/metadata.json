package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.GlobalServer
import com.example.model.ShowcaseItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

class GlobalServerManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("global_servers_prefs", Context.MODE_PRIVATE)

    companion object {
        const val PREF_SELECTED_SERVER = "selected_server_id"
        const val PREF_TURBO_ENABLED = "turbo_overclock_enabled"

        val DEFAULT_SERVERS = listOf(
            GlobalServer(
                id = "srv_tokyo_prime",
                name = "Tokyo Nexus Prime",
                nameAr = "طوكيو نكسس برايم",
                flag = "🇯🇵",
                country = "Japan",
                countryAr = "اليابان",
                regionCode = "asia-northeast1",
                pingMs = 12,
                bandwidthGbps = 120,
                gpuCluster = "16x NVIDIA B200 Blackwell",
                loadPercent = 28,
                uptimePercent = 99.999,
                isOptimal = true,
                descriptionAr = "أسرع استجابة فائقة لمعالجة الذكاء الاصطناعي وتوليد الأكواد في أجزاء من الثانية."
            ),
            GlobalServer(
                id = "srv_frankfurt_core",
                name = "Frankfurt Cyber-Core",
                nameAr = "فرانكفورت سايبر كور",
                flag = "🇩🇪",
                country = "Germany",
                countryAr = "ألمانيا",
                regionCode = "europe-west3",
                pingMs = 16,
                bandwidthGbps = 100,
                gpuCluster = "8x NVIDIA H100 Tensor Core",
                loadPercent = 34,
                uptimePercent = 99.998,
                isOptimal = true,
                descriptionAr = "محور السيرفرات الأوروبية الرئيسي، حماية فائقة واستقرار 100% لأضخم المشاريع."
            ),
            GlobalServer(
                id = "srv_silicon_valley",
                name = "Silicon Valley Brain",
                nameAr = "سيليكون فالي هايبر كلاود",
                flag = "🇺🇸",
                country = "USA West",
                countryAr = "أمريكا - الساحل الغربي",
                regionCode = "us-west1",
                pingMs = 22,
                bandwidthGbps = 150,
                gpuCluster = "32x NVIDIA H100 MegaPod",
                loadPercent = 45,
                uptimePercent = 100.0,
                isOptimal = false,
                descriptionAr = "المركز العصبي لموديلات الذكاء الاصطناعي العالمية وأعلى قدرة حسابية على الكوكب."
            ),
            GlobalServer(
                id = "srv_dubai_falcon",
                name = "Dubai Falcon Neural Node",
                nameAr = "دبي فالكون نيوترون",
                flag = "🇦🇪",
                country = "UAE",
                countryAr = "الإمارات العربية المتحدة",
                regionCode = "me-central1",
                pingMs = 14,
                bandwidthGbps = 100,
                gpuCluster = "8x NVIDIA H100 Tensor Core",
                loadPercent = 19,
                uptimePercent = 99.999,
                isOptimal = true,
                descriptionAr = "سيرفر الشرق الأوسط فائق السرعة بزمن وصول فوري وتكامل مباشر مع شبكات الخليج."
            ),
            GlobalServer(
                id = "srv_ashburn_east",
                name = "Ashburn Ultra-Grid",
                nameAr = "أشبيرن ألترا جريد",
                flag = "🇺🇸",
                country = "USA East",
                countryAr = "أمريكا - الساحل الشرقي",
                regionCode = "us-east4",
                pingMs = 19,
                bandwidthGbps = 100,
                gpuCluster = "8x NVIDIA H100 Tensor Core",
                loadPercent = 38,
                uptimePercent = 99.997,
                isOptimal = false,
                descriptionAr = "عاصمة الإنترنت العالمية، تمرير فوري لحزم البيانات وبنية تحتية ضخمة لا تتوقف."
            ),
            GlobalServer(
                id = "srv_london_mesh",
                name = "London Quantum Gateway",
                nameAr = "لندن كوانتم جيت واي",
                flag = "🇬🇧",
                country = "UK",
                countryAr = "المملكة المتحدة",
                regionCode = "europe-west2",
                pingMs = 18,
                bandwidthGbps = 90,
                gpuCluster = "8x NVIDIA H100 Tensor Core",
                loadPercent = 41,
                uptimePercent = 99.995,
                isOptimal = false,
                descriptionAr = "بوابة كمومية ذكية لتوليد واختبار تطبيقات الهواتف الذكية باحترافية."
            ),
            GlobalServer(
                id = "srv_singapore_ocean",
                name = "Singapore Oceanic Fiber",
                nameAr = "سنغافورة أوشيانيك فايبر",
                flag = "🇸🇬",
                country = "Singapore",
                countryAr = "سنغافورة",
                regionCode = "asia-southeast1",
                pingMs = 21,
                bandwidthGbps = 100,
                gpuCluster = "8x NVIDIA H100 Tensor Core",
                loadPercent = 29,
                uptimePercent = 99.998,
                isOptimal = false,
                descriptionAr = "حلقة وصل كابلات الألياف البحرية بين آسيا والمحيط الهادئ وموثوقية مذهلة."
            ),
            GlobalServer(
                id = "srv_sydney_aurora",
                name = "Sydney Aurora Grid",
                nameAr = "سيدني أورورا كلاود",
                flag = "🇦🇺",
                country = "Australia",
                countryAr = "أستراليا",
                regionCode = "australia-southeast1",
                pingMs = 31,
                bandwidthGbps = 80,
                gpuCluster = "8x NVIDIA A100 Tensor Core",
                loadPercent = 22,
                uptimePercent = 99.99,
                isOptimal = false,
                descriptionAr = "تغطية كاملة للقارة الأسترالية مع تخزين مؤقت حركي لزمن استجابة قياسي."
            )
        )

        val SHOWCASE_ITEMS = listOf(
            ShowcaseItem(
                id = "showcase_ecommerce_store",
                title = "NovaStore E-Commerce & Pay",
                titleAr = "🛒 متجر التجارة الإلكترونية والدفع الذكي",
                category = "E-Commerce",
                categoryAr = "التجارة والتسوق السريع",
                type = "App",
                rating = 4.99,
                activeUsers = "64.2k مستخدم",
                iconName = "shopping_bag",
                colorHex = "#06B6D4",
                descriptionAr = "متجر تسوق متكامل مع كتالوج منتجات، سلة تسوق، بوابات دفع إلكتروني، وتتبع فوري للشحنات.",
                interactiveType = "STOREFRONT",
                prompt = "تطبيق متجر إلكتروني وسلة مشتريات مع بطاقات منتجات وحساب الخصم وتأكيد الطلب والدفع"
            ),
            ShowcaseItem(
                id = "showcase_crypto_wallet",
                title = "CryptoPulse AI Portfolio",
                titleAr = "💰 محفظة الاستثمار والعملات الذكية",
                category = "FinTech Pro",
                categoryAr = "المال والأعمال الحديثة",
                type = "App",
                rating = 4.98,
                activeUsers = "58.1k مستخدم",
                iconName = "account_balance_wallet",
                colorHex = "#10B981",
                descriptionAr = "تطبيق مالي فائق لمراقبة الميزانية والاستثمارات ورسوم بيانية حية مع نصائح الذكاء الاصطناعي.",
                interactiveType = "EXPENSE_TRACKER",
                prompt = "تطبيق متطور لإدارة المصروفات والميزانية الشخصية مع بطاقات تحليل بياني وتصنيف الفئات"
            ),
            ShowcaseItem(
                id = "showcase_task_habit",
                title = "NeuroFlow Habit & Tasks",
                titleAr = "📋 إدارة الإنجاز والتركيز الذكي",
                category = "Productivity",
                categoryAr = "الإنتاجية وتطوير الذات",
                type = "App",
                rating = 4.96,
                activeUsers = "51.2k مستخدم",
                iconName = "checklist",
                colorHex = "#6366F1",
                descriptionAr = "نظام مهام وعادات يومية مبني على علم الأعصاب لمضاعفة الإنتاجية وتنظيم الوقت بدقة.",
                interactiveType = "TODO_PRO",
                prompt = "تطبيق إدارة مهام وعادات يومية مع مستويات إنجاز، إحصائيات ونظام تتبع دقيق"
            ),
            ShowcaseItem(
                id = "showcase_fitness_flow",
                title = "FitMatrix Hydration & Health",
                titleAr = "💧 متتبع الصحة واللياقة البدنية",
                category = "Health & Life",
                categoryAr = "الصحة والرشاقة اليومية",
                type = "App",
                rating = 4.97,
                activeUsers = "42.6k رياضي",
                iconName = "favorite",
                colorHex = "#14B8A6",
                descriptionAr = "عداد ذكي لاستهلاك المياه وحرق السعرات مع رسوم دائرية ومذكرات حركية مجدولة.",
                interactiveType = "FITNESS_FLOW",
                prompt = "تطبيق لياقة وصحة ذكي لتسجيل كمية الماء والتمارين اليومية مع أهداف نشاط"
            ),
            ShowcaseItem(
                id = "showcase_doctor_telehealth",
                title = "MediBook Clinic & Health",
                titleAr = "🩺 حجز العيادات والاستشارات الطبية",
                category = "Healthcare",
                categoryAr = "الرعاية والطب الرقمي",
                type = "App",
                rating = 4.95,
                activeUsers = "37.9k مريض",
                iconName = "local_hospital",
                colorHex = "#F59E0B",
                descriptionAr = "منصة حجز مواعيد الأطباء، ملفات المرضى، واستشارات الفيديو المباشرة مع تذكير الأدوية.",
                interactiveType = "DOCTOR_BOOKING",
                prompt = "تطبيق طبي لحجز مواعيد العيادات واستشارات الأطباء وتتبع الوصفات الطبية"
            ),
            ShowcaseItem(
                id = "showcase_ai_assistant",
                title = "OmniMind AI Assistant",
                titleAr = "🤖 المساعد التنفيذي والدردشة الذكية",
                category = "AI Utility",
                categoryAr = "الذكاء الاصطناعي والأعمال",
                type = "App",
                rating = 4.98,
                activeUsers = "71.4k محترف",
                iconName = "smart_toy",
                colorHex = "#EC4899",
                descriptionAr = "مساعد دردشة مدعوم بموديلات Gemini فائقة السرعة لكتابة المحتوى، تلخيص المستندات، وبرمجة المهام.",
                interactiveType = "AI_ASSISTANT",
                prompt = "تطبيق مساعد دردشة ذكي متعدد التخصصات مع نماذج ردود سريعة ومحفوظات المحادثات"
            )
        )
    }

    private val _servers = MutableStateFlow(DEFAULT_SERVERS)
    val servers: StateFlow<List<GlobalServer>> = _servers.asStateFlow()

    private val _selectedServerId = MutableStateFlow(
        prefs.getString(PREF_SELECTED_SERVER, "srv_tokyo_prime") ?: "srv_tokyo_prime"
    )
    val selectedServerId: StateFlow<String> = _selectedServerId.asStateFlow()

    private val _isTurboEnabled = MutableStateFlow(
        prefs.getBoolean(PREF_TURBO_ENABLED, true)
    )
    val isTurboEnabled: StateFlow<Boolean> = _isTurboEnabled.asStateFlow()

    private val _isPinging = MutableStateFlow(false)
    val isPinging: StateFlow<Boolean> = _isPinging.asStateFlow()

    fun getActiveServer(): GlobalServer {
        val currentId = _selectedServerId.value
        return _servers.value.find { it.id == currentId } ?: _servers.value.first()
    }

    fun selectServer(serverId: String) {
        val exists = _servers.value.any { it.id == serverId }
        if (exists) {
            _selectedServerId.value = serverId
            prefs.edit().putString(PREF_SELECTED_SERVER, serverId).apply()
        }
    }

    fun toggleTurbo(): Boolean {
        val newValue = !_isTurboEnabled.value
        _isTurboEnabled.value = newValue
        prefs.edit().putBoolean(PREF_TURBO_ENABLED, newValue).apply()
        return newValue
    }

    suspend fun pingAllServers() {
        _isPinging.value = true
        delay(600) // Simulate fast network probe
        val updated = _servers.value.map { server ->
            val jitter = Random.nextInt(-3, 4)
            val basePing = when (server.id) {
                "srv_tokyo_prime" -> 11
                "srv_dubai_falcon" -> 13
                "srv_frankfurt_core" -> 15
                "srv_london_mesh" -> 17
                "srv_ashburn_east" -> 18
                "srv_singapore_ocean" -> 20
                "srv_silicon_valley" -> 21
                "srv_sydney_aurora" -> 29
                else -> 20
            }
            val turboBonus = if (_isTurboEnabled.value) -3 else 0
            val calculatedPing = (basePing + jitter + turboBonus).coerceAtLeast(8)
            server.copy(
                pingMs = calculatedPing,
                loadPercent = (server.loadPercent + Random.nextInt(-4, 5)).coerceIn(12, 75)
            )
        }
        _servers.value = updated
        _isPinging.value = false
    }
}
