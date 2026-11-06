package com.gempukku.swccgo.cards.set1230.light;

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

public class Card_1230_095_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("thatsone", "1230_95");
            put("second", "1230_95");
            put("chewie", "2_3");
            put("luke", "1_19");
            put("h1", "8_10");
            put("h2", "8_5");
            put("h3", "1_11");
            put("forest", "8_72");
        }}, new HashMap<>() {{
            put("vader", "1_168");
        }}, 30, 30, StartingSetup.LSStartingLocation("8_69"), StartingSetup.DefaultDSGroundLocation,
        StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
        StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void ThatsOneStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("thatsone").getBlueprint();
        assertEquals("That's One", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.DEPLOYS_ON_CHARACTERS);
            add(Keyword.DEPLOYS_ON_SITE);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.ENDOR);
            add(Icon.LEGACY_BLOCK_3);
            add(Icon.EFFECT);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void ThatsOneDeploysOnChewieForTwoAndMayDeclineHandPlacement() {
        var scn = DeployReady(5);
        int force = scn.GetLSForcePileCount();
        scn.LSPlayCard(scn.GetLSCard("thatsone"));
        assertTrue(scn.LSHasCardChoiceAvailable(scn.GetLSCard("chewie")));
        assertTrue(scn.LSHasCardChoiceAvailable(scn.GetLSStartingLocation()));
        assertFalse(scn.LSHasCardChoiceAvailable(scn.GetLSCard("luke")));
        scn.LSChooseCard(scn.GetLSCard("chewie"));
        scn.PassAllResponses();
        assertEquals(scn.GetLSCard("chewie"), scn.GetLSCard("thatsone").getAttachedTo());
        assertEquals(force - 2, scn.GetLSForcePileCount());
        assertInZone(Zone.HAND, scn.GetLSCard("h1"));
        assertInZone(Zone.HAND, scn.GetLSCard("h2"));
    }

    @Test
    public void ThatsOneMayDeployOnBackDoor() {
        var scn = DeployReady(5);
        scn.LSPlayCard(scn.GetLSCard("thatsone"));
        scn.LSChooseCard(scn.GetLSStartingLocation());
        scn.PassAllResponses();
        assertEquals(scn.GetLSStartingLocation(), scn.GetLSCard("thatsone").getAttachedTo());
    }

    @Test
    public void ThatsOneRequiresTwoAvailableForce() {
        var scn = DeployReady(1);
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertFalse(scn.LSCardPlayAvailable(scn.GetLSCard("thatsone")));
    }

    @Test
    public void ThatsOneMayChooseZeroCardsAfterSelectingPlacement() {
        var scn = PlacementReady();
        int force = scn.GetLSForcePileCount();
        scn.LSUseCardAction(scn.GetLSCard("thatsone"), "Place up to two cards");
        assertEquals(0, scn.LSGetChoiceMin());
        scn.LSChooseCards();
        scn.PassAllResponses();
        assertTrue(scn.AwaitingLSDeployPhaseActions() || scn.AwaitingDSDeployPhaseActions());
        assertEquals(force, scn.GetLSForcePileCount());
        assertInZone(Zone.HAND, scn.GetLSCard("h1"));
    }

    @Test
    public void ThatsOnePlacesOneCardOnTopOfForcePile() {
        var scn = PlacementReady();
        int force = scn.GetLSForcePileCount();
        scn.LSUseCardAction(scn.GetLSCard("thatsone"), "Place up to two cards");
        scn.LSChooseCards(scn.GetLSCard("h1"));
        scn.PassAllResponses();
        scn.LSChooseCards();
        scn.PassAllResponses();
        assertTrue(scn.AwaitingLSDeployPhaseActions() || scn.AwaitingDSDeployPhaseActions());
        assertEquals(force + 1, scn.GetLSForcePileCount());
        assertEquals(scn.GetLSCard("h1"), scn.GetTopOfLSForcePile());
        assertInZone(Zone.HAND, scn.GetLSCard("h2"));
    }

    @Test
    public void ThatsOnePlacesAtMostTwoHandCardsOnForcePile() {
        var scn = PlacementReady();
        int force = scn.GetLSForcePileCount();
        scn.LSUseCardAction(scn.GetLSCard("thatsone"), "Place up to two cards");
        scn.LSChooseCards(scn.GetLSCard("h1"));
        scn.PassAllResponses();
        assertTrue(scn.LSDecisionAvailable("Choose card to put on Force Pile"));
        scn.LSChooseCards(scn.GetLSCard("h2"));
        scn.PassAllResponses();
        assertEquals(force + 2, scn.GetLSForcePileCount());
        assertEquals(scn.GetLSCard("h2"), scn.GetTopOfLSForcePile());
        assertEquals(scn.GetLSCard("h1"), scn.GetLSForcePile().get(1));
        assertTrue(scn.AwaitingLSDeployPhaseActions() || scn.AwaitingDSDeployPhaseActions());
        assertInZone(Zone.FORCE_PILE, scn.GetLSCard("h1"));
        assertInZone(Zone.FORCE_PILE, scn.GetLSCard("h2"));
        assertInZone(Zone.HAND, scn.GetLSCard("h3"));
    }

    @Test
    public void ThatsOneAddsToYourBattleDestinyEvenAtAnotherSite() {
        var scn = BattleReady(false);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(scn.GetLSCard("forest"));
        scn.PassAllResponses();
        scn.PrepareLSDestiny(3);
        scn.SkipToPowerSegment();
        scn.PassAllResponses();
        scn.LSChooseYes();
        scn.PassAllResponses();
        scn.DSChooseNo();
        scn.PassAllResponses();
        scn.SkipToDamageSegment(false);
        scn.PassAllResponses();
        assertEquals(4, scn.GetUnpaidDSAttrition());
    }

    @Test
    public void ThatsOneDoesNotAddToOpponentsBattleDestiny() {
        var scn = BattleReady(false);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(scn.GetLSCard("forest"));
        scn.PassAllResponses();
        scn.PrepareDSDestiny(3);
        scn.SkipToPowerSegment();
        scn.PassAllResponses();
        scn.LSChooseNo();
        scn.PassAllResponses();
        scn.DSChooseYes();
        scn.PassAllResponses();
        scn.SkipToDamageSegment(false);
        scn.PassAllResponses();
        // Vader adds one to the opponent's draw; this Effect must not add another.
        assertEquals(4, scn.GetUnpaidLSAttrition());
    }

    @Test
    public void ThatsOnePlacesItselfOutOfPlayToCancelBattleJustInitiatedHere() {
        var scn = BeginBattle(true);
        assertTrue(scn.LSCardActionAvailable(scn.GetLSCard("thatsone"), "Place out of play"));
        scn.LSUseCardAction(scn.GetLSCard("thatsone"), "Place out of play");
        scn.PassAllResponses();
        assertInZone(Zone.OUT_OF_PLAY, scn.GetLSCard("thatsone"));
        org.junit.Assert.assertNull(scn.gameState().getBattleState());
    }

    @Test
    public void ThatsOneCannotCancelBattleAtAnotherSite() {
        var scn = BeginBattle(false);
        AssertNoCancelOffered(scn, "thatsone");
        assertInZone(Zone.ATTACHED, scn.GetLSCard("thatsone"));
    }

    @Test
    public void ThatsOneAnotherCopyCannotCancelAgainInTheSameGame() {
        var scn = BeginBattle(true);
        scn.LSUseCardAction(scn.GetLSCard("thatsone"), "Place out of play");
        scn.PassAllResponses();
        assertInZone(Zone.OUT_OF_PLAY, scn.GetLSCard("thatsone"));
        org.junit.Assert.assertNull(scn.gameState().getBattleState());
        scn.AttachCardsTo(scn.GetLSCard("chewie"), scn.GetLSCard("second"));
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSUseCardAction(scn.GetLSStartingLocation(), "Initiate battle");
        scn.PassForceUseResponses();
        if (scn.DSDecisionAvailable("Optional responses")) scn.DSPass();
        AssertNoCancelOffered(scn, "second");
        assertInZone(Zone.ATTACHED, scn.GetLSCard("second"));
    }

    private void AssertNoCancelOffered(VirtualTableScenario scn, String key) {
        if (scn.LSDecisionAvailable("Optional responses")) {
            assertFalse(scn.LSCardActionAvailable(scn.GetLSCard(key), "Place out of play"));
            scn.PassAllResponses();
        }
        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
    }

    private VirtualTableScenario DeployReady(int force) {
        var scn = GetScenario();
        scn.StartGame();
        scn.SkipToLSTurn(Phase.ACTIVATE);
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("chewie"), scn.GetLSCard("luke"));
        scn.MoveCardsToLSHand(scn.GetLSCard("thatsone"), scn.GetLSCard("h1"), scn.GetLSCard("h2"), scn.GetLSCard("h3"));
        scn.LSChooseAction("Activate Force");
        int activated = Math.min(scn.GetLSReserveDeckCount() - 1, scn.LSGetChoiceMax());
        scn.LSDecided(activated);
        if (scn.DSDecisionAvailable("Choose amount of Force to allow opponent")) scn.DSDecided(activated);
        scn.EnsureLSForcePile(force);
        scn.DrainLSForcePileTo(force);
        scn.PassAllResponses();
        scn.PassActivateActions();
        scn.PassAllResponses();
        assertTrue(scn.AwaitingLSControlPhaseActions());
        scn.SkipToPhase(Phase.DEPLOY);
        return scn;
    }

    private VirtualTableScenario PlacementReady() {
        var scn = DeployReady(5);
        scn.LSPlayCard(scn.GetLSCard("thatsone"));
        scn.LSChooseCard(scn.GetLSCard("chewie"));
        scn.PassCardAndForceUseResponses();
        scn.PassCardAndForceUseResponses();
        scn.PassResponses("Deploying <div");
        if (scn.DSDecisionAvailable("Optional responses")) scn.DSPass();
        assertTrue(scn.LSCardActionAvailable(scn.GetLSCard("thatsone"), "Place up to two cards"));
        return scn;
    }

    private VirtualTableScenario BattleReady(boolean here) {
        var scn = GetScenario();
        scn.StartGame();
        scn.MoveLocationToTable(scn.GetLSCard("forest"));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("chewie"));
        scn.AttachCardsTo(scn.GetLSCard("chewie"), scn.GetLSCard("thatsone"));
        scn.MoveCardsToLocation(here ? scn.GetLSStartingLocation() : scn.GetLSCard("forest"), scn.GetLSCard("luke"), scn.GetDSCard("vader"));
        scn.EnsureLSForcePile(5);
        scn.EnsureDSForcePile(5);
        return scn;
    }

    private VirtualTableScenario BeginBattle(boolean here) {
        var scn = BattleReady(here);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSUseCardAction(here ? scn.GetLSStartingLocation() : scn.GetLSCard("forest"), "Initiate battle");
        scn.PassForceUseResponses();
        if (scn.DSDecisionAvailable("Optional responses")) scn.DSPass();
        return scn;
    }
}
