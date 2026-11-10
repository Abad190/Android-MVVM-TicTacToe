package org.proven.a3enraya.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.proven.a3enraya.model.CellModel

/**
 * VIEW: dibuixa UNA casella.
 * No sap res del ViewModel: rep les dades i un callback (onClick) per avisar del clic.
 * Aquest patró s'anomena "state hoisting" (l'estat i les accions viuen a dalt).
 */
@Composable
fun CellItemView(
    cell: CellModel,
    isWinning: Boolean,          // true si forma part de la línia guanyadora
    onClick: () -> Unit,         // funció que cridarem quan es toqui la casella
    modifier: Modifier = Modifier
) {
    // Color segons el símbol
    val symbolColor = when (cell.symbol) {
        "X" -> Color(0xFF1E88E5)
        "O" -> Color(0xFFE53935)
        else -> Color.Transparent
    }

    // Surface és un contenidor de Material 3 amb color de fons, forma i vora
    Surface(
        modifier = modifier
            .aspectRatio(1f)   // aspectRatio(1f): amplada = alçada, així la casella és quadrada
            .padding(4.dp)
            .clickable { onClick() }, // clickable: fa que el component respongui als tocs
        shape = MaterialTheme.shapes.medium,
        color = if (isWinning) Color(0xFFC8E6C9) else MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.outline)
    ) {
        // Box amb contentAlignment centra el seu contingut
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = cell.symbol,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = symbolColor
            )
        }
    }
}