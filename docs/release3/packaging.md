# Packaging Guide: JLink and JPackage

## Description

Added support for building distributable versions of the Workout Tracker UI using JLink and JPackage. This setup allows us to create self-contained runtime images and installable desktop applications for different operating systems.

## Motivation

We want the project to be deliverable as a standalone desktop app, without requiring users to install Java or JavaFX manually.

## Notes

- Inspired by the IT1901 Todo List Example setup for JavaFX packaging.
- Linux menuGroup and shortcut options are currently disabled to avoid WSL-specific issues.
- **macOS**: Packaging is currently not working due to JavaFX version compatibility issues. Upgrading to JavaFX 17.0.12 breaks all tests, so we remain on 17.0.1. macOS packaging needs further investigation.

---

## Tutorial

### JLink

#### JLink Setup

JLink lets us turn our Java project into a ready-to-run application. Instead of making just a `.jar` file that needs Java installed on your computer, JLink builds a folder that already includes:

- a minimal, custom-built Java Runtime
- all our compiled application classes
- a platform-specific executable

That means anyone can run the app directly, without needing to install Java or JavaFX.

#### How to build With JLink

To build a platform-specific runtime image using JLink, run one of the following commands from the project root (`workout-tracker`):

```bash
mvn clean javafx:jlink -pl ui -Pwin
mvn clean javafx:jlink -pl ui -Plinux
mvn clean javafx:jlink -pl ui -Pmac
```

This uses the `javafx-maven-plugin` to create a self-contained runtime image that includes only the modules required by the UI. In other words, it produces a minimal Java runtime bundled with our application, no external JDK or JavaFX installation is needed.

After the build, you should find the generated image here:

- `ui/target/workout-tracker-win/`
- `ui/target/workout-tracker-linux/`
- `ui/target/workout-tracker-mac/`

Each folder contains its own `/bin` (binary file) directory with the executable launcher.

#### Running the Application

Make sure to start the server first, then run the launcher for your OS:

- **Windows**: `ui\target\workout-tracker-win\bin\workout-tracker.bat`
- **Linux**: `./ui/target/workout-tracker-linux/bin/workout-tracker`
- **macOS**: `./ui/target/workout-tracker-mac/bin/workout-tracker`

Once the server is running and you execute the launcher, the Workout Tracker UI should open and connect to the backend automatically.

---

### JPackage

#### JPackage Setup

JPackage lets us turn our JavaFX app into an installable desktop application, such as a `.deb` file on Linux, `.exe` on Windows, or `.dmg` on macOS. Instead of just shipping a runnable folder, JPackage bundles everything (our app + the custom runtime) into a native installer that users can easily install like any other program.

#### Prerequisites

Make sure you have the required tools installed for your operating system before running JPackage:

**Windows**

Install [WiX Toolset 3.11.2](https://wixtoolset.org/)

**Linux**

Run these commands first:

```bash
sudo apt update
sudo apt install fakeroot binutils xdg-utils -y
```

**macOS**

(No additional prerequisites needed)

#### How to build With JPackage

**Important:** JPackage requires the JLink runtime image to exist. Running `mvn clean jpackage:jpackage` will fail because `clean` deletes the JLink output. Always build JLink first or use the combined command below.

To build an installable package, run one of the following commands from the project root (`workout-tracker`):

```bash
mvn clean javafx:jlink jpackage:jpackage -pl ui -Pwin
mvn clean javafx:jlink jpackage:jpackage -pl ui -Plinux
mvn clean javafx:jlink jpackage:jpackage -pl ui -Pmac
```

This uses the `jpackage-maven-plugin` to wrap the JLink image into a proper installer. After building, you'll find the generated package here:

- `ui/target/dist-win/`
- `ui/target/dist-linux/`
- `ui/target/dist-mac/`

Each folder contains a native installer for your operating system (e.g.,  `.exe`, `.deb` or `.dmg` file).

#### Installing and Running

Once the installer is created, you can install it like a normal application:

**Windows**

Double-click the generated `.exe` installer and follow the setup wizard.

**Linux**

```bash
sudo dpkg -i ui/target/dist-linux/WorkoutTracker_1.0.0-1_amd64.deb
```

Then run:

```bash
/opt/workouttracker/bin/WorkoutTracker
```

**macOS**

Open the `.dmg` file and drag the app to Applications.

⚠️ **Known Issue**: macOS packaging is currently not working due to JavaFX version compatibility. See the Notes section above for details.

**NB:** On Linux (especially WSL), menuGroup and shortcut options are disabled (`false`) because WSL doesn't support graphical desktop menus. On regular Linux systems with a GUI, these can be enabled later to add menu entries automatically.

---

## Summary (demonstrated with Linux OS)

**Build Runtime (JLink) and installer (JPackage)**

```bash
mvn clean javafx:jlink jpackage:jpackage -pl ui -Plinux
```

**Verify build output**

- `ui/target/workout-tracker-linux/` - JLink runtime image
- `ui/target/dist-linux/WorkoutTracker_1.0.0-1_amd64.deb` - JPackage installer

**Install and run:**

```bash
sudo dpkg -i ui/target/dist-linux/WorkoutTracker_1.0.0-1_amd64.deb
/opt/workouttracker/bin/WorkoutTracker
```
