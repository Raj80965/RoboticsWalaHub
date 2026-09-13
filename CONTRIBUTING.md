# Contributing to Robotics Wala Hub

First off, **thank you** for considering contributing to **RW HUB**! 🤖 Every contribution — from fixing a typo to adding a major feature — helps make this project better for the entire robotics community.

---

## 📋 Table of Contents

- [Code of Conduct](#-code-of-conduct)
- [How Can I Contribute?](#-how-can-i-contribute)
- [Getting Started](#-getting-started)
- [Development Workflow](#-development-workflow)
- [Commit Convention](#-commit-convention)
- [Pull Request Process](#-pull-request-process)
- [Code Style Guide](#-code-style-guide)
- [Reporting Bugs](#-reporting-bugs)
- [Suggesting Features](#-suggesting-features)

---

## 📜 Code of Conduct

This project adheres to the [Contributor Covenant Code of Conduct](CODE_OF_CONDUCT.md). By participating, you are expected to uphold this code. Please report unacceptable behavior via [GitHub Issues](https://github.com/Raj80965/RoboticsWalaHub/issues).

---

## 🤝 How Can I Contribute?

| Contribution Type | Description |
|---|---|
| 🐛 **Bug Reports** | Found something broken? [Open a bug report](https://github.com/Raj80965/RoboticsWalaHub/issues/new?template=bug_report.yml) |
| 💡 **Feature Requests** | Have an idea? [Suggest a feature](https://github.com/Raj80965/RoboticsWalaHub/issues/new?template=feature_request.yml) |
| 📝 **Documentation** | Fix typos, improve guides, add examples |
| 🧪 **Testing** | Write unit tests, UI tests, or test on different devices |
| 🎨 **Design** | Improve UI/UX, create icons, or enhance the design system |
| 🔧 **Code** | Fix bugs, implement features, optimize performance |

---

## 🚀 Getting Started

### Prerequisites

- **Android Studio** Ladybug (2024.2) or newer
- **JDK 17** or **JDK 21**
- **Android SDK** — API Level 24 (minimum), API Level 35 (target)
- **Git** — [Download Git](https://git-scm.com/downloads)

### Fork & Clone

```bash
# 1. Fork the repository on GitHub (click the "Fork" button)

# 2. Clone your fork
git clone https://github.com/<YOUR_USERNAME>/RoboticsWalaHub.git
cd RoboticsWalaHub

# 3. Add the upstream remote
git remote add upstream https://github.com/Raj80965/RoboticsWalaHub.git

# 4. Open in Android Studio and let Gradle sync complete
```

### Firebase Setup

1. Create a project in [Firebase Console](https://console.firebase.google.com/).
2. Enable **Authentication** (Email/Password), **Cloud Firestore**, and **Cloud Storage**.
3. Download `google-services.json` and place it in the `app/` directory.
4. Deploy `firestore.rules` and `storage.rules`.

---

## 🔄 Development Workflow

```
main (stable)
  └── feature/your-feature-name
       └── commit → commit → commit
            └── Pull Request → Code Review → Merge ✅
```

1. **Sync with upstream** before starting work:
   ```bash
   git checkout main
   git pull upstream main
   ```

2. **Create a feature branch**:
   ```bash
   git checkout -b feature/add-iot-dashboard
   ```

3. **Make your changes**, committing often with meaningful messages.

4. **Push to your fork**:
   ```bash
   git push origin feature/add-iot-dashboard
   ```

5. **Open a Pull Request** against `Raj80965/RoboticsWalaHub:main`.

---

## 📝 Commit Convention

We follow the [Conventional Commits](https://www.conventionalcommits.org/) specification:

```
<type>(<scope>): <short description>

[optional body]
[optional footer]
```

### Types

| Type | Description |
|---|---|
| `feat` | A new feature |
| `fix` | A bug fix |
| `docs` | Documentation only changes |
| `style` | Code style (formatting, semicolons, etc.) |
| `refactor` | Code refactoring (no feature or fix) |
| `perf` | Performance improvements |
| `test` | Adding or updating tests |
| `build` | Build system or dependency changes |
| `ci` | CI/CD configuration changes |
| `chore` | Other changes that don't modify src or test files |

### Examples

```
feat(attendance): add NFC-based check-in support
fix(booking): resolve overlapping slot conflict detection
docs(readme): update installation instructions
refactor(auth): simplify Firebase session management
```

---

## 🔀 Pull Request Process

1. **Fill out the PR template** completely.
2. **Link related issues** using keywords (`Closes #42`, `Fixes #17`).
3. **Ensure CI passes** — the GitHub Actions build must succeed.
4. **Add screenshots** for any UI changes.
5. **Keep PRs focused** — one feature or fix per PR.
6. **Request a review** from the maintainers.

### PR Checklist

- [ ] Code compiles without errors
- [ ] New code follows the project's code style
- [ ] Self-reviewed my own code
- [ ] Added comments for complex logic
- [ ] Updated documentation if needed
- [ ] No new warnings introduced

---

## 🎨 Code Style Guide

### Kotlin

- Follow the [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use `camelCase` for variables and functions
- Use `PascalCase` for classes and composables
- Prefer `val` over `var` when possible
- Use meaningful, descriptive names
- Maximum line length: **120 characters**

### Jetpack Compose

- Composable functions use `PascalCase` (e.g., `StudentDashboard`)
- State hoisting: lift state up to the caller when appropriate
- Use `remember` and `derivedStateOf` for computed values
- Follow unidirectional data flow (UDF) pattern

### Project Architecture (MVVM)

```
ui/screens/       → Composable UI screens
ui/viewmodels/    → ViewModels with StateFlow
data/models/      → Data classes and enums
data/repository/  → Firebase repository implementations
```

---

## 🐛 Reporting Bugs

Use the [Bug Report template](https://github.com/Raj80965/RoboticsWalaHub/issues/new?template=bug_report.yml) and include:

- **Device info** (model, Android version)
- **Steps to reproduce** the bug
- **Expected behavior** vs. **actual behavior**
- **Screenshots or screen recordings** if applicable
- **Logcat output** for crashes

---

## 💡 Suggesting Features

Use the [Feature Request template](https://github.com/Raj80965/RoboticsWalaHub/issues/new?template=feature_request.yml) and describe:

- **The problem** your feature solves
- **Your proposed solution**
- **Alternative approaches** you've considered
- **Additional context** (mockups, references, etc.)

---

## 🙏 Thank You!

Every contribution, no matter how small, makes **Robotics Wala Hub** better. We appreciate your time and effort! 🤖❤️

---

<p align="center">
  <sub>Built with ❤️ by the Robotics Community</sub>
</p>
