# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.5.0] - Unreleased

### Added
- Jewelry sets. Four named sets, one for each of the mod's legendary jewelers: Sal'andaar's Anvilwork (Sal'andaar's Ward, Ring of Fortitude), Silbro's Grove (Silbro's Ring of Vitality, Strongman's Bracers), Maldritch's Remains (Maldritch's First Amulet, Ring of Life and Death, Skull Belt) and Selene's Tides (Aqua Ring, Journeyman's Bands, Eye of the Phoenix). Wearing 2 pieces of a set turns on its bonus: ward spells cost 10% less, healing spells heal 15% more and 25% more often, Drain spells reach 1 block further, or Water Breathing and Night Vision last 50% longer. A set piece's tooltip lists the set, lights the pieces you're wearing and the bonuses that are on. Without Curios, set pieces count from the hotbar, like their spells.
- Jewelry HUD in the top-left corner while you wear jewelry with a spell. It shows each worn piece with a mana bar under it (orange for mana wells), the same sweep vanilla draws over an item on cooldown, and the sets you're wearing pieces of, in green once a bonus is on. The client config has `showJewelryHud`, `jewelryHudCorner`, `jewelryHudOffsetX` and `jewelryHudOffsetY`.
- Silbro's Grove grows to five pieces: Silbro's Acorn (necklace, Satiety), the Rootstaff (weapon) and the Barkskin Vest (chest armor). Set weapons and armor carry no spells. The Rootstaff siphons: a full-strength hit on a mob puts 2 mana back into the worn jewelry that has the least. The Barkskin Vest resonates: each worn armor piece of a set gives the spells on that set's jewelry 10% more effect. With 4 pieces, standing on grass, moss or leaves slowly heals you; with all 5, Take Root mends worn set pieces in sunlight (1 durability every 30 seconds). The new pieces drop from Uncommon and better chests, at most one set piece per chest.

### Changed
- Redesigned jewelry tooltips. The header shows the jewelry's type, gem and max spell level under its name. Mana and durability are short bars, each spell has a one-line description and a small box for each of its numbers (effect, range, cooldown, mana cost and so on), and the jewelry's modifiers sit in two columns with full names. Dashed lines are now drawn lines, long text wraps, and Hold [SHIFT] shows the material, gem, repairs and recharges.
- The "+" after a spell's name is now a green "stacks" box: that spell's effect adds up with the same spell on other jewelry you wear.
- New lore for Silbro's Ring of Vitality, and Strongman's Bracers has lore (it's Silbro's work too).
- Fixed Strength (Quick, Greater and Giant Strength) showing its cooldown where its tooltip says the effect's duration.
- With Obscure Tooltips installed, Magic Treasures tooltips leave the icon header to it, so the name isn't shown twice. Legendary Tooltips needs no change.

## [1.4.0] - 2026-10-10

### Added
- Magic Treasures item tooltips now show the item, at double size, beside its name.
- Client config (`magictreasures-client.toml`) with `enableSpellParticles` and `showTooltipIcon`, both on by default. Each player chooses for themselves, even on a server.
- Jewelry Pliers (2 Iron Ingots + 1 Iron Nugget, 64 uses). Craft them with a piece of jewelry that has a gem to get the gem back; the jewelry is destroyed. Removing a gem at the anvil still keeps the jewelry and destroys the gem, so you choose which to keep.
- Advancements: a Magic Treasures tab with Set in Stone (add a gem), Spellbound (add a spell), Second Wind (recharge), Delicate Work (remove a gem with pliers) and Fully Adorned (wear a ring, necklace and bracelet at once; without Curios, have all three in your hotbar).
- The Skull Belt is now a working mana well (250 mana, 2 recharges) and is back in the creative tab. Wear it in the Curios belt slot, hold it, or (without Curios) keep it in your hotbar; when a spell's jewelry runs out of mana, the rest of the cost comes from the belt, so spells keep casting. Recharge it at an anvil with an Amethyst Shard, Emerald or Recharge Scroll. Found in Rare, Epic, Legendary and Mythical chests.

### Changed
- Fixed Mana Shield and Mana Tower Shield letting through too little damage when the jewelry ran short of mana. The damage the shield couldn't pay for replaced the damage it passes through instead of adding to it.
- Every spell cast now costs at least 1 mana. Fire, Magic and Wither Resistance and Mana Shield cost a share of the damage they block, which for small hits like standing in fire was a fraction of a point, so the jewelry wore out (1 durability per cast) long before its mana ran low.
- Fixed Drain ignoring the jewelry's range modifier when finding mobs. The tooltip already showed the modified range.
- Mana Shield and Reflection tooltips now show their cooldown.
- Magic Resistance (Magic Ward, Sal'andaar's Magic Coat) now resists all magic damage: Harming potions, witch potions, evoker fangs and poison. Before, it only worked while the player was poisoned.
- Fixed Reflection and Crushing Response hitting every creature in range, including villagers, iron golems, pets and farm animals, and firing on any damage (falls, fire, drowning). They now reflect only onto the mob that hurt you (the shooter, for arrows), if it's within range, and damage with no attacker doesn't trigger them or cost mana.
- Only shears remove a gem at the anvil now. Axes are no longer in the `magictreasures:tools/stone_removal` tag, because the anvil uses up the tool and it was easy to lose a good axe.
- Fixed fishing junk never giving Magic Treasures loot, and the two fishing options in the server config doing nothing. `enableFishingJunkChestLootModifier` and `enableFishingTreasureChestLootModifier` now control the fishing loot (before, fishing treasure followed the Scarce chest option).
- License metadata now says GNU LGPL v3, matching LICENSE.txt and the source headers (it said GPL v3).
- Harm, Mind Jab and Mind Fist play a sound when they cast, and target the nearest mob in range instead of an arbitrary one.
- Passive spells now show when they work, with new glowing spark particles: Harm, Mind Jab and Mind Fist (violet arc to the mob), Drain (crimson arc from each drained mob), Reflection (cyan arc to each mob hit), Mana Shield and Magic Resistance (blue), Fire Resistance (orange), Wither Resistance and Spectral Armor (pale ash), Paladin Strike (gold, on the struck mob) and Cheat Death (big gold burst plus the totem sound). Healing shows small hearts. The sparks are adapted from gottsch's Monster Manual.
- Rebalanced Harm, Mind Jab and Mind Fist so they strike mobs as they approach instead of only ones already in melee range. Damage: 2 / 3 / 4 to 3 / 4.5 / 6. Range: 2 / 2.5 / 3 to 4 / 5 / 6 blocks. Cooldown and cost are unchanged.

## [1.3.2] - 2026-10-09

### Changed
- Fixed gem ores (Topaz, Onyx, Jadeite, Ruby, Sapphire, in Stone and Deepslate) dropping the ore block instead of the gem. They now drop their gem, with Fortune giving more, like vanilla diamond ore. Silk Touch still drops the ore block.
- Fixed Uncommon, Scarce, Rare, Legendary and Mythical chest loot never appearing. Their loot table ids were missing the `chests/` folder (ex. `minecraft:simple_dungeon` instead of `minecraft:chests/simple_dungeon`), the same typo fixed for Epic chests in 1.3.1, so only Epic chests ever had Magic Treasures loot. Also fixed three misspelled ids: Shepherd and Tannery village chests, and big Underwater Ruins (Epic).

## [1.3.1] - 2026-10-08

### Added
- Spanish translation (es_es, plus es_mx for Latin American players), including the Patchouli guide.
  NOTE: the Spanish translation was generated by a translation service, so it may contain mistakes. If you find any issues, please let me know at https://github.com/gottsch/gottsch-minecraft-Magic-Treasures/issues.

### Changed
- Fixed Epic chest loot modifier never firing (incorrect loot table ids, ex. `minecraft:desert_pyramid` instead of `minecraft:chests/desert_pyramid`).
- Epic chest loot modifier now also applies to Abandoned Mineshaft, Ancient City and Ancient City Ice Box chests.
- Fixed Epic entity loot table "legendar" rarity typo.
- Removed Vex from the General entity loot modifier.
- Updated GottschCore dependency to 2.6.0 (now the minimum required version).
- Fixed rarity loot modifiers always giving the same item; items are now chosen at random.
- Fixed fractional mana being truncated on save/load and client sync (jewelry and Mana Well).
- Fixed hotbar jewelry (no Curios) only checking the first 4 hotbar slots instead of the first 4 jewelry items.
- Fixed jewelry in a second (or later) Curios slot of the same type not casting spells.
- Fixed custom names and enchantments on jewelry being lost on the client and in creative mode.
- Fixed named jewelry (ex. Silbro's Ring of Vitality) gaining duplicate copies of its spells each time it was repaired or recharged at an anvil.
- Fixed spell priority sorting violating the comparator contract (equal priorities now keep their order).
- Harm spells (Harm, Mind Jab, Mind Fist) no longer cast at mobs that are briefly invulnerable after being hit (ex. right after a melee hit), which wasted the spell's mana and cooldown. The spell waits until the mob can be hurt again, or targets another mob in range.
- Curios slots (necklace, ring, bracelet, belt) are now registered by datapack (`data/magictreasures/curios/`) instead of the deprecated IMC messages. Players still get 1 ring slot. Modpacks can add more with their own `curios/slots/ring.json` (Curios uses the largest size).
- Fixed the Speed spell tooltip on jewelry showing "%s" instead of its duration and cooldown.
- Removed an unused Treasure2 import that broke compiling against Treasure2 4.x.
- Skull Belt hidden from the creative tab (the Mana Well feature is not implemented yet).
- Network protocol version bumped to 1.1 (client and server must both be updated).

## [1.3.0] - 2024-11-12

### Changed
- applied Config options to control each Loot Modifier for LootModifierByRarity
- fix Raw Silver smelting recipe
- remove guide book on first join. Still available in Creative tab.

### Added
- Russian lang by CoolDemon96.
- Raw Silver blast furnace recipe.

## [1.2.0] - 2024-09-02

### Changed
- Silver Ore blocks now drop Raw Silver items.
- Raw Silver can be smelted into Silver Ingots.
- Use datagen for blockstate, block model and block loot tables.

### Added
- Raw Silver Item
- Raw Silver to the Forge tags (aka ore dictionary)
- Config options to control each Loot Modifier

## [1.1.0] - 2024-07-15

### Changed
- Renamed Magic Treasures Manual to Guide.
- Added Magic Treasures Guide (Patchouli Guide) to the Magic Treasures tab.
- Updated Guide book to version 1.2
- Added jewelry entries to Guide book.
- Fixed Patchouli guide not adding on first use (naming issue).
- Fixed mods.toml to point to the correct update.json file.
- Blood jewelry material has -10% spell cost.
- Fixed LootModifiers to use the correct chance (the chance always calculated to 0 and thus was not working).
- Update global_loot_modifiers files with the zombie and wither_skeleton modifier.
- Update loot_modifiers loot tables to remove killed_by_player, use random_chance, and rebalance values.

### Added
- Jewelry-Rarity tag mappings!

## [1.0.0] - 2024-07-11

- Initial port from mc1.19.2.

### Changed
- Moved loot function registrations to the common setup.
- Removed Integration config options. If Curios is present, it is enabled.
- Updated Patchouli book to version 1.1
- Cleaned up extra textures
- Fixed naming of some jewelery items during deferred construction.

### Added
