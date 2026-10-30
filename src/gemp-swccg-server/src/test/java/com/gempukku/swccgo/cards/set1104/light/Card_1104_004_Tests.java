package com.gempukku.swccgo.cards.set1104.light;

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

public class Card_1104_004_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("dissension", "1104_4");
                    put("demotion", "1_47");
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
    public void DarkDissensionStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("dissension").getBlueprint();

        assertEquals("Dark Dissension", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.USED_OR_LOST, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.HOTH);
            add(Icon.INTERRUPT);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void DarkDissensionTakesDemotionIntoHandFromReserveDeck() {
        var scn = GetScenario();
        var dissension = scn.GetLSCard("dissension");
        var demotion = scn.GetLSCard("demotion");

        scn.StartGame();
        scn.MoveCardsToLSHand(dissension);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfLSReserveDeck(demotion);

        assertTrue(scn.LSCardPlayAvailable(dissension, "Take Demotion into hand from Reserve Deck"));
        scn.LSPlayCard(dissension, "Take Demotion into hand from Reserve Deck");
        for (int i = 0; i < 20; i++) {
            if ((dissension.getZone() == Zone.USED_PILE || dissension.getZone() == Zone.TOP_OF_USED_PILE)
                    && demotion.getZone() == Zone.HAND) {
                break;
            }
            if (scn.LSHasCardChoiceAvailable(demotion)) {
                scn.LSChooseCard(demotion);
                continue;
            }
            scn.PassAllResponses();
        }
        assertEquals(Zone.HAND, demotion.getZone());
        assertInZone(Zone.USED_PILE, dissension);
    }
}
