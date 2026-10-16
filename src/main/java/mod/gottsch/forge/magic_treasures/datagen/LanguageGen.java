/*
 * This file is part of  Magic Treasures.
 * Copyright (c) 2023 Mark Gottschling (gottsch)
 *
 * Magic Treasures is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Magic Treasures is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Magic Treasures.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */
package mod.gottsch.forge.magic_treasures.datagen;

import mod.gottsch.forge.magic_treasures.core.entity.MagicTreasuresEntities;
import java.util.List;
import java.util.Map;
import mod.gottsch.forge.magic_treasures.MagicTreasures;
import mod.gottsch.forge.magic_treasures.core.block.MagicTreasuresBlocks;
import mod.gottsch.forge.magic_treasures.core.item.GenericJewelry;
import mod.gottsch.forge.magic_treasures.core.item.MagicTreasuresItems;
import mod.gottsch.forge.magic_treasures.core.item.SpellScroll;
import mod.gottsch.forge.magic_treasures.core.setup.Registration;
import mod.gottsch.forge.magic_treasures.core.util.LangUtil;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;
import org.apache.commons.lang3.text.WordUtils;

/**
 * 
 * @author Mark Gottschling Jun 2, 2023
 *
 */
@SuppressWarnings("deprecation")
public class LanguageGen extends LanguageProvider {

    public LanguageGen(PackOutput output, String locale) {
        super(output, MagicTreasures.MOD_ID, locale);
    }
    
    @Override
    protected void addTranslations() {
    	// tabs
        add("itemGroup." + MagicTreasures.MOD_ID, "Magic Treasures");
        add("itemGroup." + MagicTreasures.MOD_ID + ".jewelry_tab", "Magic Treasures Jewelry");

        // metals
        add(MagicTreasuresItems.SILVER_INGOT.get(), "Silver Ingot");
        add(MagicTreasuresItems.RAW_SILVER.get(), "Raw Silver");
        // stones
        add(MagicTreasuresItems.TOPAZ.get(), "Topaz");
        add(MagicTreasuresItems.ONYX.get(), "Onyx");
        add(MagicTreasuresItems.JADEITE.get(), "Jadeite");
        add(MagicTreasuresItems.RUBY.get(), "Ruby");
        add(MagicTreasuresItems.SAPPHIRE.get(), "Sapphire");
        add(MagicTreasuresItems.WHITE_PEARL.get(), "White Pearl");
        add(MagicTreasuresItems.BLACK_PEARL.get(), "Black Pearl");
//        add(MagicTreasuresItems.SKELETONS_HEART.get(), "Skeleton's Heart");

        // the 418 1.x item ids (M9: legacy items, converted when carried). Their names are also what 2.0 generic
        // jewelry and the named presets use (GenericJewelry.name; preset "name": item.magictreasures.<id>)
        Map<String, String> namedPieces = Map.ofEntries(
                Map.entry("silbros_ring_of_vitality", WordUtils.capitalizeFully("Silbro's Ring of Vitality")),
                Map.entry("strongmans_bracers", WordUtils.capitalizeFully("Strongman's Bracers")),
                Map.entry("silbros_acorn", "Silbro's Acorn"),
                Map.entry("maldritchs_first_amulet", WordUtils.capitalizeFully("Maldritch's First Amulet")),
                Map.entry("peasants_fortune", WordUtils.capitalizeFully("Peasant's Fortune")),
                Map.entry("aqua_ring", WordUtils.capitalizeFully("Aqua Ring")),
                Map.entry("amulet_of_defence", WordUtils.capitalizeFully("Amulet of Defence")),
                Map.entry("journeyman_bands", WordUtils.capitalizeFully("Journeyman's Bands")),
                Map.entry("medics_token", WordUtils.capitalizeFully("Medic's Token")),
                Map.entry("adephagias_bounty", WordUtils.capitalizeFully("Adephagia's Bounty")),
                Map.entry("angels_ring", WordUtils.capitalizeFully("Ring of Angels")),
                Map.entry("salandaars_ward", WordUtils.capitalizeFully("Sal'andaar's Ward")),
                Map.entry("ring_of_fortitude", WordUtils.capitalizeFully("Ring of Fortitude")),
                Map.entry("eye_of_the_phoenix", WordUtils.capitalizeFully("Eye of the Phoenix")),
                Map.entry("ring_of_life_death", WordUtils.capitalizeFully("Ring of Life and Death")));
        MagicTreasuresItems.LEGACY_JEWELRY.forEach((id, item) -> add(item.get(),
                namedPieces.getOrDefault(id.getPath(), WordUtils.capitalizeFully(id.getPath().replace("_", " ")))));
        add(LangUtil.tooltip("legacy_jewelry"), "Magic Treasures 1.x jewelry: becomes 2.0 jewelry when carried");

        // other special items
        add(MagicTreasuresItems.ROOTSTAFF.get(), "Rootstaff");
        add(MagicTreasuresItems.BARKSKIN_VEST.get(), "Barkskin Vest");
        add(MagicTreasuresItems.STONEMARK_HAMMER.get(), "Stonemark Hammer");
        add(MagicTreasuresItems.BONE_SCYTHE.get(), "Bone Scythe");
        add(MagicTreasuresItems.LICHS_MASK.get(), "Lich's Mask");
        add(MagicTreasuresItems.MOONGLAIVE.get(), "Moonglaive");
        add(MagicTreasuresItems.LORE_BOOK.get(), "Lore Book");
        add(LangUtil.tooltip("lore_book.author"), "by %s");
        // lore books: title, author and pages per story (see LoreBookDef)
        add("lore_book.magictreasures.silbros_journal_1.title", "Silbro's Journal, I");
        add("lore_book.magictreasures.silbros_journal_1.author", "Silbro");
        add("lore_book.magictreasures.silbros_journal_1.page.1", "Spring.\n\nThe smiths in the valley laughed when I told them wood could hold magic. They are right, in their way. Dead wood cannot. Cut a branch and the magic runs out of it like sap.");
        add("lore_book.magictreasures.silbros_journal_1.page.2", "So I did not cut the willow by the mill. I sat with it instead, and asked.\n\nA tree answers slowly. It took the whole of that first summer before I felt it lean toward my hand.");
        add("lore_book.magictreasures.silbros_journal_1.page.3", "Second summer.\n\nA root has curled around my finger while I slept. I have not moved it. It is warm, which roots are not meant to be, and when I set a little healing into it, the healing stayed.");
        add("lore_book.magictreasures.silbros_journal_1.page.4", "Third summer.\n\nThe ring is grown. It holds mana as well as any silver ring I have seen at the forge, and when I cracked it on a stone it mended itself, once.\n\nI do not think it will do that twice.");
        add("lore_book.magictreasures.silbros_journal_1.page.5", "I showed it to Sal'andaar. He turned it over for a long time and said only, \"Show me how.\"\n\nI told him the truth: you cannot make it. You can only wait to be given it.\n\nHe did not like that answer.");
        add("lore_book.magictreasures.silbros_journal_1.page.6", "It is a little pleased with itself, I think.\n\nSo am I.\n\n- S.");
        add("lore_book.magictreasures.smiths_ledger.title", "The Runesmith's Ledger");
        add("lore_book.magictreasures.smiths_ledger.author", "Sal'andaar");
        add("lore_book.magictreasures.smiths_ledger.page.1", "Ledger of S. Stone, runesmith.\n\nThree horseshoes, a mended plough. 4 copper.\n\nA ward set in a farmer's ring, against fevers. 1 silver. He paid in eggs. Wrote it down as a silver anyway.");
        add("lore_book.magictreasures.smiths_ledger.page.2", "Took on two apprentices.\n\nBren: strong, slow, honest.\n\nMaldritch: best hands I've had. Learns a weld in a day that took me a year.\n\nAsks too much about the dead.");
        add("lore_book.magictreasures.smiths_ledger.page.3", "Silbro sent another ring that grew itself. Sent him back a horseshoe that didn't.\n\nHe'll know what I meant.");
        add("lore_book.magictreasures.smiths_ledger.page.4", "The coat is done. Took the winter. Spells slide off it like rain off oilcloth.\n\nTested it on myself. Then on Maldritch, at his asking. He laughed the whole time.\n\nShould not have let him.");
        add("lore_book.magictreasures.smiths_ledger.page.5", "Maldritch gone three days. Came back with a bone charm that holds mana.\n\nBone doesn't hold mana. I know what does, and so does he.\n\nWe did not speak of it.");
        add("lore_book.magictreasures.smiths_ledger.page.6", "Bren has left. Says he won't work beside \"that one\".\n\nOrders are down. Folk in the market cross the street.\n\nThe ward for the reeve's door: no charge.");
        add("lore_book.magictreasures.maldritchs_notes_1.title", "Maldritch's Notes, I");
        add("lore_book.magictreasures.maldritchs_notes_1.author", "Maldritch");
        add("lore_book.magictreasures.maldritchs_notes_1.page.1", "Master Stone says magic is set, not made. A hammer closes it into metal like a weld.\n\nI have watched him do it a hundred times and I still cannot see the moment it happens.\n\nI will.");
        add("lore_book.magictreasures.maldritchs_notes_1.page.2", "A summer with the druid. Silbro talks to willows and they listen. He let me hold a ring he grew. It was warm.\n\nIf a living thing can hold magic, it is already full of it. Why wait to be given?");
        add("lore_book.magictreasures.maldritchs_notes_1.page.3", "Mother is sick. The healers' charms do nothing.\n\nSilbro came himself and sat with her three days. He was kind. He was patient.\n\nShe died on the fourth.");
        add("lore_book.magictreasures.maldritchs_notes_1.page.4", "He said some things cannot be mended, only let go.\n\nI have decided he is wrong.\n\nThe gentle way is too slow.");
        add("lore_book.magictreasures.maldritchs_notes_1.page.5", "A rabbit. Bone holds nothing; everyone knows that. But I closed a ward around the life as it left, the way Master Stone closes a weld, and the bone kept it.\n\nIt was warm, like the willow ring.");
        add("lore_book.magictreasures.maldritchs_notes_1.page.6", "I have told no one. Master Stone would not understand. Silbro would understand too well.\n\nI am not doing anything wrong. I am only learning faster.");
        add("lore_book.magictreasures.selenes_account.title", "Selene's Account");
        add("lore_book.magictreasures.selenes_account.author", "Selene Tidewalker");
        add("lore_book.magictreasures.selenes_account.page.1", "They will tell you there were three: the runesmith, the druid, and the one we don't name in harbours.\n\nThere were four. I sold the other three their pearls, and I am the one still here to write it down.");
        add("lore_book.magictreasures.selenes_account.page.2", "Sal'andaar thought magic was a thing you hammered shut. Silbro thought it was a thing you were given.\n\nThey argued for forty years and were both right, which neither of them could stand.");
        add("lore_book.magictreasures.selenes_account.page.3", "Pearls are grown, you know. An oyster takes a grain of grit, a wound, and wraps it in light, year after year.\n\nSilbro understood at once. Sal'andaar asked what the oyster charged.");
        add("lore_book.magictreasures.selenes_account.page.4", "I made bands for my couriers, so they could run the coast roads by night and see the ruts before they fell in them.\n\nHalf the letters between the runesmith and the druid went by my runners. I read none of them. Mostly.");
        add("lore_book.magictreasures.selenes_account.page.5", "And the boy. Maldritch. Clever hands, frightened eyes. He asked me once how long a pearl lives.\n\nI told him: longer than the oyster.\n\nI have wondered since if that was the worst thing I ever said.");
        add("lore_book.magictreasures.selenes_account.page.6", "I have kept their papers, what I could find. Read them as you find them.\n\nYou will not find them all. I have seen to that.\n\n- S. T.");
        add("lore_book.magictreasures.silbros_journal_2.title", "Silbro's Journal, II");
        add("lore_book.magictreasures.silbros_journal_2.author", "Silbro");
        add("lore_book.magictreasures.silbros_journal_2.page.1", "Autumn.\n\nHale the woodcutter came up the hill with his arm in a sling. Thirty years of the axe, and the elbow gave out before he did.\n\nI did not mend the arm. I asked the oak he cuts to help him.");
        add("lore_book.magictreasures.silbros_journal_2.page.2", "The bracers took the winter: oak bark, still on the tree when I began, grown around a band of willow.\n\nHe swings as he did at twenty. He says he can hear the oak in them when he works. I expect he can.");
        add("lore_book.magictreasures.silbros_journal_2.page.3", "Spring.\n\nThe village healer asked for something she could carry. I gave her a token cut from a living elm, no bigger than a thumbnail.\n\nIt does not heal. It only reminds the hurt that they can.");
        add("lore_book.magictreasures.silbros_journal_2.page.4", "Summer.\n\nSal'andaar has sent me his best apprentice, \"to learn patience.\" His name is Maldritch. Quick hands, quick eyes, and a sadness he wears like a cloak in August.");
        add("lore_book.magictreasures.silbros_journal_2.page.5", "He listens to the willows better than I did at his age. I let him hold the ring. He held it a long while.\n\nThen he asked me how it could be emptied. Not filled. Emptied.");
        add("lore_book.magictreasures.silbros_journal_2.page.6", "I told him a ring is not a cup. What it holds was given, and given things are not taken back.\n\nHe nodded as if he understood. I do not think he believed me. He goes home tomorrow.\n\n- S.");
        add("lore_book.magictreasures.letters_across_the_valley_1.title", "Letters Across the Valley, I");
        add("lore_book.magictreasures.letters_across_the_valley_1.author", "Sal'andaar and Silbro");
        add("lore_book.magictreasures.letters_across_the_valley_1.page.1", "Friend,\n\nThe willow you laughed at has given me a second ring. The pearl woman's runner brings it, so you may laugh at it in person.\n\nKeep it from the forge. It dislikes fire.\n\n- Silbro");
        add("lore_book.magictreasures.letters_across_the_valley_1.page.2", "Silbro,\n\nRing arrived. Laughed. Put it on the anvil and hit it. Hammer bounced.\n\nWhat did you do to it?\n\n- S. Stone");
        add("lore_book.magictreasures.letters_across_the_valley_1.page.3", "Nothing. That is the whole of it. I waited, and the tree did the rest.\n\nYou hit things until they agree with you. I wait until they agree with me. We both get rings.\n\n- Silbro");
        add("lore_book.magictreasures.letters_across_the_valley_1.page.4", "Silbro,\n\nWaiting is not a craft.\n\nSent you a ward for your door. Keeps out fever, thieves and wolves. Does not keep out squirrels. Did not try.\n\n- S. Stone");
        add("lore_book.magictreasures.letters_across_the_valley_1.page.5", "The squirrels thank you. They are living in it.\n\nIt is a fine ward, my friend, the finest closed thing I have ever seen. I only wish you would leave something open now and then.\n\n- Silbro");
        add("lore_book.magictreasures.letters_across_the_valley_1.page.6", "Silbro,\n\nOpen things leak.\n\nCome down for the midsummer fair. Bring the ring. I want to hit it again.\n\n- S.");
        add("lore_book.magictreasures.market_warning.title", "A Warning, Posted in the Market");
        add("lore_book.magictreasures.market_warning.author", "The Reeve");
        add("lore_book.magictreasures.market_warning.page.1", "BY ORDER OF THE REEVE\n\nThe mill village on the east road stands empty. No sickness was found there, and no raiders seen. Its folk lie in their beds as if asleep, and they are cold.");
        add("lore_book.magictreasures.market_warning.page.2", "Buy no charms of bone. Trade with no pale young smith who sells them, nor give him lodging. If he is seen, send word to the reeve, and to Master Stone at the forge.\n\nDo not hold what he offers. It is warm.");
        add("lore_book.magictreasures.letters_across_the_valley_2.title", "Letters Across the Valley, II");
        add("lore_book.magictreasures.letters_across_the_valley_2.author", "Sal'andaar and Silbro");
        add("lore_book.magictreasures.letters_across_the_valley_2.page.1", "Friend,\n\nThe boy's mother is buried. I sat with her three days and could not keep her. He thanked me at the grave, politely, and did not look at me once.\n\nWatch him.\n\n- Silbro");
        add("lore_book.magictreasures.letters_across_the_valley_2.page.2", "Silbro,\n\nHe's back at the anvil. Works harder than before. Best hands I've had.\n\nGrief is grief. Leave him be.\n\n- S. Stone");
        add("lore_book.magictreasures.letters_across_the_valley_2.page.3", "A fox came to my door, alive and hollow as a scraped gourd. Something drew it empty and let it walk away.\n\nI have only met one person who asked me how a thing could be emptied.\n\n- Silbro");
        add("lore_book.magictreasures.letters_across_the_valley_2.page.4", "Silbro,\n\nFoxes get sick.\n\nThe boy has no one but this forge. I won't turn him out on a druid's feeling.\n\n- S. Stone");
        add("lore_book.magictreasures.letters_across_the_valley_2.page.5", "They say in the market that the mill village on the east road stands empty. They say you made the reeve a ward for his door, and would not take his coin.\n\nWrite to me, Stone.\n\n- Silbro");
        add("lore_book.magictreasures.letters_across_the_valley_2.page.6", "Friend,\n\nA year, and no letter. The runner says the forge is lit, so I know you are alive, and that is all I know.\n\nI will not ask what you knew. Only write.\n\n- Silbro");
        add("lore_book.magictreasures.maldritchs_notes_2.title", "Maldritch's Notes, II");
        add("lore_book.magictreasures.maldritchs_notes_2.author", "Maldritch");
        add("lore_book.magictreasures.maldritchs_notes_2.page.1", "The rabbit bone held nine days, then went cold. A ward closed at the moment of death keeps what is left, and what is left is very little.\n\nTake it before the end, not at it.");
        add("lore_book.magictreasures.maldritchs_notes_2.page.2", "A goat, sedated. I drew from it while it lived, into a ward of thigh bone. The bone held four times what the rabbit gave, and the goat lived a day after.\n\nLiving things refill. Silbro was right about that much.");
        add("lore_book.magictreasures.maldritchs_notes_2.page.3", "Bone is the right vessel. It was alive once; it remembers being full, and it wants to be again. Silver only holds. Bone pulls.\n\nMaster Stone would call that a leak. It is a well.");
        add("lore_book.magictreasures.maldritchs_notes_2.page.4", "The belt is finished: small skulls on a band of iron, each one a ward. Worn, it feeds a ring as a spring feeds a pond.\n\nI wore it to the forge. Master Stone did not look at it. Bren did.");
        add("lore_book.magictreasures.maldritchs_notes_2.page.5", "Animals are slow. Each holds a little and takes a season to fill again, and the woods near the forge are going quiet.\n\nI need a vessel that holds more, and lives in numbers.");
        add("lore_book.magictreasures.maldritchs_notes_2.page.6", "The mill on the east road has forty souls.\n\nI have been counting.");
        add("lore_book.magictreasures.silbros_journal_3.title", "Silbro's Journal, III");
        add("lore_book.magictreasures.silbros_journal_3.author", "Silbro");
        add("lore_book.magictreasures.silbros_journal_3.page.1", "Autumn, and I am old.\n\nStone has written at last. Three lines. The boy is gone from the forge; he does not say where, and I do not ask. I have had his silence long enough to read it.");
        add("lore_book.magictreasures.silbros_journal_3.page.2", "The oldest wood I know is two days north. It has been calling me since spring, the way the willow did, only slower, and much deeper.\n\nI think it is my turn to be given to something.");
        add("lore_book.magictreasures.silbros_journal_3.page.3", "I leave the willow's ring on the sill, for whoever needs it. Not for whoever wants it. It will know the difference; it always has.\n\nTell Stone I was right about waiting. Tell him kindly.");
        add("lore_book.magictreasures.silbros_journal_3.page.4", "[The last lines are not written. They are pressed into the page, like the grain of wood.]\n\nroots are slow\n\nthey are not lonely\n\ngo gently");
        add(MagicTreasuresItems.PEARL_CIRCLET.get(), "Pearl Circlet");
        add(MagicTreasuresItems.SMITHS_HELM.get(), "Runesmith's Helm");
        add(MagicTreasuresItems.ANVIL_SHIELD.get(), "Anvil Shield");
        // a 2.0-only preset (no 1.x item id, so not in the legacy names above)
        add("item.magictreasures.smiths_cuff", "Runesmith's Cuff");



         // GOTTSCHS_AMULET_OF_HEAVENS
        // GOTTSCHS_RING_OF_MOON
        // SHADOWS_GIFT

        // belts
        add(MagicTreasuresItems.SKULL_BELT.get(), WordUtils.capitalizeFully("Skull Belt"));
        add(MagicTreasuresItems.SPECTRAL_SWORD.get(), "Spectral Sword");
        add(MagicTreasuresEntities.ORBITING_SWORD.get(), "Spectral Sword");

        // scrolls
        Registration.ITEMS.getEntries().forEach(o -> {
            if (o.get() instanceof SpellScroll) {
                add(o.get(), WordUtils.capitalizeFully("Scroll of " + ((SpellScroll) o.get()).getSpell().getName().getPath().replace("_", " ")));
            }
        });

        // recipes scrolls
        add(MagicTreasuresItems.RING_RECIPE.get(), "Ring Recipe");
        add(MagicTreasuresItems.NECKLACE_RECIPE.get(), "Necklace Recipe");
        add(MagicTreasuresItems.BRACELET_RECIPE.get(), "Bracelet Recipe");

        // 2.0 generic jewelry: the bare item names, then the parts a combination's name is composed from. A combination
        // that was a 1.x item uses that item's name key instead (GenericJewelry.name)
        add(MagicTreasuresItems.RING.get(), "Ring");
        add(MagicTreasuresItems.NECKLACE.get(), "Necklace");
        add(MagicTreasuresItems.BRACELET.get(), "Bracelet");
        // arguments: (style, material, gem, type)
        add(GenericJewelry.NAME_KEY, "%2$s %4$s");
        add(GenericJewelry.NAME_KEY + ".gem", "%2$s %3$s %4$s");
        add(GenericJewelry.NAME_KEY + ".style", "%1$s %2$s %4$s");
        add(GenericJewelry.NAME_KEY + ".style.gem", "%1$s %2$s %3$s %4$s");
        add("jewelry_style.magictreasures.carved", "Carved");
        add("jewelry_style.magictreasures.fine", "Fine");
        add("jewelry_style.magictreasures.great", "Great");
        add("jewelry_style.magictreasures.lords", "Lords");
        add("jewelry_style.magictreasures.wreathed", "Wreathed");
        for (String material : List.of("wood", "iron", "copper", "silver", "gold", "bone", "blood", "shadow", "atium")) {
            add("material.magictreasures." + material, WordUtils.capitalizeFully(material));
        }
        add(LangUtil.tooltip("jewelry.unknown"), "Unknown material or style: %s (is a datapack missing?)");
        add(LangUtil.tooltip("jewelry.unknown_gem"), "Unknown gem %s: it has no effect");

        // recharge scroll
        add(MagicTreasuresItems.RECHARGE_SCROLL.get(), "Recharge Scroll");

        // ore
        add(MagicTreasuresBlocks.TOPAZ_ORE.get(), "Topaz Ore");
        add(MagicTreasuresBlocks.ONYX_ORE.get(), "Onyx Ore");
        add(MagicTreasuresBlocks.JADEITE_ORE.get(), "Jadeite Ore");
        add(MagicTreasuresBlocks.RUBY_ORE.get(), "Ruby Ore");
        add(MagicTreasuresBlocks.SAPPHIRE_ORE.get(), "Sapphire Ore");
        add(MagicTreasuresBlocks.SILVER_ORE.get(), "Silver Ore");

        add(MagicTreasuresBlocks.DEEPSLATE_TOPAZ_ORE.get(), "Deepslate Topaz Ore");
        add(MagicTreasuresBlocks.DEEPSLATE_ONYX_ORE.get(), "Deepslate Onyx Ore");
        add(MagicTreasuresBlocks.DEEPSLATE_JADEITE_ORE.get(), "Deepslate Jadeite Ore");
        add(MagicTreasuresBlocks.DEEPSLATE_RUBY_ORE.get(), "Deepslate Ruby Ore");
        add(MagicTreasuresBlocks.DEEPSLATE_SAPPHIRE_ORE.get(), "Deepslate Sapphire Ore");
        add(MagicTreasuresBlocks.DEEPSLATE_SILVER_ORE.get(), "Deepslate Silver Ore");

        
        /*
         *  Util.tooltips
         */
        // general
        add(LangUtil.tooltip("boolean.yes"), "Yes");
        add(LangUtil.tooltip("boolean.no"), "No");
        add(LangUtil.tooltip("infinite"), "Infinite");
        add(LangUtil.tooltip("hold_shift"), "Hold [SHIFT] to expand");
        add(LangUtil.tooltip("divider"), "--------------------");

        // tools
        add(MagicTreasuresItems.JEWELRY_PLIERS.get(), "Jewelry Pliers");
        add(LangUtil.tooltip("mana_well.usage"), "Wear or hold it. Spells draw from it when their jewelry runs out of mana.");

        // advancements
        add("advancements.magictreasures.root.title", "Magic Treasures");
        add("advancements.magictreasures.root.description", "Find a piece of magic jewelry");
        add("advancements.magictreasures.set_in_stone.title", "Set in Stone");
        add("advancements.magictreasures.set_in_stone.description", "Set a gem into jewelry at an anvil");
        add("advancements.magictreasures.spellbound.title", "Spellbound");
        add("advancements.magictreasures.spellbound.description", "Imbue jewelry with a spell scroll at an anvil");
        add("advancements.magictreasures.second_wind.title", "Second Wind");
        add("advancements.magictreasures.second_wind.description", "Recharge jewelry at an anvil");
        add("advancements.magictreasures.delicate_work.title", "Delicate Work");
        add("advancements.magictreasures.delicate_work.description", "Pull a gem out of jewelry with Jewelry Pliers");
        add("advancements.magictreasures.second_thoughts.title", "Second Thoughts");
        add("advancements.magictreasures.second_thoughts.description", "Remove a gem from jewelry with shears on an anvil (the gem is lost)");
        add("advancements.magictreasures.apprentice_jeweler.title", "Apprentice Jeweler");
        add("advancements.magictreasures.apprentice_jeweler.description", "Craft jewelry from a recipe scroll");
        add("advancements.magictreasures.good_as_new.title", "Good as New");
        add("advancements.magictreasures.good_as_new.description", "Repair jewelry at an anvil with another piece of jewelry");
        add("advancements.magictreasures.silver_lining.title", "Silver Lining");
        add("advancements.magictreasures.silver_lining.description", "Obtain a silver ingot");
        add("advancements.magictreasures.journeyman_enchanter.title", "Journeyman Enchanter");
        add("advancements.magictreasures.journeyman_enchanter.description", "Imbue jewelry with a spell of level 3 or higher");
        add("advancements.magictreasures.adept_enchanter.title", "Adept Enchanter");
        add("advancements.magictreasures.adept_enchanter.description", "Imbue jewelry with a spell of level 5 or higher");
        add("advancements.magictreasures.master_enchanter.title", "Master Enchanter");
        add("advancements.magictreasures.master_enchanter.description", "Imbue jewelry with a spell of level 7 or higher");
        add("advancements.magictreasures.archmage.title", "Archmage");
        add("advancements.magictreasures.archmage.description", "Imbue jewelry with a spell of level 9 or higher");
        add("advancements.magictreasures.running_on_empty.title", "Running on Empty");
        add("advancements.magictreasures.running_on_empty.description", "Cast a spell that draws on a mana well when its jewelry runs dry");
        add("advancements.magictreasures.gem_collector.title", "Gem Collector");
        add("advancements.magictreasures.gem_collector.description", "Hold every kind of Magic Treasures gem");
        add("advancements.magictreasures.lords_ransom.title", "Lord's Ransom");
        add("advancements.magictreasures.lords_ransom.description", "Carry a piece of Lord's jewelry");
        add("advancements.magictreasures.treasure_hunter.title", "Treasure Hunter");
        add("advancements.magictreasures.treasure_hunter.description", "Carry a piece of legendary or mythical jewelry");
        add("advancements.magictreasures.dressed_to_the_nines.title", "Dressed to the Nines");
        add("advancements.magictreasures.dressed_to_the_nines.description", "Wear a magic ring, necklace and bracelet and a belt at once");
        add("advancements.magictreasures.fully_adorned.title", "Fully Adorned");
        add("advancements.magictreasures.fully_adorned.description", "Wear every piece of a named set at once");
        add("advancements.magictreasures.silbros_grove.title", "Silbro's Grove");
        add("advancements.magictreasures.silbros_grove.description", "Wear every piece of Silbro's Grove at once");
        add("advancements.magictreasures.salandaars_anvilwork.title", "Sal'andaar's Anvilwork");
        add("advancements.magictreasures.salandaars_anvilwork.description", "Wear every piece of Sal'andaar's Anvilwork at once");
        add("advancements.magictreasures.maldritchs_remains.title", "Maldritch's Remains");
        add("advancements.magictreasures.maldritchs_remains.description", "Wear every piece of Maldritch's Remains at once");
        add("advancements.magictreasures.selenes_tides.title", "Selene's Tides");
        add("advancements.magictreasures.selenes_tides.description", "Wear every piece of Selene's Tides at once");
        add("advancements.magictreasures.first_pages.title", "First Pages");
        add("advancements.magictreasures.first_pages.description", "Read a lore book");
        add("advancements.magictreasures.archivist.title", "Archivist");
        add("advancements.magictreasures.archivist.description", "Read every lore book");
        add("advancements.magictreasures.silbros_seasons.title", "Silbro's Seasons");
        add("advancements.magictreasures.silbros_seasons.description", "Read all of Silbro's journals");
        add("advancements.magictreasures.the_smiths_hand.title", "The Runesmith's Hand");
        add("advancements.magictreasures.the_smiths_hand.description", "Read everything Sal'andaar wrote");
        add("advancements.magictreasures.maldritchs_descent.title", "Maldritch's Descent");
        add("advancements.magictreasures.maldritchs_descent.description", "Read all of Maldritch's notes");
        add("advancements.magictreasures.the_tidewalkers_tale.title", "The Tidewalker's Tale");
        add("advancements.magictreasures.the_tidewalkers_tale.description", "Read everything Selene wrote");
        add("advancements.magictreasures.collector_of_legends.title", "Collector of Legends");
        add("advancements.magictreasures.collector_of_legends.description", "Wear each of the four named sets whole");
        add(LangUtil.tooltip("tools.jewelry_pliers"), "Craft with jewelry to remove its gem. The jewelry is destroyed.");

        // gemstones
        add(LangUtil.tooltip("gemstone.usage"), "Place on an anvil with Magic Treasures jewelry to combine.");
        add(LangUtil.tooltip("gemstone.recharge.usage"), "Place on an anvil with Magic Treasures jewelry to recharge.");
        add(LangUtil.tooltip("gemstone.rarity"), "Rarity: %s");
        add(LangUtil.tooltip("gemstone.tier"), "Tier: %s");
        add(LangUtil.tooltip("gemstone.mana"), "Mana: %s");
        add(LangUtil.tooltip("gemstone.recharges"), "Recharges: %s");
        add(LangUtil.tooltip("gemstone.cost_factor"), "Mana Cost: %s");
        add(LangUtil.tooltip("gemstone.cooldown_factor"), "Cooldown Time: %s");
        add(LangUtil.tooltip("gemstone.effect_amount_factor"), "Effect Amount: %s");
        add(LangUtil.tooltip("gemstone.range_factor"), "Range: %s");
        add(LangUtil.tooltip("gemstone.duration_factor"), "Duration: %s");
        add(LangUtil.tooltip("gemstone.affixes_to"), "Only fits: %s");

        // gemstone tier names
//        add(LangUtil.tooltip("gemstone.tier.tier1.name"), "Tier 1");
//        add(LangUtil.tooltip("gemstone.tier.skeletons_heart.name"), "Skeleton's Heart");

        // jewelry
        add(LangUtil.tooltip("jewelry.usage"), "Hold in hand or equip for buffs.");
        add(LangUtil.tooltip("jewelry.material"), "Material: %s");
        add(LangUtil.tooltip("jewelry.stone"), "Stone: %s");

//        add(LangUtil.tooltip("jewelry.durability"), "Durability: ");
//        add(LangUtil.tooltip("jewelry.durability.gauge"), "[%s/%s]");
        add(LangUtil.tooltip("jewelry.durability.amount"), "Durability: [%s/%s]");
        add(LangUtil.tooltip("jewelry.durability.infinite"), "Durability: -%s-");
        add(LangUtil.tooltip("jewelry.durability.repairs"), "Repairs: %s");

        add(LangUtil.tooltip("jewelry.max_level"), "Spell Max. Level: %s");
        add(LangUtil.tooltip("jewelry.mana"), "Mana: [%s/%s]");
        add(LangUtil.tooltip("jewelry.mana.recharges"), "Recharges: %s");
        add(LangUtil.tooltip("jewelry.mana.gauge"), "[%s/%s]");
        add(LangUtil.tooltip("jewelry.spells"), "Spells:");

        // 2.0.0 tooltip redesign
        add(LangUtil.tooltip("jewelry.bar.mana"), "Mana");
        add(LangUtil.tooltip("jewelry.bar.durability"), "Durability");
        add(LangUtil.tooltip("jewelry.level"), "Lvl %s");
        add(LangUtil.tooltip("jewelry.type.ring"), "Ring");
        add(LangUtil.tooltip("jewelry.type.necklace"), "Necklace");
        add(LangUtil.tooltip("jewelry.type.bracelet"), "Bracelet");
        add(LangUtil.tooltip("jewelry.type.pocket"), "Pocket");
        add(LangUtil.tooltip("jewelry.type.earring"), "Earring");
        add(LangUtil.tooltip("jewelry.type.unknown"), "Jewelry");
        add(LangUtil.tooltip("jewelry.modifier.cost"), "Cost %s");
        add(LangUtil.tooltip("jewelry.modifier.cooldown"), "Cooldown %s");
        add(LangUtil.tooltip("jewelry.modifier.effect"), "Effect %s");
        add(LangUtil.tooltip("jewelry.modifier.duration"), "Duration %s");
        add(LangUtil.tooltip("jewelry.modifier.range"), "Range %s");
        add(LangUtil.tooltip("spell.stat.absorb"), "%s absorb");
        add(LangUtil.tooltip("spell.stat.armor"), "%s armor");
        add(LangUtil.tooltip("spell.stat.cooldown"), "%s cooldown");
        add(LangUtil.tooltip("spell.stat.damage"), "%s dmg");
        add(LangUtil.tooltip("spell.stat.drain"), "%s drain");
        add(LangUtil.tooltip("spell.stat.duration"), "%s duration");
        add(LangUtil.tooltip("spell.stat.every"), "every %s");
        add(LangUtil.tooltip("spell.stat.heal"), "%s heal");
        add(LangUtil.tooltip("spell.stat.hunger"), "%s hunger");
        add(LangUtil.tooltip("spell.stat.life"), "%s life");
        add(LangUtil.tooltip("spell.stat.mana"), "%s mana");
        add(LangUtil.tooltip("spell.stat.range"), "%s range");
        add(LangUtil.tooltip("spell.stat.reflect"), "%s reflect");
        add(LangUtil.tooltip("spell.stat.swords"), "%s swords");
        add(LangUtil.tooltip("spell.stat.fire_resist"), "%s fire resist");
        add(LangUtil.tooltip("spell.stat.magic_resist"), "%s magic resist");
        add(LangUtil.tooltip("spell.stat.wither_resist"), "%s wither resist");
        add(LangUtil.tooltip("spell.stat.stacks"), "stacks");
        add(LangUtil.tooltip("spell.stat.seconds"), "%ss");
        // sets
        add("tooltip.magictreasures.set.header", "%s (%s/%s)");
        add("tooltip.magictreasures.set.bonus", "(%s) %s");
        add(LangUtil.tooltip("set_piece.siphon"), "Siphon: full-strength hits restore %s mana to your worn jewelry");
        add(LangUtil.tooltip("set_piece.resonance"), "Resonance: +%s%% effect to this set's jewelry spells");
        add("set.magictreasures.salandaars_anvilwork", "Sal'andaar's Anvilwork");
        add("set.magictreasures.salandaars_anvilwork.bonus.2", "Ward spells cost 10%% less mana");
        add("set.magictreasures.salandaars_anvilwork.bonus.4", "Ward spells recover 15%% faster");
        add("set.magictreasures.salandaars_anvilwork.bonus.6", "Tempered: worn set jewelry's next anvil repair is free");
        add("set.magictreasures.silbros_grove", "Silbro's Grove");
        add("set.magictreasures.silbros_grove.bonus.2", "Healing spells heal 15%% more, 25%% more often");
        add("set.magictreasures.silbros_grove.bonus.4", "Standing on grass, moss or leaves slowly heals you");
        add("set.magictreasures.silbros_grove.bonus.5", "Take Root: in sunlight, worn set pieces slowly mend");
        add("set.magictreasures.maldritchs_remains", "Maldritch's Remains");
        add("set.magictreasures.maldritchs_remains.bonus.2", "Drain spells reach 1 block further");
        add("set.magictreasures.maldritchs_remains.bonus.4", "Drained life also refills the casting jewelry's mana");
        add("set.magictreasures.maldritchs_remains.bonus.5", "Bound possessions: the set's worn pieces stay with you when you die");
        add("set.magictreasures.selenes_tides", "Selene's Tides");
        add("set.magictreasures.selenes_tides.bonus.2", "Water Breathing and Night Vision last 50%% longer");
        add("set.magictreasures.selenes_tides.bonus.4", "Tidewalker: in water, swim with Dolphin's Grace and the tide refills your set jewelry's mana");
        add("set.magictreasures.selenes_tides.bonus.5", "Moonlit: spells cost 25%% less at night");
        add(LangUtil.tooltip("spell.flavor.blessing_of_the_phoenix"), "Rise unburnt from the flames, as the phoenix does.");
        add(LangUtil.tooltip("spell.flavor.cat_sight"), "See in the dark as clearly as a cat.");
        add(LangUtil.tooltip("spell.flavor.cheat_death"), "Turns aside one killing blow.");
        add(LangUtil.tooltip("spell.flavor.crushing_response"), "Every blow you take is repaid in kind.");
        add(LangUtil.tooltip("spell.flavor.desiccate"), "Withers the life from all who stand near.");
        add(LangUtil.tooltip("spell.flavor.disembodied_armor"), "An armor of spirit that turns aside claws and blades.");
        add(LangUtil.tooltip("spell.flavor.drain"), "Sips the life from nearby foes.");
        add(LangUtil.tooltip("spell.flavor.fire_resistance"), "A cool ward against flame and ember.");
        add(LangUtil.tooltip("spell.flavor.fire_ward"), "A stronger ward against flame and ember.");
        add(LangUtil.tooltip("spell.flavor.ghostly_armor"), "A faint, ghostly shell against mob attacks.");
        add(LangUtil.tooltip("spell.flavor.giant_strength"), "The strength of a giant, for a time.");
        add(LangUtil.tooltip("spell.flavor.greater_drain"), "Draws deeply from the life of nearby foes.");
        add(LangUtil.tooltip("spell.flavor.greater_healing"), "A deep, steady mending of wounds.");
        add(LangUtil.tooltip("spell.flavor.greater_invisibility"), "Vanish from sight for longer.");
        add(LangUtil.tooltip("spell.flavor.greater_night_vision"), "Night is no darker than day.");
        add(LangUtil.tooltip("spell.flavor.greater_ring_of_swords"), "A whirl of spirit blades that cut down all who come near.");
        add(LangUtil.tooltip("spell.flavor.greater_speed"), "Your feet barely touch the ground.");
        add(LangUtil.tooltip("spell.flavor.greater_strength"), "Your blows land with greater force.");
        add(LangUtil.tooltip("spell.flavor.greater_water_breathing"), "Breathe beneath the waves for a long while.");
        add(LangUtil.tooltip("spell.flavor.harm"), "A bolt of force at the nearest mob.");
        add(LangUtil.tooltip("spell.flavor.healing"), "Mends your wounds over time.");
        add(LangUtil.tooltip("spell.flavor.horse_power"), "Run like a horse at full gallop.");
        add(LangUtil.tooltip("spell.flavor.invisibility"), "Fade from sight for a moment.");
        add(LangUtil.tooltip("spell.flavor.lesser_healing"), "Slowly knits small wounds closed.");
        add(LangUtil.tooltip("spell.flavor.magic_resistance"), "Dulls the bite of hostile magic.");
        add(LangUtil.tooltip("spell.flavor.magic_ward"), "A firm ward against hostile magic.");
        add(LangUtil.tooltip("spell.flavor.mana_buckler"), "Your mana takes part of the blow.");
        add(LangUtil.tooltip("spell.flavor.mana_pavise_shield"), "A great wall of mana between you and harm.");
        add(LangUtil.tooltip("spell.flavor.mana_shield"), "Your mana takes the blow before you do.");
        add(LangUtil.tooltip("spell.flavor.mana_tower_shield"), "A towering shield of mana that blocks most blows.");
        add(LangUtil.tooltip("spell.flavor.mind_fist"), "A crushing blow of pure will.");
        add(LangUtil.tooltip("spell.flavor.mind_jab"), "A quick jab of mental force.");
        add(LangUtil.tooltip("spell.flavor.night_vision"), "Lets you see in the dark.");
        add(LangUtil.tooltip("spell.flavor.paladin_smite"), "Your life fuels a holy smite.");
        add(LangUtil.tooltip("spell.flavor.paladin_strike"), "Trade a little of your life for a harder strike.");
        add(LangUtil.tooltip("spell.flavor.quick_strength"), "A short burst of strength.");
        add(LangUtil.tooltip("spell.flavor.reflection"), "Turns part of each blow back on the attacker.");
        add(LangUtil.tooltip("spell.flavor.regeneration"), "Your wounds close almost as fast as they open.");
        add(LangUtil.tooltip("spell.flavor.ring_of_swords"), "Blades of spirit wheel about you when foes draw near.");
        add(LangUtil.tooltip("spell.flavor.salandaars_magic_coat"), "Sal'andaar's own coat against hostile magic.");
        add(LangUtil.tooltip("spell.flavor.satiety"), "Keeps hunger at bay.");
        add(LangUtil.tooltip("spell.flavor.shadow_armor"), "Shadows gather to soften each blow.");
        add(LangUtil.tooltip("spell.flavor.spectral_armor"), "A spectral shell that softens mob attacks.");
        add(LangUtil.tooltip("spell.flavor.speed"), "Quickens your step.");
        add(LangUtil.tooltip("spell.flavor.strength"), "Lends your arm more force.");
        add(LangUtil.tooltip("spell.flavor.water_breathing"), "Breathe underwater for a while.");
        add(LangUtil.tooltip("spell.flavor.wither_resistance"), "Slows the creeping rot of wither.");
        add(LangUtil.tooltip("spell.flavor.wither_ward"), "A firm ward against wither.");
        add(LangUtil.tooltip("spell.flavor.withers_skin"), "Skin as tough as the Wither's own.");
        add(LangUtil.tooltip("jewelry.stats.cost_factor"), "C:%s");
        add(LangUtil.tooltip("jewelry.stats.cooldown_factor"), "Cd:%s");
        add(LangUtil.tooltip("jewelry.stats.effect_amount_factor"), "E:%s");
        add(LangUtil.tooltip("jewelry.stats.range_factor"), "R:%s");

        /*
         * specific jewelry
         */
        // lore
        add(LangUtil.tooltip("jewelry.castle_ring.lore"), "Castle rings contain an abundance of mana and durability.~Can only be affixed with Rubies & Sapphires.");
        add(LangUtil.tooltip("jewelry.hawk_ring.lore"), "Hawk rings contain an abundance of mana and a higher max spell level.~Cannot be affixed with any gems.");
        add(LangUtil.tooltip("jewelry.silbros_ring_of_vitality.lore"), "Silbro grew this ring into shape.~Living wood holds mana as well as any metal.");
        add(LangUtil.tooltip("jewelry.strongmans_bracers.lore"), "Silbro grew these for a woodcutter who wore out every axe.~Living wood holds strength as well as mana.");
        add(LangUtil.tooltip("jewelry.silbros_acorn.lore"), "Silbro gave an acorn from his oldest oak to anyone who went hungry.~Planted, it feeds a village; worn, it feeds you.");
        add(LangUtil.tooltip("set_piece.rootstaff.lore"), "Silbro never cut a staff; he asked a root to grow straight.~Every blow it lands sends a little mana home.");
        add(LangUtil.tooltip("jewelry.smiths_cuff.lore"), "Smiths wear a cuff to keep sparks off the wrist.~Sal'andaar's sends the blow back to whoever struck it.");
        add(LangUtil.tooltip("set_piece.stonemark_hammer.lore"), "Every piece Sal'andaar forged bears this hammer's mark.~Each blow it lands sends a little mana home.");
        add(LangUtil.tooltip("set_piece.bone_scythe.lore"), "Maldritch cut it from his own bones, so it would never leave him.~Every blow it lands draws mana out of the wound.");
        add(LangUtil.tooltip("set_piece.moonglaive.lore"), "Selene's blade waxes and wanes, but never dulls.~Every blow it lands draws the tide of mana home.");
        add(LangUtil.tooltip("set_piece.pearl_circlet.lore"), "The pearls were her first, and her best.~They catch the light of her other work, as the sea catches the moon.");
        add(LangUtil.tooltip("set_piece.lichs_mask.lore"), "He wears it so the living see a man, not what he became.~It whispers when his other bones are near.");
        add(LangUtil.tooltip("set_piece.smiths_helm.lore"), "Hammered from the last of the good steel, in one night.~It rings softly when his other work is near.");
        add(LangUtil.tooltip("set_piece.anvil_shield.lore"), "Cut from a cracked anvil he couldn't bear to scrap.~It still takes a blow better than anything else he made.");
        add(LangUtil.tooltip("jewelry.tempered"), "Tempered: next anvil repair is free");
        add(LangUtil.tooltip("set_piece.barkskin_vest.lore"), "Bark from a living tree, which grew it back the next spring.~It hums when Silbro's other pieces are near.");
        add(LangUtil.tooltip("jewelry.maldritchs_first_amulet.lore"), "Maldritch is a powerful lich, but he wasn't always one.~This is his first amulet when he was a mere apprentice.");

        /*
         * spells
         */

        // spell scroll stats
        add(LangUtil.tooltip("spell.name"), "Name: %s");
        add(LangUtil.tooltip("spell.level"), "Level: %s");
        add(LangUtil.tooltip("spell.rarity"), "Rarity: %s");
        add(LangUtil.tooltip("spell.cost"), "Cost: %s");
        add(LangUtil.tooltip("spell.cost.varies"), "Cost: Varies");
        add(LangUtil.tooltip("spell.effect_amount"), "Effect Amount: %s");
        add(LangUtil.tooltip("spell.cooldown"), "Cooldown Time: %s seconds.");
        add(LangUtil.tooltip("spell.range"), "Range: %s blocks.");

        ///// healing /////
        add(LangUtil.tooltip("spell.name.lesser_healing"), "Lesser Healing");
        add(LangUtil.tooltip("spell.name.healing"), "Healing");
        add(LangUtil.tooltip("spell.name.greater_healing"), "Greater Healing");
        add(LangUtil.tooltip("spell.name.regeneration"), "Regeneration");

        add(LangUtil.tooltip("spell.healing.rate"), "Heals %s hp every %s seconds");

        ///// mana shield /////
        add(LangUtil.tooltip("spell.name.mana_buckler"), "Mana Buckler");
        add(LangUtil.tooltip("spell.name.mana_shield"), "Mana Shield");
        add(LangUtil.tooltip("spell.name.mana_tower_shield"), "Mana Tower Shield");
        add(LangUtil.tooltip("spell.name.mana_pavise_shield"), "Mana Pavise Shield");

        add(LangUtil.tooltip("spell.mana_shield.rate"), "Absorbs %s damage. Cooldown: %s seconds.");

        ///// spectral armor /////
        add(LangUtil.tooltip("spell.name.ghostly_armor"), "Ghostly Armor");
        add(LangUtil.tooltip("spell.name.spectral_armor"), "Spectral Armor");
        add(LangUtil.tooltip("spell.name.shadow_armor"), "Shadow Armor");
        add(LangUtil.tooltip("spell.name.disembodied_armor"), "Disembodied Armor");

        add(LangUtil.tooltip("spell.spectral_armor.rate"), "Reduces %s mob damage.");

        ///// ring of swords /////
        add(LangUtil.tooltip("spell.name.ring_of_swords"), "Ring of Swords");
        add(LangUtil.tooltip("spell.name.greater_ring_of_swords"), "Greater Ring of Swords");
        add(LangUtil.tooltip("spell.ring_of_swords.rate"), "Summons %s swords that circle you, %s dmg a hit, for %s seconds.");

        ///// drain /////
        add(LangUtil.tooltip("spell.name.drain"), "Drain");
        add(LangUtil.tooltip("spell.name.greater_drain"), "Greater Drain");
        add(LangUtil.tooltip("spell.name.desiccate"), "Desiccate");

        add(LangUtil.tooltip("spell.drain.rate"), "Drains %s hp from mobs within %s blocks every %s seconds.");

        ///// fire resistance /////
        add(LangUtil.tooltip("spell.name.fire_resistance"), "Fire Resistance");
        add(LangUtil.tooltip("spell.name.fire_ward"), "Fire Ward");
        add(LangUtil.tooltip("spell.name.blessing_of_the_phoenix"), "Blessing of the Phoenix");

        add(LangUtil.tooltip("spell.fire_resistance.rate"), "Resists %s fire damage.");

        ///// magic resistance /////
        add(LangUtil.tooltip("spell.name.magic_resistance"), "Magic Resistance");
        add(LangUtil.tooltip("spell.name.magic_ward"), "Magic Ward");
        add(LangUtil.tooltip("spell.name.salandaars_magic_coat"), "Sal'andaar's Magic Coat");

        add(LangUtil.tooltip("spell.magic_resistance.rate"), "Resists %s magic damage.");

        ///// wither resistance /////
        add(LangUtil.tooltip("spell.name.wither_resistance"), "Wither Resistance");
        add(LangUtil.tooltip("spell.name.wither_ward"), "Wither Ward");
        add(LangUtil.tooltip("spell.name.withers_skin"), "Wither's Skin");

        add(LangUtil.tooltip("spell.wither_resistance.rate"), "Resists %s wither damage.");

        ///// reflection /////
        add(LangUtil.tooltip("spell.name.reflection"), "Reflection");
        add(LangUtil.tooltip("spell.name.crushing_response"), "Crushing Response");

        add(LangUtil.tooltip("spell.reflection.rate"), "Reflects %s damage back onto mob. Cooldown: %s seconds.");

        ///// paladin strike /////
        add(LangUtil.tooltip("spell.name.paladin_strike"), "Paladin Strike");
        add(LangUtil.tooltip("spell.name.paladin_smite"), "Paladin Smite");

        add(LangUtil.tooltip("spell.paladin_strike.rate"), "Inflicts extra %s hp every %s seconds costing %s mana and %s hp.");

        ///// satiety /////
        add(LangUtil.tooltip("spell.name.satiety"), "Satiety");
        add(LangUtil.tooltip("spell.satiety.rate"), "Restores 0.5 hunger every %s seconds.");

        ///// cheat death /////
        add(LangUtil.tooltip("spell.name.cheat_death"), "Cheat Death");
        add(LangUtil.tooltip("spell.cheat_death.rate"), "Prevents death. Cooldown: %s seconds.");

        ///// strength /////
        add(LangUtil.tooltip("spell.name.quick_strength"), "Quick Strength");
        add(LangUtil.tooltip("spell.name.strength"), "Strength");
        add(LangUtil.tooltip("spell.name.greater_strength"), "Greater Strength");
        add(LangUtil.tooltip("spell.name.giant_strength"), "Giant Strength");
        add(LangUtil.tooltip("spell.strength.rate"), "Bestows Strength effect for %s seconds. Cooldown: %s seconds.");

        ///// speed /////
        add(LangUtil.tooltip("spell.name.speed"), "Speed");
        add(LangUtil.tooltip("spell.name.greater_speed"), "Greater Speed");
        add(LangUtil.tooltip("spell.name.horse_power"), "Horse Power");
        add(LangUtil.tooltip("spell.speed.rate"), "Bestows Speed effect for %s seconds. Cooldown: %s seconds.");

        ///// night vision /////
        add(LangUtil.tooltip("spell.name.night_vision"), "Night Vision");
        add(LangUtil.tooltip("spell.name.greater_night_vision"), "Greater Night Vision");
        add(LangUtil.tooltip("spell.name.cat_sight"), "Cat Sight");
        add(LangUtil.tooltip("spell.night_vision.rate"), "Bestows Night Vision effect for %s seconds. Cooldown: %s seconds.");

        ///// invisibility /////
        add(LangUtil.tooltip("spell.name.invisibility"), "Invisibility");
        add(LangUtil.tooltip("spell.name.greater_invisibility"), "Greater Invisibility");
        add(LangUtil.tooltip("spell.invisibility.rate"), "Bestows Invisibility effect for %s seconds. Cooldown: %s seconds.");

        ///// water-breathing /////
        add(LangUtil.tooltip("spell.name.water_breathing"), "Water Breathing");
        add(LangUtil.tooltip("spell.name.greater_water_breathing"), "Greater Water Breathing");
        add(LangUtil.tooltip("spell.water_breathing.rate"), "Bestows Water Breathing effect for %s seconds. Cooldown: %s seconds.");

        ///// harm /////
        add(LangUtil.tooltip("spell.name.harm"), "Harm");
        add(LangUtil.tooltip("spell.name.mind_jab"), "Mind Jab");
        add(LangUtil.tooltip("spell.name.mind_fist"), "Mind Fist");
        add(LangUtil.tooltip("spell.harm.rate"), "Inflicts %s hp to a single mob within %s blocks. Cooldown: %s seconds.");


        // scrolls
        add(LangUtil.tooltip("spell_scroll.usage"), "Place on an anvil with Magic Treasures jewelry to combine.");

        add(LangUtil.tooltip("jewelry_recipe_scroll.usage"), "Combine with 4 ingots of respective material on crafting table to craft jewelry item.");

        /*
         * screens
         */
        // chests
        //add(LangUtil.screen("wood_chest.name"), "Wood Chest");
   
        /*
         *  chat
         */
        // keys
        //add(LangUtil.chat("key.key_break"), "Your key broke whilst attempting to unlock the lock!");
    
    }
}
