package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Legacy batch B16 (Virtual Blocks 5-6, 11 families; ai-team-skills shared/gemp-legacy-cards/versions/batches.json):
 * the current card carries the latest version's text and every older version is its own 22xx card with its text,
 * version label and current printing. Dark Waters &amp; Krayt Dragon Bones (PC-E1, 2261_1033) and Lone Pilot (V)
 * (PC-E2, 2261_2042) are versions again: the 2012 card list reordered their texts (not only icon notation).
 * Images (each version's own text) are pinned in cardImageUrlTests.js.
 */
public class LegacyBatchB16VersionsTests {
    private static final Map<String, String> CURRENT_TEXT = Map.ofEntries(
            Map.entry("1250_15", "Deploy on your alien of ability < 4. Character gains bounty hunter skill and immunity to attrition < 4. If character just hit, may place this device in Lost Pile (Used Pile if on Jango) to restore it to normal."),
            Map.entry("1250_18", "Deploy on Sidious. While at Senate, if battle was just initiated anywhere, opponent must relocate this card to their participating non-droid character; during battle here, your destiny draws may not be canceled. If character about to be lost, relocate to Sidious; may ▲ any one card."),
            Map.entry("1250_28", "Retarget an attempt to make your [Episode I] character 'hit' or lost to your other [Episode I] character present. OR Retarget opponent's just drawn destiny result about to make your starship 'hit' or lost to your character aboard; starship is protected from all such attempts this turn. OR ▼ Sil Unch."),
            Map.entry("1250_44ai", "Deploys -1 to [Episode I] locations. If you just lost Force from an opponent's Effect, may take an [Episode I] Interrupt (except Control) into hand from Reserve Deck; reshuffle, or place a card from hand under Credits Will Do Fine. May not be Disarmed. Immune to You Are Beaten and attrition."),
            Map.entry("1250_45ai", "Mace's game text may not be canceled (except at Senate) and he may fire one weapon twice per battle. Boba Fett may not add battle destiny draws here. Immune to attrition."),
            Map.entry("1250_82", "Deploy Jedi Council Chamber. For remainder of game, you may not deploy Affect Mind, Senate, [New Republic] characters or starships, Rebel starships, or Rebels of ability > 2. You lose no Force to Vengeance Of The Dark Prince or Your Destiny. Jedi Survivor's [Maintenance] is canceled. Speak With The Jedi Council is a Used Interrupt. While this side up, Yoda is deploy -3 to Jedi Council Chamber. Your Force drains are -1. Flip this card if Yoda and two other Jedi are at Jedi Council Chamber."),
            Map.entry("601_31", "Plays on table. In order to play an Interrupt from Lost Pile, opponent must first stack it here (if possible) and use +1 Force for each card here, even if Interrupt is normally free."),
            Map.entry("1261_24", "Plays on table. At opponent's Dantooine and Subjugated Planet sites, your Imperials are deploy -2 and your Force drains may not be modified or canceled by opponent. Your game text at opponent's <>Swamp and <>Jungle is canceled. Your characters present at opponent's <>Forest are immune to attrition."),
            Map.entry("1261_33", "Deploy on Rancor Pit. May ▼ a non-[Selective] ferocious creature here. During your battle phase, if you have no presence here, may relocate a captive from Audience Chamber to here. If opponent's character here was just eaten, retrieve 3 Force. If a creature here is lost, owner loses Force equal to half its printed deploy cost (round up). Once per turn, if a creature here is about to draw destiny for ferocity, may instead use its destiny number. (Immune to Alter.)"),
            Map.entry("1261_42", "USED: Activate 1 Force. STARTING: ▲ Bossk, Dengar, DS-61-2, Guri, or Juno Eclipse (may also ▲ a system with exactly 1 [Dark Side Force], a matching starship, and an always [Immune to Alter] Effect.) When drawing your starting hand, draw four cards instead of eight. Place this Interrupt in Lost Pile."),
            Map.entry("1261_45", "If Galen was just lost and Juno Eclipse is on table (or vice versa), place the just-lost character on your Used Pile instead. OR If Juno Eclipse is participating in a battle, may lose Galen from hand to satisfy all battle damage and attrition against you."));

    /** version id -> {current printing id, label, game text} */
    private static final Map<String, String[]> VERSIONS = Map.ofEntries(
            Map.entry("2250_15", new String[]{"1250_15", "", "Deploy on your alien of ability < 4. Character gains bounty hunter skill and immunity to attrition < 4. If character just hit, may place this device in Lost Pile (Used Pile if on The Mandalorian) to restore it to normal."}),
            Map.entry("2250_18", new String[]{"1250_18", "", "Deploy on Sidious. While at Senate, if battle was just initiated anywhere, opponent must relocate this card to their participating character; during battle here, your destiny draws may not be canceled. If character about to be lost, relocate to Sidious; may ▲ a card."}),
            Map.entry("2250_28", new String[]{"1250_28", "", "USED: For remainder of turn, if your starship is about to be hit (or lost), that action may instead affect your character aboard that starship. LOST: If your [Episode I] character is about to be hit (or lost), that action instead affects your other [Episode I] character present."}),
            Map.entry("2250_44ai", new String[]{"1250_44ai", "", "Deploys -1 to [Episode I] locations. If you just lost Force from an opponent's Effect, may ▲ an [Episode I] Interrupt (except Control), or place a card from hand under Credits Will Do Fine. Immune to Disarmed, You Are Beaten, and attrition."}),
            Map.entry("2250_45ai", new String[]{"1250_45ai", "", "Mace's game text may not be canceled (except at Senate). During battle, Mace may fire one weapon twice. Boba Fett may not add battle destiny draws here. Immune to attrition."}),
            Map.entry("2250_82", new String[]{"1250_82", "", "Deploy Jedi Council Chamber. For remainder of game, you may not deploy Affect Mind, Senate, Rebel starships, or Rebels of ability > 2. You lose no Force to Vengeance Of The Dark Prince or Your Destiny. Jedi Survivor's [Maintenance] is canceled. Speak With The Jedi Council is a Used Interrupt. While this side up, Yoda is deploy -3 to Jedi Council Chamber. Your Force drains are -1. Flip this card if Yoda and two other Jedi are at Jedi Council Chamber."}),
            Map.entry("2261_20", new String[]{"1261_20", "", "Plays on table. In order to play an Interrupt from Lost Pile, opponent must first stack it here and use +1 Force for each card here, even if Interrupt is normally free."}),
            Map.entry("2261_24", new String[]{"1261_24", "PC-E1", "Plays on table. At opponent's Dantooine and Subjugated Planet sites, your Imperials are deploy -2 and your Force drains may not be modified or canceled by opponent."}),
            Map.entry("2261_33", new String[]{"1261_33", "", "Deploy on a site with \"Waters\" in game text. During your battle phase, if on Rancor Pit, may relocate a captive from Audience Chamber to here. If opponent's character here was just eaten, retrieve 3 Force. If a creature here is lost, owner loses Force equal to half its printed deploy cost (round up). Once per turn, if a creature here is about to draw destiny for ferocity, may instead use its destiny number. May use 1 Force to ▼ a non-[Selective] ferocious creature here. (Immune to Alter.)"}),
            Map.entry("2261_1033", new String[]{"1261_33", "PC-E1", "Deploy on Rancor Pit. During your battle phase, if you have no presence here, may relocate a captive from Audience Chamber to here. If opponent's character here was just eaten, retrieve 3 Force. If a creature here is lost, owner loses Force equal to half its printed deploy cost (round up). Once per turn, if a creature here is about to draw destiny for ferocity, may instead use its destiny number. May ▼ a non-[Selective] ferocious creature here. (Immune to Alter.)"}),
            Map.entry("2261_42", new String[]{"1261_42", "", "USED: Activate 1 Force. STARTING: ▲ Black Leader, Bossk, Dengar, DS-61-2, or Guri (may also ▲ a system with 1 [Dark Side Force], a matching starship, and an always [Immune to Alter] Effect.) When drawing your starting hand, draw four cards instead of eight. Place this Interrupt in Lost Pile."}),
            Map.entry("2261_1042", new String[]{"1261_42", "PC-E1", "USED: Activate 1 Force. STARTING: ▲ Black Leader, Bossk, Dengar, DS-61-2, or Guri (may also ▲ a system with exactly 1 [Dark Side Force], a matching starship, and an always [Immune to Alter] Effect.) When drawing your starting hand, draw four cards instead of eight. Place this Interrupt in Lost Pile."}),
            Map.entry("2261_2042", new String[]{"1261_42", "PC-E2", "USED: Activate 1 Force. STARTING: ▲ Black Leader, Bossk, Dengar, DS-61-2, or Guri (may also ▲ a matching starship, a system with exactly 1 [Dark Side Force], and an always [Immune to Alter] Effect). When drawing your starting hand, draw four cards instead of eight. Place this Interrupt in Lost Pile."}),
            Map.entry("2261_45", new String[]{"1261_45", "", "If Galen was just lost and Black Leader is on table (or vice versa), place the just-lost character on your Used Pile instead. OR If Black Leader is participating in a battle, you may lose Galen from hand to satisfy all battle damage and attrition against you."}));

    private static SwccgCardBlueprintLibrary library;

    @BeforeClass
    public static void setUpClass() {
        library = new SwccgCardBlueprintLibrary();
    }

    @Test
    public void CurrentCardsCarryTheLatestText() {
        assertEquals(11, CURRENT_TEXT.size());
        CURRENT_TEXT.forEach((id, text) -> {
            SwccgCardBlueprint card = library.getSwccgoCardBlueprint(id);
            assertNotNull(id, card);
            assertNull(id, card.getLegacyVersion());
            assertEquals(id, text, card.getGameText());
        });
    }

    @Test
    public void OlderVersionsAreTheirOwnCards() {
        assertEquals(14, VERSIONS.size());
        VERSIONS.forEach((id, v) -> {
            SwccgCardBlueprint card = library.getSwccgoCardBlueprint(id);
            assertNotNull(id, card);
            assertEquals(id, id, library.getBaseBlueprintId(id));
            LegacyVersion version = card.getLegacyVersion();
            assertNotNull(id, version);
            assertEquals(id, v[0], version.currentPrintingId());
            assertEquals(id, v[1], version.label());
            assertEquals(id, v[2], card.getGameText());
        });
    }

    @Test
    public void SupersededOnTheDateOfTheNextText() {
        Map<String, LocalDate> superseded = Map.ofEntries(
                Map.entry("2250_18", LocalDate.of(2012, 7, 21)),
                Map.entry("2250_28", LocalDate.of(2010, 12, 20)),
                Map.entry("2250_44ai", LocalDate.of(2013, 4, 27)),
                Map.entry("2250_45ai", LocalDate.of(2012, 7, 21)),
                Map.entry("2261_20", LocalDate.of(2012, 7, 21)),
                Map.entry("2261_33", LocalDate.of(2010, 7, 26)),
                Map.entry("2261_1033", LocalDate.of(2012, 7, 21)),
                Map.entry("2261_42", LocalDate.of(2010, 4, 20)),
                Map.entry("2261_1042", LocalDate.of(2012, 7, 21)),
                Map.entry("2261_2042", LocalDate.of(2013, 4, 27)));
        superseded.forEach((id, date) -> assertEquals(id, date, library.getSwccgoCardBlueprint(id).getLegacyVersion().supersededOn()));
        assertEquals(LocalDate.of(2012, 7, 21), library.getSwccgoCardBlueprint("2261_2042").getLegacyVersion().printedOn());
        assertEquals(LocalDate.of(2010, 7, 26), library.getSwccgoCardBlueprint("2261_1033").getLegacyVersion().printedOn());
    }

    @Test
    public void RestoredVersionsPlayLikeTheirCurrentCard() {
        assertEquals("2261_1033", LegacyVersion.versionIdOf("1261_33", 1));
        assertEquals("2261_2042", LegacyVersion.versionIdOf("1261_42", 2));
        assertSame(library.getSwccgoCardBlueprint("1261_33").getClass(), library.getSwccgoCardBlueprint("2261_1033").getClass().getSuperclass());
        assertSame(library.getSwccgoCardBlueprint("1261_42").getClass(), library.getSwccgoCardBlueprint("2261_2042").getClass().getSuperclass());
    }

    @Test
    public void MandalorianArmorLoreAsPrinted() {
        assertTrue(library.getSwccgoCardBlueprint("1250_15").getLore().contains("a projected grappling hook"));
    }
}
