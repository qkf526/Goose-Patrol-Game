# GOOSEAGEDDON (Goose Patrol)

**University of York — Engineering 1 (ENG1), Cohort 4, Team 8**

GOOSEAGEDDON is a single-player, non-violent, university-themed 2D game developed in **Java 17** using **libGDX 1.14.2**. Players evade and disperse successive waves of geese, use inventory items, discover hidden events and attempt to win within a five-minute match.

## Game controls

| Action | Control |
| --- | --- |
| Move player | `W` / `A` / `S` / `D` or arrow keys |
| Select inventory item | `1`, `2`, `3`, `4` |
| Unequip current item | `0` |
| Use equipped item | `Space` |
| Pause / open pause menu | `Esc` |
| Navigate main menu | Up/down arrows |
| Select main menu option | `Enter` |

See the in-game tutorial for further details about items and hidden events.

## Requirements

- **Java 17** or a compatible supported Java installation.
- A desktop operating system supported by the project's LWJGL3 backend.
- The included **Gradle wrapper** (no separate Gradle installation required).

## Project structure

- `core/` — game logic, screens, player, inventory, goose behaviour and events.
- `lwjgl3/` — desktop application launcher and executable JAR configuration.
- `assets/` — maps, sprites, UI graphics, music and sound effects.
- `CREDITS.md` — third-party asset credits and licence information.

## Run the game from source

From the **project root directory**:

**macOS / Linux:**

```bash
./gradlew lwjgl3:run
```

**Windows (Command Prompt / PowerShell):**

```bat
.\gradlew.bat lwjgl3:run
```

The first Gradle build may require internet access to download dependencies.

## Build an executable JAR

From the **project root directory**:

**macOS / Linux:**

```bash
./gradlew lwjgl3:jar
```

**Windows:**

```bat
.\gradlew.bat lwjgl3:jar
```

The resulting JAR will be in `lwjgl3/build/libs/`. With the version included in this project, the expected file name is `GoosePatrol-1.0.0.jar`.

To launch it, run:

```bash
java -jar lwjgl3/build/libs/GoosePatrol-1.0.0.jar
```

Test the JAR on a separate machine or in a clean directory before distributing it; the project build is configured to package runtime dependencies and assets.

## Credits and licensing

Please read **[CREDITS.md](CREDITS.md)** for game asset sources, background-music attribution, sound-effect creators and third-party software licences.

## Development team

Abdullahi Alhassan · Aidan Wong · Chaoke Du · Harry Challinor · Jaki Sun · Kathryn Moon