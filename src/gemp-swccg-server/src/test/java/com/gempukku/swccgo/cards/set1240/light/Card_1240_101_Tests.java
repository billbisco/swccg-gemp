package com.gempukku.swccgo.cards.set1240.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1240_101_Tests {
    private static final String DEVICE = "Deploy a device here from Reserve Deck";
    private static final String EFFECT = "Deploy an Effect on a character here from Reserve Deck";

    private VirtualTableScenario scenario() {
        var scn = new VirtualTableScenario(new HashMap<>(Map.ofEntries(
                Map.entry("outpost", "1240_101"), Map.entry("forest", "7_121"),
                Map.entry("city", "7_125"),
                Map.entry("kiffex", "6_83"), Map.entry("bothawui", "7_110"),
                Map.entry("hoth", "3_55"), Map.entry("bespin", "5_76"),
                Map.entry("coruscant", "7_116"), Map.entry("dantooine", "1_122"),
                Map.entry("kessel", "1_126"), Map.entry("tatooine", "1_127"),
                Map.entry("computer", "1_39"), Map.entry("comlink", "7_54"), Map.entry("rycar", "1_63"),
                Map.entry("savrip", "1_55"), Map.entry("falcon", "1_143"),
                Map.entry("luke", "1_19"), Map.entry("boshek", "1_4"),
                Map.entry("kiffexOperative", "7_26"), Map.entry("bothawuiOperative", "7_6"),
                Map.entry("creature", "4_3"))), new HashMap<>(Map.of(
                "premium", "112_16", "shield", "13_78", "darkOperative", "7_185")));
        scn.StartGame();
        return scn;
    }

    @Test
    public void OutpostHasPrintedIdentityAndIndependentSideText() {
        var scn = scenario();
        var card = scn.GetLSCard("outpost").getBlueprint();
        assertEquals("Outpost", card.getTitle());
        assertEquals(Side.LIGHT, card.getSide());
        assertTrue(card.isCardType(CardType.LOCATION));
        assertEquals(CardSubtype.SITE, card.getCardSubtype());
        assertEquals(Uniqueness.DIAMOND_1, card.getUniqueness());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
        assertFalse(card.hasVirtualSuffix());
        assertTrue(card.getLocationLightSideGameText().contains("Dantooine"));
        assertTrue(card.getLocationDarkSideGameText().contains("matching operative"));
        scn.BlueprintIconCheck(card, new ArrayList<>(List.of(Icon.LIGHT_FORCE, Icon.DARK_FORCE,
                Icon.HOTH, Icon.EXTERIOR_SITE, Icon.PLANET, Icon.LEGACY_BLOCK_4)));
        scn.BlueprintKeywordCheck(card, new ArrayList<>());
    }

    @Test
    public void OutpostMayJoinAnotherPlanetButNoneOfItsFiveForbiddenSystems() {
        var scn = scenario();
        for (String key : List.of("kiffex", "bespin", "coruscant", "dantooine", "kessel", "tatooine"))
            scn.MoveLocationToTable(scn.GetLSCard(key));
        var outpost = scn.GetLSCard("outpost");
        assertFalse(scn.gameState().getLocationPlacement(scn.game(), outpost, Title.Kiffex, null).isEmpty());
        for (String system : List.of(Title.Bespin, Title.Coruscant, Title.Dantooine, Title.Kessel, Title.Tatooine))
            assertTrue(system, scn.gameState().getLocationPlacement(scn.game(), outpost, system, null).isEmpty());
    }

    @Test
    public void DevicePaysItsNativeCostAndConsumesTheSharedGameLimit() {
        var scn = downloads(true, 2);
        var outpost = scn.GetLSCard("outpost");
        var computer = scn.GetLSCard("computer");
        assertTrue(scn.LSCardActionAvailable(outpost, EFFECT));
        scn.LSUseCardAction(outpost, DEVICE);
        scn.PassAllResponses();
        assertFalse(SelectableInReserve(scn, scn.GetLSCard("comlink")));
        scn.LSChooseCard(computer);
        scn.PassAllResponses();
        if (scn.LSHasCardChoiceAvailable(scn.GetLSCard("falcon")))
            scn.LSChooseCard(scn.GetLSCard("falcon"));
        scn.PassAllResponses();
        scn.PassCardAndForceUseResponses();
        assertTrue(scn.IsAttachedTo(scn.GetLSCard("falcon"), computer));
        assertTrue(scn.gameState().getLastMessages().stream().anyMatch(m -> m.contains("shuffles Reserve Deck")));
        assertEquals(0, scn.GetLSForcePileCount());
        if (scn.GetAwaitingDecision(scn.DS) != null && scn.DSDecisionAvailable("action or Pass")) scn.DSPass();
        assertFalse(scn.LSCardActionAvailable(outpost, DEVICE));
        assertFalse(scn.LSCardActionAvailable(outpost, EFFECT));
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertFalse(scn.LSCardActionAvailable(outpost, EFFECT));
    }

    @Test
    public void EffectDeploysOnTheLocalCharacterAndCannotDeployOnTable() {
        var scn = downloads(true, 2);
        var outpost = scn.GetLSCard("outpost");
        var rycar = scn.GetLSCard("rycar");
        var luke = scn.GetLSCard("luke");
        scn.LSUseCardAction(outpost, EFFECT);
        scn.PassAllResponses();
        assertFalse(SelectableInReserve(scn, scn.GetLSCard("savrip")));
        scn.LSChooseCard(rycar);
        scn.PassAllResponses();
        if (scn.LSHasCardChoiceAvailable(luke)) {
            assertTrue(scn.LSHasCardChoiceNotAvailable(scn.GetLSCard("boshek")));
            scn.LSChooseCard(luke);
        }
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(luke, rycar));
        assertTrue(scn.gameState().getLastMessages().stream().anyMatch(m -> m.contains("shuffles Reserve Deck")));
        if (scn.GetAwaitingDecision(scn.DS) != null && scn.DSDecisionAvailable("action or Pass")) scn.DSPass();
        assertFalse(scn.LSCardActionAvailable(outpost, DEVICE));
    }

    @Test
    public void UnaffordableDeviceCannotBeChosenEvenWithALocalStarship() {
        var scn = downloads(true, 1);
        scn.LSUseCardAction(scn.GetLSCard("outpost"), DEVICE);
        scn.PassAllResponses();
        assertFalse(SelectableInReserve(scn, scn.GetLSCard("computer")));
        assertEquals(1, scn.GetLSForcePileCount());
    }

    @Test
    public void OriginalWristComlinkCanStillDeployFromHandAtOutpost() {
        var scn = scenario();
        var outpost = scn.GetLSCard("outpost");
        var luke = scn.GetLSCard("luke");
        var comlink = scn.GetLSCard("comlink");
        scn.MoveLocationToTable(scn.GetLSCard("kiffex"));
        place(scn, "outpost", Title.Kiffex);
        scn.MoveCardsToLocation(outpost, luke);
        scn.MoveCardsToHand(comlink);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertFalse(scn.LSCardActionAvailable(outpost, DEVICE));
        assertFalse(scn.LSCardActionAvailable(outpost, EFFECT));
        scn.EnsureLSForcePile(1);
        scn.DrainLSForcePileTo(1);
        scn.SkipToPhase(Phase.DEPLOY);
        scn.LSDeployCard(comlink);
        scn.PassAllResponses();
        if (scn.LSHasCardChoiceAvailable(luke)) scn.LSChooseCard(luke);
        scn.PassAllResponses();
        scn.PassCardAndForceUseResponses();
        assertTrue(scn.IsAttachedTo(luke, comlink));
        assertEquals(0, scn.GetLSForcePileCount());
    }

    @Test
    public void RemoteStarshipAndCharacterDoNotQualify() {
        var scn = downloads(false, 2);
        var outpost = scn.GetLSCard("outpost");
        assertFalse(scn.LSCardActionAvailable(outpost, EFFECT));
        scn.LSUseCardAction(outpost, DEVICE);
        scn.PassAllResponses();
        assertFalse(SelectableInReserve(scn, scn.GetLSCard("computer")));
        assertEquals(2, scn.GetLSForcePileCount());
    }

    @Test
    public void PremiumNoEscapeIsBlockedAtOutpostAndRelatedSitesOnly() {
        var scn = operativeLocations();
        var premium = scn.GetDSCard("premium");
        var outpost = scn.GetLSCard("outpost");
        var related = scn.GetLSCard("forest");
        var unrelated = scn.GetLSCard("city");
        float atOutpost = generation(scn, outpost);
        float atForest = generation(scn, related);
        float atCity = generation(scn, unrelated);
        scn.MoveCardsToDSSideOfTable(premium);
        assertEquals(atOutpost, generation(scn, outpost), 0);
        assertEquals(atForest, generation(scn, related), 0);
        assertEquals(atCity + 1, generation(scn, unrelated), 0);

        scn.MoveCardsToLocation(outpost, scn.GetLSCard("bothawuiOperative"), scn.GetDSCard("darkOperative"));
        assertEquals(atOutpost, generation(scn, outpost), 0);
        scn.MoveCardsToLocation(unrelated, scn.GetLSCard("bothawuiOperative"));
        assertEquals(atOutpost, generation(scn, outpost), 0);
        scn.MoveCardsToLocation(related, scn.GetLSCard("kiffexOperative"));
        assertEquals(atOutpost + 1, generation(scn, outpost), 0);
        assertEquals(atForest + 1, generation(scn, related), 0);
        scn.MoveCardsToLocation(outpost, scn.GetLSCard("kiffexOperative"));
        assertEquals(atOutpost + 1, generation(scn, outpost), 0);
        scn.MoveOutOfPlay(scn.GetLSCard("kiffexOperative"));
        assertEquals(atOutpost, generation(scn, outpost), 0);
    }

    @Test
    public void SameNamedReflectionsShieldRetainsItsNativeForceBonus() {
        var scn = operativeLocations();
        var outpost = scn.GetLSCard("outpost");
        scn.MoveCardsToLocation(outpost, scn.GetLSCard("creature"));
        float before = generation(scn, outpost);
        scn.MoveCardsToDSSideOfTable(scn.GetDSCard("shield"));
        assertEquals(before + 1, generation(scn, outpost), 0);
    }

    private VirtualTableScenario downloads(boolean local, int force) {
        var scn = scenario();
        var outpost = scn.GetLSCard("outpost");
        var remote = scn.GetLSStartingLocation();
        scn.MoveLocationToTable(scn.GetLSCard("kiffex"));
        place(scn, "outpost", Title.Kiffex);
        scn.MoveCardsToLocation(local ? outpost : remote, scn.GetLSCard("falcon"), scn.GetLSCard("luke"));
        scn.MoveCardsToLocation(remote, scn.GetLSCard("boshek"));
        scn.SkipToLSTurn(Phase.CONTROL);
        var searched = new PhysicalCardImpl[] {scn.GetLSCard("computer"), scn.GetLSCard("rycar"),
                scn.GetLSCard("savrip"), scn.GetLSCard("comlink")};
        // Held aside while the Force Pile is set, so that none of them is counted as Force.
        scn.MoveCardsToLSHand(searched);
        scn.EnsureLSForcePile(force);
        scn.DrainLSForcePileTo(force);
        scn.MoveCardsToTopOfLSReserveDeck(searched);
        assertEquals(force, scn.GetLSForcePileCount());
        scn.SkipToPhase(Phase.DEPLOY);
        return scn;
    }

    private static boolean SelectableInReserve(VirtualTableScenario scn, PhysicalCardImpl card) {
        var blueprints = scn.GetADParam(scn.LS, "blueprintId");
        var selectable = scn.GetADParam(scn.LS, "selectable");
        if (blueprints == null || selectable == null) return false;
        for (int i = 0; i < blueprints.length; i++) {
            if (blueprints[i].equals(card.getBlueprintId(true)) && Boolean.parseBoolean(selectable[i])) return true;
        }
        return false;
    }

    private VirtualTableScenario operativeLocations() {
        var scn = scenario();
        scn.MoveLocationToTable(scn.GetLSCard("kiffex"));
        scn.MoveLocationToTable(scn.GetLSCard("bothawui"));
        place(scn, "outpost", Title.Kiffex);
        place(scn, "forest", Title.Kiffex);
        place(scn, "city", Title.Bothawui);
        return scn;
    }

    private void place(VirtualTableScenario scn, String card, String system) {
        var site = scn.GetLSCard(card);
        var placements = scn.gameState().getLocationPlacement(scn.game(), site, system, null);
        scn.MoveLocationToTable(site, placements.getFirst());
    }

    private float generation(VirtualTableScenario scn, PhysicalCardImpl site) {
        return scn.game().getModifiersQuerying().getForceGenerationFromLocation(scn.gameState(), site, scn.DS);
    }
}
