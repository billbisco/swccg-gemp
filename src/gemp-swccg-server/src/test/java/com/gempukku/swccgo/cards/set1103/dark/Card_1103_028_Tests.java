package com.gempukku.swccgo.cards.set1103.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
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

public class Card_1103_028_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("falcon", "1_143");
                    put("han", "1_11");
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("besieged", "1103_28");
                    put("devastator", "1_301");
                    put("vader", "1_168");
                }},
                10, 10,
                StartingSetup.DefaultLSSpaceSystem,
                StartingSetup.DefaultDSSpaceSystem,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void BesiegedStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("besieged").getBlueprint();

        assertEquals("Besieged", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.A_NEW_HOPE);
            add(Icon.EFFECT);
        }});
        assertTrue(card.isImmuneToCardTitle(Title.Alter));
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void BesiegedDeploysOnTable() {
        var scn = GetScenario();
        var besieged = scn.GetDSCard("besieged");

        scn.StartGame();
        scn.MoveCardsToDSHand(besieged);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.EnsureDSForcePile(5);

        assertTrue(scn.DSCardPlayAvailable(besieged));
        scn.DSPlayCard(besieged);
        scn.PassAllResponses();

        assertInZone(Zone.SIDE_OF_TABLE, besieged);
    }

    @Test
    public void BesiegedCapturesAForfeitedCharacterAtSameSystemAsYourImperialCapitalStarship() {
        var scn = GetScenario();
        var besieged = scn.GetDSCard("besieged");
        var devastator = scn.GetDSCard("devastator");
        var vader = scn.GetDSCard("vader");
        var falcon = scn.GetLSCard("falcon");
        var han = scn.GetLSCard("han");
        var luke = scn.GetLSCard("luke");
        var system = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(besieged);
        scn.MoveCardsToLocation(system, devastator, falcon);
        scn.BoardAsPilot(devastator, vader);
        scn.BoardAsPilot(falcon, han);
        scn.BoardAsPassenger(falcon, luke);
        scn.SkipToPhase(Phase.BATTLE);
        scn.PrepareDSDestiny(6);
        scn.PrepareLSDestiny(1);
        scn.DSInitiateBattle(system);
        scn.SkipToDamageSegment(false);

        boolean forfeited = false;
        for (int i = 0; i < 24; i++) {
            if ((scn.AwaitingLSAttritionPayment() || scn.AwaitingLSBattleDamagePayment()
                    || (scn.LSGetDecision() != null && scn.LSHasCardChoiceAvailable(luke)))
                    && scn.LSHasCardChoiceAvailable(luke)) {
                scn.LSChooseCard(luke);
                forfeited = true;
                break;
            }
            if (scn.LSGetDecision() != null && (scn.LSChoiceAvailable("Pass")
                    || scn.LSDecisionAvailable("optional") || scn.LSDecisionAvailable("Optional"))) {
                scn.LSPass();
                continue;
            }
            if (scn.DSGetDecision() != null && (scn.DSChoiceAvailable("Pass")
                    || scn.DSDecisionAvailable("optional") || scn.DSDecisionAvailable("Optional"))
                    && !dsHasCardActions(scn)) {
                scn.DSPass();
                continue;
            }
            if (scn.LSGetDecision() == null && scn.DSGetDecision() == null) {
                break;
            }
            break;
        }
        assertTrue(forfeited);
        scn.PassResponses("ABOUT_TO_LOSE_FORCE_NOT_FROM_BATTLE_DAMAGE");
        scn.PrepareDSDestiny(6);
        boolean offered = false;
        for (int i = 0; i < 24; i++) {
            if (dsHasCardActions(scn) && scn.DSCardActionAvailable(besieged, "Draw destiny to capture")) {
                scn.DSUseCardAction(besieged, "Draw destiny to capture");
                offered = true;
                break;
            }
            if (scn.DSGetDecision() != null && scn.DSGetADParam("cardId") != null) {
                var actions = scn.DSGetADParamAsList("actionText");
                if (actions != null) {
                    for (String a : actions) {
                        if (a != null && a.toLowerCase().contains("capture")) {
                            scn.DSChooseAction(a);
                            offered = true;
                            break;
                        }
                    }
                }
            }
            if (offered) {
                break;
            }
            if (scn.DSChoiceAvailable("Yes") && decisionText(scn).toLowerCase().contains("capture")) {
                scn.DSChooseYes();
                offered = true;
                break;
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
        assertTrue(offered);
        scn.PassAllResponses();
        if (scn.DSCaptureDecisionAvailable()) {
            scn.DSChooseSeizeCaptive();
            if (scn.DSHasCardChoiceAvailable(vader)) {
                scn.DSChooseCard(vader);
            }
            scn.PassAllResponses();
        }

        assertTrue(luke.isCaptive());
    }

    private boolean dsHasCardActions(VirtualTableScenario scn) {
        return scn.DSGetDecision() != null && scn.DSGetADParam("cardId") != null;
    }

    private String decisionText(VirtualTableScenario scn) {
        var decision = scn.DSGetDecision();
        return decision != null && decision.getText() != null ? decision.getText() : "";
    }
}
