package com.gempukku.swccgo.cards.set1263.dark;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.filters.Filters;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import static org.junit.Assert.*;

public class Card_1263_006_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{ put("leia", "202_1"); put("ice", "3_37"); }},
                new HashMap<>() {{ put("pirate", "1263_6"); put("veers", "3_87"); put("walker", "3_154"); put("ship", "1_299"); put("system", "1_282"); put("ice", "3_104"); put("sand", "6_148"); put("hoth", "3_144"); put("bay", "3_147"); put("desert", "6_169"); }},
                10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }
    @Test
    public void MercenaryShadowPirateStatsIconsAndKeywordsMatchPrintedImage() {
        var scn = scenario(); var bp = scn.GetDSCard("pirate").getBlueprint();
        assertEquals("Mercenary Shadow Pirate", bp.getTitle()); assertFalse(bp.hasVirtualSuffix());
        assertEquals(Side.DARK, bp.getSide()); assertEquals(Uniqueness.UNRESTRICTED, bp.getUniqueness());
        assertEquals(2, bp.getDestiny(), scn.epsilon); assertEquals(2, bp.getDeployCost(), scn.epsilon);
        assertEquals(2, bp.getPower(), scn.epsilon); assertEquals(2, bp.getAbility(), scn.epsilon); assertEquals(3, bp.getForfeit(), scn.epsilon);
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_3, bp.getExpansionSet());
        scn.BlueprintCardTypeCheck(bp, new ArrayList<>() {{ add(CardType.ALIEN); }});
        scn.BlueprintIconCheck(bp, new ArrayList<>() {{ add(Icon.ALIEN); add(Icon.PILOT); add(Icon.WARRIOR); add(Icon.SPECIAL_EDITION); add(Icon.LEGACY_BLOCK_6); }});
        scn.BlueprintKeywordCheck(bp, new ArrayList<>() {{ add(Keyword.ISB_AGENT); add(Keyword.FEMALE); }});
    }
    @Test
    public void MercenaryShadowPirateAddsTwoPowerToPilotedVehicleButNotAsPassenger() {
        var scn = scenario(); var pirate = scn.GetDSCard("pirate"); var walker = scn.GetDSCard("walker");
        scn.StartGame(); scn.MoveCardsToLocation(scn.GetDSStartingLocation(), walker);
        scn.BoardAsPassenger(walker, pirate); scn.SkipToDSTurn(Phase.BATTLE); assertEquals(7, scn.GetPower(walker));
        scn.BoardAsPilot(walker, pirate); assertEquals(9, scn.GetPower(walker));
        scn.MoveCardsToDSHand(pirate); assertEquals(7, scn.GetPower(walker));
    }
    @Test
    public void MercenaryShadowPirateAddsTwoPowerToPilotedStarship() {
        var scn = scenario(); var ship = scn.GetDSCard("ship");
        scn.StartGame(); scn.MoveLocationToTable(scn.GetDSCard("system"));
        scn.MoveCardsToLocation(scn.GetDSCard("system"), ship); scn.BoardAsPilot(ship, scn.GetDSCard("pirate"));
        scn.SkipToDSTurn(Phase.BATTLE); assertEquals(10, scn.GetPower(ship));
    }
    private VirtualTableScenario battle(boolean pirateHere) {
        var scn = scenario(); var site = scn.GetDSStartingLocation();
        scn.StartGame(); scn.MoveCardsToLocation(site, scn.GetDSCard("veers"), scn.GetLSCard("leia"));
        scn.MoveCardsToLocation(pirateHere ? site : scn.GetLSStartingLocation(), scn.GetDSCard("pirate"));
        scn.LSStartBattleAndSkipToWeaponsSegment(site);
        return scn;
    }
    @Test
    public void MercenaryShadowPirateStopsOpponentAddingDestinyToPowerAndAttritionHere() {
        var scn = battle(true); var leia = scn.GetLSCard("leia");
        assertFalse(scn.LSCardActionAvailable(leia, "Add one destiny to total power"));
        assertFalse(scn.LSCardActionAvailable(leia, "Add one destiny to attrition"));
        assertEquals(1, scn.GetLSBattleDestinyCount());
        assertTrue(GameConditions.canAddDestinyDrawsToPower(scn.game(), scn.DS));
        assertTrue(GameConditions.canAddDestinyDrawsToAttrition(scn.game(), scn.DS));
    }
    @Test
    public void MercenaryShadowPirateAtAnotherSiteLeavesBothOpponentDestinyOptions() {
        var scn = battle(false); var leia = scn.GetLSCard("leia");
        assertTrue(scn.LSCardActionAvailable(leia, "Add one destiny to total power"));
        assertTrue(scn.LSCardActionAvailable(leia, "Add one destiny to attrition"));
    }
    private void resolveWeather(VirtualTableScenario scn, PhysicalCardImpl weather) {
        for (int i=0; i<10; i++) {
            scn.PassAllResponses();
            if (scn.DSCardActionAvailable(scn.GetDSCard("pirate"), "Cancel"))
                scn.DSUseCardAction(scn.GetDSCard("pirate"), "Cancel");
            else if (scn.DSDecisionAvailable("Target card to cancel"))
                scn.DSChooseCard(weather);
            else return;
        }
    }

    private VirtualTableScenario weather(String weatherName, String weatherSite, String pirateSite) {
        var scn = scenario(); var pirate = scn.GetDSCard("pirate");
        scn.StartGame(); scn.MoveLocationToTable(scn.GetDSCard("hoth"));
        scn.MoveLocationToTable(scn.GetDSCard("bay")); scn.MoveLocationToTable(scn.GetDSCard("desert"));
        scn.MoveCardsToDSHand(pirate); scn.SkipToDSTurn(Phase.DEPLOY);
        scn.AttachCardsTo(scn.GetDSCard(weatherSite), scn.GetDSCard(weatherName));
        scn.DSDeployCard(pirate); scn.DSChooseCard(scn.GetDSCard(pirateSite)); resolveWeather(scn, scn.GetDSCard(weatherName));
        return scn;
    }
    @Test public void MercenaryShadowPirateCancelsExistingIceStormAtSameLocation() {
        var scn = weather("ice", "hoth", "hoth"); assertEquals(Zone.LOST_PILE, scn.GetDSCard("ice").getZone());
        assertEquals(Zone.AT_LOCATION, scn.GetDSCard("pirate").getZone());
        assertFalse(Filters.missing.accepts(scn.game(), scn.GetDSCard("pirate")));
    }
    @Test public void MercenaryShadowPirateCancelsExistingIceStormAtRelatedLocation() {
        var scn = weather("ice", "hoth", "bay"); assertEquals(Zone.LOST_PILE, scn.GetDSCard("ice").getZone());
        assertEquals(Zone.AT_LOCATION, scn.GetDSCard("pirate").getZone());
        assertFalse(Filters.missing.accepts(scn.game(), scn.GetDSCard("pirate")));
    }
    @Test public void MercenaryShadowPirateCancelsExistingSandwhirlAtRelatedLocation() {
        var scn = scenario(); var pirate = scn.GetDSCard("pirate");
        scn.StartGame(); scn.MoveLocationToTable(scn.GetDSCard("desert"));
        scn.MoveCardsToDSHand(pirate); scn.SkipToDSTurn(Phase.DEPLOY);
        scn.AttachCardsTo(scn.GetDSCard("desert"), scn.GetDSCard("sand"));
        scn.DSDeployCard(pirate); scn.DSChooseCard(scn.GetDSStartingLocation()); resolveWeather(scn, scn.GetDSCard("sand"));
        assertEquals(Zone.LOST_PILE, scn.GetDSCard("sand").getZone());
    }
    @Test public void MercenaryShadowPirateDoesNotCancelWeatherAtUnrelatedLocation() {
        var scn = weather("sand", "desert", "bay"); assertEquals(Zone.ATTACHED, scn.GetDSCard("sand").getZone());
    }
    @Test public void MercenaryShadowPirateCancelsNewIceStormBeforeItDeploys() {
        var scn = scenario(); var ice = scn.GetDSCard("ice");
        scn.StartGame(); scn.MoveLocationToTable(scn.GetDSCard("hoth")); scn.MoveLocationToTable(scn.GetDSCard("bay"));
        scn.MoveCardsToLocation(scn.GetDSCard("bay"), scn.GetDSCard("pirate"));
        scn.MoveCardsToDSHand(ice); scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSDeployCard(ice); scn.DSChooseCard(scn.GetDSCard("hoth")); resolveWeather(scn, ice);
        assertEquals(Zone.LOST_PILE, ice.getZone());
    }
    @Test public void MercenaryShadowPirateCancelsNewSandwhirlBeforeItDeploys() {
        var scn = scenario(); var sand = scn.GetDSCard("sand");
        scn.StartGame(); scn.MoveLocationToTable(scn.GetDSCard("desert"));
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("pirate"));
        scn.MoveCardsToDSHand(sand); scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSDeployCard(sand); scn.DSChooseCard(scn.GetDSCard("desert")); resolveWeather(scn, sand);
        assertEquals(Zone.LOST_PILE, sand.getZone());
    }

    @Test
    public void MercenaryShadowPirateAlsoCancelsOpponentsWeather() {
        var scn = scenario(); var pirate = scn.GetDSCard("pirate"); var ice = scn.GetLSCard("ice");
        scn.StartGame(); scn.MoveLocationToTable(scn.GetDSCard("hoth")); scn.MoveLocationToTable(scn.GetDSCard("bay"));
        scn.MoveCardsToDSHand(pirate); scn.SkipToDSTurn(Phase.DEPLOY);
        scn.AttachCardsTo(scn.GetDSCard("hoth"), ice);
        scn.DSDeployCard(pirate); scn.DSChooseCard(scn.GetDSCard("bay")); resolveWeather(scn, ice);
        assertEquals(Zone.LOST_PILE, ice.getZone());
        assertFalse(Filters.missing.accepts(scn.game(), pirate));
    }
}
