package pl.workoutplanner.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

fun getCategoryColor(category: String): Color {
    return when (category) {
        "Klatka piersiowa" -> Color(0xFFE57373)
        "Plecy" -> Color(0xFF64B5F6)
        "Nogi" -> Color(0xFF81C784)
        "Barki" -> Color(0xFFFFB74D)
        "Ramiona" -> Color(0xFFBA68C8)
        "Brzuch" -> Color(0xFF4DD0E1)
        else -> Color(0xFF90A4AE)
    }
}

@Composable
fun CategoryBadge(category: String, modifier: Modifier = Modifier) {
    val color = getCategoryColor(category)
    Text(
        text = category,
        color = color,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        maxLines = 1,
        softWrap = false,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}
