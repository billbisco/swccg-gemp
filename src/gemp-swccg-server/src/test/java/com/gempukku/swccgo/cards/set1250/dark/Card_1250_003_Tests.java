package com.gempukku.swccgo.cards.set1250.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import java.util.ArrayList;
import java.util.HashMap;
import static org.junit.Assert.*;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;

public class Card_1250_003_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{ put("luke", "1_19"); put("interrupt", "1_115"); }},
                new HashMap<>() {{ put("chokk", "1250_3"); put("trooper", "1_194"); put("droid", "2_99"); put("interrupt", "1_268"); }},
                10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }
    @Test
    public void ChokkStatsIconsAndKeywordsMatchPrintedImage() {
        var scn = scenario(); var bp = scn.GetDSCard("chokk").getBlueprint();
        assertEquals("Chokk", bp.getTitle()); assertTrue(bp.hasVirtualSuffix());
        assertEquals(Side.DARK, bp.getSide()); assertEquals(Uniqueness.UNIQUE, bp.getUniqueness());
        assertEquals(3, bp.getDestiny(), scn.epsilon); assertEquals(3, bp.getDeployCost(), scn.epsilon);
        assertEquals(4, bp.getPower(), scn.epsilon); assertEquals(1, bp.getAbility(), scn.epsilon);
        assertEquals(4, bp.getArmor(), scn.epsilon); assertEquals(3, bp.getForfeit(), scn.epsilon);
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_5, bp.getExpansionSet());
        scn.BlueprintCardTypeCheck(bp, new ArrayList<>() {{ add(CardType.ALIEN); }});
        scn.BlueprintIconCheck(bp, new ArrayList<>() {{ add(Icon.ALIEN); add(Icon.WARRIOR); add(Icon.EPISODE_I); add(Icon.CORUSCANT); add(Icon.LEGACY_BLOCK_5); }});
        scn.BlueprintKeywordCheck(bp, new ArrayList<>() {{ add(Keyword.BODYGUARD); }});
    }
    private void battle(VirtualTableScenario scn, boolean companion) {
        var site = scn.GetDSStartingLocation();
        scn.StartGame(); scn.MoveCardsToLSHand(scn.GetLSCard("interrupt")); scn.MoveCardsToDSHand(scn.GetDSCard("interrupt"));
        scn.MoveCardsToLocation(site, scn.GetDSCard("chokk"), scn.GetLSCard("luke"));
        if (companion) scn.MoveCardsToLocation(site, scn.GetDSCard("trooper"));
        scn.SkipToLSTurn(Phase.BATTLE); scn.LSInitiateBattle(site);
    }
    @Test
    public void ChokkPaysOneForceAndReturnsToUsedPileAfterBattleInitiated() {
        var scn = scenario(); battle(scn, true); var chokk = scn.GetDSCard("chokk");
        assertTrue(scn.DSCardActionAvailable(chokk)); int force = scn.GetDSForcePileCount();
        scn.DSUseCardAction(chokk); scn.PassAllResponses();
        assertEquals(force - 1, scn.GetDSForcePileCount()); assertInZone(Zone.USED_PILE, chokk);
    }
    @Test
    public void ChokkCannotUseAbilityWithoutAnotherFriendlyNonDroid() {
        var scn = scenario(); battle(scn, false);
        assertNoChokkResponse(scn);
    }
    @Test
    public void ChokkMayDeclineAndRemainInBattle() {
        var scn = scenario(); battle(scn, true); var chokk = scn.GetDSCard("chokk");
        assertTrue(scn.DSCardActionAvailable(chokk)); scn.PassAllResponses();
        assertEquals(Zone.AT_LOCATION, chokk.getZone());
    }
    @Test
    public void ChokkDeploysForTwoForceToTatooine() {
        var scn = scenario(); var chokk = scn.GetDSCard("chokk"); var site = scn.GetDSStartingLocation();
        scn.StartGame(); scn.MoveCardsToDSHand(chokk); scn.SkipToDSTurn(Phase.DEPLOY);
        int force = scn.GetDSForcePileCount(); scn.DSDeployCard(chokk); scn.DSChooseCard(site); scn.PassAllResponses();
        assertEquals(force - 2, scn.GetDSForcePileCount()); assertEquals(site, chokk.getAtLocation());
    }
    @Test
    public void ChokkDeploysForThreeForceAwayFromTatooine() {
        var scn = scenario(); var chokk = scn.GetDSCard("chokk"); var site = scn.GetLSStartingLocation();
        scn.StartGame(); scn.MoveCardsToDSHand(chokk); scn.SkipToDSTurn(Phase.DEPLOY);
        int force = scn.GetDSForcePileCount(); scn.DSDeployCard(chokk); scn.DSChooseCard(site); scn.PassAllResponses();
        assertEquals(force - 3, scn.GetDSForcePileCount()); assertEquals(site, chokk.getAtLocation());
    }

    @Test
    public void ChokkBlocksBothPlayersInterruptsUntilWeaponsSegmentEnds() {
        var scn = scenario(); battle(scn, true);
        scn.DSUseCardAction(scn.GetDSCard("chokk")); scn.PassAllResponses();
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        assertFalse(scn.LSCardPlayAvailable(scn.GetLSCard("interrupt")));
        scn.LSPass();
        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
        assertFalse(scn.DSCardPlayAvailable(scn.GetDSCard("interrupt")));
        scn.DSPass();
        assertTrue(scn.gameState().getBattleState().isReachedPowerSegment());
        assertFalse(scn.game().getModifiersQuerying().isPlayingCardProhibited(scn.gameState(), scn.GetLSCard("interrupt"), false));
        assertFalse(scn.game().getModifiersQuerying().isPlayingCardProhibited(scn.gameState(), scn.GetDSCard("interrupt"), false));

    }

    @Test
    public void ChokkDecliningLeavesInterruptsPlayableInWeaponsSegment() {
        var scn = scenario(); battle(scn, true); scn.PassAllResponses();
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        assertTrue(scn.LSCardPlayAvailable(scn.GetLSCard("interrupt")));
        scn.LSPass();
        assertTrue(scn.DSCardPlayAvailable(scn.GetDSCard("interrupt")));
    }

    @Test
    public void ChokkDroidCompanionDoesNotEnableAbility() {
        var scn = scenario(); var site = scn.GetDSStartingLocation();
        scn.StartGame(); scn.MoveCardsToLocation(site, scn.GetDSCard("chokk"), scn.GetDSCard("droid"), scn.GetLSCard("luke"));
        scn.SkipToLSTurn(Phase.BATTLE); scn.LSInitiateBattle(site);
        assertNoChokkResponse(scn);
    }

    @Test
    public void ChokkCannotUseAbilityWithoutOneForce() {
        var scn = scenario(); var site = scn.GetDSStartingLocation();
        scn.StartGame(); scn.MoveCardsToLocation(site, scn.GetDSCard("chokk"), scn.GetDSCard("trooper"), scn.GetLSCard("luke"));
        scn.SkipToLSTurn(Phase.BATTLE); setDarkForce(scn, 0); scn.LSInitiateBattle(site);
        assertEquals(0, scn.GetDSForcePileCount());
        assertNoChokkResponse(scn);
    }

    @Test
    public void ChokkCanRespondToHisOwnersBattleUsingExactlyOneForce() {
        var scn = scenario(); var site = scn.GetDSStartingLocation();
        scn.StartGame(); scn.MoveCardsToLocation(site, scn.GetDSCard("chokk"), scn.GetDSCard("trooper"), scn.GetLSCard("luke"));
        scn.SkipToDSTurn(Phase.BATTLE); setDarkForce(scn, 2); scn.DSInitiateBattle(site);
        assertEquals(1, scn.GetDSForcePileCount());
        assertTrue(scn.DSCardActionAvailable(scn.GetDSCard("chokk")));
        scn.DSUseCardAction(scn.GetDSCard("chokk")); scn.PassAllResponses();
        assertEquals(0, scn.GetDSForcePileCount());
        assertInZone(Zone.USED_PILE, scn.GetDSCard("chokk"));
    }

    @Test
    public void ChokkCompanionAtAnotherSiteDoesNotEnableAbility() {
        var scn = scenario(); var site = scn.GetDSStartingLocation();
        scn.StartGame(); scn.MoveCardsToLocation(site, scn.GetDSCard("chokk"), scn.GetLSCard("luke"));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetDSCard("trooper"));
        scn.SkipToLSTurn(Phase.BATTLE); scn.LSInitiateBattle(site);
        assertNoChokkResponse(scn);
    }

    private void setDarkForce(VirtualTableScenario scn, int amount) {
        scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSForcePile().toArray(PhysicalCardImpl[]::new));
        scn.EnsureDSForcePile(amount);
    }

    private void assertNoChokkResponse(VirtualTableScenario scn) {
        assertFalse(scn.AnyDecisionsAvailable(scn.DS));
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
    }
}
