package org.proven.a3enraya.model

/**
 * MODEL: representa TOT l'estat de la partida i conté la lògica pura del joc.
 * No coneix Android, ni Compose, ni el ViewModel. Per això es pot testejar
 * amb un test unitari normal.
 *
 * Tots els mètodes retornen un BoardModel NOU (immutabilitat) mitjançant .copy().
 */
data class BoardModel(
    val cells: List<CellModel>,
    val currentTurn: String = "X",          // de qui és el torn: "X" o "O"
    val winner: String? = null,             // null = encara no hi ha guanyador
    val isDraw: Boolean = false,            // true si el tauler és ple sense guanyador
    val isGameOver: Boolean = false,        // true si hi ha guanyador o empat
    val winningLine: List<Int> = emptyList() // posicions de la línia guanyadora (per ressaltar-la)
) {

    /**
     * Fa una jugada a la posició indicada.
     * - Si la partida ja ha acabat o la casella està ocupada, retorna el mateix tauler (no canvia res).
     * - Si no, retorna un tauler nou amb el símbol col·locat, el torn canviat i el guanyador comprovat.
     */
    fun makeMove(position: Int): BoardModel {
        // Validacions: no deixem jugar si el joc ha acabat o la casella no existeix / està ocupada
        if (isGameOver) return this
        if (position !in cells.indices) return this
        if (!cells[position].isEmpty) return this

        // map recorre la llista i crea una llista NOVA; només canviem la casella clicada amb .copy()
        val newCells = cells.map { cell ->
            if (cell.position == position) cell.copy(symbol = currentTurn) else cell
        }

        // Canviem el torn: si era "X" passa a "O" i viceversa
        val nextTurn = if (currentTurn == "X") "O" else "X"

        // Creem el tauler nou i li passem la comprovació de victòria / empat
        return copy(cells = newCells, currentTurn = nextTurn).checkWinner()
    }

    /**
     * Comprova si hi ha guanyador o empat i retorna un tauler actualitzat.
     */
    fun checkWinner(): BoardModel {
        for (line in WINNING_LINES) {
            val (a, b, c) = line // desestructuració: reparteix els 3 valors de la llista en 3 variables
            val symbolA = cells[a].symbol

            // Hi ha victòria si la primera casella no és buida i les tres tenen el mateix símbol
            if (symbolA.isNotEmpty() &&
                symbolA == cells[b].symbol &&
                symbolA == cells[c].symbol
            ) {
                return copy(
                    winner = symbolA,
                    isGameOver = true,
                    winningLine = line
                )
            }
        }

        // Si no hi ha guanyador però no queden caselles buides => empat
        if (cells.none { it.isEmpty }) {
            return copy(isDraw = true, isGameOver = true)
        }

        // Si no passa res d'això, la partida continua igual
        return this
    }

    companion object {
        // Les 8 combinacions guanyadores (posicions 0..8)
        private val WINNING_LINES = listOf(
            listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8), // files
            listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8), // columnes
            listOf(0, 4, 8), listOf(2, 4, 6)                   // diagonals
        )

        /**
         * Mètode de fàbrica: crea un tauler buit de 9 caselles, torn de "X".
         */
        fun createInitialBoard(): BoardModel {
            // List(9) { ... } crea una llista de 9 elements; 'index' va de 0 a 8
            val emptyCells = List(9) { index -> CellModel(id = index, position = index) }
            return BoardModel(cells = emptyCells)
        }
    }
}