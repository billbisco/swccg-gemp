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

public class Card_1106_007_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("interface", "1106_7");
                    put("lobot", "5_6");
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
    public void ComputerInterfaceStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("interface").getBlueprint();

        assertEquals("Computer Interface", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
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
    public void ComputerInterfacePeeksAtTopThreeOfReserveWhenLobotIsOnCloudCity() {
        var scn = GetScenario();
        var iface = scn.GetLSCard("interface");
        var lobot = scn.GetLSCard("lobot");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, lobot);
        scn.MoveCardsToLSHand(iface);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfLSReserveDeck(luke);

        assertTrue(scn.LSCardPlayAvailable(iface, "Peek at top 3 cards of Reserve Deck"));
        scn.LSPlayCard(iface, "Peek at top 3 cards of Reserve Deck");
        for (int i = 0; i < 20; i++) {
            if (iface.getZone() == Zone.USED_PILE || iface.getZone() == Zone.TOP_OF_USED_PILE) {
                break;
            }
            if (scn.LSHasCardChoiceAvailable(luke)) {
                scn.LSChooseCard(luke);
                continue;
            }
            if (scn.LSDecisionAvailable("Top card") || scn.LSDecisionAvailable("Choose card")) {
                if (scn.LSGetCardChoices() != null && !scn.LSGetCardChoices().isEmpty()) {
                    scn.LSChooseAnyCard();
                    continue;
                }
                scn.LSDecided("");
                continue;
            }
            scn.PassAllResponses();
        }
        assertInZone(Zone.USED_PILE, iface);
    }
}
