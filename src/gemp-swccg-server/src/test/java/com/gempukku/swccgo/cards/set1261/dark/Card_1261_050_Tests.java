package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import static org.junit.Assert.*;

public class Card_1261_050_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{ put("obiwan", "1_21"); }},
                new HashMap<>() {{ put("sentry", "1261_50"); put("vader", "1_168");
                    put("uniqueImperial", "1_306"); put("nonuniqueImperial", "1_304");
                    put("independent", "5_177"); put("trooper", "1_194"); }}, 20, 20,
                StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void TieSentryShipsPrintedMetadataMatchesLegacySheet() {
        var scn = scenario(); var b = scn.GetDSCard("sentry").getBlueprint();
        assertEquals("TIE Sentry Ships", b.getTitle()); assertTrue(b.hasVirtualSuffix());
        assertEquals(Side.DARK, b.getSide()); assertEquals(Uniqueness.UNIQUE, b.getUniqueness());
        assertEquals(5, b.getDestiny(), scn.epsilon); assertEquals(CardSubtype.USED_OR_LOST, b.getCardSubtype());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, b.getExpansionSet()); assertEquals(Rarity.V, b.getRarity());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>() {{ add(CardType.INTERRUPT); }});
        scn.BlueprintKeywordCheck(b, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(b, new ArrayList<>() {{ add(Icon.INTERRUPT); add(Icon.CLOUD_CITY); add(Icon.LEGACY_BLOCK_6); }});
    }

    private VirtualTableScenario draw(String destinyCard, boolean responseExpected) {
        var scn = scenario(); scn.StartGame(); var site = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(site, scn.GetDSCard("vader"), scn.GetLSCard("obiwan"));
        scn.MoveCardsToDSHand(scn.GetDSCard("sentry")); scn.DSStartBattleAndSkipToWeaponsSegment(site); scn.SkipToPowerSegment();
        scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSCard(destinyCard)); scn.DSChooseYes();
        scn.PassResponses("COST_TO_DRAW_DESTINY_CARD"); scn.PassResponses("ABOUT_TO_DRAW_DESTINY_CARD");
        assertTrue(scn.LSDecisionAvailable("DESTINY_DRAWN")); scn.LSPass();
        assertEquals(responseExpected, scn.DSDecisionAvailable("Just drew")); return scn;
    }

    @Test
    public void TieSentryShipsUsedTakesUniqueImperialIntoHandAndRedrawsForFree() {
        var scn = draw("uniqueImperial", true); int force = scn.GetDSForcePileCount();
        scn.PrepareDSDestiny(5); scn.DSPlayCard(scn.GetDSCard("sentry"), "Take destiny card into hand"); scn.PassAllResponses();
        com.gempukku.swccgo.framework.Assertions.assertInZone(Zone.HAND, scn.GetDSCard("uniqueImperial"));
        com.gempukku.swccgo.framework.Assertions.assertInZone(Zone.USED_PILE, scn.GetDSCard("sentry"));
        assertEquals(6, scn.GetDSTotalDestiny()); assertEquals(force, scn.GetDSForcePileCount());
    }

    @Test
    public void TieSentryShipsUsedDoesNotAcceptNonuniqueImperialStarship() { draw("nonuniqueImperial", false); }

    @Test
    public void TieSentryShipsUsedDoesNotAcceptUniqueIndependentStarship() { draw("independent", false); }

    @Test
    public void TieSentryShipsUsedDoesNotAcceptCharacterDestiny() { draw("trooper", false); }

    @Test
    public void TieSentryShipsLostRetrievesOnlyUniqueImperialIntoHandForThreeForce() {
        var scn = scenario(); scn.StartGame(); var sentry = scn.GetDSCard("sentry"); var target = scn.GetDSCard("uniqueImperial");
        scn.MoveCardsToDSHand(sentry); scn.MoveCardsToTopOfDSLostPile(target, scn.GetDSCard("nonuniqueImperial"), scn.GetDSCard("independent"));
        scn.DSActivateMaxForceAndPass(); scn.SkipToPhase(Phase.CONTROL); scn.EnsureDSForcePile(5); int force = scn.GetDSForcePileCount();
        scn.DSPlayCard(sentry, "Retrieve a unique Imperial starship into hand"); scn.PassAllResponses();
        assertTrue(isSelectable(scn, target)); assertFalse(isSelectable(scn, scn.GetDSCard("nonuniqueImperial")));
        assertFalse(isSelectable(scn, scn.GetDSCard("independent"))); scn.DSChooseCard(target); scn.PassAllResponses();
        com.gempukku.swccgo.framework.Assertions.assertInZone(Zone.HAND, target);
        com.gempukku.swccgo.framework.Assertions.assertInZone(Zone.LOST_PILE, sentry);
        assertEquals(force - 3, scn.GetDSForcePileCount());
    }

    @Test
    public void TieSentryShipsLostCannotBePlayedWithOnlyTwoForce() {
        var scn = scenario(); scn.StartGame(); scn.MoveCardsToDSHand(scn.GetDSCard("sentry"));
        scn.MoveCardsToTopOfDSLostPile(scn.GetDSCard("uniqueImperial"));
        scn.DSActivateMaxForceAndPass(); scn.SkipToPhase(Phase.CONTROL); scn.DrainDSForcePileTo(2);
        scn.SkipToPhase(Phase.DEPLOY); assertEquals(2, scn.GetDSForcePileCount());
        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertFalse(scn.DSCardPlayAvailable(scn.GetDSCard("sentry"), "Retrieve a unique Imperial starship into hand"));
    }

    private boolean isSelectable(VirtualTableScenario scn, PhysicalCardImpl card) {
        var blueprints = scn.GetADParam(scn.DS, "blueprintId"); var ids = scn.GetADParam(scn.DS, "cardId");
        var allowed = scn.GetADParam(scn.DS, "selectable"); int count = blueprints != null ? blueprints.length : ids.length;
        for (int i = 0; i < count; i++) {
            boolean matches = blueprints != null ? blueprints[i].equals(card.getBlueprintId(true)) : ids[i].equals(String.valueOf(card.getCardId()));
            if (matches) return allowed == null || Boolean.parseBoolean(allowed[i]);
        }
        return false;
    }

    @org.junit.Test
    public void CurrentTextIsTheLatestVersion() {
        // PC card list (PC-E1) is the latest text ("redraw the destiny"; same play).
        var scn = new com.gempukku.swccgo.framework.VirtualTableScenario(new java.util.HashMap<>(), new java.util.HashMap<>(java.util.Map.of("c", "1261_50")), 10, 10,
                com.gempukku.swccgo.framework.StartingSetup.DefaultLSGroundLocation, com.gempukku.swccgo.framework.StartingSetup.DefaultDSGroundLocation,
                com.gempukku.swccgo.framework.StartingSetup.NoLSStartingInterrupts, com.gempukku.swccgo.framework.StartingSetup.NoDSStartingInterrupts,
                com.gempukku.swccgo.framework.StartingSetup.NoLSShields, com.gempukku.swccgo.framework.StartingSetup.NoDSShields,
                com.gempukku.swccgo.framework.VirtualTableScenario.Open);
        var text = scn.GetDSCard("c").getBlueprint().getGameText();
        org.junit.Assert.assertEquals("USED: If you just drew a unique (•) Imperial starship for destiny, take that starship into hand to cancel and redraw the destiny. LOST: Use 3 Force to retrieve a unique (•) Imperial starship into hand.", text);
        org.junit.Assert.assertFalse(text.contains("redraw that destiny"));
        org.junit.Assert.assertNull(scn.GetDSCard("c").getBlueprint().getLegacyVersion());
    }
}
