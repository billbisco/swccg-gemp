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

public class Card_1104_002_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("delta", "1104_2");
                    put("luke", "1_19");
                    put("speeder", "3_69");
                    put("hoth", "3_55");
                    put("ridge", "3_56");
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
    public void AttackPatternDeltaStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("delta").getBlueprint();

        assertEquals("Attack Pattern Delta", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.LOST, card.getCardSubtype());
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
    public void AttackPatternDeltaSubstitutesCombatVehicleManeuverForBattleDestinyAtASite() {
        var scn = GetScenario();
        var delta = scn.GetLSCard("delta");
        var luke = scn.GetLSCard("luke");
        var speeder = scn.GetLSCard("speeder");
        var hoth = scn.GetLSCard("hoth");
        var ridge = scn.GetLSCard("ridge");
        var vader = scn.GetDSCard("vader");

        scn.StartGame();
        scn.MoveLocationToTable(hoth);
        scn.MoveLocationToTable(ridge);
        scn.MoveCardsToLocation(ridge, luke, speeder, vader);
        scn.MoveCardsToLSHand(delta);
        scn.LSStartBattleAndSkipToWeaponsSegment(ridge);
        scn.LSPass();
        scn.DSPass();
        scn.SkipToPowerSegment();
        scn.PassAllResponses();
        if (scn.LSDecisionAvailable("battle destiny")) {
            scn.LSChooseYes();
        }
        scn.PassResponses("COST_TO_DRAW_DESTINY_CARD");
        scn.PassResponses("ABOUT_TO_DRAW_DESTINY_CARD");
        for (int i = 0; i < 12; i++) {
            if (scn.LSAnyActionsAvailable() && scn.LSCardPlayAvailable(delta, "Substitute destiny")) {
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
            break;
        }
        assertTrue("ls=" + scn.GetLSAvailableActions() + " lsDec=" + (scn.LSGetDecision() != null ? scn.LSGetDecision().getText() : null),
                scn.LSAnyActionsAvailable() && scn.LSCardPlayAvailable(delta, "Substitute destiny"));
        scn.LSPlayCard(delta, "Substitute destiny");
        for (int i = 0; i < 12; i++) {
            if (delta.getZone() == Zone.LOST_PILE || delta.getZone() == Zone.TOP_OF_LOST_PILE) {
                break;
            }
            if (scn.LSHasCardChoiceAvailable(speeder)) {
                scn.LSChooseCard(speeder);
                continue;
            }
            scn.PassAllResponses();
        }
        assertInZone(Zone.LOST_PILE, delta);
    }
}
