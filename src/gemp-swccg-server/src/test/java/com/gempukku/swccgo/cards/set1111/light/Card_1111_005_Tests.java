package com.gempukku.swccgo.cards.set1111.light;

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
import static org.junit.Assert.assertTrue;

public class Card_1111_005_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("gs1", "1111_5");
                    put("greenleader", "9_16");
                    put("tactics", "13_14");
                }},
                new HashMap<>() {{
                    put("vader", "1_168");
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
            add(Icon.DEATH_STAR_II);
            add(Icon.NAV_COMPUTER);
            add(Icon.SCOMP_LINK);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_11, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void GreenSquadron1SitsAtASystem() {
        var scn = GetScenario();
        var gs1 = scn.GetLSCard("gs1");
        var system = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(system, gs1);
        assertAtLocation(system, gs1);
    }

    @Test
    public void GreenSquadron1TakesDiversionaryTacticsIntoHandFromReserveDeck() {
        var scn = GetScenario();
        var gs1 = scn.GetLSCard("gs1");
        var greenleader = scn.GetLSCard("greenleader");
        var tactics = scn.GetLSCard("tactics");
        var system = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(system, gs1);
        scn.BoardAsPilot(gs1, greenleader);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfLSReserveDeck(tactics);
        assertTrue(scn.LSCardActionAvailable(gs1, "Take Diversionary Tactics into hand from Reserve Deck"));
        scn.LSUseCardAction(gs1, "Take Diversionary Tactics into hand from Reserve Deck");
        scn.PassAllResponses();
        scn.PassCardAndForceUseResponses();
        scn.LSChooseCard(tactics);
        scn.PassAllResponses();
        assertEquals(Zone.HAND, tactics.getZone());
    }
}
