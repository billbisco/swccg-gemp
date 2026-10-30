package com.gempukku.swccgo.cards.set1103.light;

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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1103_014_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("delay", "1103_14");
                    put("tatooine", "1_127");
                    put("falcon", "1_143");
                    put("luke", "1_19");
                    put("leia", "1_17");
                }},
                new HashMap<>() {{
                    put("chimaera", "9_154");
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
    public void LogisticalDelayStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("delay").getBlueprint();

        assertEquals("Logistical Delay", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.A_NEW_HOPE);
            add(Icon.EFFECT);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void LogisticalDelayDeploysOnTable() {
        var scn = GetScenario();
        var delay = scn.GetLSCard("delay");

        scn.StartGame();
        scn.MoveCardsToLSHand(delay);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertTrue(scn.LSCardPlayAvailable(delay) || scn.LSDeployAvailable(delay));
        scn.LSPlayCard(delay);
        scn.PassAllResponses();

        assertInZone(Zone.SIDE_OF_TABLE, delay);
    }

    @Test
    public void LogisticalDelayReducesStarshipImmunityToAttritionByXDuringPowerSegment() {
        var scn = GetScenario();
        var delay = scn.GetLSCard("delay");
        var tatooine = scn.GetLSCard("tatooine");
        var falcon = scn.GetLSCard("falcon");
        var luke = scn.GetLSCard("luke");
        var chimaera = scn.GetDSCard("chimaera");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLSSideOfTable(delay);
        scn.MoveCardsToLocation(tatooine, falcon, chimaera);
        scn.BoardAsPilot(falcon, luke);
        scn.EnsureLSForcePile(4);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(tatooine);
        scn.PassAllResponses();
        scn.PassWeaponsSegmentActions();

        float before = scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), chimaera);
        boolean offered = false;
        for (int i = 0; i < 16; i++) {
            if (lsHasCardActions(scn) && scn.LSCardActionAvailable(delay, "Reduce starship immunity")) {
                scn.LSUseCardAction(delay, "Reduce starship immunity");
                offered = true;
                break;
            }
            if (scn.DSGetDecision() != null) {
                scn.DSPass();
                continue;
            }
            if (scn.LSGetDecision() != null && !lsHasCardActions(scn)
                    && (scn.LSDecisionAvailable("DESTINY") || scn.LSDecisionAvailable("destiny to power")
                    || scn.LSDecisionAvailable("COST_TO_DRAW") || scn.LSDecisionAvailable("ABOUT_TO_DRAW"))) {
                scn.LSPass();
                continue;
            }
            break;
        }
        assertTrue(offered);
        scn.LSChooseCard(chimaera);
        scn.LSDecided(2);
        scn.PassAllResponses();

        assertEquals(before - 2, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), chimaera), scn.epsilon);
    }

    @Test
    public void LogisticalDelayCancelsMaintenanceCharacterOrSenatorGameTextOncePerTurn() {
        var scn = GetScenario();
        var delay = scn.GetLSCard("delay");
        var leia = scn.GetLSCard("leia");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(delay);
        scn.MoveCardsToLocation(site, leia);
        scn.EnsureLSForcePile(4);
        scn.SkipToLSTurn(Phase.CONTROL);

        assertTrue(scn.LSCardActionAvailable(delay, "Cancel character game text"));
        scn.LSUseCardAction(delay, "Cancel character game text");
        scn.LSChooseCard(leia);
        scn.PassAllResponses();
        scn.SkipToPhase(Phase.DEPLOY);
        scn.PassAllResponses();

        assertTrue(scn.game().getModifiersQuerying().isGameTextCanceled(scn.gameState(), leia));
        assertFalse(scn.LSCardActionAvailable(delay, "Cancel character game text"));
    }

    @Test
    public void LogisticalDelayDoesNotOfferReduceImmunityActionOutsidePowerSegment() {
        var scn = GetScenario();
        var delay = scn.GetLSCard("delay");
        var tatooine = scn.GetLSCard("tatooine");
        var falcon = scn.GetLSCard("falcon");
        var luke = scn.GetLSCard("luke");
        var chimaera = scn.GetDSCard("chimaera");
        var site = scn.GetLSStartingLocation();
        var vader = scn.GetDSCard("vader");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLSSideOfTable(delay);
        scn.MoveCardsToLocation(tatooine, falcon, chimaera);
        scn.BoardAsPilot(falcon, luke);
        scn.MoveCardsToLocation(site, vader);
        scn.EnsureLSForcePile(4);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertFalse(scn.LSCardActionAvailable(delay, "Reduce starship immunity"));

        scn.SkipToPhase(Phase.BATTLE);
        scn.LSInitiateBattle(tatooine);
        scn.PassAllResponses();
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        assertFalse(scn.LSCardActionAvailable(delay, "Reduce starship immunity"));
    }

    private boolean lsHasCardActions(VirtualTableScenario scn) {
        return scn.LSGetDecision() != null && scn.LSGetADParam("cardId") != null;
    }
}
