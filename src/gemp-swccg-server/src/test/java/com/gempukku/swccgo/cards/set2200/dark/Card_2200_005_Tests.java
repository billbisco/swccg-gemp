package com.gempukku.swccgo.cards.set2200.dark;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/** 2200_5 Fanfare (V) "original" (the PC card list text, by 2012-07-21). Batch B22: the Virtual Shields sheet (May 2013) prints the same text, so the current card 1200_5 plays it too; whether 2200_5 stays a version waits for Bill (shared Fanfare ids are not split or merged). */
public class Card_2200_005_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("litw", "8_57"); // Lost In The Wilderness
                }},
                new HashMap<>() {{
                    put("fanfare", "2200_5");
                    put("current", "1200_5");
                }},
                10, 10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void OriginalFanfareIdentityAndText() {
        var scn = GetScenario();
        var card = scn.GetDSCard("fanfare").getBlueprint();

        assertEquals("Fanfare", card.getTitle());
        assertTrue(card.isLegacy());
        assertEquals("Plays on table. Cancels Order To Engage and Scramble. Once per game, may [upload] an Immediate Effect. While opponent occupies no battleground systems, Staging Areas is suspended. 'Missing' on Lost In The Wilderness is treated as 'landspeed = 0 for remainder of turn'.",
                card.getGameText());
        var version = card.getLegacyVersion();
        org.junit.Assert.assertNotNull(version);
        assertEquals("1200_5", version.currentPrintingId());
        assertEquals("", version.label());
        assertEquals(java.time.LocalDate.of(2012, 7, 21), version.printedOn());
        assertEquals(java.time.LocalDate.of(2013, 5, 21), version.supersededOn());
        assertEquals("Fanfare (V)", com.gempukku.swccgo.logic.GameUtils.getFullName(card));
    }

    private static boolean missingTreatedAsLandspeedZero(VirtualTableScenario scn) {
        return com.gempukku.swccgo.cards.GameConditions.hasGameTextModification(scn.game(), scn.GetLSCard("litw"),
                ModifyGameTextType.LOST_IN_THE_WILDERNESS__MISSING_TREATED_AS_LANDSPEED_0);
    }

    @Test
    public void CurrentFanfareIsTheVirtualShieldsSheetText() {
        com.gempukku.swccgo.cards.Vb89VersionAsserts.assertCurrentCard("1200_5", "Fanfare",
                "Plays on table. Cancels Order To Engage and Scramble. Once per game, may [upload] an Immediate Effect. While opponent occupies no battleground systems, Staging Areas is suspended. 'Missing' on Lost In The Wilderness is treated as 'landspeed = 0 for remainder of turn.'");
    }

    @Test
    public void CurrentFanfareTreatsMissingOnLostInTheWildernessAsLandspeedZero() {
        // Batch B22: both •Fanfare (V) slips of the Virtual Shields sheet print the Lost In The Wilderness sentence.
        var scn = GetScenario();
        scn.StartGame();
        scn.MoveCardsToLSHand(scn.GetLSCard("litw"));
        scn.MoveCardsToDSSideOfTable(scn.GetDSCard("current"));
        assertTrue(missingTreatedAsLandspeedZero(scn));
    }

    @Test
    public void OriginalFanfareTreatsMissingOnLostInTheWildernessAsLandspeedZero() {
        var scn = GetScenario();
        scn.StartGame();
        scn.MoveCardsToLSHand(scn.GetLSCard("litw"));
        scn.MoveCardsToDSSideOfTable(scn.GetDSCard("fanfare"));
        assertTrue(missingTreatedAsLandspeedZero(scn));
    }

    @Test
    public void OriginalFanfareMayTakeAnImmediateEffectIntoHandOncePerGame() {
        var scn = GetScenario();
        var fanfare = scn.GetDSCard("fanfare");
        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(fanfare);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSCardActionAvailable(fanfare, "Take Immediate Effect into hand"));
    }
}
