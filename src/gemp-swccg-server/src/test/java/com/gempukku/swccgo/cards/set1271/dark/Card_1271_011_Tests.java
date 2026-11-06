package com.gempukku.swccgo.cards.set1271.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1271_011_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("luke", "1_19");
        }}, new HashMap<>() {{
            put("myself", "1271_11");
            put("copy", "1271_11");
            put("setup", "1271_27");
            put("immediate", "12_133");
            put("trooper", "1_194");
        }}, 30, 30, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    private boolean pileCardSelectable(VirtualTableScenario scn, com.gempukku.swccgo.game.PhysicalCardImpl card) {
        String[] ids = scn.DSGetADParam("blueprintId");
        String[] selectable = scn.DSGetADParam("selectable");
        org.junit.Assert.assertNotNull(ids);
        org.junit.Assert.assertNotNull(selectable);
        for (int i = 0; i < ids.length; i++)
            if (ids[i].equals(card.getBlueprintId(true)) && Boolean.parseBoolean(selectable[i]))
                return true;
        return false;
    }

    @Test
    public void IllTakeThemMyselfStatsIconsAndKeywordsAreCorrect() {
        var scn = scenario();
        var card = scn.GetDSCard("myself").getBlueprint();
        assertEquals("I'll Take Them Myself", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
        assertTrue(card.isImmuneToCardTitle(Title.Alter));
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{ add(CardType.EFFECT); }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{ add(Icon.EFFECT); add(Icon.LEGACY_BLOCK_7); }});
    }

    private VirtualTableScenario ready() {
        var scn = scenario();
        scn.StartGame();
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.MoveCardsToDSSideOfTable(scn.GetDSCard("myself"));
        scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSCard("setup"), scn.GetDSCard("immediate"), scn.GetDSCard("trooper"));
        scn.SkipToPhase(Phase.DEPLOY);
        return scn;
    }

    private void returnToDarkAction(VirtualTableScenario scn) {
        if (scn.GetDecidingPlayer().equals(scn.LS))
            scn.LSPass();
    }

    @Test
    public void IllTakeThemMyselfDeploysOnTableWithoutUsingForce() {
        var scn = scenario();
        scn.StartGame();
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.MoveCardsToDSHand(scn.GetDSCard("myself"));
        scn.SkipToPhase(Phase.DEPLOY);
        int force = scn.GetDSForcePileCount();
        scn.DSPlayCard(scn.GetDSCard("myself"));
        scn.PassAllResponses();
        assertEquals(Zone.SIDE_OF_TABLE, scn.GetDSCard("myself").getZone());
        assertEquals(force, scn.GetDSForcePileCount());
    }

    @Test
    public void IllTakeThemMyselfTakesOnlyASetupCardIntoHand() {
        var scn = ready();
        scn.DSUseCardAction(scn.GetDSCard("myself"), "Setup card");
        scn.PassAllResponses();
        assertTrue(pileCardSelectable(scn, scn.GetDSCard("setup")));
        assertFalse(pileCardSelectable(scn, scn.GetDSCard("immediate")));
        assertFalse(pileCardSelectable(scn, scn.GetDSCard("trooper")));
        scn.DSChooseCard(scn.GetDSCard("setup"));
        scn.PassAllResponses();
        assertEquals(Zone.HAND, scn.GetDSCard("setup").getZone());
        assertEquals(Zone.SIDE_OF_TABLE, scn.GetDSCard("myself").getZone());
        returnToDarkAction(scn);
        assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("myself"), "Setup card"));
    }

    @Test
    public void IllTakeThemMyselfSetupLimitDoesNotResetOnLaterTurns() {
        var scn = ready();
        scn.DSUseCardAction(scn.GetDSCard("myself"), "Setup card");
        scn.PassAllResponses();
        scn.DSChooseCard(scn.GetDSCard("setup"));
        scn.PassAllResponses();
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.SkipToDSTurn(Phase.CONTROL);
        assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("myself"), "Setup card"));
        assertTrue(scn.DSCardActionAvailable(scn.GetDSCard("myself"), "Immediate Effect"));
    }

    @Test
    public void IllTakeThemMyselfASecondCopyCannotUseTheSetupSearchAgain() {
        var scn = ready();
        scn.DSUseCardAction(scn.GetDSCard("myself"), "Setup card");
        scn.PassAllResponses();
        scn.DSChooseCard(scn.GetDSCard("setup"));
        scn.PassAllResponses();
        scn.MoveCardsToTopOfDSLostPile(scn.GetDSCard("myself"));
        scn.MoveCardsToDSSideOfTable(scn.GetDSCard("copy"));
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.SkipToDSTurn(Phase.CONTROL);
        assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("copy"), "Setup card"));
        assertTrue(scn.DSCardActionAvailable(scn.GetDSCard("copy"), "Immediate Effect"));
    }

    @Test
    public void IllTakeThemMyselfPlacesItselfInUsedPileToTakeAnImmediateEffect() {
        var scn = ready();
        scn.DSUseCardAction(scn.GetDSCard("myself"), "Immediate Effect");
        scn.PassAllResponses();
        assertTrue(scn.GetDSUsedPile().contains(scn.GetDSCard("myself")));
        assertTrue(pileCardSelectable(scn, scn.GetDSCard("immediate")));
        assertFalse(pileCardSelectable(scn, scn.GetDSCard("setup")));
        assertFalse(pileCardSelectable(scn, scn.GetDSCard("trooper")));
        scn.DSChooseCard(scn.GetDSCard("immediate"));
        scn.PassAllResponses();
        assertEquals(Zone.HAND, scn.GetDSCard("immediate").getZone());
    }

    @Test
    public void IllTakeThemMyselfMayUseTheImmediateSearchWithoutUsingTheSetupSearch() {
        var scn = ready();
        assertTrue(scn.DSCardActionAvailable(scn.GetDSCard("myself"), "Setup card"));
        scn.DSUseCardAction(scn.GetDSCard("myself"), "Immediate Effect");
        scn.PassAllResponses();
        scn.DSChooseCard(scn.GetDSCard("immediate"));
        scn.PassAllResponses();
        assertEquals(Zone.HAND, scn.GetDSCard("immediate").getZone());
        assertTrue(scn.GetDSUsedPile().contains(scn.GetDSCard("myself")));
    }

    @Test
    public void IllTakeThemMyselfCancelsAnActualForceDrainByPlacingItselfInLostPile() {
        var scn = ready();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("luke"));
        scn.SkipToLSTurn(Phase.CONTROL);
        int force = scn.GetDSLifeForceRemaining();
        scn.LSForceDrainAt(scn.GetLSStartingLocation());
        scn.LSPass();
        assertTrue(scn.DSCardActionAvailable(scn.GetDSCard("myself"), "cancel Force drain"));
        scn.DSUseCardAction(scn.GetDSCard("myself"), "cancel Force drain");
        scn.PassAllResponses();
        assertFalse(scn.IsActiveForceDrain());
        assertEquals(force, scn.GetDSLifeForceRemaining());
        assertTrue(scn.GetDSLostPile().contains(scn.GetDSCard("myself")));
    }

    @Test
    public void IllTakeThemMyselfCannotCancelAForceDrainOutsideItsResponseWindow() {
        var scn = ready();
        assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("myself"), "cancel Force drain"));
        assertEquals(Zone.SIDE_OF_TABLE, scn.GetDSCard("myself").getZone());
    }

    @Test
    public void IllTakeThemMyselfMayCancelItsOwnersDrainBecauseTheTextDoesNotLimitThePlayer() {
        var scn = ready();
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("trooper"));
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.SkipToDSTurn(Phase.CONTROL);
        int force = scn.GetLSLifeForceRemaining();
        scn.DSForceDrainAt(scn.GetDSStartingLocation());
        for (int i = 0; i < 10 && scn.GetDecidingPlayer().equals(scn.LS); i++)
            scn.LSPass();
        assertTrue(scn.DSCardActionAvailable(scn.GetDSCard("myself"), "cancel Force drain"));
        scn.DSUseCardAction(scn.GetDSCard("myself"), "cancel Force drain");
        scn.PassAllResponses();
        assertFalse(scn.IsActiveForceDrain());
        assertEquals(force, scn.GetLSLifeForceRemaining());
        assertTrue(scn.GetDSLostPile().contains(scn.GetDSCard("myself")));
    }

    @Test
    public void IllTakeThemMyselfImmediateSearchStillPaysTheUsedPileCostWhenNothingIsFound() {
        var scn = ready();
        scn.MoveOutOfPlay(scn.GetDSCard("immediate"));
        scn.DSUseCardAction(scn.GetDSCard("myself"), "Immediate Effect");
        scn.PassAllResponses();
        scn.DSDecided("");
        scn.PassAllResponses();
        assertTrue(scn.GetDSUsedPile().contains(scn.GetDSCard("myself")));
        assertEquals(Zone.OUT_OF_PLAY, scn.GetDSCard("immediate").getZone());
    }
}
