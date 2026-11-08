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

/** Original Fanfare (V) (Virtual Shields sheet, May 2013), a version of 1200_5. */
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
        assertEquals("Plays on table. Cancels Order To Engage and Scramble. Once per game, you may [upload] an Immediate Effect. While opponent occupies no battleground systems, Staging Areas is suspended.",
                card.getGameText());
        var version = card.getLegacyVersion();
        org.junit.Assert.assertNotNull(version);
        assertEquals("1200_5", version.currentPrintingId());
        assertEquals("", version.label());
        assertEquals(java.time.LocalDate.of(2013, 5, 21), version.printedOn());
        assertEquals(java.time.LocalDate.of(2014, 2, 4), version.supersededOn());
        assertEquals("Fanfare (V)", com.gempukku.swccgo.logic.GameUtils.getFullName(card));
    }

    private static boolean missingTreatedAsLandspeedZero(VirtualTableScenario scn) {
        return com.gempukku.swccgo.cards.GameConditions.hasGameTextModification(scn.game(), scn.GetLSCard("litw"),
                ModifyGameTextType.LOST_IN_THE_WILDERNESS__MISSING_TREATED_AS_LANDSPEED_0);
    }

    @Test
    public void OriginalFanfareHasNoLostInTheWildernessClause() {
        var scn = GetScenario();
        scn.StartGame();
        scn.MoveCardsToLSHand(scn.GetLSCard("litw"));
        scn.MoveCardsToDSSideOfTable(scn.GetDSCard("fanfare"));
        assertFalse(missingTreatedAsLandspeedZero(scn));
    }

    @Test
    public void CurrentFanfareTreatsMissingOnLostInTheWildernessAsLandspeedZero() {
        var scn = GetScenario();
        scn.StartGame();
        scn.MoveCardsToLSHand(scn.GetLSCard("litw"));
        scn.MoveCardsToDSSideOfTable(scn.GetDSCard("current"));
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
