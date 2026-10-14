package com.slashgil.formula1.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.slashgil.formula1.ui.theme.F1Dark
import com.slashgil.formula1.ui.theme.F1Red
import com.slashgil.formula1.ui.theme.F1TextSecondary

@Composable
fun StatBox(label: String, value: String) {
    Box(
        modifier = Modifier
            .width(100.dp)
            .background(F1Dark, RoundedCornerShape(8.dp))
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = label, color = F1TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text(text = value, color = F1Red, fontSize = 18.sp, fontWeight = FontWeight.Black)
        }
    }
}