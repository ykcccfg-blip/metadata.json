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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

data class Brick(
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    val color: Color,
    var isDestroyed: Boolean = false
)

@Composable
fun CyberBricksGame(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val vibrator = remember { context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator }

    var isPlaying by remember { mutableStateOf(false) }
    var isGameOver by remember { mutableStateOf(false) }
    var isWin by remember { mutableStateOf(false) }
    var score by remember { mutableIntStateOf(0) }

    var paddleX by remember { mutableFloatStateOf(120f) }
    val paddleWidth = 70f
    val paddleHeight = 14f

    var ballX by remember { mutableFloatStateOf(160f) }
    var ballY by remember { mutableFloatStateOf(350f) }
    var ballVx by remember { mutableFloatStateOf(3.2f) }
    var ballVy by remember { mutableFloatStateOf(-4.2f) }
    val ballRadius = 7f

    val bricks = remember { mutableStateListOf<Brick>() }

    fun vibrateShort() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(30)
            }
        } catch (_: Exception) {}
    }

    fun initBricks() {
        bricks.clear()
        val brickW = 45f
        val brickH = 16f
        val colors = listOf(Color(0xFF6366F1), Color(0xFF10B981), Color(0xFFF59E0B), Color(0xFFEC4899))
        for (row in 0 until 4) {
            for (col in 0 until 6) {
                bricks.add(
                    Brick(
                        x = 15f + col * (brickW + 6f),
                        y = 60f + row * (brickH + 8f),
                        width = brickW,
                        height = brickH,
                        color = colors[row % colors.size]
                    )
                )
            }
        }
    }

    fun resetGame() {
        initBricks()
        paddleX = 120f
        ballX = 160f
        ballY = 350f
        ballVx = 3.2f
        ballVy = -4.2f
        score = 0
        isGameOver = false
        isWin = false
        isPlaying = true
    }

    // Physics Loop
    LaunchedEffect(isPlaying, isGameOver, isWin) {
        if (!isPlaying || isGameOver || isWin) return@LaunchedEffect

        while (isPlaying && !isGameOver && !isWin) {
            ballX += ballVx
            ballY += ballVy

            // Wall bounces
            if (ballX - ballRadius <= 5f) {
                ballX = 5f + ballRadius
                ballVx = -ballVx
            } else if (ballX + ballRadius >= 320f) {
                ballX = 320f - ballRadius
                ballVx = -ballVx
            }
            if (ballY - ballRadius <= 40f) {
                ballY = 40f + ballRadius
                ballVy = -ballVy
            }

            // Paddle bounce
            val paddleY = 490f
            if (ballY + ballRadius >= paddleY && ballY - ballRadius <= paddleY + paddleHeight) {
                if (ballX >= paddleX && ballX <= paddleX + paddleWidth) {
                    ballVy = -kotlin.math.abs(ballVy)
                    // Offset angle depending on where it hit the paddle
                    val hitPos = (ballX - (paddleX + paddleWidth / 2f)) / (paddleWidth / 2f)
                    ballVx = (hitPos * 4.5f).coerceIn(-5f, 5f)
                    vibrateShort()
                }
            }

            // Fall below paddle
            if (ballY > 540f) {
                isGameOver = true
                vibrateShort()
            }

            // Brick collisions
            for (brick in bricks) {
                if (!brick.isDestroyed) {
                    if (ballX >= brick.x && ballX <= brick.x + brick.width &&
                        ballY >= brick.y && ballY <= brick.y + brick.height
                    ) {
                        brick.isDestroyed = true
                        ballVy = -ballVy
                        score += 20
                        vibrateShort()
                        break
                    }
                }
            }

            if (bricks.isNotEmpty() && bricks.all { it.isDestroyed }) {
                isWin = true
            }

            delay(20)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(isPlaying) {
                    if (isPlaying && !isGameOver && !isWin) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            paddleX = (paddleX + dragAmount.x).coerceIn(10f, size.width - paddleWidth - 10f)
                        }
                    }
                }
        ) {
            // Draw border
            drawRect(
                color = Color(0xFF334155),
                topLeft = Offset(4f, 40f),
                size = Size(size.width - 8f, size.height - 48f),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
            )

            // Draw Bricks
            for (brick in bricks) {
                if (!brick.isDestroyed) {
                    drawRoundRect(
                        color = brick.color,
                        topLeft = Offset(brick.x, brick.y),
                        size = Size(brick.width, brick.height),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
                    )
                }
            }

            // Draw Paddle
            val paddleY = 490f
            drawRoundRect(
                color = Color(0xFF6366F1),
                topLeft = Offset(paddleX, paddleY),
                size = Size(paddleWidth, paddleHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
            )

            // Draw Ball
            drawCircle(
                color = Color(0xFF10B981),
                radius = ballRadius,
                center = Offset(ballX, ballY)
            )
        }

        // HUD
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("النقاط: $score", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text("قوالب باقية: ${bricks.count { !it.isDestroyed }}", color = Color(0xFF94A3B8), fontSize = 13.sp)
        }

        // Overlay: Start or Game Over Screen
        if (!isPlaying || isGameOver || isWin) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.8f)),
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
                            imageVector = if (isWin) Icons.Default.EmojiEvents else if (isGameOver) Icons.Default.Cancel else Icons.Default.VideogameAsset,
                            contentDescription = null,
                            tint = if (isWin) Color(0xFFF59E0B) else if (isGameOver) Color(0xFFEF4444) else Color(0xFF6366F1),
                            modifier = Modifier.size(52.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (isWin) "انتصار مبهر! 🏆" else if (isGameOver) "انتهت المحاولة! 💔" else "Cyber Bricks Breaker",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (isWin) "لقد حطمت جميع القوالب بنجاح! النقاط: $score"
                            else if (isGameOver) "سقطت الكرة تحت المضرب! نتيجتك: $score"
                            else "اسحب المضرب لرد الكرة وتدمير جميع قوالب النيون!",
                            color = Color(0xFF94A3B8),
                            fontSize = 13.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                        Button(
                            onClick = { resetGame() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (!isPlaying) "ابدأ اللعب 🎮" else "إعادة المحاولة 🔄", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
