package com.gempukku.swccgo.cards.set1220.dark;

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

public class Card_1220_066_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("lukesback", "1_95");
                    put("leia", "1_10");
                    put("han", "1_11");
                }},
                new HashMap<>() {{
                    put("shocking", "1220_66");
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
    public void ShockingRevelationStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("shocking").getBlueprint();

        assertEquals("Shocking Revelation", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.USED, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.CLOUD_CITY);
            add(Icon.INTERRUPT);
            add(Icon.LEGACY_BLOCK_2);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void ShockingRevelationPlaysWhenOpponentTakesACharacterIntoHandFromReserve() {
        var scn = GetScenario();
        var shocking = scn.GetDSCard("shocking");
        var lukesback = scn.GetLSCard("lukesback");
        var luke = scn.GetLSCard("luke");
        var leia = scn.GetLSCard("leia");
        var han = scn.GetLSCard("han");

        scn.StartGame();
        scn.MoveCardsToDSHand(shocking);
        scn.MoveCardsToLSHand(lukesback, han);
        scn.EnsureLSForcePile(4);
        scn.MoveCardsToTopOfLSReserveDeck(luke);
        scn.MoveCardsToTopOfLSReserveDeck(leia);
        scn.SkipToPhase(Phase.DEPLOY);
        if (scn.DSGetDecision() != null && scn.LSGetDecision() == null) {
            scn.DSPass();
        }

        assertTrue("ls=" + scn.GetLSAvailableActions() + " lsDec=" + scn.LSGetDecision()
                        + " dsDec=" + scn.DSGetDecision() + " lukeZone=" + luke.getZone(),
                scn.LSGetADParam("cardId") != null
                        && (scn.LSCardPlayAvailable(lukesback, "Take Luke")
                        || scn.LSCardPlayAvailable(lukesback)));
        if (scn.LSCardPlayAvailable(lukesback, "Take Luke")) {
            scn.LSPlayCard(lukesback, "Take Luke");
        }
        else {
            scn.LSPlayCard(lukesback);
        }
        for (int i = 0; i < 20; i++) {
            if (scn.DSGetADParam("cardId") != null
                    && scn.DSCardPlayAvailable(shocking, "Place 2 cards from hand on Used Pile")) {
                break;
            }
            if (scn.LSHasCardChoiceAvailable(luke)) {
                scn.LSChooseCard(luke);
                continue;
            }
            var lsForce = scn.GetTopOfLSForcePile();
            if (lsForce != null && scn.LSHasCardChoiceAvailable(lsForce)) {
                scn.LSChooseCard(lsForce);
                continue;
            }
            var ds = scn.DSGetDecision();
            String dsText = ds != null && ds.getText() != null ? ds.getText().toLowerCase() : "";
            var ls = scn.LSGetDecision();
            String lsText = ls != null && ls.getText() != null ? ls.getText().toLowerCase() : "";
            if (dsText.contains("optional response") || lsText.contains("optional response")) {
                scn.PassAllResponses();
                continue;
            }
            if (dsText.contains("force") || lsText.contains("force")) {
                scn.PassForceUseResponses();
                continue;
            }
            if (scn.LSGetADParam("cardId") != null) {
                scn.LSPass();
                continue;
            }
            if (ls != null && scn.DSGetADParam("cardId") == null) {
                scn.LSPass();
                continue;
            }
            if (ds != null && scn.DSGetADParam("cardId") == null) {
                scn.DSPass();
                continue;
            }
            break;
        }
        assertTrue("ds=" + scn.GetDSAvailableActions() + " dsDec=" + (scn.DSGetDecision() != null ? scn.DSGetDecision().getText() : null)
                        + " lsDec=" + (scn.LSGetDecision() != null ? scn.LSGetDecision().getText() : null)
                        + " lukeZone=" + luke.getZone(),
                scn.DSGetADParam("cardId") != null
                        && scn.DSCardPlayAvailable(shocking, "Place 2 cards from hand on Used Pile"));
        scn.DSPlayCard(shocking, "Place 2 cards from hand on Used Pile");
        for (int i = 0; i < 16; i++) {
            if (shocking.getZone() == Zone.USED_PILE || shocking.getZone() == Zone.TOP_OF_USED_PILE) {
                break;
            }
            if (scn.LSHasCardChoiceAvailable(han)) {
                scn.LSChooseCard(han);
                continue;
            }
            if (scn.LSGetCardChoices() != null && !scn.LSGetCardChoices().isEmpty()) {
                scn.LSChooseAnyCard();
                continue;
            }
            var ls = scn.LSGetDecision();
            String lsText = ls != null && ls.getText() != null ? ls.getText().toLowerCase() : "";
            if (lsText.contains("optional response") || (scn.DSGetDecision() != null
                    && scn.DSGetDecision().getText() != null
                    && scn.DSGetDecision().getText().toLowerCase().contains("optional response"))) {
                scn.PassAllResponses();
                continue;
            }
            if (ls != null) {
                scn.LSPass();
                continue;
            }
            scn.PassAllResponses();
        }
        assertInHand(luke);
        assertInZone(Zone.USED_PILE, shocking);
    }
}
