# mc-mods

This repository contains several Minecraft Forge mods. The `duel-mod` and `linkedheart-mod` directories provide source code for two example mods targeting Forge 1.20.1.

* **Duel Mod** – adds a `/duell` command for 1v1 duels with `/accept` and `/deny` support.
* **Linked Heart Mod** – demonstrates a basic linked-heart system triggered when players lose extra hearts.

Both mods include minimal Forge build files. Download `forge-1.20.1-47.4.2-mdk.zip` and extract it somewhere. Use the provided `gradlew` wrapper from that archive to build the mods, for example:

```bash
unzip forge-1.20.1-47.4.2-mdk.zip -d forge-mdk
./forge-mdk/gradlew -p duel-mod build
./forge-mdk/gradlew -p linkedheart-mod build
```

The wrapper handles all dependencies without requiring a local Gradle installation.
