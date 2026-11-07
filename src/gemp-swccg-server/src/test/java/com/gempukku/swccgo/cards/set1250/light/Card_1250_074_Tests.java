package com.gempukku.swccgo.cards.set1250.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

public class Card_1250_074_Tests {
    private static final String SEARCH = "Deploy a starship site from Reserve Deck";

    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("platform", "1250_74"); put("dock", "12_74"); put("fighter", "1_146"); put("home", "9_74");
            put("bay", "9_57"); put("room", "9_58"); put("other", "12_83"); put("plains", "12_79");
        }}, new HashMap<>(), 35, 35, StartingSetup.DefaultLSSpaceSystem, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void FinalPlatformHasTwoLightOneDarkAndNoVirtualSuffix() {
        var s = scenario(); var bp = s.GetLSCard("platform").getBlueprint();
        assertEquals("Coruscant: Senate Landing Platform", bp.getTitle()); assertFalse(bp.hasVirtualSuffix());
        assertEquals(Title.Coruscant, bp.getSystemName()); assertEquals(Uniqueness.UNIQUE, bp.getUniqueness());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_5, bp.getExpansionSet()); assertEquals(CardSubtype.SITE, bp.getCardSubtype());
        s.BlueprintCardTypeCheck(bp, new ArrayList<>(List.of(CardType.LOCATION)));
        s.BlueprintIconCheck(bp, new ArrayList<>(List.of(Icon.LIGHT_FORCE, Icon.DARK_FORCE, Icon.EXTERIOR_SITE,
                Icon.PLANET, Icon.EPISODE_I, Icon.CORUSCANT, Icon.LEGACY_BLOCK_5)));
        s.BlueprintKeywordCheck(bp, new ArrayList<>());
        assertEquals(2, bp.getIconCount(Icon.LIGHT_FORCE)); assertEquals(1, bp.getIconCount(Icon.DARK_FORCE));
    }

    @Test
    public void ActualStarfighterCanDeployAtPlatformButNotOrdinaryExteriorSite() {
        var s = scenario(); s.StartGame(); var platform = s.GetLSCard("platform"); var dock = s.GetLSCard("dock");
        var fighter = s.GetLSCard("fighter"); var plains = s.GetLSCard("plains");
        s.MoveLocationToTable(platform); s.MoveLocationToTable(dock); s.MoveLocationToTable(plains);
        s.MoveCardsToHand(fighter);
        s.SkipToLSTurn(Phase.DEPLOY);
        if (s.GetLSForcePileCount() < 2) s.LSActivateForceCheat(2 - s.GetLSForcePileCount());
        assertTrue(s.LSDeployAvailable(fighter)); s.LSDeployCard(fighter); s.PassAllResponses();
        assertTrue("Printed site permission", s.LSHasCardChoicesAvailable(platform));
        assertTrue("Normal docking bay permission remains", s.LSHasCardChoicesAvailable(dock));
        assertFalse("Ordinary exterior site", s.LSHasCardChoicesAvailable(plains));
        s.LSChooseCard(platform); s.PassAllResponses();
        assertEquals(platform, fighter.getAtLocation());
    }

    private boolean searchOffered(VirtualTableScenario s) {
        return s.GetADParam(s.LS, "cardId") != null && s.GetADParam(s.LS, "actionText") != null
                && s.LSCardActionAvailable(s.GetLSCard("platform"), SEARCH);
    }

    private VirtualTableScenario searchSetup(boolean homeOnTable) {
        var s = scenario(); s.StartGame(); s.MoveLocationToTable(s.GetLSCard("platform"));
        if (homeOnTable) s.MoveCardsToLocation(s.GetLSStartingLocation(), s.GetLSCard("home"));
        s.SkipToLSTurn(Phase.DEPLOY);
        s.MoveCardsToTopOfLSReserveDeck(s.GetLSCard("bay"), s.GetLSCard("room"), s.GetLSCard("other"));
        return s;
    }

    private void selectable(VirtualTableScenario s, String name, boolean expected) {
        int index = s.LSGetADParamAsList("blueprintId").indexOf(s.GetLSCard(name).getBlueprintId(true));
        assertTrue(name, index >= 0);
        assertEquals(name, Boolean.toString(expected), s.LSGetADParam("selectable")[index]);
    }

    @Test
    public void ActualDownloadRequiresMatchingShipAndReshufflesOnceThisTurn() {
        var s = searchSetup(true); var other = s.GetLSCard("other"); var bay = s.GetLSCard("bay");
        s.gameState().markCardsRevealedFromPile(List.of(other));
        assertTrue(searchOffered(s)); s.LSUseCardAction(s.GetLSCard("platform"), SEARCH); s.PassAllResponses();
        selectable(s, "bay", true); selectable(s, "room", true); selectable(s, "other", false);
        s.LSChooseCard(bay); s.PassAllResponses();
        assertEquals(Zone.LOCATIONS, bay.getZone());
        assertFalse(s.gameState().isCardRevealedFromPile(other));
        assertFalse(searchOffered(s)); assertTrue(s.GetLSReserveDeck().contains(s.GetLSCard("room")));
    }

    @Test
    public void SitesAreNotEligibleWhileTheirStarshipIsOffTable() {
        var s = searchSetup(false);
        if (searchOffered(s)) {
            s.LSUseCardAction(s.GetLSCard("platform"), SEARCH); s.PassAllResponses();
            if (s.LSGetADParam("selectable") != null) {
                selectable(s, "bay", false); selectable(s, "room", false); selectable(s, "other", false);
            }
        }
        assertTrue(s.GetLSReserveDeck().contains(s.GetLSCard("bay")));
        assertTrue(s.GetLSReserveDeck().contains(s.GetLSCard("room")));
    }
    private static final String RETRIEVE = "Retrieve 1 Force";

    /**
     * Legacy Wipe Them Out on table; the Light Side has one non-battleground site with no related locations (a
     * starship site, or an ordinary site made a holosite). Returns at Dark's chance to use Wipe in Light's draw phase.
     */
    private VirtualTableScenario wipeDraw(boolean platform, boolean occupy, boolean holosite) {
        var s = new VirtualTableScenario(new HashMap<>() {{
            put("platform", "1250_74"); put("bay", "9_57"); put("swamp", "4_85");
        }}, new HashMap<>() {{
            put("wipe", "1250_25"); put("trooper", "1_194");
        }}, 35, 35, StartingSetup.DefaultLSSpaceSystem, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
        s.StartGame();
        var platformSite = s.GetLSCard("platform");
        s.AttachCardsTo(s.GetDSStartingLocation(), s.GetDSCard("wipe"));
        if (platform) s.MoveLocationToTable(platformSite);
        if (holosite) {
            var swamp = s.GetLSCard("swamp"); s.MoveLocationToTable(swamp);
            s.game().getModifiersEnvironment().addUntilEndOfGameModifier(
                    new KeywordModifier(null, Filters.samePermanentCardId(swamp), Keyword.HOLOSITE));
        } else {
            s.MoveLocationToTable(s.GetLSCard("bay"));
        }
        if (occupy) s.MoveCardsToLocation(platformSite, s.GetDSCard("trooper"));
        s.SkipToLSTurn(Phase.DRAW); s.LSPass();
        return s;
    }

    private boolean wipeOffered(VirtualTableScenario s) {
        return s.DSCardActionAvailable(s.GetDSCard("wipe"), RETRIEVE);
    }

    @Test
    public void WithoutThePlatformWipeCountsTheIsolatedStarshipSite() {
        assertTrue(wipeOffered(wipeDraw(false, false, false)));
    }

    @Test
    public void UnlessDarkOccupiesWipeIgnoresStarshipSites() {
        assertFalse(wipeOffered(wipeDraw(true, false, false)));
    }

    @Test
    public void UnlessDarkOccupiesWipeIgnoresHolosites() {
        assertTrue(wipeOffered(wipeDraw(false, false, true)));
        assertFalse(wipeOffered(wipeDraw(true, false, true)));
    }

    @Test
    public void WhenDarkOccupiesThePlatformWipeCountsStarshipSitesAndHolosites() {
        assertTrue(wipeOffered(wipeDraw(true, true, false)));
        assertTrue(wipeOffered(wipeDraw(true, true, true)));
    }
}
