# Project Description: EncryptionApp

A simple, privacy-focused Android app that demonstrates secure text encryption and decryption entirely on-device. It’s designed to be educational and user-friendly, showing how modern cryptography works while providing a polished UI and practical utilities like a strong password generator and an in-app guide.

## Features

- **AES-256 encryption with password**
- **PBKDF2 key derivation** with random salt and 10,000 iterations
- **Random IV per encryption** for strong security
- **Base64 outputs** that bundle salt + IV + ciphertext
- **Encrypt/Decrypt modes** with clear UI and helpful toasts
- **Guide bottom sheet** explaining encryption/decryption with sample actions
- **Generate Password** button to create strong random passwords
- **Copy to Clipboard** for results
- **Firebase Analytics (optional)**:
  - Logs `app_open` at startup
  - Logs `tutorial_begin` when the Guide is opened

## How It Works (Crypto)

- Derives a 256-bit AES key from the user’s password using PBKDF2WithHmacSHA1 + random 16-byte salt.
- Encrypts with AES/CBC/PKCS5Padding using a fresh IV per encryption.
- Output is Base64-encoded bytes of: `salt || iv || ciphertext` (no line wraps).
- Decryption reverses the process with the same password.
- Important: A legacy AES/ECB helper exists but is not used (insecure mode).

## How To Use

- **Encrypt**
  - Select “Encrypt Text”.
  - Enter your message and a strong password (or tap “Generate Password”).
  - Tap “Encrypt” and copy the encrypted text.

- **Decrypt**
  - Select “Decrypt Text”.
  - Paste the full encrypted text and enter the same password.
  - Tap “Decrypt” to reveal the original message.

- **Learn**
  - Tap “Guide” for an in-app tutorial and sample encrypt/decrypt actions.

## UI/UX Highlights

- Clean Material layout, clear sections, and professional styling.
- Extra action row for Guide and Generate Password.
- Friendly helper messages for errors and success events.

## Tech Stack

- Language: Kotlin
- AndroidX: AppCompat, Material Components, ConstraintLayout
- Crypto: javax.crypto (Cipher, SecretKeyFactory), PBKDF2, AES/CBC
- Gradle Kotlin DSL with Version Catalog
- Compose theme files included and supported (Material3) for future UI migration
- Firebase Analytics (optional; requires `google-services.json`)

## Project Structure

- [MainActivity.kt](cci:7://file:///c:/Users/LAPTOP%204/AndroidStudioProjects/EncryptionApp/app/src/main/java/com/example/encryptionapp/MainActivity.kt:0:0-0:0) – UI logic, actions, guide, and password generator
- [EnhancedEncryptionUtils.kt](cci:7://file:///c:/Users/LAPTOP%204/AndroidStudioProjects/EncryptionApp/app/src/main/java/com/example/encryptionapp/EnhancedEncryptionUtils.kt:0:0-0:0) – AES-256/PBKDF2 encryption utilities
- [EncryptionUtils.kt](cci:7://file:///c:/Users/LAPTOP%204/AndroidStudioProjects/EncryptionApp/app/src/main/java/com/example/encryptionapp/EncryptionUtils.kt:0:0-0:0) – legacy AES/ECB (unused; for reference only)
- [res/layout/activity_main.xml](cci:7://file:///c:/Users/LAPTOP%204/AndroidStudioProjects/EncryptionApp/app/src/main/res/layout/activity_main.xml:0:0-0:0) – main UI
- [res/layout/bottomsheet_guide.xml](cci:7://file:///c:/Users/LAPTOP%204/AndroidStudioProjects/EncryptionApp/app/src/main/res/layout/bottomsheet_guide.xml:0:0-0:0) – educational guide bottom sheet
- [AndroidManifest.xml](cci:7://file:///c:/Users/LAPTOP%204/AndroidStudioProjects/EncryptionApp/app/src/main/AndroidManifest.xml:0:0-0:0) – app manifest
- Gradle files – Kotlin DSL with Compose + Firebase configuration

## Setup and Build

- Open in Android Studio (use JDK 17; module targets JVM 11).
- Sync Gradle and run on API 21+.
- Optional Firebase:
  - In Firebase Console, create a project and register Android app with package `com.example.encryptionapp`.
  - Place `google-services.json` in `app/`.
  - Sync and run to log Analytics events.

## Security Notes

- Password strength determines security—use long, complex passwords.
- Losing the password means you cannot decrypt the data.
- Do not use the legacy AES/ECB helper; ECB is insecure.

