package com.gempukku.swccgo.cards.set1230.light;

import com.gempukku.swccgo.common.CardSubtype;
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
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1230_104_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("celebration", "1230_104");
                    put("door", "8_69"); put("forest", "8_72");
                    put("scout", "8_5"); put("alien", "1_30"); put("han", "1_11");
                }},
                new HashMap<>() {{
                    put("occupation", "7_223");
                    put("trooper", "1_194"); put("trooper2", "1_194");
                    put("bespin", "5_164"); put("chamber", "5_166");
                    put("scout", "7_195"); put("vader", "1_168"); put("sense", "1_267");
                }},
                30, 30,
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
    public void EndorCelebrationStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("celebration").getBlueprint();
        assertEquals("Endor Celebration", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.USED, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.INTERRUPT);
            add(Icon.LEGACY_BLOCK_3);
            add(Icon.ENDOR);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }


    @Test public void EndorCelebrationCancelsOccupationOnTableAndGoesToUsedPile() {
        var scn = OccupationScenario(); scn.SkipToLSTurn(Phase.ACTIVATE);
        scn.AttachCardsTo(scn.GetDSCard("bespin"), scn.GetDSCard("occupation")); scn.SkipToPhase(Phase.CONTROL);
        scn.LSPlayCard(scn.GetLSCard("celebration"), "Cancel Effect with occupation in title");
        scn.LSChooseCard(scn.GetDSCard("occupation")); scn.PassAllResponses();
        assertInZone(Zone.LOST_PILE, scn.GetDSCard("occupation")); assertInZone(Zone.USED_PILE, scn.GetLSCard("celebration"));
    }
    @Test public void EndorCelebrationActivatesFiveThenGoesBelowExistingLostCards() {
        var scn = ScoutScenario(); scn.MoveCardsToTopOfLSLostPile(scn.GetLSFillerRange(1,3)); scn.PrepareLSDestiny(1);
        var before = List.copyOf(scn.GetLSLostPile()); int force = scn.GetLSForcePileCount(); int reserve = scn.GetLSReserveDeckCount();
        scn.LSPlayCardAndPassResponses(scn.GetLSCard("celebration")); scn.LSChooseOption("Activate 5 Force"); scn.PassAllResponses();
        assertEquals(force + 5, scn.GetLSForcePileCount()); assertEquals(reserve - 6, scn.GetLSReserveDeckCount());
        var after = scn.GetLSLostPile(); assertEquals(before, after.subList(0, before.size()));
        assertEquals(scn.GetLSCard("celebration"), after.getLast()); assertEquals(before.size() + 1, after.size());
    }
    @Test public void EndorCelebrationRetrievesFiveThenGoesOnBottomOfRemainingLostCards() {
        var scn = ScoutScenario(); scn.MoveCardsToTopOfLSLostPile(scn.GetLSFillerRange(1,7)); scn.PrepareLSDestiny(1);
        var before = List.copyOf(scn.GetLSLostPile()); int used = scn.GetLSUsedPileCount();
        scn.LSPlayCardAndPassResponses(scn.GetLSCard("celebration")); scn.LSChooseOption("Retrieve 5 Force"); scn.PassAllResponses();
        assertEquals(used + 6, scn.GetLSUsedPileCount()); var after = scn.GetLSLostPile();
        assertEquals(before.subList(5,7), after.subList(0,2)); assertEquals(scn.GetLSCard("celebration"), after.getLast()); assertEquals(3, after.size());
    }
    @Test public void EndorCelebrationCannotRetrieveItselfWhenFewerThanFiveCardsAreLost() {
        var scn = ScoutScenario(); scn.MoveCardsToTopOfLSLostPile(scn.GetLSFillerRange(1,3)); scn.PrepareLSDestiny(1);
        int used = scn.GetLSUsedPileCount(); scn.LSPlayCardAndPassResponses(scn.GetLSCard("celebration"));
        scn.LSChooseOption("Retrieve 5 Force"); scn.PassAllResponses();
        assertEquals(used + 4, scn.GetLSUsedPileCount()); assertEquals(1, scn.GetLSLostPileCount());
        assertInZone(Zone.LOST_PILE, scn.GetLSCard("celebration"));
    }
    @Test public void EndorCelebrationEqualDestinyFailsAndGoesToUsedPile() {
        var scn = ScoutScenario(); scn.PrepareLSDestiny(2); int force = scn.GetLSForcePileCount();
        scn.LSPlayCardAndPassResponses(scn.GetLSCard("celebration"));
        assertFalse(scn.LSChoiceAvailable("Activate 5")); assertEquals(force, scn.GetLSForcePileCount());
        assertInZone(Zone.USED_PILE, scn.GetLSCard("celebration"));
    }
    @Test public void EndorCelebrationCountsYourAlienScoutsAwayFromBackDoor() {
        var scn = ScoutScenario(); scn.PrepareLSDestiny(1);
        scn.LSPlayCardAndPassResponses(scn.GetLSCard("celebration")); assertTrue(scn.LSChoiceAvailable("Activate 5"));
        scn.LSChooseOption("Activate 5 Force"); scn.PassAllResponses(); assertInZone(Zone.LOST_PILE, scn.GetLSCard("celebration"));
    }
    @Test public void EndorCelebrationDoesNotCountOpponentsScouts() {
        var scn = GetScenario(); scn.StartGame(); scn.MoveLocationToTable(scn.GetLSCard("door")); scn.MoveLocationToTable(scn.GetLSCard("forest"));
        scn.MoveCardsToLocation(scn.GetLSCard("door"), scn.GetLSCard("scout")); scn.MoveCardsToLocation(scn.GetLSCard("forest"), scn.GetDSCard("scout"));
        scn.MoveCardsToLSHand(scn.GetLSCard("celebration")); scn.SkipToLSTurn(Phase.DRAW); scn.PrepareLSDestiny(1);
        scn.LSPlayCardAndPassResponses(scn.GetLSCard("celebration")); assertFalse(scn.LSChoiceAvailable("Activate 5"));
        assertInZone(Zone.USED_PILE, scn.GetLSCard("celebration"));
    }
    @Test public void EndorCelebrationRequiresRebelScoutRatherThanAlienScoutAtBackDoor() { CheckUnavailable(0); }
    @Test public void EndorCelebrationRequiresScoutAtBackDoorRatherThanAnotherEndorSite() { CheckUnavailable(1); }
    @Test public void EndorCelebrationRequiresScoutToBeAlone() { CheckUnavailable(2); }
    @Test public void EndorCelebrationRequiresYourDrawPhase() { CheckUnavailable(3); }
    @Test public void EndorCelebrationCancelsOccupationWhileBeingPlayed() {
        var scn = OccupationScenario(); scn.MoveCardsToDSHand(scn.GetDSCard("occupation"));
        scn.EnsureDSForcePile(5); scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSDeployCard(scn.GetDSCard("occupation")); scn.DSChooseCard(scn.GetDSCard("bespin")); scn.DSPass();
        scn.LSPlayCard(scn.GetLSCard("celebration")); scn.PassAllResponses();
        assertInZone(Zone.LOST_PILE, scn.GetDSCard("occupation")); assertInZone(Zone.USED_PILE, scn.GetLSCard("celebration"));
    }
    @Test public void EndorCelebrationOccupationCancellationIsImmuneToSense() {
        var scn = OccupationScenario(); scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("vader"));
        scn.MoveCardsToDSHand(scn.GetDSCard("sense")); scn.SkipToLSTurn(Phase.ACTIVATE);
        scn.AttachCardsTo(scn.GetDSCard("bespin"), scn.GetDSCard("occupation")); scn.SkipToPhase(Phase.CONTROL);
        scn.LSPlayCard(scn.GetLSCard("celebration"), "Cancel Effect with occupation in title");
        scn.LSChooseCard(scn.GetDSCard("occupation")); scn.LSPass();
        assertTrue(scn.DSDecisionAvailable("Optional responses")); assertFalse(scn.DSCardPlayAvailable(scn.GetDSCard("sense")));
        scn.PassAllResponses(); assertInZone(Zone.LOST_PILE, scn.GetDSCard("occupation"));
    }
    @Test public void EndorCelebrationScoutDestinyActionCanBeSensed() {
        var scn = ScoutScenario(); scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("vader"));
        scn.MoveCardsToDSHand(scn.GetDSCard("sense")); scn.PrepareDSDestiny(5);
        scn.LSPlayCard(scn.GetLSCard("celebration")); scn.LSPass();
        assertTrue(scn.DSDecisionAvailable("Optional responses")); assertTrue(scn.DSCardPlayAvailable(scn.GetDSCard("sense")));
        scn.DSPlayCard(scn.GetDSCard("sense")); scn.DSChooseCard(scn.GetDSCard("vader")); scn.PassAllResponses();
        assertInZone(Zone.LOST_PILE, scn.GetLSCard("celebration")); assertFalse(scn.LSChoiceAvailable("Activate 5"));
        assertInZone(Zone.USED_PILE, scn.GetDSCard("sense"));
    }
    private void CheckUnavailable(int mode) {
        var scn = GetScenario(); scn.StartGame(); scn.MoveLocationToTable(scn.GetLSCard("door")); scn.MoveLocationToTable(scn.GetLSCard("forest"));
        scn.MoveCardsToLocation(scn.GetLSCard(mode == 1 ? "forest" : "door"), scn.GetLSCard(mode == 0 ? "alien" : "scout"));
        if (mode == 2) scn.MoveCardsToLocation(scn.GetLSCard("door"), scn.GetLSCard("han"));
        scn.MoveCardsToLSHand(scn.GetLSCard("celebration")); scn.SkipToLSTurn(mode == 3 ? Phase.CONTROL : Phase.DRAW);
        assertFalse(scn.LSCardPlayAvailable(scn.GetLSCard("celebration")));
    }
    private VirtualTableScenario ScoutScenario() {
        var scn = GetScenario(); scn.StartGame(); scn.MoveLocationToTable(scn.GetLSCard("door")); scn.MoveLocationToTable(scn.GetLSCard("forest"));
        scn.MoveCardsToLocation(scn.GetLSCard("door"), scn.GetLSCard("scout")); scn.MoveCardsToLocation(scn.GetLSCard("forest"), scn.GetLSCard("alien"));
        scn.MoveCardsToLSHand(scn.GetLSCard("celebration")); scn.SkipToLSTurn(Phase.DRAW); return scn;
    }
    private VirtualTableScenario OccupationScenario() {
        var scn = GetScenario(); scn.StartGame(); scn.MoveLocationToTable(scn.GetDSCard("bespin")); scn.MoveLocationToTable(scn.GetDSCard("chamber"));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetDSCard("trooper")); scn.MoveCardsToLocation(scn.GetDSCard("chamber"), scn.GetDSCard("trooper2"));
        scn.MoveCardsToLSHand(scn.GetLSCard("celebration")); return scn;
    }
}
