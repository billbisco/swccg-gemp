package com.gempukku.swccgo.cards.set1210.dark;

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

public class Card_1210_069_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("utinni", "1210_69");
                    put("lukeEffect", "1_223");
                    put("jawa", "1_182");
                    put("dathcha", "1_169");
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
        var card = scn.GetDSCard("utinni").getBlueprint();

        assertEquals("Utinni!", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
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
        var utinni = scn.GetDSCard("utinni");
        var lukeEffect = scn.GetDSCard("lukeEffect");

        scn.StartGame();
        scn.MoveCardsToDSHand(utinni);
        scn.SkipToPhase(Phase.CONTROL);
        scn.MoveCardsToTopOfDSReserveDeck(lukeEffect);

        assertTrue(scn.DSCardPlayAvailable(utinni, "Take Utinni Effect"));
        scn.DSPlayCard(utinni, "Take Utinni Effect");
        for (int i = 0; i < 20; i++) {
            if (lukeEffect.getZone() == Zone.HAND) {
                break;
            }
            if (scn.DSHasCardChoiceAvailable(lukeEffect)) {
                scn.DSChooseCard(lukeEffect);
                continue;
            }
            if (scn.LSGetDecision() != null) {
                scn.LSPass();
                continue;
            }
            if (scn.DSGetDecision() != null && scn.DSGetDecision().getText().toLowerCase().contains("optional")) {
                scn.DSPass();
                continue;
            }
            break;
        }
        finishUsedInterrupt(scn, utinni);

        assertInHand(lukeEffect);
        assertInZone(Zone.USED_PILE, utinni);
    }

    @Test
    public void UtinniTakesUniqueAndOrNonUniqueJawaIntoHandFromReserveDeck() {
        var scn = GetScenario();
        var utinni = scn.GetDSCard("utinni");
        var jawa = scn.GetDSCard("jawa");
        var dathcha = scn.GetDSCard("dathcha");

        scn.StartGame();
        scn.MoveCardsToDSHand(utinni);
        scn.SkipToPhase(Phase.CONTROL);
        scn.MoveCardsToTopOfDSReserveDeck(jawa, dathcha);

        assertTrue(scn.DSCardPlayAvailable(utinni, "Take Jawa"));
        scn.DSPlayCard(utinni, "Take Jawa");
        for (int i = 0; i < 20; i++) {
            if (dathcha.getZone() == Zone.HAND && jawa.getZone() == Zone.HAND) {
                break;
            }
            if (dathcha.getZone() != Zone.HAND && scn.DSHasCardChoiceAvailable(dathcha)
                    && jawa.getZone() != Zone.HAND && scn.DSHasCardChoiceAvailable(jawa)) {
                scn.DSChooseCards(dathcha, jawa);
                continue;
            }
            if (dathcha.getZone() != Zone.HAND && scn.DSHasCardChoiceAvailable(dathcha)) {
                scn.DSChooseCard(dathcha);
                continue;
            }
            if (jawa.getZone() != Zone.HAND && scn.DSHasCardChoiceAvailable(jawa)) {
                scn.DSChooseCard(jawa);
                continue;
            }
            if (scn.LSGetDecision() != null) {
                scn.LSPass();
                continue;
            }
            if (scn.DSGetDecision() != null && scn.DSGetDecision().getText().toLowerCase().contains("optional")) {
                scn.DSPass();
                continue;
            }
            break;
        }
        finishUsedInterrupt(scn, utinni);

        assertTrue(dathcha.getZone() == Zone.HAND || jawa.getZone() == Zone.HAND);
        assertInZone(Zone.USED_PILE, utinni);
    }

    private void finishUsedInterrupt(VirtualTableScenario scn, PhysicalCardImpl interrupt) {
        for (int i = 0; i < 10; i++) {
            if (interrupt.getZone() == Zone.USED_PILE) {
                return;
            }
            if (scn.LSGetDecision() != null) {
                scn.LSPass();
                continue;
            }
            if (scn.DSGetDecision() != null && scn.DSGetDecision().getText().toLowerCase().contains("optional")) {
                scn.DSPass();
                continue;
            }
            return;
        }
    }
}
