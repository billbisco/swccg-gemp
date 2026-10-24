package com.gempukku.swccgo.cards.set1210.light;

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
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInHand;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1210_163_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("utinni", "1210_163");
                    put("plans", "1_46");
                    put("jawa", "1_12");
                    put("het", "2_9");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
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
    public void UtinniStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("utinni").getBlueprint();

        assertEquals("Utinni!", card.getTitle());
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
            add(Icon.INTERRUPT);
            add(Icon.LEGACY_BLOCK_1);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void UtinniTakesUtinniEffectIntoHandFromReserveDeck() {
        var scn = GetScenario();
        var utinni = scn.GetLSCard("utinni");
        var plans = scn.GetLSCard("plans");

        scn.StartGame();
        scn.MoveCardsToLSHand(utinni);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfLSReserveDeck(plans);

        assertTrue(scn.LSCardPlayAvailable(utinni, "Take Utinni Effect"));
        scn.LSPlayCard(utinni, "Take Utinni Effect");
        for (int i = 0; i < 20; i++) {
            if (plans.getZone() == Zone.HAND) {
                break;
            }
            if (scn.LSHasCardChoiceAvailable(plans)) {
                scn.LSChooseCard(plans);
                continue;
            }
            if (scn.DSGetDecision() != null) {
                scn.DSPass();
                continue;
            }
            if (scn.LSGetDecision() != null && scn.LSGetDecision().getText().toLowerCase().contains("optional")) {
                scn.LSPass();
                continue;
            }
            break;
        }
        finishUsedInterrupt(scn, utinni);

        assertInHand(plans);
        assertInZone(Zone.USED_PILE, utinni);
    }

    @Test
    public void UtinniTakesUniqueAndOrNonUniqueJawaIntoHandFromReserveDeck() {
        var scn = GetScenario();
        var utinni = scn.GetLSCard("utinni");
        var jawa = scn.GetLSCard("jawa");
        var het = scn.GetLSCard("het");

        scn.StartGame();
        scn.MoveCardsToLSHand(utinni);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfLSReserveDeck(jawa, het);

        assertTrue(scn.LSCardPlayAvailable(utinni, "Take Jawa"));
        scn.LSPlayCard(utinni, "Take Jawa");
        for (int i = 0; i < 20; i++) {
            if (het.getZone() == Zone.HAND && jawa.getZone() == Zone.HAND) {
                break;
            }
            if (het.getZone() != Zone.HAND && scn.LSHasCardChoiceAvailable(het)
                    && jawa.getZone() != Zone.HAND && scn.LSHasCardChoiceAvailable(jawa)) {
                scn.LSChooseCards(het, jawa);
                continue;
            }
            if (het.getZone() != Zone.HAND && scn.LSHasCardChoiceAvailable(het)) {
                scn.LSChooseCard(het);
                continue;
            }
            if (jawa.getZone() != Zone.HAND && scn.LSHasCardChoiceAvailable(jawa)) {
                scn.LSChooseCard(jawa);
                continue;
            }
            if (scn.DSGetDecision() != null) {
                scn.DSPass();
                continue;
            }
            if (scn.LSGetDecision() != null && scn.LSGetDecision().getText().toLowerCase().contains("optional")) {
                scn.LSPass();
                continue;
            }
            break;
        }
        finishUsedInterrupt(scn, utinni);

        assertTrue(het.getZone() == Zone.HAND || jawa.getZone() == Zone.HAND);
        assertInZone(Zone.USED_PILE, utinni);
    }

    private void finishUsedInterrupt(VirtualTableScenario scn, PhysicalCardImpl interrupt) {
        for (int i = 0; i < 10; i++) {
            if (interrupt.getZone() == Zone.USED_PILE) {
                return;
            }
            if (scn.DSGetDecision() != null) {
                scn.DSPass();
                continue;
            }
            if (scn.LSGetDecision() != null && scn.LSGetDecision().getText().toLowerCase().contains("optional")) {
                scn.LSPass();
                continue;
            }
            return;
        }
    }
}
