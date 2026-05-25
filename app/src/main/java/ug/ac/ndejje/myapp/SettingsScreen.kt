package ug.ac.ndejje.myapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToAppearance: () -> Unit,
    onRestartOnboarding: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SettingsSection(title = "Account") {
                SettingsItem(
                    icon = Icons.Filled.Person,
                    title = "Profile",
                    subtitle = "Name, email, currency",
                    onClick = onNavigateToProfile
                )
            }

            SettingsSection(title = "Privacy & Security") {
                SettingsToggleItem(icon = Icons.Filled.Lock, title = "Biometric lock", initialValue = true)
                SettingsToggleItem(icon = Icons.Filled.CloudOff, title = "Offline mode (AI only)", initialValue = false)
                SettingsItem(icon = Icons.Filled.History, title = "Clear conversation history")
                SettingsItem(icon = Icons.Filled.Download, title = "Export my data (GDPR)")
            }

            SettingsSection(title = "AI Assistant Settings") {
                SettingsToggleItem(icon = Icons.Filled.AutoAwesome, title = "Enable AI", initialValue = true)
                SettingsToggleItem(icon = Icons.Filled.RecordVoiceOver, title = "Voice responses", initialValue = true)
                SettingsItem(icon = Icons.Filled.FileDownload, title = "Download offline model", subtitle = "2.3 GB")
            }

            SettingsSection(title = "App Preferences") {
                SettingsItem(
                    icon = Icons.Filled.Palette,
                    title = "Appearance",
                    subtitle = "Theme, colors, dark mode",
                    onClick = onNavigateToAppearance
                )
                SettingsItem(icon = Icons.Filled.Notifications, title = "Notifications")
            }

            SettingsSection(title = "About") {
                SettingsItem(icon = Icons.Filled.Info, title = "Version", subtitle = "1.0.0")
                SettingsItem(icon = Icons.Filled.Policy, title = "Privacy Policy")
                SettingsItem(
                    icon = Icons.Filled.RestartAlt,
                    title = "Run Onboarding",
                    subtitle = "Review app features and setup",
                    onClick = onRestartOnboarding
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppearanceScreen(
    currentTheme: ThemeMode,
    currentAccent: AccentColor,
    onThemeChange: (ThemeMode) -> Unit,
    onAccentChange: (AccentColor) -> Unit,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Appearance") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text("THEME MODE", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ThemeCard("Light", Icons.Filled.LightMode, currentTheme == ThemeMode.LIGHT, Modifier.weight(1f)) { onThemeChange(ThemeMode.LIGHT) }
                ThemeCard("Dark", Icons.Filled.DarkMode, currentTheme == ThemeMode.DARK, Modifier.weight(1f)) { onThemeChange(ThemeMode.DARK) }
                ThemeCard("System", Icons.Filled.SettingsSuggest, currentTheme == ThemeMode.SYSTEM, Modifier.weight(1f)) { onThemeChange(ThemeMode.SYSTEM) }
            }

            Text("ACCENT COLOR", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                AccentOption("Green (Finance)", Color(0xFF4CAF50), currentAccent == AccentColor.GREEN) { onAccentChange(AccentColor.GREEN) }
                AccentOption("Blue", Color(0xFF2196F3), currentAccent == AccentColor.BLUE) { onAccentChange(AccentColor.BLUE) }
                AccentOption("Purple", Color(0xFF9C27B0), currentAccent == AccentColor.PURPLE) { onAccentChange(AccentColor.PURPLE) }
            }

            Text("PREVIEW", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Sample Card", fontWeight = FontWeight.Bold)
                    Text("This is how your financial data will look.")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = {}) { Text("Primary") }
                        OutlinedButton(onClick = {}) { Text("Secondary") }
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(onClick = onNavigateBack, modifier = Modifier.weight(1f)) {
                    Text("Apply Theme")
                }
                TextButton(onClick = { 
                    onThemeChange(ThemeMode.SYSTEM)
                    onAccentChange(AccentColor.GREEN)
                }, modifier = Modifier.weight(1f)) {
                    Text("Reset to Default")
                }
            }
        }
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                content()
            }
        }
    }
}

@Composable
fun SettingsItem(icon: ImageVector, title: String, subtitle: String? = null, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Medium)
            if (subtitle != null) {
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        }
        Icon(Icons.Filled.ChevronRight, null, tint = Color.LightGray)
    }
}

@Composable
fun SettingsToggleItem(icon: ImageVector, title: String, initialValue: Boolean) {
    var checked by remember { mutableStateOf(initialValue) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(16.dp))
        Text(title, modifier = Modifier.weight(1f), fontWeight = FontWeight.Medium)
        Switch(checked = checked, onCheckedChange = { checked = it })
    }
}

@Composable
fun ThemeCard(label: String, icon: ImageVector, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    OutlinedCard(
        onClick = onClick,
        modifier = modifier,
        border = BorderStroke(if (selected) 2.dp else 1.dp, if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, null, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(label, style = MaterialTheme.typography.bodyMedium)
            if (selected) {
                Icon(Icons.Filled.CheckCircle, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
fun AccentOption(label: String, color: Color, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(24.dp).background(color, CircleShape))
        Spacer(modifier = Modifier.width(16.dp))
        Text(label, modifier = Modifier.weight(1f))
        RadioButton(selected = selected, onClick = onClick)
    }
}
