package com.gempukku.swccgo.cards.set1230.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Zone;
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

public class Card_1230_098_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("yarna", "1230_98");
            put("objective", "112_1");
            put("bo", "6_54");
            put("otherEffect", "8_41");
            put("chewie", "2_3");
            put("luke", "1_19");
            put("audience", "6_81");
            put("mos", "1_133");
            put("palace", "7_131");
            put("hut", "1_134");
            put("endor", "8_69");
        }}, new HashMap<>() {{
            put("vader", "1_168");
            put("pit", "6_166");
            put("bubo", "6_138");
            put("rancor", "6_139");
        }}, 30, 30, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
        StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
        StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void YarnaDalGarganStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("yarna").getBlueprint();
        assertEquals("Yarna d'al' Gargan", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(6, card.getDestiny(), scn.epsilon);
        assertTrue(card.isImmuneToCardTitle(Title.Alter));
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.JABBAS_PALACE);
            add(Icon.LEGACY_BLOCK_3);
            add(Icon.EFFECT);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void YarnaDalGarganRequiresAgentsInTheCourtToDeploy() {
        var scn = GetScenario();
        var yarna = scn.GetLSCard("yarna");
        scn.StartGame();
        scn.SkipToLSTurn(Phase.ACTIVATE);
        scn.MoveCardsToLSHand(yarna);
        scn.SkipToPhase(Phase.DEPLOY);
        com.gempukku.swccgo.framework.Assertions.assertInZone(Zone.HAND, yarna);
        assertFalse(scn.LSCardPlayAvailable(yarna));
    }

    @Test
    public void YarnaDalGarganDeploysForFreeWithAgentsInTheCourt() {
        var scn = GetScenario();
        scn.StartGame();
        scn.SkipToLSTurn(Phase.ACTIVATE);
        // Isolate this card's deployment prerequisite from the Objective's starting-card setup.
        scn.MoveCardsToLSSideOfTable(scn.GetLSCard("objective"));
        scn.MoveCardsToLSHand(scn.GetLSCard("yarna"));
        scn.SkipToPhase(Phase.DEPLOY);
        int force = scn.GetLSForcePileCount();
        com.gempukku.swccgo.framework.Assertions.assertInZone(Zone.SIDE_OF_TABLE, scn.GetLSCard("objective"));
        assertTrue(scn.LSCardPlayAvailable(scn.GetLSCard("yarna")));
        scn.LSPlayCard(scn.GetLSCard("yarna"));
        scn.PassAllResponses();
        com.gempukku.swccgo.framework.Assertions.assertInZone(Zone.SIDE_OF_TABLE, scn.GetLSCard("yarna"));
        assertEquals(force, scn.GetLSForcePileCount());
    }

    @Test
    public void YarnaDalGarganBlocksNoLoveForTheEmpireFromFlippingButNotItsFront() {
        var scn = GetScenario();
        scn.StartGame();
        scn.SkipToLSTurn(Phase.ACTIVATE);
        var objective = scn.GetLSCard("objective");
        scn.MoveCardsToLSSideOfTable(objective, scn.GetLSCard("yarna"));
        assertFalse(scn.game().getModifiersQuerying().cannotBeFlipped(scn.gameState(), objective));
        scn.gameState().flipCard(scn.game(), objective, true);
        assertTrue(scn.game().getModifiersQuerying().cannotBeFlipped(scn.gameState(), objective));
        scn.MoveCardsToLSHand(scn.GetLSCard("yarna"));
        assertFalse(scn.game().getModifiersQuerying().cannotBeFlipped(scn.gameState(), objective));
    }

    @Test
    public void YarnaDalGarganProtectsBoShudaFromCancellationButNotOtherEffects() {
        var scn = GetScenario();
        scn.StartGame();
        scn.SkipToLSTurn(Phase.ACTIVATE);
        scn.MoveLocationToTable(scn.GetLSCard("audience"));
        scn.MoveCardsToLocation(scn.GetLSCard("audience"), scn.GetLSCard("chewie"));
        scn.AttachCardsTo(scn.GetLSCard("audience"), scn.GetLSCard("bo"));
        scn.AttachCardsTo(scn.GetLSCard("chewie"), scn.GetLSCard("otherEffect"));
        assertFalse(scn.game().getModifiersQuerying().mayNotBeCanceled(scn.gameState(), scn.GetLSCard("bo")));
        scn.MoveCardsToLSSideOfTable(scn.GetLSCard("yarna"));
        assertTrue(scn.game().getModifiersQuerying().mayNotBeCanceled(scn.gameState(), scn.GetLSCard("bo")));
        assertFalse(scn.game().getModifiersQuerying().mayNotBeCanceled(scn.gameState(), scn.GetLSCard("otherEffect")));
    }

    @Test
    public void YarnaDalGarganProtectsYourCharactersOnlyAtJabbasPalaceSites() {
        var scn = GetScenario();
        scn.StartGame();
        scn.SkipToLSTurn(Phase.ACTIVATE);
        scn.MoveLocationToTable(scn.GetLSCard("audience"));
        scn.MoveLocationToTable(scn.GetLSCard("mos"));
        scn.MoveCardsToLocation(scn.GetLSCard("audience"), scn.GetLSCard("luke"), scn.GetDSCard("bubo"));
        assertFalse(scn.game().getModifiersQuerying().isProhibitedFromAttackingTarget(scn.gameState(), scn.GetDSCard("bubo"), scn.GetLSCard("luke")));
        scn.MoveCardsToLSSideOfTable(scn.GetLSCard("yarna"));
        assertTrue(scn.game().getModifiersQuerying().isProhibitedFromAttackingTarget(scn.gameState(), scn.GetDSCard("bubo"), scn.GetLSCard("luke")));
        scn.MoveCardsToLocation(scn.GetLSCard("mos"), scn.GetLSCard("luke"), scn.GetDSCard("bubo"));
        assertFalse(scn.game().getModifiersQuerying().isProhibitedFromAttackingTarget(scn.gameState(), scn.GetDSCard("bubo"), scn.GetLSCard("luke")));
    }

    @Test
    public void YarnaDalGarganDoesNotProtectOpponentsCharactersFromCreatureAttacks() {
        var scn = GetScenario();
        scn.StartGame();
        scn.SkipToLSTurn(Phase.ACTIVATE);
        scn.MoveLocationToTable(scn.GetDSCard("pit"));
        scn.MoveCardsToLocation(scn.GetDSCard("pit"), scn.GetLSCard("luke"), scn.GetDSCard("vader"), scn.GetDSCard("rancor"));
        scn.MoveCardsToLSSideOfTable(scn.GetLSCard("yarna"));
        assertTrue(scn.game().getModifiersQuerying().isProhibitedFromAttackingTarget(scn.gameState(), scn.GetDSCard("rancor"), scn.GetLSCard("luke")));
        assertFalse(scn.game().getModifiersQuerying().isProhibitedFromAttackingTarget(scn.gameState(), scn.GetDSCard("rancor"), scn.GetDSCard("vader")));
    }

    @Test
    public void YarnaDalGarganDoesNotPreventOrdinaryBattlesAtJabbasPalace() {
        var scn = GetScenario();
        scn.StartGame();
        scn.SkipToLSTurn(Phase.ACTIVATE);
        scn.MoveLocationToTable(scn.GetLSCard("audience"));
        scn.MoveCardsToLocation(scn.GetLSCard("audience"), scn.GetLSCard("luke"), scn.GetDSCard("vader"));
        scn.MoveCardsToLSSideOfTable(scn.GetLSCard("yarna"));
        scn.EnsureLSForcePile(5);
        scn.SkipToPhase(Phase.BATTLE);
        scn.LSInitiateBattle(scn.GetLSCard("audience"));
        scn.PassAllResponses();
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
    }

    @Test
    public void YarnaDalGarganDownloadsOnlyTatooineBattlegroundLocationsForOneForce() {
        var scn = SearchReady(3, Phase.DEPLOY);
        DownloadMosEisley(scn);
    }

    @Test
    public void YarnaDalGarganDownloadIsOncePerDeployPhaseAndResetsNextTurn() {
        var scn = SearchReady(3, Phase.DEPLOY);
        DownloadMosEisley(scn);
        if (scn.AwaitingDSDeployPhaseActions()) scn.DSPass();
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertEquals(2, scn.GetLSForcePileCount());
        assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("yarna"), "Deploy Tatooine battleground"));
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSReserveDeck(scn.GetLSCard("palace"));
        assertTrue(scn.LSCardActionAvailable(scn.GetLSCard("yarna"), "Deploy Tatooine battleground"));
    }

    @Test
    public void YarnaDalGarganCannotDownloadWithoutOneAvailableForce() {
        var scn = SearchReady(0, Phase.DEPLOY);
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("yarna"), "Deploy Tatooine battleground"));
    }

    @Test
    public void YarnaDalGarganCannotDownloadDuringControlPhase() {
        var scn = SearchReady(3, Phase.CONTROL);
        assertTrue(scn.AwaitingLSControlPhaseActions());
        assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("yarna"), "Deploy Tatooine battleground"));
    }

    private VirtualTableScenario SearchReady(int force, Phase phase) {
        var scn = GetScenario();
        scn.StartGame();
        scn.SkipToLSTurn(Phase.ACTIVATE);
        scn.MoveCardsToLSSideOfTable(scn.GetLSCard("objective"), scn.GetLSCard("yarna"));
        for (String key : new String[]{"mos", "palace", "hut", "endor"}) scn.MoveOutOfPlay(scn.GetLSCard(key));
        scn.LSChooseAction("Activate Force");
        int activated = Math.min(scn.GetLSReserveDeckCount() - 1, scn.LSGetChoiceMax());
        scn.LSDecided(activated);
        if (scn.DSDecisionAvailable("Choose amount of Force to allow opponent")) scn.DSDecided(activated);
        scn.EnsureLSForcePile(force);
        scn.DrainLSForcePileTo(force);
        scn.PassAllResponses();
        scn.PassActivateActions();
        scn.PassAllResponses();
        if (phase == Phase.DEPLOY) scn.SkipToPhase(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSReserveDeck(scn.GetLSCard("mos"), scn.GetLSCard("palace"), scn.GetLSCard("hut"), scn.GetLSCard("endor"));
        com.gempukku.swccgo.framework.Assertions.assertInZone(Zone.SIDE_OF_TABLE, scn.GetLSCard("yarna"));
        assertEquals(force, scn.GetLSForcePileCount());
        return scn;
    }

    private void DownloadMosEisley(VirtualTableScenario scn) {
        int force = scn.GetLSForcePileCount();
        int reserve = scn.GetLSReserveDeckCount();
        assertTrue(scn.LSCardActionAvailable(scn.GetLSCard("yarna"), "Deploy Tatooine battleground"));
        scn.LSUseCardAction(scn.GetLSCard("yarna"), "Deploy Tatooine battleground");
        scn.PassAllResponses();
        AssertDownloadSelectable(scn, "mos", true);
        AssertDownloadSelectable(scn, "palace", true);
        AssertDownloadSelectable(scn, "hut", false);
        AssertDownloadSelectable(scn, "endor", false);
        scn.LSChooseCard(scn.GetLSCard("mos"));
        scn.PassAllResponses();
        assertTrue(scn.LSDecisionAvailable("On which side of"));
        scn.LSChoose("Left");
        scn.PassAllResponses();
        com.gempukku.swccgo.framework.Assertions.assertInZone(Zone.LOCATIONS, scn.GetLSCard("mos"));
        assertEquals(force - 1, scn.GetLSForcePileCount());
        assertEquals(reserve - 1, scn.GetLSReserveDeckCount());
    }
    private void AssertDownloadSelectable(VirtualTableScenario scn, String key, boolean expected) {
        // Reserve Deck decisions also display cards that are not selectable.
        var ids = scn.LSGetADParamAsList("blueprintId");
        int index = ids.indexOf(scn.GetLSCard(key).getBlueprintId(true));
        assertTrue("Expected displayed card: " + key, index >= 0);
        assertEquals(key, expected, Boolean.parseBoolean(scn.LSGetADParam("selectable")[index]));
    }

}
