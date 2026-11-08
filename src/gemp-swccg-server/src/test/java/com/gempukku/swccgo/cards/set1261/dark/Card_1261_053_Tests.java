package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.legacy.MissionTestSupport;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.game.SwccgGame;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class Card_1261_053_Tests {
    private static final String SEARCH = "Deploy Effect on a character or device here";

    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(
                new HashMap<>(Map.of("luke", "1_19", "han", "1_11", "ounee", "112_4")),
                new HashMap<>(Map.ofEntries(
                        Map.entry("depot", "1261_53"), Map.entry("planet", "1_282"),
                        Map.entry("related", "7_281"), Map.entry("sleen", "4_111"),
                        Map.entry("match", "7_174"), Map.entry("nonmatch", "7_173"),
                        Map.entry("bounty", "5_113"), Map.entry("tableEffect", "1_225"),
                        Map.entry("siteEffect", "1_227"), Map.entry("holocam", "1_204"))),
                26, 26, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    private void placeOnDantooine(VirtualTableScenario scn, PhysicalCardImpl site) {
        var locations = scn.gameState().getLocationPlacement(scn.game(), site, Title.Dantooine, null);
        assertFalse("Expected legal Dantooine placement for " + site.getTitle(), locations.isEmpty());
        scn.MoveLocationToTable(site, locations.getFirst());
    }

    private void startAndPlaceDepot(VirtualTableScenario scn) {
        scn.StartGame();
        scn.MoveLocationToTable(scn.GetDSCard("planet"));
        placeOnDantooine(scn, scn.GetDSCard("depot"));
        placeOnDantooine(scn, scn.GetDSCard("related"));
    }

    @Test
    public void printedDiamondSiteAndAllFiveDeploymentExclusions() {
        var scn = scenario();
        var depot = scn.GetDSCard("depot");
        var card = depot.getBlueprint();
        assertEquals("Remote Depot", card.getTitle());
        assertEquals(Uniqueness.DIAMOND_1, card.getUniqueness());
        assertEquals(CardSubtype.SITE, card.getCardSubtype());
        assertEquals(Side.DARK, card.getSide());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
        assertEquals(1, card.getIconCount(Icon.DARK_FORCE));
        assertEquals(1, card.getIconCount(Icon.LIGHT_FORCE));
        scn.BlueprintIconCheck(card, new ArrayList<>(List.of(Icon.DARK_FORCE, Icon.LIGHT_FORCE,
                Icon.HOTH, Icon.EXTERIOR_SITE, Icon.PLANET, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(card, new ArrayList<>());
        startAndPlaceDepot(scn);
        for (String planet : List.of(Title.Bespin, Title.Coruscant, Title.Kessel, Title.Ralltiir, Title.Tatooine))
            assertTrue("Excluded from " + planet, card.mayNotBePartOfSystem(scn.game(), planet));
        assertFalse(card.mayNotBePartOfSystem(scn.game(), Title.Dantooine));
    }

    @Test
    public void oncePerGameSearchDeploysOnCharacterHereAndReshuffles() {
        var scn = scenario();
        var depot = scn.GetDSCard("depot");
        var bounty = scn.GetDSCard("bounty");
        var luke = scn.GetLSCard("luke");
        var han = scn.GetLSCard("han");
        startAndPlaceDepot(scn);
        scn.MoveCardsToLocation(depot, luke);
        scn.MoveCardsToLocation(scn.GetDSCard("related"), han);
        scn.SkipToDSTurn(Phase.DEPLOY);
        for (String key : List.of("bounty", "tableEffect", "siteEffect"))
            scn.MoveCardsToTopOfOwnReserveDeck(scn.GetDSCard(key));
        var revealed = scn.GetDSCard("siteEffect");
        scn.gameState().markCardsRevealedFromPile(List.of(revealed));
        assertTrue(scn.DSCardActionAvailable(depot, SEARCH));
        scn.DSUseCardAction(depot, SEARCH);
        scn.PassAllResponses();
        assertTrue(selectable(scn, bounty));
        assertFalse(selectable(scn, scn.GetDSCard("tableEffect")));
        assertFalse(selectable(scn, revealed));
        scn.DSChooseCard(bounty);
        if (scn.DSDecisionAvailable("Choose where")) {
            assertTrue(scn.DSHasCardChoiceAvailable(luke));
            assertFalse(scn.DSHasCardChoiceAvailable(han));
            scn.DSChooseCard(luke);
        }
        scn.PassAllResponses();
        assertSame(luke, bounty.getAttachedTo());
        assertTrue(scn.GetDSReserveDeck().contains(revealed));
        assertFalse(scn.gameState().isCardRevealedFromPile(revealed));
        if (scn.AwaitingLSDeployPhaseActions())
            scn.LSPass();
        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertFalse(scn.DSCardActionAvailable(depot, SEARCH));
        assertTrue(GameTextActionId.LEGACY__REMOTE_DEPOT__DEPLOY_EFFECT.isPerGame());
        assertTrue(GameTextActionId.LEGACY__REMOTE_DEPOT__DEPLOY_EFFECT.isSearchCardPile());
    }

    @Test
    public void canDeployEffectOnADeviceAtTheDepot() {
        var scn = scenario();
        var depot = scn.GetDSCard("depot");
        var holocam = scn.GetDSCard("holocam");
        startAndPlaceDepot(scn);
        scn.MoveCardsToLocation(depot, holocam);
        scn.SkipToDSTurn(Phase.DEPLOY);
        // Minimal normal Effect exercises the printed device target independently of the card pool.
        var effect = MissionTestSupport.putInHand(scn, 9053, scn.DS,
                new AbstractNormalEffect(Side.DARK, 1, PlayCardZoneOption.ATTACHED,
                        "Test effect for device attachment", Uniqueness.UNIQUE, ExpansionSet.PREMIERE, Rarity.R1) {
                    @Override
                    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self,
                                                                          PlayCardOptionId option, boolean asReact) {
                        return Filters.device;
                    }
                });
        scn.MoveCardsToTopOfOwnReserveDeck(effect);
        assertTrue(scn.DSCardActionAvailable(depot, SEARCH));
        scn.DSUseCardAction(depot, SEARCH);
        scn.PassAllResponses();
        assertTrue(selectable(scn, effect));
        scn.DSChooseCard(effect);
        if (scn.DSDecisionAvailable("Choose where"))
            scn.DSChooseCard(holocam);
        scn.PassAllResponses();
        assertSame(holocam, effect.getAttachedTo());
    }

    @Test
    public void otherSitesAndControlPhaseDoNotAllowTheDownload() {
        var scn = scenario();
        var depot = scn.GetDSCard("depot");
        startAndPlaceDepot(scn);
        scn.MoveCardsToLocation(scn.GetDSCard("related"), scn.GetLSCard("luke"), scn.GetDSCard("holocam"));
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertFalse(scn.DSCardActionAvailable(depot, SEARCH));
        scn.MoveCardsToLocation(depot, scn.GetLSCard("luke"));
        scn.SkipToDSTurn(Phase.CONTROL);
        assertFalse(scn.DSCardActionAvailable(depot, SEARCH));
    }

    @Test
    public void ouneeTaImmunityCoversTheDepotAndRelatedSitesUntilMatchingOperativeOrSleenArrives() {
        var scn = scenario();
        var depot = scn.GetDSCard("depot");
        var related = scn.GetDSCard("related");
        var away = scn.GetDSStartingLocation();
        startAndPlaceDepot(scn);
        scn.MoveCardsToLSSideOfTable(scn.GetLSCard("ounee"));
        assertTrue(immune(scn, depot));
        assertTrue(immune(scn, related));
        assertFalse(immune(scn, away));
        scn.MoveCardsToLocation(related, scn.GetDSCard("nonmatch"));
        assertTrue(immune(scn, depot));
        assertTrue(immune(scn, related));
        scn.MoveCardsToLocation(related, scn.GetDSCard("match"));
        assertFalse(immune(scn, depot));
        assertFalse(immune(scn, related));
        scn.MoveCardsToLocation(away, scn.GetDSCard("match"));
        assertTrue(immune(scn, depot));
        scn.MoveCardsToLocation(depot, scn.GetDSCard("sleen"));
        assertFalse(immune(scn, depot));
        assertFalse(immune(scn, related));
    }

    private boolean immune(VirtualTableScenario scn, PhysicalCardImpl site) {
        return scn.game().getModifiersQuerying().isImmuneToCardTitle(scn.gameState(), site, Title.Ounee_Ta);
    }

    private boolean selectable(VirtualTableScenario scn, PhysicalCardImpl card) {
        var ids = scn.DSGetADParamAsList("blueprintId");
        int index = ids != null ? ids.indexOf(card.getBlueprintId(true))
                : scn.DSGetADParamAsList("cardId").indexOf(Integer.toString(card.getCardId()));
        if (index < 0)
            return false;
        var flags = scn.DSGetADParamAsList("selectable");
        return flags == null || flags.isEmpty() || Boolean.parseBoolean(flags.get(index));
    }

    @org.junit.Test
    public void CurrentTextIsTheLatestVersion() {
        // Virtual Block 6.1 sheet text is the only published version; GEMP's 'same site or related site' was a PC data error.
        var scn = new com.gempukku.swccgo.framework.VirtualTableScenario(new java.util.HashMap<>(), new java.util.HashMap<>(java.util.Map.of("c", "1261_53")), 10, 10,
                com.gempukku.swccgo.framework.StartingSetup.DefaultLSGroundLocation, com.gempukku.swccgo.framework.StartingSetup.DefaultDSGroundLocation,
                com.gempukku.swccgo.framework.StartingSetup.NoLSStartingInterrupts, com.gempukku.swccgo.framework.StartingSetup.NoDSStartingInterrupts,
                com.gempukku.swccgo.framework.StartingSetup.NoLSShields, com.gempukku.swccgo.framework.StartingSetup.NoDSShields,
                com.gempukku.swccgo.framework.VirtualTableScenario.Open);
        var bp = scn.GetDSCard("c").getBlueprint();
        org.junit.Assert.assertEquals("Unless opponent's Sleen or matching operative at same or related site, those sites are immune to Ounee Ta.", bp.getLocationLightSideGameText());
        org.junit.Assert.assertEquals("May not deploy to Bespin, Coruscant, Kessel, Ralltiir, or Tatooine. Once per game, may deploy an Effect that deploys on a character or a device here from Reserve Deck; reshuffle.", bp.getLocationDarkSideGameText());
        org.junit.Assert.assertFalse(bp.getLocationLightSideGameText().contains("same site or related site"));
    }
}
