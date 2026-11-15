# Goose Patrol

**Goose Patrol** is a single-player, top-down desktop game developed in Java with [libGDX](https://libgdx.com/) for the Software Engineering 1 (ENG1) group project. Play as a student exploring a campus while trying to disperse successive waves of geese before time or Energy runs out.

## Objective

Disperse **all five waves of geese within five minutes** while protecting your Energy. Geese approach from the lake area, chase the student and can peck them. A successful peck reduces Energy by one point. The student starts with **10 Energy**.

The current implementation uses the following wave sizes:

| Wave | Geese |
| --- | ---: |
| 1 | 5 |
| 2 | 7 |
| 3 | 10 |
| 4 | 13 |
| 5 | 15 |
| **Total** | **50** |

There is a **10-second break** between cleared waves. A new match begins with a **three-second “Get Ready!” countdown** before gameplay starts. You win by dispersing every wave; the game ends in defeat if the five-minute timer expires or Energy reaches zero.

## Requirements and running the game

Use the **complete project repository**, including the Gradle wrapper, `core`, `lwjgl3` and `assets` directories. The Java-only source archive is not runnable by itself.

- **Java:** JDK 17 (the version used for development).
- **Platform:** Desktop (libGDX with the LWJGL3 launcher).
- **Build tool:** Gradle, using the included wrapper; a separate Gradle installation is not required.

Open a terminal **at the project root**, where `settings.gradle` and `gradlew` are located.

**macOS / Linux**

```bash
./gradlew lwjgl3:run
```

**Windows (Command Prompt)**

```bat
gradlew.bat lwjgl3:run
```

The game opens on the Main Menu. Select **Play** and press **Enter** to start a new match.

> **Assets required:** Both Tiled maps (`maps/campus.tmx` and `maps/greggs.tmx`), the referenced map images/tilesets, character and goose sprites, menu images, inventory icons and files under `assets/sounds/` must be included in the complete repository. The game loads these files at runtime.

## Controls

| Key | Action |
| --- | --- |
| **W / A / S / D** or **arrow keys** | Move the student during gameplay |
| **1–4** | Select an available inventory item |
| **0** | Unequip the current item |
| **Space** | Use the equipped item |
| **E** | Interact with Greggs doors, the cashier or the Long Boi statue; advance dialogue |
| **Esc** | Pause or resume gameplay |
| **Up / Down** | Change the highlighted menu option |
| **Enter** (or numpad Enter) | Confirm a menu selection |
| **Enter / Esc** | Return from the Tutorial screen |

Menus and gameplay are designed primarily for **keyboard input**.

## Items and Energy

| Item | Starting quantity | Effect |
| --- | ---: | --- |
| **Seeds** | 20 | Distract up to two nearby geese, which move towards the seeds and subsequently flee |
| **Air Horn** | 10 | Frighten up to three eligible nearby geese, causing them to flee |
| **Bandage** | 7 | Restore up to two Energy points |
| **Sausage Roll** | 0 | Restore Energy to full; discover and collect one through the Greggs event |

The fourth inventory slot initially shows a **question mark** and zero quantity. Completing the Greggs reward dialogue reveals the Sausage Roll icon. Healing items cannot be used at full Energy, and items are consumed only after a successful use.

## Hidden campus events

There are **two optional events**, shown as a completed-events counter in the gameplay HUD:

- **Greggs anniversary:** Approach the Greggs entrance and press **E** to enter the interior. Talk to the cashier and advance through the dialogue with **E** to receive one Sausage Roll. Use **E** at the interior exit to return to campus.
- **Long Boi statue:** Approach the statue and press **E**. Its one-time honk frightens eligible geese within range. The event can be triggered once per match.

The overall gameplay timer continues while the student is inside Greggs. Completing the two events is optional; the win condition is clearing all goose waves.

## Screens and on-screen information

- **Main Menu:** Play, Tutorial and Quit.
- **Gameplay:** Five-minute timer; wave and goose counters; Energy percentage; inventory; items-used count; and events completed (`0/2` to `2/2`).
- **Pause Menu:** Resume, Restart, Tutorial and End Game.
- **Tutorial:** Keyboard controls and objectives; return to the previous screen with Enter or Esc.
- **Game Over:** Separate outcomes for running out of Energy and running out of time; Try Again or Main Menu.
- **Victory:** Remaining time, Energy percentage, number of items used and number of completed events; Play Again or Main Menu.

The game includes background music and sound effects for movement, geese, items, interactions, countdowns, wave starts and endings.

## Project organisation

The full libGDX project follows this structure:

```text
GoosePatrol/
├── assets/                              # Maps, textures, UI and audio
│   ├── maps/                            # campus.tmx, greggs.tmx and dependencies
│   ├── character/                       # Student and item animation sprites
│   ├── goose/                           # Goose animation sprites
│   ├── menu/                            # Menu and end-screen artwork
│   ├── ui/                              # HUD and inventory icons
│   ├── effects/                         # Seed-patch artwork
│   └── sounds/                          # Music and sound effects
├── core/                                # Shared Java gameplay code
│   └── src/main/java/com/goosepatrol/game/
├── lwjgl3/                              # Desktop launcher
├── gradlew                              # Gradle wrapper (macOS/Linux)
├── gradlew.bat                          # Gradle wrapper (Windows)
└── settings.gradle                      # Gradle project settings
```

The main Java responsibilities are:

- `GoosePatrolGame`: application lifecycle, screen transitions and shared sound management.
- `GameplayScreen`: gameplay updates, map rendering, HUD and integration of game systems.
- `Player`, `CollisionMap`: student movement, Energy, animations and collision handling.
- `Goose`, `GooseWaveManager`, `GooseAnimations`, `GooseHUD`: goose behaviour, waves, effects, animations and counters.
- `Inventory`, `InventorySlot`, `InventoryController`, `InventoryUI`, `Item` and subclasses: item quantities, selection, usage and presentation.
- `EventArea`, `Event`, `StatueEvent`, `Dialog`: Greggs and Long Boi interactions.
- `SoundManager`: shared background music and sound effects.
- `MainMenuScreen`, `PauseScreen`, `TutorialScreen`, `GameOverScreen`, `VictoryScreen`: menu and outcome screens.

## Asset attribution


## Development notes

The game uses Tiled (`.tmx`) maps, libGDX cameras and viewports for world and UI rendering, and separate application screens for menus and gameplay. Each new match creates fresh gameplay state, resetting wave progression, inventory quantities and optional event completion.
