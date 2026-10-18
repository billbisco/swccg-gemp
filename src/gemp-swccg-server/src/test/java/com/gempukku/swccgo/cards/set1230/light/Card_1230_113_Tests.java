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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1230_113_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("red7", "1230_113");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
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
    public void RedSquadron7StatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("red7").getBlueprint();

        assertEquals("Red Squadron 7", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(3, card.getPower(), scn.epsilon);
        assertEquals(3, card.getManeuver(), scn.epsilon);
        assertEquals(5, card.getHyperspeed(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.STARSHIP);
        }});
        assertEquals(CardSubtype.STARFIGHTER, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.RED_SQUADRON);
            add(Keyword.HEROIC);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.STARSHIP);
            add(Icon.NAV_COMPUTER);
            add(Icon.SCOMP_LINK);
            add(Icon.DEATH_STAR_II);
            add(Icon.LEGACY_BLOCK_3);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void RedSquadron7MatchingPilotIsKeirSantageOnly() {
        var scn = GetPilotScenario();
        var red7 = scn.GetLSCard("red7");
        var keir = scn.GetLSCard("keir");
        var corran = scn.GetLSCard("corran");
        var yavin = scn.GetLSCard("yavin");

        scn.StartGame();
        scn.MoveLocationToTable(yavin);
        scn.MoveCardsToLocation(yavin, red7);

        assertTrue(scn.IsMatchingPilot(red7, keir));
        assertFalse(scn.IsMatchingPilot(red7, corran));
    }

    @Test
    public void RedSquadron7CorranAndKeirDeployMinusOneAboard() {
        var scn = GetPilotScenario();
        var red7 = scn.GetLSCard("red7");
        var keir = scn.GetLSCard("keir");
        var corran = scn.GetLSCard("corran");
        var yavin = scn.GetLSCard("yavin");

        scn.StartGame();
        scn.MoveLocationToTable(yavin);
        scn.MoveCardsToLocation(yavin, red7);

        assertEquals(scn.GetDeployCost(keir) - 1, deployCostTo(scn, keir, red7), scn.epsilon);
        assertEquals(scn.GetDeployCost(corran) - 1, deployCostTo(scn, corran, red7), scn.epsilon);
    }

    @Test
    public void RedSquadron7ImmuneToAttritionLessThanFourWhileKeirOrCorranPilots() {
        var scn = GetPilotScenario();
        var red7 = scn.GetLSCard("red7");
        var keir = scn.GetLSCard("keir");
        var corran = scn.GetLSCard("corran");
        var yavin = scn.GetLSCard("yavin");

        scn.StartGame();
        scn.MoveLocationToTable(yavin);
        scn.MoveCardsToLocation(yavin, red7);

        assertEquals(0, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), red7), scn.epsilon);

        scn.BoardAsPilot(red7, keir);
        assertEquals(4, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), red7), scn.epsilon);

        scn.MoveCardsToLocation(yavin, keir);
        scn.BoardAsPilot(red7, corran);
        assertEquals(4, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), red7), scn.epsilon);
    }

    @Test
    public void RedSquadron7OncePerGamePlacesNonImmuneEffectAtSameSystemInUsedPile() {
        var scn = GetPilotScenario();
        var red7 = scn.GetLSCard("red7");
        var keir = scn.GetLSCard("keir");
        var revolution = scn.GetLSCard("revolution");
        var yavin = scn.GetLSCard("yavin");

        scn.StartGame();
        scn.MoveLocationToTable(yavin);
        scn.MoveCardsToLocation(yavin, red7);
        scn.BoardAsPilot(red7, keir);
        scn.AttachCardsTo(yavin, revolution);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.PassAllResponses();
        if (scn.AwaitingDSControlPhaseActions()) {
            scn.DSPass();
        }

        assertTrue("Place Effect must be offered at same system; ls=" + scn.LSGetDecision() + " actions=" + scn.GetLSAvailableActions(),
                scn.GetLSAvailableActions().toString().contains("Place an Effect"));
        scn.LSChooseAction("Place an Effect");
        scn.LSChooseCard(revolution);
        scn.PassAllResponses();
        assertTrue(revolution.getZone() == Zone.USED_PILE || revolution.getZone() == Zone.TOP_OF_USED_PILE);

        assertFalse(scn.GetLSAvailableActions().toString().contains("Place an Effect"));
    }

    @Test
    public void RedSquadron7DoesNotPlaceImmuneToAlterEffect() {
        var scn = GetPilotScenario();
        var red7 = scn.GetLSCard("red7");
        var keir = scn.GetLSCard("keir");
        var revolution = scn.GetLSCard("revolution");
        var haven = scn.GetLSCard("haven");
        var yavin = scn.GetLSCard("yavin");

        scn.StartGame();
        scn.MoveLocationToTable(yavin);
        scn.MoveCardsToLocation(yavin, red7);
        scn.BoardAsPilot(red7, keir);
        scn.AttachCardsTo(yavin, revolution, haven);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.PassAllResponses();
        if (scn.AwaitingDSControlPhaseActions()) {
            scn.DSPass();
        }

        assertTrue("Place Effect must be offered for the non-immune Effect; ls=" + scn.LSGetDecision()
                        + " actions=" + scn.GetLSAvailableActions(),
                scn.GetLSAvailableActions().toString().contains("Place an Effect"));
        scn.LSChooseAction("Place an Effect");
        assertTrue(scn.LSHasCardChoiceAvailable(revolution));
        assertTrue("Haven is immune to Alter and must not be offered",
                scn.LSHasCardChoiceNotAvailable(haven));
    }

    @Test
    public void RedSquadron7DoesNotPlaceEffectWhenAtASite() {
        var scn = GetPilotScenario();
        var red7 = scn.GetLSCard("red7");
        var revolution = scn.GetLSCard("revolution");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, red7);
        scn.AttachCardsTo(site, revolution);
        scn.SkipToLSTurn(Phase.CONTROL);
        if (scn.AwaitingDSControlPhaseActions()) {
            scn.DSPass();
        }

        assertFalse(scn.GetLSAvailableActions().toString().contains("Place an Effect"));
    }

    private VirtualTableScenario GetPilotScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("red7", "1230_113");
                    put("keir", "9_19");
                    put("corran", "10_5");
                    put("revolution", "1_62");
                    put("haven", "5_24");
                    put("yavin", "1_135");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
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

    private float deployCostTo(VirtualTableScenario scn, com.gempukku.swccgo.game.PhysicalCardImpl card,
                               com.gempukku.swccgo.game.PhysicalCardImpl target) {
        return scn.game().getModifiersQuerying().getDeployCost(
                scn.gameState(), card, card, target, false, null, false, 0, null, false);
    }
}
