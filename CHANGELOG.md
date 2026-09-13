# Changelog

All notable changes to **Robotics Wala Hub** will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [2.1.0] — 2026-09-13

### Added
- 🏆 **Continuous Sliding Achievement Image Bar** on Student Dashboard with auto-rotation every 3.5 seconds
- 📸 **Admin Achievement Photo Upload System** with base64 file upload and live preview
- 🎨 Achievement Detail Modal with full-resolution photo viewer, award badges, and verification seal
- 🛡️ Admin Achievement Banners Management Screen with edit, delete, and live dashboard preview
- 💾 `localStorage` persistence for achievement banners (survives browser refresh)
- 📱 Android Jetpack Compose `AchievementBannerCarousel` composable in `StudentHomeScreen.kt`
- 🔔 Admin sliding showcase banner notice in `AdminAchievementsScreen.kt`

### Changed
- Enhanced Admin Command Center with "Achievement Banners Manager" module card
- Updated both `index.html` and `preview.html` web simulators with carousel system

---

## [2.0.0] — 2026-09-01

### Added
- 🔐 **Firebase Authentication** with Email/Password sign-in and session persistence
- 👥 **Role-Based Access Control** — Student and Admin dashboards with role isolation
- 📷 **QR Code Attendance System** — CameraX + ML Kit barcode scanner for students, ZXing QR generator for admins
- 🗓️ **Lab Slot Booking System** — Date/time/workstation picker with conflict detection
- 🚀 **Robotics Project Management** — Create projects, track milestones, manage team members
- 📝 **Daily Work Progress & Weekly Tasks** — Lab logs with verified hours and task status workflows
- 🏆 **Digital Achievements & Certificates** — Portfolio with admin verification and gamification badges
- 📢 **Notice Board & Event Management** — Priority-tagged notices and event RSVP system
- ⚙️ **Hardware Inventory Management** — Checkout/return flow with low-stock warnings
- 💰 **Project Budget & Expenses** — Receipt uploads with admin approval workflow
- 📊 **Analytics & Leaderboards** — Lab metrics, student rankings, exportable reports
- 🌐 **Interactive Web Simulator** — Full-featured browser-based app simulator (`index.html`)
- ⚡ **GitHub Actions CI/CD** — Automated APK build and deployment pipeline
- 🎨 **Obsidian Cyber-Robotics Design System** — Material Design 3 with custom dark/light themes

### Security
- Comprehensive Firestore Security Rules with role-based field-level access control
- Firebase Storage Rules with path isolation, size limits, and MIME validation

---

## [1.0.0] — 2026-08-15

### Added
- 🏗️ Initial project scaffolding with Kotlin 2.0+ and Jetpack Compose
- 📱 Basic Android app structure with MVVM architecture
- 🔥 Firebase project integration (Auth, Firestore, Storage)
- 🎨 Material Design 3 theme setup

---

[2.1.0]: https://github.com/Raj80965/RoboticsWalaHub/compare/v2.0.0...HEAD
[2.0.0]: https://github.com/Raj80965/RoboticsWalaHub/compare/v1.0.0...v2.0.0
[1.0.0]: https://github.com/Raj80965/RoboticsWalaHub/releases/tag/v1.0.0
