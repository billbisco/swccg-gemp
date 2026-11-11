package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Legacy batch B31 (Virtual Blocks 7-8, title changes; ai-team-skills shared/gemp-legacy-cards/versions/batches.json).
 * Bill (2026-11-09): title changes are versions. Six cards were renamed between the PC card list of 2013-01-11 and
 * 2013-03-13/14 (Virtual Cards Master List 2013-04-27); each original printing, under its old title, is its own 22xx
 * card. Jango Fett, The Assassin and Kyle Katarn also have a card-list PC-E1 text under the old title (2281_1009 /
 * 2281_1028). The current cards keep the latest title and text; the AI printings (1281_9ai / 1281_28ai / 1281_45ai)
 * were only published under the new titles and stay aliases. Images are pinned in cardImageUrlTests.js.
 */
public class LegacyBatchB31VersionsTests {
    /** version id -> {current printing id, label, game text, printed on, superseded on, current title} */
    private static final Map<String, String[]> VERSIONS = Map.ofEntries(
            Map.entry("2273_9", new String[]{"1273_9", "", "Deploys -1 to Kessel. While at a Kessel battleground site, your Force drains are +1 at X related locations, where X = number of 'spice' cards on Spice Mine Operations, and once per turn, may use 3 Force to take a 'spice' card you own into hand.", "2011, 6, 3", "2013, 3, 14", "Moruth Doole, Kessel Administrator"}),
            Map.entry("2281_9", new String[]{"1281_9", "", "Adds 3 to the power of anything he pilots. Adds one battle destiny. SD-17 Homing Missile may deploy on (and is a matching weapon for) him, fires for free, and when fired may go to Lost Pile instead. While piloted by two Fetts (unless your [Block 4] objective on table), Slave I is immune to attrition < 9.", "2012, 5, 11", "2012, 7, 21", "Jango Fett, The Assassin"}),
            Map.entry("2281_1009", new String[]{"1281_9", "PC-E1", "Adds 3 to the power of anything he pilots. Adds one battle destiny. SD-17 Homing Missile may deploy on (and is a matching weapon for) him, fires for free, and when fired may go to lost pile instead. While piloting Slave I with Boba (unless your [Block 4] objective on table), it is immune to attrition <9.", "2012, 7, 21", "2013, 3, 13", "Jango Fett, The Assassin"}),
            Map.entry("2281_28", new String[]{"1281_28", "", "May use any stolen weapon. Opponent may not 'react' to or from same site. Your total battle destiny here is +1 for each [Scoundrel] Effect on table. Once per game, may make a regular move during your control phase. Immune to attrition < 4 (< 5 if armed).", "2012, 5, 11", "2012, 7, 21", "Kyle Katarn"}),
            Map.entry("2281_1028", new String[]{"1281_28", "PC-E1", "May use any stolen weapon. Opponent may not react to or from same site. Your total battle destiny +1 here, for each [Scoundrel] Effect on table. Once per game, may make a regular move during your control phase. Immune to attrition < 4 (< 5 if armed).", "2012, 7, 21", "2013, 3, 13", "Kyle Katarn"}),
            Map.entry("2281_45", new String[]{"1281_45", "", "May add 6 pilots. Permanent pilot provides ability of 3. Unless your [Reflections II] objective on table, once per game may ▼ a non-[Maintenance] scoundrel aboard and while Corran or a Terrik piloting, immune to attrition < 7.", "2012, 5, 11", "2013, 3, 13", "Errant Venture"}),
            Map.entry("2282_13", new String[]{"1282_13", "", "Adds 2 to the power of anything he pilots. While present at Audience Chamber or stacked on your Objective: non-unique Devaronians are deploy -1, power and forfeit +1, and if Yerka Mig is about to be lost, may place it in Used Pile instead.", "2012, 9, 14", "2013, 3, 13", "Vilmarh 'Villie' Grahrk"}),
            Map.entry("2282_27", new String[]{"1282_27", "", "May add 2 pilots and 2 passengers. Scoundrels deploy -1 aboard. May reveal from hand to ▲ [Cloud City] Lando and deploy both simultaneously. While Lando piloting, immune to attrition < 5 (< 6 if at Bespin).", "2012, 9, 14", "2013, 3, 13", "Lady Luck"}));

    private static SwccgCardBlueprintLibrary library;

    @BeforeClass
    public static void setUpClass() {
        library = new SwccgCardBlueprintLibrary();
    }

    private static LocalDate date(String ymd) {
        String[] p = ymd.split(", ");
        return LocalDate.of(Integer.parseInt(p[0]), Integer.parseInt(p[1]), Integer.parseInt(p[2]));
    }

    @Test
    public void OlderVersionsAreTheirOwnCards() {
        assertEquals(8, VERSIONS.size());
        VERSIONS.forEach((id, v) -> {
            SwccgCardBlueprint card = library.getSwccgoCardBlueprint(id);
            assertNotNull(id, card);
            assertEquals(id, id, library.getBaseBlueprintId(id));
            LegacyVersion version = card.getLegacyVersion();
            assertNotNull(id, version);
            assertEquals(id, v[0], version.currentPrintingId());
            assertEquals(id, v[1], version.label());
            assertEquals(id, v[2], card.getGameText());
            assertEquals(id, date(v[3]), version.printedOn());
            assertEquals(id, date(v[4]), version.supersededOn());
            assertEquals(id, v[5], card.getTitle());
            SwccgCardBlueprint current = library.getSwccgoCardBlueprint(v[0]);
            assertNull(v[0], current.getLegacyVersion());
            assertNull(v[0], current.getDisplayTitle());
            assertEquals(id, current.getTitle(), card.getTitle());
            assertEquals(id, current.getPersonas(), card.getPersonas());
        });
    }

    @Test
    public void CurrentCardsCarryTheLatestTitleAndText() {
        Map<String, String[]> current = Map.of(
                "1281_28", new String[]{"Kyle Katarn", "Scoundrel.", "May use any stolen weapon. Opponent may not 'react' to or from same site. Your total battle destiny here is +1 for each [Scoundrel] Effect on table. Once per game, may make a regular move during your control phase. Immune to attrition < 4 (< 5 if armed)."},
                "1281_9", new String[]{"Jango Fett, The Assassin", "Trade Federation. Scout.", "Adds 3 to the power of anything he pilots. Adds one battle destiny. SD-17 Homing Missile may deploy on (and is a matching weapon for) him, fires for free, and when fired may go to Lost Pile instead. While piloted by two Fetts (unless your [Block 4] objective on table), Slave I is immune to attrition < 9."});
        current.forEach((id, v) -> {
            SwccgCardBlueprint card = library.getSwccgoCardBlueprint(id);
            assertEquals(id, v[0], card.getTitle());
            assertEquals(id, v[1], card.getLore());
            assertEquals(id, v[2], card.getGameText());
        });
        assertEquals(VERSIONS.get("2281_45")[2], library.getSwccgoCardBlueprint("1281_45").getGameText());
        assertEquals(VERSIONS.get("2282_27")[2], library.getSwccgoCardBlueprint("1282_27").getGameText());
    }

    @Test
    public void MandalorianOriginalKeepsItsPrintedLore() {
        SwccgCardBlueprint original = library.getSwccgoCardBlueprint("2281_9");
        assertEquals("Trade Federation assassin and scout.", original.getLore());
        assertEquals("Trade Federation assassin and scout.", library.getSwccgoCardBlueprint("2281_1009").getLore());
        assertTrue(original.hasKeyword(Keyword.ASSASSIN));
        assertTrue(original.hasKeyword(Keyword.SCOUT));
        assertTrue(original.hasPersona(Persona.JANGO_FETT));
        assertTrue(library.getSwccgoCardBlueprint("2281_28").hasPersona(Persona.KYLE_KATARN));
    }

    @Test
    public void CardListVersionsKeepTheOldTitle() {
        assertEquals("The Mandalorian, Father Of Fett", library.getSwccgoCardBlueprint("2281_1009").getDisplayTitle());
        assertEquals("Rebel Agent", library.getSwccgoCardBlueprint("2281_1028").getDisplayTitle());
        assertEquals("The Mandalorian, Father Of Fett [PC-E1]", com.gempukku.swccgo.logic.GameUtils.getFullName(library.getSwccgoCardBlueprint("2281_1009")).replace("•", ""));
    }

    @Test
    public void AiPrintingsStayAliasesOfTheirBaseCards() {
        assertEquals("601_13", library.getBaseBlueprintId("1281_9ai"));
        assertEquals("1281_28", library.getBaseBlueprintId("1281_28ai"));
        assertEquals("1281_45", library.getBaseBlueprintId("1281_45ai"));
    }
}
