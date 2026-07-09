<p align="center">
  <img src="mainpicture.jpg" alt="DnD Combat Manager mascot" width="220" />
</p>

<h1 align="center">DnD Combat Manager</h1>

<p align="center">
  Un gestionnaire de combat pour Donjons & Dragons (5e).<br/>
  A combat tracker for Dungeons & Dragons (5e).
</p>

<p align="center">
  <a href="#-français">Français</a> ·
  <a href="#-english">English</a>
</p>

---

## 🇫🇷 Français

### Qu'est-ce que c'est ?

**DnD Combat Manager** est une application qui aide les maîtres du jeu à gérer leurs combats de Donjons & Dragons : ordre d'initiative, points de vie, états, actions disponibles, capacités spéciales... tout ce qui se passe habituellement sur une feuille volante ou un tableur devient une interface claire et interactive.

L'application est écrite en **Kotlin Multiplatform** avec **Compose Multiplatform** : le même code d'interface et de logique tourne sur Android, iOS, le Web et le Bureau (Windows/macOS/Linux).

### Fonctionnalités

- **Suivi d'initiative** : les combattants sont automatiquement triés par initiative, avec un compteur de round et un bouton "tour suivant" qui réinitialise les ressources du personnage actif.
- **Fiches de combattant** : type (Personnage joueur, Monstre, Créature légendaire), points de vie (actuels/max/temporaires), classe d'armure, vitesses (marche, vol, nage, escalade), jets de sauvegarde (FOR/DEX/CON/INT/SAG/CHA).
- **Économie d'action** : bascule Action / Action bonus / Réaction, ainsi que la gestion des actions légendaires et des résistances légendaires pour les boss.
- **États et exhaustion** : application des conditions D&D (Aveuglé, Charmé, Effrayé, Empoisonné, Étourdi, Paralysé, etc.) et suivi du niveau d'épuisement (0 à 6).
- **Attaques personnalisées** : éditeur d'attaques avec des étapes typées (jet d'attaque, dégâts, autre effet) et un coût en ressource, utilisables directement pendant le combat.
- **Notes** libres par personnage.
- **Préréglages (presets)** : sauvegardez un personnage ou un monstre pour le réutiliser dans une future rencontre, ou sauvegardez un combat entier (tous les combattants présents) pour le recharger plus tard.
- **Mises en page multiples** : vue en colonne, en frise chronologique, ou en focus (modal) selon vos préférences.
- **Sauvegarde locale persistante** : les préréglages et combats sauvegardés survivent au redémarrage de l'application, sur chaque plateforme.
- **Confirmations** avant les actions destructrices (suppression d'un personnage, réinitialisation du combat, écrasement d'un combat en cours).

### Plateformes

> Statut de buildabilité **sur cette machine** (Linux, JDK + Android SDK installés).

| Plateforme | Buildable ici             |
|---|---------------------------|
| Android | ✅                         |
| Bureau — exécution (Linux) | ✅                         |
| Bureau — installeur `.deb` (Linux) | ✅                         |
| Bureau — installeur `.msi` (Windows) | ✅ |
| Bureau — installeur `.dmg` (macOS) | ❌ (pas encore)            |
| Web (Wasm / JS) | ✅                         |
| iOS | ❌ (pas encore)            |

### Stack technique

- [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html) — logique et modèle partagés entre toutes les cibles.
- [Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform) — interface utilisateur déclarative partagée.
- `kotlinx.serialization` — sérialisation des préréglages et combats sauvegardés.

### Structure du projet

```
├── androidApp/     Point d'entrée Android
├── desktopApp/     Point d'entrée Bureau (JVM)
├── webApp/         Point d'entrée Web (Wasm/JS)
├── iosApp/         Point d'entrée iOS (Xcode/SwiftUI)
└── shared/         Code partagé (UI Compose, état, modèles, stockage)
    └── src/commonMain/kotlin/.../combat/
        ├── model/      Personnage, Attaque, Préréglages...
        ├── state/      État du tracker de combat
        ├── storage/    Persistance des préréglages
        ├── theme/      Thème et couleurs
        └── ui/         Écrans et composants Compose
```

### Lancer l'application

- **Android** : `./gradlew :androidApp:assembleDebug`
- **Bureau** :
  - Hot reload : `./gradlew :desktopApp:hotRun --auto`
  - Exécution standard : `./gradlew :desktopApp:run`
- **Web** :
  - Cible Wasm (plus rapide, navigateurs récents) : `./gradlew :webApp:wasmJsBrowserDevelopmentRun`
  - Cible JS (plus lente, compatible anciens navigateurs) : `./gradlew :webApp:jsBrowserDevelopmentRun`
- **iOS** : ouvrez le dossier [`/iosApp`](./iosApp) dans Xcode et lancez depuis l'IDE.

Vous pouvez aussi utiliser directement les configurations de lancement fournies par votre IDE (Android Studio / IntelliJ IDEA).

### Lancer les tests

- Android : `./gradlew :shared:testAndroidHostTest`
- Bureau : `./gradlew :shared:jvmTest`
- Web : `./gradlew :shared:wasmJsTest` ou `./gradlew :shared:jsTest`
- iOS : `./gradlew :shared:iosSimulatorArm64Test`

### Générer un installeur (Bureau)

L'application Bureau peut être empaquetée en installeur natif via Compose Multiplatform :

```
./gradlew packageMsi   # Windows
./gradlew packageDmg   # macOS
./gradlew packageDeb   # Linux
```

Un workflow GitHub Actions (`.github/workflows/build-msi.yml`) permet de générer le `.msi` Windows à la demande.

---

## 🇬🇧 English (Not yet 😭)

### What is this?

**DnD Combat Manager** is an app that helps Dungeon Masters run Dungeons & Dragons combat encounters: initiative order, hit points, conditions, available actions, custom abilities... everything that usually lives on a scrap of paper or a spreadsheet becomes a clear, interactive interface.

The app is built with **Kotlin Multiplatform** and **Compose Multiplatform**: the same UI and logic code runs on Android, iOS, Web, and Desktop (Windows/macOS/Linux).

### Features

- **Initiative tracking**: combatants are automatically sorted by initiative, with a round counter and a "next turn" button that refreshes the active character's resources.
- **Combatant sheets**: type (Player character, Monster, Legendary creature), hit points (current/max/temporary), armor class, speeds (walk, fly, swim, climb), saving throws (STR/DEX/CON/INT/WIS/CHA).
- **Action economy**: toggle Action / Bonus action / Reaction, plus legendary action and legendary resistance tracking for bosses.
- **Conditions & exhaustion**: apply standard D&D conditions (Blinded, Charmed, Frightened, Poisoned, Stunned, Paralyzed, etc.) and track exhaustion level (0 to 6).
- **Custom attacks**: an attack editor with typed steps (attack roll, damage, other effect) and a resource cost, usable directly during combat.
- **Free-form notes** per character.
- **Presets**: save a character or monster to reuse in a future encounter, or save an entire combat (every combatant present) to reload it later.
- **Multiple layouts**: sidebar (column), timeline, or focus (modal) view, depending on preference.
- **Persistent local storage**: saved presets and combats survive an app restart, on every platform.
- **Confirmation dialogs** before destructive actions (deleting a character, clearing the encounter, overwriting an in-progress combat).

### Platforms

> Buildability status **on this machine** (Linux, JDK + Android SDK installed).

| Platform | Buildable here            |
|---|---------------------------|
| Android | ✅                         |
| Desktop — run (Linux) | ✅                         |
| Desktop — `.deb` installer (Linux) | ✅                         |
| Desktop — `.msi` installer (Windows) | ✅  |
| Desktop — `.dmg` installer (macOS) | ❌ (not yet)               |
| Web (Wasm / JS) | ✅                         |
| iOS | ❌ (not yet)               |

### Tech stack

- [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html) — shared logic and models across every target.
- [Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform) — shared declarative UI.
- `kotlinx.serialization` — serialization for saved presets and combats.

### Project structure

```
├── androidApp/     Android entry point
├── desktopApp/     Desktop (JVM) entry point
├── webApp/         Web (Wasm/JS) entry point
├── iosApp/         iOS entry point (Xcode/SwiftUI)
└── shared/         Shared code (Compose UI, state, models, storage)
    └── src/commonMain/kotlin/.../combat/
        ├── model/      Character, Attack, Presets...
        ├── state/      Combat tracker state
        ├── storage/    Preset persistence
        ├── theme/      Theme and colors
        └── ui/         Compose screens and components
```

### Running the app

- **Android**: `./gradlew :androidApp:assembleDebug`
- **Desktop**:
  - Hot reload: `./gradlew :desktopApp:hotRun --auto`
  - Standard run: `./gradlew :desktopApp:run`
- **Web**:
  - Wasm target (faster, modern browsers): `./gradlew :webApp:wasmJsBrowserDevelopmentRun`
  - JS target (slower, supports older browsers): `./gradlew :webApp:jsBrowserDevelopmentRun`
- **iOS**: open the [`/iosApp`](./iosApp) directory in Xcode and run it from there.

You can also use the run configurations provided by your IDE's toolbar (Android Studio / IntelliJ IDEA).

### Running tests

- Android: `./gradlew :shared:testAndroidHostTest`
- Desktop: `./gradlew :shared:jvmTest`
- Web: `./gradlew :shared:wasmJsTest` or `./gradlew :shared:jsTest`
- iOS: `./gradlew :shared:iosSimulatorArm64Test`

### Building a native installer (Desktop)

The desktop app can be packaged into a native installer via Compose Multiplatform:

```
./gradlew packageMsi   # Windows
./gradlew packageDmg   # macOS
./gradlew packageDeb   # Linux
```

A GitHub Actions workflow (`.github/workflows/build-msi.yml`) can build the Windows `.msi` on demand.
