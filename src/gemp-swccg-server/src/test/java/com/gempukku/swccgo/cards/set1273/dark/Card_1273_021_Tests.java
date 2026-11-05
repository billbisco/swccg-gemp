package com.gempukku.swccgo.cards.set1273.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
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

public class Card_1273_021_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("tatooine", "1_127"); // Tatooine (Light system)
                }},
                new HashMap<>() {{
                    put("elis", "1273_21");
                    put("evazan", "1_172"); // Dr. Evazan (smuggler)
                    put("kessel", "1_288"); // Kessel (Dark system)
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
    public void ElisInHinthraStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("elis").getBlueprint();
        assertEquals("Elis In Hinthra", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(4, card.getDeployCost(), scn.epsilon);
        assertEquals(4, card.getPower(), scn.epsilon);
        assertEquals(5, card.getManeuver(), scn.epsilon);
        assertEquals(4, card.getHyperspeed(), scn.epsilon);
        assertEquals(5, card.getForfeit(), scn.epsilon);
        assertEquals(1, card.getPilotCapacity());
        assertEquals(3, card.getPassengerCapacity());
        assertEquals(CardSubtype.STARFIGHTER, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.STARSHIP);
        }});
        scn.BlueprintModelTypeCheck(card, new ArrayList<>() {{
            add(ModelType.HEAVILY_MODIFIED_FREIGHTER);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.STARSHIP);
            add(Icon.PILOT);
            add(Icon.NAV_COMPUTER);
            add(Icon.INDEPENDENT);
            add(Icon.SCOMP_LINK);
            add(Icon.LEGACY_BLOCK_7);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void ElisInHinthraHasPermanentPilotWithAbilityTwo() {
        var scn = GetScenario();
        var elis = scn.GetDSCard("elis");
        var tatooine = scn.GetLSCard("tatooine");
        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, elis);
        assertEquals(2, scn.GetDSAbilityAtLocation(tatooine));
    }

    @Test
    public void ElisInHinthraDeploysSmugglerAboardFromReserveDeck() {
        var scn = GetScenario();
        var elis = scn.GetDSCard("elis");
        var evazan = scn.GetDSCard("evazan");
        var tatooine = scn.GetLSCard("tatooine");
        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, elis);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfDSReserveDeck(evazan);
        assertTrue(scn.DSCardActionAvailable(elis, "Deploy smuggler aboard"));
        scn.DSUseCardAction(elis, "Deploy smuggler aboard");
        scn.DSChooseCard(evazan);
        scn.DSChoose("Passenger");
        scn.PassAllResponses();
        assertTrue(scn.IsAboard(elis, evazan));
    }

    @Test
    public void ElisInHinthraAddsLightIconAtOpponentsSystemOnly() {
        var scn = GetScenario();
        var elis = scn.GetDSCard("elis");
        var tatooine = scn.GetLSCard("tatooine");
        var kessel = scn.GetDSCard("kessel");
        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveLocationToTable(kessel);
        int tatooineBefore = scn.GetIconCount(tatooine, Icon.LIGHT_FORCE);
        int kesselBefore = scn.GetIconCount(kessel, Icon.LIGHT_FORCE);
        scn.MoveCardsToLocation(tatooine, elis);
        assertEquals(tatooineBefore + 1, scn.GetIconCount(tatooine, Icon.LIGHT_FORCE));
        scn.MoveCardsToLocation(kessel, elis);
        assertEquals(tatooineBefore, scn.GetIconCount(tatooine, Icon.LIGHT_FORCE));
        assertEquals(kesselBefore, scn.GetIconCount(kessel, Icon.LIGHT_FORCE));
    }

    @Test
    public void ElisInHinthraIsImmuneToAttritionLessThanFive() {
        var scn = GetScenario();
        var elis = scn.GetDSCard("elis");
        var tatooine = scn.GetLSCard("tatooine");
        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, elis);
        assertEquals(5, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.game().getGameState(), elis), scn.epsilon);
    }
}
