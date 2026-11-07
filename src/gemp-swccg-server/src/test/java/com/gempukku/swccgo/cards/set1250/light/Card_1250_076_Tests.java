package com.gempukku.swccgo.cards.set1250.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.*;

public class Card_1250_076_Tests {
    private VirtualTableScenario scenario(boolean starting) {
        HashMap<String, String> light = new HashMap<>();
        if (!starting) light.put("kashyyyk", "1250_76");
        light.put("forest", "1116_16"); light.put("haven", "1116_17");
        light.put("otherSite", "1_132"); light.put("interrupt", "1116_14");
        light.put("tantive", "2_73"); light.put("biggs", "1_3"); light.put("tatooine", "1_127");
        return new VirtualTableScenario(light, new HashMap<>() {{
            put("destroyer", "1_301"); put("motti", "1_164"); put("darkKashyyyk", "2_146");
            put("pilot1", "1_180"); put("pilot2", "1_180");
        }}, 15, 15, starting ? StartingSetup.LSStartingLocation("1250_76") : StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation, StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    private boolean selectable(VirtualTableScenario scn, PhysicalCardImpl card) {
        String[] ids = scn.GetADParam(scn.LS, "cardId");
        String identity = String.valueOf(card.getCardId());
        if (ids == null || (ids.length > 0 && ids[0].startsWith("temp"))) {
            ids = scn.GetADParam(scn.LS, "blueprintId");
            identity = card.getBlueprintId(true);
        }
        if (ids == null) return false;
        String[] enabled = scn.GetADParam(scn.LS, "selectable");
        for (int i = 0; i < ids.length; i++) {
            if (identity.equals(ids[i]) && (enabled == null || Boolean.parseBoolean(enabled[i]))) return true;
        }
        return false;
    }

    @Test
    public void KashyyykMetadataMatchesFinalPrinted75EImage() {
        var scn = scenario(true);
        var card = scn.GetLSStartingLocation().getBlueprint();
        assertEquals("Kashyyyk", card.getTitle());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(6, card.getParsec());
        assertEquals(CardSubtype.SYSTEM, card.getCardSubtype());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_5, card.getExpansionSet());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{ add(CardType.LOCATION); }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.PLANET); add(Icon.A_NEW_HOPE); add(Icon.EPISODE_I); add(Icon.LEGACY_BLOCK_5);
            add(Icon.LIGHT_FORCE); add(Icon.DARK_FORCE);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        assertEquals(2, card.getIconCount(Icon.LIGHT_FORCE));
        assertEquals(1, card.getIconCount(Icon.DARK_FORCE));
    }

    @Test
    public void KashyyykStartingLocationDownloadsOnlyItsSitesAndReshuffles() {
        var scn = scenario(true);
        var site = scn.GetLSCard("forest");
        scn.StartGame();
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSReserveDeck(site, scn.GetLSCard("haven"), scn.GetLSCard("otherSite"), scn.GetLSCard("interrupt"));
        int reserve = scn.GetLSReserveDeckCount();
        scn.gameState().markCardsRevealedFromPile(java.util.List.of(scn.GetLSCard("interrupt")));
        assertTrue(scn.gameState().isCardRevealedFromPile(scn.GetLSCard("interrupt")));
        scn.LSUseCardAction(scn.GetLSStartingLocation(), "Deploy a Kashyyyk site");
        assertTrue(selectable(scn, site));
        assertTrue(selectable(scn, scn.GetLSCard("haven")));
        assertFalse(selectable(scn, scn.GetLSCard("otherSite")));
        assertFalse(selectable(scn, scn.GetLSCard("interrupt")));
        scn.LSChooseCard(site);
        scn.PassAllResponses();
        assertEquals(Zone.LOCATIONS, site.getZone());
        assertEquals("Kashyyyk", site.getBlueprint().getSystemName());
        assertEquals(reserve - 1, scn.GetLSReserveDeckCount());
        assertFalse(scn.gameState().isCardRevealedFromPile(scn.GetLSCard("interrupt")));
        assertFalse(scn.GetLSHand().contains(site));
    }

    @Test
    public void KashyyykDownloadIsOncePerTurnAndReturnsOnNextTurn() {
        var scn = scenario(true);
        scn.StartGame();
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSReserveDeck(scn.GetLSCard("forest"), scn.GetLSCard("haven"));
        scn.LSUseCardAction(scn.GetLSStartingLocation(), "Deploy a Kashyyyk site");
        scn.LSChooseCard(scn.GetLSCard("forest"));
        scn.PassAllResponses();
        scn.DSPass();
        assertFalse(scn.LSCardActionAvailable(scn.GetLSStartingLocation(), "Deploy a Kashyyyk site"));
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSReserveDeck(scn.GetLSCard("haven"));
        assertTrue(scn.LSCardActionAvailable(scn.GetLSStartingLocation(), "Deploy a Kashyyyk site"));
        scn.LSUseCardAction(scn.GetLSStartingLocation(), "Deploy a Kashyyyk site");
        scn.LSChooseCard(scn.GetLSCard("haven"));
        assertTrue(scn.LSDecisionAvailable("On which side"));
        scn.LSChooseOption("Right");
        scn.PassAllResponses();
        assertEquals(Zone.LOCATIONS, scn.GetLSCard("haven").getZone());
    }

    @Test
    public void KashyyykNonStartingLocationCannotDownloadEvenWithSiteInReserve() {
        var scn = scenario(false);
        var system = scn.GetLSCard("kashyyyk");
        scn.StartGame();
        scn.MoveLocationToTable(system);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSReserveDeck(scn.GetLSCard("forest"));
        assertFalse(system.isStartingLocation());
        assertNotNull(scn.GetADParam(scn.LS, "actionText"));
        assertFalse(scn.LSCardActionAvailable(system, "Deploy a Kashyyyk site"));
        assertTrue(scn.GetLSReserveDeck().contains(scn.GetLSCard("forest")));
    }

    @Test
    public void KashyyykDarkSideCannotUseLightSideDownload() {
        var scn = scenario(true);
        scn.StartGame();
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertNotNull(scn.GetADParam(scn.DS, "actionText"));
        assertFalse(scn.DSCardActionAvailable(scn.GetLSStartingLocation(), "Deploy a Kashyyyk site"));
    }

    @Test
    public void KashyyykCanBeConvertedUnlikeOriginalVirtualPrinting() {
        var scn = scenario(true);
        var dark = scn.GetDSCard("darkKashyyyk");
        scn.StartGame();
        scn.MoveCardsToHand(dark);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSCardPlayAvailable(dark));
        scn.DSDeployCard(dark);
        scn.PassAllResponses();
        assertTrue(scn.gameState().getTopLocations().contains(dark));
        assertFalse(scn.gameState().getTopLocations().contains(scn.GetLSStartingLocation()));
    }

    @Test
    public void KashyyykDarkAbilityFiveCannotDrawBattleDestinyHere() { checkBattle(5, true, 0); }

    @Test
    public void KashyyykDarkAbilityExactlySixDrawsBattleDestinyHere() { checkBattle(6, true, 4); }

    @Test
    public void KashyyykDarkAbilityFiveStillDrawsAtOtherSystem() { checkBattle(5, false, 4); }

    @Test
    public void KashyyykDarkAbilitySevenAlsoDrawsBattleDestinyHere() { checkBattle(7, true, 4); }

    private void checkBattle(int darkAbility, boolean atKashyyyk, int expectedDestiny) {
        var scn = scenario(true);
        scn.StartGame();
        var system = scn.GetLSStartingLocation();
        if (!atKashyyyk) {
            system = scn.GetLSCard("tatooine");
            scn.MoveLocationToTable(system);
        }
        var tantive = scn.GetLSCard("tantive");
        var destroyer = scn.GetDSCard("destroyer");
        scn.MoveCardsToLocation(system, tantive, destroyer);
        scn.BoardAsPilot(tantive, scn.GetLSCard("biggs"));
        if (darkAbility == 5) scn.BoardAsPilot(destroyer, scn.GetDSCard("motti"));
        else if (darkAbility == 7) {
            scn.BoardAsPilot(destroyer, scn.GetDSCard("motti"));
            scn.BoardAsPilot(destroyer, scn.GetDSCard("pilot1"));
        }
        else {
            scn.BoardAsPilot(destroyer, scn.GetDSCard("pilot1"));
            scn.BoardAsPilot(destroyer, scn.GetDSCard("pilot2"));
        }
        scn.DSActivateForceCheat(3);
        scn.DSStartBattleAndSkipToWeaponsSegment(system);
        assertEquals(darkAbility, scn.GetDSAbilityAtLocation(system));
        assertEquals(4, scn.GetLSAbilityAtLocation(system));
        scn.PrepareDSDestiny(4);
        scn.PrepareLSDestiny(3);
        scn.SkipToEndOfPowerSegment(true);
        assertEquals(expectedDestiny, scn.GetDSTotalDestiny());
        assertEquals(3, scn.GetLSTotalDestiny());
    }
}
