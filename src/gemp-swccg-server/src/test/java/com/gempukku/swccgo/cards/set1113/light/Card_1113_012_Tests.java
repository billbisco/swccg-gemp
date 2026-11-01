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

public class Card_1113_012_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("through", "1113_12");
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
    public void ThroughTheForceThingsYouWillSeeStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("through").getBlueprint();

        assertEquals("Through The Force Things You Will See", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.OUT_OF_PLAY, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DAGOBAH);
            add(Icon.INTERRUPT);
}});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_13, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void ThroughTheForcePlacesTopCardOfUsedPileOnForcePile() {
        var scn = GetScenario();
        var through = scn.GetLSCard("through");
        var luke = scn.GetLSCard("luke");

        scn.StartGame();
        scn.MoveCardsToLSHand(through);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfLSUsedPile(luke);
        for (int i = 0; i < 16; i++) {
            if (scn.LSAnyActionsAvailable()
                    && scn.LSCardPlayAvailable(through, "Place top card of Used Pile on Force Pile")) {
                break;
            }
            if (scn.LSGetDecision() != null && !scn.LSAnyActionsAvailable()) {
                scn.LSPass();
                continue;
            }
            if (scn.DSGetDecision() != null) {
                scn.DSPass();
                continue;
            }
            scn.PassAllResponses();
        }

        assertTrue("ls=" + scn.GetLSAvailableActions() + " lukeZone=" + luke.getZone()
                        + " lsDec=" + (scn.LSGetDecision() != null ? scn.LSGetDecision().getText() : null),
                scn.LSAnyActionsAvailable()
                        && scn.LSCardPlayAvailable(through, "Place top card of Used Pile on Force Pile"));
        scn.LSPlayCard(through, "Place top card of Used Pile on Force Pile");
        for (int i = 0; i < 12; i++) {
            if (through.getZone() == Zone.OUT_OF_PLAY
                    && (luke.getZone() == Zone.FORCE_PILE || luke.getZone() == Zone.TOP_OF_FORCE_PILE)) {
                break;
            }
            scn.PassAllResponses();
        }
        assertInZone(Zone.FORCE_PILE, luke);
        assertInZone(Zone.OUT_OF_PLAY, through);
    }
}
