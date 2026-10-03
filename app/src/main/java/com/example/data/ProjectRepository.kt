package com.example.data

import com.example.model.ProjectEntity
import kotlinx.coroutines.flow.Flow

class ProjectRepository(private val projectDao: ProjectDao) {

    val allProjects: Flow<List<ProjectEntity>> = projectDao.getAllProjects()

    fun getProjectsByType(type: String): Flow<List<ProjectEntity>> =
        projectDao.getProjectsByType(type)

    suspend fun getProjectById(id: Long): ProjectEntity? =
        projectDao.getProjectById(id)

    suspend fun insertProject(project: ProjectEntity): Long =
        projectDao.insertProject(project)

    suspend fun updateProject(project: ProjectEntity) =
        projectDao.updateProject(project)

    suspend fun deleteProject(project: ProjectEntity) =
        projectDao.deleteProject(project)

    suspend fun deleteProjectById(id: Long) =
        projectDao.deleteProjectById(id)

    suspend fun ensureDefaultProjectsIfEmpty() {
        if (projectDao.getProjectCount() == 0) {
            val starterProjects = listOf(
                ProjectEntity(
                    title = "NovaStore E-Commerce",
                    description = "متجر إلكتروني ذكي متكامل مع سلة مشتريات وبوابات دفع إلكتروني وتتبع طلبات.",
                    type = "App",
                    category = "E-Commerce",
                    iconName = "shopping_bag",
                    colorHex = "#06B6D4",
                    prompt = "تطبيق متجر إلكتروني مع سلة مشتريات وحساب الفاتورة وبوابات دفع",
                    generatedCode = """// Jetpack Compose E-Commerce App
@Composable
fun StoreEcommerceApp() {
    // Product catalog & shopping cart state
}""",
                    interactiveType = "STOREFRONT"
                ),
                ProjectEntity(
                    title = "Task Flow Pro",
                    description = "تطبيق متطور لإدارة المهام اليومية مع تصنيف الأولويات وتتبع نسب الإنجاز وحفظ الحالة.",
                    type = "App",
                    category = "Productivity",
                    iconName = "check_circle",
                    colorHex = "#6366F1",
                    prompt = "تطبيق إنتاجية لتتبع المهام اليومية مع إحصائيات بصرية وتصفية ذكية",
                    generatedCode = """// Jetpack Compose Task Manager
@Composable
fun TaskFlowApp() {
    // Interactive StateFlow with dynamic filtering
}""",
                    interactiveType = "TODO_PRO"
                ),
                ProjectEntity(
                    title = "CryptoPulse Wallet",
                    description = "محفظة مالية واستثمارية لمراقبة الميزانية والمصروفات والعملات المشفرة مع رسوم بيانية تفاعلية.",
                    type = "App",
                    category = "Personal Finance",
                    iconName = "account_balance_wallet",
                    colorHex = "#10B981",
                    prompt = "تطبيق محفظة وإدارة ميزانية شخصية مع بطاقات تحليل بياني",
                    generatedCode = """// Expense Tracker & Wallet
@Composable
fun ExpenseTrackerApp() {
    // Interactive balance & transaction ledger
}""",
                    interactiveType = "EXPENSE_TRACKER"
                ),
                ProjectEntity(
                    title = "FitTrack Daily Flow",
                    description = "متتبع اللياقة والصحة الشامل، تسجيل شرب الماء وحرق السعرات الحرارية وتتبع التمارين اليومية.",
                    type = "App",
                    category = "Health & Fitness",
                    iconName = "fitness_center",
                    colorHex = "#14B8A6",
                    prompt = "تطبيق لياقة وصحة ذكي لتسجيل كمية الماء والتمارين اليومية",
                    generatedCode = """// Health & Fitness Dashboard
@Composable
fun FitTrackApp() {
    // Animated progress rings and interactive logs
}""",
                    interactiveType = "FITNESS_FLOW"
                )
            )

            for (project in starterProjects) {
                projectDao.insertProject(project)
            }
        }
    }
}
