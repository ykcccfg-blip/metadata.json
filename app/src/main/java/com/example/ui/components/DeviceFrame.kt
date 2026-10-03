package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DeviceFrame(
    title: String,
    primaryColor: Color = Color(0xFF6366F1),
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        // Physical Device Chassis Mockup (Matches 320x600 Flutter design + modern polish)
        Box(
            modifier = Modifier
                .width(330.dp)
                .height(620.dp)
                .shadow(elevation = 24.dp, shape = RoundedCornerShape(36.dp), spotColor = Color(0xFF6366F1))
                .background(Color(0xFF090D16), RoundedCornerShape(36.dp))
                .border(width = 7.dp, color = Color(0xFF334155), shape = RoundedCornerShape(36.dp))
                .padding(6.dp)
        ) {
            // Screen area
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(26.dp))
                    .background(Color(0xFF0F172A))
            ) {
                // Top Bezel / Status Bar with Camera Hole & System Icons
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(28.dp)
                        .background(Color(0xFF1E293B).copy(alpha = 0.9f))
                        .padding(horizontal = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Clock
                    Text(
                        text = "09:41",
                        color = Color(0xFFCBD5E1),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterStart)
                    )

                    // Front Camera Cutout
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(Color(0xFF090D16), CircleShape)
                            .border(1.dp, Color(0xFF475569), CircleShape)
                    )

                    // System Icons (Wifi, Battery)
                    Row(
                        modifier = Modifier.align(Alignment.CenterEnd),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.Wifi, contentDescription = null, tint = Color(0xFFCBD5E1), modifier = Modifier.size(12.dp))
                        Icon(Icons.Default.BatteryFull, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(13.dp))
                    }
                }

                // Inner Simulated App Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .background(primaryColor)
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = title,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            modifier = Modifier.weight(1f)
                        )
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(Color(0xFF10B981), CircleShape)
                        )
                    }
                }

                // App / Game Sandbox Screen Content
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    content()
                }

                // Bottom Gesture Navigation Pill
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(18.dp)
                        .background(Color(0xFF0F172A)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .width(72.dp)
                            .height(4.dp)
                            .background(Color(0xFF64748B), RoundedCornerShape(2.dp))
                    )
                }
            }
        }
    }
}
