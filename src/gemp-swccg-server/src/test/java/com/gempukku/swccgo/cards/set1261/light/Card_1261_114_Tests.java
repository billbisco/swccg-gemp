package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.*;
import static org.junit.Assert.*;

public class Card_1261_114_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("ric", "12_24"); put("ship", "12_91"); put("otherShip", "2_71");
            put("otherCharacter", "1_27"); put("trooper", "1_28"); put("trooper2", "1_28");
            put("otherSite", "1_136");
        }}, new HashMap<>() {{ put("vader", "1_168"); }}, 50, 50,
                StartingSetup.LSStartingLocation("1261_114"), StartingSetup.DSStartingLocation("2_148"),
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void TatooineQueensLandingSiteStatsMatchFinalMaster() {
        //test1: final master prints one Light Force icon and no Dark Force icons.
        var scn = scenario(); var b = scn.GetLSStartingLocation().getBlueprint();
        assertEquals("Tatooine: Queen's Landing Site", b.getTitle()); //test1
        assertEquals(Side.LIGHT, b.getSide()); assertEquals(Uniqueness.UNIQUE, b.getUniqueness());
        assertFalse(b.hasVirtualSuffix()); assertEquals(0, b.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.SITE, b.getCardSubtype()); assertEquals(Title.Tatooine, b.getSystemName());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, b.getExpansionSet()); assertEquals(Rarity.V, b.getRarity());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(Arrays.asList(CardType.LOCATION)));
        scn.BlueprintIconCheck(b, new ArrayList<>(Arrays.asList(Icon.PREMIUM, Icon.EPISODE_I,
                Icon.EXTERIOR_SITE, Icon.PLANET, Icon.LIGHT_FORCE, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>());
    }

    private boolean selectable(VirtualTableScenario scn, com.gempukku.swccgo.game.PhysicalCardImpl card) {
        String[] blueprints = scn.LSGetADParam("blueprintId"), enabled = scn.LSGetADParam("selectable");
        if (blueprints == null || enabled == null) return scn.LSHasCardChoiceAvailable(card);
        for (int i = 0; i < blueprints.length; i++)
            if (blueprints[i].equals(card.getBlueprintId(true)) && Boolean.parseBoolean(enabled[i])) return true;
        return false;
    }

    private void skipEmptySearches(VirtualTableScenario scn) {
        while (scn.LSGetADParam("selectable") != null && scn.LSDecisionAvailable("deploy from")
                && Arrays.stream(scn.LSGetADParam("selectable")).noneMatch(Boolean::parseBoolean)) {
            scn.LSChooseCards(); scn.PassAllResponses();
        }
    }

    private VirtualTableScenario deploy(int force, boolean ricInReserve, boolean shipInReserve) {
        var scn = scenario(); scn.StartGame();
        if (!ricInReserve) scn.MoveCardsToLSHand(scn.GetLSCard("ric"));
        if (!shipInReserve) scn.MoveCardsToLSHand(scn.GetLSCard("ship"));
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSReserveDeck(scn.GetLSForcePile().toArray(com.gempukku.swccgo.game.PhysicalCardImpl[]::new));
        scn.LSActivateForceCheat(force);
        scn.LSUseCardAction(scn.GetLSStartingLocation(), "Deploy Ric Olie");
        scn.PassAllResponses();
        skipEmptySearches(scn);
        return scn;
    }

    private void choose(VirtualTableScenario scn, String name) {
        var card = scn.GetLSCard(name); assertTrue(selectable(scn, card));
        scn.LSChooseCard(card); scn.PassAllResponses();
        skipEmptySearches(scn);
        if (scn.LSHasCardChoiceAvailable(scn.GetLSStartingLocation())) {
            scn.LSChooseCard(scn.GetLSStartingLocation()); scn.PassAllResponses();
        }
    }

    @Test
    public void TatooineQueensLandingSiteDownloadsOnlyRicForTwoForce() {
        //test1: Ric alone is legal and costs printed deploy3 minus1.
        var scn = deploy(2, true, false); choose(scn, "ric");
        assertAtLocation(scn.GetLSStartingLocation(), scn.GetLSCard("ric")); //test1
        assertEquals(0, scn.GetLSForcePileCount());
        assertInHand(scn.GetLSCard("ship"));
    }

    @Test
    public void TatooineQueensLandingSiteDownloadsOnlyNubianForThreeForce() {
        //test1: a ship alone is legal and costs printed deploy4 minus1.
        var scn = deploy(3, false, true); choose(scn, "ship");
        assertAtLocation(scn.GetLSStartingLocation(), scn.GetLSCard("ship")); //test1
        assertEquals(0, scn.GetLSForcePileCount());
        assertInHand(scn.GetLSCard("ric"));
    }

    @Test
    public void TatooineQueensLandingSiteDownloadsBothForFiveForce() {
        //test1: one Nubian and Ric can both deploy, each receiving its own discount.
        var scn = deploy(5, true, true); choose(scn, "ship"); choose(scn, "ric");
        assertAtLocation(scn.GetLSStartingLocation(), scn.GetLSCard("ship"), scn.GetLSCard("ric")); //test1
        assertEquals(0, scn.GetLSForcePileCount());
    }

    @Test
    public void TatooineQueensLandingSiteDoesNotOfferOtherStarfightersOrCharacters() {
        //test1: the first selection accepts only a327 Nubian, and the second only Ric.
        var scn = deploy(5, true, true);
        assertTrue(selectable(scn, scn.GetLSCard("ship"))); //test1
        assertFalse(selectable(scn, scn.GetLSCard("otherShip")));
        assertFalse(selectable(scn, scn.GetLSCard("ric")));
        choose(scn, "ship");
        assertTrue(selectable(scn, scn.GetLSCard("ric")));
        assertFalse(selectable(scn, scn.GetLSCard("otherCharacter")));
    }

    @Test
    public void TatooineQueensLandingSiteCannotDeployUnaffordableRic() {
        //test1: one Force cannot pay Ric's reduced deploy cost of2.
        var scn = deploy(1, true, false);
        assertInZone(Zone.RESERVE_DECK, scn.GetLSCard("ric")); //test1
        assertEquals(1, scn.GetLSForcePileCount());
        assertFalse(selectable(scn, scn.GetLSCard("ric")));
    }

    @Test
    public void TatooineQueensLandingSiteCannotDeployUnaffordableNubian() {
        //test1: two Force cannot pay the Nubian's reduced deploy cost of3.
        var scn = deploy(2, false, true);
        assertInZone(Zone.RESERVE_DECK, scn.GetLSCard("ship")); //test1
        assertEquals(2, scn.GetLSForcePileCount());
        assertFalse(selectable(scn, scn.GetLSCard("ship")));
    }

    @Test
    public void TatooineQueensLandingSiteMayDeclineEitherOrBothDownloads() {
        //test1: both categories are optional; declining spends no Force but consumes this turn's use.
        var scn = deploy(5, true, true); scn.LSChooseCards(); scn.PassAllResponses();
        assertTrue(selectable(scn, scn.GetLSCard("ric"))); //test1
        scn.LSChooseCards(); scn.PassAllResponses();
        assertInZone(Zone.RESERVE_DECK, scn.GetLSCard("ship"), scn.GetLSCard("ric"));
        assertEquals(5, scn.GetLSForcePileCount());
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertFalse(scn.LSCardActionAvailable(scn.GetLSStartingLocation(), "Deploy Ric Olie"));
        scn.SkipToDSTurn(Phase.ACTIVATE); scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSCardActionAvailable(scn.GetLSStartingLocation(), "Deploy Ric Olie"));
    }

    @Test
    public void TatooineQueensLandingSiteLandedPermissionExpiresWithDownloadAction() {
        //test1: declining the download does not permit a subsequent ordinary hand deployment here.
        var scn = deploy(8, false, true); scn.LSChooseCards(); scn.PassAllResponses(); skipEmptySearches(scn);
        var site = scn.GetLSStartingLocation(); var other = scn.GetLSCard("otherSite");
        scn.MoveLocationToTable(other); scn.MoveCardsToLSHand(scn.GetLSCard("ship"));
        // A normal Ric deployment refreshes the real deploy decision after moving the fixture to hand.
        scn.LSDeployCard(scn.GetLSCard("ric"));
        if (scn.LSHasCardChoiceAvailable(site)) scn.LSChooseCard(site);
        scn.PassAllResponses(); scn.LSDeployCard(scn.GetLSCard("ship"));
        assertFalse(scn.LSHasCardChoiceAvailable(site)); //test1
        assertTrue(scn.LSHasCardChoiceAvailable(other)); scn.LSChooseCard(other); scn.PassAllResponses();
        assertAtLocation(other, scn.GetLSCard("ship"));
        assertEquals(1, scn.GetLSForcePileCount());
    }

    @Test
    public void TatooineQueensLandingSiteDoesNotDownloadOutsideDeployPhase() {
        var scn = scenario(); scn.StartGame(); scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.AwaitingLSControlPhaseActions());
        assertFalse(scn.LSCardActionAvailable(scn.GetLSStartingLocation(), "Deploy Ric Olie"));
    }

    @Test
    public void TatooineQueensLandingSitePreventsLightSideDrainButAllowsAnotherSite() {
        //test1: the prohibition is for the opponent of the site's Dark text, rather than its owner.
        var scn = scenario(); scn.StartGame(); var site = scn.GetLSStartingLocation();
        var other = scn.GetLSCard("otherSite"); scn.MoveLocationToTable(other);
        scn.MoveCardsToLocation(site, scn.GetLSCard("trooper"));
        scn.MoveCardsToLocation(other, scn.GetLSCard("trooper2"));
        scn.SkipToLSTurn(Phase.CONTROL);
        assertFalse(scn.LSForceDrainAvailable(site)); //test1
        assertTrue(scn.LSForceDrainAvailable(other));
        int before = scn.GetDSLostPileCount(); scn.LSForceDrainAt(other); scn.PassForceDrainStartResponses();
        scn.DSPayRemainingForceLossFromReserveDeck(); scn.PassAllResponses();
        assertEquals(1, scn.GetDSLostPileCount() - before);
    }

    @Test
    public void TatooineQueensLandingSiteAddsOneToActualDarkSideDrain() {
        //test1: the printed Light icon plus Dark text's bonus causes two actual Force loss.
        var scn = scenario(); scn.StartGame(); var site = scn.GetLSStartingLocation();
        scn.MoveCardsToLocation(site, scn.GetDSCard("vader")); scn.SkipToDSTurn(Phase.CONTROL);
        assertTrue(scn.DSForceDrainAvailable(site)); //test1
        int before = scn.GetLSLostPileCount(); scn.DSForceDrainAt(site); scn.PassForceDrainStartResponses();
        scn.LSPayRemainingForceLossFromReserveDeck(); scn.PassAllResponses();
        assertEquals(2, scn.GetLSLostPileCount() - before);
    }
}
