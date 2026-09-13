# Security Policy

## Supported Versions

| Version | Supported |
|---------|-----------|
| 2.0.x   | ✅ Active  |
| 1.0.x   | ⚠️ Critical fixes only |
| < 1.0   | ❌ Not supported |

## Reporting a Vulnerability

We take the security of **Robotics Wala Hub** seriously. If you discover a security vulnerability, please report it responsibly.

### 🔒 How to Report

1. **DO NOT** open a public GitHub issue for security vulnerabilities.
2. Send details to the maintainers via a **private** channel:
   - Open a [GitHub Security Advisory](https://github.com/Raj80965/RoboticsWalaHub/security/advisories/new) (recommended)
   - Or contact the maintainer directly through their GitHub profile

### 📝 What to Include

Please include as much of the following information as possible:

- **Type of vulnerability** (e.g., SQL injection, XSS, authentication bypass, data exposure)
- **Affected component** (e.g., Firebase rules, authentication flow, storage rules, API endpoint)
- **Steps to reproduce** the vulnerability
- **Proof of concept** (code, screenshots, or video)
- **Potential impact** and severity assessment
- **Suggested fix** (if you have one)

### ⏱️ Response Timeline

| Action | Timeline |
|--------|----------|
| Acknowledgment of report | Within **48 hours** |
| Initial assessment | Within **5 business days** |
| Fix development & testing | Within **30 days** (for critical issues) |
| Public disclosure | After fix is deployed |

### 🛡️ Security Best Practices for Contributors

When contributing to this project, please ensure:

- **Never commit** private keys, API keys, or `google-services.json` with production credentials
- **Never commit** keystore files (`*.jks`, `*.keystore`) or signing configurations
- **Never commit** `local.properties` or any file containing secrets
- **Always validate** user input on both client and server (Firestore Security Rules)
- **Follow** the principle of least privilege in Firebase Security Rules
- **Use** Firebase Authentication for all authenticated operations
- **Test** Firestore and Storage security rules before deployment

### 🏆 Recognition

We appreciate security researchers who help keep our project safe. With your permission, we will:

- Credit you in our release notes
- Add you to our security acknowledgments

---

Thank you for helping keep **Robotics Wala Hub** and its community safe! 🤖🔐
