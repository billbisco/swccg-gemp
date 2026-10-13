package com.gempukku.swccgo.cards.set225.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_225_043_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>()
                {{
                    put("n1", "225_043"); //Din Djarin's Modified N-1
                    put("din", "214_018"); //Din Djarin
                    put("pilot", "1_015"); //Kal'Falnl C'ndros (non-Din pilot)
                }},
                new HashMap<>()
                {{
                    put("tie", "1_304"); //TIE Fighter (permanent pilot)
                }},
                10,
                10,
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
    public void DinDjarinsModifiedN1StatsAndKeywordsAreCorrect() {
        /**
         * Title: Din Djarin's Modified N-1
         * Uniqueness: Unique
         * Side: Light
         * Type: Starship
         * Subtype: Starfighter: Modified N-1 Starfighter
         * Destiny: 3
         * Deploy: 3
         * Power: 3
         * Maneuver: 5
         * Hyperspeed: 3
         * Forfeit: 5
         * Icons: Nav Computer, Independent, Virtual Set 25
         * Game Text: May add 1 pilot and Grogu as a passenger. Din deploys -1 aboard. While Din piloting,
         *         immune to attrition < 5 and, once per game, may use 3 Force to cancel a battle just initiated here.
         * Set: Set 25
         * Rarity: V
         */
        var scn = GetScenario();
        var card = scn.GetLSCard("n1").getBlueprint();

        assertEquals("Din Djarin's Modified N-1", card.getTitle());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(3, card.getPower(), scn.epsilon);
        assertEquals(5, card.getManeuver(), scn.epsilon);
        assertEquals(3, card.getHyperspeed(), scn.epsilon);
        assertEquals(5, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.STARSHIP);
        }});
        assertEquals(CardSubtype.STARFIGHTER, card.getCardSubtype());
        scn.BlueprintModelTypeCheck(card, new ArrayList<>() {{
            add(ModelType.MODIFIED_N_1_STARFIGHTER);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.STARSHIP);
            add(Icon.NAV_COMPUTER);
            add(Icon.INDEPENDENT);
            add(Icon.VIRTUAL_SET_25);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>());
        assertEquals(ExpansionSet.SET_25, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void DinDjarinsModifiedN1UsesThreeForceToCancelBattleJustInitiatedHere() {
        //test1: with Din piloting, Light may cancel a battle just initiated here
        //test2: canceling costs 3 Force
        //test3: the battle is canceled
        var scn = GetScenario();
        var n1 = scn.GetLSCard("n1");
        var din = scn.GetLSCard("din");
        var tie = scn.GetDSCard("tie");
        var system = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(system, n1, tie);
        scn.BoardAsPilot(n1, din);

        scn.SkipToPhase(Phase.BATTLE);
        scn.MoveCardsToTopOfLSForcePile(scn.GetLSFiller(1), scn.GetLSFiller(2), scn.GetLSFiller(3), scn.GetLSFiller(4));
        int forceBefore = scn.GetLSForcePileCount();
        scn.DSUseCardAction(system, "Initiate battle");
        scn.PassForceUseResponses();

        scn.DSPass();
        assertTrue(scn.LSCardActionAvailable(n1, "cancel battle")); //test1
        scn.LSUseCardAction(n1, "cancel battle");
        scn.PassAllResponses();

        assertEquals(forceBefore - 3, scn.GetLSForcePileCount()); //test2
        assertFalse(scn.IsActiveBattle()); //test3
        assertTrue(scn.AwaitingLSBattlePhaseActions()); //test3: battle phase continues with no battle
    }

    @Test
    public void DinDjarinsModifiedN1CannotCancelBattleWithoutDinPiloting() {
        var scn = GetScenario();
        var n1 = scn.GetLSCard("n1");
        var pilot = scn.GetLSCard("pilot");
        var tie = scn.GetDSCard("tie");
        var system = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(system, n1, tie);
        scn.BoardAsPilot(n1, pilot);

        scn.SkipToPhase(Phase.BATTLE);
        scn.MoveCardsToTopOfLSForcePile(scn.GetLSFiller(1), scn.GetLSFiller(2), scn.GetLSFiller(3), scn.GetLSFiller(4));
        scn.DSUseCardAction(system, "Initiate battle");
        scn.PassForceUseResponses();

        scn.DSPass();
        assertFalse(scn.LSCardActionAvailable(n1, "cancel battle"));
    }

    @Test
    public void DinDjarinsModifiedN1CancelBattleIsOncePerGame() {
        //test1: first battle may be canceled
        //test2: a later battle here may not be canceled
        var scn = GetScenario();
        var n1 = scn.GetLSCard("n1");
        var din = scn.GetLSCard("din");
        var tie = scn.GetDSCard("tie");
        var system = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(system, n1, tie);
        scn.BoardAsPilot(n1, din);

        scn.SkipToPhase(Phase.BATTLE);
        scn.MoveCardsToTopOfLSForcePile(scn.GetLSFiller(1), scn.GetLSFiller(2), scn.GetLSFiller(3), scn.GetLSFiller(4));
        scn.DSUseCardAction(system, "Initiate battle");
        scn.PassForceUseResponses();
        scn.DSPass();
        assertTrue(scn.LSCardActionAvailable(n1, "cancel battle")); //test1
        scn.LSUseCardAction(n1, "cancel battle");
        scn.PassAllResponses();

        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSUseCardAction(system, "Initiate battle");
        scn.PassForceUseResponses();
        scn.DSPass();
        assertFalse(scn.LSCardActionAvailable(n1, "cancel battle")); //test2
    }
}
