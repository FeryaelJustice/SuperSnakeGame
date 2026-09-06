# Informe de Auditoría Técnica, Seguridad, Rendimiento y Clean Architecture

**Proyecto:** SuperSnakeGame  
**Fecha y Hora de la Auditoría Inicial:** 2026-09-06 17:05:27 CEST  
**Herramientas Empleadas:** Android CLI (`android`), Gradle Daemon (`gradlew`), Android Lint Analyzer (AGP 9.4.0), Compose Compiler Inspector.

---

## 1. Resumen Ejecutivo

Se realizó una revisión integral del proyecto orientada a:
1. **Seguridad y configuración:** Eliminación de exposición indebida de identificadores y configuraciones del manifiesto.
2. **Rendimiento de Jetpack Compose:** Erradicación de bucles de recomposición forzados a 60-120 FPS que afectaban el consumo de CPU y batería.
3. **Clean Architecture e Inversión de Dependencias:** Desacoplamiento de la capa `domain/` frente a la capa `ui/` y librerías externas.
4. **Reusabilidad y Buenas Prácticas:** Estandarización de modificadores, eliminación de cierres obsoletos en gestos y resolución de advertencias de Android Lint (KTX, autoboxing, manejo de excepciones de credenciales).

---

## 2. Hallazgos y Correcciones Detalladas

### A. Seguridad y Configuración del Manifiesto

- **Estado previo (2026-09-06 17:05:27):**  
  En `app/src/main/AndroidManifest.xml`, existía la siguiente etiqueta dentro de `<application>`:
  ```xml
  <meta-data
      android:name="com.google.android.geo.API_KEY"
      android:value="@string/default_web_client_id"/>
  ```
  Esto configuraba erróneamente el Web Client ID de OAuth en la clave de la API de Google Maps (`geo.API_KEY`), a pesar de que el juego no utiliza mapas ni geolocalización.
- **Riesgo:** Confusión en los servicios de Google Play, advertencias en revisiones de la tienda e inicialización no deseada de metadatos.
- **Corrección aplicada:** Se eliminó la etiqueta `<meta-data>` innecesaria del manifiesto.
- **Archivos modificados:** `app/src/main/AndroidManifest.xml`.

---

### B. Arquitectura Limpia y Desacoplamiento del Dominio

- **Estado previo (2026-09-06 17:05:27):**  
  En `app/src/main/java/com/feryaeljustice/supersnakegame/domain/GameLogic.kt`, la capa de dominio importaba directamente una clase de la capa de presentación:
  ```kotlin
  import com.feryaeljustice.supersnakegame.ui.screens.game.SnakeGameState
  ```
  Esto violaba la regla estricta de Clean Architecture donde la capa de dominio debe ser Kotlin puro sin dependencias hacia UI o capas externas.
- **Corrección aplicada:**
  1. Se creó el modelo de dominio puro `SnakeGameState` en el paquete `com.feryaeljustice.supersnakegame.domain`.
  2. Se eliminó la dependencia de presentación en `GameLogic.kt`.
  3. En `app/src/main/java/com/feryaeljustice/supersnakegame/ui/screens/game/SnakeGameState.kt` se definió un `typealias` hacia el modelo de dominio para preservar total compatibilidad sin duplicar código.
- **Archivos creados:**  
  `app/src/main/java/com/feryaeljustice/supersnakegame/domain/SnakeGameState.kt`
- **Archivos modificados:**  
  `app/src/main/java/com/feryaeljustice/supersnakegame/domain/GameLogic.kt`  
  `app/src/main/java/com/feryaeljustice/supersnakegame/ui/screens/game/SnakeGameState.kt`

---

### C. Algoritmo y Prevención de ANR en `generateFood`

- **Estado previo (2026-09-06 17:05:27):**  
  En `GameLogic.kt`, la generación de comida utilizaba un bucle probabilístico sin cota:
  ```kotlin
  var candidate: Pair<Int, Int>
  do {
      candidate = Pair(Random.nextInt(cols), Random.nextInt(rows))
  } while (candidate in snake)
  return candidate
  ```
  Al crecer la serpiente y ocupar una gran porción del tablero (o el tablero completo en victoria), el bucle podía iterar miles de veces o entrar en un ciclo infinito bloqueante, provocando un ANR (Application Not Responding). Además, `candidate in snake` realizaba búsquedas lineales $O(N)$ en una lista.
- **Corrección aplicada:**
  1. Conversión de las posiciones de la serpiente a un `HashSet` para búsquedas en $O(1)$.
  2. Verificación de saturación total del tablero (`snakeSet.size >= totalCells`).
  3. Cuando la serpiente ocupa más del 70% del tablero, se computan de forma directa las celdas libres disponibles, seleccionando una aleatoria en $O(\text{celdas libres})$.
  4. Límite de 100 intentos aleatorios con respaldo secuencial determinista si la generación probabilística no encuentra celda rápidamente.
- **Archivos modificados:** `app/src/main/java/com/feryaeljustice/supersnakegame/domain/GameLogic.kt`.

---

### D. Rendimiento de Jetpack Compose y Frecuencia de Recomposición

- **Estado previo (2026-09-06 17:05:27):**  
  En `SnakeGameScreen.kt`:
  ```kotlin
  var frameTick by remember { mutableLongStateOf(0L) }

  LaunchedEffect(Unit) {
      while (true) {
          withFrameNanos { nano ->
              frameTick = nano
          }
      }
  }
  ```
  Este estado `frameTick` forzaba la recomposición continua de TODA la jerarquía de `SnakeGameScreen` a 60-120 veces por segundo, afectando la barra de puntuación, el récord, los botones de navegación, el D-pad y los diálogos modales. El único propósito de `frameTick` era calcular una oscilación senoidal para la comida en `SnakeGameCanvas`.
  Además, los flujos de estado utilizaban `collectAsState()` en lugar de `collectAsStateWithLifecycle()`, manteniendo la recolección activa en segundo plano.
  Por último, `measuredCols` usaba `mutableStateOf(20)` generando autoboxing en cada asignación.
- **Corrección aplicada:**
  1. Se eliminó el `LaunchedEffect(Unit)` con `frameTick` de `SnakeGameScreen`.
  2. Se encapsuló la animación del pulso de la comida dentro de `SnakeGameCanvas` mediante `rememberInfiniteTransition`, de modo que el valor animado solo se lee en la fase de dibujo (`Canvas { ... }`), redibujando únicamente el lienzo sin recomponer la pantalla ni los composables adyacentes.
  3. Se migraron todos los flujos de `SnakeGameViewModel` a `collectAsStateWithLifecycle()` para respetar el ciclo de vida del usuario y ahorrar energía.
  4. Se corrigió `measuredCols` a `mutableIntStateOf(20)`.
- **Archivos modificados:**  
  `app/src/main/java/com/feryaeljustice/supersnakegame/ui/screens/game/SnakeGameScreen.kt`  
  `app/src/main/java/com/feryaeljustice/supersnakegame/ui/components/SnakeGameCanvas.kt`

---

### E. Robustez en Autenticación y Credential Manager

- **Estado previo (2026-09-06 17:05:27):**  
  En `AuthRepositoryImpl.kt`:
  - Los métodos de solicitud de credenciales estaban anotados con `@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)` (Android 14 / API 34), a pesar de que el proyecto tiene `minSdk = 24` y `androidx.credentials` ofrece compatibilidad desde API 14 mediante Google Play Services.
  - No se capturaban excepciones específicas recomendadas por Android Lint (`NoCredentialException`, `GetCredentialCancellationException`, `GetCredentialException`), marcando la advertencia `CredentialManagerMisuse`.
  - En `firebaseSignIn`, se utilizaba `suspendCancellableCoroutine` con `addOnCompleteListener` y `addOnFailureListener` simultáneamente. Ante un fallo en la autenticación, `resumeWithException` se invocaba dos veces, produciendo un fallo crítico no recuperable: `IllegalStateException: Already resumed`.
  - En los bloques de captura de excepciones genéricas (`catch (e: Exception)`), se corría el riesgo de tragar `CancellationException`, rompiendo la cancelación cooperativa de Kotlin Coroutines.
- **Corrección aplicada:**
  1. Se eliminó la anotación `@RequiresApi` incorrecta.
  2. Se implementó el manejo explícito de `NoCredentialException`, `GetCredentialCancellationException` y `GetCredentialException`.
  3. Se reemplazó el callback manual propenso a fallos por la extensión suspendida nativa `firebaseAuth.signInWithCredential(...).await()` soportada por `kotlinx-coroutines-play-services`, la cual implementa internamente `suspendCancellableCoroutine` conectada de forma segura con los tokens de cancelación de Google Play Tasks sin riesgo de doble reanudación.
  4. Se agregó la re-propagación explícita de `CancellationException` (`catch (e: CancellationException) { throw e }`) antes de los bloques genéricos para garantizar la cancelación cooperativa ante cambios de ciclo de vida o navegación.
- **Archivos modificados:** `app/src/main/java/com/feryaeljustice/supersnakegame/data/repository/AuthRepositoryImpl.kt`.

---

### F. Reusabilidad y Calidad de Componentes UI

- **Estado previo (2026-09-06 17:05:27):**  
  - `ButtonsDirectionController.kt`: El parámetro `modifier: Modifier` no tenía valor predeterminado, y el código de cada uno de los 4 botones direccionales estaba repetido con más de 70 líneas duplicadas.
  - `DirectionController.kt`: No aceptaba `modifier: Modifier = Modifier`, y `pointerInput(Unit)` capturaba la lambda `onDirectionChange` en un cierre estático, provocando retención de lambdas obsoletas.
  - `GoogleButton.kt`: No aceptaba `modifier: Modifier = Modifier`, y cuando estaba en estado de carga no existía separación entre el texto y el `CircularProgressIndicator`.
  - `GenericNavType.kt`: En `parseValue(value)` no se decodificaba el URI (`Uri.decode`), provocando fallos de deserialización de JSON cuando las rutas contenían caracteres codificados.
  - `GameSettingsSheet.kt` y `SettingsRepositoryImpl.kt`: Advertencias de Lint por no utilizar las extensiones KTX `toUri()` y `prefs.edit { ... }`.
- **Corrección aplicada:**
  1. Se añadieron modificadores predeterminados `modifier: Modifier = Modifier` a todos los componentes de la interfaz.
  2. En `ButtonsDirectionController`, se extrajo la función composable auxiliar `DirectionButton`, reduciendo la duplicación y mejorando la legibilidad.
  3. En `DirectionController`, se integró `rememberUpdatedState` para proteger el callback de gestos.
  4. En `GoogleButton`, se agregó el espacio de `10.dp` antes del indicador de progreso.
  5. En `GenericNavType`, se agregó `Uri.decode(value)` antes de `Json.decodeFromString`.
  6. Se adoptaron las extensiones oficiales KTX (`toUri()` y `prefs.edit`).
- **Archivos modificados:**  
  `app/src/main/java/com/feryaeljustice/supersnakegame/ui/components/ButtonsDirectionController.kt`  
  `app/src/main/java/com/feryaeljustice/supersnakegame/ui/components/DirectionController.kt`  
  `app/src/main/java/com/feryaeljustice/supersnakegame/ui/components/GoogleButton.kt`  
  `app/src/main/java/com/feryaeljustice/supersnakegame/ui/navigation/GenericNavType.kt`  
  `app/src/main/java/com/feryaeljustice/supersnakegame/ui/components/GameSettingsSheet.kt`  
  `app/src/main/java/com/feryaeljustice/supersnakegame/data/repository/SettingsRepositoryImpl.kt`

---

## 3. Matriz de Archivos Modificados y Creados

| Tipo | Archivo | Motivo de Cambio |
| :--- | :--- | :--- |
| **NUEVO** | `domain/SnakeGameState.kt` | Modelo de estado puro en capa de dominio (Clean Architecture). |
| **NUEVO** | `docs/audit-and-optimizations-report.md` | Documento histórico de auditoría, estado previo y correcciones. |
| **MODIFICADO** | `AndroidManifest.xml` | Eliminación de `geo.API_KEY` errónea. |
| **MODIFICADO** | `domain/GameLogic.kt` | Desacoplamiento de UI, prevención de ANR en `generateFood`. |
| **MODIFICADO** | `data/repository/AuthRepositoryImpl.kt` | Eliminación de `@RequiresApi(34)`, manejo de `NoCredentialException`, uso seguro de `.await()`. |
| **MODIFICADO** | `data/repository/SettingsRepositoryImpl.kt` | Adopción de KTX `edit { ... }`. |
| **MODIFICADO** | `ui/screens/game/SnakeGameState.kt` | Typealias al modelo de dominio. |
| **MODIFICADO** | `ui/screens/game/SnakeGameScreen.kt` | Eliminación de recomposiciones a 60 FPS, `collectAsStateWithLifecycle`, corrección de autoboxing. |
| **MODIFICADO** | `ui/components/SnakeGameCanvas.kt` | Animación de pulso aislada en fase de dibujo, sobrecarga retrocompatible. |
| **MODIFICADO** | `ui/components/ButtonsDirectionController.kt` | Modifier por defecto y extracción de `DirectionButton`. |
| **MODIFICADO** | `ui/components/DirectionController.kt` | Modifier por defecto y `rememberUpdatedState`. |
| **MODIFICADO** | `ui/components/GoogleButton.kt` | Modifier por defecto y espaciado de carga. |
| **MODIFICADO** | `ui/components/GameSettingsSheet.kt` | Uso de KTX `toUri()`. |
| **MODIFICADO** | `ui/navigation/GenericNavType.kt` | Decodificación `Uri.decode` y supresión controlada de deprecación. |
