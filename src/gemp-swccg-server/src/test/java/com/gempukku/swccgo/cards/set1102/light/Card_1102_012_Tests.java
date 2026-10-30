package com.gempukku.swccgo.cards.set1102.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
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

public class Card_1102_012_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("slug", "1102_12");
                    put("luke", "1_19");
                    put("obiwan", "1_21");
                }},
                new HashMap<>() {{
                    put("bfhi", "4_116");
                    put("emperor", "9_109");
                    put("lightning", "9_136");
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
    public void KlorslugStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("slug").getBlueprint();

        assertEquals("K'lor'slug", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.DEJARIK);
            add(Keyword.HOLOGRAM);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
        assertTrue(card.isImmuneToCardTitle(Title.Alter));
    }

    @Test
    public void KlorslugStopsBadFeelingHaveIFromRaisingNonJediDeployCost() {
        var scn = GetScenario();
        var slug = scn.GetLSCard("slug");
        var luke = scn.GetLSCard("luke");
        var obiwan = scn.GetLSCard("obiwan");
        var bfhi = scn.GetDSCard("bfhi");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(slug);
        scn.MoveCardsToDSSideOfTable(bfhi);
        scn.MoveCardsToLSHand(luke, obiwan);

        float lukeCost = scn.game().getModifiersQuerying().getDeployCost(
                scn.gameState(), luke, luke, site, false, null, false, 0, null, false);
        float obiCost = scn.game().getModifiersQuerying().getDeployCost(
                scn.gameState(), obiwan, obiwan, site, false, null, false, 0, null, false);
        assertEquals(3, lukeCost, scn.epsilon);
        assertEquals(7, obiCost, scn.epsilon);
    }

    @Test
    public void KlorslugDelaysACharacterLostBeforeTheDamageSegmentUntilEndOfBattle() {
        var scn = GetScenario();
        var slug = scn.GetLSCard("slug");
        var luke = scn.GetLSCard("luke");
        var emperor = scn.GetDSCard("emperor");
        var lightning = scn.GetDSCard("lightning");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(slug);
        scn.MoveCardsToLocation(site, luke, emperor);
        scn.MoveCardsToDSHand(lightning);
        scn.EnsureDSForcePile(4);
        scn.DSStartBattleAndSkipToWeaponsSegment(site);
        scn.PrepareDSDestiny(7);

        assertTrue(scn.DSCardPlayAvailable(lightning, "Make target lost"));
        scn.DSPlayCard(lightning, "Make target lost");
        if (scn.DSDecisionAvailable("Choose Emperor")) {
            scn.DSChooseCard(emperor);
        }
        if (scn.DSHasCardChoiceAvailable(luke)) {
            scn.DSChooseCard(luke);
        }
        scn.PassResponses("ATTRIBUTE");
        scn.PassCardPlayResponses();
        scn.PassDestinyDrawResponses();
        scn.PassResponses("DESTINY");
        scn.PassResponses("ATTRIBUTE");
        scn.PassResponses("ABOUT_TO_BE_LOST");
        passOptional(scn);
        assertTrue(luke.getZone().isInPlay());

        scn.SkipToDamageSegment(false);
        if (scn.AwaitingLSBattleDamagePayment()) {
            scn.LSPayRemainingBattleDamageFromReserveDeck();
        }
        scn.PassDamageSegmentActions();
        scn.PassAllResponses();
        if (scn.LSDecisionAvailable("Choose character") || scn.DSDecisionAvailable("Choose character")) {
            if (scn.LSHasCardChoiceAvailable(luke)) {
                scn.LSChooseCard(luke);
            } else if (scn.DSHasCardChoiceAvailable(luke)) {
                scn.DSChooseCard(luke);
            }
            scn.PassAllResponses();
        }
        assertInZone(Zone.LOST_PILE, luke);
    }

    private void passOptional(VirtualTableScenario scn) {
        for (int i = 0; i < 8; i++) {
            if (scn.LSGetDecision() != null && scn.LSGetDecision().getText() != null
                    && scn.LSGetDecision().getText().toLowerCase().contains("optional")) {
                scn.LSPass();
                continue;
            }
            if (scn.DSGetDecision() != null && scn.DSGetDecision().getText() != null
                    && scn.DSGetDecision().getText().toLowerCase().contains("optional")) {
                scn.DSPass();
                continue;
            }
            return;
        }
    }
}
