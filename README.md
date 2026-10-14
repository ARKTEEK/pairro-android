# Pairro for Android

Pairro for Android is the Android client application for controlling a PC running [Pairro for Desktop](https://github.com/arkteek/pairro-desktop). It allows users to control mouse movements, keyboard inputs, clipboard content, and system volume over secure encrypted network connections.

## Features

* **Mouse Control**: Real-time cursor movement, left/right clicks, dragging and selection.
* **Keyboard Input**:
  * Send keystrokes directly to the host machine.
  * Create custom key macros (e.g Win+E, Win+R).
* **Clipboard Synchronization**: Share and sync clipboard text between mobile and desktop devices.
* **Volume Management**: Adjust host device system volume remotely.

## Security

* **Encrypted Communication**:
    * TLS: Secure TCP-based control commands and metadata.
    * dTLS: Encrypts low-latency UDP packet streams.
* **Strict Access Control**: All unverified attempts from unpaired devices get instantly rejected.
* **Pairing Process**: Pairing works using ACK keys and a shared token to validate authorization before opening network sockets.

## Prerequisites

* **Android Device**: Android 8.0 (API level 26) or higher.
* **Pairro for Desktop**: The desktop host software running on the desired Desktop device. Ensure it is running on the same local network.

## Getting Started

### Installation

1. Download the latest `.apk` from the [Releases](https://github.com/arkteek/pairro-android/releases) section.
2. Install the APK on your Android device (ensure installation from unknown sources is allowed).

### Pairing & Usage
1. Launch Pairro on your Android device.
2. Launch Pairro on your Desktop device.
3. Turn on Discoverability on both devices.
4. Select Desktop device you want to pair with on your Android device.
5. Verify codes match on Android and Desktop devices and accept Android device's pairing request.
6. After that you will be able to connect to paired Desktop device and control desired peritherals using your Android device.
