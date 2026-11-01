package com.gempukku.swccgo.cards.set1113.light;

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

public class Card_1113_010_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("scan", "1113_10");
                    put("throne", "1_138");
                    put("warroom", "1_139");
                    put("yavin", "1_135");
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
    public void PerimeterScanStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("scan").getBlueprint();

        assertEquals("Perimeter Scan", card.getTitle());
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
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_13, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void PerimeterScanDeploysRelatedSystemWhenTwoRelatedUniqueSitesAreOnTable() {
        var scn = GetScenario();
        var scan = scn.GetLSCard("scan");
        var throne = scn.GetLSCard("throne");
        var warroom = scn.GetLSCard("warroom");
        var yavin = scn.GetLSCard("yavin");

        scn.StartGame();
        scn.MoveLocationToTable(throne);
        scn.MoveLocationToTable(warroom);
        scn.MoveCardsToLSHand(scan);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSReserveDeck(yavin);

        assertTrue(scn.LSCardPlayAvailable(scan, "Deploy related system from Reserve Deck"));
        scn.LSPlayCard(scan, "Deploy related system from Reserve Deck");
        for (int i = 0; i < 20; i++) {
            if (yavin.getZone() == Zone.LOCATIONS
                    && (scan.getZone() == Zone.USED_PILE || scan.getZone() == Zone.TOP_OF_USED_PILE)) {
                break;
            }
            if (scn.LSHasCardChoiceAvailable(yavin)) {
                scn.LSChooseCard(yavin);
                continue;
            }
            if (scn.LSDecisionAvailable("which side") || scn.LSDecisionAvailable("On which side")) {
                scn.LSChoose("Left");
                continue;
            }
            scn.PassAllResponses();
        }
        assertEquals(Zone.LOCATIONS, yavin.getZone());
        assertInZone(Zone.USED_PILE, scan);
    }
}
