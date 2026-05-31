# ⚔️ AnotherRPG

[![Hytale Version Support](https://img.shields.io/badge/Hytale-Server%20Plugin-blueviolet?style=for-the-badge&logo=minecraft)](https://hytale.com)
[![Build Tool](https://img.shields.io/badge/Build-Gradle-02303A?style=for-the-badge&logo=gradle)](https://gradle.org)
[![Licence](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)](https://opensource.org/licenses/MIT)

An advanced RPG skill and progression plugin for Hytale servers. **AnotherRPG** adds deep, customizable leveling mechanisms, magic systems, combat modifiers, and interactive profession skills with performance-focused reflection caching.

---

## 🚀 Key Features

*   **🏆 OSRS-Style Leveling Strategy**
    *   Implements a classic exponential leveling curve with a configurable maximum level (default: `99`).
    *   Customizable experience multipliers for global or event-specific tuning.
*   **🔮 Rich Skill Modules**
    *   **Combat**: Power up your damage and defensive abilities in combat.
    *   **Magic**: Control magical forces and spells.
    *   **Prosperity**: Encompasses various gathering professions (Mining, Woodcutting, Farming) with dynamic rewards.
*   **🌿 Node-Based Skill Trees**
    *   Define tree structures and perk nodes using JSON configs loaded dynamically from `AnotherRPG/skilltrees/`.
*   **⚡ High-Frequency Game Integration**
    *   Fast event handling systems for block breaking, custom combat/damage display reflection caching, item dropping, and player connections.
    *   **Custom Perks**: Implemented effects like *Double Drop* and *Shatter Strike* to reward gathers.
*   **💾 Flexible Storage**
    *   Local JSON-based flat file player data storage (`player_data/`) for lightweight, robust persistence.
*   **⚙️ Declarative Configurations**
    *   Configurable via `AnotherRPG/AnotherRPGConfig.json` with support for custom block XP rewards.

---

## 🛠️ Commands

| Command | Description | Default Permission |
| :--- | :--- | :--- |
| `/xp` | Displays player levels and current experience points across all active skills. | Players |
| `/rpg` | Opens the custom RPG Skill Tree menu / user interface. | Players |

---

## ⚙️ Configuration

A configuration file is automatically generated at `AnotherRPG/AnotherRPGConfig.json` when the plugin first boots. You can customize the base multiplier and specific XP rewards for individual blocks:

```json
[
  {
    "debugMode": false,
    "xpMultiplier": 1.0,
    "blockRewards": {
      "Stone": {
        "skill": "prosperity",
        "xp": 5.0
      },
      "Ore_Copper": {
        "skill": "prosperity",
        "xp": 15.0
      },
      "Ore_Iron": {
        "skill": "prosperity",
        "xp": 10.0
      },
      "Trunk": {
        "skill": "prosperity",
        "xp": 10.0
      },
      "Plant_Crop": {
        "skill": "prosperity",
        "xp": 8.0
      }
    }
  }
]
```

---

## 📦 Building & Development

Ensure you have Java 17+ and the required Hytale server dependencies inside `libs/Server/HytaleServer.jar`.

### Build & Deploy Mod to Local Run Dir
To compile and copy the resulting `.jar` straight to your test server folder:
```bash
./gradlew deployMod
```

### Run Debug Server
To start the Hytale server with the mod loaded and early plugin acceptance enabled:
```bash
./gradlew runServer
```
