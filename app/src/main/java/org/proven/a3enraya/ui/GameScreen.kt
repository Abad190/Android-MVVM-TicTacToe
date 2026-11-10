package org.proven.a3enraya.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.proven.a3enraya.viewmodel.GameViewModel

/**
 * VIEW: pantalla principal. Obté el ViewModel, observa l'estat i el dibuixa.
 */
@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    viewModel: GameViewModel = viewModel() // viewModel(): crea o recupera el ViewModel lligat a la pantalla
) {
    // collectAsState(): converteix el StateFlow en un State de Compose.
    // Cada cop que el flow emet un valor nou, Compose recompon aquesta funció.
    // 'by' (delegat) permet usar 'board' directament sense escriure .value.
    val board by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Tres en ratlla",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(16.dp))

        // Missatge d'estat: victòria, empat o torn actual
        val message = when {
            board.winner != null -> "Ha guanyat ${board.winner}!"
            board.isDraw -> "Empat!"
            else -> "Torn de: ${board.currentTurn}"
        }
        Text(text = message, style = MaterialTheme.typography.titleLarge)

        Spacer(Modifier.height(16.dp))

        BoardGrid(
            board = board,
            onCellClick = viewModel::onCellClick, // referència a mètode: equival a { viewModel.onCellClick(it) }
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))

        Button(onClick = { viewModel.restartGame() }) {
            Text("Reiniciar partida")
        }
    }
}