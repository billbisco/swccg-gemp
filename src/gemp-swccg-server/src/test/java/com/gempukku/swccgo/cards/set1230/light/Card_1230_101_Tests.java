package com.gempukku.swccgo.cards.set1230.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
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

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import java.util.List;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1230_101_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("landing", "1230_101");
                    put("scout", "8_5");
                    put("forest", "8_72");
                    put("bunker", "8_70");
                    put("han", "1_11");
                    put("luke", "1_19");
                    put("obi", "1_21");
                    put("leia", "8_8"); put("alien", "1_30");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                    put("vader", "1_168"); put("tarkin", "1_167"); put("greedo", "2_89");
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
    public void CovertLandingStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("landing").getBlueprint();
        assertEquals("Covert Landing", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.USED_OR_LOST, card.getCardSubtype());
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

    @Test
    public void CovertLandingIsNotPlayableDuringYourMovePhase() {
        var scn = GetScenario();
        var landing = scn.GetLSCard("landing");
        var scout = scn.GetLSCard("scout");
        var forest = scn.GetLSCard("forest");
        scn.StartGame();
        scn.MoveLocationToTable(forest);
        var bunker = scn.GetLSCard("bunker"); scn.MoveLocationToTable(bunker);
        scn.game().getModifiersQuerying().blownAway(bunker); bunker.setBlownAway(true);
        scn.MoveCardsToTopOfLSReserveDeck(scout);
        scn.MoveCardsToLSHand(landing);
        scn.SkipToLSTurn(Phase.MOVE);
        assertFalse(scn.LSCardPlayAvailable(landing, "Deploy Rebel scout from Reserve Deck"));
        assertFalse(scn.LSCardPlayAvailable(landing, "Peek at top four cards of Reserve Deck"));
        assertEquals(Zone.HAND, landing.getZone());
    }

    @Test public void CovertLandingLostDeploysEligibleScoutToUnoccupiedEndorSiteAtNormalCost() {
        var scn = GetScenario(); var landing = scn.GetLSCard("landing"); var scout = scn.GetLSCard("scout");
        var forest = scn.GetLSCard("forest"); scn.StartGame(); scn.MoveLocationToTable(forest);
        scn.MoveCardsToTopOfLSReserveDeck(scout); scn.MoveCardsToLSHand(landing); scn.EnsureLSForcePile(4);
        scn.SkipToDSTurn(Phase.MOVE); scn.MoveCardsToTopOfLSReserveDeck(scout); scn.DSPass(); int force = scn.GetLSForcePileCount();
        scn.LSPlayCardAndPassResponses(landing, "Deploy Rebel scout from Reserve Deck");
        scn.LSChooseCard(scout); scn.PassAllResponses();
        assertAtLocation(forest, scout); assertEquals(force - 2, scn.GetLSForcePileCount());
        assertInZone(Zone.LOST_PILE, landing);
    }
    @Test public void CovertLandingUsedRequiresBlownAwayBunker() {
        var scn = GetScenario(); scn.StartGame(); scn.MoveLocationToTable(scn.GetLSCard("bunker"));
        scn.MoveCardsToLSHand(scn.GetLSCard("landing")); scn.SkipToDSTurn(Phase.MOVE); scn.DSPass();
        assertFalse(scn.LSCardPlayAvailable(scn.GetLSCard("landing"), "Peek at top four cards of Reserve Deck"));
    }
    @Test public void CovertLandingUsedReordersOwnTopFourWithoutTouchingFifth() { CheckReorder(false); }
    @Test public void CovertLandingUsedReordersOpponentsTopFourWithoutTouchingFifth() { CheckReorder(true); }
    private void CheckReorder(boolean opponent) {
        var scn = GetScenario(); scn.StartGame(); var bunker = scn.GetLSCard("bunker");
        scn.MoveLocationToTable(bunker); scn.game().getModifiersQuerying().blownAway(bunker); bunker.setBlownAway(true);
        var cards = List.of(scn.GetLSCard("scout"), scn.GetLSCard("han"), scn.GetLSCard("luke"), scn.GetLSCard("obi"));

        scn.MoveCardsToLSHand(scn.GetLSCard("landing"));
        scn.SkipToDSTurn(Phase.MOVE);
        if (opponent) scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSCard("trooper"), scn.GetDSCard("vader"), scn.GetDSCard("tarkin"), scn.GetDSCard("greedo"));
        else scn.MoveCardsToTopOfLSReserveDeck(cards.toArray(new com.gempukku.swccgo.game.PhysicalCardImpl[0]));
        scn.DSPass();
        var before = new ArrayList<>(scn.gameState().getReserveDeck(opponent ? scn.DS : scn.LS));
        scn.LSPlayCard(scn.GetLSCard("landing"), "Peek at top four cards of Reserve Deck");
        scn.LSChooseCard(opponent ? scn.GetTopOfDSReserveDeck() : scn.GetTopOfLSReserveDeck()); scn.PassAllResponses();
        for (int i = 0; i < 4; i++) scn.LSChooseCard((com.gempukku.swccgo.game.PhysicalCardImpl) before.get(i));
        scn.PassAllResponses();
        var after = new ArrayList<>(scn.gameState().getReserveDeck(opponent ? scn.DS : scn.LS));
        for (int i = 0; i < 4; i++) assertEquals(before.get(3-i), after.get(i));
        assertEquals(before.subList(4, before.size()), after.subList(4, after.size()));
        assertInZone(Zone.USED_PILE, scn.GetLSCard("landing"));
    }

    @Test public void CovertLandingLostExcludesNonScoutAbilityFiveAndAlienScout() { CheckScoutChoices(0); }
    @Test public void CovertLandingLostCannotDeployToOpponentOccupiedEndorSite() { CheckScoutChoices(1); }
    @Test public void CovertLandingLostCannotDeployToNonEndorSite() { CheckScoutChoices(2); }
    @Test public void CovertLandingLostDoesNotDeployScoutForFree() { CheckScoutChoices(3); }
    @Test public void CovertLandingLostUnavailableDuringOpponentsControlPhase() {
        var scn = GetScenario(); scn.StartGame(); scn.MoveCardsToLSHand(scn.GetLSCard("landing"));
        scn.SkipToDSTurn(Phase.CONTROL); scn.DSPass();
        assertFalse(scn.LSCardPlayAvailable(scn.GetLSCard("landing")));
    }
    private void CheckScoutChoices(int mode) {
        var scn = GetScenario(); scn.StartGame(); var forest = scn.GetLSCard("forest");
        if (mode != 2) scn.MoveLocationToTable(forest);
        if (mode == 1) scn.MoveCardsToLocation(forest, scn.GetDSCard("trooper"));
        scn.MoveCardsToLSHand(scn.GetLSCard("landing")); scn.SkipToDSTurn(Phase.MOVE);
        var scout = scn.GetLSCard("scout"); var han = scn.GetLSCard("han");
        var leia = scn.GetLSCard("leia"); var alien = scn.GetLSCard("alien");
        scn.MoveCardsToLSHand(scout, han, leia, alien); scn.EnsureLSForcePile(10); scn.DrainLSForcePileTo(mode == 3 ? 1 : 10);
        scn.MoveCardsToTopOfLSReserveDeck(scout, han, leia, alien); scn.DSPass();
        scn.LSPlayCardAndPassResponses(scn.GetLSCard("landing"), "Deploy Rebel scout");
        assertEquals(mode == 0, Selectable(scn, "8_5"));
        assertFalse(Selectable(scn, "1_11")); assertFalse(Selectable(scn, "8_8")); assertFalse(Selectable(scn, "1_30"));
        if (mode == 0) {
            scn.LSChooseCard(scout); scn.PassAllResponses(); assertAtLocation(forest, scout);
        } else {
            assertTrue(scn.LSDecisionAvailable("Verify")); assertTrue(scn.DSDecisionAvailable("Verify"));
            scn.LSChooseCards(); scn.DSChooseCards(); scn.PassAllResponses();
            assertInZone(Zone.RESERVE_DECK, scout);
        }
        assertInZone(Zone.LOST_PILE, scn.GetLSCard("landing"));
    }
    private boolean Selectable(VirtualTableScenario scn, String blueprint) {
        var ids = scn.LSGetBPChoices(); int index = ids.indexOf(blueprint);
        assertTrue("Expected card shown in searched Reserve Deck: " + blueprint, index >= 0);
        return Boolean.parseBoolean(scn.GetADParamAsList(scn.LS, "selectable").get(index));
    }
}
