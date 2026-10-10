<p align="center">
  <img src="https://raw.githubusercontent.com/wiki/gottsch/gottsch-minecraft-Magic-Treasures/images/curseforge/headings/magic_treasures_title_stacked.png" alt="Magic Treasures">
</p>

Magic Treasures adds magic jewelry and wearable items, gems and spells. There are 300+ items and 30+ spells.

[CurseForge](https://www.curseforge.com/minecraft/mc-mods/magic-treasures) · [Issues](https://github.com/gottsch/gottsch-minecraft-Magic-Treasures/issues) · [Changelog](CHANGELOG.md)

## Requirements

- [GottschCore](https://www.curseforge.com/minecraft/mc-mods/gottschcore) (required)

Magic Treasures is a stand-alone mod, but it's highly recommended to play it with [Treasure2](https://www.curseforge.com/minecraft/mc-mods/treasure2).

## How jewelry works

To activate jewelry, hold it in your hand and/or keep it in your hotbar. Only the first 4 jewelry items in your hotbar are used.

If [Curios](https://www.curseforge.com/minecraft/mc-mods/curios) is installed, wear jewelry in the matching Curios slot (ring, necklace, bracelet or belt) instead. The hotbar is then disabled for jewelry.

## Jewelry

![Jewelry](https://raw.githubusercontent.com/wiki/gottsch/gottsch-minecraft-Magic-Treasures/images/curseforge/jewelry.png)

300+ jewelry items to discover or craft. Each piece of jewelry has its own mana and can be imbued with magic spells. Jewelry is made of different materials and sizes, each with its own set of attributes that affect its spells. Gems can be affixed for added mana, recharges and spell buffs.

## Gems

![Gems](https://raw.githubusercontent.com/wiki/gottsch/gottsch-minecraft-Magic-Treasures/images/curseforge/gems.png)

Most gems can be combined with jewelry to increase mana and the number of recharges, and to boost spells. Other gems, like Amethyst and Emerald, are used to recharge jewelry.

## Spell Scrolls

![Spell Scrolls](https://raw.githubusercontent.com/wiki/gottsch/gottsch-minecraft-Magic-Treasures/images/curseforge/spell_scrolls.png)

30+ spells to discover, from simple Healing to Paladin's Strike to Cheat Death. Each spell scroll's color shows its level:

| Color | Level |
|---|---|
| Yellow | 1–2 |
| Green | 3–4 |
| Blue | 5–6 |
| Red | 7–8 |
| Black | 9+ |

To imbue jewelry with a spell, place the jewelry and the spell scroll in an anvil.

## Recipe Scrolls

![Recipe Scrolls](https://raw.githubusercontent.com/wiki/gottsch/gottsch-minecraft-Magic-Treasures/images/curseforge/recipe_scrolls.png)

Recipe Scrolls are one-time-use scrolls for crafting a jewelry item from materials you mine or find: Wood, Iron, Copper, Silver, Gold, Bone and more.

## Ores

![Ores](https://raw.githubusercontent.com/wiki/gottsch/gottsch-minecraft-Magic-Treasures/images/curseforge/ores.png)

Gem and Silver ores are found in both Stone and Deepslate. Spawn depths vary, but they're generally found around Y=0.

Note: Silver ingots are only used to make jewelry. There are no Silver swords or armor.

## Integrations

![Integrations](https://raw.githubusercontent.com/wiki/gottsch/gottsch-minecraft-Magic-Treasures/images/curseforge/integrations.png)

- **[Treasure2](https://www.curseforge.com/minecraft/mc-mods/treasure2):** integrated by default. Treasure chests are filled with Magic Treasures' gems, jewelry, spells and recipes. Magic Treasures' gems can be used in Treasure2's wishing wells, and Treasure2's gems can be used to make jewelry.
- **[Curios](https://www.curseforge.com/minecraft/mc-mods/curios):** wear jewelry in the Ring, Necklace, Bracelet and Belt slots. When Curios is loaded, it replaces the default hotbar setup.
- **[Diamethysts!](https://www.curseforge.com/minecraft/mc-mods/diamethysts):** use its crystals and shards to recharge magic jewelry.
- **[Patchouli](https://www.curseforge.com/minecraft/mc-mods/patchouli):** an in-game guide book.

---

## Versions

| Minecraft | Loader | Magic Treasures | Branch | GottschCore | Treasure2 (optional) |
|---|---|---|---|---|---|
| 1.21.1 | NeoForge 21.1.117+ | 1.3.2+ | `neoforge-1.21.1-main` | 2.7.0+ | 5.0.0+ |
| 1.20.1 | Forge 47+ | 1.3.x | `1.20.1-main` | 2.6.0+ | 3.8.1+ |

Jewelry from a 1.20.1 world doesn't keep its Magic Treasures data in 1.21.1. See the [changelog](CHANGELOG.md).

## For modpack and datapack makers

- **Curios slots:** registered by datapack in `data/magictreasures/curios/`. Players get 1 ring slot. To add more, ship your own `curios/slots/ring.json` with a larger `size` (Curios uses the largest one).
- **Loot:** Magic Treasures adds its loot through global loot modifiers (`data/neoforge/loot_modifiers/` on 1.21.1, `data/forge/loot_modifiers/` on 1.20.1). Each modifier can be turned off in the config.
- **Tags:** silver and gem items use the common tags: `c:` on 1.21.1 (ex. `c:ingots/silver`), `forge:` on 1.20.1.

## Building

```
./gradlew build
```

The jar is written to `build/libs/`.

## License

[GNU LGPL v3](LICENSE.txt)
