# Tres en Ratlla amb MVVM (Kotlin + Jetpack Compose)

Projecte fet amb Kotlin, Jetpack Compose i Android Studio, seguint MVVM amb model separat.

---

# 1. Anàlisi i documentació de l'arquitectura MVVM

## 1.1. Documentació dels components (basant-me en `MVVMSimple`)

### Model
- **Què guarda:** les dades pures de l'aplicació. Normalment són `data class` de Kotlin (per exemple, una casella amb el seu `id` i el seu valor).
- **Funció principal:** descriure com són les dades. No sap res de pantalles, botons ni de Compose. Només és informació.

### ViewModel
- **Quina feina fa:** és l'intermediari. Rep les accions de l'usuari (clics), decideix què s'ha de fer amb les dades i actualitza l'estat.
- **Com es comunica amb el Model:** crea/guarda instàncies del Model i les modifica quan arriba una acció (a `MVVMSimple` el ViewModel fa directament els canvis sobre les dades).
- **Com exposa les dades a la UI:** mitjançant un estat observable (`mutableStateOf` o `StateFlow`). La UI es "subscriu" a aquest estat i es redibuixa sola quan canvia.

### View (UI / Compose)
- **Paper:** dibuixar l'estat actual i res més. Les funcions `@Composable` són una funció de l'estat: donat un estat, pinten una pantalla.
- **Com rep els canvis d'estat:** llegeix l'estat del ViewModel (amb `collectAsState()` si és `StateFlow`, o directament si és `mutableStateOf`). Quan l'estat canvia, Compose executa una recomposició i només torna a pintar el que ha canviat.
- **Com notifica les accions de l'usuari:** cridant mètodes del ViewModel (`onClick = { viewModel.onIncrement() }`). La View no modifica mai les dades directament, només avisa.

## 1.2. Anàlisi comparatiu (`MVVMSimple` vs `MVVM-separatModel`)

### Estructura i separació de responsabilitats

| | `MVVMSimple` | `MVVM-separatModel` |
|---|---|---|
| Model | Dades senzilles (data classes), amb poca o cap lògica | Model complet i separat: `CellModel` + `BoardModel` amb la seva pròpia lògica |
| ViewModel | Conté dades i també part de la lògica | Només gestiona l'estat de la UI i delega la feina al Model |
| Fitxers/classes | Tot més barrejat | Cada cosa al seu lloc |

**On s'executa la lògica de negoci:**
- A `MVVMSimple`, la lògica del joc (comprovar combinacions, girar cartes…) viu dins del ViewModel, juntament amb la gestió de l'estat.
- A `MVVM-separatModel`, la lògica viu dins del Model (`BoardModel`), en mètodes purs. El ViewModel només crida aquests mètodes i guarda el resultat.

### Immutabilitat i gestió de l'estat
- **Com es modifica el tauler:** no es canvia l'objecte existent. Cada mètode del Model (per exemple `makeMove`) retorna un `BoardModel` nou amb els canvis aplicats, i el ViewModel substitueix l'estat antic pel nou.
- **El paper de `.copy()`:** és un mètode que Kotlin genera automàticament a les `data class`. Crea una còpia de l'objecte on només canvies els camps que indiques; la resta es mantenen.
- **Per què és important per a l'arquitectura reactiva:** `StateFlow` i Compose detecten que l'estat ha canviat comparant el valor antic amb el nou. Si es modifiqués el mateix objecte, serien iguals i la pantalla no s'actualitzaria. Amb `.copy()` sempre hi ha un objecte nou i el canvi es detecta.

### Avantatges de la separació
- **Responsabilitat única:** el ViewModel només gestiona l'estat; el Model només sap les regles del joc.
- **Testabilitat:** `BoardModel` és Kotlin pur, sense Android. Puc comprovar `checkWinner()` amb un test unitari ràpid, sense emulador.
- **Reutilització:** les mateixes regles es poden usar des d'un altre ViewModel o una altra pantalla.
- **Manteniment:** si canvio una regla, només toco el Model. Si canvio com s'exposa l'estat, només toco el ViewModel.
- **Llegibilitat:** un ViewModel que ho fa tot es converteix en un fitxer enorme i difícil de seguir.

## 1.3. `StateFlow` / `MutableStateFlow` vs `mutableStateOf` / `State<T>`

### Per a què serveix cadascun
- **`StateFlow` / `MutableStateFlow`:** flux observable de Kotlin (coroutines) amb un valor actual que avisa els observadors quan canvia. `MutableStateFlow` es pot modificar (`.value = ...`) i `StateFlow` és la versió de només lectura. No depèn de Compose i s'hi llegeix amb `collectAsState()`.
- **`mutableStateOf` / `State<T>`:** sistema d'estat propi de Compose. Quan el valor canvia, les funcions `@Composable` que l'han llegit es recomponen soles. S'hi llegeix directament.

### Diferències

| Aspecte | `StateFlow` / `MutableStateFlow` | `mutableStateOf` / `State<T>` |
|---|---|---|
| Biblioteca | Kotlin coroutines | Compose runtime |
| Depèn de Compose? | No | Sí |
| Com la UI el llegeix | `collectAsState()` / `collectAsStateWithLifecycle()` | Directament (`vm.count`) |
| Valor inicial | Obligatori | Obligatori |
| Operadors de flux (`map`, `combine`, `debounce`, `filter`…) | Sí | No |
| Ideal per | Estat que ve de capes de dades, repositoris, corrutines | Estat simple de UI i estat local de Composables |
| Provar-lo en tests | Fàcil, sense Compose | Necessita entorn de Compose en alguns casos |
| Detecció de canvis | Emet només si el valor és diferent (`equals`) | També compara el valor per decidir si recomposar |
| Codi necessari | Una mica més (privat + `asStateFlow()` + `collectAsState()`) | Menys codi |

### Quin triaria
Triaria **`StateFlow` / `MutableStateFlow`** per al Tres en Ratlla:
- **Encaixa amb l'arquitectura demanada:** el ViewModel és independent de Compose, i `StateFlow` no importa res de Compose.
- **Encapsulació clara:** el patró `_uiState` privat + `uiState` públic amb `asStateFlow()` garanteix que només el ViewModel pot canviar l'estat.
- **Encaixa amb l'estat immutable:** tot l'estat del joc és un únic `BoardModel` immutable, i cada jugada fa `_uiState.value = board.makeMove(position)` amb una nova còpia. És el que recomana Android per a l'estat de la UI.
- **Escalabilitat:** si més endavant s'afegeix persistència, un historial de partides o un mode en xarxa, `StateFlow` ja funciona amb corrutines i operadors de flux.
- **Testabilitat:** puc provar el ViewModel sense dependre de Compose.

---

# 2. Arquitectura del Tres en Ratlla

```
org.proven.a3enraya
├── model       CellModel.kt, BoardModel.kt
├── viewmodel   GameViewModel.kt
├── ui          GameScreen.kt, BoardGrid.kt, CellItemView.kt, theme/
└── MainActivity.kt
```

- **`CellModel`:** una casella (`id`, `position`, `symbol`).
- **`BoardModel`:** caselles, torn, guanyador, empat i fi de partida. Conté `createInitialBoard()`, `makeMove()` i `checkWinner()`, que retornen còpies amb `.copy()`.
- **`GameViewModel`:** `MutableStateFlow` privat exposat amb `asStateFlow()`, amb `onCellClick()` i `restartGame()`.
- **`GameScreen`:** llegeix l'estat amb `collectAsState()` i mostra torn, resultat i botó de reinici.
- **`BoardGrid` / `CellItemView`:** tauler 3x3 amb `LazyVerticalGrid` i cada casella clicable.

**Flux:** toc a la casella → `onClick` → `GameViewModel.onCellClick()` → `BoardModel.makeMove()` retorna un tauler nou → `_uiState.value = nou` → `collectAsState()` → la UI es redibuixa.

![Partida en curs](capturas/partida.png)

---

# 3. Conceptes i mètodes apresos

| Concepte | Què fa | On |
|---|---|---|
| `MutableStateFlow` | Guarda un valor observable i modificable (`.value = ...`) | `GameViewModel` |
| `asStateFlow()` | El converteix en `StateFlow` de només lectura | `GameViewModel` |
| `collectAsState()` | Converteix un `StateFlow` en estat de Compose; la UI es recompon quan canvia | `GameScreen` |
| `by` | Permet escriure `board` en comptes de `board.value` | `GameScreen` |
| `viewModel()` | Crea o recupera el ViewModel (no es perd en girar el mòbil) | `GameScreen` |
| `.copy()` | Crea una còpia d'una `data class` canviant només alguns camps | `BoardModel` |
| `List(9) { }` | Crea una llista de 9 elements | `BoardModel` |
| `map` / `none` | Transforma una llista / comprova que cap element compleix una condició | `BoardModel` |
| `companion object` | Membres de classe, com el mètode de fàbrica | `BoardModel` |
| `LazyVerticalGrid` | Quadrícula; `GridCells.Fixed(3)` fixa 3 columnes | `BoardGrid` |
| `aspectRatio(1f)` | Fa la casella quadrada | `CellItemView` |
| `clickable` | Fa que un component respongui als tocs | `CellItemView` |

Exemple per provar `.copy()` en un *scratch file* d'Android Studio:

```kotlin
data class Casella(val id: Int, val simbol: String = "")

val a = Casella(1)
val b = a.copy(simbol = "X")
println(a)        // Casella(id=1, simbol=)
println(b)        // Casella(id=1, simbol=X)
println(a === b)  // false: són objectes diferents
```
