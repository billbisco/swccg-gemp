package com.gempukku.swccgo.cards.set1106.light;

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

public class Card_1106_013_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("eyes", "1106_13");
                    put("luke", "1_19");
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
    public void KeepYourEyesOpenStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("eyes").getBlueprint();

        assertEquals("Keep Your Eyes Open", card.getTitle());
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
            add(Icon.CLOUD_CITY);
            add(Icon.INTERRUPT);        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_6, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void KeepYourEyesOpenPeeksAtTopOfForcePileAndMayTakeThatCardIntoHand() {
        var scn = GetScenario();
        var eyes = scn.GetLSCard("eyes");
        var luke = scn.GetLSCard("luke");

        scn.StartGame();
        scn.MoveCardsToLSHand(eyes);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfLSForcePile(luke);

        assertTrue(scn.LSCardPlayAvailable(eyes, "Peek at top card of Force Pile or Reserve Deck"));
        scn.LSPlayCard(eyes, "Peek at top card of Force Pile or Reserve Deck");
        for (int i = 0; i < 24; i++) {
            if (luke.getZone() == Zone.HAND
                    && (eyes.getZone() == Zone.USED_PILE || eyes.getZone() == Zone.TOP_OF_USED_PILE)) {
                break;
            }
            if (scn.LSDecisionAvailable("Top card")) {
                scn.LSDecided("");
                continue;
            }
            if (scn.LSHasCardChoiceAvailable(luke) && luke.getZone() != Zone.HAND) {
                scn.LSChooseCard(luke);
                continue;
            }
            if (Boolean.TRUE.equals(scn.LSChoiceAvailable("Take card into hand"))) {
                scn.LSChoose("Take card into hand");
                continue;
            }
            scn.PassAllResponses();
        }
        assertEquals(Zone.HAND, luke.getZone());
        assertInZone(Zone.USED_PILE, eyes);
    }
}
