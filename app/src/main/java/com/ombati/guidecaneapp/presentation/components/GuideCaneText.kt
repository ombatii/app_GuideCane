package com.ombati.guidecaneapp.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ombati.EduAUVICWANTPreFontFamily


@Composable
fun GuideCaneText(
    modifier: Modifier = Modifier
) {
    Text(text = "GuideCaneApp",
         style = TextStyle(
             fontFamily = EduAUVICWANTPreFontFamily,
             fontWeight = FontWeight.Bold,
             fontSize = 20.sp,
             color = MaterialTheme.colorScheme.primary
         ),
        modifier = modifier.padding(10.dp)

        )
}

