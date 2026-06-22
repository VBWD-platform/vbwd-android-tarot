# vbwd-android-tarot

Part of the **vbwd-android** SDK — the Kotlin/Compose/Hilt port of the vbwd-ios
plugin-host platform. Module `tarot`.

## Dependency
Consumes the core SDK as a GitHub Packages artifact:
`com.vbwd:vbwd-android-core:0.1.0`.
Set `gpr.user`/`gpr.key` (a PAT with `read:packages`) in
`~/.gradle/gradle.properties`, or `GITHUB_ACTOR`/`GITHUB_TOKEN` in the env.

## Build & test
```bash
./gradlew check        # ktlint + detekt + unit tests
```

## Docs
See `docs/`. License: BSL 1.1.
