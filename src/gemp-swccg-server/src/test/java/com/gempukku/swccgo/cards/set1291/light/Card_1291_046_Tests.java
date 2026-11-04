package com.gempukku.swccgo.cards.set1291.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1291_046_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("max", "1291_46");
                    put("bith", "1_115"); // Bith Shuffle (Used Interrupt)
                    put("plaza", "5_84"); // Cloud City: Upper Plaza Corridor (related to Chasm Walkway, Light icon only)
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                }},
                10, 10,
                StartingSetup.DefaultLSGroundLocation, // Cloud City: Chasm Walkway (battleground)
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void MaxReboStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("max").getBlueprint();
        assertEquals("Max Rebo", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(1, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        assertEquals(Species.ORTOLAN, card.getSpecies());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.ALIEN);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.MUSICIAN);
            add(Keyword.GAMBLER);
            add(Keyword.LEADER);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.ALIEN);
            add(Icon.LEGACY_BLOCK_9);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_9_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void MaxReboJustLostFromTableIsStolenIntoOpponentsLostPile() {
        var scn = GetScenario();
        var max = scn.GetLSCard("max");
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), max);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.LSExecuteAdHocEffect(max, new LoseCardFromTableEffect(new TopLevelGameTextAction(max, max.getCardId()), max));
        scn.PassAllResponses();
        Vb91LightTestSupport.advanceUntil(scn, () -> scn.DS.equals(max.getZoneOwner()));
        assertInZone(Zone.LOST_PILE, max);
        assertEquals(scn.DS, max.getOwner());
        assertEquals(scn.DS, max.getZoneOwner());
        assertFalse(scn.GetLSLostPile().contains(max));
    }

    @Test
    public void MaxReboLostFromLifeForceIsStolenIntoOpponentsLostPile() {
        var scn = GetScenario();
        var max = scn.GetLSCard("max");
        var plaza = scn.GetLSCard("plaza");
        var trooper = scn.GetDSCard("trooper");
        scn.StartGame();
        scn.MoveLocationToTable(plaza);
        scn.MoveCardsToLocation(plaza, trooper);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfOwnReserveDeck(max);
        scn.DSForceDrainAt(plaza);
        Vb91LightTestSupport.reach(scn, scn::AwaitingLSForceLossPayment);
        scn.LSPayForceLossFromReserveDeck();
        Vb91LightTestSupport.advanceUntil(scn, () -> scn.DS.equals(max.getZoneOwner()));
        assertInZone(Zone.LOST_PILE, max);
        assertEquals(scn.DS, max.getOwner());
        assertEquals(scn.DS, max.getZoneOwner());
    }

    @Test
    public void MaxReboAtBattlegroundSiteLetsBithShuffleCancelForceDrainAtRelatedLocationAsLostInterrupt() {
        var scn = GetScenario();
        var max = scn.GetLSCard("max");
        var bith = scn.GetLSCard("bith");
        var plaza = scn.GetLSCard("plaza");
        var trooper = scn.GetDSCard("trooper");
        scn.StartGame();
        scn.MoveLocationToTable(plaza);
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), max);
        scn.MoveCardsToLocation(plaza, trooper);
        scn.MoveCardsToHand(bith);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.DSForceDrainAt(plaza);
        Vb91LightTestSupport.reach(scn, () -> Vb91LightTestSupport.lsAction(scn, bith, "Cancel Force drain"));
        scn.LSUseCardAction(bith, "Cancel Force drain");
        scn.PassAllResponses();
        assertInZone(Zone.LOST_PILE, bith);
    }

    @Test
    public void MaxReboNotOnTableBithShuffleMayNotCancelForceDrain() {
        var scn = GetScenario();
        var bith = scn.GetLSCard("bith");
        var plaza = scn.GetLSCard("plaza");
        var trooper = scn.GetDSCard("trooper");
        scn.StartGame();
        scn.MoveLocationToTable(plaza);
        scn.MoveCardsToLocation(plaza, trooper);
        scn.MoveCardsToHand(bith);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.DSForceDrainAt(plaza);
        assertFalse(Vb91LightTestSupport.advanceUntil(scn, () -> Vb91LightTestSupport.lsAction(scn, bith, "Cancel Force drain")));
    }

    @Test
    public void MaxReboAtBattlegroundSiteOncePerTurnTakesBithShuffleIntoHand() {
        var scn = GetScenario();
        var max = scn.GetLSCard("max");
        var bith = scn.GetLSCard("bith");
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), max);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfOwnReserveDeck(bith);
        assertTrue(scn.LSCardActionAvailable(max, "Take Bith Shuffle or Ommni Box"));
        scn.LSUseCardAction(max, "Take Bith Shuffle or Ommni Box");
        Vb91LightTestSupport.reach(scn, () -> Vb91LightTestSupport.lsCardChoiceCount(scn) > 0);
        scn.LSChooseCard(bith);
        scn.PassAllResponses();
        assertInZone(Zone.HAND, bith);
        Vb91LightTestSupport.reach(scn, scn::AwaitingLSDeployPhaseActions);
        assertFalse(scn.LSCardActionAvailable(max, "Take Bith Shuffle or Ommni Box"));
    }
}
