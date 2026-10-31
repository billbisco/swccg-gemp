package com.gempukku.swccgo.cards.set1103.dark;

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

public class Card_1103_027_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("tatooine", "1_127");
                    put("falcon", "1_143");
                    put("luke", "1_19");
                    put("leia", "1_17");
                }},
                new HashMap<>() {{
                    put("shortage", "1103_27");
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
    public void AstromechShortageStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("shortage").getBlueprint();

        assertEquals("Astromech Shortage", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
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
    public void AstromechShortageDeploysOnTable() {
        var scn = GetScenario();
        var shortage = scn.GetDSCard("shortage");

        scn.StartGame();
        scn.MoveCardsToDSHand(shortage);
        scn.SkipToDSTurn(Phase.DEPLOY);

        assertTrue(scn.DSCardPlayAvailable(shortage) || scn.DSDeployAvailable(shortage));
        scn.DSPlayCard(shortage);
        scn.PassAllResponses();

        assertInZone(Zone.SIDE_OF_TABLE, shortage);
    }

    @Test
    public void AstromechShortageCancelsSenatorGameTextOncePerTurn() {
        var scn = GetScenario();
        var shortage = scn.GetDSCard("shortage");
        var leia = scn.GetLSCard("leia");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(shortage);
        scn.MoveCardsToLocation(site, leia);
        scn.EnsureDSForcePile(4);
        scn.SkipToDSTurn(Phase.CONTROL);

        assertTrue(scn.DSCardActionAvailable(shortage, "Cancel character game text"));
        scn.DSUseCardAction(shortage, "Cancel character game text");
        scn.DSChooseCard(leia);
        scn.PassAllResponses();
        scn.SkipToPhase(Phase.DEPLOY);
        scn.PassAllResponses();

        assertTrue(scn.game().getModifiersQuerying().isGameTextCanceled(scn.gameState(), leia));
        assertFalse(scn.DSCardActionAvailable(shortage, "Cancel character game text"));
    }

    @Test
    public void AstromechShortageReducesStarshipImmunityToAttritionByXDuringPowerSegment() {
        var scn = GetScenario();
        var shortage = scn.GetDSCard("shortage");
        var tatooine = scn.GetLSCard("tatooine");
        var falcon = scn.GetLSCard("falcon");
        var luke = scn.GetLSCard("luke");
        var chimaera = scn.GetDSCard("chimaera");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToDSSideOfTable(shortage);
        scn.MoveCardsToLocation(tatooine, falcon, chimaera);
        scn.BoardAsPilot(falcon, luke);
        scn.EnsureDSForcePile(4);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(tatooine);
        scn.PassAllResponses();
        scn.PassWeaponsSegmentActions();

        float before = scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), chimaera);
        boolean offered = false;
        for (int i = 0; i < 16; i++) {
            if (dsHasCardActions(scn) && scn.DSCardActionAvailable(shortage, "Reduce starship immunity")) {
                scn.DSUseCardAction(shortage, "Reduce starship immunity");
                offered = true;
                break;
            }
            if (scn.LSGetDecision() != null) {
                scn.LSPass();
                continue;
            }
            if (scn.DSGetDecision() != null && !dsHasCardActions(scn)
                    && (scn.DSDecisionAvailable("DESTINY") || scn.DSDecisionAvailable("destiny to power")
                    || scn.DSDecisionAvailable("COST_TO_DRAW") || scn.DSDecisionAvailable("ABOUT_TO_DRAW"))) {
                scn.DSPass();
                continue;
            }
            break;
        }
        assertTrue(offered);
        scn.DSChooseCard(chimaera);
        scn.DSDecided(2);
        scn.PassAllResponses();

        assertEquals(before - 2, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), chimaera), scn.epsilon);
    }

    private boolean dsHasCardActions(VirtualTableScenario scn) {
        return scn.DSGetDecision() != null && scn.DSGetADParam("cardId") != null;
    }
}
