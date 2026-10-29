package com.gempukku.swccgo.cards.set1220.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1220_165_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("hope", "1220_165");
                    put("redemption", "1220_166");
                }},
                new HashMap<>() {{
                    put("tie", "1_304");
                }},
                10, 10,
                StartingSetup.DefaultLSSpaceSystem,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void BrightHopeStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("hope").getBlueprint();

        assertEquals("Bright Hope", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(4, card.getArmor(), scn.epsilon);
        assertEquals(5, card.getHyperspeed(), scn.epsilon);
        assertEquals(5, card.getForfeit(), scn.epsilon);
        assertEquals(2, card.getPilotCapacity());
        assertEquals(6, card.getPassengerCapacity());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.STARSHIP);
        }});
        assertEquals(CardSubtype.CAPITAL, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.SHIP_DOCKING_CAPABILITY);
            add(Keyword.MEDIUM_TRANSPORT);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.STARSHIP);
            add(Icon.CLOUD_CITY);
            add(Icon.PILOT);
            add(Icon.NAV_COMPUTER);
            add(Icon.SCOMP_LINK);
            add(Icon.LEGACY_BLOCK_2);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void BrightHopePermanentPilotProvidesAbilityOfTwo() {
        var scn = GetScenario();
        var hope = scn.GetLSCard("hope");
        var system = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(system, hope);
        assertEquals(2, scn.GetBattleDestinyAbility(hope));
    }

    @Test
    public void BrightHopeMayCancelBattleDestinyIfWithARebelCapitalStarship() {
        var scn = GetScenario();
        var hope = scn.GetLSCard("hope");
        var redemption = scn.GetLSCard("redemption");
        var system = scn.GetLSStartingLocation();
        var tie = scn.GetDSCard("tie");

        scn.StartGame();
        scn.MoveCardsToLocation(system, hope, redemption, tie);
        scn.EnsureLSForcePile(2);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(system);
        scn.PassAllResponses();
        scn.PassWeaponsSegmentActions();
        scn.PrepareDSDestiny(5);
        scn.PrepareLSDestiny(5);
        boolean canceled = false;
        for (int i = 0; i < 20; i++) {
            try {
                var actions = scn.GetLSAvailableActions();
                if (actions != null && actions.stream().anyMatch(a -> a != null && a.contains("Cancel battle destiny"))) {
                    scn.LSUseCardAction(hope, "Cancel battle destiny");
                    canceled = true;
                    break;
                }
            } catch (RuntimeException ignored) {
            }
            if (scn.LSDecisionAvailable("battle destiny?")) {
                scn.LSChooseYes();
                continue;
            }
            if (scn.DSDecisionAvailable("battle destiny?")) {
                scn.DSChooseYes();
                continue;
            }
            if (scn.LSGetDecision() != null) {
                scn.LSPass();
            } else if (scn.DSGetDecision() != null) {
                scn.DSPass();
            } else {
                break;
            }
        }
        assertTrue(canceled);
        scn.PassAllResponses();
    }

    @Test
    public void BrightHopeDoesNotOfferCancelBattleDestinyWithoutAnotherRebelCapital() {
        var scn = GetScenario();
        var hope = scn.GetLSCard("hope");
        var system = scn.GetLSStartingLocation();
        var tie = scn.GetDSCard("tie");

        scn.StartGame();
        scn.MoveCardsToLocation(system, hope, tie);
        scn.EnsureLSForcePile(2);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(system);
        scn.PassAllResponses();
        scn.PassWeaponsSegmentActions();
        scn.PrepareDSDestiny(5);
        scn.PrepareLSDestiny(5);
        boolean sawDestiny = false;
        boolean reachedAfterDestinies = false;
        boolean sawCancel = false;
        for (int i = 0; i < 20; i++) {
            try {
                var actions = scn.GetLSAvailableActions();
                if (actions != null && actions.stream().anyMatch(a -> a != null && a.contains("Cancel battle destiny"))) {
                    sawCancel = true;
                    break;
                }
            } catch (RuntimeException ignored) {
            }
            String decisionText = "";
            if (scn.GetCurrentDecision() != null && scn.GetCurrentDecision().getText() != null) {
                decisionText = scn.GetCurrentDecision().getText().toLowerCase();
            }
            if (decisionText.contains("forfeit") || decisionText.contains("choose force to lose")
                    || decisionText.contains("damage segment")) {
                reachedAfterDestinies = true;
                break;
            }
            if (decisionText.contains("battle destiny")) {
                sawDestiny = true;
                if (scn.LSGetDecision() != null) {
                    scn.LSChooseYes();
                } else {
                    scn.DSChooseYes();
                }
                continue;
            }
            if (scn.LSGetDecision() != null) {
                scn.LSPass();
            } else if (scn.DSGetDecision() != null) {
                scn.DSPass();
            } else {
                break;
            }
        }
        assertTrue(sawDestiny || reachedAfterDestinies);
        assertFalse(sawCancel);
    }
}
