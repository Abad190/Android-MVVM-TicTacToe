package org.proven.a3enraya.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.proven.a3enraya.model.BoardModel

/**
 * VIEWMODEL: guarda l'estat de la UI i rep les accions de l'usuari.
 * No conté la lògica del joc: la delega a BoardModel.
 *
 * Hereta de ViewModel() perquè l'estat sobrevisqui a rotacions de pantalla.
 */
class GameViewModel : ViewModel() {

    // MutableStateFlow: flux amb un valor actual que es pot canviar.
    // És PRIVAT: només el ViewModel el pot modificar.
    private val _uiState = MutableStateFlow(BoardModel.createInitialBoard())

    // asStateFlow(): el converteix en StateFlow de NOMÉS LECTURA.
    // La View el pot observar però no modificar.
    val uiState: StateFlow<BoardModel> = _uiState.asStateFlow()

    /** La View avisa que l'usuari ha tocat una casella. */
    fun onCellClick(position: Int) {
        // .value llegeix l'estat actual; li apliquem la jugada i guardem el tauler nou.
        // Com que és un objecte diferent (.copy()), StateFlow emet i la UI es redibuixa.
        _uiState.value = _uiState.value.makeMove(position)
    }

    /** Torna a començar: substitueix l'estat per un tauler buit. */
    fun restartGame() {
        _uiState.value = BoardModel.createInitialBoard()
    }
}