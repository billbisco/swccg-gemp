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
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1291_043_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("bel", "1291_43");
                    put("corvette", "1_140"); // Corellian Corvette (no [Episode I] icon)
                    put("xwing", "1_146"); // Red 5: not a corvette
                    put("corellia", "2_61"); // Corellia system
                }},
                new HashMap<>() {{
                    put("tie", "1_304");
                    put("dreadnaught", "106_13"); // Dreadnaught-Class Heavy Cruiser
                    put("tie2", "1_304");
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
    public void GeneralBelIblisStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("bel").getBlueprint();
        assertEquals("General Bel Iblis", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(1, card.getDestiny(), scn.epsilon);
        assertEquals(4, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(4, card.getAbility(), scn.epsilon);
        assertEquals(7, card.getForfeit(), scn.epsilon);
        assertEquals("Insurgent leader. Corellian.", card.getLore());
        assertEquals(Species.CORELLIAN, card.getSpecies());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.NEW_REPUBLIC);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.GENERAL);
            add(Keyword.LEADER);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.NEW_REPUBLIC);
            add(Icon.PILOT);
            add(Icon.LEGACY_BLOCK_9);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_9_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    private void battleAtLSSystem(VirtualTableScenario scn) {
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(scn.GetLSStartingLocation());
        scn.PassAllResponses();
        scn.SkipToPowerSegment();
    }

    @Test
    public void GeneralBelIblisPilotingNonEpisodeICorvetteAddsPowerBattleDestinyAndImmunity() {
        var scn = GetScenario();
        var bel = scn.GetLSCard("bel");
        var corvette = scn.GetLSCard("corvette");
        var tie = scn.GetDSCard("tie");
        var system = scn.GetLSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(system, corvette, tie);
        int power = scn.GetPower(corvette);
        scn.BoardAsPilot(corvette, bel);
        assertEquals(power + 3, scn.GetPower(corvette));
        assertEquals(5, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), corvette), scn.epsilon);
        battleAtLSSystem(scn);
        assertEquals(2, scn.GetLSBattleDestinyCount());
    }

    @Test
    public void GeneralBelIblisPilotingStarfighterAddsOnlyPower() {
        var scn = GetScenario();
        var bel = scn.GetLSCard("bel");
        var xwing = scn.GetLSCard("xwing");
        var tie = scn.GetDSCard("tie");
        var system = scn.GetLSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(system, xwing, tie);
        int power = scn.GetPower(xwing);
        scn.BoardAsPilot(xwing, bel);
        assertEquals(power + 3, scn.GetPower(xwing));
        assertEquals(0, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), xwing), scn.epsilon);
        battleAtLSSystem(scn);
        assertEquals(1, scn.GetLSBattleDestinyCount());
    }

    @Test
    public void GeneralBelIblisPilotingStolenStarshipIsImmuneToAttritionLessThanFive() {
        var scn = GetScenario();
        var bel = scn.GetLSCard("bel");
        var tie2 = scn.GetDSCard("tie2");
        var system = scn.GetLSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(system, tie2);
        tie2.setOwner(scn.LS); // a stolen starship
        scn.BoardAsPilot(tie2, bel);
        assertEquals(5, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), tie2), scn.epsilon);
    }

    @Test
    public void GeneralBelIblisIsExiledAtCorellia() {
        var scn = GetScenario();
        var bel = scn.GetLSCard("bel");
        var corvette = scn.GetLSCard("corvette");
        var corellia = scn.GetLSCard("corellia");
        scn.StartGame();
        scn.MoveLocationToTable(corellia);
        scn.MoveCardsToLocation(corellia, corvette);
        scn.MoveCardsToHand(bel);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSDeployAvailable(bel));
        scn.LSDeployCard(bel);
        if (Vb91LightTestSupport.lsCardChoiceCount(scn) > 0) {
            scn.LSChooseCard(corvette);
        }
        if (scn.LSDecisionAvailable("capacity slot")) {
            scn.LSChoose("Pilot");
        }
        Vb91LightTestSupport.reach(scn, () -> bel.getZone() == Zone.OUT_OF_PLAY);
        assertInZone(Zone.OUT_OF_PLAY, bel);
    }

    @Test
    public void GeneralBelIblisOncePerGameStealsDreadnaughtFromOpponentsLostPile() {
        var scn = GetScenario();
        var bel = scn.GetLSCard("bel");
        var corvette = scn.GetLSCard("corvette");
        var dreadnaught = scn.GetDSCard("dreadnaught");
        var tie = scn.GetDSCard("tie");
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), corvette);
        scn.BoardAsPilot(corvette, bel);
        scn.MoveCardsToTopOfDSLostPile(tie);
        scn.MoveCardsToTopOfDSLostPile(dreadnaught);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSCardActionAvailable(bel, "Steal a Dreadnaught"));
        scn.LSUseCardAction(bel, "Steal a Dreadnaught");
        Vb91LightTestSupport.reach(scn, () -> Vb91LightTestSupport.lsCardChoiceCount(scn) > 0);
        scn.LSChooseCard(dreadnaught);
        scn.PassAllResponses();
        assertInZone(Zone.HAND, dreadnaught);
        assertEquals(scn.LS, dreadnaught.getOwner());
        Vb91LightTestSupport.reach(scn, scn::AwaitingLSDeployPhaseActions);
        assertFalse(scn.LSCardActionAvailable(bel, "Steal a Dreadnaught"));
    }
}
