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
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static com.gempukku.swccgo.framework.Assertions.assertInHand;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class Card_1230_112_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("gs1", "1230_112");
                    put("greenleader", "9_16");
                    put("tactics", "13_14");
                    put("wrongpilot", "1_11"); put("corvette", "1_140");
                }},
                new HashMap<>() {{
                    put("vader", "1_168");
                    put("executor", "4_167"); put("other", "1_301");
                }},
                30, 30,
                StartingSetup.DefaultLSSpaceSystem,
                StartingSetup.DefaultDSSpaceSystem,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void GreenSquadron1StatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("gs1").getBlueprint();

        assertEquals("Green Squadron 1", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(3, card.getPower(), scn.epsilon);
        assertEquals(5, card.getManeuver(), scn.epsilon);
        assertEquals(4, card.getHyperspeed(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        assertEquals(1, card.getPilotCapacity());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.STARSHIP);
        }});
        assertEquals(CardSubtype.STARFIGHTER, card.getCardSubtype());
        scn.BlueprintModelTypeCheck(card, new ArrayList<>() {{
            add(ModelType.A_WING);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.GREEN_SQUADRON);
        }});
        scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
            add(Persona.GREEN_SQUADRON_1);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.STARSHIP);
            add(Icon.LEGACY_BLOCK_3);
            add(Icon.DEATH_STAR_II);
            add(Icon.NAV_COMPUTER);
            add(Icon.SCOMP_LINK);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test public void GreenSquadron1AllowsOnlyGreenLeaderToDeployAsPilot() {
        var scn = GetScenario(); scn.StartGame(); scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("gs1"));
        scn.MoveCardsToLSHand(scn.GetLSCard("greenleader"), scn.GetLSCard("wrongpilot")); scn.EnsureLSForcePile(10); scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSDeployAvailable(scn.GetLSCard("greenleader"))); assertFalse(scn.LSDeployAvailable(scn.GetLSCard("wrongpilot")));
        scn.LSDeployCard(scn.GetLSCard("greenleader")); scn.LSChooseCard(scn.GetLSCard("gs1")); scn.PassAllResponses();
        assertTrue(scn.IsAboardAsPilot(scn.GetLSCard("gs1"), scn.GetLSCard("greenleader")));
    }
    @Test public void GreenSquadron1SearchIsFreeOncePerTurnAndResetsNextTurn() {
        var scn = GetScenario(); scn.StartGame(); var ship = scn.GetLSCard("gs1"); var tactics = scn.GetLSCard("tactics");
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), ship); scn.BoardAsPilot(ship, scn.GetLSCard("greenleader"));
        scn.SkipToLSTurn(Phase.CONTROL); scn.MoveCardsToTopOfLSReserveDeck(tactics); int force = scn.GetLSForcePileCount();
        scn.LSUseCardAction(ship, "Take Diversionary Tactics"); scn.PassAllResponses(); scn.PassCardAndForceUseResponses(); scn.LSChooseCard(tactics); scn.PassAllResponses();
        assertInHand(tactics); assertEquals(force, scn.GetLSForcePileCount());
        if (scn.AwaitingDSControlPhaseActions()) scn.DSPass(); assertTrue(scn.AwaitingLSControlPhaseActions());
        assertFalse(scn.LSCardActionAvailable(ship, "Take Diversionary Tactics"));
        scn.SkipToLSTurn(Phase.CONTROL); scn.MoveCardsToTopOfLSReserveDeck(tactics);
        assertTrue(scn.LSCardActionAvailable(ship, "Take Diversionary Tactics"));
        scn.LSUseCardAction(ship, "Take Diversionary Tactics"); scn.PassAllResponses(); scn.PassCardAndForceUseResponses(); scn.LSChooseCard(tactics); scn.PassAllResponses(); assertInHand(tactics);
    }
    @Test public void GreenSquadron1EndBattleHighDestinyLosesBothShipsAndAttachedPilot() {
        var scn = EndBattle(true); scn.PrepareLSDestiny(6);
        scn.LSUseCardAction(scn.GetLSCard("gs1"), "Draw destiny"); scn.PassAllResponses();
        assertTrue(scn.DSDecisionAvailable("Choose card to be lost"));
        scn.DSChooseCard(scn.GetDSCard("executor")); scn.PassAllResponses();
        assertInZone(Zone.LOST_PILE, scn.GetDSCard("executor"));
        assertTrue(scn.LSDecisionAvailable("Choose card to put on Lost Pile") || scn.DSDecisionAvailable("Choose card to put on Lost Pile"));
        if (scn.LSDecisionAvailable("Choose card to put on Lost Pile")) scn.LSChooseCard(scn.GetLSCard("gs1"));
        else scn.DSChooseCard(scn.GetLSCard("gs1"));
        scn.PassAllResponses();
        assertInZone(Zone.LOST_PILE, scn.GetLSCard("gs1")); assertInZone(Zone.LOST_PILE, scn.GetDSCard("executor"));
        assertInZone(Zone.LOST_PILE, scn.GetLSCard("greenleader"));
        assertNull(scn.gameState().getBattleState());
    }
    @Test public void GreenSquadron1EndBattleDestinyFiveLosesNeitherShip() {
        var scn = EndBattle(true); scn.PrepareLSDestiny(5);
        scn.LSUseCardAction(scn.GetLSCard("gs1"), "Draw destiny"); scn.PassAllResponses();
        assertAtLocation(scn.GetLSStartingLocation(), scn.GetLSCard("gs1"), scn.GetDSCard("executor"));
        assertNull(scn.gameState().getBattleState());
    }
    @Test public void GreenSquadron1DoesNotOfferSacrificeAgainstAnotherImperialShip() {
        var scn = EndBattle(false); assertTrue(scn.AwaitingDSBattlePhaseActions() || scn.AwaitingLSBattlePhaseActions()); assertNull(scn.gameState().getBattleState());
    }
    @Test public void GreenSquadron1MayDeclineEndBattleDestiny() {
        var scn = EndBattle(true); assertTrue(scn.LSCardActionAvailable(scn.GetLSCard("gs1"), "Draw destiny"));
        scn.LSPass(); scn.PassAllResponses(); assertAtLocation(scn.GetLSStartingLocation(), scn.GetLSCard("gs1"), scn.GetDSCard("executor"));
        assertNull(scn.gameState().getBattleState());
    }
    @Test public void GreenSquadron1CannotSacrificeWhenAtAnotherSystem() {
        var scn = EndBattle(true, false); assertTrue(scn.AwaitingDSBattlePhaseActions() || scn.AwaitingLSBattlePhaseActions());
        assertNull(scn.gameState().getBattleState()); assertAtLocation(scn.GetDSStartingLocation(), scn.GetLSCard("gs1"));
    }
    private VirtualTableScenario EndBattle(boolean executor) { return EndBattle(executor, true); }
    private VirtualTableScenario EndBattle(boolean executor, boolean participates) {
        var scn = GetScenario(); scn.StartGame(); var ship = scn.GetLSCard("gs1"); var system = scn.GetLSStartingLocation();
        scn.MoveCardsToLocation(system, scn.GetLSCard("corvette"), scn.GetDSCard(executor ? "executor" : "other"));
        scn.MoveCardsToLocation(participates ? system : scn.GetDSStartingLocation(), ship);
        scn.BoardAsPilot(ship, scn.GetLSCard("greenleader")); scn.EnsureDSForcePile(3); scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(system); scn.SkipToDamageSegment(false); scn.PassAllResponses();
        if (scn.AwaitingLSBattleDamagePayment()) scn.LSPayRemainingBattleDamageFromReserveDeck();
        if (scn.AwaitingDSBattleDamagePayment()) scn.DSPayRemainingBattleDamageFromReserveDeck();
        scn.PassResponses("BATTLE_DAMAGE_RESOLVED");
        if (scn.DSDecisionAvailable("Optional responses")) scn.DSPass();
        if (executor && participates) assertTrue(scn.LSDecisionAvailable("Optional responses"));
        return scn;
    }




}
