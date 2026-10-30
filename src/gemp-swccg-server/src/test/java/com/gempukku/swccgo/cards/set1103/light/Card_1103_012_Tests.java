package com.gempukku.swccgo.cards.set1103.light;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1103_012_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("leia", "1103_12");
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("senate", "12_167");
                    put("tarkin", "1_179");
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
    public void LeiaStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("leia").getBlueprint();

        assertEquals("Leia", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(1, card.getDestiny(), scn.epsilon);
        assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(3, card.getPower(), scn.epsilon);
        assertEquals(4, card.getAbility(), scn.epsilon);
        assertEquals(5, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.REBEL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.LEADER);
            add(Keyword.FEMALE);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.REBEL);
            add(Icon.PREMIUM);
            add(Icon.WARRIOR);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void LeiaAtOpponentsGalacticSenatePlacesHerselfAndACharacterOutOfPlay() {
        var scn = GetScenario();
        var leia = scn.GetLSCard("leia");
        var luke = scn.GetLSCard("luke");
        var senate = scn.GetDSCard("senate");

        scn.StartGame();
        scn.MoveLocationToTable(senate);
        scn.MoveCardsToLocation(senate, leia, luke);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.EnsureLSForcePile(4);

        assertTrue(scn.LSCardActionAvailable(leia, "out of play"));
        scn.LSUseCardAction(leia, "out of play");
        if (scn.LSHasCardChoiceAvailable(luke)) {
            scn.LSChooseCard(luke);
        }
        for (int i = 0; i < 16; i++) {
            if (leia.getZone() == Zone.OUT_OF_PLAY) {
                break;
            }
            if (scn.LSHasCardChoiceAvailable(luke)) {
                scn.LSChooseCard(luke);
                continue;
            }
            if (scn.LSGetDecision() != null && scn.LSHasCardChoiceAvailable(scn.GetTopOfLSForcePile())) {
                scn.LSChooseCard(scn.GetTopOfLSForcePile());
                continue;
            }
            if (scn.LSGetDecision() != null) {
                scn.PassAllResponses();
                continue;
            }
            if (scn.DSGetDecision() != null) {
                scn.DSPass();
                continue;
            }
            break;
        }

        assertEquals(Zone.OUT_OF_PLAY, leia.getZone());
        assertEquals(Zone.OUT_OF_PLAY, luke.getZone());
    }

    @Test
    public void LeiaCancelsGameTextOfALeaderOfAbilityLessThanFiveDuringBattle() {
        var scn = GetScenario();
        var leia = scn.GetLSCard("leia");
        var tarkin = scn.GetDSCard("tarkin");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, leia, tarkin);
        scn.LSStartBattleAndSkipToWeaponsSegment(site);

        assertTrue(scn.LSCardActionAvailable(leia, "Cancel a leader's game text"));
        scn.LSUseCardAction(leia, "Cancel a leader's game text");
        scn.LSChooseCard(tarkin);
        scn.PassAllResponses();

        assertTrue(scn.game().getModifiersQuerying().isGameTextCanceled(scn.gameState(), tarkin));
    }
}
