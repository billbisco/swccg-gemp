package com.gempukku.swccgo.cards.set1274.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1274_020_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("foothold", "1274_20");
                    put("luke", "1_19");
                    put("leia", "1_17");
                    put("yavin", "1_135");
                    put("corvette", "1_140");
                    put("dockingbay", "1_136");
                    put("jungle", "1_137");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
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
    public void TemporaryFootholdStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("foothold").getBlueprint();

        assertEquals("Temporary Foothold", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.SETUP);
            add(Icon.EFFECT);
            add(Icon.LEGACY_BLOCK_7);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.DEPLOYS_ON_LOCATION);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void TemporaryFootholdDeploysOnlyOnABattleground() {
        var scn = GetScenario();
        var foothold = scn.GetLSCard("foothold");
        var battleground = scn.GetDSStartingLocation();
        var notBattleground = scn.GetLSCard("jungle");

        scn.StartGame();
        scn.MoveLocationToTable(notBattleground);
        scn.MoveCardsToHand(foothold);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSDeployAvailable(foothold));
        scn.LSDeployCard(foothold);
        assertTrue(scn.LSHasCardChoiceAvailable(battleground));
        assertFalse(scn.LSHasCardChoiceAvailable(notBattleground));
        scn.LSChooseCard(battleground);
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(battleground, foothold));
    }

    @Test
    public void TemporaryFootholdForceDrainsHereMayNotBeCanceledWhileOccupyingTwoBattlegrounds() {
        var scn = GetScenario();
        var foothold = scn.GetLSCard("foothold");
        var luke = scn.GetLSCard("luke");
        var yavin = scn.GetLSCard("yavin");
        var corvette = scn.GetLSCard("corvette");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(yavin);
        scn.AttachCardsTo(site, foothold);
        scn.MoveCardsToLocation(site, luke);

        var querying = scn.game().getModifiersQuerying();
        assertFalse(querying.cantCancelForceDrainAtLocation(scn.gameState(), site, null, scn.DS, scn.LS));

        scn.MoveCardsToLocation(yavin, corvette);
        assertTrue(querying.cantCancelForceDrainAtLocation(scn.gameState(), site, null, scn.DS, scn.LS));
        // Only the owner's Force drains are protected
        assertFalse(querying.cantCancelForceDrainAtLocation(scn.gameState(), site, null, scn.LS, scn.DS));
    }

    @Test
    public void TemporaryFootholdPlacesInUsedPileDuringBattleHereToDrawTwoCards() {
        var scn = GetScenario();
        var foothold = scn.GetLSCard("foothold");
        var luke = scn.GetLSCard("luke");
        var leia = scn.GetLSCard("leia");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.AttachCardsTo(site, foothold);
        scn.MoveCardsToLocation(site, luke);
        scn.MoveCardsToLocation(site, trooper);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.MoveCardsToTopOfOwnReserveDeck(leia);
        assertFalse(scn.LSCardActionAvailable(foothold, "Place in Used Pile to draw cards"));

        scn.LSInitiateBattle(site);
        scn.PassBattleStartResponses();
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        int handBefore = scn.GetLSHandCount();
        assertTrue(scn.LSCardActionAvailable(foothold, "Place in Used Pile to draw cards"));
        scn.LSUseCardAction(foothold, "Place in Used Pile to draw cards");
        scn.PassAllResponses();
        assertTrue(scn.LSDecisionAvailable("Choose number of cards to draw"));
        assertEquals(2, scn.LSGetChoiceMax());
        scn.LSDecided(2);
        scn.PassAllResponses();

        assertInZone(Zone.USED_PILE, foothold);
        assertInZone(Zone.HAND, leia);
        assertEquals(handBefore + 2, scn.GetLSHandCount());
    }

    @Test
    public void TemporaryFootholdMayDrawDuringBattleAtRelatedLocation() {
        var scn = GetScenario();
        var foothold = scn.GetLSCard("foothold");
        var yavin = scn.GetLSCard("yavin");
        var dockingBay = scn.GetLSCard("dockingbay");
        var luke = scn.GetLSCard("luke");
        var trooper = scn.GetDSCard("trooper");

        scn.StartGame();
        scn.MoveLocationToTable(yavin);
        scn.MoveLocationToTable(dockingBay);
        scn.AttachCardsTo(yavin, foothold);
        scn.MoveCardsToLocation(dockingBay, luke);
        scn.MoveCardsToLocation(dockingBay, trooper);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(dockingBay);
        scn.PassBattleStartResponses();
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        assertTrue(scn.LSCardActionAvailable(foothold, "Place in Used Pile to draw cards"));
    }

    @Test
    public void TemporaryFootholdMayNotDrawDuringBattleAtUnrelatedLocation() {
        var scn = GetScenario();
        var foothold = scn.GetLSCard("foothold");
        var yavin = scn.GetLSCard("yavin");
        var luke = scn.GetLSCard("luke");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(yavin);
        scn.AttachCardsTo(yavin, foothold);
        scn.MoveCardsToLocation(site, luke);
        scn.MoveCardsToLocation(site, trooper);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PassBattleStartResponses();
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        assertFalse(scn.LSCardActionAvailable(foothold, "Place in Used Pile to draw cards"));
    }
}
