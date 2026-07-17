# Implementation Plan - Fix Compilation Errors and Warnings

Fix the compilation errors in `SplashScreen.kt` and address several warnings across the project to improve code health.

## Proposed Changes

### UI Components

#### [MODIFY] [SplashScreen.kt](file:///D:/70%20days%20hard%20challenge%20app/app/src/main/java/com/rain/dailygrind/ui/SplashScreen.kt)
- Add missing imports: `androidx.compose.ui.layout.layout`, `androidx.compose.ui.layout.Measurable`, and `androidx.compose.ui.layout.Constraints`.
- Correct the `offsetY` modifier implementation to use the `layout` extension function properly.
- Remove the unused `textMain` variable.

#### [MODIFY] [ExportCard.kt](file:///D:/70%20days%20hard%20challenge%20app/app/src/main/java/com/rain/dailygrind/ui/ExportCard.kt)
- Remove the unused `colors: GrindColors` parameter from the `ExportCard` composable.
- Update deprecated icons: `Icons.Rounded.MenuBook` -> `Icons.AutoMirrored.Rounded.MenuBook` and `Icons.Rounded.DirectionsWalk` -> `Icons.AutoMirrored.Rounded.DirectionsWalk`.

#### [MODIFY] [HomeScreen.kt](file:///D:/70%20days%20hard%20challenge%20app/app/src/main/java/com/rain/dailygrind/ui/HomeScreen.kt)
- Update `delay(index * 60L)` to use `Duration` if applicable (e.g., `(index * 60).milliseconds`).

#### [MODIFY] [MainActivity.kt](file:///D:/70%20days%20hard%20challenge%20app/app/src/main/java/com/rain/dailygrind/MainActivity.kt)
- Update `delay(2500)` to `2500.milliseconds`.
- Use named parameter for `SharingStarted.WhileSubscribed(5_000, replayExpirationMillis = 0)` to clarify the boolean/long argument.

### Logic & Data

#### [MODIFY] [GrindViewModel.kt](file:///D:/70%20days%20hard%20challenge%20app/app/src/main/java/com/rain/dailygrind/GrindViewModel.kt)
- Update `debounce(400)` to `400.milliseconds`.
- Use named parameters for `SharingStarted.WhileSubscribed`.

## Verification Plan

### Automated Tests
- Run `./gradlew app:assembleDebug` to verify that the project compiles without errors and with reduced warnings.

### Manual Verification
- None required for compilation fixes, but will verify the app still builds and runs correctly.
