<p align="center">
  <img src="https://raw.githubusercontent.com/wiki/gottsch/gottsch-minecraft-Magic-Treasures/images/curseforge/headings/magic_treasures_title_stacked.png" alt="Magic Treasures">
</p>

Magic Treasures adds magic jewelry, gems and spells: 40+ spells, named jewelry, four jewelry sets and fifteen lore books. Find or craft rings, necklaces and bracelets, set gems in them, imbue them with spells at an anvil, and wear them to have the spells cast for you.

[CurseForge](https://www.curseforge.com/minecraft/mc-mods/magic-treasures) · [Wiki](https://github.com/gottsch/gottsch-minecraft-Magic-Treasures/wiki) · [Issues](https://github.com/gottsch/gottsch-minecraft-Magic-Treasures/issues) · [Changelog](CHANGELOG.md)

## Requirements

- [GottschCore](https://www.curseforge.com/minecraft/mc-mods/gottschcore) (required)
- Gottsch Books, the reader for the manual and lore books, comes inside Magic Treasures. Nothing extra to install.

Magic Treasures is a stand-alone mod, but it's highly recommended to play it with [Treasure2](https://www.curseforge.com/minecraft/mc-mods/treasure2).

## How jewelry works

Jewelry casts its spells while it's in your hotbar (including the slot in your hand). Only the first 4 jewelry items in your hotbar are used.

If [Curios](https://www.curseforge.com/minecraft/mc-mods/curios) is installed, wear jewelry in the matching Curios slot (ring, necklace, bracelet or belt) instead. The hotbar is then disabled for jewelry.

## Jewelry

![Jewelry](https://raw.githubusercontent.com/wiki/gottsch/gottsch-minecraft-Magic-Treasures/images/curseforge/jewelry.png)

Rings, necklaces and bracelets to discover or craft. Each piece has its own mana and can be imbued with magic spells. Its material and style (Regular, Great or Lord's) set its mana, durability and spell level, and how its spells perform. Gems can be affixed for added mana, recharges and spell buffs.

Named jewelry are unique pieces that come with their own spells. Some belong to one of four **sets**: wear more pieces of a set for set bonuses.

## Gems

![Gems](https://raw.githubusercontent.com/wiki/gottsch/gottsch-minecraft-Magic-Treasures/images/curseforge/gems.png)

Most gems can be combined with jewelry to increase mana and the number of recharges, and to boost spells. Other gems, like Amethyst and Emerald, are used to recharge jewelry.

## Spell Scrolls

![Spell Scrolls](https://raw.githubusercontent.com/wiki/gottsch/gottsch-minecraft-Magic-Treasures/images/curseforge/spell_scrolls.png)

40+ spells to discover, from simple Healing to Paladin's Strike to Cheat Death. Each spell scroll's color shows its level:

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

Recipe Scrolls are one-time-use scrolls for crafting a ring, necklace or bracelet from 4 iron, copper, silver or gold ingots, or 4 bones.

## Lore Books

Fifteen books tell the story behind the named jewelry and sets: the journals, ledgers and letters of Silbro, Sal'andaar, Maldritch and Selene. Find them in chests, and read them in their own book reader.

## Ores

![Ores](https://raw.githubusercontent.com/wiki/gottsch/gottsch-minecraft-Magic-Treasures/images/curseforge/ores.png)

Gem and Silver ores are found in both Stone and Deepslate. Spawn depths vary, but they're generally found around Y=0.

Note: Silver ingots are only used to make jewelry. There are no Silver swords or armor.

## Integrations

![Integrations](https://raw.githubusercontent.com/wiki/gottsch/gottsch-minecraft-Magic-Treasures/images/curseforge/integrations.png)

- **[Treasure2](https://www.curseforge.com/minecraft/mc-mods/treasure2):** integrated by default. Treasure chests are filled with Magic Treasures' gems, jewelry, spells and recipes. Magic Treasures' gems can be used in Treasure2's wishing wells, and Treasure2's gems can be used to make jewelry.
- **[Curios](https://www.curseforge.com/minecraft/mc-mods/curios):** wear jewelry in the Ring, Necklace, Bracelet and Belt slots. When Curios is loaded, it replaces the default hotbar setup.
- **[Diamethysts!](https://www.curseforge.com/minecraft/mc-mods/diamethysts):** use its crystals and shards to recharge magic jewelry.

The in-game **Magic Treasures Manual** (in the Magic Treasures creative tab) no longer needs Patchouli.

---

## Versions

| Minecraft | Loader | Magic Treasures | Branch | GottschCore | Treasure2 (optional) |
|---|---|---|---|---|---|
| 1.21.1 | NeoForge 21.1.117+ | 2.0.0+ | `neoforge-1.21.1-main` | 2.7.0+ | 5.0.0+ |
| 1.20.1 | Forge 47+ | 2.0.0+ | `1.20.1-main` | 2.10.0+ | 3.8.1+ |

Jewelry from a 1.20.1 world doesn't keep its Magic Treasures data in 1.21.1. See the [changelog](CHANGELOG.md).

## For modpack and datapack makers

- **Jewelry from datapacks:** materials, gems, styles and named pieces are JSON in `data/<namespace>/magictreasures/`. A datapack can add its own or change the built-in ones. See [Jewelry Datapacks](https://github.com/gottsch/gottsch-minecraft-Magic-Treasures/wiki/Jewelry-Datapacks) on the wiki.
- **Curios slots:** registered by datapack in `data/magictreasures/curios/`. Players get 1 ring slot. To add more, ship your own `curios/slots/ring.json` with a larger `size` (Curios uses the largest one).
- **Loot:** Magic Treasures adds its loot through global loot modifiers (`data/neoforge/loot_modifiers/` on 1.21.1, `data/forge/loot_modifiers/` on 1.20.1). Each modifier can be turned off in the config.
- **Tags:** silver and gem items use the common tags: `c:` on 1.21.1 (ex. `c:ingots/silver`), `forge:` on 1.20.1.

More on the [wiki](https://github.com/gottsch/gottsch-minecraft-Magic-Treasures/wiki/Modpacks-and-Datapacks).

## Building

```
./gradlew build
```

The jar is written to `build/libs/`.

## License

[GNU LGPL v3](LICENSE.txt)
