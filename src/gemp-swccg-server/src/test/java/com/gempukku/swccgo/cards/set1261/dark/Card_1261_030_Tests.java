package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.modifiers.ManeuverModifier;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import static org.junit.Assert.*;

public class Card_1261_030_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("beggar", "1_44"); put("frozen", "5_23"); put("chances", "1_77");
            put("flaps", "7_69"); put("foils", "7_75"); put("speeder", "3_69");
            put("xwing", "1_146"); put("otherXwing", "1_146"); put("luke", "1_19");
            put("weapon", "1_153"); put("farm", "1_132");
        }}, new HashMap<>() {{
            put("shield", "1261_30"); put("fear", "13_69"); put("trooper", "1_194");
            put("weapon", "1_312"); put("vehicle", "7_315"); put("tie", "1_303");
            put("system", "1_282"); put("otherSystem", "1_296");
        }}, 40, 40, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
            StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
            StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }
    private VirtualTableScenario ready(boolean shield) {
        var s = scenario(); s.StartGame(); s.SkipToDSTurn(Phase.ACTIVATE); s.SkipToPhase(Phase.CONTROL);
        s.MoveLocationToTable(s.GetLSCard("farm"));
        if (shield) s.MoveCardsToDSSideOfTable(s.GetDSCard("shield"));
        return s;
    }
    private void playShield(VirtualTableScenario s) {
        var fear=s.GetDSCard("fear"); s.MoveCardsToDSSideOfTable(fear); s.StackCardsOn(fear,s.GetDSCard("shield"));
        s.DSUseCardAction(fear,"Defensive Shield"); s.DSChooseCard(s.GetDSCard("shield")); s.PassAllResponses();
    }
    @Test public void WellLetFateADecideHuhPrintedStatsIconsAndKeywordsAreCorrect() {
        var s=scenario(); var b=s.GetDSCard("shield").getBlueprint();
        assertEquals("We'll Let Fate-a Decide, Huh?",b.getTitle()); assertTrue(b.hasVirtualSuffix());
        assertEquals(0,b.getDestiny(),s.epsilon); assertEquals(Side.DARK,b.getSide());
        assertEquals(Uniqueness.UNIQUE,b.getUniqueness()); assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1,b.getExpansionSet());
        assertEquals(Rarity.V,b.getRarity());
        s.BlueprintCardTypeCheck(b,new ArrayList<>() {{add(CardType.DEFENSIVE_SHIELD);}});
        s.BlueprintIconCheck(b,new ArrayList<>() {{add(Icon.DEFENSIVE_SHIELD);add(Icon.REFLECTIONS_III);add(Icon.EPISODE_I);add(Icon.LEGACY_BLOCK_6);}});
        s.BlueprintKeywordCheck(b,new ArrayList<Keyword>());
    }
    @Test public void WellLetFateADecideHuhActuallyPlaysFromStartingEffectForFree() {
        var s=ready(false);int force=s.GetDSForcePileCount();playShield(s);
        assertEquals(Zone.SIDE_OF_TABLE,s.GetDSCard("shield").getZone());assertEquals(force,s.GetDSForcePileCount());
    }
    @Test public void WellLetFateADecideHuhCancelsAnAlreadyDeployedBeggar() {
        var s=ready(false);var c=s.GetLSCard("beggar");s.AttachCardsTo(s.GetLSCard("farm"),c);
        playShield(s);assertTrue(s.GetLSLostPile().contains(c));
    }
    @Test public void WellLetFateADecideHuhCancelsAlreadyDeployedFrozenAssets() {
        var s=ready(false);var c=s.GetLSCard("frozen");s.MoveCardsToLSSideOfTable(c);
        playShield(s);assertTrue(s.GetLSLostPile().contains(c));
    }
    @Test public void WellLetFateADecideHuhCancelsAnActualBeggarDeploymentAfterItsCost() {
        var s=ready(true);var c=s.GetLSCard("beggar");s.MoveCardsToLSHand(c);s.SkipToLSTurn(Phase.DEPLOY);s.EnsureLSForcePile(5);
        s.LSPlayCard(c);s.LSChooseCard(s.GetLSCard("farm"));s.PassAllResponses();
        assertTrue(s.GetLSLostPile().contains(c));assertEquals(2,s.GetLSForcePileCount());
    }
    @Test public void WellLetFateADecideHuhCancelsAnActualFrozenAssetsPlay() {
        var s=ready(true);var c=s.GetLSCard("frozen");s.MoveCardsToLSHand(c);s.SkipToLSTurn(Phase.DEPLOY);
        s.LSPlayCard(c,"Deploy on your side of table");s.PassAllResponses();assertTrue(s.GetLSLostPile().contains(c));
    }
    @Test public void WellLetFateADecideHuhCancelsTheActualBattleDamageInterrupt() {
        var s=ready(true);var c=s.GetLSCard("chances");s.MoveCardsToLSHand(c);
        s.MoveCardsToLocation(s.GetLSCard("farm"),s.GetDSCard("trooper"),s.GetLSCard("luke"));
        s.SkipToPhase(Phase.BATTLE);s.DSUseCardAction(s.GetLSCard("farm"),"Initiate battle");s.PassForceUseResponses();
        if (s.GetDecidingPlayer().equals(s.DS)) s.DSPass();
        s.LSPlayCard(c);s.PassAllResponses();assertTrue(s.GetLSLostPile().contains(c));
    }
    private VirtualTableScenario flaps() {
        var s=ready(true);s.MoveCardsToLocation(s.GetLSCard("farm"),s.GetLSCard("speeder"));
        s.MoveCardsToLSSideOfTable(s.GetLSCard("flaps"));s.SkipToLSTurn(Phase.CONTROL);s.EnsureLSForcePile(5);
        assertEquals(3,s.GetPower(s.GetLSCard("speeder")));s.LSUseCardAction(s.GetLSCard("flaps"),"Engage Maneuvering Flaps");s.PassAllResponses();
        assertEquals(5,s.GetPower(s.GetLSCard("speeder")));return s;
    }
    @Test public void WellLetFateADecideHuhOwnWeaponPresentSuspendsFlapsAndLeavingRestoresThem() {
        var s=flaps();var trooper=s.GetDSCard("trooper");var weapon=s.GetDSCard("weapon");
        s.MoveCardsToLocation(s.GetLSCard("farm"),trooper);s.AttachCardsTo(trooper,weapon);s.SkipToPhase(Phase.DEPLOY);
        assertEquals(3,s.GetPower(s.GetLSCard("speeder")));s.MoveCardsToDSHand(weapon);s.SkipToPhase(Phase.BATTLE);
        assertEquals(5,s.GetPower(s.GetLSCard("speeder")));
    }
    @Test public void WellLetFateADecideHuhOpponentsWeaponDoesNotSuspendFlaps() {
        var s=flaps();var luke=s.GetLSCard("luke");s.MoveCardsToLocation(s.GetLSCard("farm"),luke);
        s.AttachCardsTo(luke,s.GetLSCard("weapon"));s.SkipToPhase(Phase.DEPLOY);assertEquals(5,s.GetPower(s.GetLSCard("speeder")));
    }
    @Test public void WellLetFateADecideHuhWeaponAtAnotherLocationDoesNotSuspendFlapsHere() {
        var s=flaps();s.MoveCardsToLocation(s.GetDSStartingLocation(),s.GetDSCard("trooper"));
        s.AttachCardsTo(s.GetDSCard("trooper"),s.GetDSCard("weapon"));s.SkipToPhase(Phase.DEPLOY);assertEquals(5,s.GetPower(s.GetLSCard("speeder")));
    }
    @Test public void WellLetFateADecideHuhVehicleManeuverThreeDoesNotSuspendFlaps() {
        var s=flaps();var v=s.GetDSCard("vehicle");s.MoveCardsToLocation(s.GetLSCard("farm"),v);s.SkipToPhase(Phase.DEPLOY);
        assertEquals(3,s.GetManeuver(v));assertEquals(5,s.GetPower(s.GetLSCard("speeder")));
    }
    @Test public void WellLetFateADecideHuhVehicleModifiedManeuverFourSuspendsFlaps() {
        var s=flaps();var v=s.GetDSCard("vehicle");s.MoveCardsToLocation(s.GetLSCard("farm"),v);
        s.ApplyAdHocModifier(new ManeuverModifier(null,Filters.sameCardId(v),null,1));s.SkipToPhase(Phase.DEPLOY);
        assertEquals(4,s.GetManeuver(v));assertEquals(3,s.GetPower(s.GetLSCard("speeder")));
    }
    private VirtualTableScenario foils() {
        var s=ready(true);s.MoveLocationToTable(s.GetDSCard("system"));s.MoveLocationToTable(s.GetDSCard("otherSystem"));
        s.MoveCardsToLocation(s.GetDSCard("system"),s.GetLSCard("xwing"));
        s.MoveCardsToLocation(s.GetDSCard("otherSystem"),s.GetLSCard("otherXwing"));
        s.MoveCardsToLSSideOfTable(s.GetLSCard("foils"));s.SkipToLSTurn(Phase.CONTROL);s.EnsureLSForcePile(5);
        assertEquals(3,s.GetPower(s.GetLSCard("xwing")));s.LSUseCardAction(s.GetLSCard("foils"),"Lock S-foils");s.PassAllResponses();
        assertEquals(5,s.GetPower(s.GetLSCard("xwing")));assertEquals(5,s.GetPower(s.GetLSCard("otherXwing")));return s;
    }
    @Test public void WellLetFateADecideHuhStarshipManeuverThreeLeavesFoilsActive() {
        var s=foils();var tie=s.GetDSCard("tie");s.MoveCardsToLocation(s.GetDSCard("system"),tie);s.SkipToPhase(Phase.DEPLOY);
        assertEquals(3,s.GetManeuver(tie));assertEquals(5,s.GetPower(s.GetLSCard("xwing")));
    }
    @Test public void WellLetFateADecideHuhModifiedStarshipManeuverFourSuspendsOnlyAtItsSystem() {
        var s=foils();var tie=s.GetDSCard("tie");s.MoveCardsToLocation(s.GetDSCard("system"),tie);
        s.ApplyAdHocModifier(new ManeuverModifier(null,Filters.sameCardId(tie),null,1));s.SkipToPhase(Phase.DEPLOY);
        assertEquals(4,s.GetManeuver(tie));assertEquals(3,s.GetPower(s.GetLSCard("xwing")));assertEquals(5,s.GetPower(s.GetLSCard("otherXwing")));
        s.MoveCardsToDSHand(tie);s.SkipToPhase(Phase.BATTLE);assertEquals(5,s.GetPower(s.GetLSCard("xwing")));
    }
    @Test public void WellLetFateADecideHuhWeaponAboardAStarshipIsNotPresentAtTheSystem() {
        var s=foils();var tie=s.GetDSCard("tie");s.MoveCardsToLocation(s.GetDSCard("system"),tie);
        s.AttachCardsTo(tie,s.GetDSCard("trooper"));s.AttachCardsTo(s.GetDSCard("trooper"),s.GetDSCard("weapon"));s.SkipToPhase(Phase.DEPLOY);
        assertEquals(3,s.GetManeuver(tie));assertEquals(5,s.GetPower(s.GetLSCard("xwing")));
    }
    @Test public void WellLetFateADecideHuhDoesNotCancelAnOrdinaryCharacterDeployment() {
        var s=ready(true);var trooper=s.GetDSCard("trooper");s.MoveCardsToDSHand(trooper);
        s.SkipToPhase(Phase.DEPLOY);s.EnsureDSForcePile(5);s.DSDeployCard(trooper);s.DSChooseCard(s.GetDSStartingLocation());s.PassAllResponses();
        assertEquals(s.GetDSStartingLocation(),trooper.getAtLocation());assertEquals(4,s.GetDSForcePileCount());
    }
}
