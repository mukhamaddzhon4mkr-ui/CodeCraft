package com.example.codecraft.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val BgDark = Color(0xFF0D1117)
private val Surface = Color(0xFF161B22)
private val Accent = Color(0xFF00FF94)
private val TextPrim = Color(0xFFE6EDF3)
private val TextSecond = Color(0xFF8B949E)
private val CardBorder = Color(0xFF30363D)

@Composable
fun SettingsScreen(
    onBack: () -> Unit
) {
    Scaffold(
        containerColor = BgDark,
        modifier = Modifier.statusBarsPadding(),
        topBar = {
            Column {
                Spacer(modifier = Modifier.height(60.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад", tint = Accent)
                    }
                    Text(
                        text = "Настройки",
                        color = TextPrim,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp)
        ) {
            Text(
                text = "Приложение",
                color = Accent,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            SettingsGroup {
                SettingsItem(
                    icon = Icons.Default.Notifications,
                    title = "Уведомления",
                    subtitle = "Настроить напоминания об уроках"
                )
                HorizontalDivider(color = CardBorder, thickness = 1.dp)
                SettingsItem(
                    icon = Icons.Default.Info,
                    title = "О приложении",
                    subtitle = "Версия 1.0.0 (CodeCraft)"
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "CodeCraft — это платформа для изучения программирования в игровом формате. Развивайтесь и достигайте новых высот!",
                color = TextSecond,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
fun SettingsGroup(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Surface)
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp)),
        content = content
    )
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = Accent, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = title, color = TextPrim, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Text(text = subtitle, color = TextSecond, fontSize = 12.sp)
        }
    }
}
