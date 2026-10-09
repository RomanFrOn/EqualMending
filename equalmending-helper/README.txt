Equal Mending Helper — Fabric 1.21.1

What it does:
- Shows equipped armor durability in a small HUD panel.
- Highlights low durability and shows which equipped piece has the least durability remaining.
- It does NOT change Minecraft's server-side Mending XP distribution.

Build:
- Use Java 21.
- Install Gradle 8.10.2 or newer.
- Run: gradle build
- Output: build/libs/equalmending-1.0.0.jar

Or upload the entire folder to a GitHub repository; .github/workflows/build.yml builds and uploads the JAR as an Actions artifact.

Important:
This is a client-side helper only. It cannot force public multiplayer servers to repair armor in a chosen order.
