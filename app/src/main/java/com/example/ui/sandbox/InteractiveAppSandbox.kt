package com.example.ui.sandbox

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.random.Random

// ==========================================
// 1. TODO PRO (Smart Task & Habit Flow)
// ==========================================
data class TodoItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val priority: String, // "High", "Med", "Low"
    var isDone: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoProApp(modifier: Modifier = Modifier) {
    var newTaskText by remember { mutableStateOf("") }
    var selectedPriority by remember { mutableStateOf("Med") }
    var filter by remember { mutableStateOf("All") } // "All", "Pending", "Done"

    val tasks = remember {
        mutableStateListOf(
            TodoItem(title = "مراجعة هيكلة تطبيق الذكاء الاصطناعي", priority = "High", isDone = true),
            TodoItem(title = "اختبار محرك الألعاب في وضع Sandbox", priority = "High", isDone = false),
            TodoItem(title = "تصدير حزم الأكواد للإنتاج", priority = "Med", isDone = false),
            TodoItem(title = "شرب 2 لتر من الماء اليوم", priority = "Low", isDone = true)
        )
    }

    val completedCount = tasks.count { it.isDone }
    val progress = if (tasks.isNotEmpty()) completedCount.toFloat() / tasks.size else 0f

    val filteredTasks = when (filter) {
        "Pending" -> tasks.filter { !it.isDone }
        "Done" -> tasks.filter { it.isDone }
        else -> tasks
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .padding(12.dp)
    ) {
        // Progress Card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("الإنجاز اليومي", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("${(progress * 100).toInt()}% مكتمل", color = Color(0xFF10B981), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                    color = Color(0xFF6366F1),
                    trackColor = Color(0xFF334155)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Input Field
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = newTaskText,
                onValueChange = { newTaskText = it },
                placeholder = { Text("أضف مهمة أو عادة جديدة...", fontSize = 12.sp, color = Color(0xFF94A3B8)) },
                modifier = Modifier.weight(1f),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF6366F1),
                    unfocusedBorderColor = Color(0xFF334155),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color(0xFF1E293B),
                    unfocusedContainerColor = Color(0xFF1E293B)
                ),
                shape = RoundedCornerShape(10.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            IconButton(
                onClick = {
                    if (newTaskText.isNotBlank()) {
                        tasks.add(0, TodoItem(title = newTaskText.trim(), priority = selectedPriority))
                        newTaskText = ""
                    }
                },
                modifier = Modifier
                    .size(46.dp)
                    .background(Color(0xFF6366F1), RoundedCornerShape(10.dp))
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Filter Chips
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf("All" to "الكل", "Pending" to "قيد التنفيذ", "Done" to "المكتملة").forEach { (key, label) ->
                FilterChip(
                    selected = filter == key,
                    onClick = { filter = key },
                    label = { Text(label, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF6366F1),
                        selectedLabelColor = Color.White,
                        containerColor = Color(0xFF1E293B),
                        labelColor = Color(0xFF94A3B8)
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = Color(0xFF334155),
                        enabled = true,
                        selected = filter == key
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Tasks List
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(filteredTasks, key = { it.id }) { task ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = task.isDone,
                            onCheckedChange = { task.isDone = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = Color(0xFF10B981),
                                uncheckedColor = Color(0xFF64748B)
                            )
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = task.title,
                                color = if (task.isDone) Color(0xFF64748B) else Color.White,
                                fontSize = 13.sp,
                                textDecoration = if (task.isDone) TextDecoration.LineThrough else null,
                                fontWeight = if (task.isDone) FontWeight.Normal else FontWeight.Medium
                            )
                            val badgeColor = when (task.priority) {
                                "High" -> Color(0xFFEF4444)
                                "Med" -> Color(0xFFF59E0B)
                                else -> Color(0xFF10B981)
                            }
                            Text(
                                text = task.priority,
                                color = badgeColor,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        IconButton(
                            onClick = { tasks.remove(task) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 2. FITNESS & HEALTH FLOW
// ==========================================
@Composable
fun FitnessFlowApp(modifier: Modifier = Modifier) {
    var waterMl by remember { mutableIntStateOf(1500) }
    val waterGoal = 3000
    var caloriesBurned by remember { mutableIntStateOf(420) }
    val caloriesGoal = 600
    var steps by remember { mutableIntStateOf(6840) }

    val workouts = remember {
        mutableStateListOf(
            "ركض صباحي - 25 دقيقة (240 سعرة)",
            "تمارين تمدد وإطالة - 15 دقيقة (80 سعرة)",
            "تمارين القوة الأساسية - 20 دقيقة (100 سعرة)"
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .padding(12.dp)
    ) {
        // Quick Stats Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.AutoMirrored.Filled.DirectionsWalk, contentDescription = null, tint = Color(0xFF6366F1), modifier = Modifier.size(24.dp))
                    Text("$steps", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("خطوة اليوم", color = Color(0xFF94A3B8), fontSize = 11.sp)
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(24.dp))
                    Text("$caloriesBurned", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("سعرة محروقة", color = Color(0xFF94A3B8), fontSize = 11.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Water Tracker
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.WaterDrop, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("متتبع شرب الماء", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Text("$waterMl / $waterGoal مل", color = Color(0xFF38BDF8), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { (waterMl.toFloat() / waterGoal).coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                    color = Color(0xFF38BDF8),
                    trackColor = Color(0xFF334155)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { waterMl = (waterMl + 250).coerceAtMost(5000) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        Text("+250 مل 💧", fontSize = 11.sp)
                    }
                    Button(
                        onClick = { waterMl = (waterMl + 500).coerceAtMost(5000) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0369A1)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        Text("+500 مل 💧", fontSize = 11.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Workouts Header & List
        Text("تمارين اليوم المسجلة", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(6.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(workouts) { item ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(item, color = Color.White, fontSize = 12.sp, modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        Button(
            onClick = {
                val newExercise = listOf("تمرين بطن (Core) - 10 دقائق", "قفز حبل - 15 دقيقة", "دراجة ثابتة - 20 دقيقة").random()
                workouts.add(0, "$newExercise (+${Random.nextInt(60, 150)} سعرة)")
                caloriesBurned += 90
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("تسجيل نشاط رياضي جديد", fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// ==========================================
// 3. EXPENSE TRACKER (Ledger Wallet)
// ==========================================
data class ExpenseEntry(
    val title: String,
    val amount: Double,
    val isIncome: Boolean,
    val category: String
)

@Composable
fun ExpenseTrackerApp(modifier: Modifier = Modifier) {
    var balance by remember { mutableDoubleStateOf(2850.0) }
    val transactions = remember {
        mutableStateListOf(
            ExpenseEntry("راتب شهري", 4500.0, true, "راتب"),
            ExpenseEntry("إيجار الشقة", 1200.0, false, "سكن"),
            ExpenseEntry("تسوق بقالة وسوبرماركت", 320.0, false, "طعام"),
            ExpenseEntry("فاتورة إنترنت وهاتف", 130.0, false, "خدمات")
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .padding(12.dp)
    ) {
        // Balance Card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("الرصيد المتاح الحالي", color = Color(0xFF94A3B8), fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("$balance ر.س", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("المدخول", color = Color(0xFF10B981), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("+4,500 ر.س", color = Color(0xFF10B981), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    VerticalDivider(modifier = Modifier.height(28.dp).width(1.dp), color = Color(0xFF334155))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("المصروفات", color = Color(0xFFEF4444), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("-1,650 ر.س", color = Color(0xFFEF4444), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text("سجل المعاملات الأخيرة", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(6.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(transactions) { tx ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (tx.isIncome) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                            contentDescription = null,
                            tint = if (tx.isIncome) Color(0xFF10B981) else Color(0xFFEF4444),
                            modifier = Modifier
                                .size(32.dp)
                                .background(
                                    (if (tx.isIncome) Color(0xFF10B981) else Color(0xFFEF4444)).copy(alpha = 0.15f),
                                    CircleShape
                                )
                                .padding(6.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(tx.title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            Text(tx.category, color = Color(0xFF94A3B8), fontSize = 11.sp)
                        }
                        Text(
                            text = "${if (tx.isIncome) "+" else "-"}${tx.amount.toInt()} ر.س",
                            color = if (tx.isIncome) Color(0xFF10B981) else Color(0xFFEF4444),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        Button(
            onClick = {
                val samples = listOf("قهوة ومخبوزات" to 25.0, "شحن وقود" to 80.0, "كتب إلكترونية" to 45.0)
                val (t, a) = samples.random()
                transactions.add(0, ExpenseEntry(t, a, false, "متنوع"))
                balance -= a
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF06B6D4)),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("إضافة مصروف سريع", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
    }
}

// ==========================================
// 4. STOREFRONT (E-Commerce & Instant Checkout)
// ==========================================
data class StoreProduct(
    val id: String,
    val name: String,
    val category: String,
    val price: Double,
    val rating: Double,
    val icon: String
)

@Composable
fun StoreEcommerceApp(modifier: Modifier = Modifier) {
    var cartCount by remember { mutableIntStateOf(2) }
    var totalPrice by remember { mutableDoubleStateOf(349.0) }
    var orderPlaced by remember { mutableStateOf(false) }

    val products = remember {
        listOf(
            StoreProduct("p1", "سماعات Pro اللاسلكية", "إلكترونيات", 199.0, 4.9, "🎧"),
            StoreProduct("p2", "ساعة ذكية Ultra Fit", "رياضة", 150.0, 4.8, "⌚"),
            StoreProduct("p3", "شاحن سريع GaN 65W", "ملحقات", 45.0, 4.7, "🔌"),
            StoreProduct("p4", "حقيبة ظهر مقاومة للماء", "سفر", 89.0, 4.9, "🎒")
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .padding(12.dp)
    ) {
        // Store Header & Cart Banner
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFF06B6D4).copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🛒", fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("NovaStore Express", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("عروض خاصة وتوصيل فوري", color = Color(0xFF94A3B8), fontSize = 10.sp)
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF06B6D4))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("$cartCount سلع (${totalPrice.toInt()}$) 🛍️", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (orderPlaced) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF065F46)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("✅", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("تم تأكيد الطلب بنجاح! 🚀", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text("رقم التتبع: #NV-892410 - التوصيل خلال 24 ساعة", color = Color(0xFFA7F3D0), fontSize = 10.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
        }

        Text("المنتجات الأكثر مبيعاً", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(6.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(products) { p ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF0F172A)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(p.icon, fontSize = 22.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(p.name, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("${p.price} $", color = Color(0xFF10B981), fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("★ ${p.rating}", color = Color(0xFFF59E0B), fontSize = 10.sp)
                            }
                        }

                        Button(
                            onClick = {
                                cartCount++
                                totalPrice += p.price
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF06B6D4)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("+ أضف للسلة", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { orderPlaced = true },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Payment, contentDescription = null, tint = Color.Black)
            Spacer(modifier = Modifier.width(6.dp))
            Text("إتمام الشراء والدفع السريع (${totalPrice.toInt()}$) 💳", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
    }
}

// ==========================================
// 5. AI CHAT ASSISTANT
// ==========================================
data class AssistantMessage(val isUser: Boolean, val text: String, val time: String)

@Composable
fun AiChatAssistantApp(modifier: Modifier = Modifier) {
    var inputText by remember { mutableStateOf("") }
    val messages = remember {
        mutableStateListOf(
            AssistantMessage(false, "مرحباً! أنا مساعدك الذكي 🤖 كيف يمكنني مساعدتك في تخطيط مشاريعك أو أعمالك اليوم؟", "10:00"),
            AssistantMessage(true, "أريد ملخصاً لأفضل استراتيجية تسويق للتطبيقات في 2026", "10:01"),
            AssistantMessage(false, "أفضل استراتيجية هي: 1) إطلاق متجر سحابي فوري PWA بدون انتظار، 2) فيديوهات قصيرة تشرح الحل في 5 ثوانٍ، 3) نشر فوري على Google Play و App Store.", "10:01")
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .padding(12.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).background(Color(0xFF10B981), CircleShape))
                Spacer(modifier = Modifier.width(6.dp))
                Text("OmniMind AI (متصل بالسيرفر العالمي)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages) { msg ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (msg.isUser) Arrangement.End else Arrangement.Start
                ) {
                    Box(
                        modifier = Modifier
                            .widthIn(max = 240.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (msg.isUser) Color(0xFF6366F1) else Color(0xFF1E293B))
                            .padding(10.dp)
                    ) {
                        Column {
                            Text(msg.text, color = Color.White, fontSize = 11.sp, lineHeight = 16.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(msg.time, color = Color(0xFF94A3B8), fontSize = 8.sp, modifier = Modifier.align(Alignment.End))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = { Text("اكتب رسالتك...", fontSize = 11.sp, color = Color(0xFF64748B)) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color(0xFF1E293B),
                    unfocusedContainerColor = Color(0xFF1E293B)
                )
            )
            Spacer(modifier = Modifier.width(6.dp))
            IconButton(
                onClick = {
                    if (inputText.isNotBlank()) {
                        messages.add(AssistantMessage(true, inputText.trim(), "الآن"))
                        val prompt = inputText
                        inputText = ""
                        messages.add(AssistantMessage(false, "تمت المعالجة بنجاح عبر سيرفر الذكاء الاصطناعي: $prompt جاهز للتنفيذ الفوري ⚡", "الآن"))
                    }
                },
                modifier = Modifier.size(46.dp).background(Color(0xFFEC4899), RoundedCornerShape(10.dp))
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(18.dp))
            }
        }
    }
}

// ==========================================
// 6. DOCTOR BOOKING APP
// ==========================================
@Composable
fun DoctorBookingApp(modifier: Modifier = Modifier) {
    var booked by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .padding(12.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("عيادات MediBook الرقمية 🩺", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("حجز مواعيد أطباء واستشارات فورية", color = Color(0xFF94A3B8), fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (booked) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF065F46)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("✅ تم تأكيد موعدك بنجاح! سيتم الاتصال بك في الموعد المحدد.", color = Color.White, fontSize = 11.sp, modifier = Modifier.padding(10.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        listOf(
            Triple("د. سارة المنصور", "استشارية طب وجراحة العيون", "4.9 ★ • متاح اليوم"),
            Triple("د. أحمد خالد", "أخصائي باطنية وقلب", "4.8 ★ • متاح غداً"),
            Triple("د. ليلى الشريف", "استشارية طب أسرة وأطفال", "5.0 ★ • استشارة فورية")
        ).forEach { (name, spec, time) ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(38.dp).background(Color(0xFFF59E0B).copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("👨‍⚕️", fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text(spec, color = Color(0xFF94A3B8), fontSize = 10.sp)
                        Text(time, color = Color(0xFFF59E0B), fontSize = 9.sp)
                    }
                    Button(
                        onClick = { booked = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text("حجز 📅", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

