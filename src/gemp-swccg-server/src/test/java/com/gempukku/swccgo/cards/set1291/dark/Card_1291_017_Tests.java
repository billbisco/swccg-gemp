package com.gempukku.swccgo.cards.set1291.dark;

import com.gempukku.swccgo.cards.legacy.MissionTestSupport;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1291_017_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("leia", "1_17");
                }},
                new HashMap<>() {{
                    put("petranaki", "1291_17");
                    put("pcpetranaki", "227_10");
                    put("arena", "1291_26");
                    put("dooku", "200_76");
                    put("jango", "201_25");
                    put("vader", "1_168");
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
    public void GeonosisPetranakiArenaStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("petranaki").getBlueprint();

        assertEquals("Geonosis: Petranaki Arena", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.LOCATION);
        }});
        assertEquals(CardSubtype.SITE, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EPISODE_I);
            add(Icon.PLANET);
            add(Icon.EXTERIOR_SITE);
            add(Icon.DARK_FORCE);
            add(Icon.LIGHT_FORCE);
            add(Icon.LEGACY_BLOCK_9);
        }});
        assertEquals(2, card.getIconCount(Icon.DARK_FORCE));
        assertEquals(1, card.getIconCount(Icon.LIGHT_FORCE));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_9_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void GeonosisPetranakiArenaDookuAndJangoArePowerPlus1Here() {
        var scn = GetScenario();
        var petranaki = scn.GetDSCard("petranaki");
        var dooku = scn.GetDSCard("dooku");
        var jango = scn.GetDSCard("jango");
        var vader = scn.GetDSCard("vader");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(petranaki);
        scn.MoveCardsToLocation(site, dooku, jango, vader);
        int dookuOff = scn.GetPower(dooku);
        int jangoOff = scn.GetPower(jango);
        int vaderOff = scn.GetPower(vader);

        scn.MoveCardsToLocation(petranaki, dooku, jango, vader);
        assertEquals(dookuOff + 1, scn.GetPower(dooku));
        assertEquals(jangoOff + 1, scn.GetPower(jango));
        assertEquals(vaderOff, scn.GetPower(vader));
    }

    /**
     * Arena Execution (1291_26) makes Petranaki Arena a prison and lets the opponent release captives there during
     * their control phase. Imprisons Leia at the given arena, puts Luke there so LS controls it, and walks LS up to
     * the point of having chosen Leia as the captive to release.
     */
    private void imprisonLeiaAndStartRelease(VirtualTableScenario scn, PhysicalCardImpl arena) {
        var mission = scn.GetDSCard("arena");
        var luke = scn.GetLSCard("luke");
        var leia = scn.GetLSCard("leia");
        var host = MissionTestSupport.putOnTable(scn, 9301, scn.DS, MissionTestSupport.warHasBegun());
        scn.AttachCardsTo(host, mission);
        scn.MoveLocationToTable(arena);
        assertTrue(scn.HasKeyword(arena, Keyword.PRISON));
        scn.gameState().imprisonCharacter(scn.game(), leia, arena);
        assertTrue(leia.isImprisoned());
        scn.MoveCardsToLocation(arena, luke);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.AwaitingLSControlPhaseActions());
        assertTrue(scn.LSActionAvailable("Release any number of captives"));
        scn.LSUseCardAction(mission, "Release any number of captives");
        assertTrue(scn.LSHasCardChoiceAvailable(leia));
        scn.LSChooseCard(leia);
        scn.PassAllResponses();
    }

    @Test
    public void GeonosisPetranakiArenaImprisonedCaptiveReleasedHereMayEscape() {
        var scn = GetScenario();
        var petranaki = scn.GetDSCard("petranaki");
        var leia = scn.GetLSCard("leia");

        scn.StartGame();
        imprisonLeiaAndStartRelease(scn, petranaki);

        assertTrue(scn.LSReleaseDecisionAvailable());
        assertTrue(scn.LSChoiceAvailable("Escape"));
        assertTrue(scn.LSChoiceAvailable("Rally"));
        scn.LSChooseEscape();
        scn.PassAllResponses();

        assertFalse(leia.isCaptive());
        assertFalse(leia.isImprisoned());
        assertInZone(Zone.USED_PILE, leia);
    }

    @Test
    public void GeonosisPetranakiArenaImprisonedCaptiveReleasedHereMayStillRally() {
        var scn = GetScenario();
        var petranaki = scn.GetDSCard("petranaki");
        var leia = scn.GetLSCard("leia");

        scn.StartGame();
        imprisonLeiaAndStartRelease(scn, petranaki);

        assertTrue(scn.LSReleaseDecisionAvailable());
        assertTrue(scn.LSChoiceAvailable("Rally"));
        scn.LSChooseRally();
        scn.PassAllResponses();

        assertFalse(leia.isCaptive());
        assertFalse(leia.isImprisoned());
        assertAtLocation(petranaki, leia);
    }

    @Test
    public void ImprisonedCaptiveReleasedAtPremiereCloseUpPetranakiArenaMustRally() {
        var scn = GetScenario();
        var pcPetranaki = scn.GetDSCard("pcpetranaki");
        var leia = scn.GetLSCard("leia");

        scn.StartGame();
        imprisonLeiaAndStartRelease(scn, pcPetranaki);

        // No "Imprisoned captives released here may Escape" text: the captive rallies with no release option offered
        assertFalse(scn.LSReleaseDecisionAvailable());
        assertFalse(leia.isCaptive());
        assertFalse(leia.isImprisoned());
        assertAtLocation(pcPetranaki, leia);
    }

    /**
     * Puts a test-only Light side Petranaki Arena on table, then has DS deploy 1291_17 from hand on top of it
     * (converting it). Returns the LS copy, which is now underneath.
     */
    private PhysicalCardImpl convertLightPetranakiWithDarkCopy(VirtualTableScenario scn) {
        var petranaki = scn.GetDSCard("petranaki");
        var lsPetranaki = MissionTestSupport.putLocationOnTable(scn, 9317, scn.LS, MissionTestSupport.geonosisPetranakiArena(Side.LIGHT));
        assertEquals(Zone.LOCATIONS, lsPetranaki.getZone());
        scn.MoveCardsToHand(petranaki);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSDeployAvailable(petranaki));
        scn.DSDeployCard(petranaki);
        scn.PassAllResponses();
        assertEquals(Zone.LOCATIONS, petranaki.getZone());
        assertEquals(Zone.CONVERTED_LOCATIONS, lsPetranaki.getZone());
        return lsPetranaki;
    }

    @Test
    public void GeonosisPetranakiArenaLightSideMayRaiseConvertedPetranakiArenaWhenControlling() {
        var scn = GetScenario();
        var petranaki = scn.GetDSCard("petranaki");
        var luke = scn.GetLSCard("luke");

        scn.StartGame();
        var lsPetranaki = convertLightPetranakiWithDarkCopy(scn);
        scn.MoveCardsToLocation(petranaki, luke);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.AwaitingLSControlPhaseActions());

        assertTrue(scn.LSActionAvailable("Raise converted Petranaki Arena"));
        scn.LSUseCardAction(petranaki, "Raise converted Petranaki Arena");
        assertTrue(scn.LSHasCardChoiceAvailable(petranaki));
        scn.LSChooseCard(petranaki);
        scn.PassAllResponses();

        assertEquals(Zone.LOCATIONS, lsPetranaki.getZone());
        assertEquals(Zone.CONVERTED_LOCATIONS, petranaki.getZone());
    }

    @Test
    public void GeonosisPetranakiArenaLightSideMayNotRaiseConvertedPetranakiArenaWhenNotControlling() {
        var scn = GetScenario();
        var petranaki = scn.GetDSCard("petranaki");
        var luke = scn.GetLSCard("luke");
        var vader = scn.GetDSCard("vader");

        scn.StartGame();
        var lsPetranaki = convertLightPetranakiWithDarkCopy(scn);
        scn.MoveCardsToLocation(petranaki, luke);
        scn.MoveCardsToLocation(petranaki, vader);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.AwaitingLSControlPhaseActions());

        assertFalse(scn.LSActionAvailable("Raise converted Petranaki Arena"));
        assertEquals(Zone.CONVERTED_LOCATIONS, lsPetranaki.getZone());
    }

    @Test
    public void GeonosisPetranakiArenaLightSideRaiseConvertedPetranakiArenaIsOncePerTurn() {
        var scn = GetScenario();
        var petranaki = scn.GetDSCard("petranaki");
        var luke = scn.GetLSCard("luke");

        scn.StartGame();
        var lsPetranaki = convertLightPetranakiWithDarkCopy(scn);
        scn.MoveCardsToLocation(petranaki, luke);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.AwaitingLSControlPhaseActions());
        assertTrue(scn.LSActionAvailable("Raise converted Petranaki Arena"));
        scn.LSUseCardAction(petranaki, "Raise converted Petranaki Arena");
        assertTrue(scn.LSHasCardChoiceAvailable(petranaki));
        scn.LSChooseCard(petranaki);
        scn.PassAllResponses();
        assertEquals(Zone.LOCATIONS, lsPetranaki.getZone());
        assertEquals(Zone.CONVERTED_LOCATIONS, petranaki.getZone());

        // Still LS's control phase: put the Dark copy back on top (converting the Light copy again) so the
        // Light side text has a valid target once more. Only the once-per-turn limit should now stop it.
        assertTrue(scn.AwaitingDSControlPhaseActions());
        scn.MoveLocationToTable(petranaki);
        assertEquals(Zone.LOCATIONS, petranaki.getZone());
        assertEquals(Zone.CONVERTED_LOCATIONS, lsPetranaki.getZone());
        scn.MoveCardsToLocation(petranaki, luke);
        assertTrue(scn.CardsAtLocation(petranaki, luke));
        scn.DSPass();
        scn.PassAllResponses();
        assertTrue(scn.AwaitingLSControlPhaseActions());
        assertTrue(scn.LSCardActionAvailable(petranaki, "Force drain"));
        assertFalse(scn.LSActionAvailable("Raise converted Petranaki Arena"));

        // Next LS turn it is available again
        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.AwaitingLSControlPhaseActions());
        assertTrue(scn.LSActionAvailable("Raise converted Petranaki Arena"));
    }
}
