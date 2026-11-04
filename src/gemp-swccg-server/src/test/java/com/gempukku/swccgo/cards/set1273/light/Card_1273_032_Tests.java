package com.gempukku.swccgo.cards.set1273.light;

import com.gempukku.swccgo.cards.legacy.MissionTestSupport;
import com.gempukku.swccgo.cards.set1281.dark.Vb81RestTestSupport;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1273_032_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("bacta", "1273_32");
                    put("insurrection", "8_040");
                    put("luke", "1273_26");
                    put("rebel", "1_028");
                }},
                new HashMap<>() {{
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

    @Test
    public void BactaInfirmaryStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("bacta").getBlueprint();

        assertEquals("Bacta Infirmary", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);
            add(Icon.LEGACY_BLOCK_7);
        }});
        assertTrue(card.isImmuneToCardTitle(Title.Alter));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void BactaInfirmaryDeploysOnlyIfCenterOfTyrannyOnTableAndInsurrectionIsLost() {
        var scn = GetScenario();
        var bacta = scn.GetLSCard("bacta");
        var insurrection = scn.GetLSCard("insurrection");

        scn.StartGame();
        scn.MoveCardsToLSHand(bacta);
        scn.MoveCardsToLSSideOfTable(insurrection);
        scn.LSActivateForceCheat(5);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertFalse(scn.LSDeployAvailable(bacta));

        Vb73LightTestSupport.putOnTable(scn, scn.LS, "1273_42");
        scn.SkipToNextTurn(scn.LS);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSDeployAvailable(bacta));
        scn.LSDeployCard(bacta);
        scn.PassAllResponses();
        assertEquals(Zone.SIDE_OF_TABLE, bacta.getZone());
        assertTrue(Vb81RestTestSupport.inZone(insurrection, Zone.LOST_PILE));
    }

    @Test
    public void BactaInfirmaryRogueSquadronPilotsOfAbilityLessThanThreeAreForfeitPlusOne() {
        var scn = GetScenario();
        var bacta = scn.GetLSCard("bacta");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        var pilot = MissionTestSupport.putInReserve(scn, 97320, scn.LS, Vb73LightTestSupport.rogueSquadronPilot());
        scn.MoveCardsToLocation(site, pilot, luke);
        int pilotForfeit = scn.GetForfeit(pilot);
        int lukeForfeit = scn.GetForfeit(luke);
        scn.MoveCardsToLSSideOfTable(bacta);
        assertEquals(pilotForfeit + 1, scn.GetForfeit(pilot));
        assertEquals(lukeForfeit, scn.GetForfeit(luke));
    }

    @Test
    public void BactaInfirmaryRogueSquadronPilotJustLostMayBePlacedHere() {
        var scn = GetScenario();
        var bacta = scn.GetLSCard("bacta");
        var vader = scn.GetDSCard("vader");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        var pilot = MissionTestSupport.putInReserve(scn, 97321, scn.LS, Vb73LightTestSupport.rogueSquadronPilot());
        scn.MoveCardsToLSSideOfTable(bacta);
        scn.MoveCardsToLocation(site, pilot, vader);
        scn.SkipToPhase(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();
        scn.SkipToEndOfPowerSegment(false);
        scn.PassAllResponses();
        assertTrue(scn.AwaitingLSBattleDamagePayment());
        scn.LSChooseCard(pilot);
        assertTrue(Vb81RestTestSupport.passUntil(scn, () -> scn.LSActionAvailable("Place")));
        scn.LSChooseAction(scn.GetLSAvailableActions().stream().filter(s -> s.startsWith("Place")).findFirst().get());
        scn.PassAllResponses();
        assertTrue(scn.IsStackedOn(bacta, pilot));
    }

    @Test
    public void BactaInfirmaryControlPhaseUsesTwoForceToTakeCharacterHereIntoHand() {
        var scn = GetScenario();
        var bacta = scn.GetLSCard("bacta");
        var rebel = scn.GetLSCard("rebel");

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(bacta);
        scn.StackCardsOn(bacta, rebel);
        scn.LSActivateForceCheat(3);
        scn.SkipToLSTurn(Phase.CONTROL);
        int force = scn.GetLSForcePileCount();
        assertTrue(scn.LSCardActionAvailable(bacta, "Take character here into hand"));
        scn.LSUseCardAction(bacta, "Take character here into hand");
        if (scn.LSHasCardChoiceAvailable(rebel)) {
            scn.LSChooseCard(rebel);
        }
        scn.PassAllResponses();
        assertEquals(Zone.HAND, rebel.getZone());
        assertEquals(force - 2, scn.GetLSForcePileCount());
    }
}
