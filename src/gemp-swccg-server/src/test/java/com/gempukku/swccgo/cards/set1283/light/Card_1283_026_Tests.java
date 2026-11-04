package com.gempukku.swccgo.cards.set1283.light;

import com.gempukku.swccgo.common.CardSubtype;
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
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.logic.modifiers.ModifierType;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1283_026_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("run", "1283_26");
                    put("han", "1_11");
                    put("falcon", "1_143");
                    put("tatooine", "1_127");
                    put("kessel", "1_126");
                    put("boshek", "1_4");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                    put("vader", "1_168");
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

    private static PhysicalCardImpl putObjectiveOnTable(VirtualTableScenario scn, String id) {
        PhysicalCardImpl objective = (PhysicalCardImpl) scn.gameState().createPhysicalCard(scn.LS, VirtualTableScenario._cardLibrary, id);
        scn.gameState().addCardToZone(objective, Zone.SIDE_OF_TABLE, scn.LS, true, false, false);
        objective.setObjectiveDeploymentComplete(true);
        scn.gameState().reapplyAffectingForCard(scn.game(), objective);
        return objective;
    }

    /**
     * Infiltration on table with Kessel Run (V) on it, Han piloting the Falcon at Tatooine, Kessel on table.
     */
    private PhysicalCardImpl setUp(VirtualTableScenario scn) {
        var run = scn.GetLSCard("run");
        var han = scn.GetLSCard("han");
        var falcon = scn.GetLSCard("falcon");
        var tatooine = scn.GetLSCard("tatooine");
        var kessel = scn.GetLSCard("kessel");

        scn.StartGame();
        var infiltration = putObjectiveOnTable(scn, "1281_44");
        scn.AttachCardsTo(infiltration, run);
        scn.gameState().reapplyAffectingForCard(scn.game(), run);
        scn.MoveLocationToTable(tatooine);
        scn.MoveLocationToTable(kessel);
        scn.MoveCardsToLocation(tatooine, falcon);
        scn.BoardAsPilot(falcon, han);
        return infiltration;
    }

    private void target(VirtualTableScenario scn) {
        var run = scn.GetLSCard("run");
        var han = scn.GetLSCard("han");
        assertTrue(scn.LSCardActionAvailable(run, "Target"));
        scn.LSUseCardAction(run, "Target");
        if (scn.LSAnyDecisionsAvailable() && scn.LSHasCardChoiceAvailable(han)) {
            scn.LSChooseCard(han);
        }
        scn.PassAllResponses();
        if (scn.DSAnyDecisionsAvailable()) {
            scn.DSPass();
        }
    }

    /**
     * Moves the Falcon (with Han aboard) to the given system and lets the game process the table change by passing
     * the rest of the current phase.
     */
    private void flyTo(VirtualTableScenario scn, PhysicalCardImpl system, Phase nextPhase) {
        var run = scn.GetLSCard("run");
        scn.MoveCardsToLocation(system, scn.GetLSCard("falcon"));
        for (int i = 0; i < 120; i++) {
            var ls = scn.GetAwaitingDecision(scn.LS);
            var ds = scn.GetAwaitingDecision(scn.DS);
            if (scn.GetCurrentPhase() == nextPhase) break;
            if (ls != null && ls.getText().startsWith("Choose card to put on")) {
                var candidates = new ArrayList<PhysicalCardImpl>(scn.GetStackedCards(run));
                candidates.add(run);
                for (var card : candidates) {
                    if (scn.LSHasCardChoiceAvailable(card)) {
                        scn.LSChooseCard(card);
                        break;
                    }
                }
                continue;
            }
            if (ds != null) scn.DSPass(); else if (ls != null) scn.LSPass();
        }
    }

    @Test
    public void KesselRunVStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("run").getBlueprint();

        assertEquals(Title.Kessel_Run, card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.MISSION);
        }});
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.MISSION);
            add(Icon.SETUP);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void KesselRunDeploysOnInfiltration() {
        var scn = GetScenario();
        var run = scn.GetLSCard("run");
        scn.StartGame();
        var infiltration = putObjectiveOnTable(scn, "1281_44");
        scn.MoveCardsToLSHand(run);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSCardPlayAvailable(run));
        scn.LSPlayCard(run);
        if (scn.LSAnyDecisionsAvailable() && scn.LSHasCardChoiceAvailable(infiltration)) {
            scn.LSChooseCard(infiltration);
        }
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(infiltration, run));
    }

    @Test
    public void KesselRunDeploysOnNonVirtualBlock4WatchYourStep() {
        var scn = GetScenario();
        var run = scn.GetLSCard("run");
        scn.StartGame();
        var wys = putObjectiveOnTable(scn, "10_26");
        scn.MoveCardsToLSHand(run);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSCardPlayAvailable(run));
    }

    @Test
    public void KesselRunDoesNotDeployOnVirtualBlock4WatchYourStep() {
        var scn = GetScenario();
        var run = scn.GetLSCard("run");
        scn.StartGame();
        putObjectiveOnTable(scn, "601_146");
        scn.MoveCardsToLSHand(run);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertFalse(scn.LSCardPlayAvailable(run));
    }

    @Test
    public void KesselRunMayDeployKesselFromReserveDeck() {
        var scn = GetScenario();
        var run = scn.GetLSCard("run");
        var kessel = scn.GetLSCard("kessel");
        scn.StartGame();
        var infiltration = putObjectiveOnTable(scn, "1281_44");
        scn.AttachCardsTo(infiltration, run);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSReserveDeck(kessel);
        assertTrue(scn.LSCardActionAvailable(run, "Deploy Kessel"));
        scn.LSUseCardAction(run, "Deploy Kessel");
        scn.PassAllResponses();
        if (scn.LSAnyDecisionsAvailable() && scn.LSHasCardChoiceAvailable(kessel)) {
            scn.LSChooseCard(kessel);
            scn.PassAllResponses();
        }
        assertEquals(Zone.LOCATIONS, kessel.getZone());
    }

    @Test
    public void KesselRunTargetsYourSmugglerAtANonKesselSystemAndMakesItImmuneToSetForStun() {
        var scn = GetScenario();
        var han = scn.GetLSCard("han");
        setUp(scn);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertFalse(scn.game().getModifiersQuerying().isImmuneToCardTitle(scn.gameState(), han, Title.Set_For_Stun));
        target(scn);
        assertTrue(scn.game().getModifiersQuerying().isImmuneToCardTitle(scn.gameState(), han, Title.Set_For_Stun));
        assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("run"), "Target"));
    }

    @Test
    public void KesselRunMayNotTargetASmugglerAtKessel() {
        var scn = GetScenario();
        setUp(scn);
        scn.MoveCardsToLocation(scn.GetLSCard("kessel"), scn.GetLSCard("falcon"));
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("run"), "Target"));
    }

    @Test
    public void KesselRunMayNotBeCanceled() {
        var scn = GetScenario();
        setUp(scn);
        assertTrue(scn.game().getModifiersQuerying().mayNotBeCanceled(scn.gameState(), scn.GetLSCard("run")));
    }

    @Test
    public void WhenTargetReachesKesselDrawFiveParsecDestiniesAndStackThemHere() {
        var scn = GetScenario();
        var run = scn.GetLSCard("run");
        setUp(scn);
        scn.SkipToLSTurn(Phase.DEPLOY);
        target(scn);
        for (int i = 0; i < 5; i++) {
            scn.PrepareLSDestiny(1);
        }
        flyTo(scn, scn.GetLSCard("kessel"), Phase.BATTLE);
        assertEquals(5, scn.GetStackedCards(run).size());
        assertTrue(run.getAttachedTo() != null);
    }

    @Test
    public void TakeACardHereIntoHandAtStartOfYourTurn() {
        var scn = GetScenario();
        var run = scn.GetLSCard("run");
        var boshek = scn.GetLSCard("boshek");
        setUp(scn);
        scn.StackCardsOn(run, boshek);
        scn.SkipToLSTurn(Phase.ACTIVATE);
        scn.PassAllResponses();
        assertInZone(Zone.HAND, boshek);
    }

    @Test
    public void IfTargetIsLostCardsHereArePlacedInUsedPile() {
        var scn = GetScenario();
        var run = scn.GetLSCard("run");
        var han = scn.GetLSCard("han");
        setUp(scn);
        scn.SkipToLSTurn(Phase.DEPLOY);
        target(scn);
        for (int i = 0; i < 5; i++) {
            scn.PrepareLSDestiny(1);
        }
        flyTo(scn, scn.GetLSCard("kessel"), Phase.BATTLE);
        assertEquals(5, scn.GetStackedCards(run).size());
        int usedBefore = scn.GetLSUsedPileCount();
        var stacked = new ArrayList<>(scn.GetStackedCards(run));
        scn.MoveCardsToTopOfLSLostPile(han);
        for (int i = 0; i < 20 && scn.GetStackedCards(run).size() > 0; i++) {
            if (scn.LSDecisionAvailable("Choose card to put on Used Pile")) {
                for (var card : stacked) {
                    if (scn.LSHasCardChoiceAvailable(card)) {
                        scn.LSChooseCard(card);
                        break;
                    }
                }
            } else if (scn.DSAnyDecisionsAvailable()) {
                scn.DSPass();
            } else if (scn.LSAnyDecisionsAvailable()) {
                scn.LSPass();
            }
        }
        assertEquals(0, scn.GetStackedCards(run).size());
        assertEquals(5, scn.GetLSUsedPileCount() - usedBefore);
        assertEquals(null, run.getWhileInPlayData());
    }

    @Test
    public void WhenTargetReturnsToOriginalSystemMissionCompletedCountsTwiceFlipsInfiltrationAndMayTakeACard() {
        var scn = GetScenario();
        var run = scn.GetLSCard("run");
        var boshek = scn.GetLSCard("boshek");
        var infiltration = setUp(scn);
        scn.SkipToLSTurn(Phase.DEPLOY);
        target(scn);
        for (int i = 0; i < 5; i++) {
            scn.PrepareLSDestiny(1);
        }
        flyTo(scn, scn.GetLSCard("kessel"), Phase.BATTLE);
        assertEquals(5, scn.GetStackedCards(run).size());

        scn.MoveCardsToTopOfLSReserveDeck(boshek);
        scn.MoveCardsToLocation(scn.GetLSCard("tatooine"), scn.GetLSCard("falcon"));
        for (int i = 0; i < 60 && !scn.LSDecisionAvailable("Reserve Deck?"); i++) {
            if (scn.LSAnyDecisionsAvailable()) {
                scn.LSPass();
            } else if (scn.DSAnyDecisionsAvailable()) {
                scn.DSPass();
            }
        }
        assertTrue(scn.LSDecisionAvailable("Reserve Deck?"));
        scn.LSChooseYes();
        scn.LSChooseCard(boshek);
        scn.PassAllResponses();
        assertInZone(Zone.HAND, boshek);
        assertEquals("Unlikely Allies", infiltration.getBlueprint().getTitle());
        assertTrue(scn.game().getModifiersQuerying().hasCompletedUtinniEffect(scn.gameState(), scn.LS, 2, Filters.Kessel_Run));
    }

    @Test
    public void AfterCompletingWithParsecTotalUnderTwelveMaySubtractOneFromOpponentsDestinyDraws() {
        var scn = GetScenario();
        var run = scn.GetLSCard("run");
        setUp(scn);
        scn.SkipToLSTurn(Phase.DEPLOY);
        target(scn);
        for (int i = 0; i < 5; i++) {
            scn.PrepareLSDestiny(1);
        }
        flyTo(scn, scn.GetLSCard("kessel"), Phase.BATTLE);
        scn.MoveCardsToLocation(scn.GetLSCard("tatooine"), scn.GetLSCard("falcon"));
        for (int i = 0; i < 60 && !scn.LSDecisionAvailable("Reserve Deck?"); i++) {
            if (scn.LSAnyDecisionsAvailable()) {
                scn.LSPass();
            } else if (scn.DSAnyDecisionsAvailable()) {
                scn.DSPass();
            }
        }
        scn.LSChooseNo();
        scn.PassAllResponses();
        assertTrue(run.getWhileInPlayData().getTextValue().startsWith("COMPLETED:"));
        assertEquals(5, Float.parseFloat(run.getWhileInPlayData().getTextValue().substring("COMPLETED:".length())), scn.epsilon);
        assertTrue(scn.game().getModifiersQuerying().getModifiersAffectingCard(scn.gameState(), ModifierType.MAY_NOT_BE_CANCELED, run).size() > 0);
    }

    /**
     * Targets Han, flies to Kessel (five 'parsec' destinies of 1) and back to Tatooine, declining the [upload].
     */
    private void completeRun(VirtualTableScenario scn) {
        scn.SkipToLSTurn(Phase.DEPLOY);
        target(scn);
        for (int i = 0; i < 5; i++) {
            scn.PrepareLSDestiny(1);
        }
        flyTo(scn, scn.GetLSCard("kessel"), Phase.BATTLE);
        scn.MoveCardsToLocation(scn.GetLSCard("tatooine"), scn.GetLSCard("falcon"));
        for (int i = 0; i < 60 && !scn.LSDecisionAvailable("Reserve Deck?"); i++) {
            if (scn.LSAnyDecisionsAvailable()) {
                scn.LSPass();
            } else if (scn.DSAnyDecisionsAvailable()) {
                scn.DSPass();
            }
        }
        scn.LSChooseNo();
        scn.PassAllResponses();
    }

    @Test
    public void AfterCompletingUnderTwelveYouMaySubtractOneFromOpponentsBattleDestiny() {
        var scn = GetScenario();
        var run = scn.GetLSCard("run");
        var boshek = scn.GetLSCard("boshek");
        var site = scn.GetLSStartingLocation();
        setUp(scn);
        completeRun(scn);
        scn.MoveCardsToLocation(site, boshek, scn.GetDSCard("trooper"), scn.GetDSCard("vader"));
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.EnsureDSForcePile(2);
        scn.PrepareDSDestiny(4);
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();
        scn.SkipToPowerSegment();
        assertTrue(scn.DSDecisionAvailable("battle destiny?"));
        scn.DSChooseYes();
        boolean offered = false;
        for (int i = 0; i < 12; i++) {
            if (scn.LSAnyDecisionsAvailable() && scn.LSCardActionAvailable(run, "Subtract 1")) {
                offered = true;
                break;
            }
            if (scn.DSAnyDecisionsAvailable()) {
                scn.DSPass();
            } else if (scn.LSAnyDecisionsAvailable()) {
                scn.LSPass();
            } else {
                break;
            }
        }
        assertTrue(offered);
        scn.LSUseCardAction(run, "Subtract 1");
        scn.PassAllResponses();
    }

    @Test
    public void KesselRunIsLostIfFewerThanFiveParsecDestiniesCanBeDrawn() {
        var scn = GetScenario();
        var run = scn.GetLSCard("run");
        setUp(scn);
        scn.SkipToLSTurn(Phase.DEPLOY);
        target(scn);
        var extras = new ArrayList<PhysicalCardImpl>();
        for (var card : scn.GetLSReserveDeck()) {
            extras.add((PhysicalCardImpl) card);
        }
        for (int i = 0; i < extras.size() - 3; i++) {
            scn.MoveOutOfPlay(extras.get(i));
        }
        assertEquals(3, scn.GetLSReserveDeckCount());
        flyTo(scn, scn.GetLSCard("kessel"), Phase.BATTLE);
        assertInZone(Zone.LOST_PILE, run);
    }

    @Test
    public void TakeACardHereIntoHandWhenYouWinABattle() {
        var scn = GetScenario();
        var run = scn.GetLSCard("run");
        var boshek = scn.GetLSCard("boshek");
        var site = scn.GetLSStartingLocation();
        setUp(scn);
        scn.StackCardsOn(run, boshek);
        scn.SkipToLSTurn(Phase.DEPLOY);
        // the start-of-turn trigger already took Bo Shek; stack him again for the battle
        scn.StackCardsOn(run, boshek);
        scn.MoveCardsToLocation(site, scn.GetLSCard("han"), scn.GetDSCard("trooper"));
        scn.SkipToPhase(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();
        scn.SkipToDamageSegment(false);
        assertTrue(scn.LSWonBattle());
        for (int i = 0; i < 12 && boshek.getZone() != Zone.HAND; i++) {
            if (scn.LSAnyDecisionsAvailable()) {
                scn.LSPass();
            } else if (scn.DSAnyDecisionsAvailable()) {
                scn.DSPass();
            }
        }
        assertInZone(Zone.HAND, boshek);
    }
}
