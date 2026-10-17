# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [2.0.0] - Unreleased

**Breaking: jewelry is now data.** Every ring, necklace and bracelet is one of three items (`magictreasures:ring`, `magictreasures:necklace`, `magictreasures:bracelet`); what a piece is (material, gem, style, named piece) is stored on the item. Your 1.x jewelry converts on its own, keeping its name, enchantments, gem, spells, mana and uses. Datapacks and modpacks that name the old jewelry items (`magictreasures:gold_ruby_ring` and so on) need updating; see Changed.

### Added
- Jewelry from datapacks. Materials, styles (regular, great, lords), stone tiers, gems and named pieces are JSON in `data/<namespace>/magictreasures/` (`jewelry_material/`, `jewelry_style/`, `stone_tier/`, `gemstone/`, `jewelry_preset/`). A datapack can add a material (titanium, say), a gem, a style or a named piece, change a built-in one by writing the same file, or switch one off with `"disabled": true`. A material or gem without art of its own is drawn by tinting the plain textures with its `color`, so a pack needs no resource pack to work. See the wiki's Jewelry Datapacks page.
- One crafting recipe for every material: a recipe scroll and 4 of the material's ingredient (iron, copper, silver or gold ingots, bones, or a pack's own), so a pack's material is craftable with no recipe file. A datapack recipe for the same grid still wins.
- Loot draws from the data: a pack's materials, gems and named pieces drop from mobs and chests (including Treasure2's) with no loot JSON. Loot functions `magictreasures:random_jewelry` and `magictreasures:random_gemstone` take ids or `#tags`, and the new `magictreasures:set_jewelry_preset` makes a named piece. When a rarity has no gems or jewelry (a pack with no mythical gems, say), the next rarity down is used.
- `/magictreasures give <players> <type> <material> [<gem> [<style>]]` (`minecraft:none` for no gem) and `/magictreasures give <players> preset <id>` (operators). `/magictreasures data` writes every loaded jewelry entry to the log; `/magictreasures jewelry` shows the held piece's stats.
- 17 new advancements, in three branches. Crafting: Apprentice Jeweler (craft jewelry), Good as New (repair at the anvil), Silver Lining (a silver ingot). Magic: Journeyman, Adept and Master Enchanter and Archmage (a spell of level 3, 5, 7 or 9 and up), Running on Empty (a spell paid by a mana well). Collecting: Gem Collector (every Magic Treasures gem), Lord's Ransom (Lord's jewelry), Treasure Hunter (legendary or mythical jewelry), Dressed to the Nines (a ring, necklace, bracelet and belt at once), one for each named set worn whole, and Collector of Legends (all four sets).
- Second Thoughts advancement: remove a gem with shears at the anvil.
- For mod developers: `MagicTreasuresApi` can recognise, read and make jewelry (`isJewelry`, `getIdentity`, `getJewelry`, `createJewelry`, `createPreset`, `isAllowed`), and `MagicTreasuresRegistries` has the registry keys.
- Jewelry sets. Four named sets, one for each of the mod's legendary jewelers: Sal'andaar's Anvilwork (Sal'andaar's Ward, Ring of Fortitude), Silbro's Grove (Silbro's Ring of Vitality, Strongman's Bracers), Maldritch's Remains (Maldritch's First Amulet, Ring of Life and Death, Skull Belt) and Selene's Tides (Aqua Ring, Journeyman's Bands, Eye of the Phoenix). Wearing 2 pieces of a set turns on its bonus: ward spells cost 10% less, healing spells heal 15% more and 25% more often, Drain spells reach 1 block further, or Water Breathing and Night Vision last 50% longer. A set piece's tooltip lists the set, lights the pieces you're wearing and the bonuses that are on. Without Curios, set pieces count from the hotbar, like their spells.
- Jewelry HUD in the top-left corner while you wear jewelry with a spell. It shows each worn piece with a mana bar under it (orange for mana wells), the same sweep vanilla draws over an item on cooldown (split into one column per spell when a piece has more than one, so the Ring of Life and Death shows Cheat Death and Greater Drain side by side), and the sets you're wearing pieces of, in green once a bonus is on. The client config has `showJewelryHud`, `jewelryHudCorner`, `jewelryHudOffsetX` and `jewelryHudOffsetY`.
- Silbro's Grove grows to five pieces: Silbro's Acorn (necklace, Satiety), the Rootstaff (weapon) and the Barkskin Vest (chest armor). Set weapons and armor carry no spells. The Rootstaff (7 damage, a little longer reach than a sword) siphons: a full-strength hit on a mob puts 2 mana back into the worn jewelry that has the least. The Barkskin Vest resonates: each worn armor piece of a set gives the spells on that set's jewelry 10% more effect. With 4 pieces, standing on grass, moss or leaves slowly heals you; with all 5, Take Root mends worn set pieces in sunlight (1 durability every 30 seconds). The new pieces drop from Uncommon and better chests, at most one set piece per chest.
- Sal'andaar's Anvilwork grows to six pieces: the Runesmith's Cuff (bracelet, Reflection), the Stonemark Hammer (weapon: 11 damage, slow, siphon 2), the Runesmith's Helm (helmet, with its own horned model) and the Anvil Shield (shield). With 4 pieces, ward spells recover 15% faster; with all 6, Tempered: the set's worn jewelry is stamped, and its next anvil repair is free (it doesn't use up one of its repairs). Wearing the full set again stamps it again.
- Maldritch's Remains grows to five pieces: the Bone Scythe (weapon: 9 damage, longer reach, siphon 3, the strongest) and the Lich's Mask (helmet). With 4 pieces, half the life your Drain spells take comes back as mana in the jewelry that cast them. With all 5, Bound Possessions: if you die wearing the full set, its worn pieces stay with you (back in their slots when you respawn, Curios slots included); everything else drops as usual, so grave mods still work.
- Selene's Tides grows to five pieces: the Moonglaive (weapon: 8 damage, longer reach, siphon 2) and the Pearl Circlet (helmet). With 4 pieces, Tidewalker: in water you swim with Dolphin's Grace, and the set's worn jewelry regains 1 mana every 5 seconds. With all 5, Moonlit: every spell costs 25% less at night (never in the Nether or the End).
- The long set weapons (Rootstaff, Bone Scythe, Moonglaive) reach 0.5 blocks further than a sword. Set weapons and armor show up in the enchanting table with the enchantments of their kind (sword, helmet, chestplate, shield).
- Lore books: the stories of Sal'andaar, Silbro, Maldritch and Selene, found in chests (one per chest, from the chest's rarity). Right-click to read; they can't be edited or copied. Fifteen in all, from Silbro's journals and the runesmith's ledger to Maldritch's notes, the letters between the four, and Selene's last page. They open in a book reader of their own, sized to your window, with pictures of the pieces the stories mention, and they're translated into Spanish and Russian. New advancements: First Pages (read one), Archivist (read them all) and a hidden one for each of the four authors. The reader is Gottsch Books, which comes inside Magic Treasures (nothing extra to install). Books are data, so packs can add their own: `data/<namespace>/magictreasures/lore_book/<id>.json` (rarity, icon), `data/<namespace>/gottschbooks/book/<id>.json` (`{}`, or `{"disabled": true}` to switch a book off), the text in Markdown at `assets/<namespace>/books/<id>/<locale>/text.md`, and the icon at `textures/item/lore_book/<id>.png`. The per-author advancements count the book tags `#magictreasures:books/<author>`.
- New spells: Ring of Swords (level 5, Scarce) and Greater Ring of Swords (level 9, Legendary). When a hostile mob comes within 6 blocks, spectral swords appear and circle you: 4 swords dealing 4 damage a hit for 20 seconds, or 6 swords dealing 6 for 25 seconds. A sword wears out as it lands blows (2 hits, or 3 for the Greater) and shatters. One cast pays once (10 or 20 mana), and only one ring is out at a time. The swords hit only hostile mobs, never players, pets or villagers. Their scrolls drop from Scarce and Legendary chests.

### Changed
- Redesigned jewelry tooltips. The header shows the jewelry's type, gem and max spell level under its name. Mana and durability are short bars, each spell has a one-line description and a small box for each of its numbers (effect, range, cooldown, mana cost and so on), and the jewelry's modifiers sit in two columns with full names. Dashed lines are now drawn lines, long text wraps, and Hold [SHIFT] shows the material, gem, repairs and recharges.
- The "+" after a spell's name is now a green "stacks" box: that spell's effect adds up with the same spell on other jewelry you wear.
- New lore for Silbro's Ring of Vitality, and Strongman's Bracers has lore (it's Silbro's work too).
- Russian: "silver" was misspelled in many jewelry names, and Medic's Token and Peasant's Fortune were still in English; both fixed.
- The Journeyman's Bands are now silver with a white pearl, as Selene made them in the lore books (they were gold with a jadeite). Bands from 1.x convert to the new look.
- Fixed Strength (Quick, Greater and Giant Strength) showing its cooldown where its tooltip says the effect's duration.
- With Obscure Tooltips installed, Magic Treasures tooltips leave the icon header to it, so the name isn't shown twice. Legendary Tooltips needs no change.
- Healing (Lesser Healing to Regeneration), Drain (Drain, Greater Drain, Desiccate) and Satiety now act as soon as they're needed, then wait their "every N s" time before acting again. Before, they only acted on a fixed beat, so after you got hurt or hungry the first heal or bite could take up to the full wait. Their wait now shows on the jewelry HUD like other cooldowns.
- **Old jewelry converts to 2.0 jewelry** when you carry it (inventory and Curios slots, checked once a second) or open the chest it's in. A chest nobody opens keeps its old jewelry, which still looks and reads the same until then. The old item ids stay registered, so no world loses jewelry.
- **Datapack and modpack authors:** recipes, loot tables and tags that name the old jewelry items (`magictreasures:gold_ruby_ring`, `magictreasures:silver_castle_ring` …) no longer work; the jewelry tags `jewelry/tiers/...` and `jewelry/rarity/...` are gone, and the gem tier tags (`jewelry/stones/tier/...`) no longer make an item a gem or set its tier. Use the jewelry JSON instead (a gem is a `gemstone` entry), and `magictreasures:ring` with the loot functions above in loot tables.
- Fully Adorned now asks for a whole named set worn at once. Wearing a ring, necklace and bracelet (plus a belt) is the new Dressed to the Nines.
- Spellbound is now the start of the magic branch rather than following Set in Stone.
- New art for the Ruby, Sapphire, Topaz, Jadeite and Onyx items.
- Fixed mobs only ever dropping Topaz: the `gemstones` list in mob loot tables was ignored. Mobs now drop the gems their tables name.
- Fixed a gem set at the anvil on jewelry that had no ready-made version (a third-party or Treasure2 gem, or a swapped gem) giving only part of the gem's bonuses.
- Removed the Frequency spell modifier: those three spells use Cooldown now. Materials and gems always had the same Frequency and Cooldown values, so no numbers change. Silbro's Grove's "25% more often" bonus now lowers their cooldown.
- The set advancements and Collector of Legends are harder now that the sets are bigger: each asks for every piece of its set, weapons and armor included.
- Mana Shield and Mana Tower Shield never block more than all of a hit, however many bonuses stack on them. Before, stacked bonuses could push the block past 100%, which passed negative damage on and overfilled the jewelry's mana. The Spectral Armor spells (Ghostly, Spectral, Shadow and Disembodied Armor) now block at most 80%, vanilla armor's ceiling. Tooltips show the capped numbers.
- The three kinds of ward spell now look different when they work: the Spectral Armor spells puff smoke, mana shields ring you in blue, and Reflection draws a gold line to the attacker with crit sparks.
- Russian: Journeyman's Bands has a Russian name, and the Maldritch's Remains bonus uses the Drain spell's name.

### Fixed
- The tooltip chips of the three resistance spells now say what they resist ("100% fire resist", "magic resist", "wither resist") instead of just "resist".

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

### Fixed
- Fire Ward and Blessing of the Phoenix now block every kind of fire damage vanilla's Fire Resistance does: lava, magma blocks and fireballs too, not only fire and burning.

## [1.3.2] - 2026-10-09

First release for NeoForge 1.21.1. Version numbers match the Forge 1.20.1 line from here on: NeoForge 1.3.2 has the same features as Forge 1.3.2, including all of 1.3.1 and 1.3.2's fixes and the Spanish translation.

### Changed
- Ported to Minecraft 1.21.1 / NeoForge 21.1.117+.
- Requires GottschCore for NeoForge 1.21.1, version 2.7.0 or later.
- Optional integrations now target Treasure2 5.0.0+ and Curios 9.x for NeoForge 1.21.1.
- Patchouli (for the Magic Treasures Guide book) is now listed as an optional dependency, so it shows in the mod list.
- Jewelry data (mana, uses, repairs, recharges, stone, spells and cooldowns) is now stored as an item data component (`magictreasures:jewelry`) instead of a Forge capability.
  NOTE: jewelry from a 1.20.1 world does not keep its Magic Treasures data. It resets to that item's default stats and spells, so spells added with scrolls are lost.
- Jewelry now syncs to the client through Curios and vanilla item syncing. The custom spell update packet was removed.
- Item tags moved from the `forge` namespace to the common `c` namespace (ex. `c:ingots/silver`, `c:ores/silver`, `c:gems/...`, `c:raw_materials/silver`). Datapacks and modpack recipes that use the `forge:` tags need updating.
- Global loot modifiers moved to `data/neoforge/loot_modifiers/` and biome modifiers to `data/magictreasures/neoforge/biome_modifier/`. Loot tables moved to `loot_table/` (1.21 folder names).
- Looting chance conditions in entity loot tables now use `minecraft:random_chance_with_enchanted_bonus` (1.21 replacement for `random_chance_with_looting`).
- Raw Silver blasting recipe (`magictreasures:silver_ingot_from_blasting`) is now generated by datagen.
- Fixed the Random Gemstone loot function reporting itself as the Random Spell function.
- Fixed gem ores (Topaz, Onyx, Jadeite, Ruby, Sapphire, in Stone and Deepslate) dropping the ore block instead of the gem. They now drop their gem, with Fortune giving more, like vanilla diamond ore. Silk Touch still drops the ore block.
- Fixed Uncommon, Scarce, Rare, Legendary and Mythical chest loot never appearing. Their loot table ids were missing the `chests/` folder (ex. `minecraft:simple_dungeon` instead of `minecraft:chests/simple_dungeon`), the same typo fixed for Epic chests in Forge 1.3.1, so only Epic chests ever had Magic Treasures loot. Also fixed three misspelled ids: Shepherd and Tannery village chests, and big Underwater Ruins (Epic).

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
