<div align="center">

# 🤖 Robotics Wala Hub (RW HUB)

### *Enterprise-Grade Cyber-Robotics Lab Management & Research Ecosystem*

[![Android CI](https://img.shields.io/github/actions/workflow/status/Raj80965/RoboticsWalaHub/build-apk.yml?branch=main&label=CI%20Build&logo=github-actions&logoColor=white&style=for-the-badge)](https://github.com/Raj80965/RoboticsWalaHub/actions)
[![Kotlin Version](https://img.shields.io/badge/Kotlin-2.0.21-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Android API](https://img.shields.io/badge/API-24%20--%2035-34A853?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg?style=for-the-badge)](https://opensource.org/licenses/MIT)
[![Release](https://img.shields.io/badge/Release-v2.1.0-blue?style=for-the-badge&logo=rocket)](https://github.com/Raj80965/RoboticsWalaHub/releases)

<br/>

[📥 Download APK (Direct)](https://raj80965.github.io/RoboticsWalaHub/RW_HUB.apk) • [🌐 Live Web Simulator](https://raj80965.github.io/RoboticsWalaHub/) • [📖 User Guide](USER_GUIDE.md) • [🐛 Report Bug](https://github.com/Raj80965/RoboticsWalaHub/issues/new?template=bug_report.yml)

</div>

---

## 📑 Table of Contents

- [Overview](#-overview)
- [Key Features](#-key-features)
- [System Architecture](#-system-architecture)
- [Tech Stack](#-tech-stack)
- [Project Directory Structure](#-project-directory-structure)
- [Getting Started & Build Instructions](#-getting-started--build-instructions)
- [Firebase Configuration](#-firebase-configuration)
- [Web Simulator](#-web-simulator)
- [Contributing](#-contributing)
- [Security](#-security)
- [Changelog](#-changelog)
- [License & Credits](#-license--credits)

---

## 🌟 Overview

**Robotics Wala Hub (RW HUB)** is a comprehensive, production-ready Android mobile application and web-based management platform built specifically for university robotics laboratories, makerspaces, innovation centers, and hardware research clubs.

It bridges the gap between hardware researchers and lab administrators by digitizing all laboratory operations:
- **Zero-paper administration** for attendance, lab access, and equipment allocations.
- **Role-based isolation** between students/researchers and professors/lab in-charges.
- **Real-time collaboration** on multi-member robotics engineering projects.
- **Automated hardware inventory tracking** preventing stock loss and bottlenecking.

---

## 🚀 Key Features

<table>
  <tr>
    <td width="50%">
      <h3>🔐 Authentication & Role Isolation</h3>
      <ul>
        <li>Secure Firebase Authentication with email & password.</li>
        <li>Approval-gated student onboarding (Pending, Approved, Suspended).</li>
        <li>Dedicated dashboards tailored for Students vs. Admins.</li>
      </ul>
    </td>
    <td width="50%">
      <h3>📷 Digital QR Attendance Engine</h3>
      <ul>
        <li>Lightning-fast QR scanning via <b>Google ML Kit + CameraX</b>.</li>
        <li>Dynamic attendance QR generation using <b>ZXing</b> for faculty.</li>
        <li>Automated lab work hours calculation and duplicate prevention.</li>
      </ul>
    </td>
  </tr>
  <tr>
    <td width="50%">
      <h3>🗓️ Lab Slot & Bench Scheduling</h3>
      <ul>
        <li>Interactive workstation and equipment booking calendar.</li>
        <li>Automated conflict detection prevents overlapping reservations.</li>
        <li>Admin one-click approval, rescheduling, and cancellation alerts.</li>
      </ul>
    </td>
    <td width="50%">
      <h3>⚙️ Hardware & Tool Inventory</h3>
      <ul>
        <li>Catalog for microcontrollers (ESP32, STM32, Arduino), sensors, actuators.</li>
        <li>Atomic checkout & return system with real-time stock deductions.</li>
        <li>Low-stock triggers and overdue equipment tracking.</li>
      </ul>
    </td>
  </tr>
  <tr>
    <td width="50%">
      <h3>🏆 Continuous Achievement Showcase</h3>
      <ul>
        <li>Dynamic sliding image showcase on student dashboard.</li>
        <li>Admin photo upload pipeline with instant live broadcast.</li>
        <li>Digital certificate portfolio with gamification points & badges.</li>
      </ul>
    </td>
    <td width="50%">
      <h3>🚀 Projects, Tasks & Budget Tracker</h3>
      <ul>
        <li>Multi-student team milestones with progress tracking.</li>
        <li>Weekly milestone submission and faculty review workflow.</li>
        <li>Bill & expense receipt upload with automated balance auditing.</li>
      </ul>
    </td>
  </tr>
</table>

---

## 🏛️ System Architecture

RW HUB strictly adheres to the modern Android **MVVM (Model-View-ViewModel)** architectural pattern with Google's recommended **Clean Architecture** principles.

```mermaid
graph TD
    subgraph UI_Layer["🎨 UI Presentation Layer (Jetpack Compose)"]
        A[StudentHomeScreen]
        B[AdminDashboardScreen]
        C[QRAttendanceScreen]
        D[AdminAchievementsScreen]
    end

    subgraph ViewModel_Layer["🧠 State & ViewModel Layer"]
        VM1[StudentHomeViewModel]
        VM2[AdminDashboardViewModel]
        VM3[AttendanceViewModel]
        VM4[InventoryViewModel]
    end

    subgraph Domain_Layer["📦 Repository & Domain Layer"]
        R1[AuthRepository]
        R2[AttendanceRepository]
        R3[ProjectRepository]
        R4[InventoryRepository]
        R5[AchievementRepository]
    end

    subgraph Data_Layer["☁️ Backend & Storage (Google Firebase)"]
        F1[(Cloud Firestore)]
        F2[Firebase Authentication]
        F3[Firebase Cloud Storage]
        F4[ML Kit / CameraX]
    end

    UI_Layer --> ViewModel_Layer
    ViewModel_Layer --> Domain_Layer
    Domain_Layer --> Data_Layer
```

---

## 🛠️ Tech Stack

### Android Application
![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-7F52FF?style=flat-square&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-BOM%202024.10.00-4285F4?style=flat-square&logo=android&logoColor=white)
![Material 3](https://img.shields.io/badge/Material%20Design-3-0066FF?style=flat-square&logo=material-design&logoColor=white)
![Coroutines](https://img.shields.io/badge/Coroutines-StateFlow-00599C?style=flat-square)
![Navigation Compose](https://img.shields.io/badge/Navigation-Compose%202.8-blue?style=flat-square)
![Coil](https://img.shields.io/badge/Image%20Loading-Coil%20Compose-1C7C54?style=flat-square)

### Backend & Cloud Services
![Firebase Auth](https://img.shields.io/badge/Firebase-Authentication-FFCA28?style=flat-square&logo=firebase&logoColor=black)
![Cloud Firestore](https://img.shields.io/badge/Database-Cloud%20Firestore-FFCA28?style=flat-square&logo=firebase&logoColor=black)
![Cloud Storage](https://img.shields.io/badge/Storage-Firebase%20Cloud%20Storage-FFCA28?style=flat-square&logo=firebase&logoColor=black)
![ML Kit](https://img.shields.io/badge/Vision-Google%20ML%20Kit-4285F4?style=flat-square&logo=google&logoColor=white)
![CameraX](https://img.shields.io/badge/Hardware-CameraX%201.3-34A853?style=flat-square)

### Web Simulator & CI/CD
![HTML5](https://img.shields.io/badge/Frontend-HTML5%20%2F%20Modern%20CSS3-E34F26?style=flat-square&logo=html5&logoColor=white)
![JavaScript](https://img.shields.io/badge/Logic-Vanilla%20ES6+-F7DF1E?style=flat-square&logo=javascript&logoColor=black)
![GitHub Actions](https://img.shields.io/badge/CI%2FCD-GitHub%20Actions-2088FF?style=flat-square&logo=github-actions&logoColor=white)
![GitHub Pages](https://img.shields.io/badge/Hosting-GitHub%20Pages-222222?style=flat-square&logo=github&logoColor=white)

---

## 📁 Project Directory Structure

```text
RoboticsWalaHub/
├── .github/
│   ├── ISSUE_TEMPLATE/
│   │   ├── bug_report.yml            # Interactive bug report form
│   │   ├── feature_request.yml       # Interactive feature suggestion form
│   │   └── config.yml                # Template community configuration
│   ├── workflows/
│   │   └── build-apk.yml             # Automated Android build CI pipeline
│   ├── FUNDING.yml                   # Sponsorship config
│   └── PULL_REQUEST_TEMPLATE.md      # Standard PR checklist
├── app/
│   ├── src/main/
│   │   ├── java/com/roboticswala/hub/
│   │   │   ├── data/                 # Models, repositories, Firebase mappers
│   │   │   ├── ui/
│   │   │   │   ├── components/       # Custom Compose widgets & carousels
│   │   │   │   ├── screens/
│   │   │   │   │   ├── admin/        # Admin Command Center screens & tabs
│   │   │   │   │   ├── auth/         # Login, Registration, Splash
│   │   │   │   │   └── student/      # Student Dashboard, Attendance, Tasks
│   │   │   │   └── theme/            # Obsidian Cyber-Robotics M3 theme
│   │   │   └── MainActivity.kt       # Single-activity navigation root
│   │   └── res/                      # Icons, XML layouts, themes, mipmaps
│   └── build.gradle.kts              # Module-level Gradle configuration
├── firestore.rules                   # Production Firestore security rules
├── storage.rules                     # Cloud Storage security rules
├── index.html                        # Interactive Zero-Install Web Simulator
├── preview.html                      # Standalone simulator mirror
├── RW_HUB.apk                        # Compiled direct-download release binary
├── RoboticsWalaHub.apk               # Backup direct-download release binary
├── CHANGELOG.md                      # Detailed version history
├── CODE_OF_CONDUCT.md                # Contributor Covenant v2.1
├── CONTRIBUTING.md                   # Contribution workflow & guidelines
├── LICENSE                           # MIT Open-Source License
├── SECURITY.md                       # Vulnerability reporting policy
├── USER_GUIDE.md                     # Step-by-step bilingual manual
└── README.md                         # Main repository presentation
```

---

## 💻 Getting Started & Build Instructions

### Prerequisites
- **Android Studio** Ladybug (2024.2.1+) or newer
- **JDK 17** or **JDK 21** configured in JAVA_HOME
- **Android SDK Platform** 35 with Build Tools 35.0.0
- **Git**

### Clone & Open
```bash
# Clone the repository
git clone https://github.com/Raj80965/RoboticsWalaHub.git

# Navigate into project directory
cd RoboticsWalaHub
```

### Build with Gradle
```bash
# Assemble Debug APK
./gradlew assembleDebug

# Output APK path:
# app/build/outputs/apk/debug/app-debug.apk
```

---

## 🔥 Firebase Configuration

To configure your own Firebase project:
1. Create a project in the [Firebase Console](https://console.firebase.google.com/).
2. Add an **Android App** with package name `com.roboticswala.hub`.
3. Download `google-services.json` and place it inside the `app/` directory.
4. Enable **Authentication** (Email/Password).
5. Enable **Cloud Firestore** and deploy [`firestore.rules`](firestore.rules).
6. Enable **Cloud Storage** and deploy [`storage.rules`](storage.rules).

---

## 🌐 Web Simulator

Can't install the APK right now? RW HUB includes a high-fidelity, interactive **Web Simulator** that runs in any modern browser without needing Android Studio or an emulator:

- **Launch Simulator Online:** [https://raj80965.github.io/RoboticsWalaHub/](https://raj80965.github.io/RoboticsWalaHub/)
- **Run Locally:** Simply double-click [`index.html`](index.html) or run a local HTTP server:
  ```bash
  npx serve .
  ```

---

## 🤝 Contributing

Contributions are what make the open-source community an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**!

1. Check out our [Contribution Guidelines](CONTRIBUTING.md).
2. Familiarize yourself with our [Code of Conduct](CODE_OF_CONDUCT.md).
3. Fork the project and submit a Pull Request using our [PR Template](.github/PULL_REQUEST_TEMPLATE.md).

---

## 🛡️ Security

If you discover any security issues or vulnerabilities, please review our [Security Policy](SECURITY.md) before opening any public reports.

---

## 📜 Changelog

For a complete breakdown of version history and recent additions (including the v2.1.0 sliding achievement banner), see [CHANGELOG.md](CHANGELOG.md).

---

## 📄 License & Maintainer

Distributed under the **MIT License**. See [`LICENSE`](LICENSE) for more details.

**Lead Maintainer:** [@Raj80965](https://github.com/Raj80965)  
**Project:** Robotics Wala Hub (RW HUB) 🤖

<div align="center">
  <sub>Built with ❤️ for robotics clubs, student engineers, and future innovators.</sub>
</div>
