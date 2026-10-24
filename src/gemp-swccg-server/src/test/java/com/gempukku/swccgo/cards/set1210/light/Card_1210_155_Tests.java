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

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1210_155_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("chute", "1210_155");
                    put("luke", "1_19");
                    put("compactor", "1_125");
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
    public void IntoTheGarbageChuteFlyboyStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("chute").getBlueprint();

        assertEquals("Into The Garbage Chute, Flyboy", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(6, card.getDestiny(), scn.epsilon);
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
    public void IntoTheGarbageChuteFlyboyAddsOneBattleDestinyAtTrashCompactor() {
        var scn = GetScenario();
        var chute = scn.GetLSCard("chute");
        var luke = scn.GetLSCard("luke");
        var compactor = scn.GetLSCard("compactor");
        var vader = scn.GetDSCard("vader");

        scn.StartGame();
        scn.MoveLocationToTable(compactor);
        scn.MoveCardsToLocation(compactor, luke, vader);
        scn.MoveCardsToLSHand(chute);
        scn.SkipToLSTurn(Phase.BATTLE);

        scn.LSInitiateBattle(compactor);
        scn.PassAllResponses();
        assertTrue(scn.LSCardPlayAvailable(chute, "Add one battle destiny") || scn.LSCardPlayAvailable(chute));
        if (scn.LSCardPlayAvailable(chute, "Add one battle destiny")) {
            scn.LSPlayCard(chute, "Add one battle destiny");
        } else {
            scn.LSPlayCard(chute);
        }
        scn.PassAllResponses();
        finishUsedInterrupt(scn, chute);

        assertInZone(Zone.USED_PILE, chute);
    }

    @Test
    public void IntoTheGarbageChuteFlyboyIsNotOfferedAtANormalSite() {
        var scn = GetScenario();
        var chute = scn.GetLSCard("chute");
        var luke = scn.GetLSCard("luke");
        var vader = scn.GetDSCard("vader");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke, vader);
        scn.MoveCardsToLSHand(chute);
        scn.SkipToLSTurn(Phase.BATTLE);

        scn.LSInitiateBattle(site);
        scn.PassAllResponses();
        assertFalse(scn.LSCardPlayAvailable(chute));
    }

    private void finishUsedInterrupt(VirtualTableScenario scn, PhysicalCardImpl interrupt) {
        for (int i = 0; i < 10; i++) {
            if (interrupt.getZone() == Zone.USED_PILE) {
                return;
            }
            if (scn.LSGetDecision() != null && scn.LSGetDecision().getText() != null
                    && (scn.LSGetDecision().getText().toLowerCase().contains("optional")
                    || scn.LSGetDecision().getText().toLowerCase().contains("or pass"))) {
                scn.LSPass();
                continue;
            }
            if (scn.DSGetDecision() != null && scn.DSGetDecision().getText() != null
                    && (scn.DSGetDecision().getText().toLowerCase().contains("optional")
                    || scn.DSGetDecision().getText().toLowerCase().contains("or pass"))) {
                scn.DSPass();
                continue;
            }
            return;
        }
    }
}
