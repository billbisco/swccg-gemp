package com.gempukku.swccgo.cards.set1220.light;

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

public class Card_1220_149_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("nice", "1220_149");
                    put("rogue2", "3_67");
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
    public void NiceOfYouGuysToDropByStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("nice").getBlueprint();

        assertEquals("Nice Of You Guys To Drop By", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.USED, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.HOTH);
            add(Icon.INTERRUPT);
            add(Icon.LEGACY_BLOCK_2);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void NiceOfYouGuysTakesARogueSpeederIntoHandFromReserveDeck() {
        var scn = GetScenario();
        var nice = scn.GetLSCard("nice");
        var rogue2 = scn.GetLSCard("rogue2");

        scn.StartGame();
        scn.MoveCardsToLSHand(nice);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfLSReserveDeck(rogue2);

        assertTrue(scn.LSCardPlayAvailable(nice, "Take card into hand from Reserve Deck"));
        scn.LSPlayCard(nice, "Take card into hand from Reserve Deck");
        for (int i = 0; i < 20; i++) {
            if (rogue2.getZone() == Zone.HAND
                    && (nice.getZone() == Zone.USED_PILE || nice.getZone() == Zone.TOP_OF_USED_PILE)) {
                break;
            }
            if (scn.LSHasCardChoiceAvailable(rogue2)) {
                scn.LSChooseCard(rogue2);
                continue;
            }
            scn.PassAllResponses();
        }
        assertEquals(Zone.HAND, rogue2.getZone());
        assertInZone(Zone.USED_PILE, nice);
    }
}
