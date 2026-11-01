package com.vanta.reporter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                VantaAppContent()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VantaAppContent() {
    var showSplash by remember { mutableStateOf(true) }
    var currentTab by remember { mutableStateOf(0) }
    var isReporting by remember { mutableStateOf(false) }
    var reportCount by remember { mutableStateOf(0) }
    var selectedPlatform by remember { mutableStateOf("فيسبوك") }
    var targetAccount by remember { mutableStateOf("") }
    var reportReason by remember { mutableStateOf("حساب زائف / انتهاك معايير") }
    
    val reportLogs = remember { mutableStateListOf<String>() }

    if (showSplash) {
        LaunchedEffect(Unit) {
            delay(2500)
            showSplash = false
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF030303)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "🔥 VANTA SYSTEM 🔥",
                    color = Color(0xFFFF1A1A),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                CircularProgressIndicator(color = Color(0xFFFF1A1A))
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "صُنع بوسط كريم بيك", color = Color(0xFFE8DCC8).copy(alpha = 0.5f))
            }
        }
        return
    }

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color(0xFF0A0A0A)) {
                NavigationBarItem(
                    selected = currentTab == 0,
                    onClick = { currentTab = 0 },
                    label = { Text("البلاغات", color = Color.White) },
                    icon = { Text("🔥") }
                )
                NavigationBarItem(
                    selected = currentTab == 1,
                    onClick = { currentTab = 1 },
                    label = { Text("السجل", color = Color.White) },
                    icon = { Text("📜") }
                )
                NavigationBarItem(
                    selected = currentTab == 2,
                    onClick = { currentTab = 2 },
                    label = { Text("الإعدادات", color = Color.White) },
                    icon = { Text("⚙️") }
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.sweepGradient(
                        colors = listOf(Color(0xFF0A0A0A), Color(0xFF260000), Color(0xFF0A0A0A))
                    )
                )
        ) {
            when (currentTab) {
                0 -> ReportingScreen(
                    selectedPlatform = selectedPlatform,
                    onPlatformSelect = { selectedPlatform = it },
                    targetAccount = targetAccount,
                    onTargetChange = { targetAccount = it },
                    reportReason = reportReason,
                    onReasonChange = { reportReason = it },
                    isReporting = isReporting,
                    reportCount = reportCount,
                    onStartReport = {
                        if (targetAccount.isNotBlank()) {
                            isReporting = true
                            reportCount = 0
                            CoroutineScope(Dispatchers.IO).launch {
                                for (i in 1..100) {
                                    reportCount = i
                                    val logMessage = "[$selectedPlatform] بلاغ متكرر #$i إلى: $targetAccount ($reportReason)"
                                    reportLogs.add(0, logMessage)
                                    delay(150)
                                }
                                isReporting = false
                            }
                        }
                    }
                )
                1 -> LogsScreen(logs = reportLogs)
                2 -> SettingsScreen()
            }
        }
    }
}

@Composable
fun ReportingScreen(
    selectedPlatform: String,
    onPlatformSelect: (String) -> Unit,
    targetAccount: String,
    onTargetChange: (String) -> Unit,
    reportReason: String,
    onReasonChange: (String) -> Unit,
    isReporting: Boolean,
    reportCount: Int,
    onStartReport: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🔥 VANTA SYSTEM v1.0 🔥",
            color = Color(0xFFFF1A1A),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(
                onClick = { onPlatformSelect("فيسبوك") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedPlatform == "فيسبوك") Color(0xFFFF1A1A) else Color(0xFF1A1A1A)
                )
            ) {
                Text("🔵 وحدة فيسبوك", color = Color.White)
            }
            Button(
                onClick = { onPlatformSelect("واتساب") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedPlatform == "واتساب") Color(0xFF00FF66) else Color(0xFF1A1A1A)
                )
            ) {
                Text("🟢 وحدة واتساب", color = if (selectedPlatform == "واتساب") Color.Black else Color.White)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = targetAccount,
            onValueChange = onTargetChange,
            label = { Text("معرف / رابط الهدف ($selectedPlatform)", color = Color.LightGray) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFFF1A1A),
                unfocusedBorderColor = Color.Gray,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = reportReason,
            onValueChange = onReasonChange,
            label = { Text("سبب البلاغ الجماعي", color = Color.LightGray) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFFF1A1A),
                unfocusedBorderColor = Color.Gray,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF111111)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isReporting) "⚡ جاري إرسال البلاغات المتكررة... ($reportCount/100)" else "💤 النظام في وضع الاستعداد",
                    color = if (isReporting) Color(0xFFFF1A1A) else Color.Gray,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onStartReport,
            enabled = !isReporting && targetAccount.isNotBlank(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF1A1A)),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text(
                text = if (isReporting) "جارِ تنفيذ البلاغات المتكررة..." else "بدء الإبلاغ الجماعي المتكرر 💀🔥",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.weight(1f))
        Text(text = "صُنع بوسط كريم بيك", color = Color(0xFFE8DCC8).copy(alpha = 0.45f))
    }
}

@Composable
fun LogsScreen(logs: List<String>) {
    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        Text("📜 سجل البلاغات المحلي", color = Color(0xFFFF1A1A), fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        if (logs.isEmpty()) {
            Text("لا توجد بلاغات مسجلة حتى الآن.", color = Color.Gray)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(logs) { log ->
                    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF181818))) {
                        Text(text = log, color = Color.White, modifier = Modifier.padding(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        Text("⚙️ شاشة الإعدادات", color = Color(0xFFFF1A1A), fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(20.dp))
        Text("• تكرار البلاغات: مفعل (100 بلاغ/دفعة)", color = Color.White)
        Spacer(modifier = Modifier.height(10.dp))
        Text("• سرعة الإرسال: فائقة (150ms)", color = Color.White)
        Spacer(modifier = Modifier.height(10.dp))
        Text("• دعم اللغات: العربية (RTL)", color = Color.White)
        Spacer(modifier = Modifier.weight(1f))
        Text(text = "صُنع بوسط كريم بيك", color = Color(0xFFE8DCC8).copy(alpha = 0.45f))
    }
}
