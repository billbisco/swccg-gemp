package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class Card_1261_057_Tests {
    @Test
    public void finalTIEPrintingUsesTheNamedUniquePermanentPilotAndNativeAttritionImmunity() {
        var scn = new VirtualTableScenario(new HashMap<>(), new HashMap<>(Map.of("black", "1261_57")),
                14, 14, StartingSetup.DefaultLSSpaceSystem, StartingSetup.DefaultDSSpaceSystem,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
        var ship = scn.GetDSCard("black");
        var bp = ship.getBlueprint();
        assertEquals("DS-61-11 In Black 11", bp.getTitle());
        assertFalse(bp.hasVirtualSuffix());
        assertEquals(Side.DARK, bp.getSide());
        assertEquals(2, bp.getDestiny(), scn.epsilon);
        assertEquals(3, bp.getDeployCost(), scn.epsilon);
        assertEquals(4, bp.getPower(), scn.epsilon);
        assertEquals(3, bp.getManeuver(), scn.epsilon);
        assertNull(bp.getHyperspeed());
        assertEquals(4, bp.getForfeit(), scn.epsilon);
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, bp.getExpansionSet());
        assertEquals(Rarity.V, bp.getRarity());
        assertEquals(Uniqueness.UNIQUE, bp.getUniqueness());
        assertEquals(CardSubtype.STARFIGHTER, bp.getCardSubtype());
        scn.BlueprintCardTypeCheck(bp, new ArrayList<>(List.of(CardType.STARSHIP)));
        scn.BlueprintIconCheck(bp, new ArrayList<>(List.of(Icon.STARSHIP, Icon.PILOT, Icon.DEATH_STAR_II,
                Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(bp, new ArrayList<>(List.of(Keyword.NO_HYPERDRIVE, Keyword.BLACK_SQUADRON)));
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), ship);
        var pilots = bp.getPermanentsAboard(ship);
        assertEquals(1, pilots.size());
        var pilot = pilots.getFirst();
        assertTrue(pilot.isPilot());
        assertEquals("DS-61-11", pilot.getTitle(scn.game()));
        assertEquals(Uniqueness.UNIQUE, pilot.getUniqueness());
        assertEquals(2, pilot.getAbility(), scn.epsilon);
        assertEquals(5, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), ship),
                scn.epsilon);
    }
}
