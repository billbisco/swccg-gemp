package com.gempukku.swccgo.cards.set1112.light;

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

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1112_019_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("leadership", "1112_19");
                    put("dodonna", "1_10");
                }},
                new HashMap<>() {{
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
    public void RebelLeadershipStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("leadership").getBlueprint();

        assertEquals("Rebel Leadership", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.USED, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DEATH_STAR_II);
            add(Icon.INTERRUPT);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_12, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void RebelLeadershipTakesAnAdmiralOrNonJediGeneralIntoHand() {
        var scn = GetScenario();
        var leadership = scn.GetLSCard("leadership");
        var dodonna = scn.GetLSCard("dodonna");

        scn.StartGame();
        scn.MoveCardsToLSHand(leadership);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfLSReserveDeck(dodonna);

        assertTrue(scn.LSCardPlayAvailable(leadership, "Take card into hand from Reserve Deck"));
        scn.LSPlayCard(leadership, "Take card into hand from Reserve Deck");
        for (int i = 0; i < 20; i++) {
            if ((leadership.getZone() == Zone.USED_PILE || leadership.getZone() == Zone.TOP_OF_USED_PILE)
                    && dodonna.getZone() == Zone.HAND) {
                break;
            }
            if (scn.LSHasCardChoiceAvailable(dodonna)) {
                scn.LSChooseCard(dodonna);
                continue;
            }
            scn.PassAllResponses();
        }
        assertEquals(Zone.HAND, dodonna.getZone());
        assertInZone(Zone.USED_PILE, leadership);
    }
}
