package com.gempukku.swccgo.cards.set1263.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class Card_1263_004_Tests {
    private VirtualTableScenario scenario() {
        var scn = new VirtualTableScenario(
                new HashMap<>(Map.of("ben", "1_21", "saber", "1_157", "clash", "5_38")),
                new HashMap<>(Map.of("grotto", "1263_4", "trooper", "1_194", "ship", "7_307", "system", "1_289")),
                30, 30, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
        scn.StartGame();
        scn.MoveLocationToTable(scn.GetDSCard("system"));
        return scn;
    }

    @Test public void GrottoWerribeePrintedMetadataMatches() {
        var scn = scenario();
        var b = scn.GetDSCard("grotto").getBlueprint();
        assertEquals("Grotto Werribee", b.getTitle());
        assertEquals(Side.DARK, b.getSide());
        assertEquals(Uniqueness.UNIQUE, b.getUniqueness());
        assertEquals(1, b.getDestiny(), 0.01);
        assertEquals(2, b.getPower(), 0.01);
        assertEquals(4, b.getAbility(), 0.01);
        assertEquals(3, b.getDeployCost(), 0.01);
        assertEquals(4, b.getForfeit(), 0.01);
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_3, b.getExpansionSet());
        assertEquals(Rarity.V, b.getRarity());
        assertTrue(b.hasVirtualSuffix());
        assertTrue(b.isLegacy());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(List.of(CardType.ALIEN)));
        scn.BlueprintIconCheck(b, new ArrayList<>(List.of(Icon.ALIEN, Icon.EPISODE_I, Icon.PILOT, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>(List.of(Keyword.INFORMATION_BROKER)));
    }

    @Test public void GrottoWerribeeAddsTwoToShipPowerOnlyWhenPiloting() {
        for (boolean piloting : List.of(false, true)) {
            var scn = scenario();
            var ship = scn.GetDSCard("ship");
            var grotto = scn.GetDSCard("grotto");
            scn.MoveCardsToLocation(scn.GetDSCard("system"), ship);
            int power = scn.GetPower(ship);
            if (piloting) scn.BoardAsPilot(ship, grotto);
            else scn.BoardAsPassenger(ship, grotto);
            assertEquals(power + (piloting ? 2 : 0), scn.GetPower(ship));
        }
    }

    @Test public void GrottoWerribeeGrantsImmunityToActualClashOnlyAtHisLocation() {
        for (boolean grottoHere : List.of(false, true)) {
            var scn = scenario();
            var site = scn.GetDSStartingLocation();
            var trooper = scn.GetDSCard("trooper");
            var ben = scn.GetLSCard("ben");
            var clash = scn.GetLSCard("clash");
            scn.MoveCardsToLocation(site, trooper, ben);
            scn.MoveCardsToLocation(grottoHere ? site : scn.GetLSStartingLocation(), scn.GetDSCard("grotto"));
            scn.AttachCardsTo(ben, scn.GetLSCard("saber"));
            scn.MoveCardsToLSHand(clash);
            scn.SkipToLSTurn(Phase.ACTIVATE);
            scn.EnsureLSForcePile(3);
            scn.SkipToPhase(Phase.CONTROL);
            assertEquals(!grottoHere, scn.LSCardPlayAvailable(clash, "Prevent character from moving or battling"));
            assertEquals(grottoHere ? 3 : 0, scn.game().getModifiersQuerying()
                    .getImmunityToAttritionLessThan(scn.gameState(), trooper), scn.epsilon);
        }
    }
}
