package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class Card_1261_054_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>(Map.of("luke", "1_19", "corvette", "1_140")), new HashMap<>(Map.of(
                "station", "1261_54", "devastator", "1_301", "destroyer", "1_302", "trooper", "1_194")),
                18, 18, StartingSetup.DefaultLSSpaceSystem, StartingSetup.DefaultDSSpaceSystem,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void verifiesThreeDotMobileStarshipSiteAndOneDarkIcon() {
        var scn = scenario();
        var bp = scn.GetDSCard("station").getBlueprint();
        assertEquals("Star Destroyer: Command Station", bp.getTitle());
        assertFalse(bp.hasVirtualSuffix());
        assertEquals(Side.DARK, bp.getSide());
        assertEquals(Uniqueness.RESTRICTED_3, bp.getUniqueness());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, bp.getExpansionSet());
        assertEquals(Rarity.V, bp.getRarity());
        assertEquals(0, bp.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.SITE, bp.getCardSubtype());
        scn.BlueprintCardTypeCheck(bp, new ArrayList<>(List.of(CardType.LOCATION)));
        scn.BlueprintIconCheck(bp, new ArrayList<>(List.of(Icon.DARK_FORCE, Icon.DAGOBAH,
                Icon.INTERIOR_SITE, Icon.MOBILE, Icon.STARSHIP_SITE, Icon.SCOMP_LINK, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(bp, new ArrayList<>());
    }

    @Test
    public void lightControlTurnsOffFourOrPlusTwoAndUnrelatedShipIsUnchanged() {
        var scn = scenario();
        scn.StartGame();
        var station = scn.GetDSCard("station");
        var host = scn.GetDSCard("destroyer");
        var unrelated = scn.GetDSCard("devastator");
        setupHost(scn, station, host, unrelated);
        var query = scn.game().getModifiersQuerying();
        assertEquals(4, query.getImmunityToAttritionLessThan(scn.gameState(), host), scn.epsilon);
        assertEquals(0, query.getImmunityToAttritionLessThan(scn.gameState(), unrelated), scn.epsilon);
        var luke = scn.GetLSCard("luke");
        scn.MoveCardsToLocation(station, luke);
        scn.game().getModifiersEnvironment().addUntilEndOfTurnModifier(new ImmuneToAttritionLessThanModifier(luke, host, 5));
        assertEquals(5, query.getImmunityToAttritionLessThan(scn.gameState(), host), scn.epsilon);
        scn.MoveCardsToLocation(station, scn.GetDSCard("trooper"));
        assertEquals(7, query.getImmunityToAttritionLessThan(scn.gameState(), host), scn.epsilon);
    }

    @Test
    public void darkControlProtectsOnlyTheRelatedDevastatorAndGrantsFullImmunity() {
        var scn = scenario();
        scn.StartGame();
        var station = scn.GetDSCard("station");
        var host = scn.GetDSCard("devastator");
        var unrelated = scn.GetDSCard("destroyer");
        setupHost(scn, station, host, unrelated);
        var luke = scn.GetLSCard("luke");
        scn.MoveCardsToLocation(station, luke);
        scn.game().getModifiersEnvironment().addUntilEndOfTurnModifier(
                new PowerModifier(luke, Filters.or(host, unrelated), -1));
        assertEquals(host.getBlueprint().getPower() - 1, scn.GetPower(host), scn.epsilon);
        scn.MoveCardsToTopOfLSLostPile(luke);
        scn.MoveCardsToLocation(station, scn.GetDSCard("trooper"));
        scn.game().getModifiersEnvironment().addUntilEndOfTurnModifier(
                new PowerModifier(scn.GetDSCard("trooper"), Filters.or(host, unrelated), -1));
        assertEquals(host.getBlueprint().getPower(), scn.GetPower(host), scn.epsilon);
        // Both players added lasting penalties; the unrelated ship keeps both.
        assertEquals(unrelated.getBlueprint().getPower() - 2, scn.GetPower(unrelated), scn.epsilon);
        assertEquals(Float.MAX_VALUE, scn.game().getModifiersQuerying()
                .getImmunityToAttritionLessThan(scn.gameState(), host), 0);
    }

    @Test(timeout = 10000)
    public void theJirSearchRequiresDevastatorButNotAsTheStationsHost() {
        for (boolean present : List.of(false, true)) {
            var scn = scenario();
            scn.StartGame();
            var station = scn.GetDSCard("station");
            var host = scn.GetDSCard("destroyer");
            setupHost(scn, station, host, present ? scn.GetDSCard("devastator") : null);
            scn.SkipToDSTurn(Phase.DEPLOY);
            assertEquals(present, scn.DSCardActionAvailable(station, "Deploy Jir here from Reserve Deck"));
        }
    }

    private void setupHost(VirtualTableScenario scn, PhysicalCardImpl station, PhysicalCardImpl host,
            PhysicalCardImpl unrelated) {
        var system = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(system, host);
        if (unrelated != null)
            scn.MoveCardsToLocation(system, unrelated);
        var placement = scn.gameState().getLocationPlacement(scn.game(), station, null, null).stream()
                .filter(p -> p.getParentStarshipOrVehicleCard() == host).findFirst().orElseThrow();
        scn.MoveLocationToTable(station, placement);
        assertSame(host, station.getRelatedStarshipOrVehicle());
    }

    private boolean hostMayReactToLightBattle(boolean darkControlsStation) {
        var scn = scenario();
        scn.StartGame();
        var station = scn.GetDSCard("station");
        var host = scn.GetDSCard("devastator");
        setupHost(scn, station, host, null);
        if (darkControlsStation)
            scn.MoveCardsToLocation(station, scn.GetDSCard("trooper"));
        var lsSystem = scn.GetLSStartingLocation();
        var corvette = scn.GetLSCard("corvette");
        scn.MoveCardsToLocation(lsSystem, corvette, scn.GetDSCard("destroyer"));
        scn.BoardAsPilot(corvette, scn.GetLSCard("luke"));
        scn.LSStartBattleAndSkipToWeaponsSegment(lsSystem);
        return scn.game().getModifiersQuerying().getMoveAsReactOption(scn.gameState(), host, null, false,
                com.gempukku.swccgo.filters.Filters.any) != null;
    }

    @Test(timeout = 10000)
    public void whileDarkControlsRelatedStarshipMayMoveAsReact() {
        assertTrue(hostMayReactToLightBattle(true));
    }

    @Test(timeout = 10000)
    public void withoutControlRelatedStarshipHasNoReact() {
        assertFalse(hostMayReactToLightBattle(false));
    }
}
