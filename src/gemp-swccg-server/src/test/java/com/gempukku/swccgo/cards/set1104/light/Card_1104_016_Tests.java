package com.gempukku.swccgo.cards.set1104.light;

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
import static org.junit.Assert.assertTrue;

public class Card_1104_016_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("scout", "1104_16");
                    put("luke", "1_19");
                    put("peek", "1_5");
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
    public void RebelScoutStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("scout").getBlueprint();

        assertEquals("Rebel Scout", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.RESTRICTED_3, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(1, card.getAbility(), scn.epsilon);
        assertEquals(3, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.REBEL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.SCOUT);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.REBEL);
            add(Icon.WARRIOR);
            add(Icon.HOTH);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void RebelScoutPeeksAtTopOfAReserveDeckWhenDeployedAndMayPlaceItOnBottom() {
        var scn = GetScenario();
        var scout = scn.GetLSCard("scout");
        var peek = scn.GetLSCard("peek");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSHand(scout);
        scn.EnsureLSForcePile(3);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSReserveDeck(peek);
        assertTrue(scn.LSCardPlayAvailable(scout));
        scn.LSPlayCard(scout);
        scn.LSChooseCard(site);
        int safety = 0;
        while (safety++ < 16
                && !scn.LSDecisionAvailable("Peek at top of a Reserve Deck")
                && !scn.GetLSAvailableActions().toString().contains("Peek at top of a Reserve Deck")) {
            if (scn.DSGetDecision() != null) {
                scn.DSPass();
            } else if (scn.LSGetDecision() != null) {
                scn.LSPass();
            } else {
                break;
            }
        }
        assertTrue(scn.LSDecisionAvailable("Peek at top of a Reserve Deck")
                || scn.GetLSAvailableActions().toString().contains("Peek at top of a Reserve Deck"));
        scn.LSChooseAction("Peek at top of a Reserve Deck");
        if (scn.LSDecisionAvailable("Choose Reserve Deck")) {
            scn.LSChooseCard(peek);
        }
        if (scn.LSDecisionAvailable("Place") || scn.LSDecisionAvailable("bottom")) {
            scn.LSChooseYes();
        }
        scn.PassAllResponses();
        assertEquals(site, scout.getAtLocation());
        assertTrue(peek.getZone() == Zone.RESERVE_DECK || peek.getZone() == Zone.TOP_OF_RESERVE_DECK);
    }
}
