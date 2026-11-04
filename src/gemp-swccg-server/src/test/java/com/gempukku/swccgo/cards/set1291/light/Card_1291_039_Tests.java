package com.gempukku.swccgo.cards.set1291.light;

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

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1291_039_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("adar", "1291_39");
                    put("bravo2", "14_54"); // Bravo 2: unique [Republic] starfighter, power 2, maneuver 4
                    put("n1", "12_90"); // Naboo Defense Fighter: non-unique [Republic] starfighter, power 2, maneuver 4
                    put("luke", "1_19");
                    put("xwing", "1_146"); // Red 5
                }},
                new HashMap<>() {{
                    put("black3", "1210_74"); // Black 3 (V): Tallon Roll may target a [Republic] starfighter
                    put("ds613", "1_174");
                    put("tie", "1_304");
                    put("tallon", "1_270");
                }},
                10, 10,
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
    public void CommanderAdarTallonStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("adar").getBlueprint();
        assertEquals("Commander Adar Tallon", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(3, card.getAbility(), scn.epsilon);
        assertEquals(5, card.getForfeit(), scn.epsilon);
        assertEquals("Leader.", card.getLore());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.REPUBLIC);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.COMMANDER);
            add(Keyword.LEADER);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.REPUBLIC);
            add(Icon.PILOT);
            add(Icon.WARRIOR);
            add(Icon.EPISODE_I);
            add(Icon.LEGACY_BLOCK_9);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_9_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void CommanderAdarTallonIsMatchingPilotForUniqueRepublicStarfighterOnly() {
        var scn = GetScenario();
        var adar = scn.GetLSCard("adar");
        var bravo2 = scn.GetLSCard("bravo2");
        var n1 = scn.GetLSCard("n1");
        scn.StartGame();
        assertTrue(scn.IsMatchingPilot(bravo2, adar));
        assertFalse(scn.IsMatchingPilot(n1, adar));
    }

    @Test
    public void CommanderAdarTallonAddsPowerAndRepublicStarfightersHereArePowerAndManeuverPlusOne() {
        var scn = GetScenario();
        var adar = scn.GetLSCard("adar");
        var bravo2 = scn.GetLSCard("bravo2");
        var n1 = scn.GetLSCard("n1");
        var system = scn.GetLSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(system, bravo2, n1);
        assertEquals(2, scn.GetPower(n1));
        assertEquals(4, scn.GetManeuver(n1));
        scn.BoardAsPilot(bravo2, adar);
        // Bravo 2: power 2 +2 (Adar pilots) +1; maneuver 4 +1
        assertEquals(5, scn.GetPower(bravo2));
        assertEquals(5, scn.GetManeuver(bravo2));
        assertEquals(3, scn.GetPower(n1));
        assertEquals(5, scn.GetManeuver(n1));
    }

    private void battleAtLSSystem(VirtualTableScenario scn) {
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(scn.GetLSStartingLocation());
        scn.PassAllResponses();
        scn.SkipToPowerSegment();
    }

    @Test
    public void CommanderAdarTallonAddsBattleDestinyWhilePilotingUniqueRepublicStarfighter() {
        var scn = GetScenario();
        var adar = scn.GetLSCard("adar");
        var bravo2 = scn.GetLSCard("bravo2");
        var luke = scn.GetLSCard("luke");
        var xwing = scn.GetLSCard("xwing");
        var tie = scn.GetDSCard("tie");
        var system = scn.GetLSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(system, bravo2, xwing, tie);
        scn.BoardAsPilot(bravo2, adar);
        scn.BoardAsPilot(xwing, luke);
        battleAtLSSystem(scn);
        assertEquals(2, scn.GetLSBattleDestinyCount());
    }

    @Test
    public void CommanderAdarTallonAddsNoBattleDestinyWhilePilotingNonUniqueStarfighter() {
        var scn = GetScenario();
        var adar = scn.GetLSCard("adar");
        var n1 = scn.GetLSCard("n1");
        var luke = scn.GetLSCard("luke");
        var xwing = scn.GetLSCard("xwing");
        var tie = scn.GetDSCard("tie");
        var system = scn.GetLSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(system, n1, xwing, tie);
        scn.BoardAsPilot(n1, adar);
        scn.BoardAsPilot(xwing, luke);
        battleAtLSSystem(scn);
        assertEquals(1, scn.GetLSBattleDestinyCount());
    }

    @Test
    public void CommanderAdarTallonRepublicStarfighterAddsPilotAbilityWhenTargetedByTallonRoll() {
        var scn = GetScenario();
        var adar = scn.GetLSCard("adar");
        var bravo2 = scn.GetLSCard("bravo2");
        var black3 = scn.GetDSCard("black3");
        var ds613 = scn.GetDSCard("ds613");
        var tallon = scn.GetDSCard("tallon");
        var system = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(system, black3, bravo2);
        scn.BoardAsPilot(black3, ds613);
        scn.BoardAsPilot(bravo2, adar);
        scn.MoveCardsToDSHand(tallon);
        int lsBase = scn.GetPower(bravo2); // ability of pilot (3) is added on top of this
        int dsBase = scn.GetPower(black3) + scn.GetManeuver(black3);
        // Light Side total without ability = lsDestiny + lsBase; with ability = lsDestiny + lsBase + 3.
        // Dark Side total is set one above the 'without ability' total, so only the added ability wins.
        int dsDestiny = Math.max(0, lsBase + 1 - dsBase);
        int lsDestiny = dsDestiny + dsBase - lsBase - 1;
        assertTrue("destiny " + dsDestiny + "/" + lsDestiny, dsDestiny <= 7 && lsDestiny >= 0 && lsDestiny <= 7);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.PrepareDSDestiny(dsDestiny);
        scn.PrepareLSDestiny(lsDestiny);
        assertTrue(scn.DSCardPlayAvailable(tallon));
        scn.DSPlayCard(tallon);
        scn.DSChooseCard(black3);
        scn.DSChooseCard(bravo2);
        scn.PassAllResponses();
        Vb91LightTestSupport.reach(scn, () -> scn.DSDecisionAvailable("put on Lost Pile") || bravo2.getZone() == Zone.LOST_PILE);
        for (int i = 0; i < 3 && scn.DSDecisionAvailable("put on Lost Pile"); ++i) {
            scn.DSChooseCard(scn.DSHasCardChoiceAvailable(black3) ? black3 : ds613);
        }
        scn.PassAllResponses();
        assertAtLocation(system, bravo2);
        assertInZone(Zone.LOST_PILE, black3);
    }
}
