package org.proven.a3enraya.model

/**
 * MODEL: representa UNA casella del tauler.
 *
 * És una data class immutable (tot són 'val'). Per això, quan una casella
 * canvia, no la modifiquem: en creem una còpia amb .copy().
 *
 * @param id       identificador únic (també l'usem com a 'key' a la llista de Compose)
 * @param position posició al tauler, de 0 a 8 (fila * 3 + columna)
 * @param symbol   "X", "O" o "" (buit)
 */
data class CellModel(
    val id: Int,
    val position: Int,
    val symbol: String = ""
) {
    // Propietat calculada: ens estalvia escriure symbol.isEmpty() arreu
    val isEmpty: Boolean get() = symbol.isEmpty()
}