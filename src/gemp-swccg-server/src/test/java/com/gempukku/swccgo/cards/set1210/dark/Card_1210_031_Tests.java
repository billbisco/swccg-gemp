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
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1210_031_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("tatooine", "1_127");
                    put("falcon", "1_143");
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("shortage", "1210_31");
                    put("chimaera", "9_154");
                    put("bobaMaint", "13_59");
                    put("lott", "12_110");
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
            add(Icon.LEGACY_BLOCK_1);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_1, card.getExpansionSet());
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
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.EnsureDSForcePile(4);
        scn.DSInitiateBattle(tatooine);
        scn.PassAllResponses();
        scn.PassWeaponsSegmentActions();

        float before = scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), chimaera);
        boolean offered = false;
        for (int i = 0; i < 24; i++) {
            if (dsHasCardActions(scn) && scn.DSCardActionAvailable(shortage, "Reduce starship immunity")) {
                scn.DSUseCardAction(shortage, "Reduce starship immunity");
                offered = true;
                break;
            }
            if (scn.LSGetDecision() != null) {
                scn.LSPass();
                continue;
            }
            if (scn.DSGetDecision() != null) {
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

    @Test
    public void AstromechShortageCancelsMaintenanceCharacterOrSenatorGameTextOncePerTurn() {
        var scn = GetScenario();
        var shortage = scn.GetDSCard("shortage");
        var lott = scn.GetDSCard("lott");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(shortage);
        scn.MoveCardsToLocation(site, lott);
        scn.EnsureDSForcePile(4);
        scn.SkipToPhase(Phase.CONTROL);

        assertTrue(scn.DSCardActionAvailable(shortage, "Cancel character game text"));
        scn.DSUseCardAction(shortage, "Cancel character game text");
        scn.DSChooseCard(lott);
        scn.PassAllResponses();
        scn.SkipToPhase(Phase.DEPLOY);
        scn.PassAllResponses();

        assertTrue(scn.game().getModifiersQuerying().isGameTextCanceled(scn.gameState(), lott));
    }

    private boolean dsHasCardActions(VirtualTableScenario scn) {
        return scn.DSGetDecision() != null && scn.DSGetADParam("cardId") != null;
    }

    private void reenterDsPhaseActions(VirtualTableScenario scn) {
        for (int i = 0; i < 6; i++) {
            if (dsHasCardActions(scn)) {
                return;
            }
            if (scn.LSGetDecision() != null && (scn.LSChoiceAvailable("Pass")
                    || scn.LSDecisionAvailable("optional") || scn.LSDecisionAvailable("Optional"))) {
                scn.LSPass();
                continue;
            }
            if (scn.DSGetDecision() != null && (scn.DSChoiceAvailable("Pass")
                    || scn.DSDecisionAvailable("optional") || scn.DSDecisionAvailable("Optional"))) {
                scn.DSPass();
                continue;
            }
            break;
        }
    }
}
