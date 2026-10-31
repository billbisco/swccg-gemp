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

public class Card_1106_023_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("findhan", "1106_23");
                    put("han", "1_11");
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
    public void WellFindHanStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("findhan").getBlueprint();

        assertEquals("We'll Find Han", card.getTitle());
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
            add(Icon.CLOUD_CITY);
            add(Icon.INTERRUPT);        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_6, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void WellFindHanDeploysHanFromReserveDeck() {
        var scn = GetScenario();
        var findhan = scn.GetLSCard("findhan");
        var han = scn.GetLSCard("han");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSHand(findhan);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSReserveDeck(han);

        assertTrue(scn.LSCardPlayAvailable(findhan, "Deploy Han from Reserve Deck"));
        scn.LSPlayCard(findhan, "Deploy Han from Reserve Deck");
        for (int i = 0; i < 24; i++) {
            if ((findhan.getZone() == Zone.USED_PILE || findhan.getZone() == Zone.TOP_OF_USED_PILE)
                    && han.getZone() == Zone.AT_LOCATION) {
                break;
            }
            if (scn.LSHasCardChoiceAvailable(han)) {
                scn.LSChooseCard(han);
                continue;
            }
            if (scn.LSHasCardChoiceAvailable(site)) {
                scn.LSChooseCard(site);
                continue;
            }
            if (Boolean.TRUE.equals(scn.LSChoiceAvailable("Yes"))) {
                scn.LSChooseYes();
                continue;
            }
            if (scn.LSDecisionAvailable("Viewing") || scn.LSDecisionAvailable("prior to choosing")) {
                scn.LSDecided("");
                continue;
            }
            if (scn.LSDecisionAvailable("Choose card") || scn.LSDecisionAvailable("Choose location")) {
                if (scn.LSGetCardChoices() != null && !scn.LSGetCardChoices().isEmpty()) {
                    scn.LSChooseAnyCard();
                    continue;
                }
            }
            scn.PassAllResponses();
        }
        assertEquals(Zone.AT_LOCATION, han.getZone());
        assertInZone(Zone.USED_PILE, findhan);
    }
}
