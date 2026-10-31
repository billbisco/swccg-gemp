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

public class Card_1104_025_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("dagobahgo", "1104_25");
                    put("luke", "1_19");
                    put("bog", "4_85");
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
    public void YouWillGoToTheDagobahSystemStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("dagobahgo").getBlueprint();

        assertEquals("You Will Go To The Dagobah System", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.USED, card.getCardSubtype());
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
    public void YouWillGoToTheDagobahSystemDeploysCharacterToDagobahSiteFromReserveDeck() {
        var scn = GetScenario();
        var dagobahgo = scn.GetLSCard("dagobahgo");
        var luke = scn.GetLSCard("luke");
        var bog = scn.GetLSCard("bog");

        scn.StartGame();
        scn.MoveLocationToTable(bog);
        scn.MoveCardsToLSHand(dagobahgo);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSReserveDeck(luke);

        assertTrue(scn.LSCardPlayAvailable(dagobahgo, "Deploy card to Dagobah site from Reserve Deck"));
        scn.LSPlayCard(dagobahgo, "Deploy card to Dagobah site from Reserve Deck");
        for (int i = 0; i < 24; i++) {
            if ((dagobahgo.getZone() == Zone.USED_PILE || dagobahgo.getZone() == Zone.TOP_OF_USED_PILE)
                    && luke.getZone() == Zone.AT_LOCATION) {
                break;
            }
            if (scn.LSHasCardChoiceAvailable(luke)) {
                scn.LSChooseCard(luke);
                continue;
            }
            if (scn.LSHasCardChoiceAvailable(bog)) {
                scn.LSChooseCard(bog);
                continue;
            }
            scn.PassAllResponses();
        }
        assertEquals(Zone.AT_LOCATION, luke.getZone());
        assertInZone(Zone.USED_PILE, dagobahgo);
        assertEquals(bog, luke.getAtLocation());
    }
}
