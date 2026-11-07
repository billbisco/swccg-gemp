package com.gempukku.swccgo.cards.set1271.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class Card_1271_023_Tests {
    private VirtualTableScenario scenario() {
        var light = new HashMap<>(Map.of("alderaan", "1_121", "luke", "1_19", "rough", "10_26_BACK"));
        var dark = new HashMap<>(Map.ofEntries(
            Map.entry("warRoom", "1271_23"), Map.entry("deathStar", "2_143"), Map.entry("bay", "1_285"),
            Map.entry("executorSite", "4_163"), Map.entry("bespin", "5_164"), Map.entry("devastator", "1_301"),
            Map.entry("trooper", "1_194"), Map.entry("presence", "1_227"), Map.entry("coruscantWipe", "12_143")));
        return new VirtualTableScenario(light, dark, 40, 40, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
            StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
            StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    private VirtualTableScenario board(boolean blownAway) {
        var s = scenario();
        s.StartGame();
        s.MoveLocationToTable(s.GetDSCard("deathStar"));
        s.MoveLocationToTable(s.GetDSCard("warRoom"));
        s.MoveLocationToTable(s.GetDSCard("bay"));
        var alderaan = s.GetLSCard("alderaan");
        s.MoveLocationToTable(alderaan);
        if (blownAway) {
            s.game().getModifiersQuerying().blownAway(alderaan);
            s.gameState().flipBlownAwayLocations(s.game(), List.of(alderaan));
        }
        return s;
    }

    @Test public void DeathStarWarRoomPrintedStatsIconsAndKeywordsAreCorrect() {
        var s = scenario();
        var b = s.GetDSCard("warRoom").getBlueprint();
        assertEquals(Title.Death_Star_War_Room, b.getTitle());
        assertEquals(Side.DARK, b.getSide());
        assertEquals(Uniqueness.UNIQUE, b.getUniqueness());
        assertEquals(CardSubtype.SITE, b.getCardSubtype());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_1, b.getExpansionSet());
        assertEquals(Rarity.V, b.getRarity());
        assertTrue(b.hasVirtualSuffix());
        assertEquals(2, b.getIconCount(Icon.DARK_FORCE));
        assertEquals(0, b.getIconCount(Icon.LIGHT_FORCE));
        s.BlueprintCardTypeCheck(b, List.of(CardType.LOCATION));
        s.BlueprintKeywordCheck(b, List.of(Keyword.WAR_ROOM));
        s.BlueprintIconCheck(b, List.of(Icon.DARK_FORCE, Icon.INTERIOR_SITE, Icon.MOBILE, Icon.SCOMP_LINK, Icon.LEGACY_BLOCK_7));
    }

    private void move(boolean blownAway, boolean toDeathStar) {
        var s = board(blownAway);
        var deathStar = s.GetDSCard("deathStar");
        var bespin = s.GetDSCard("bespin");
        var ship = s.GetDSCard("devastator");
        s.MoveLocationToTable(bespin);
        deathStar.setSystemOrbited(Title.Bespin);
        var destination = toDeathStar ? deathStar : bespin;
        s.MoveCardsToLocation(toDeathStar ? bespin : deathStar, ship);
        s.SkipToDSTurn(Phase.CONTROL);
        s.EnsureDSForcePile(4);
        s.SkipToPhase(Phase.MOVE);
        int force = s.GetDSForcePileCount();
        s.DSUseCardAction(ship, "Move without using hyperspeed");
        if (s.DSHasCardChoiceAvailable(destination)) s.DSChooseCard(destination);
        s.PassAllResponses();
        assertTrue(s.CardsAtLocation(destination, ship));
        assertEquals(force - (blownAway ? 0 : 1), s.GetDSForcePileCount());
    }

    @Test public void DeathStarWarRoomMakesActualMovementFromDeathStarFreeAfterAlderaanIsBlownAway() { move(true, false); }
    @Test public void DeathStarWarRoomMakesActualMovementToDeathStarFreeAfterAlderaanIsBlownAway() { move(true, true); }
    @Test public void DeathStarWarRoomLeavesNormalMovementCostBeforeAlderaanIsBlownAway() { move(false, false); }

    private void drain(boolean blownAway, boolean atDeathStar) {
        var s = board(blownAway);
        var site = atDeathStar ? s.GetDSCard("bay") : s.GetDSStartingLocation();
        s.MoveCardsToLocation(site, s.GetDSCard("trooper"));
        s.SkipToLSTurn(Phase.CONTROL);
        s.SkipToDSTurn(Phase.CONTROL);
        s.DSForceDrainAt(site);
        s.PassAllResponses();
        boolean lossPending = s.LSDecisionAvailable("Choose Force to lose");
        assertEquals("An uncanceled drain must reach Light Force loss", !(blownAway && atDeathStar), lossPending);
    }

    @Test public void DeathStarWarRoomCancelsAnActualDarkDrainAtAnotherDeathStarSiteAfterAlderaanIsBlownAway() { drain(true, true); }
    @Test public void DeathStarWarRoomLeavesDeathStarDrainAvailableBeforeAlderaanIsBlownAway() { drain(false, true); }
    @Test public void DeathStarWarRoomDoesNotCancelAnActualDrainAtAnUnrelatedSite() { drain(true, false); }

    private void battle(boolean blownAway, boolean atDeathStar) {
        var s = board(blownAway);
        var site = atDeathStar ? s.GetDSCard("bay") : s.GetDSStartingLocation();
        s.MoveCardsToLocation(site, s.GetDSCard("trooper"), s.GetLSCard("luke"));
        s.SkipToDSTurn(Phase.CONTROL);
        s.EnsureDSForcePile(4);
        s.PrepareLSDestiny(2);
        s.SkipToPhase(Phase.BATTLE);
        s.DSInitiateBattle(site);
        s.SkipToDamageSegment(true);
        assertEquals(2, s.GetUnpaidDSAttrition());
        assertEquals(blownAway && atDeathStar ? 0 : 4, s.GetUnpaidDSBattleDamage());
    }

    @Test public void DeathStarWarRoomPreventsDarkBattleDamageButKeepsAttritionAtDeathStarSites() { battle(true, true); }
    @Test public void DeathStarWarRoomDoesNotPreventBattleDamageBeforeAlderaanIsBlownAway() { battle(false, true); }
    @Test public void DeathStarWarRoomDoesNotPreventBattleDamageAtUnrelatedSites() { battle(true, false); }

    @Test public void DeathStarWarRoomRemainsNonBattlegroundAfterAnActualEffectAddsALightIcon() {
        var s = board(false);
        var room = s.GetDSCard("warRoom");
        var presence = s.GetDSCard("presence");
        s.SkipToDSTurn(Phase.CONTROL);
        s.MoveCardsToDSHand(presence);
        s.SkipToPhase(Phase.DEPLOY);
        s.DSDeployCard(presence);
        s.DSChooseCard(room);
        s.PassAllResponses();
        assertEquals(room, presence.getAttachedTo());
        assertEquals(1, s.GetLSIconsOnLocation(room));
        assertFalse(Filters.battleground.accepts(s.game(), room));
        assertTrue(Filters.battleground.accepts(s.game(), s.GetDSCard("bay")));
    }

    @Test public void DeathStarWarRoomBlocksActualLightSideGenerationLimitAtMobileLocationsExceptExecutorSites() {
        var s = board(false);
        var chamber = s.GetDSCard("executorSite");
        s.MoveLocationToTable(chamber);
        var rough = s.GetLSCard("rough");
        s.MoveCardsToLSSideOfTable(rough);
        var q = s.game().getModifiersQuerying();
        assertEquals(2, q.getForceGenerationFromLocation(s.gameState(), s.GetDSCard("warRoom"), s.DS), s.epsilon);
        assertEquals(3, q.getForceGenerationFromLocation(s.gameState(), s.GetDSCard("deathStar"), s.DS), s.epsilon);
        assertEquals(1, q.getForceGenerationFromLocation(s.gameState(), chamber, s.DS), s.epsilon);
    }

    @Test public void DeathStarWarRoomDoesNotCancelTheOriginalCoruscantPrintingOfWipeThemOut() {
        var s = board(false);
        var wipe = s.GetDSCard("coruscantWipe");
        s.SkipToDSTurn(Phase.CONTROL);
        s.MoveCardsToDSHand(wipe);
        s.SkipToPhase(Phase.DEPLOY);
        s.DSDeployCard(wipe);
        s.PassAllResponses();
        assertEquals(Zone.SIDE_OF_TABLE, wipe.getZone());
    }
}
