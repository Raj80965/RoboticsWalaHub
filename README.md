# 🤖 Robotics Wala Hub

A modern, high-tech Android application for robotics enthusiasts, engineers, and makers built using **Kotlin**, **Jetpack Compose**, **Material Design 3**, and **MVVM Architecture**.

---

## 🚀 Overview (Day 1 Foundation)

The Day 1 release of **Robotics Wala Hub** delivers the complete application foundation, cyber-robotics design system, reactive MVVM architecture, and core authentication navigation flows:

1. **Splash Screen**:
   - Custom animated **Robotics Logo** emblem with dynamic glowing visor, pulse aura, and cyber circuit nodes.
   - High-tech typography with the title **"Robotics Wala Hub"** and tagline.
   - Smooth animated linear system initialization loader.
   - Timed automatic transition (with tap-to-skip support) to the Login screen.

2. **Login Screen**:
   - Clean Material 3 card container with cyber glow borders.
   - **Email & Password inputs** with live validation and error handling.
   - Password visibility toggle (show/hide).
   - Interactive **"Forgot Password?"** modal dialog for simulated password recovery.
   - High-tech **Primary Gradient Sign In** button with simulated loading state.
   - **"Create Account"** action button navigating to the Registration screen.

3. **Registration Screen**:
   - Full registration form: **Full Name**, **Email**, **Password**, and **Confirm Password**.
   - Real-time password confirmation check and match validation.
   - Interactive **Terms of Service & Privacy Policy** checkbox.
   - **"Create Account"** action button.
   - Back navigation and **"Already have an account? Log In"** link.

4. **Navigation Flow**:
   - `Splash` ➔ `Login` (replaces splash on backstack).
   - `Login` ⇄ `Registration` (smooth bidirectional slide & fade transitions).

---

## 🎨 Design System & Theme

- **Palette**: Professional Blue, Black, and White Cyber-Robotics Theme
  - **Primary**: Electric Blue (`#0066FF`), Cyber Neon Cyan (`#00D4FF`), Deep Cobalt (`#0038A8`)
  - **Dark Mode**: Obsidian Black (`#0A0D14`), Dark Matte Surface (`#121824`), Elevated Surface (`#1A2232`)
  - **Light Mode**: High-Tech Clean Ice (`#F5F8FC`), Pure White Surface (`#FFFFFF`), Slate Borders (`#D7E2EE`)
  - **Accents**: Neon Cyber Green (`#00E676`), Warning Amber (`#FFB300`), Error Red (`#FF3B30`)
- **Theme Support**: Seamless **Light Mode** and **Dark Mode** with adaptive contrast and system bar styling.
- **Ambient Graphics**: Custom Canvas-rendered cyber coordinate grid and radial glow mesh.

---

## 🏗️ Architecture & Project Structure

```
robotics_hub/
├── build.gradle.kts                          # Root Gradle build script
├── settings.gradle.kts                       # Project settings & repositories
├── gradle.properties                         # JVM & AndroidX configurations
├── gradle/
│   ├── libs.versions.toml                   # Version catalog (Compose BOM, M3, Navigation)
│   └── wrapper/
│       └── gradle-wrapper.properties         # Gradle 8.7 wrapper
├── app/
│   ├── build.gradle.kts                      # App module configuration
│   ├── proguard-rules.pro                   # ProGuard rules
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml           # Android Manifest declaration
│           ├── res/
│           │   ├── values/
│           │   │   ├── strings.xml           # String resources
│           │   │   ├── colors.xml            # Fallback colors
│           │   │   └── themes.xml            # Android theme
│           │   └── drawable/
│           │       ├── ic_launcher_background.xml
│           │       └── ic_launcher_foreground.xml
│           └── java/com/roboticswala/hub/
│               ├── MainActivity.kt           # Edge-to-edge Compose entry point
│               ├── RoboticsApp.kt            # Application root
│               ├── ui/
│               │   ├── theme/
│               │   │   ├── Color.kt          # Robotics color palette & gradients
│               │   │   ├── Type.kt           # Modern geometric typography
│               │   │   ├── Shape.kt          # Rounded tech corner shapes
│               │   │   └── Theme.kt          # M3 Dark/Light theme provider
│               │   ├── components/
│               │   │   ├── RoboticsLogo.kt   # High-tech animated robot emblem
│               │   │   ├── RoboticsTextField.kt # Custom styled input with icons & toggle
│               │   │   ├── RoboticsButton.kt # Futuristic primary/secondary buttons
│               │   │   ├── RoboticsBackground.kt # Circuit grid & tech mesh background
│               │   │   └── ForgotPasswordDialog.kt # Interactive password reset dialog
│               │   ├── navigation/
│               │   │   ├── Screen.kt         # Navigation route definitions
│               │   │   └── AppNavigation.kt  # Animated NavHost (Splash -> Login <-> Register)
│               │   └── screens/
│               │       ├── splash/
│               │       │   ├── SplashScreen.kt # Logo, title, pulse animation & timer
│               │       │   └── SplashViewModel.kt
│               │       ├── auth/
│               │       │   ├── login/
│               │       │   │   ├── LoginScreen.kt # Email, password, login, create account
│               │       │   │   ├── LoginViewModel.kt # MVVM StateFlow logic
│               │       │   │   └── LoginUiState.kt
│               │       │   └── register/
│               │       │       ├── RegistrationScreen.kt # Full Name, email, pass, confirm, terms
│               │       │       ├── RegisterViewModel.kt # MVVM validation & submit logic
│               │       │       └── RegisterUiState.kt
```

---

## 🛠️ Tech Stack

- **Language**: Kotlin 2.0+
- **UI Toolkit**: Jetpack Compose with Compose BOM
- **Design System**: Material Design 3 (M3)
- **Architecture**: MVVM with `StateFlow` and `viewModelScope`
- **Navigation**: Jetpack Navigation Compose (`NavHost`)
- **Graphics**: Hardware-accelerated Compose Canvas & Vector graphics

---

## 📱 How to Open & Run

1. Open **Android Studio** (Koala / Ladybug or newer recommended).
2. Select **File ➔ Open...** and choose the `robotics_hub` folder (`c:\Users\hp\Desktop\robotics_hub`).
3. Allow Gradle to sync the project dependencies.
4. Select an Android Emulator (API 24+) or physical device.
5. Click **Run (`Shift + F10`)** to launch **Robotics Wala Hub**!
