package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import static org.junit.Assert.*;

public class Card_1261_056_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{ put("xwing", "1_146"); put("xwingtwo", "1_146"); put("xwingthree", "1_146"); put("xwingfour", "1_146"); put("cocky", "1_76"); }},
                new HashMap<>() {{ put("black", "1261_56"); put("juno", "601_89"); put("vader", "1_168");
                    put("blacktwo", "1_299"); put("tie", "1_304"); put("destroyer", "1_301"); }}, 30, 30,
                StartingSetup.DefaultLSSpaceSystem, StartingSetup.DefaultDSSpaceSystem,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void BlackOnePrintedStatsIconsAndKeywordsMatchFinalLegacyMaster() {
        var scn = scenario(); var b = scn.GetDSCard("black").getBlueprint();
        assertEquals("Black 1", b.getTitle()); assertFalse(b.hasVirtualSuffix());
        assertEquals(Side.DARK, b.getSide()); assertEquals(Uniqueness.UNIQUE, b.getUniqueness());
        assertEquals(2, b.getDestiny(), scn.epsilon); assertEquals(2, b.getDeployCost(), scn.epsilon); assertEquals(2, b.getPower(), scn.epsilon);
        assertEquals(5, b.getManeuver(), scn.epsilon); assertEquals(3, b.getHyperspeed(), scn.epsilon); assertEquals(5, b.getForfeit(), scn.epsilon);
        assertEquals(1, b.getPilotCapacity()); assertEquals(0, b.getPassengerCapacity());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, b.getExpansionSet()); assertEquals(Rarity.V, b.getRarity());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>() {{ add(CardType.STARSHIP); }});
        scn.BlueprintModelTypeCheck(b, new ArrayList<>() {{ add(ModelType.TIE_ADVANCED_X1); }});
        scn.BlueprintKeywordCheck(b, new ArrayList<>() {{ add(Keyword.BLACK_SQUADRON); }});
        scn.BlueprintIconCheck(b, new ArrayList<>() {{ add(Icon.STARSHIP); add(Icon.NAV_COMPUTER); add(Icon.SCOMP_LINK); add(Icon.LEGACY_BLOCK_6); }});
    }

    @Test
    public void BlackOneAcceptsJunoButNotAnotherPilotAndHasNoPermanentPilot() {
        var scn = scenario(); scn.StartGame(); var black = scn.GetDSCard("black");
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), black);
        var pilots = black.getBlueprint().getValidPilotFilter(scn.DS, scn.game(), black, true);
        assertTrue(pilots.accepts(scn.game(), scn.GetDSCard("juno"))); assertFalse(pilots.accepts(scn.game(), scn.GetDSCard("vader")));
        assertEquals(0, scn.GetBattleDestinyAbility(black));
        scn.BoardAsPilot(black, scn.GetDSCard("juno")); assertEquals(3, scn.GetBattleDestinyAbility(black));
    }

    private VirtualTableScenario battle(boolean natural) {
        var scn = scenario(); scn.StartGame(); var system = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(system, scn.GetDSCard("black"), scn.GetLSCard("xwing"));
        scn.BoardAsPilot(scn.GetDSCard("black"), scn.GetDSCard("juno"));
        if (natural) scn.MoveCardsToLocation(system, scn.GetDSCard("tie"));
        scn.DSStartBattleAndSkipToWeaponsSegment(system); return scn;
    }

    @Test public void BlackOneGrantsOneBattleDestinyBelowFourAbility() { assertEquals(1, battle(false).GetDSBattleDestinyCount()); }
    @Test public void BlackOneDoesNotAddAnExtraDestinyWhenAlreadyAbleToDraw() { assertEquals(1, battle(true).GetDSBattleDestinyCount()); }

    @Test
    public void BlackOneProtectsOtherTiesGloballyButNotNonTies() {
        var scn = scenario(); scn.StartGame(); var black = scn.GetDSCard("black"); var tie = scn.GetDSCard("tie");
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), black, scn.GetDSCard("destroyer"));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), tie, scn.GetLSCard("xwing"));
        var q = scn.game().getModifiersQuerying();
        assertTrue(q.isImmuneToCardTitle(scn.gameState(), tie, Title.Dont_Get_Cocky));
        assertTrue(q.isImmuneToCardTitle(scn.gameState(), black, Title.Dont_Get_Cocky));
        assertFalse(q.isImmuneToCardTitle(scn.gameState(), scn.GetDSCard("destroyer"), Title.Dont_Get_Cocky));
        assertFalse(q.isImmuneToCardTitle(scn.gameState(), scn.GetLSCard("xwing"), Title.Dont_Get_Cocky));
        scn.MoveCardsToDSHand(black); assertFalse(q.isImmuneToCardTitle(scn.gameState(), tie, Title.Dont_Get_Cocky));
    }

    private VirtualTableScenario search() {
        var scn = scenario(); scn.StartGame(); scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("black"));
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSCard("blacktwo"), scn.GetDSCard("tie"), scn.GetDSCard("juno")); return scn;
    }

    private boolean selectable(VirtualTableScenario scn, com.gempukku.swccgo.game.PhysicalCardImpl card) {
        var bp = scn.GetADParam(scn.DS, "blueprintId"); var ids = scn.GetADParam(scn.DS, "cardId"); var allowed = scn.GetADParam(scn.DS, "selectable");
        int n = bp != null ? bp.length : ids.length;
        for (int i = 0; i < n; i++) {
            boolean match = bp != null ? bp[i].equals(card.getBlueprintId(true)) : ids[i].equals(String.valueOf(card.getCardId()));
            if (match) return allowed == null || Boolean.parseBoolean(allowed[i]);
        }
        return false;
    }

    @Test
    public void BlackOneActuallyUploadsOnlyBlackSquadronTieForOneForceOncePerTurn() {
        var scn = search(); var black = scn.GetDSCard("black"); var target = scn.GetDSCard("blacktwo");
        int force = scn.GetDSForcePileCount(); scn.DSUseCardAction(black, "Take Black Squadron TIE"); scn.PassAllResponses();
        assertTrue(selectable(scn, target)); assertFalse(selectable(scn, scn.GetDSCard("tie"))); assertFalse(selectable(scn, scn.GetDSCard("juno")));
        scn.DSChooseCard(target); scn.PassAllResponses(); assertEquals(Zone.HAND, target.getZone());
        assertEquals(force - 1, scn.GetDSForcePileCount());
        if (scn.LSGetDecision() != null) scn.LSPass(); assertFalse(scn.DSCardActionAvailable(black, "Take Black Squadron TIE"));
    }

    @Test
    public void BlackOneCannotUploadWithoutOneForceInFreshActionWindow() {
        var scn = search(); scn.DrainDSForcePileTo(0); scn.SkipToPhase(Phase.DEPLOY);
        assertEquals(0, scn.GetDSForcePileCount()); assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("black"), "Take Black Squadron TIE"));
    }
    private void attritionBoundary(int destiny, boolean immune) {
        var scn = scenario(); scn.StartGame(); var black = scn.GetDSCard("black"); var system = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(system, black, scn.GetLSCard("xwing"), scn.GetLSCard("xwingtwo"), scn.GetLSCard("xwingthree"), scn.GetLSCard("xwingfour"));
        scn.BoardAsPilot(black, scn.GetDSCard("juno")); scn.DSStartBattleAndSkipToWeaponsSegment(system);
        scn.PrepareDSDestiny(0); scn.PrepareLSDestiny(destiny); scn.SkipToDamageSegment(true); scn.PassDamageSegmentActions();
        scn.DSPayRemainingBattleDamageFromReserveDeck(); scn.PassAllResponses();
        assertEquals(immune ? "0" : "1", scn.GetADParam(scn.DS, "min")[0]);
        if (immune) { scn.DSPass(); scn.PassAllResponses(); assertEquals(system, black.getAtLocation()); }
        else { scn.DSPayAttritionFromCardInPlay(black); scn.PassAllResponses(); com.gempukku.swccgo.framework.Assertions.assertInZone(Zone.LOST_PILE, black); }
    }

    @Test
    public void BlackOneActuallySurvivesFourAttritionButMustSatisfyFive() {
        attritionBoundary(4, true); attritionBoundary(5, false);
    }

}
