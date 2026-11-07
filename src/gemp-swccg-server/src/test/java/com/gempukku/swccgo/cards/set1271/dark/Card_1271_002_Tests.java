package com.gempukku.swccgo.cards.set1271.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;
import static com.gempukku.swccgo.cards.set1271.dark.LegacyVaderScenario.*;

public class Card_1271_002_Tests {
    @Test public void DarthVaderMoreMachineThanManPrintedStatsIconsAndKeywordsAreCorrect() {
        var s = scenario();
        var b = s.GetDSCard("vader").getBlueprint();
        assertEquals("Darth Vader, More Machine Than Man", b.getTitle());
        assertEquals(Side.DARK, b.getSide());
        assertEquals(1, b.getDestiny(), s.epsilon);
        assertEquals(6, b.getDeployCost(), s.epsilon);
        assertEquals(6, b.getPower(), s.epsilon);
        assertEquals(6, b.getAbility(), s.epsilon);
        assertEquals(8, b.getForfeit(), s.epsilon);
        assertEquals(Uniqueness.UNIQUE, b.getUniqueness());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_1, b.getExpansionSet());
        assertFalse(b.hasVirtualSuffix());
        assertEquals(CardCategory.CHARACTER, b.getCardCategory());
        s.BlueprintCardTypeCheck(b, List.of(CardType.IMPERIAL));
        s.BlueprintPersonaCheck(b, List.of(Persona.VADER));
        s.BlueprintKeywordCheck(b, List.of());
        s.BlueprintIconCheck(b, List.of(Icon.IMPERIAL, Icon.VADER, Icon.PILOT, Icon.WARRIOR, Icon.LEGACY_BLOCK_7));
    }

    @Test public void DarthVaderMoreMachineThanManCannotDeployWithoutRiseOfTheDarkLord() { deploy(false); }
    @Test public void DarthVaderMoreMachineThanManActuallyDeploysWithRiseOfTheDarkLord() { deploy(true); }

    private void deploy(boolean rise) {
        var s = scenario();
        var vader = s.GetDSCard("vader");
        s.StartGame();
        if (rise) s.MoveCardsToDSSideOfTable(s.GetDSCard("rise"));
        s.SkipToDSTurn(Phase.CONTROL);
        s.MoveCardsToDSHand(vader);
        s.EnsureDSForcePile(10);
        s.SkipToPhase(Phase.DEPLOY);
        assertEquals(rise, s.DSDeployAvailable(vader));
        if (!rise) return;
        s.DSDeployCard(vader);
        if (s.DSHasCardChoiceAvailable(s.GetDSStartingLocation())) s.DSChooseCard(s.GetDSStartingLocation());
        s.PassAllResponses();
        assertTrue(s.CardsAtLocation(s.GetDSStartingLocation(), vader));
    }

    @Test public void DarthVaderMoreMachineThanManAddsThreePowerWhenHePilots() {
        var s = board(null);
        var ship = s.GetDSCard("customTie");
        var system = s.GetDSCard("system");
        s.MoveLocationToTable(system);
        s.MoveCardsToLocation(system, ship);
        var vader = s.GetDSCard("vader");
        s.MoveCardsToDSHand(vader);
        s.SkipToDSTurn(Phase.CONTROL);
        s.EnsureDSForcePile(10);
        s.SkipToPhase(Phase.DEPLOY);
        s.DSDeployCard(vader);
        s.DSChooseCard(ship);
        s.PassAllResponses();
        assertTrue(s.IsAboardAsPilot(ship, vader));
        assertEquals(5, s.GetPower(ship));
    }

    private void applyAtAnotherBattle(boolean controlsVaderSite) {
        var s = board(null);
        var vader = s.GetDSCard("vader");
        var farm = s.GetLSCard("farm");
        s.MoveCardsToLocation(farm, s.GetDSCard("trooper"), s.GetLSCard("luke"));
        if (!controlsVaderSite) s.MoveCardsToLocation(s.GetDSStartingLocation(), s.GetLSCard("obi"));
        s.SkipToDSTurn(Phase.CONTROL);
        s.EnsureDSForcePile(5);
        s.SkipToPhase(Phase.BATTLE);
        s.StartBattleAndSkipToWeaponsSegment(s.DS, farm);
        s.PrepareDSDestiny(5);
        s.PrepareLSDestiny(0);
        assertEquals(controlsVaderSite, s.DSCardActionAvailable(vader, "Apply Vader's ability"));
        if (controlsVaderSite) {
            s.DSUseCardAction(vader, "Apply Vader's ability");
            s.PassAllResponses();
        }
        s.SkipToDamageSegment(true);
        assertEquals(controlsVaderSite ? 5 : 0, s.GetDSTotalDestiny());
        assertTrue(s.CardsAtLocation(s.GetDSStartingLocation(), vader));
    }

    @Test public void DarthVaderMoreMachineThanManAppliesAbilityToActualBattleAtAnotherSite() {
        applyAtAnotherBattle(true);
    }

    @Test public void DarthVaderMoreMachineThanManCannotApplyAbilityIfHeDoesNotControlHisSite() {
        applyAtAnotherBattle(false);
    }
}
