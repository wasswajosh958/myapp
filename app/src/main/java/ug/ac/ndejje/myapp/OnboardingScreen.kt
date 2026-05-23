package ug.ac.ndejje.myapp

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    var step by remember { mutableIntStateOf(1) }
    
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Step $step of 4",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                AnimatedContent(targetState = step, label = "OnboardingStep") { currentStep ->
                    when (currentStep) {
                        1 -> OnboardingContent(
                            icon = Icons.Filled.AccountBalanceWallet,
                            title = "Welcome to FinTrack!",
                            description = "Your private AI-powered financial assistant. All data stays on your device unless you enable cloud."
                        )
                        2 -> OnboardingContent(
                            icon = Icons.Filled.PrivacyTip,
                            title = "Privacy First",
                            description = "• Your transactions never leave your phone unless you choose cloud AI\n• Biometric lock available\n• You control all data export"
                        )
                        3 -> OnboardingContent(
                            icon = Icons.Filled.Mic,
                            title = "Voice Assistant",
                            description = "• Ask questions by speaking\n• Get spoken answers\n• Works offline\n\nTry asking: \"How does this app work?\""
                        )
                        4 -> OnboardingContent(
                            icon = Icons.Filled.AddCard,
                            title = "Add your first account",
                            description = "Link your Checking, Savings, or Cash accounts to get started. You can also skip this and add them later."
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Step Indicators
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    repeat(4) { i ->
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    if (step == i + 1) MaterialTheme.colorScheme.primary 
                                    else MaterialTheme.colorScheme.outlineVariant,
                                    CircleShape
                                )
                        )
                    }
                }
                
                Button(
                    onClick = {
                        if (step < 4) step++ else onFinish()
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(if (step < 4) "Continue" else "Get Started")
                    Icon(Icons.Filled.ArrowForward, contentDescription = null, modifier = Modifier.padding(start = 8.dp).size(18.dp))
                }
            }
        }
    }
}

@Composable
fun OnboardingContent(icon: ImageVector, title: String, description: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(120.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.padding(32.dp).fillMaxSize(),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        
        Spacer(modifier = Modifier.height(40.dp))
        
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 24.sp
        )
    }
}
