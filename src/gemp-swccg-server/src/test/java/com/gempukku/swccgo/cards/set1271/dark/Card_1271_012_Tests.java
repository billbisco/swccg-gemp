package com.gempukku.swccgo.cards.set1271.dark;

import com.gempukku.swccgo.cards.set1271.LegacyShuffleObserver;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class Card_1271_012_Tests {
    private static final String TAKE_AGENT = "Take an ISB agent with Imperial Security Bureau";
    private static final String TAKE_MISSION = "Take a Setup Mission into hand";

    private VirtualTableScenario scenario() {
        var dark = new HashMap<>(Map.ofEntries(
            Map.entry("bureau", "1271_12"), Map.entry("isbSite", "1272_11"),
            Map.entry("leader", "1272_2"), Map.entry("trooper", "1_194"),
            Map.entry("look", "1_259"), Map.entry("advisor", "6_160"), Map.entry("unrelated", "1_234"),
            Map.entry("mission", "1271_25"), Map.entry("mission2", "1271_26"), Map.entry("setupEffect", "1271_10"),
            Map.entry("system", "1_282"), Map.entry("sunsdown", "1_230")));
        StartingSetup passAdvisor = new StartingSetup() {
            @Override public HashMap<String, String> Cards() { return new HashMap<>(); }
            @Override public void Setup(VirtualTableScenario s) {
                if (s.DSDecisionAvailable("starting interrupt")) s.DSPass();
            }
        };
        return new VirtualTableScenario(new HashMap<>(), dark, 40, 40,
            StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
            StartingSetup.NoLSStartingInterrupts, passAdvisor,
            StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    private VirtualTableScenario ready(boolean active) {
        var s = scenario();
        s.StartGame();
        s.MoveLocationToTable(s.GetDSCard("isbSite"));
        s.MoveLocationToTable(s.GetDSCard("system"));
        if (active) s.MoveCardsToDSSideOfTable(s.GetDSCard("bureau"));
        s.SkipToDSTurn(Phase.CONTROL);
        s.EnsureDSForcePile(12);
        return s;
    }

    private boolean selectable(VirtualTableScenario s, String name) {
        var ids = s.DSGetADParam("blueprintId");
        var allowed = s.DSGetADParam("selectable");
        assertNotNull(ids);
        assertNotNull(allowed);
        for (int i = 0; i < ids.length; i++) {
            if (ids[i].equals(s.GetDSCard(name).getBlueprintId(true)) && Boolean.parseBoolean(allowed[i])) return true;
        }
        return false;
    }

    @Test public void ImperialSecurityBureauPrintedStatsIconsAndKeywordsAreCorrect() {
        var s = scenario();
        var b = s.GetDSCard("bureau").getBlueprint();
        assertEquals("Imperial Security Bureau", b.getTitle());
        assertEquals(Side.DARK, b.getSide());
        assertEquals(1, b.getDestiny(), s.epsilon);
        assertEquals(Uniqueness.UNIQUE, b.getUniqueness());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_1, b.getExpansionSet());
        assertEquals(Rarity.V, b.getRarity());
        assertFalse(b.hasVirtualSuffix());
        assertTrue(b.getLore() == null || b.getLore().isBlank());
        s.BlueprintCardTypeCheck(b, List.of(CardType.EFFECT));
        s.BlueprintKeywordCheck(b, List.of());
        s.BlueprintIconCheck(b, List.of(Icon.EFFECT, Icon.CLOUD_CITY, Icon.LEGACY_BLOCK_7));
    }

    private void deployCharacter(String name, boolean atOffice, int cost) {
        var s = ready(true);
        var card = s.GetDSCard(name);
        var site = atOffice ? s.GetDSCard("isbSite") : s.GetDSStartingLocation();
        s.MoveCardsToDSHand(card);
        s.SkipToPhase(Phase.DEPLOY);
        int force = s.GetDSForcePileCount();
        s.DSDeployCard(card);
        if (s.DSHasCardChoiceAvailable(site)) s.DSChooseCard(site);
        s.PassAllResponses();
        assertTrue(s.CardsAtLocation(site, card));
        assertEquals(force - cost, s.GetDSForcePileCount());
    }

    @Test public void ImperialSecurityBureauDiscountsActualIsbLeaderDeploymentAtAnIsbSite() { deployCharacter("leader", true, 1); }
    @Test public void ImperialSecurityBureauDoesNotDiscountAnIsbLeaderAtAnotherSite() { deployCharacter("leader", false, 3); }
    @Test public void ImperialSecurityBureauDoesNotDiscountAnUnqualifiedTrooperAtAnIsbSite() { deployCharacter("trooper", true, 1); }

    @Test public void ImperialSecurityBureauActuallyTakesOnlySetupMissionsAndReshufflesOncePerGame() {
        var s = ready(true);
        var bureau = s.GetDSCard("bureau");
        var observer = LegacyShuffleObserver.observe(s);
        s.DSUseCardAction(bureau, TAKE_MISSION);
        s.PassAllResponses();
        assertTrue(selectable(s, "mission"));
        assertTrue(selectable(s, "mission2"));
        assertFalse(selectable(s, "setupEffect"));
        assertFalse(selectable(s, "leader"));
        s.DSChooseCard(s.GetDSCard("mission"));
        s.PassAllResponses();
        assertEquals(Zone.HAND, s.GetDSCard("mission").getZone());
        assertTrue(observer.sawOwnedShuffle(s.DS, Zone.RESERVE_DECK));
        assertFalse(s.DSCardActionAvailable(bureau, TAKE_MISSION));
    }

    private void grant(String name) {
        var s = ready(true);
        var interrupt = s.GetDSCard(name);
        s.MoveCardsToDSHand(interrupt);
        s.SkipToPhase(Phase.DEPLOY);
        int force = s.GetDSForcePileCount();
        var observer = LegacyShuffleObserver.observe(s);
        s.DSPlayCard(interrupt, TAKE_AGENT);
        s.PassAllResponses();
        assertTrue(selectable(s, "leader"));
        assertFalse(selectable(s, "trooper"));
        s.DSChooseCard(s.GetDSCard("leader"));
        s.PassAllResponses();
        assertEquals(force - 1, s.GetDSForcePileCount());
        assertEquals(Zone.HAND, s.GetDSCard("leader").getZone());
        assertTrue(s.GetDSUsedPile().contains(interrupt));
        assertFalse(s.GetDSLostPile().contains(interrupt));
        assertTrue(observer.sawOwnedShuffle(s.DS, Zone.RESERVE_DECK));
    }

    @Test public void ImperialSecurityBureauGrantsActualUsedModeToLostLookSirDroids() { grant("look"); }
    @Test public void ImperialSecurityBureauGrantsAnActualAlternateModeToAnAdvisorInterrupt() { grant("advisor"); }

    @Test public void ImperialSecurityBureauDoesNotGrantModeToAnUnrelatedInterruptOrWhenItIsAbsent() {
        var s = ready(false);
        s.MoveCardsToDSHand(s.GetDSCard("look"), s.GetDSCard("advisor"), s.GetDSCard("unrelated"));
        s.SkipToPhase(Phase.DEPLOY);
        assertFalse(s.DSCardPlayAvailable(s.GetDSCard("look"), TAKE_AGENT));
        assertFalse(s.DSCardPlayAvailable(s.GetDSCard("advisor"), TAKE_AGENT));

        var active = ready(true);
        active.MoveCardsToDSHand(active.GetDSCard("unrelated"));
        active.SkipToPhase(Phase.DEPLOY);
        assertFalse(active.DSCardPlayAvailable(active.GetDSCard("unrelated"), TAKE_AGENT));
    }

    @Test public void ImperialSecurityBureauGrantedModeRequiresOneForce() {
        var s = ready(true);
        s.MoveCardsToDSHand(s.GetDSCard("look"), s.GetDSCard("advisor"));
        s.DrainDSForcePileTo(0);
        s.SkipToPhase(Phase.DEPLOY);
        assertFalse(s.DSCardPlayAvailable(s.GetDSCard("look"), TAKE_AGENT));
        assertFalse(s.DSCardPlayAvailable(s.GetDSCard("advisor"), TAKE_AGENT));
    }

    @Test public void ImperialSecurityBureauCancelsAnActualSunsdownBeingDeployed() {
        var s = ready(true);
        var sunsdown = s.GetDSCard("sunsdown");
        s.MoveCardsToDSHand(sunsdown);
        s.SkipToPhase(Phase.DEPLOY);
        s.DSDeployCard(sunsdown);
        if (s.DSHasCardChoiceAvailable(s.GetDSCard("system"))) s.DSChooseCard(s.GetDSCard("system"));
        s.PassAllResponses();
        assertTrue(s.GetDSLostPile().contains(sunsdown));
        assertNull(sunsdown.getAttachedTo());
    }

    @Test public void ImperialSecurityBureauActuallyDeploysAndCancelsAnExistingSunsdown() {
        var s = ready(false);
        var sunsdown = s.GetDSCard("sunsdown");
        var bureau = s.GetDSCard("bureau");
        s.AttachCardsTo(s.GetDSCard("system"), sunsdown);
        s.MoveCardsToDSHand(bureau);
        s.SkipToPhase(Phase.DEPLOY);
        s.DSDeployCard(bureau);
        s.PassAllResponses();
        if (s.DSHasCardChoiceAvailable(sunsdown)) s.DSChooseCard(sunsdown);
        s.PassAllResponses();
        assertEquals(Zone.SIDE_OF_TABLE, bureau.getZone());
        assertTrue(s.GetDSLostPile().contains(sunsdown));
    }
}
