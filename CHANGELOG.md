# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [Unreleased]
### Planned
- Optional “Compact Mode” for the config screen.
- Localization support (Polish / English auto-detect).
- Minor visual polish pass for Mod Menu description.
---

## [1.2.2] - 2026-09-24

### Updated

* Added full compatibility with Minecraft **26.3**.
* A single JAR now supports Minecraft **26.1.2, 26.2 and 26.3**.

### Fixed

* Fixed global configuration buttons not visually refreshing their state on Minecraft 26.1.2.
* Fixed **Restore Vanilla** not immediately refreshing the configuration screen on Minecraft 26.1.2.
* Fixed **Save & Quit** behavior across all supported Minecraft versions.

### Compatibility

* Minecraft **26.1.2 – 26.3**
* Fabric Loader **0.19.3+**
* Client-side only.
---

## [1.2.1] - 2026-07-03

### Updated

* Updated compatibility for Minecraft **26.2**.
---
## [1.2.0] - 2026-05-08
### Updated
- Full migration to Minecraft 26.1.2 rendering and GUI systems.
- Migrated the entire project from Yarn mappings to official Mojang mappings.
- Rebuilt the entire configuration screen using the new `ContainerObjectSelectionList` architecture.
- Modernized layout and scrolling behavior for better compatibility with future Minecraft UI changes.
- Improved Mod Menu integration and overall GUI responsiveness.

---

## [1.1.2] - 2026-01-08
### Updated
- Updated compatibility for Minecraft 1.21.11
- No gameplay or behavior changes
- Technical updates and Fabric environment maintenance

## [1.1.0] - 2025-10-11
### Updated
- Full compatibility update for **Minecraft 1.21.9 / 1.21.10**.
- Updated to **Fabric Loader**, **Fabric API**, and **Mod Menu 11.0.1**.
- Migrated UI input handling (`Click` events, scrollbars) to new Fabric mappings.
- Adjusted Gradle build to automatically inject version number into `fabric.mod.json`.

### Fixed
- Version placeholder in Mod Menu is now correctly replaced with the current mod version.
- Resolved client crash caused by private `World.isClient` access in new mappings.

### Notes
- Still client-side only.
- Confirmed working with Mod Menu 11.0.1 and Fabric API for MC 1.21.9+.

### Compatibility
- Fully compatible with **Minecraft 1.21.9 – 1.21.10**
- May partially work on earlier 1.21.x versions (1.21.1–1.21.8), but is not officially supported.
- Requires Fabric Loader 0.16.0 or newer.

### Tested Environment
- Minecraft **1.21.9**
- Fabric Loader **0.16.14**
- Fabric API **0.110.0+1.21.9**
- Mod Menu **11.0.1**
- Java **21 (Temurin 21.0.5 LTS)**
- IDE: **IntelliJ IDEA 2024.2 / VS Code**
---

## [1.0.0] - 2025-08-17
### Added
- Config screen with **per-block** and **per-effect** (Flame/Smoke) toggles:
  Torch, Wall Torch, Redstone Torch, Wall Redstone Torch, Soul Torch, Wall Soul Torch, Candles, Furnace, Smoker, Blast Furnace.
- Global toggles: “All Flames”, “All Smoke”, **Apply to all**, **Restore vanilla**.
- Mod Menu integration (optional).
- Resource-pack friendly UI (vanilla separators & scroller).

### Notes
- Client-side only. Works on 1.21.x.