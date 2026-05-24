package ug.ac.ndejje.myapp

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.*
import androidx.glance.appwidget.*
import androidx.glance.layout.*
import androidx.glance.text.*
import androidx.glance.unit.ColorProvider

class FinanceWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = FinanceWidget()
}

class FinanceWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val prefs = context.getSharedPreferences("widget_prefs", Context.MODE_PRIVATE)
            val balance = prefs.getFloat("widget_balance", 0f)
            val currency = prefs.getString("widget_currency", "Shs") ?: "Shs"
            
            WidgetContent(balance, currency)
        }
    }
}

@Composable
fun WidgetContent(balance: Float, currency: String) {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Total Balance",
            style = TextStyle(
                fontSize = 14.sp,
                color = ColorProvider(Color.Gray)
            )
        )
        Spacer(modifier = GlanceModifier.height(8.dp))
        Text(
            text = "$currency ${String.format("%,.0f", balance)}",
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = ColorProvider(Color(0xFF4CAF50))
            )
        )
    }
}
