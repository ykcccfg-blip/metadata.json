package com.example.ui.sandbox

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.random.Random

data class Asteroid(
    var x: Float,
    var y: Float,
    val speed: Float,
    val radius: Float
)

data class EnergyOrb(
    var x: Float,
    var y: Float,
    val speed: Float,
    val radius: Float
)

@Composable
fun SpaceDodgerGame(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val vibrator = remember { context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator }

    var isPlaying by remember { mutableStateOf(false) }
    var isGameOver by remember { mutableStateOf(false) }
    var score by remember { mutableIntStateOf(0) }
    var highScore by remember { mutableIntStateOf(0) }
    var lives by remember { mutableIntStateOf(3) }

    var playerX by remember { mutableFloatStateOf(160f) }
    val asteroids = remember { mutableStateListOf<Asteroid>() }
    val energyOrbs = remember { mutableStateListOf<EnergyOrb>() }

    fun vibrateShort() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(50)
            }
        } catch (_: Exception) {}
    }

    fun resetGame() {
        isPlaying = true
        isGameOver = false
        score = 0
        lives = 3
        playerX = 160f
        asteroids.clear()
        energyOrbs.clear()
    }

    // Game Loop
    LaunchedEffect(isPlaying, isGameOver) {
        if (!isPlaying || isGameOver) return@LaunchedEffect

        var frame = 0
        while (isPlaying && !isGameOver) {
            frame++

            // Spawn asteroids
            if (frame % 25 == 0) {
                val spawnX = Random.nextFloat() * 300f + 10f
                asteroids.add(
                    Asteroid(
                        x = spawnX,
                        y = -20f,
                        speed = Random.nextFloat() * 4f + 3f,
                        radius = Random.nextFloat() * 12f + 10f
                    )
                )
            }

            // Spawn energy orbs
            if (frame % 45 == 0) {
                val spawnX = Random.nextFloat() * 300f + 10f
                energyOrbs.add(
                    EnergyOrb(
                        x = spawnX,
                        y = -20f,
                        speed = 3.5f,
                        radius = 9f
                    )
                )
            }

            // Update asteroids
            val playerY = 480f
            val hitAsteroids = mutableListOf<Asteroid>()
            for (ast in asteroids) {
                ast.y += ast.speed

                // Collision detection with player
                val dx = ast.x - playerX
                val dy = ast.y - playerY
                val dist = kotlin.math.sqrt(dx * dx + dy * dy)
                if (dist < (ast.radius + 18f)) {
                    hitAsteroids.add(ast)
                    lives--
                    vibrateShort()
                    if (lives <= 0) {
                        isGameOver = true
                        if (score > highScore) highScore = score
                    }
                }
            }
            asteroids.removeAll(hitAsteroids)
            asteroids.removeAll { it.y > 600f }

            // Update energy orbs
            val collectedOrbs = mutableListOf<EnergyOrb>()
            for (orb in energyOrbs) {
                orb.y += orb.speed
                val dx = orb.x - playerX
                val dy = orb.y - playerY
                val dist = kotlin.math.sqrt(dx * dx + dy * dy)
                if (dist < (orb.radius + 20f)) {
                    collectedOrbs.add(orb)
                    score += 10
                }
            }
            energyOrbs.removeAll(collectedOrbs)
            energyOrbs.removeAll { it.y > 600f }

            // Increment survival score
            if (frame % 10 == 0) {
                score += 1
            }

            delay(25) // ~40 FPS loop
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF090D16), Color(0xFF0F172A), Color(0xFF1E1B4B))
                )
            )
    ) {
        // Starfield & Game Canvas
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(isPlaying, isGameOver) {
                    if (isPlaying && !isGameOver) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            playerX = (playerX + dragAmount.x).coerceIn(24f, size.width - 24f)
                        }
                    }
                }
        ) {
            // Draw background stars
            drawCircle(Color.White.copy(alpha = 0.4f), 2f, Offset(40f, 70f))
            drawCircle(Color.White.copy(alpha = 0.6f), 3f, Offset(200f, 130f))
            drawCircle(Color.White.copy(alpha = 0.3f), 1.5f, Offset(280f, 90f))
            drawCircle(Color.White.copy(alpha = 0.5f), 2.5f, Offset(120f, 250f))
            drawCircle(Color.White.copy(alpha = 0.7f), 3f, Offset(250f, 380f))
            drawCircle(Color.White.copy(alpha = 0.4f), 2f, Offset(70f, 430f))

            if (isPlaying && !isGameOver) {
                // Draw asteroids
                for (ast in asteroids) {
                    drawCircle(
                        color = Color(0xFF94A3B8),
                        radius = ast.radius,
                        center = Offset(ast.x, ast.y)
                    )
                    drawCircle(
                        color = Color(0xFF64748B),
                        radius = ast.radius * 0.5f,
                        center = Offset(ast.x - ast.radius * 0.2f, ast.y - ast.radius * 0.2f)
                    )
                }

                // Draw energy orbs
                for (orb in energyOrbs) {
                    drawCircle(
                        color = Color(0xFF10B981).copy(alpha = 0.4f),
                        radius = orb.radius + 4f,
                        center = Offset(orb.x, orb.y)
                    )
                    drawCircle(
                        color = Color(0xFF34D399),
                        radius = orb.radius,
                        center = Offset(orb.x, orb.y)
                    )
                }

                // Draw Spaceship (Triangle with engine fire)
                val shipY = 480f
                val shipPath = Path().apply {
                    moveTo(playerX, shipY - 24f) // Nose
                    lineTo(playerX - 18f, shipY + 16f) // Left wing
                    lineTo(playerX, shipY + 8f) // Thruster center
                    lineTo(playerX + 18f, shipY + 16f) // Right wing
                    close()
                }
                drawPath(shipPath, color = Color(0xFF6366F1))

                // Cockpit
                drawCircle(
                    color = Color(0xFF38BDF8),
                    radius = 5f,
                    center = Offset(playerX, shipY - 6f)
                )

                // Engine flame
                drawCircle(
                    color = Color(0xFFF97316),
                    radius = 6f,
                    center = Offset(playerX, shipY + 14f)
                )
            }
        }

        // HUD (Score & Lives)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "النقاط: $score",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                if (highScore > 0) {
                    Text(
                        text = "الرقم القياسي: $highScore",
                        color = Color(0xFFF59E0B),
                        fontSize = 12.sp
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                repeat(3) { index ->
                    Icon(
                        imageVector = if (index < lives) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Life",
                        tint = if (index < lives) Color(0xFFEF4444) else Color(0xFF64748B),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Overlay: Start or Game Over Screen
        if (!isPlaying || isGameOver) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.75f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = if (isGameOver) Icons.Default.SentimentDissatisfied else Icons.Default.RocketLaunch,
                            contentDescription = null,
                            tint = if (isGameOver) Color(0xFFEF4444) else Color(0xFF10B981),
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (isGameOver) "انتهت اللعبة! 💥" else "Space Dodger Arcade",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (isGameOver)
                                "نقاطك النهائية: $score نقطة"
                            else
                                "اسحب بإصبعك لتوجيه المركبة وتفادي الكويكبات واجمع الكبسولات الخضراء!",
                            color = Color(0xFF94A3B8),
                            fontSize = 13.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                        Button(
                            onClick = { resetGame() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isGameOver) "إعادة اللعب 🔄" else "ابدأ اللعب الآن 🚀",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // On-screen manual controls for convenience
        if (isPlaying && !isGameOver) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 12.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconButton(
                    onClick = { playerX = (playerX - 25f).coerceAtLeast(24f) },
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color.White.copy(alpha = 0.15f), CircleShape)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Left", tint = Color.White)
                }

                IconButton(
                    onClick = { playerX = (playerX + 25f).coerceAtMost(300f) },
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color.White.copy(alpha = 0.15f), CircleShape)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Right", tint = Color.White)
                }
            }
        }
    }
}
