package com.gempukku.swccgo.cards.set1263.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.logic.decisions.CardActionSelectionDecision;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.*;

public class Card_1263_009_Tests {
    private static final String TAKE = "Take any card into hand from Used Pile";

    private VirtualTableScenario scenario() {
        var scn = new VirtualTableScenario(new HashMap<>(Map.of("strike", "9_41", "alter", "1_71", "luke", "1_19")),
                new HashMap<>(Map.ofEntries(Map.entry("guild", "1263_9"), Map.entry("maul", "11_54"),
                        Map.entry("aurra", "11_51"), Map.entry("galen", "601_81"), Map.entry("guri", "10_41"),
                        Map.entry("mara", "110_10"), Map.entry("otherMara", "601_90"), Map.entry("trooper", "1_194"),
                        Map.entry("coruscant", "12_166"), Map.entry("endo", "8_157"),
                        Map.entry("capital", "1_301"), Map.entry("target", "1_324"),
                        Map.entry("seed1", "1_168"), Map.entry("seed2", "13_59"), Map.entry("seed3", "1_306"))),
                30, 30, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
        scn.StartGame();
        scn.MoveLocationToTable(scn.GetDSCard("coruscant"));
        scn.MoveLocationToTable(scn.GetDSCard("endo"));
        return scn;
    }

    @Test public void GuildOfAssassinsPrintedMetadataMatches() {
        var scn = scenario();
        var b = scn.GetDSCard("guild").getBlueprint();
        assertEquals("Guild Of Assassins", b.getTitle());
        assertEquals(Side.DARK, b.getSide());
        assertEquals(Uniqueness.UNIQUE, b.getUniqueness());
        assertEquals(4, b.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.NORMAL, b.getCardSubtype());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_3, b.getExpansionSet());
        assertEquals(Rarity.V, b.getRarity());
        assertFalse(b.hasVirtualSuffix());
        assertTrue(b.isLegacy());
        assertEquals("Greedo cheskopokuta klees ruya Solo. Hoko yanee boopa gush Cantina. Cheeco wa Solo's anye nyuma Greedo vakee. Jabba kul steeka et en anpaw.", b.getLore());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(List.of(CardType.EFFECT)));
        scn.BlueprintIconCheck(b, new ArrayList<>(List.of(Icon.EFFECT, Icon.A_NEW_HOPE, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>());
    }

    @Test public void GuildOfAssassinsActuallyDeploysAndMakesMaulLostImmediatelyAndOnLaterDeployment() {
        var scn = scenario();
        var guild = scn.GetDSCard("guild");
        var maul = scn.GetDSCard("maul");
        var site = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(site, maul, scn.GetDSCard("trooper"));
        scn.MoveCardsToDSHand(guild);
        scn.SkipToPhase(Phase.DEPLOY);
        scn.DSDeployCard(guild);
        settleDeploy(scn);
        assertInZone(Zone.SIDE_OF_TABLE, guild);
        assertInZone(Zone.LOST_PILE, maul);
        assertInZone(Zone.AT_LOCATION, scn.GetDSCard("trooper"));
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToDSHand(maul);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.EnsureDSForcePile(8);
        scn.SkipToPhase(Phase.DEPLOY);
        scn.DSDeployCard(maul);
        if (scn.DSHasCardChoiceAvailable(site)) scn.DSChooseCard(site);
        settleDeploy(scn);
        assertInZone(Zone.LOST_PILE, maul);
    }

    @Test public void GuildOfAssassinsTakesAnActualUsedCardForEachNamedPersonaAtTheTwoPrintedPlanets() {
        for (String assassin : List.of("aurra", "galen", "guri", "mara")) {
            var scn = scenario();
            var site = assassin.equals("galen") || assassin.equals("guri")
                    ? scn.GetDSCard("coruscant") : scn.GetDSStartingLocation();
            prepareDeploy(scn, assassin);
            assertTrue(deployAndSearch(scn, assassin, site, true, 0));
            assertInZone(Zone.HAND, scn.GetDSCard("target"));
        }
    }

    @Test public void GuildOfAssassinsDeclineDoesNotConsumeThePersonaButAnotherPrintingSharesItsLimit() {
        var scn = scenario();
        var site = scn.GetDSStartingLocation();
        prepareDeploy(scn, "mara");
        assertTrue(deployAndSearch(scn, "mara", site, false, 0));
        assertInZone(Zone.USED_PILE, scn.GetDSCard("target"));
        scn.MoveCardsToDSHand(scn.GetDSCard("mara"));
        prepareNextDeploy(scn, "otherMara");
        assertTrue(deployAndSearch(scn, "otherMara", site, true, 0));
        scn.MoveCardsToDSHand(scn.GetDSCard("otherMara"));
        prepareNextDeploy(scn, "mara");
        assertFalse(deployAndSearch(scn, "mara", site, true, 0));
        assertInZone(Zone.USED_PILE, scn.GetDSCard("target"));
    }

    @Test public void GuildOfAssassinsDoesNotOfferSearchAtOtherSitesOrForOtherCharacters() {
        var offPlanet = scenario();
        prepareDeploy(offPlanet, "mara");
        assertFalse(deployAndSearch(offPlanet, "mara", offPlanet.GetLSStartingLocation(), true, 0));
        var otherPersona = scenario();
        prepareDeploy(otherPersona, "trooper");
        assertFalse(deployAndSearch(otherPersona, "trooper", otherPersona.GetDSStartingLocation(), true, 0));
    }

    @Test public void GuildOfAssassinsActuallyReshufflesRemainingUsedCards() {
        boolean reordered = false;
        for (long seed : new long[]{0, 4096, 8192, 12288}) {
            var scn = scenario();
            prepareDeploy(scn, "aurra");
            scn.MoveCardsToTopOfDSUsedPile(scn.GetDSCard("seed1"), scn.GetDSCard("seed2"), scn.GetDSCard("seed3"));
            assertTrue(deployAndSearch(scn, "aurra", scn.GetDSStartingLocation(), true, seed));
            reordered |= !beforeSearch.equals(scn.GetDSUsedPile());
            assertEquals(new HashSet<>(beforeSearch), new HashSet<>(scn.GetDSUsedPile()));
        }
        assertTrue(reordered);
    }

    private List<PhysicalCard> beforeSearch;

    private void prepareDeploy(VirtualTableScenario scn, String assassin) {
        scn.MoveCardsToSideOfTable(scn.GetDSCard("guild"));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("luke"));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetDSCard("trooper"));
        scn.MoveCardsToDSHand(scn.GetDSCard(assassin));
        scn.SkipToPhase(Phase.CONTROL);
        scn.EnsureDSForcePile(10);
        scn.MoveCardsToTopOfDSUsedPile(scn.GetDSCard("target"));
        scn.SkipToPhase(Phase.DEPLOY);
    }

    private void prepareNextDeploy(VirtualTableScenario scn, String assassin) {
        scn.MoveCardsToDSHand(scn.GetDSCard(assassin));
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.EnsureDSForcePile(10);
        scn.MoveCardsToTopOfDSUsedPile(scn.GetDSCard("target"));
        scn.SkipToPhase(Phase.DEPLOY);
    }

    private boolean deployAndSearch(VirtualTableScenario scn, String assassin, PhysicalCardImpl site,
                                    boolean take, long seed) {
        var card = scn.GetDSCard(assassin);
        var guild = scn.GetDSCard("guild");
        var target = scn.GetDSCard("target");
        assertTrue("No deploy for " + assassin + " with " + scn.GetDSForcePileCount() + " Force", scn.DSDeployAvailable(card));
        scn.DSDeployCard(card);
        boolean offered = false;
        for (int i = 0; i < 70 && !scn.AwaitingDSDeployPhaseActions(); i++) {
            if (scn.DSHasCardChoiceAvailable(site)) scn.DSChooseCard(site);
            else if (scn.DSHasCardChoiceAvailable(target)) scn.DSChooseCard(target);
            else if (scn.DSGetDecision() instanceof CardActionSelectionDecision && scn.DSCardActionAvailable(guild, TAKE)) {
                offered = true;
                if (take) {
                    beforeSearch = new ArrayList<>(scn.GetDSUsedPile());
                    beforeSearch.remove(target);
                    scn.gameState().getRandom().setSeed(seed);
                    scn.DSUseCardAction(guild, TAKE);
                } else scn.DSPass();
            } else if (scn.LSGetDecision() instanceof CardActionSelectionDecision) scn.LSPass();
            else if (scn.DSGetDecision() instanceof CardActionSelectionDecision) scn.DSPass();
            else fail("Unexpected deploy/Used Pile decision: " + scn.GetCurrentDecision().getText());
        }
        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertSame(site, card.getAtLocation());
        return offered;
    }

    private void settleDeploy(VirtualTableScenario scn) {
        for (int i = 0; i < 40 && !scn.AwaitingDSDeployPhaseActions(); i++) {
            if (scn.DSHasCardChoiceAvailable(scn.GetDSCard("maul"))) scn.DSChooseCard(scn.GetDSCard("maul"));
            else if (scn.LSGetDecision() instanceof CardActionSelectionDecision) scn.LSPass();
            else if (scn.DSGetDecision() instanceof CardActionSelectionDecision) scn.DSPass();
            else fail("Unexpected deployment decision: " + scn.GetCurrentDecision().getText());
        }
        assertTrue(scn.AwaitingDSDeployPhaseActions());
    }

    @Test public void GuildOfAssassinsProtectsActualForceGenerationAgainstStrikePlanningOnlyWhenOccupied() {
        for (boolean occupied : List.of(false, true)) {
            var scn = scenario();
            var endor = scn.GetDSCard("endo");
            scn.MoveCardsToLSSideOfTable(scn.GetLSCard("strike"));
            scn.MoveCardsToDSSideOfTable(scn.GetDSCard("guild"));
            if (occupied) scn.MoveCardsToLocation(endor, scn.GetDSCard("capital"));
            scn.SkipToLSTurn(Phase.DRAW);
            scn.PassDrawActions();
            scn.PassResponses("RECIRCULATED");
            scn.PassAllResponses();
            assertTrue(scn.AwaitingDSActivatePhaseActions());
            var query = scn.game().getModifiersQuerying();
            float icons = endor.getBlueprint().getIconCount(Icon.DARK_FORCE);
            assertEquals(occupied ? icons : 0,
                    query.getForceGenerationFromLocation(scn.gameState(), endor, scn.DS), scn.epsilon);
            int before = scn.GetDSForcePileCount();
            int expected = occupied ? (int) icons + 4 : 4;
            scn.DSActivateMaxForceAndPass();
            assertEquals(expected, scn.GetDSForcePileCount() - before);
        }
    }

    @Test public void GuildOfAssassinsIsImmuneToActualAlter() {
        var scn = scenario();
        var guild = scn.GetDSCard("guild");
        scn.MoveCardsToDSSideOfTable(guild);
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("luke"));
        scn.MoveCardsToLSHand(scn.GetLSCard("alter"));
        scn.SkipToLSTurn(Phase.CONTROL);
        assertFalse(scn.LSCardPlayAvailable(scn.GetLSCard("alter")));
        assertInZone(Zone.SIDE_OF_TABLE, guild);
    }
}
