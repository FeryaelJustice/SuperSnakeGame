# Sistema de Efectos de Audio - Super Snake Game

Este documento detalla la arquitectura, el diseño técnico, la integración con el ciclo de vida de Android y la persistencia del sistema de audio y efectos de sonido en **SuperSnakeGame**.

---

## 1. Justificación y Selección de API: `SoundPool` vs `MediaPlayer`

Para la reproducción de efectos de sonido en videojuegos 2D interactivos, la elección de la API multimedia es crítica para evitar problemas de latencia, consumo de memoria y bloqueos del hilo principal:

| Criterio | `MediaPlayer` | `SoundPool` (Implementado) |
| :--- | :--- | :--- |
| **Diseñado para** | Pistas largas, podcasts o música de fondo | Efectos de sonido cortos, repetitivos e instantáneos |
| **Latencia** | Alta (100 ms - 300 ms de descompresión bajo demanda) | Prácticamente nula (< 10 ms, decodificado en memoria PCM) |
| **Streams concurrentes** | Requiere múltiples instancias pesadas | Soporta múltiples streams simultáneos nativos con prioridades |
| **Consumo de memoria** | Decodifica en streaming con buffers dinámicos | Carga buffers de 16 bits sin comprimir una sola vez al inicio |
| **Control de volumen/tono** | Limitado al reproductor | Control dinámico por stream (volumen izquierdo, derecho, rate) |

En `SuperSnakeGame`, se implementó **`SoundPool`** mediante la clase [SoundEffectManager.kt](file:///c:/Users/nano9/AndroidStudioProjects/SuperSnakeGame/app/src/main/java/com/feryaeljustice/supersnakegame/data/audio/SoundEffectManager.kt) configurado con atributos nativos de audio para juegos:

```kotlin
val audioAttributes = AudioAttributes.Builder()
    .setUsage(AudioAttributes.USAGE_GAME)
    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
    .build()

val soundPool = SoundPool.Builder()
    .setMaxStreams(3)
    .setAudioAttributes(audioAttributes)
    .build()
```

---

## 2. Especificación Técnica del Recurso de Audio

El recurso de audio arcade retro [eat_apple.wav](file:///c:/Users/nano9/AndroidStudioProjects/SuperSnakeGame/app/src/main/res/raw/eat_apple.wav) fue diseñado con las siguientes características:

- **Ruta**: `app/src/main/res/raw/eat_apple.wav`
- **Formato**: PCM 16-bit Mono sin compresión (WAV).
- **Tasa de muestreo (Sample Rate)**: 44.1 kHz.
- **Duración**: ~90 milisegundos.
- **Tamaño de archivo**: ~7.8 KB.
- **Diseño sonoro**: Efecto retro de "mordisco / pop arcade" generado mediante barrido de frecuencia ascendente rápido (de 400 Hz a 950 Hz) combinado con un segundo armónico suave y una envolvente de caída exponencial rápida para no saturar el canal de audio tras ingestas consecutivas.

---

## 3. Integración con el Ciclo de Vida de Android

Para garantizar que el sonido no continúe reproduciéndose en segundo plano ni provoque fugas de memoria (`memory leaks`), se diseñó una integración bidireccional entre `DefaultLifecycleObserver` y `DisposableEffect` de Jetpack Compose.

### A. Observador de Ciclo de Vida en `SoundEffectManager`
[SoundEffectManager.kt](file:///c:/Users/nano9/AndroidStudioProjects/SuperSnakeGame/app/src/main/java/com/feryaeljustice/supersnakegame/data/audio/SoundEffectManager.kt) implementa `DefaultLifecycleObserver`:

- **`onPause(owner)`**: Llama a `soundPool.autoPause()`, pausando todos los canales de audio activos cuando la app pasa a segundo plano o se abre otra app.
- **`onResume(owner)`**: Llama a `soundPool.autoResume()`, reanudando los canales en caso necesario.
- **`onDestroy(owner)`**: Invoca `soundPool.release()`, liberando la memoria nativa y los descriptores de audio del sistema.

### B. Vinculación en Compose en `SnakeGameScreen`
En [SnakeGameScreen.kt](file:///c:/Users/nano9/AndroidStudioProjects/SuperSnakeGame/app/src/main/java/com/feryaeljustice/supersnakegame/ui/screens/game/SnakeGameScreen.kt):

```kotlin
val context = LocalContext.current
val lifecycleOwner = LocalLifecycleOwner.current

val soundEffectManager = remember(context) { SoundEffectManager(context) }
DisposableEffect(lifecycleOwner, soundEffectManager) {
    lifecycleOwner.lifecycle.addObserver(soundEffectManager)
    onDispose {
        lifecycleOwner.lifecycle.removeObserver(soundEffectManager)
        soundEffectManager.release()
    }
}
```

---

## 4. Flujo de Ejecución en el Game Loop

El sonido se reproduce de forma reactiva y exclusiva dentro del bucle de avance del juego:

1. El bucle en corutina `LaunchedEffect(gameRunning, isPaused, moveDelay)` despierta cada `moveDelay` milisegundos.
2. Invoca a `viewModel.moveSnakeTo()`.
3. Si la serpiente acaba de comer una manzana (`ate == true`):
   - Si `settings.hapticsEnabled == true`: Se ejecuta la respuesta háptica táctil.
   - Si `settings.soundEffectsEnabled == true`: Se dispara `soundEffectManager.playEatSound(settings.soundEffectsVolume)`.
4. El método `playEatSound` valida que el volumen sea superior a 0, que el recurso esté completamente precargado (`isLoaded == true`) y reproduce el stream con el volumen configurado por el usuario.

---

## 5. Persistencia y Control de Volumen

El volumen y el estado de activación de los efectos de sonido son configurables por el usuario y se almacenan de forma reactiva:

### Dominio
- En [GameSettings.kt](file:///c:/Users/nano9/AndroidStudioProjects/SuperSnakeGame/app/src/main/java/com/feryaeljustice/supersnakegame/domain/GameSettings.kt):
  - `soundEffectsVolume: Float = 0.8f` (rango normalizado `0.0f` a `1.0f`).
  - `soundEffectsEnabled: Boolean = true`.
- En [SettingsRepository.kt](file:///c:/Users/nano9/AndroidStudioProjects/SuperSnakeGame/app/src/main/java/com/feryaeljustice/supersnakegame/domain/repository/SettingsRepository.kt):
  - `fun setSoundEffectsVolume(volume: Float)`
  - `fun setSoundEffectsEnabled(enabled: Boolean)`

### Capa de Datos
- En [SettingsRepositoryImpl.kt](file:///c:/Users/nano9/AndroidStudioProjects/SuperSnakeGame/app/src/main/java/com/feryaeljustice/supersnakegame/data/repository/SettingsRepositoryImpl.kt):
  - Guarda los valores en `SharedPreferences` con las claves `"sound_effects_volume"` y `"sound_effects_enabled"`.
  - Asegura que el valor de volumen se mantenga siempre acotado (`volume.coerceIn(0.0f, 1.0f)`).
  - Emite inmediatamente el estado actualizado a través de `settingsFlow: StateFlow<GameSettings>`.

### Interfaz de Usuario (UI)
- En [GameSettingsSheet.kt](file:///c:/Users/nano9/AndroidStudioProjects/SuperSnakeGame/app/src/main/java/com/feryaeljustice/supersnakegame/ui/components/GameSettingsSheet.kt):
  - **Switch de activación**: Permite silenciar todos los efectos con un toque. Muestra iconos adaptados (`Icons.AutoMirrored.Filled.VolumeUp` y `VolumeOff`).
  - **Slider Material 3**: Permite deslizar el volumen de 0% a 100% con un indicador en tiempo real.

---

## 6. Permisos y Google Play Store

- **Permisos en Manifest**: Cero. La reproducción de sonidos locales con `SoundPool` a través del canal multimedia no requiere `RECORD_AUDIO` ni `MODIFY_AUDIO_SETTINGS`.
- **Ficha de Google Play / Data Safety**: No se recopilan ni transmiten datos de audio. No requiere cambios en las declaraciones de privacidad de la consola.
