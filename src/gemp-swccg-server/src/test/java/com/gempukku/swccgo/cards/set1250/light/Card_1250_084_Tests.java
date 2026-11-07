package com.gempukku.swccgo.cards.set1250.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Keyword;
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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class Card_1250_084_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("corvette", "1250_84");
                    put("tatooine", "1_127");
                    put("kessel", "1_126");
                    put("cannon", "1_159");
                    put("pilot", "1116_8");
                    put("ordinaryCorvette", "1_140");
                    put("pilot1", "1116_8");
                    put("pilot2", "1116_8");
                    put("pilot3", "1116_8");
                    put("passenger1", "1_28");
                    put("passenger2", "1_28");
                    put("passenger3", "1_28");
                    put("passenger4", "1_28");
                    put("rebelPilot", "1_3");

                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                    put("devastator", "1_301");
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
    public void RepublicCorvetteStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("corvette").getBlueprint();

        assertEquals("Republic Corvette", card.getTitle());
        assertEquals("", card.getLore());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(1, card.getDestiny(), scn.epsilon);
        assertEquals(5, card.getDeployCost(), scn.epsilon);
        assertEquals(5, card.getPower(), scn.epsilon);
        assertEquals(4, card.getArmor(), scn.epsilon);
        assertNull(card.getManeuver());
        assertEquals(3, card.getHyperspeed(), scn.epsilon);
        assertEquals(8, card.getForfeit(), scn.epsilon);
        assertEquals(3, card.getPilotCapacity());
        assertEquals(4, card.getPassengerCapacity());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.STARSHIP);
        }});
        assertEquals(CardSubtype.CAPITAL, card.getCardSubtype());
        scn.BlueprintModelTypeCheck(card, new ArrayList<>() {{
            add(ModelType.CORELLIAN_CORVETTE);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.STARSHIP);
            add(Icon.LEGACY_BLOCK_5);
            add(Icon.EPISODE_I);
            add(Icon.REPUBLIC);
            add(Icon.PILOT);
            add(Icon.NAV_COMPUTER);
            add(Icon.SCOMP_LINK);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_5, card.getExpansionSet());
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{ add(Keyword.SHIP_DOCKING_CAPABILITY); }});
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void RepublicCorvettePowerBonusRequiresImperialClassStarDestroyerAtSameSystem() {
        var scn = GetScenario();
        var corvette = scn.GetLSCard("corvette");
        var destroyer = scn.GetDSCard("devastator");
        var tatooine = scn.GetLSCard("tatooine");
        var kessel = scn.GetLSCard("kessel");
        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveLocationToTable(kessel);
        scn.MoveCardsToLocation(tatooine, corvette);
        scn.MoveCardsToLocation(kessel, destroyer);
        assertEquals(5, scn.GetPower(corvette));
        scn.MoveCardsToLocation(tatooine, destroyer);
        assertEquals(6, scn.GetPower(corvette));
        scn.MoveOutOfPlay(destroyer);
        assertEquals(5, scn.GetPower(corvette));
        assertEquals(2, scn.game().getModifiersQuerying().getHighestAbilityPiloting(
                scn.gameState(), corvette, corvette, true, false), scn.epsilon);
    }

    @Test
    public void RepublicCorvetteFullCapacityRejectsFourthPilot() {
        var scn = GetScenario();
        var corvette = scn.GetLSCard("corvette");
        var pilot = scn.GetLSCard("pilot");
        var tatooine = scn.GetLSCard("tatooine");
        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, corvette);
        scn.BoardAsPilot(corvette, scn.GetLSCard("pilot1"), scn.GetLSCard("pilot2"), scn.GetLSCard("pilot3"));
        scn.BoardAsPassenger(corvette, scn.GetLSCard("passenger1"), scn.GetLSCard("passenger2"),
                scn.GetLSCard("passenger3"), scn.GetLSCard("passenger4"));
        scn.MoveCardsToHand(pilot);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSDeployCard(pilot);
        assertFalse(scn.LSGetCardChoices().contains(String.valueOf(corvette.getCardId())));
    }

    @Test
    public void RepublicCorvetteRejectsRebelPilotWhenOnlyPilotSlotsRemain() {
        var scn = GetScenario();
        var corvette = scn.GetLSCard("corvette");
        var tatooine = scn.GetLSCard("tatooine");
        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, corvette);
        scn.BoardAsPassenger(corvette, scn.GetLSCard("passenger1"), scn.GetLSCard("passenger2"),
                scn.GetLSCard("passenger3"), scn.GetLSCard("passenger4"));
        scn.MoveCardsToHand(scn.GetLSCard("rebelPilot"));
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSDeployCard(scn.GetLSCard("rebelPilot"));
        assertFalse(scn.LSGetCardChoices().contains(String.valueOf(corvette.getCardId())));
    }

    @Test
    public void RepublicCorvetteAllowsRepublicPilotToDeployAboard() {
        var scn = GetScenario();
        var corvette = scn.GetLSCard("corvette");
        var pilot = scn.GetLSCard("pilot");
        var tatooine = scn.GetLSCard("tatooine");
        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, corvette);
        scn.MoveCardsToHand(pilot);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSDeployCard(pilot);
        scn.LSChooseCard(corvette);
        scn.LSChooseOption("Pilot");
        scn.PassAllResponses();
        assertTrue(scn.IsAboardAsPilot(corvette, pilot));
        assertEquals(7, scn.GetPower(corvette));
    }

    @Test
    public void RepublicCorvetteProhibitsQuadLaserCannonDeploymentAboard() {
        var scn = GetScenario();
        var corvette = scn.GetLSCard("corvette");
        var cannon = scn.GetLSCard("cannon");
        var tatooine = scn.GetLSCard("tatooine");
        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, corvette);
        scn.MoveCardsToLocation(tatooine, scn.GetLSCard("ordinaryCorvette"));
        scn.MoveCardsToHand(cannon);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSDeployAvailable(cannon));
        scn.LSDeployCard(cannon);
        assertFalse(scn.LSGetCardChoices().contains(String.valueOf(corvette.getCardId())));
        assertTrue(scn.LSGetCardChoices().contains(String.valueOf(scn.GetLSCard("ordinaryCorvette").getCardId())));
        scn.LSChooseCard(scn.GetLSCard("ordinaryCorvette"));
        scn.PassAllResponses();
        assertEquals(scn.GetLSCard("ordinaryCorvette"), cannon.getAttachedTo());
    }
}
