package com.gempukku.swccgo.cards.set1250.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import static org.junit.Assert.*;

public class Card_1250_037_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("chewie", "1250_37"); put("planners", "1_60"); put("bay", "8_76"); put("wookiee", "7_50"); put("drawWookiee", "7_50"); put("leia", "1_17"); put("rifle", "1_153");
            put("ship", "1_140"); put("vehicle", "102_4"); put("kashyyyk", "1116_15"); put("forest", "1116_16");
            put("grrr", "1116_14"); put("memorial", "7_3");
        }}, new HashMap<>() {{ put("vader", "1_168"); put("motti", "1_164"); put("yularen", "1_166"); put("alter", "12_145"); put("snoova", "10_48"); put("trooper", "1_194"); put("rifle", "1_312"); }},
                25, 25, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }
    @Test
    public void ChewbaccaMetadataMatchesFinal36ERebelImage() {
        var scn = scenario(); var bp = scn.GetLSCard("chewie").getBlueprint();
        assertEquals("Chewbacca Of Kashyyyk", bp.getTitle()); assertTrue(bp.hasVirtualSuffix()); assertEquals(Uniqueness.UNIQUE, bp.getUniqueness());
        assertEquals(1, bp.getDestiny(), scn.epsilon); assertEquals(6, bp.getDeployCost(), scn.epsilon);
        assertEquals(6, bp.getPower(), scn.epsilon); assertEquals(2, bp.getAbility(), scn.epsilon); assertEquals(4, bp.getArmor(), scn.epsilon); assertEquals(7, bp.getForfeit(), scn.epsilon);
        assertEquals(Species.WOOKIEE, bp.getSpecies()); assertEquals(ExpansionSet.VIRTUAL_BLOCK_5, bp.getExpansionSet());
        scn.BlueprintCardTypeCheck(bp, new ArrayList<>() {{ add(CardType.REBEL); }});
        scn.BlueprintIconCheck(bp, new ArrayList<>() {{ add(Icon.REBEL); add(Icon.ENDOR); add(Icon.PILOT); add(Icon.WARRIOR); add(Icon.EPISODE_I); add(Icon.LEGACY_BLOCK_5); }});
        scn.BlueprintKeywordCheck(bp, new ArrayList<>() {{ add(Keyword.SCOUT); }});
        scn.BlueprintPersonaCheck(bp, new ArrayList<>() {{ add(Persona.CHEWIE); }});
    }
    @Test
    public void ChewbaccaActuallyDeploysForFourToKashyyykSite() { checkDeploy(true); }
    @Test
    public void ChewbaccaActuallyDeploysForSixAwayFromKashyyyk() { checkDeploy(false); }
    private void checkDeploy(boolean kashyyyk) {
        var scn = scenario(); scn.StartGame(); var site = kashyyyk ? scn.GetLSCard("forest") : scn.GetLSStartingLocation();
        if (kashyyyk) scn.MoveLocationToTable(site);
        scn.MoveCardsToHand(scn.GetLSCard("chewie")); scn.LSActivateForceCheat(10); scn.SkipToLSTurn(Phase.DEPLOY);
        int force = scn.GetLSForcePileCount(); scn.LSDeployCard(scn.GetLSCard("chewie")); scn.LSChooseCard(site); scn.PassAllResponses();
        assertEquals(force - (kashyyyk ? 4 : 6), scn.GetLSForcePileCount()); assertEquals(site, scn.GetLSCard("chewie").getAtLocation());
    }
    @Test
    public void ChewbaccaPilotsStarshipForPlusTwoPower() { checkPilot(false); }
    @Test
    public void ChewbaccaPilotsVehicleForPlusTwoPower() { checkPilot(true); }
    private void checkPilot(boolean vehicle) {
        var scn = scenario(); scn.StartGame(); var transport = scn.GetLSCard(vehicle ? "vehicle" : "ship");
        var location = vehicle ? scn.GetLSStartingLocation() : scn.GetLSCard("kashyyyk");
        if (!vehicle) scn.MoveLocationToTable(location); scn.MoveCardsToLocation(location, transport);
        var chewie = scn.GetLSCard("chewie"); scn.MoveCardsToHand(chewie); scn.LSActivateForceCheat(10); scn.SkipToLSTurn(Phase.DEPLOY);
        int force = scn.GetLSForcePileCount(); scn.LSDeployCard(chewie); scn.LSChooseCard(transport); scn.LSChooseOption("Pilot"); scn.PassAllResponses();
        assertTrue(scn.IsAboardAsPilot(transport, chewie)); assertEquals(vehicle ? 4 : 7, scn.GetPower(transport));
        assertEquals(force - (vehicle ? 6 : 4), scn.GetLSForcePileCount());
        scn.MoveOutOfPlay(chewie); assertEquals(vehicle ? 0 : 5, scn.GetPower(transport));
    }
    @Test
    public void ChewbaccaImprovesActualWookieeBattleDestiny() { checkBattleDestiny(true, true, 3); }
    @Test
    public void ChewbaccaDoesNotImproveNonWookieeBattleDestiny() { checkBattleDestiny(true, false, 2); }
    @Test
    public void ChewbaccaAbsentDoesNotImproveWookieeBattleDestiny() { checkBattleDestiny(false, true, 2); }
    private void checkBattleDestiny(boolean active, boolean wookiee, int expected) {
        var scn = scenario(); scn.StartGame();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("leia"), scn.GetDSCard("vader"));
        if (active) scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetLSCard("chewie"));
        // Leia needs another character to reach ability4; Chewie may be elsewhere.
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("wookiee"));
        scn.LSStartBattleAndSkipToWeaponsSegment(scn.GetLSStartingLocation());
        if (wookiee) scn.MoveCardsToTopOfLSReserveDeck(scn.GetLSCard("drawWookiee"));
        else scn.PrepareLSDestiny(2);
        scn.SkipToPowerSegment(); assertTrue(scn.LSDecisionAvailable("battle destiny?")); scn.LSChooseYes(); scn.PassDestinyDrawResponses();
        assertEquals(expected, scn.GetLSTotalDestiny());
    }
    @Test
    public void ChewbaccaWeaponDestinyBonusCrossesActualRifleHitBoundary() { checkWeapon(true, true); }
    @Test
    public void ChewbaccaNoWeaponDestinyBonusForNonWookieeCard() { checkWeapon(true, false); }
    @Test
    public void ChewbaccaAbsentLeavesWookieeRifleDestinyAtBoundaryMiss() { checkWeapon(false, true); }
    private void checkWeapon(boolean active, boolean nativeDraw) {
        var scn = scenario(); scn.StartGame(); var rifle = scn.GetLSCard("rifle"); var target = scn.GetDSCard("motti");
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("leia"), target);
        scn.AttachCardsTo(scn.GetLSCard("leia"), rifle);
        if (active) scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetLSCard("chewie"));
        scn.LSActivateForceCheat(4); scn.LSStartBattleAndSkipToWeaponsSegment(scn.GetLSStartingLocation());
        if (nativeDraw) scn.MoveCardsToTopOfLSReserveDeck(scn.GetLSCard("wookiee")); else scn.PrepareLSDestiny(2);
        assertTrue(scn.LSCardActionAvailable(rifle, "Fire")); scn.LSUseCardAction(rifle, "Fire"); scn.LSChooseCard(target);
        scn.PassWeaponFireWithDestinyDraw(); scn.PassAllResponses(); assertEquals(active && nativeDraw, target.isHit());
    }
    @Test
    public void ChewbaccaStackedOnGrrrBoostsBothOwnersWookieesAndExpiresOnRemoval() { checkStack(true); }
    @Test
    public void ChewbaccaStackedOnDifferentEffectDoesNotBoostWookiees() { checkStack(false); }
    private void checkStack(boolean grrr) {
        var scn = scenario(); scn.StartGame(); var host = scn.GetLSCard(grrr ? "grrr" : "memorial"); scn.MoveCardsToLSSideOfTable(host);
        var nativeCard = scn.GetLSCard("wookiee"); var snoova = scn.GetDSCard("snoova"); var leia = scn.GetLSCard("leia");
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), nativeCard, snoova, leia);
        int power = scn.GetPower(nativeCard), forfeit = scn.GetForfeit(nativeCard), dsPower = scn.GetPower(snoova), dsForfeit = scn.GetForfeit(snoova);
        scn.StackCardsOn(host, scn.GetLSCard("chewie"));
        assertEquals(power + (grrr ? 1 : 0), scn.GetPower(nativeCard)); assertEquals(forfeit + (grrr ? 1 : 0), scn.GetForfeit(nativeCard));
        assertEquals(dsPower + (grrr ? 1 : 0), scn.GetPower(snoova)); assertEquals(dsForfeit + (grrr ? 1 : 0), scn.GetForfeit(snoova)); assertEquals(3, scn.GetPower(leia));
        scn.MoveOutOfPlay(scn.GetLSCard("chewie")); assertEquals(power, scn.GetPower(nativeCard)); assertEquals(forfeit, scn.GetForfeit(nativeCard));
    }
    @Test
    public void ChewbaccaActualWeaponHitDoublesPowerInBattle() { checkHit(4, true); }
    @Test
    public void ChewbaccaWeaponMissAtArmorBoundaryDoesNotDoublePower() { checkHit(3, false); }
    private void checkHit(int destiny, boolean hit) {
        var scn = scenario(); scn.StartGame(); var chewie = scn.GetLSCard("chewie"); var rifle = scn.GetDSCard("rifle");
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), chewie, scn.GetDSCard("trooper")); scn.AttachCardsTo(scn.GetDSCard("trooper"), rifle);
        assertEquals(6, scn.GetPower(chewie)); scn.DSActivateForceCheat(4); scn.DSStartBattleAndSkipToWeaponsSegment(scn.GetLSStartingLocation());
        scn.PrepareDSDestiny(destiny); scn.DSUseCardAction(rifle, "Fire"); scn.DSChooseCard(chewie); scn.PassWeaponFireWithDestinyDraw(); scn.PassAllResponses();
        assertEquals(hit, chewie.isHit()); assertEquals(hit ? 12 : 6, scn.GetPower(chewie));
        scn.SkipToEndOfPowerSegment(false); assertEquals(hit ? 12 : 6, scn.GetLSTotalPower());
    }

    @Test
    public void ChewbaccaPassengerDoesNotAddPilotPower() {
        var scn = scenario(); scn.StartGame(); var system = scn.GetLSCard("kashyyyk"); var ship = scn.GetLSCard("ship");
        scn.MoveLocationToTable(system); scn.MoveCardsToLocation(system, ship); scn.BoardAsPassenger(ship, scn.GetLSCard("chewie"));
        assertEquals(5, scn.GetPower(ship));
    }
    @Test
    public void ChewbaccaAlsoImprovesOpponentsActualWookieeBattleDestiny() {
        var scn = scenario(); scn.StartGame(); scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetLSCard("chewie"));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("leia"), scn.GetDSCard("motti"), scn.GetDSCard("yularen"));
        scn.DSStartBattleAndSkipToWeaponsSegment(scn.GetLSStartingLocation()); scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSCard("snoova"));
        scn.SkipToPowerSegment(); assertTrue(scn.DSDecisionAvailable("battle destiny?")); scn.DSChooseYes(); scn.PassDestinyDrawResponses();
        assertEquals(2, scn.GetDSTotalDestiny());
    }
    @Test
    public void ChewbaccaDoesNotImproveWookieeDrawnForGenericAlterDestiny() {
        var scn = scenario(); scn.StartGame(); var bay = scn.GetLSCard("bay"); scn.MoveLocationToTable(bay);
        var planners = scn.GetLSCard("planners"); scn.AttachCardsTo(bay, planners);
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("chewie"));
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("yularen")); scn.MoveCardsToHand(scn.GetDSCard("alter"));
        scn.SkipToDSTurn(Phase.CONTROL); scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSCard("snoova"));
        assertEquals(2, scn.GetAbility(scn.GetDSCard("yularen"))); scn.DSPlayCard(scn.GetDSCard("alter"), "Cancel Effect");
        if (scn.DSDecisionAvailable("Choose Effect")) scn.DSChooseCard(planners);
        if (scn.DSDecisionAvailable("Choose a character")) scn.DSChooseCard(scn.GetDSCard("yularen"));
        scn.PassAllResponses(); assertTrue(scn.GetLSLostPile().contains(planners));
    }
}
