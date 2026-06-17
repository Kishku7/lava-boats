# Lava Boats - Fabric (Minecraft 26.1.2)

**Fabric** loader builds of Lava Boats for the Minecraft 26.1.2 line. Client + server mod. Fabric only on this line (no Quilt - see below).
Standalone - no Architectury, no shared `Common/`.

## Builds

| Version | Minecraft | Java | Toolchain |
| --- | --- | --- | --- |
| [`26.1.2/`](26.1.2) | 26.1.2 | 25 | fabric-loom |

## Excluded / not built

- **Quilt** is not supported on the 26.x line - Quilt retired Quilted Fabric API at 26.1, so the Fabric API path Lava Boats uses on Fabric is no longer provided on Quilt for 26.x. (Quilt remains supported on the 1.20.x and 1.21.x branches.)

## Build

```
cd <version>
./gradlew build      # Windows: .\gradlew.bat build
```

Output: `build/libs/lava-boats-*.jar`. Part of the [`26.1` branch](https://github.com/Kishku7/lava-boats/tree/26.1).
