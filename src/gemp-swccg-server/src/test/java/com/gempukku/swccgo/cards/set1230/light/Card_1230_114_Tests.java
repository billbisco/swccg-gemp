package com.gempukku.swccgo.cards.set1230.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1230_114_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("tydirium", "1230_114");
            put("han", "1_11");
            put("pilot", "2_23");
            put("scout", "8_10");
            put("xwing", "1_146");
            put("corvette", "1_140");
        }}, new HashMap<>() {{
            put("deathStar", "2_143");
            put("bay", "7_276");
            put("devastator", "1_301");
            put("vader", "1_168");
        }}, 30, 30, StartingSetup.DefaultLSSpaceSystem, StartingSetup.DefaultDSSpaceSystem,
        StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
        StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void TydiriumStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("tydirium").getBlueprint();
        assertEquals("Tydirium", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(2, card.getManeuver(), scn.epsilon);
        assertEquals(3, card.getHyperspeed(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        assertEquals(2, card.getPilotCapacity());
        assertEquals(6, card.getPassengerCapacity());
        assertEquals(CardSubtype.STARFIGHTER, card.getCardSubtype());
        scn.BlueprintModelTypeCheck(card, new ArrayList<>() {{ add(ModelType.LAMBDA_CLASS_SHUTTLE); }});
        assertTrue(card.isAlwaysStolen());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.STARSHIP);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.ENDOR);
            add(Icon.LEGACY_BLOCK_3);
            add(Icon.NAV_COMPUTER);
            add(Icon.SCOMP_LINK);
            add(Icon.STARSHIP);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }


    @Test
    public void TydiriumDeploysWithPilotWithoutPresenceOrForceIconsForNormalCost() {
        var scn = GetScenario();
        scn.StartGame();
        scn.MoveLocationToTable(scn.GetDSCard("deathStar"));
        scn.MoveCardsToLSHand(scn.GetLSCard("tydirium"), scn.GetLSCard("han"));
        scn.EnsureLSForcePile(10);
        scn.SkipToLSTurn(Phase.DEPLOY);
        int force = scn.GetLSForcePileCount();
        assertEquals(0, scn.GetIconCount(scn.GetDSCard("deathStar"), Icon.LIGHT_FORCE));
        scn.LSDeployCard(scn.GetLSCard("tydirium"));
        assertTrue(scn.LSDecisionAvailable("simultaneously deploy aboard"));
        scn.LSChooseCard(scn.GetLSCard("han"));
        scn.LSChooseCard(scn.GetDSCard("deathStar"));
        scn.PassAllResponses();
        assertAtLocation(scn.GetDSCard("deathStar"), scn.GetLSCard("tydirium"));
        assertTrue(scn.IsAboardAsPilot(scn.GetLSCard("tydirium"), scn.GetLSCard("han")));
        assertEquals(force - 5, scn.GetLSForcePileCount());
    }

    @Test
    public void TydiriumDeploysAloneToDockingBayButNotUnpilotedToSystem() {
        var scn = GetScenario();
        scn.StartGame();
        scn.MoveLocationToTable(scn.GetDSCard("deathStar"));
        scn.MoveLocationToTable(scn.GetDSCard("bay"));
        scn.MoveCardsToLSHand(scn.GetLSCard("tydirium"));
        scn.EnsureLSForcePile(5);
        scn.SkipToLSTurn(Phase.DEPLOY);
        int force = scn.GetLSForcePileCount();
        scn.LSDeployCard(scn.GetLSCard("tydirium"));
        assertFalse(scn.LSHasCardChoiceAvailable(scn.GetDSCard("deathStar")));
        assertTrue(scn.LSHasCardChoiceAvailable(scn.GetDSCard("bay")));
        scn.LSChooseCard(scn.GetDSCard("bay"));
        scn.PassAllResponses();
        assertAtLocation(scn.GetDSCard("bay"), scn.GetLSCard("tydirium"));
        assertEquals(force - 2, scn.GetLSForcePileCount());
    }

    @Test
    public void TydiriumForfeitsScoutToCancelRemainingBattleDamageWithoutCancelingAttrition() {
        var scn = DamageReady(true, false);
        int attrition = scn.GetUnpaidLSAttrition();
        int forfeit = scn.GetForfeit(scn.GetLSCard("scout"));
        assertTrue(attrition > forfeit);
        assertTrue(scn.GetUnpaidLSBattleDamage() > forfeit);
        scn.LSUseCardAction(scn.GetLSCard("tydirium"), "Forfeit a scout aboard");
        assertTrue(scn.LSHasCardChoiceAvailable(scn.GetLSCard("scout")));
        assertFalse(scn.LSHasCardChoiceAvailable(scn.GetLSCard("pilot")));
        scn.LSChooseCard(scn.GetLSCard("scout"));
        scn.PassAllResponses();
        com.gempukku.swccgo.framework.Assertions.assertInZone(Zone.LOST_PILE, scn.GetLSCard("scout"));
        assertEquals(0, scn.GetUnpaidLSBattleDamage());
        assertEquals(attrition - forfeit, scn.GetUnpaidLSAttrition());
    }

    @Test
    public void TydiriumCannotCancelDamageWithOnlyNonScoutPilotAboard() {
        var scn = DamageReady(false, false);
        assertTrue(scn.GetUnpaidLSBattleDamage() > 0);
        assertTrue(scn.AwaitingLSAttritionPayment());
        assertFalse(scn.LSDecisionAvailable("Optional responses"));
    }

    @Test
    public void TydiriumCannotCancelBattleDamageAtAnotherSystem() {
        var scn = DamageReady(true, true);
        assertTrue(scn.GetUnpaidLSBattleDamage() > 0);
        assertTrue(scn.AwaitingLSAttritionPayment());
        assertFalse(scn.LSDecisionAvailable("Optional responses"));
    }

    @Test
    public void TydiriumCannotForfeitScoutAboardAnotherFriendlyShip() {
        var scn = DamageReady(2, false);
        assertTrue(scn.GetUnpaidLSBattleDamage() > 0);
        assertTrue(scn.AwaitingLSAttritionPayment());
        assertFalse(scn.LSDecisionAvailable("Optional responses"));
    }

    private VirtualTableScenario DamageReady(boolean scout, boolean elsewhere) {
        return DamageReady(scout ? 1 : 0, elsewhere);
    }

    private VirtualTableScenario DamageReady(int scoutPlacement, boolean elsewhere) {
        var scn = GetScenario();
        scn.StartGame();
        var system = scn.GetLSStartingLocation();
        scn.MoveCardsToLocation(elsewhere ? scn.GetDSStartingLocation() : system, scn.GetLSCard("tydirium"));
        assertTrue(scn.GetLSCard("pilot").getBlueprint().hasIcon(Icon.PILOT));
        scn.BoardAsPilot(scn.GetLSCard("tydirium"), scn.GetLSCard("pilot"));
        if (scoutPlacement == 1) scn.BoardAsPassenger(scn.GetLSCard("tydirium"), scn.GetLSCard("scout"));
        if (scoutPlacement == 2) {
            scn.MoveCardsToLocation(system, scn.GetLSCard("corvette"));
            scn.BoardAsPassenger(scn.GetLSCard("corvette"), scn.GetLSCard("scout"));
        }
        scn.MoveCardsToLocation(system, scn.GetDSCard("devastator"));
        scn.BoardAsPilot(scn.GetDSCard("devastator"), scn.GetDSCard("vader"));
        if (elsewhere) {
            // A separate piloted ship makes the other-system battle legal.
            scn.MoveCardsToLocation(system, scn.GetLSCard("xwing"));
        }
        scn.EnsureLSForcePile(5);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(system);
        scn.PrepareLSDestiny(0);
        scn.PrepareDSDestiny(6);
        scn.SkipToDamageSegment(true);
        if (scn.DSDecisionAvailable("Optional responses")) scn.DSPass();
        return scn;
    }
}
