# Lava Boats - Minecraft 26.1.2 (source branch)

MC 26.x ships unobfuscated (mojmap-native), so this branch does NOT use Architectury.
Two independent per-loader source trees, each self-contained:

- fabric/    plain fabric-loom.        Build: cd fabric   && ./gradlew build
- neoforge/  standalone ModDevGradle.  Build: cd neoforge && ./gradlew build

Both require JDK 25. Shipped per-loader (no merge on 26.x).
mod_version: 1.1.2+26.1.2 (fabric) / 1.1.2 (neoforge).
