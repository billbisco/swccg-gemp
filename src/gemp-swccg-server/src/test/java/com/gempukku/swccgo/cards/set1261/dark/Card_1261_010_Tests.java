package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class Card_1261_010_Tests {
    private static final String ADD = "Add one battle destiny";

    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(
                new HashMap<>(Map.of("obi", "13_33", "oldObi", "1_21", "mace", "601_163",
                        "saber", "201_21", "luke", "108_3", "remoteSaber", "13_34")),
                new HashMap<>(Map.of("vader", "1261_10", "circle", "1_271", "ship", "1_301",
                        "system", "1_282", "trooper", "1_194")),
                24, 24, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void printedImperialVaderIdentity() {
        var scn = scenario();
        var bp = scn.GetDSCard("vader").getBlueprint();
        assertEquals("Darth Vader, Betrayer Of The Jedi", bp.getTitle());
        assertTrue(bp.getPersonas().contains(Persona.VADER));
        assertEquals(Side.DARK, bp.getSide());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, bp.getExpansionSet());
        assertEquals(Rarity.V, bp.getRarity());
        assertEquals(Uniqueness.UNIQUE, bp.getUniqueness());
        assertFalse(bp.hasVirtualSuffix());
        assertEquals(1, bp.getDestiny(), scn.epsilon);
        assertEquals(6, bp.getDeployCost(), scn.epsilon);
        assertEquals(6, bp.getPower(), scn.epsilon);
        assertEquals(6, bp.getAbility(), scn.epsilon);
        assertEquals(8, bp.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(bp, new ArrayList<>(List.of(CardType.IMPERIAL)));
        scn.BlueprintIconCheck(bp, new ArrayList<>(List.of(Icon.IMPERIAL, Icon.WARRIOR, Icon.PILOT,
                Icon.DEATH_STAR_II, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(bp, new ArrayList<>());
    }

    @Test
    public void addsThreePowerOnlyWhilePilotingAndHasPrintedAttritionBoundary() {
        for (boolean piloting : List.of(false, true)) {
            var scn = scenario();
            var ship = scn.GetDSCard("ship");
            var vader = scn.GetDSCard("vader");
            var system = scn.GetDSCard("system");
            scn.StartGame();
            scn.MoveLocationToTable(system);
            scn.MoveCardsToLocation(system, ship);
            if (piloting)
                scn.BoardAsPilot(ship, vader);
            else
                scn.BoardAsPassenger(ship, vader);
            scn.SkipToDSTurn(Phase.CONTROL);
            assertEquals(ship.getBlueprint().getPower() + (piloting ? 3 : 0), scn.GetPower(ship), scn.epsilon);
            assertEquals(5, immunity(scn, vader), scn.epsilon);
        }
    }

    @Test
    public void cancelsCircleBeforeTheDuelStarts() {
        var scn = scenario();
        var vader = scn.GetDSCard("vader");
        var obi = scn.GetLSCard("oldObi");
        var circle = scn.GetDSCard("circle");
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), vader, obi);
        scn.MoveCardsToDSHand(circle);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.EnsureDSForcePile(5);
        assertTrue(scn.DSCardPlayAvailable(circle, "Initiate duel"));
        scn.DSPlayCard(circle, "Initiate duel");
        if (scn.DSHasCardChoiceAvailable(obi))
            scn.DSChooseCard(obi);
        if (scn.DSHasCardChoiceAvailable(vader))
            scn.DSChooseCard(vader);
        scn.PassAllResponses();
        assertTrue(scn.GetDSLostPile().contains(circle));
        assertTrue(vader.getZone().isInPlay());
        assertTrue(obi.getZone().isInPlay());
        assertNull(scn.gameState().getDuelState());
    }

    @Test
    public void reflectionsObiOrCouncilIsRequiredAndTheirOwnAttritionImmunityIsCanceled() {
        for (String key : List.of("obi", "oldObi", "mace")) {
            var scn = scenario();
            var vader = scn.GetDSCard("vader");
            var jedi = scn.GetLSCard(key);
            var site = scn.GetDSStartingLocation();
            scn.StartGame();
            scn.MoveCardsToLocation(site, vader, jedi);
            if (key.equals("oldObi"))
                assertEquals(5, immunity(scn, jedi), scn.epsilon);
            else
                assertFalse(scn.game().getModifiersQuerying().hasAnyImmunityToAttrition(scn.gameState(), jedi));
            assertEquals(5, immunity(scn, vader), scn.epsilon);
            scn.SkipToDSTurn(Phase.BATTLE);
            scn.EnsureDSForcePile(6);
            scn.DSInitiateBattle(site);
            scn.PassAllResponses();
            assertTrue(scn.AwaitingDSWeaponsSegmentActions());
            if (key.equals("oldObi")) {
                assertFalse(scn.DSCardActionAvailable(vader, ADD));
            } else {
                int before = scn.GetDSBattleDestinyCount();
                assertTrue(scn.DSCardActionAvailable(vader, ADD));
                scn.DSUseCardAction(vader, ADD);
                scn.PassAllResponses();
                assertEquals(before + 1, scn.GetDSBattleDestinyCount());
                if (scn.AwaitingLSWeaponsSegmentActions())
                    scn.LSPass();
                assertTrue(scn.AwaitingDSWeaponsSegmentActions());
                assertFalse(scn.DSCardActionAvailable(vader, ADD));
            }
        }
    }

    @Test
    public void qualifyingPairCannotAddDestinyToABattleElsewhere() {
        var scn = scenario();
        var vader = scn.GetDSCard("vader");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), vader, scn.GetLSCard("mace"));
        scn.MoveCardsToLocation(site, scn.GetDSCard("trooper"), scn.GetLSCard("luke"));
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.EnsureDSForcePile(6);
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();
        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
        assertFalse(scn.DSCardActionAvailable(vader, ADD));
    }

    @Test
    public void localAttachedAndPermanentLightsabersCannotUseRepeatPermission() {
        for (boolean vaderHere : List.of(true, false)) {
            var scn = scenario();
            var vader = scn.GetDSCard("vader");
            var mace = scn.GetLSCard("mace");
            var saber = scn.GetLSCard("saber");
            var luke = scn.GetLSCard("luke");
            var remoteSaber = scn.GetLSCard("remoteSaber");
            var site = scn.GetDSStartingLocation();
            var remote = scn.GetLSStartingLocation();
            scn.StartGame();
            scn.MoveCardsToLocation(site, mace, luke, scn.GetDSCard("trooper"));
            scn.MoveCardsToLocation(vaderHere ? site : remote, vader);
            scn.MoveCardsToLocation(remote, scn.GetLSCard("obi"));
            scn.AttachCardsTo(mace, saber);
            scn.AttachCardsTo(scn.GetLSCard("obi"), remoteSaber);
            scn.SkipToDSTurn(Phase.BATTLE);
            scn.EnsureDSForcePile(6);
            scn.DSInitiateBattle(site);
            scn.PassAllResponses();
            var mods = scn.game().getModifiersQuerying();
            assertEquals(vaderHere, mods.mayFireOnlyOncePerBattle(scn.gameState(), saber));
            assertEquals(vaderHere, mods.mayFireOnlyOncePerBattle(scn.gameState(), luke));
            assertEquals(!vaderHere, mods.mayFireOnlyOncePerBattle(scn.gameState(), remoteSaber));
            assertEquals(!vaderHere, mods.mayFireWeaponTwicePerBattle(scn.gameState(), mace, saber));
        }
    }

    private float immunity(VirtualTableScenario scn, PhysicalCardImpl card) {
        return scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), card);
    }

    @org.junit.Test
    public void CurrentTextIsTheLatestVersion() {
        // Virtual Cards Master List (PC-E1, 2013-04-27) is the latest text (clauses reordered; same play).
        var scn = new com.gempukku.swccgo.framework.VirtualTableScenario(new java.util.HashMap<>(), new java.util.HashMap<>(java.util.Map.of("c", "1261_10")), 10, 10,
                com.gempukku.swccgo.framework.StartingSetup.DefaultLSGroundLocation, com.gempukku.swccgo.framework.StartingSetup.DefaultDSGroundLocation,
                com.gempukku.swccgo.framework.StartingSetup.NoLSStartingInterrupts, com.gempukku.swccgo.framework.StartingSetup.NoDSStartingInterrupts,
                com.gempukku.swccgo.framework.StartingSetup.NoLSShields, com.gempukku.swccgo.framework.StartingSetup.NoDSShields,
                com.gempukku.swccgo.framework.VirtualTableScenario.Open);
        var text = scn.GetDSCard("c").getBlueprint().getGameText();
        org.junit.Assert.assertEquals("[Pilot] 3. The Circle Is Now Complete is canceled. While present with [Reflections III] Obi-Wan or a Jedi Council Member, may add one battle destiny, lightsabers here may not be 'swung' twice, and their immunity to attrition is canceled. Immune to attrition < 5.", text);
        org.junit.Assert.assertFalse(text.contains("and you may add one battle destiny"));
        org.junit.Assert.assertNull(scn.GetDSCard("c").getBlueprint().getLegacyVersion());
    }
}
