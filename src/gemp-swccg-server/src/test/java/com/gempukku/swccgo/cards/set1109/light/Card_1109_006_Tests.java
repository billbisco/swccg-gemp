package com.gempukku.swccgo.cards.set1109.light;

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
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1109_006_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("landing", "1109_6");
                    put("scout", "8_5");
                    put("forest", "8_72");
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
    public void CovertLandingStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("landing").getBlueprint();
        assertEquals("Covert Landing", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.USED_OR_LOST, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.INTERRUPT);
            add(Icon.ENDOR);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_9, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void CovertLandingIsNotPlayableDuringYourMovePhase() {
        var scn = GetScenario();
        var landing = scn.GetLSCard("landing");
        var scout = scn.GetLSCard("scout");
        var forest = scn.GetLSCard("forest");
        scn.StartGame();
        scn.MoveLocationToTable(forest);
        scn.MoveCardsToTopOfLSReserveDeck(scout);
        scn.MoveCardsToLSHand(landing);
        scn.SkipToLSTurn(Phase.MOVE);
        assertFalse(scn.LSCardPlayAvailable(landing, "Deploy Rebel scout from Reserve Deck"));
        assertFalse(scn.LSCardPlayAvailable(landing, "Peek at top four cards of Reserve Deck"));
        assertEquals(Zone.HAND, landing.getZone());
    }
}
