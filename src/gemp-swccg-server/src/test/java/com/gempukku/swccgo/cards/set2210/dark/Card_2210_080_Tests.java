package com.gempukku.swccgo.cards.set2210.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.GameUtils;

import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.*;
import static org.junit.Assert.*;

/** Original version of 1210_80 (Anything Goes only). */
public class Card_2210_080_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                }},
                new HashMap<>() {{
                    put("version", "2210_80");
                    put("current", "1210_80");
                }},
                10, 10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void VersionHasItsTextAndVersionInfo() {
        var scn = GetScenario();
        var card = scn.GetDSCard("version").getBlueprint();
        var current = scn.GetDSCard("current").getBlueprint();

        assertEquals(current.getTitle(), card.getTitle());
        assertEquals(current.getUniqueness(), card.getUniqueness());
        assertEquals(current.getSide(), card.getSide());
        assertEquals(current.hasVirtualSuffix(), card.hasVirtualSuffix());
        assertEquals(current.getCardTypes(), card.getCardTypes());
        for (Icon icon : Icon.values())
            assertEquals(icon.name(), current.getIconCount(icon), card.getIconCount(icon));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_1, card.getExpansionSet());
        assertTrue(card.isLegacy());
        assertEquals("Deploys only on Tatooine. Ability = ¼. May add 1 'rider' (passenger). May be targeted by Trample instead of a piloted AT-AT. During battle, may 'roar' to make a character here defense value -1.",
                card.getGameText());
        assertEquals(current.getGameText(), card.getGameText()); //stat-only version
        var version = card.getLegacyVersion();
        assertEquals("1210_80", version.currentPrintingId());
        assertEquals("", version.label());
        assertEquals(LocalDate.of(2009, 12, 7), version.printedOn());
        assertEquals(LocalDate.of(2011, 7, 25), version.supersededOn());
        assertEquals(GameUtils.getFullName(current), GameUtils.getFullName(card));
    }

    @Test
    public void OriginalHasManeuverFourInsteadOfArmorFour() {
        var scn = GetScenario();
        var card = scn.GetDSCard("version").getBlueprint();
        var current = scn.GetDSCard("current").getBlueprint();

        assertEquals(4, card.getManeuver(), scn.epsilon);
        assertNull(card.getArmor());
        assertEquals(4, current.getArmor(), scn.epsilon);
        assertNull(current.getManeuver());
        assertEquals(current.getDestiny(), card.getDestiny());
        assertEquals(current.getDeployCost(), card.getDeployCost());
        assertEquals(current.getPower(), card.getPower());
        assertEquals(current.getLandspeed(), card.getLandspeed());
        assertEquals(current.getForfeit(), card.getForfeit());
    }

    @Test
    public void OriginalInPlayHasManeuverAndNoArmor() {
        var scn = GetScenario();
        var dewback = scn.GetDSCard("version");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, dewback);
        var q = scn.game().getModifiersQuerying();
        assertEquals(4, q.getManeuver(scn.gameState(), dewback), scn.epsilon);
        assertFalse(q.hasArmor(scn.gameState(), dewback));
    }
}
