package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Legacy batch R8-U5 (Bill 2026-11-11: every publication is a version; a PC card image is a version at its proven date;
 * the last publication is current). Nine families: four whose card list differs only in notation (release sheet, PC card
 * image 2013-02-25, Master List), four whose PC card image is in the 2009 V-kit (Out Of Somewhere (V), Warrior's
 * Courage (V), Deployment Orders: placed by 2009-12-09; Careful Planning (V): the 2009-09-09 errata, H1 pattern) and
 * Scomp Link Access (V) (PC card image wording not in the V-kit: server date 2013-02-25). The Master List (2013-04-27)
 * prints the release-sheet words and is current everywhere, so no current card changes. Images in cardImageUrlTests.js.
 */
public class LegacyBatchR8U5VersionsTests {
    /** current id, version id, label, printed on, superseded on, game text */
    private static final List<String[]> VERSIONS = List.of(
            new String[]{"1240_94", "2240_3094", "PC-E1", "2009-09-09", "2009-12-07", "If your starting location was a system, [download] a related site (must be a battleground if the system is a non-battleground or Endor) with < 3 [Light Side Force] icons and up to three Effects that are always immune to Alter. Place this Interrupt in Lost Pile."},
            new String[]{"1240_94", "2240_2094", "PC-E2", "2009-12-07", "2012-07-21", "If your starting location was a system, [download] a related site (must be a battleground if the system is a non-battleground or Endor) with < 3 [Light Side Force] and up to three Effects that are always immune to Alter. Place this Interrupt in Lost Pile."},
            new String[]{"1240_94", "2240_1094", "PC-E3", "2012-07-21", "2013-04-27", "If your starting location was a system, [download] a related site (must be a battleground if the system is a non-battleground or Endor) with < 3 [Light Side Force] icons and up to three Effects that are always immune to Alter. Place this Interrupt in Lost Pile."},
            new String[]{"1261_98", "2261_2098", "", "2009-09-04", "2009-12-09", "Use 1 Force to ▼ (or retrieve) Lando. OR If a battle just initiated, and your Lando defending alone at a site, his forfeit cannot be reduced, he adds one destiny to power (+1 for each character and vehicle present), and draws one battle destiny if not able to otherwise. (Immune to Sense.)"},
            new String[]{"1261_98", "2261_3098", "PC-E1", "2009-12-09", "2012-07-21", "Use 1 Force to ▼ (or retrieve) Lando. OR If a battle just initiated, and your Lando defending alone at a site, his forfeit cannot be reduced, he adds one destiny to power (+1 for each character and vehicle present) and draws one battle destiny if not able to otherwise. (Immune to Sense.)"},
            new String[]{"1261_98", "2261_1098", "PC-E2", "2012-07-21", "2013-04-27", "Use 1 Force to ▼ (or retrieve) Lando. OR If a battle just initiated, and your Lando defending alone at a site, his forfeit cannot be reduced, he adds one destiny to power (add 1 for each character and vehicle present), and draws one battle destiny if not able to otherwise. (Immune to Sense.)"},
            new String[]{"1261_100", "2261_2100", "", "2009-09-04", "2012-07-21", "USED: ▲ a non-[Episode I] droid (except IL-19). OR Place a card from hand on Used Pile. STARTING: ▼ two always [Immune to Alter] Effects. If you have only one location (with < 3 [Light Side Force]) (except Massassi Throne Room) on table, may ▼ a [Cloud City] site. Place this Interrupt in hand."},
            new String[]{"1261_100", "2261_1100", "PC-E1", "2012-07-21", "2013-02-25", "USED: ▲ a non-[Episode I] droid (except IL-19). OR Place a card from hand on Used Pile. STARTING: ▼ two always [Immune to Alter] Effects. If you have only one location (with less than 3 [Light Side Force]) (except Massassi Throne Room) on table, may ▼ a [Cloud City] site. Place this Interrupt in hand."},
            new String[]{"1261_100", "2261_3100", "PC-E2", "2013-02-25", "2013-04-27", "USED: a ▲ non-[Episode I] droid (except IL-19). OR Place a card from hand on Used Pile. STARTING: ▼ two always [Immune to Alter] Effects. If you have only one location (with < 3 [Light Side Force]) (except Massassi Throne Room) on table, may ▼ a [Cloud City] site. Place this Interrupt in hand."},
            new String[]{"1261_102", "2261_2102", "", "2009-09-04", "2009-12-09", "Use 1 Force to ▼ (or retrieve) Leia. OR If a battle just initiated, and non-[Reflections III] Leia defending alone at a site, her forfeit cannot be reduced, she adds one destiny to power (+1 for each character and vehicle present), and draws one battle destiny if not able to otherwise. (Immune to Sense.)"},
            new String[]{"1261_102", "2261_3102", "PC-E1", "2009-12-09", "2012-07-21", "Use 1 Force to ▼ (or retrieve) Leia. OR If a battle just initiated, and non-[Reflections III] Leia defending alone at a site, her forfeit cannot be reduced, she adds one destiny to power (+1 for each character and vehicle present) and draws one battle destiny if not able to otherwise. (Immune to Sense.)"},
            new String[]{"1261_102", "2261_1102", "PC-E2", "2012-07-21", "2013-04-27", "Use 1 Force to ▼ (or retrieve) Leia. OR If a battle just initiated, and non-[Reflections III] Leia is defending alone at a site, her forfeit cannot be reduced, she adds one destiny to power (+1 for each character and vehicle present), and draws one battle destiny if not able to otherwise. (Immune to Sense.)"},
            new String[]{"1262_6", "2262_2006", "", "2009-12-07", "2009-12-09", "Deploy on table. If your Landing Craft just landed, may use 1 Force to ▼ a [Presence] droid or Neimoidian aboard. Once per turn, may use 1 Force: reveal a [Theed Palace] vehicle (or Landing Craft) from hand to ▲ a [Presence] droid pilot (or vice versa), and deploy both simultaneously. (Immune to Alter.)"},
            new String[]{"1262_6", "2262_3006", "PC-E1", "2009-12-09", "2012-07-21", "Deploy on table. If your Landing Craft just landed, you may use 1 Force to ▼ a [Presence] droid or Neimoidian aboard. Once per turn, may use 1 Force: reveal a [Theed Palace] vehicle (or Landing Craft) from hand to ▲ a [Presence] droid pilot (or vice versa), and deploy both simultaneously. (Immune to Alter.)"},
            new String[]{"1262_6", "2262_1006", "PC-E2", "2012-07-21", "2013-04-27", "Deploy on table. If your Landing Craft just landed, you may use 1 Force to ▼ a [Presence] droid or Neimoidian aboard. Once per turn, may use 1 Force; reveal a [Theed Palace] vehicle (or Landing Craft) from hand to ▲ a [Presence] droid pilot (or vice versa) and deploy both simultaneously. (Immune to Alter.)"},
            new String[]{"1220_129", "2220_2129", "", "2009-12-07", "2013-02-25", "Deploy on table. Dack Ralter and Wes Janson have [Pilot] 2. During your control phase, may [upload] one Attack Pattern Delta, Desperate Tactics, Lucky Shot, One More Pass, Rapid Fire, or a vehicle weapon. May lose 2 Force to cancel Trample or Walker Barrage. (Immune to Alter.)"},
            new String[]{"1220_129", "2220_3129", "PC-E1", "2013-02-25", "2013-04-27", "Deploy on table. Dack Ralter and Wes Janson have [Pilot] 2. During your control phase, may [upload] one Attack Pattern Delta, Desperate Tactics, Lucky Shot, One More Pass, Rapid Fire, or a vehicle weapon. May lose 2 Force to cancel Trample or Walker Barrage. (Immune to Alter.)"},
            new String[]{"1272_1", "2272_2001", "", "2010-12-15", "2013-02-25", "[Pilot] 2, 3: Blizzard 2. Deploys -1 on Hoth. Once per game, may [download] a marker site with < 2 [Dark Side] or a battleground (except Endor system or a war room). Once per game, if piloting at a site where no characters present, may peek at opponent's hand."},
            new String[]{"1272_1", "2272_3001", "PC-E1", "2013-02-25", "2013-04-27", "[Pilot] 2, 3: Blizzard 2. Deploys -1 on Hoth. Once per game, may [download] a marker site with < 2 [Dark Side] or a battleground (except Endor system or a war room). Once per game, if piloting at a site where no characters present, may peek at opponent's hand."},
            new String[]{"1272_4", "2272_4", "", "2010-12-15", "2013-02-25", "[Pilot] 2. Smuggler. May not be attacked. Velken may not target Luke with weapons. Once per game, when deployed to a battleground, opponent must use or lose 2 Force. While present at a pit, may add 1 to Force drains here, and Sarlacc is ferocity +1."},
            new String[]{"1272_4", "2272_3004", "PC-E1", "2013-02-25", "2013-04-27", "[Pilot] 2. Smuggler. May not be attacked. Velken may not target Luke with weapons. Once per game, when deployed to a battleground, opponent must use or lose 2 Force. While present at a pit, may add 1 to Force drains here, and Sarlacc is ferocity +1."},
            new String[]{"1263_29", "2263_2029", "", "2010-03-12", "2013-02-25", "If Rescue The Princess on table, LOST: Once per game, choose: Cancel an attempt to make non-Heroic R2-D2 lost. OR Lose 2 Force to cancel a battle just initiated if Leia defending alone. (Immune to Sense.) STARTING: ▼ Cell 2187 and one always-[Immune to Alter] Effect. Place this Interrupt in hand."},
            new String[]{"1263_29", "2263_3029", "PC-E1", "2013-02-25", "2013-04-27", "If Rescue The Princess on table, LOST: Once per game, choose: Cancel an attempt to make non-Heroic R2-D2 lost. OR Lose 2 Force to cancel a battle just initiated if Leia defending alone. (Immune to Sense.) STARTING: ▼ Cell 2187 and one always-[Immune to Alter] Effect. Place this Interrupt in hand."});

    private static SwccgCardBlueprintLibrary library;

    @BeforeClass
    public static void setUpClass() {
        library = new SwccgCardBlueprintLibrary();
    }

    @Test
    public void VersionsCarryTheirDocumentsAndDates() {
        assertEquals(23, VERSIONS.size());
        for (String[] v : VERSIONS) {
            SwccgCardBlueprint current = library.getSwccgoCardBlueprint(v[0]);
            SwccgCardBlueprint card = library.getSwccgoCardBlueprint(v[1]);
            assertNotNull(v[1], card);
            assertNull(v[0], current.getLegacyVersion());
            LegacyVersion lv = card.getLegacyVersion();
            assertNotNull(v[1], lv);
            assertEquals(v[1], v[0], lv.currentPrintingId());
            assertEquals(v[1], v[2], lv.label());
            assertEquals(v[1], LocalDate.parse(v[3]), lv.printedOn());
            assertEquals(v[1], LocalDate.parse(v[4]), lv.supersededOn());
            assertEquals(v[1], v[5], card.getGameText());
            assertEquals(v[1], current.getTitle(), card.getTitle());
        }
    }
}
