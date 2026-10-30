package com.gempukku.swccgo.cards.set1102.light;

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

public class Card_1102_005_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("demotion", "1102_5");
                    put("demotion2", "1102_5");
                }},
                new HashMap<>() {{
                    put("tarkin", "1_179");
                    put("tagge", "1_178");
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
    public void DemotionStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("demotion").getBlueprint();

        assertEquals("Demotion", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.DEPLOYS_ON_CHARACTERS);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void DemotionDeploysOnImperialLeaderOfAbilityLessThanFiveForTwoForce() {
        var scn = GetScenario();
        var demotion = scn.GetLSCard("demotion");
        var tarkin = scn.GetDSCard("tarkin");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, tarkin);
        scn.MoveCardsToLSHand(demotion);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureLSForcePile(2);

        assertEquals(2, scn.GetDeployCost(demotion));
        int force = scn.GetLSForcePileCount();
        assertTrue(scn.LSCardPlayAvailable(demotion));
        scn.LSPlayCard(demotion);
        scn.LSChooseCard(tarkin);
        scn.PassAllResponses();

        assertEquals(tarkin, demotion.getAttachedTo());
        assertEquals(force - 2, scn.GetLSForcePileCount());
    }

    @Test
    public void DemotionDoesNotDeployOnVader() {
        var scn = GetScenario();
        var demotion = scn.GetLSCard("demotion");
        var vader = scn.GetDSCard("vader");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, vader);
        scn.MoveCardsToLSHand(demotion);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureLSForcePile(2);

        assertFalse(scn.LSCardPlayAvailable(demotion));
        assertEquals(Zone.HAND, demotion.getZone());
    }

    @Test
    public void DemotionCancelsAttachedCharactersGameText() {
        var scn = GetScenario();
        var demotion = scn.GetLSCard("demotion");
        var tarkin = scn.GetDSCard("tarkin");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, tarkin);
        scn.AttachCardsTo(tarkin, demotion);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.PassAllResponses();

        assertTrue(scn.game().getModifiersQuerying().isGameTextCanceled(scn.gameState(), tarkin));
    }

    @Test
    public void DemotionIsLostWhenPresentWithAnotherDemotion() {
        var scn = GetScenario();
        var demotion = scn.GetLSCard("demotion");
        var demotion2 = scn.GetLSCard("demotion2");
        var tarkin = scn.GetDSCard("tarkin");
        var tagge = scn.GetDSCard("tagge");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, tarkin, tagge);
        scn.MoveCardsToLSHand(demotion, demotion2);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureLSForcePile(4);
        assertTrue(scn.LSCardPlayAvailable(demotion));
        scn.LSPlayCard(demotion);
        scn.LSChooseCard(tarkin);
        scn.PassAllResponses();
        if (scn.AwaitingDSDeployPhaseActions()) {
            scn.DSPass();
        }
        assertTrue(scn.LSCardPlayAvailable(demotion2));
        scn.LSPlayCard(demotion2);
        scn.LSChooseCard(tagge);
        for (int i = 0; i < 16; i++) {
            boolean lost1 = demotion.getZone() == Zone.LOST_PILE || demotion.getZone() == Zone.TOP_OF_LOST_PILE;
            boolean lost2 = demotion2.getZone() == Zone.LOST_PILE || demotion2.getZone() == Zone.TOP_OF_LOST_PILE;
            if (lost1 || lost2) {
                break;
            }
            String[] lsActions = scn.LSGetDecision() != null ? scn.LSGetADParam("actionText") : null;
            String[] dsActions = scn.DSGetDecision() != null ? scn.DSGetADParam("actionText") : null;
            if (lsActions != null && java.util.Arrays.toString(lsActions).contains("Make this Effect lost")) {
                scn.LSChooseAction("Make this Effect lost");
                continue;
            }
            if (dsActions != null && java.util.Arrays.toString(dsActions).contains("Make this Effect lost")) {
                scn.DSChooseAction("Make this Effect lost");
                continue;
            }
            if (takeLoseAction(scn, demotion) || takeLoseAction(scn, demotion2)) {
                continue;
            }
            if (chooseResultsContaining(scn, "lost") || chooseResultsContaining(scn, "this Effect")) {
                continue;
            }
            if (!passOptionalOrPlayWindow(scn)) {
                break;
            }
        }
        if (scn.LSGetDecision() != null || scn.DSGetDecision() != null) {
            boolean stillRequired = (scn.LSGetDecision() != null && String.valueOf(scn.LSGetDecision().getText()).toLowerCase().contains("required"))
                    || (scn.DSGetDecision() != null && String.valueOf(scn.DSGetDecision().getText()).toLowerCase().contains("required"));
            if (!stillRequired) {
                scn.PassAllResponses();
            }
        }

        boolean lost1 = demotion.getZone() == Zone.LOST_PILE || demotion.getZone() == Zone.TOP_OF_LOST_PILE;
        boolean lost2 = demotion2.getZone() == Zone.LOST_PILE || demotion2.getZone() == Zone.TOP_OF_LOST_PILE;
        String dsText = scn.DSGetDecision() != null ? String.valueOf(scn.DSGetDecision().getText()) : "null";
        String lsText = scn.LSGetDecision() != null ? String.valueOf(scn.LSGetDecision().getText()) : "null";
        assertTrue("neither Demotion lost; zones=" + demotion.getZone() + "," + demotion2.getZone()
                + " DS=" + dsText + " LS=" + lsText, lost1 || lost2);
        if (lost1) {
            assertInZone(Zone.LOST_PILE, demotion);
        }
        if (lost2) {
            assertInZone(Zone.LOST_PILE, demotion2);
        }
    }

    private boolean takeLoseAction(VirtualTableScenario scn, com.gempukku.swccgo.game.PhysicalCardImpl demotion) {
        if (scn.LSGetDecision() != null && scn.LSGetADParam("cardId") != null
                && scn.LSCardActionAvailable(demotion, "Make this Effect lost")) {
            scn.LSUseCardAction(demotion, "Make this Effect lost");
            return true;
        }
        if (scn.DSGetDecision() != null && scn.DSGetADParam("cardId") != null
                && scn.DSCardActionAvailable(demotion, "Make this Effect lost")) {
            scn.DSUseCardAction(demotion, "Make this Effect lost");
            return true;
        }
        return false;
    }

    private boolean chooseResultsContaining(VirtualTableScenario scn, String snippet) {
        if (scn.LSGetDecision() != null && scn.LSChoiceAvailable(snippet)) {
            scn.LSChoose(snippet);
            return true;
        }
        if (scn.DSGetDecision() != null && scn.DSChoiceAvailable(snippet)) {
            scn.DSChoose(snippet);
            return true;
        }
        return false;
    }

    private boolean passOptionalOrPlayWindow(VirtualTableScenario scn) {
        if (scn.DSDecisionAvailable("You have not activated Force")) {
            scn.DSChooseYes();
            return true;
        }
        if (scn.LSDecisionAvailable("You have not activated Force")) {
            scn.LSChooseYes();
            return true;
        }
        String dsText = scn.DSGetDecision() != null && scn.DSGetDecision().getText() != null
                ? scn.DSGetDecision().getText().toLowerCase() : "";
        String lsText = scn.LSGetDecision() != null && scn.LSGetDecision().getText() != null
                ? scn.LSGetDecision().getText().toLowerCase() : "";
        if (dsText.contains("required") || lsText.contains("required")) {
            return false;
        }
        if (dsText.contains("pass") || dsText.contains("optional") || dsText.contains("playing")) {
            scn.DSPass();
            return true;
        }
        if (lsText.contains("pass") || lsText.contains("optional") || lsText.contains("playing")) {
            scn.LSPass();
            return true;
        }
        return false;
    }
}
