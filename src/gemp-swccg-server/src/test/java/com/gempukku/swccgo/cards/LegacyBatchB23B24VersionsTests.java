package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Legacy batches B23 + B24, card-list versions: eleven single-version Virtual Block 1-4 cards whose PC website card list
 * text (by 2012-07-21, PC-E1) differs from the release sheet. The current card has the card-list text; the 22xx class is
 * the original release-sheet text (no label), Anything Goes only. Typos are kept as published on each text.
 */
public class LegacyBatchB23B24VersionsTests {
    /** version id -> {current id, original release-sheet text} */
    private static final Map<String, String[]> ORIGINALS = Map.ofEntries(
            Map.entry("2210_36", new String[]{"1210_36", "Deploy on table. Your troopers are Heroic. Opponent's total weapon destiny targeting your Heroic card is +2. When opponent is about to remove their last card from Life Force, you may first place all your troopers in Used Pile (last card must still be removed). (Immune to Alter.)"}),
            Map.entry("2210_63", new String[]{"1210_63", "At the beginning of your battle phase, use 1 Force to initiate a battle between two Imperials and an opponent's character present at same interior site. For remainder of turn, no other battles may occur here and opponent takes no battle damage here."}),
            Map.entry("2210_92", new String[]{"1210_92", "During battle at a site with R2-D2, may not be targeted by weapons, and may draw one destiny; subtract that amount from opponent's total power. Once per game, may place all your non-[Immune to Alter] Effects on table in Used Pile."}),
            Map.entry("2210_101", new String[]{"1210_101", "[Pilot] 3. Cards with 'Han' in title are destiny +3. May use 1 Force to cancel Boba Fett's or Jabba's game text for remainder of turn. If opponent just initiated battle here, may use 1 Force to take the first weapons segment action."}),
            Map.entry("2220_43", new String[]{"1220_43", "Deploy on table. Your capital starship weapon destiny draws are +1. During your deploy phase, may use 1 Force to [download] a starship weapon (for free) on your capital starship. If a starship was just hit, may place this Effect in Lost Pile to make that starship forfeit = 0."}),
            Map.entry("2220_123", new String[]{"1220_123", "Deploy on Hoth system if you control two exterior Hoth sites. During your control phase, may retrieve 1 Force for each Hoth site you control with a unique (•) combat vehicle. If opponent controls this system, this Effect lost. While you occupy 1st marker, [Immune to Alter]"}),
            Map.entry("2230_102", new String[]{"1230_102", "USED: Target one piloted starship at a system. That starship is hyperspeed -1 for remainder of turn. LOST: If you have two unique (•) piloted Rebel (or [Republic]) starfighters in battle at a system, draw destiny. If destiny < 4, exclude one starship from that battle."}),
            Map.entry("2240_1", new String[]{"1240_1", "Power +1 on Tatooine. Once per game, during your move phase, may relocate Arica to Luke's location. During your control phase, if present at a battleground (and Luke is not), may retrieve 1 Force. Immue to Undercover and attrition < 4."}),
            Map.entry("2240_9", new String[]{"1240_9", "Clone. Blaster Rifle deploys (and fires) for free on this trooper. When this trooper fires Blaster Rifle, may draw two weapon destiny and choose one (may take the other into hand). Immune to Rebel Barrier."}),
            Map.entry("2240_29", new String[]{"1240_29", "Deploy on table. Womp Rats are not [Selective Creature]. Once per turn, may [download] a creature (except a parasite) or a dewback. Your ferocious creatures are deploy -2 and ferocity +1. Whenever you retrieve Force because a creature has eaten a card, you need only use X-2 Force for Aim High (to a minimum of 1). (Immune to Alter.)"}),
            Map.entry("2240_68", new String[]{"1240_68", "May be battled. If with Dash Rendar, adds one destiny to attrition only. Once per turn, if at a battleground, you may peek at top card of opponent's Reserve Deck; may place that card on top of opponent's Used Pile."}));

    private static SwccgCardBlueprintLibrary library;

    @BeforeClass
    public static void setUpClass() {
        library = new SwccgCardBlueprintLibrary();
    }

    @Test
    public void OriginalsHaveTheReleaseSheetTextAndVersionInfo() {
        assertEquals(11, ORIGINALS.size());
        ORIGINALS.forEach((versionId, v) -> {
            var current = library.getSwccgoCardBlueprint(v[0]);
            var version = library.getSwccgoCardBlueprint(versionId);
            assertNotNull(versionId, version);
            LegacyVersionAsserts.assertVersionOf(current, version, v[1], v[0], "",
                    LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21));
            assertEquals(versionId, current.getLore(), version.getLore());
        });
    }

    @Test
    public void AricaOriginalMirrorsTheSheetIdOfItsLegacySetClass() {
        //test1: 1240_1 is an alias of 601_180; the version mirrors the sheet id and matches the class either way
        assertEquals(library.getSwccgoCardBlueprint("601_180").getGameText(), library.getSwccgoCardBlueprint("1240_1").getGameText());
        assertEquals("1240_1", library.getSwccgoCardBlueprint("2240_1").getLegacyVersion().currentPrintingId());
    }

    @Test
    public void UniqueOriginalIsTheSameCardAsTheCurrentPrinting() {
        // Forced Servitude (V) is unique: one version on table stops the other from deploying.
        for (boolean currentOnTable : List.of(true, false)) {
          for (boolean otherOut : List.of(false, true)) {
            var scn = new VirtualTableScenario(
                    new HashMap<>(),
                    new HashMap<>() {{
                        put("version", "2210_36");
                        put("current", "1210_36");
                    }},
                    10, 10,
                    StartingSetup.DefaultLSGroundLocation,
                    StartingSetup.DefaultDSGroundLocation,
                    StartingSetup.NoLSStartingInterrupts,
                    StartingSetup.NoDSStartingInterrupts,
                    StartingSetup.NoLSShields,
                    StartingSetup.NoDSShields,
                    VirtualTableScenario.Open);
            var onTable = scn.GetDSCard(currentOnTable ? "current" : "version");
            var inHand = scn.GetDSCard(currentOnTable ? "version" : "current");
            scn.StartGame();
            if (otherOut)
                scn.MoveCardsToDSSideOfTable(onTable);
            scn.MoveCardsToDSHand(inHand);
            scn.SkipToDSTurn(Phase.DEPLOY);
            scn.EnsureDSForcePile(8);
            assertEquals("current on table=" + currentOnTable + ", other out=" + otherOut, !otherOut, scn.DSCardPlayAvailable(inHand));
          }
        }
    }
}
