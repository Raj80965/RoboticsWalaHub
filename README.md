# 🤖 Robotics Wala Hub

A complete, enterprise-grade, cyber-robotics Android application and management portal designed for robotics laboratories, research clubs, engineering students, faculty mentors, and lab administrators. Built using **Kotlin 2.0+**, **Jetpack Compose**, **Material Design 3 (M3)**, **MVVM Architecture**, and **Google Firebase Suite (Auth, Firestore, Storage)**.

### 🚀 Quick Links & Downloads

[![Download Android App](https://img.shields.io/badge/📲_Download_Android_App-(Direct_APK)-00E676?style=for-the-badge&logo=android&logoColor=black)](https://raj80965.github.io/RoboticsWalaHub/RoboticsWalaHub.apk)
[![Live Web Portal](https://img.shields.io/badge/🌐_Open_Live_Web_Portal-Simulator-00B0FF?style=for-the-badge&logo=google-chrome&logoColor=white)](https://raj80965.github.io/RoboticsWalaHub/)

* 📱 **Direct 1-Tap APK Download:** [`https://raj80965.github.io/RoboticsWalaHub/RoboticsWalaHub.apk`](https://raj80965.github.io/RoboticsWalaHub/RoboticsWalaHub.apk)
* 🌐 **Live Interactive App Simulator:** [`https://raj80965.github.io/RoboticsWalaHub/`](https://raj80965.github.io/RoboticsWalaHub/)

---

## 🌟 Key Highlights & Full System Architecture (Day 1 - Day 15)

Robotics Wala Hub is an all-in-one ecosystem for end-to-end robotics lab workflows, resource scheduling, project tracking, hardware inventory, and student analytics:

1. **🔐 Authentication & Role-Based Access Control**:
   - Dynamic Student Registration with institutional profile verification.
   - Admin Approval pipeline (Pending, Approved, Rejected, Suspended status checks).
   - Firebase Auth session persistence, edge-to-edge login flow, and password reset.

2. **📱 Multi-Role Dashboards**:
   - **Student Dashboard**: Live attendance status, upcoming lab bookings, ongoing robotics projects, pending weekly tasks, published notices, and performance rankings.
   - **Admin Command Center**: Real-time metrics overview, pending student approvals, equipment inventory tracking, lab budget approvals, event attendance, and report generators.

3. **📷 Digital QR Attendance & Check-In**:
   - Hardware-accelerated camera QR scanner powered by Google ML Kit Barcode API.
   - Dynamic QR code generator using ZXing for faculty/admins.
   - Duplicate check-in prevention, automated lab hours calculation, and comprehensive attendance history.

4. **🗓️ Lab Slot & Workstation Booking System**:
   - Interactive scheduling with date/time pickers.
   - Conflict-detection engine preventing overlapping bookings for workstations.
   - Real-time Admin approval/rejection pipeline and booking cancellation.

5. **🚀 Robotics Project Management**:
   - Project creation, milestone tracking, progress percentage validation.
   - Multi-student team collaboration with duplicate member prevention.
   - Real-time project update logs and Firebase Storage integration for schematics and CAD files.

6. **📝 Daily Work Progress & Weekly Task Boards**:
   - Daily laboratory logs with verified hour tracking.
   - Admin-assigned weekly milestones with status workflows (Pending, In Progress, Submitted, Completed, Changes Requested).
   - Document and code submission attachments.

7. **🏆 Digital Achievements & Certificates**:
   - Student achievement portfolio with digital certificate uploads.
   - Verification workflow with admin reviews and score adjustments.
   - Automated gamification badges and point allocation.

8. **📢 Notice Board & Event Management**:
   - Campus notices with priority tags (Urgent, General, Workshop).
   - Event creation, seat capacity enforcement, RSVP registration, and attendance tracking.

9. **⚙️ Hardware Equipment & Inventory Management**:
   - Microcontrollers, sensors, actuators, and 3D printing filament stock management.
   - Atomic checkout/return flow with low-stock warnings and overdue tracking.
   - Complete inventory transaction audit trails.

10. **💰 Project Budget & Expense Management**:
    - Project budget allocation and real-time expense claim submissions.
    - Receipt image uploads to Firebase Storage with review workflows.
    - Financial audit logs and remaining budget calculations.

11. **📊 Advanced Reports, Analytics & Leaderboards**:
    - Comprehensive lab metrics (active students, equipment utilization, project completions).
    - Gamified student leaderboards with weekly, monthly, and all-time rankings.
    - Exportable CSV/PDF summary generation for institutional auditing.

---

## 🛠️ Technology Stack

| Category | Technology |
|---|---|
| **Language** | Kotlin 2.0+ |
| **UI Toolkit** | Jetpack Compose (Compose BOM), Material Design 3 (M3) |
| **Architecture** | MVVM (Model-View-ViewModel) + Repository Pattern |
| **Reactive State** | Kotlin Coroutines & `StateFlow` / `SharedFlow` |
| **Backend & Database** | Firebase Cloud Firestore |
| **Authentication** | Firebase Authentication (Email/Password) |
| **Cloud Storage** | Firebase Cloud Storage |
| **Camera & Barcode** | CameraX & Google ML Kit Barcode Scanning |
| **QR Generation** | ZXing Core |
| **Image Loading** | Coil Compose |
| **Navigation** | Jetpack Navigation Compose (`NavHost`) |

---

## 🎨 Design System & Theme

- **Palette**: Obsidian Cyber-Robotics
  - **Primary**: Electric Blue (`#0066FF`), Cyber Neon Cyan (`#00D4FF`), Deep Cobalt (`#0038A8`)
  - **Dark Theme**: Obsidian Black (`#0A0D14`), Matte Surface (`#121824`), Elevated Surface (`#1A2232`)
  - **Light Theme**: High-Tech Ice (`#F5F8FC`), Pure White (`#FFFFFF`), Slate Borders (`#D7E2EE`)
  - **Accents**: Neon Cyber Green (`#00E676`), Amber Alert (`#FFB300`), Cyber Red (`#FF3B30`)
- **Graphics**: Hardware-accelerated Canvas cyber coordinate grid, glowing visors, and circuit nodes.

---

## 🔒 Security & Firebase Rules

### Firestore Security (`firestore.rules`)
- **Role-Based Access**: Role validation stored securely in server-side `/users/{uid}` documents.
- **Student Privacy**: Students can only read/write their own attendance, work progress, and equipment requests.
- **Admin Isolation**: Only administrators can approve users, modify budgets, audit logs, and approve achievements.
- **Field-Level Protection**: Users cannot modify their own `role` or `status` fields.

### Storage Security (`storage.rules`)
- **Path Isolation**: Files scoped to `/profile_images/{userId}`, `/certificates/{userId}`, `/receipts/{userId}`, and `/project_files/{projectId}`.
- **Size Limits**: Strict validation (5MB for avatars, 10MB for receipts/certificates, 25MB for project archives).
- **MIME Validation**: Restricted to valid image formats and PDF documents.

> [!CAUTION]
> **Security Notice**: Do NOT commit private signing keys (`*.jks`, `*.keystore`), `local.properties`, or sensitive production API keys to public repositories.

---

## 🚀 Setup & Build Instructions

### Prerequisites
- **Android Studio** (Ladybug / Hedgehog / Koala or newer)
- **JDK 17 or JDK 21**
- **Android SDK** (API Level 24 minimum, API 35 target)

### Step-by-Step Setup
1. **Clone the Repository**:
   ```bash
   git clone https://github.com/Raj80965/RoboticsWalaHub.git
   cd RoboticsWalaHub
   ```

2. **Configure Firebase**:
   - Create a project in [Firebase Console](https://console.firebase.google.com/).
   - Enable **Firebase Authentication** (Email/Password provider).
   - Enable **Cloud Firestore** and deploy `firestore.rules`.
   - Enable **Cloud Storage** and deploy `storage.rules`.
   - Download your `google-services.json` and place it in the `app/` folder.

3. **Build the Debug APK**:
   ```bash
   ./gradlew assembleDebug
   ```

4. **Build the Production Release APK**:
   ```bash
   ./gradlew assembleRelease
   ```
   *Generated output:* `app/build/outputs/apk/release/app-release-unsigned.apk`

---

## 📱 Interactive Web Simulator

An interactive, zero-installation web simulator is bundled with the project for desktop testing and UI/UX presentation:
- Open [`preview.html`](preview.html) in any modern browser (Google Chrome recommended) to test all user journeys, roles, and workflows in real time.

---

## 🔮 Future Scope

- IoT-enabled real-time hardware telemetry integration (MQTT / WebSockets).
- Automated AI code review and component recommendation engine for robotics projects.
- Push notifications via Firebase Cloud Messaging (FCM) for real-time slot and approval alerts.

---

**Developed with ❤️ for the Robotics Community**
