package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.model.ChatMessage
import com.example.model.GeneratedProject
import com.example.model.GlobalServer
import com.example.model.ProjectEntity
import com.example.model.ShowcaseItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class AppCreatorViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ProjectRepository
    private val generator = AiProjectGenerator()
    private val quotaManager = UserQuotaManager(application)
    private val globalServerManager = GlobalServerManager(application)

    val savedProjects: StateFlow<List<ProjectEntity>>
    val quotaState: StateFlow<UserQuotaState> = quotaManager.quotaState

    val servers: StateFlow<List<GlobalServer>> = globalServerManager.servers
    val selectedServerId: StateFlow<String> = globalServerManager.selectedServerId
    val isTurboEnabled: StateFlow<Boolean> = globalServerManager.isTurboEnabled
    val isPinging: StateFlow<Boolean> = globalServerManager.isPinging
    val showcaseItems = GlobalServerManager.SHOWCASE_ITEMS

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _generationStep = MutableStateFlow("")
    val generationStep: StateFlow<String> = _generationStep.asStateFlow()

    private val _currentActiveProject = MutableStateFlow<GeneratedProject?>(null)
    val currentActiveProject: StateFlow<GeneratedProject?> = _currentActiveProject.asStateFlow()

    private val _selectedFilterType = MutableStateFlow("All") // "All", "Productivity", "Commerce", "Finance"
    val selectedFilterType: StateFlow<String> = _selectedFilterType.asStateFlow()

    private val _showSubscriptionModal = MutableStateFlow(false)
    val showSubscriptionModal: StateFlow<Boolean> = _showSubscriptionModal.asStateFlow()

    private val _showPublishDialog = MutableStateFlow(false)
    val showPublishDialog: StateFlow<Boolean> = _showPublishDialog.asStateFlow()

    private val _projectToPublish = MutableStateFlow<GeneratedProject?>(null)
    val projectToPublish: StateFlow<GeneratedProject?> = _projectToPublish.asStateFlow()

    private val _submittedConsultations = MutableStateFlow<List<DeveloperConsultationRequest>>(emptyList())
    val submittedConsultations: StateFlow<List<DeveloperConsultationRequest>> = _submittedConsultations.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = ProjectRepository(database.projectDao())

        savedProjects = repository.allProjects
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

        viewModelScope.launch {
            repository.ensureDefaultProjectsIfEmpty()
        }

        // Welcoming conversation focused 100% on real mobile apps & $5 multi-platform publishing
        _messages.value = listOf(
            ChatMessage(
                sender = "ai",
                text = "مرحباً بك في مصنع التطبيقات الذكية المتطور 🚀!\n" +
                        "• بناء وتصميم ومعاينة التطبيقات مفتوحة ومجانية بالكامل.\n" +
                        "• عند اكتمال تطبيقك والتأكد منه في المعاينة الحية، يمكنك دفـع 5$ فقط لرفعه ونشره فورياً في كل المنصات (Google Play و Apple App Store و Web PWA و Huawei) مدعوماً بأقوى سيرفرات الحوسبة العالمية.\n\n" +
                        "اكتب فكرة تطبيقك الآن وسيقوم المحرك الذكي بهندسة الواجهات والأكواد فورياً!"
            )
        )

        // Set initial default project: E-Commerce Storefront
        _currentActiveProject.value = GeneratedProject(
            title = "NovaStore E-Commerce",
            type = "App",
            description = "متجر تسوق إلكتروني متكامل مع سلة مشتريات وبوابات دفع وتتبع شحنات.",
            category = "E-Commerce",
            features = listOf(
                "واجهات Material 3 تفاعلية 100%",
                "سلة مشتريات وبوابات دفع إلكترونية سريعة",
                "مدعوم بأعلى سيرفرات المعالجة السحابية 100 Gbps",
                "جاهز للنشر على كافة المتاجر بـ 5$ فقط"
            ),
            interactiveType = "STOREFRONT",
            colorHex = "#06B6D4",
            iconName = "shopping_bag",
            codeCompose = "// Jetpack Compose E-Commerce Architecture",
            codeFlutter = "// Flutter E-Commerce Widget",
            architectureSchema = "{\n  \"app\": \"NovaStore\",\n  \"serverTier\": \"Tier-1 100Gbps\",\n  \"status\": \"READY_TO_PUBLISH\"\n}"
        )
    }

    fun openPublishDialog(project: GeneratedProject? = null) {
        _projectToPublish.value = project ?: _currentActiveProject.value
        _showPublishDialog.value = true
    }

    fun closePublishDialog() {
        _showPublishDialog.value = false
    }

    fun recordAppPublished() {
        quotaManager.recordAppPublished()
        _messages.value = _messages.value + ChatMessage(
            sender = "ai",
            text = "تهانينا! 🎉 تم نشر تطبيقك بنجاح على جميع المنصات العالمية (Google Play, Apple App Store, Web PWA, Huawei) عبر أعلى السيرفرات السحابية. تم تجهيز روابط التحميل والشهادات الرسمية!"
        )
    }

    fun openSubscriptionModal() {
        _showSubscriptionModal.value = true
    }

    fun closeSubscriptionModal() {
        _showSubscriptionModal.value = false
    }

    fun canGenerateApp(): Boolean {
        return quotaManager.canGenerate()
    }

    fun resetTrialQuota() {
        quotaManager.resetQuotaForTesting()
    }

    fun setLanguage(langCode: String) {
        quotaManager.setLanguage(langCode)
    }

    fun setFilterType(type: String) {
        _selectedFilterType.value = type
    }

    fun sendUserPrompt(prompt: String, projectType: String = "App") {
        if (prompt.isBlank()) return

        val userMsg = ChatMessage(sender = "user", text = prompt)
        _messages.value = _messages.value + userMsg
        _isGenerating.value = true

        viewModelScope.launch {
            try {
                val generated = generator.generateProject(
                    prompt = prompt,
                    projectType = "App",
                    onProgressUpdate = { step ->
                        _generationStep.value = step
                    }
                )

                _currentActiveProject.value = generated

                // Record count in quota tracker
                quotaManager.recordGeneration(linesCount = generated.codeCompose.lines().size)

                val aiMsg = ChatMessage(
                    sender = "ai",
                    text = "تم بناء تطبيق \"${generated.title}\" بنجاح! 🚀\n" +
                            "${generated.description}\n\n" +
                            "✨ الخصائص المضمنة:\n" +
                            generated.features.joinToString("\n") { "• $it" } +
                            "\n\nيمكنك الآن معاينة واختبار التطبيق مباشرة في المحاكي الحقيقي، وعندما تكون راضياً عن النتيجة اضغط على (رفع ونشر التطبيق بـ 5$) ليتم إطلاقه على Google Play و App Store و Web في ثوانٍ معدودة عبر أعلى السيرفرات السحابية العالمية!",
                    isArtifact = true,
                    artifact = generated
                )
                _messages.value = _messages.value + aiMsg

                // Also automatically save to Room database
                repository.insertProject(
                    ProjectEntity(
                        title = generated.title,
                        description = generated.description,
                        type = "App",
                        category = generated.category,
                        iconName = generated.iconName,
                        colorHex = generated.colorHex,
                        prompt = prompt,
                        generatedCode = generated.codeCompose,
                        interactiveType = generated.interactiveType
                    )
                )

            } catch (e: Exception) {
                _messages.value = _messages.value + ChatMessage(
                    sender = "ai",
                    text = "حدث تنبيه أثناء التوليد: ${e.localizedMessage ?: "خطأ غير معروف"}. تم تشغيل المحاكي الاحتياطي."
                )
            } finally {
                _isGenerating.value = false
                _generationStep.value = ""
            }
        }
    }

    fun setSandboxProject(project: GeneratedProject) {
        _currentActiveProject.value = project
        quotaManager.recordSandboxRun()
    }

    fun launchEngineDirectly(engineType: String) {
        quotaManager.recordSandboxRun()
        val (title, color, cat) = when (engineType) {
            "STOREFRONT" -> Triple("NovaStore Express 🛒", "#06B6D4", "E-Commerce & Pay")
            "TODO_PRO" -> Triple("Smart Habit & Task Flow 📋", "#6366F1", "Productivity")
            "FITNESS_FLOW" -> Triple("FitTrack Hydration & Workouts 💧", "#14B8A6", "Health & Fitness")
            "EXPENSE_TRACKER" -> Triple("CryptoPulse & Ledger Wallet 💰", "#10B981", "Finance & Budget")
            "AI_ASSISTANT" -> Triple("OmniMind AI Assistant 🤖", "#EC4899", "AI Utility & Chat")
            "DOCTOR_BOOKING" -> Triple("MediBook Clinic & Health 🩺", "#F59E0B", "Healthcare & Booking")
            else -> Triple("Interactive Mobile App", "#6366F1", "Productivity")
        }
        _currentActiveProject.value = GeneratedProject(
            title = title,
            type = "App",
            description = "تطبيق محمول متكامل وسريع الاستجابة مدعوم بالسيرفرات السحابية وجاهز للنشر بـ 5$.",
            category = cat,
            features = listOf(
                "تطبيق محمول حقيقي تفاعلي 100%",
                "مدعوم بأقوى السيرفرات السحابية",
                "نشر فوري في كل المنصات بـ 5$ فقط"
            ),
            interactiveType = engineType,
            colorHex = color,
            iconName = "phone_android",
            codeCompose = "// Interactive engine ready: $engineType",
            codeFlutter = "// Flutter counterpart for: $engineType",
            architectureSchema = "{\n  \"app\": \"$engineType\",\n  \"type\": \"App\",\n  \"status\": \"ACTIVE\"\n}"
        )
    }

    fun loadEntityIntoSandbox(entity: ProjectEntity) {
        quotaManager.recordSandboxRun()
        _currentActiveProject.value = GeneratedProject(
            title = entity.title,
            type = "App",
            description = entity.description,
            category = entity.category,
            features = listOf("محرك جاهز للتشغيل", "واجهات مستخدم تفاعلية", "حفظ محلي في Room"),
            interactiveType = entity.interactiveType,
            colorHex = entity.colorHex,
            iconName = entity.iconName,
            codeCompose = entity.generatedCode,
            codeFlutter = "// Generated Flutter implementation for ${entity.title}",
            architectureSchema = "{\n  \"project\": \"${entity.title}\",\n  \"savedInRoom\": true\n}"
        )
    }

    fun submitConsultationRequest(
        clientName: String,
        contactInfo: String,
        projectTitle: String,
        projectType: String,
        details: String,
        budget: String
    ): Boolean {
        val request = DeveloperConsultationRequest(
            id = UUID.randomUUID().toString(),
            clientName = clientName,
            contactInfo = contactInfo,
            projectTitle = projectTitle,
            projectType = projectType,
            details = details,
            budget = budget
        )
        _submittedConsultations.value = listOf(request) + _submittedConsultations.value
        return true
    }

    fun deleteProject(entity: ProjectEntity) {
        viewModelScope.launch {
            repository.deleteProject(entity)
        }
    }

    fun duplicateProject(entity: ProjectEntity) {
        viewModelScope.launch {
            repository.insertProject(
                entity.copy(
                    id = 0,
                    title = "${entity.title} (نسخة)",
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    fun getActiveServer(): GlobalServer = globalServerManager.getActiveServer()

    fun selectServer(serverId: String) {
        globalServerManager.selectServer(serverId)
    }

    fun toggleTurbo(): Boolean {
        return globalServerManager.toggleTurbo()
    }

    fun pingServers() {
        viewModelScope.launch {
            globalServerManager.pingAllServers()
        }
    }

    fun launchShowcaseItem(item: ShowcaseItem) {
        quotaManager.recordSandboxRun()
        val activeSrv = getActiveServer()
        _currentActiveProject.value = GeneratedProject(
            title = item.title,
            type = "App",
            description = item.descriptionAr,
            category = item.categoryAr,
            features = listOf(
                "متصل بسيرفر: ${activeSrv.flag} ${activeSrv.nameAr}",
                "زمن استجابة فائق: ${activeSrv.pingMs}ms",
                "تطبيق حقيقي جاهز للنشر في كل المنصات بـ 5$"
            ),
            interactiveType = item.interactiveType,
            colorHex = item.colorHex,
            iconName = item.iconName,
            codeCompose = "// Interactive Application Architecture for ${item.title}\n// Connected to Tier-1 Server Node: ${activeSrv.name} (${activeSrv.regionCode})\n// Latency: ${activeSrv.pingMs}ms | GPU: ${activeSrv.gpuCluster}",
            codeFlutter = "// Flutter counterpart for ${item.title}",
            architectureSchema = "{\n  \"showcaseId\": \"${item.id}\",\n  \"serverNode\": \"${activeSrv.id}\",\n  \"pingMs\": ${activeSrv.pingMs}\n}"
        )
    }

    fun activateSubscription(plan: String = "PUBLISH_5") {
        quotaManager.activateSubscription(plan)
    }

    fun recordCodeCopy() {
        quotaManager.recordCodeCopy()
    }

    fun recordApkDownload() {
        quotaManager.recordApkDownload()
    }

    fun refreshCounters() {
        quotaManager.refreshCounters()
    }
}
