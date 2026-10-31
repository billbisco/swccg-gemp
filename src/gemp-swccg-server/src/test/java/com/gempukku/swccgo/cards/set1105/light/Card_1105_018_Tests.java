package com.gempukku.swccgo.cards.set1105.light;

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

import static com.gempukku.swccgo.framework.Assertions.assertInHand;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1105_018_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("scrambled", "1105_18");
                    put("delay", "1103_14");
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
    public void ScrambledTransmissionStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("scrambled").getBlueprint();

        assertEquals("Scrambled Transmission", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.SPECIAL_EDITION);
            add(Icon.EFFECT);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_5, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void ScrambledTransmissionDeploysOnTableAndTakesLogisticalDelay() {
        var scn = GetScenario();
        var scrambled = scn.GetLSCard("scrambled");
        var delay = scn.GetLSCard("delay");

        scn.StartGame();
        scn.MoveCardsToLSHand(scrambled);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSDeployAvailable(scrambled));
        scn.LSDeployCard(scrambled);
        scn.PassAllResponses();
        assertInZone(Zone.SIDE_OF_TABLE, scrambled);

        scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfLSReserveDeck(delay);
        assertTrue(scn.LSCardActionAvailable(scrambled, "Take Logistical Delay into hand from Reserve Deck"));
        scn.LSUseCardAction(scrambled, "Take Logistical Delay into hand from Reserve Deck");
        for (int i = 0; i < 20; i++) {
            if (delay.getZone() == Zone.HAND) {
                break;
            }
            if (scn.LSDecisionAvailable("Viewing") || scn.LSDecisionAvailable("prior to choosing")
                    || scn.LSDecisionAvailable("Top card")) {
                scn.LSDecided("");
                continue;
            }
            if (scn.LSHasCardChoiceAvailable(delay)) {
                scn.LSChooseCard(delay);
                continue;
            }
            scn.PassAllResponses();
            scn.PassCardAndForceUseResponses();
        }
        assertInHand(delay);
    }
}
