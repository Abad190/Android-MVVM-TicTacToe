package org.proven.a3enraya.ui

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.proven.a3enraya.model.BoardModel

/**
 * VIEW: tauler de 3x3.
 */
@Composable
fun BoardGrid(
    board: BoardModel,
    onCellClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // LazyVerticalGrid: quadrícula que només dibuixa els elements necessaris.
    // GridCells.Fixed(3): exactament 3 columnes.
    // userScrollEnabled = false: el tauler és petit, no cal que faci scroll.
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = modifier,
        userScrollEnabled = false
    ) {
        // items(llista, key): crea un element per cada casella.
        // 'key' ajuda a Compose a identificar cada casella de manera estable.
        items(board.cells, key = { it.id }) { cell ->
            CellItemView(
                cell = cell,
                isWinning = cell.position in board.winningLine,
                onClick = { onCellClick(cell.position) }
            )
        }
    }
}