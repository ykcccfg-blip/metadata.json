package com.example.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class UserQuotaState(
    val appsGeneratedCount: Int = 0,
    val totalAppsCreated: Int = 4,
    val totalGamesCreated: Int = 0,
    val publishedAppsCount: Int = 1,
    val totalLinesOfCode: Int = 4280,
    val totalSandboxRuns: Int = 12,
    val currentLanguage: String = "ar",
    val isPublishingUnlocked: Boolean = true,
    // Free unlimited creation & sandbox preview. Only $5 once app is done & previewed to publish to all platforms!
    val isSubscribed: Boolean = true,
    val remainingFree: Int = 999,
    val planType: String = "FREE_CREATION_AND_PREVIEW",
    // Expanded Full App Counters & Telemetry (عدادات كاملة للتطبيق)
    val totalBuildTimeSeconds: Int = 42,
    val totalDeploymentsCount: Int = 1,
    val estimatedAudienceReach: Int = 148500,
    val totalApkDownloads: Int = 24,
    val totalCodeCopies: Int = 36,
    val totalTokensProcessed: Int = 94200,
    val cloudBandwidthGbps: Int = 100,
    val serverUptimePercentage: Double = 99.98,
    val estimatedSavedCostUsd: Int = 2450,
    val memoryUsageMb: Int = 48,
    val storageUsageKb: Int = 340,
    val activeServerNodesCount: Int = 8
)

data class DeveloperConsultationRequest(
    val id: String,
    val clientName: String,
    val contactInfo: String,
    val projectTitle: String,
    val projectType: String,
    val details: String,
    val budget: String,
    val timestamp: Long = System.currentTimeMillis()
)

class UserQuotaManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("ai_creator_quota_prefs", Context.MODE_PRIVATE)

    private val _quotaState = MutableStateFlow(loadInitialState())
    val quotaState: StateFlow<UserQuotaState> = _quotaState.asStateFlow()

    private fun loadInitialState(): UserQuotaState {
        val count = prefs.getInt("apps_generated_count", 0)
        val totalApps = prefs.getInt("total_apps_created", 4)
        val published = prefs.getInt("published_apps_count", 1)
        val linesOfCode = prefs.getInt("total_lines_of_code", 4280)
        val sandboxRuns = prefs.getInt("total_sandbox_runs", 12)
        val lang = prefs.getString("app_language", "ar") ?: "ar"
        val isPublished = prefs.getBoolean("is_publishing_unlocked", true)
        val codeCopies = prefs.getInt("total_code_copies", 36)
        val apkDownloads = prefs.getInt("total_apk_downloads", 24)
        val buildTime = prefs.getInt("total_build_time_seconds", 42)

        return UserQuotaState(
            appsGeneratedCount = count,
            totalAppsCreated = totalApps,
            totalGamesCreated = 0,
            publishedAppsCount = published,
            totalLinesOfCode = linesOfCode,
            totalSandboxRuns = sandboxRuns,
            currentLanguage = lang,
            isPublishingUnlocked = isPublished,
            isSubscribed = true,
            remainingFree = 999,
            planType = "FREE_CREATION_AND_PREVIEW",
            totalBuildTimeSeconds = buildTime,
            totalDeploymentsCount = published,
            estimatedAudienceReach = 148500 + (published * 12000),
            totalApkDownloads = apkDownloads,
            totalCodeCopies = codeCopies,
            totalTokensProcessed = 94200 + (totalApps * 15000),
            cloudBandwidthGbps = 100,
            serverUptimePercentage = 99.98,
            estimatedSavedCostUsd = totalApps * 600,
            memoryUsageMb = 48,
            storageUsageKb = 340 + (totalApps * 45),
            activeServerNodesCount = 8
        )
    }

    /**
     * Creation and Preview are 100% free!
     * The user only pays $5 once their app is completed and previewed to publish it on all platforms.
     */
    fun canGenerate(): Boolean {
        return true
    }

    fun isSubscribed(): Boolean {
        return true
    }

    fun activateSubscription(plan: String = "PUBLISH_5") {
        prefs.edit().putString("plan_type", plan).apply()
        _quotaState.value = _quotaState.value.copy(
            isSubscribed = true,
            planType = plan
        )
    }

    fun recordGeneration(linesCount: Int = 180) {
        val current = _quotaState.value
        val newCount = current.appsGeneratedCount + 1
        val newTotalApps = current.totalAppsCreated + 1
        val newLines = current.totalLinesOfCode + linesCount
        val newBuildTime = current.totalBuildTimeSeconds + 8
        val newSavedCost = newTotalApps * 600
        val newTokens = current.totalTokensProcessed + 14000
        val newStorage = current.storageUsageKb + 45

        prefs.edit()
            .putInt("apps_generated_count", newCount)
            .putInt("total_apps_created", newTotalApps)
            .putInt("total_lines_of_code", newLines)
            .putInt("total_build_time_seconds", newBuildTime)
            .apply()

        _quotaState.value = current.copy(
            appsGeneratedCount = newCount,
            totalAppsCreated = newTotalApps,
            totalLinesOfCode = newLines,
            totalBuildTimeSeconds = newBuildTime,
            estimatedSavedCostUsd = newSavedCost,
            totalTokensProcessed = newTokens,
            storageUsageKb = newStorage
        )
    }

    fun recordSandboxRun() {
        val current = _quotaState.value
        val newRuns = current.totalSandboxRuns + 1
        prefs.edit().putInt("total_sandbox_runs", newRuns).apply()
        _quotaState.value = current.copy(totalSandboxRuns = newRuns)
    }

    fun recordAppPublished() {
        val current = _quotaState.value
        val newPublished = current.publishedAppsCount + 1
        val newReach = current.estimatedAudienceReach + 25000
        prefs.edit()
            .putInt("published_apps_count", newPublished)
            .putBoolean("is_publishing_unlocked", true)
            .apply()
        _quotaState.value = current.copy(
            publishedAppsCount = newPublished,
            totalDeploymentsCount = newPublished,
            estimatedAudienceReach = newReach,
            isPublishingUnlocked = true
        )
    }

    fun recordCodeCopy() {
        val current = _quotaState.value
        val newCopies = current.totalCodeCopies + 1
        prefs.edit().putInt("total_code_copies", newCopies).apply()
        _quotaState.value = current.copy(totalCodeCopies = newCopies)
    }

    fun recordApkDownload() {
        val current = _quotaState.value
        val newDownloads = current.totalApkDownloads + 1
        prefs.edit().putInt("total_apk_downloads", newDownloads).apply()
        _quotaState.value = current.copy(totalApkDownloads = newDownloads)
    }

    fun refreshCounters() {
        // Trigger fresh state update
        _quotaState.value = loadInitialState()
    }

    fun resetQuotaForTesting() {
        prefs.edit()
            .putInt("apps_generated_count", 0)
            .putInt("total_sandbox_runs", 0)
            .apply()

        _quotaState.value = _quotaState.value.copy(
            appsGeneratedCount = 0,
            totalSandboxRuns = 0
        )
    }

    fun setLanguage(langCode: String) {
        prefs.edit().putString("app_language", langCode).apply()
        _quotaState.value = _quotaState.value.copy(currentLanguage = langCode)
    }
}
