# Lava Boats - Fabric (Minecraft 26.2 (pre-release))

**Fabric** loader builds of Lava Boats for the Minecraft 26.2 (pre-release) line. Client + server mod. Fabric only on this line (no Quilt - see below).
Standalone - no Architectury, no shared `Common/`.

## Builds

| Version | Minecraft | Java | Toolchain |
| --- | --- | --- | --- |
| [`26.2/`](26.2) | 26.2 (pre-release) | 25 | fabric-loom, built vs 26.2-rc-2 |

## Excluded / not built

- **Quilt** is not supported on the 26.x line - Quilt retired Quilted Fabric API at 26.1, so the Fabric API path Lava Boats uses on Fabric is no longer provided on Quilt for 26.x. (Quilt remains supported on the 1.20.x and 1.21.x branches.)

## Build

```
cd <version>
./gradlew build      # Windows: .\gradlew.bat build
```

Output: `build/libs/lava-boats-*.jar`. Part of the [`26.2` branch](https://github.com/Kishku7/lava-boats/tree/26.2).
