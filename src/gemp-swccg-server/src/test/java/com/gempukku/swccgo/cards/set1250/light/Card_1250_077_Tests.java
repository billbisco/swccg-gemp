package com.gempukku.swccgo.cards.set1250.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import static org.junit.Assert.*;

public class Card_1250_077_Tests {
    private VirtualTableScenario scenario() {
        HashMap<String, String> light = new HashMap<>();
        light.put("haven", "1250_77"); light.put("forest", "1116_16");
        light.put("chewie", "8_2"); light.put("wookiee", "7_50"); light.put("rebel", "1_28");
        light.put("vehicle", "3_69"); light.put("system", "2_64");
        for (int i = 0; i < 9; i++) light.put("hand" + i, "1_28");
        return new VirtualTableScenario(light, new HashMap<>() {{ put("snoova", "10_48"); }}, 20, 20,
                StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void KashyyykWookieeHavenMetadataMatchesPrintedImage() {
        var scn = scenario();
        var bp = scn.GetLSCard("haven").getBlueprint();
        assertEquals("Kashyyyk: Wookiee Haven (Forest)", bp.getTitle());
        assertEquals("Kashyyyk", bp.getSystemName());
        assertEquals(Uniqueness.UNIQUE, bp.getUniqueness());
        assertFalse(bp.hasVirtualSuffix());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_5, bp.getExpansionSet());
        assertEquals(CardSubtype.SITE, bp.getCardSubtype());
        scn.BlueprintCardTypeCheck(bp, new ArrayList<>() {{ add(CardType.LOCATION); }});
        scn.BlueprintIconCheck(bp, new ArrayList<>() {{
            add(Icon.PLANET); add(Icon.EXTERIOR_SITE); add(Icon.ENDOR); add(Icon.EPISODE_I);
            add(Icon.LEGACY_BLOCK_5); add(Icon.LIGHT_FORCE); add(Icon.DARK_FORCE);
        }});
        scn.BlueprintKeywordCheck(bp, new ArrayList<>() {{ add(Keyword.FOREST); }});
        assertEquals(2, bp.getIconCount(Icon.LIGHT_FORCE));
        assertEquals(1, bp.getIconCount(Icon.DARK_FORCE));
    }

    @Test
    public void KashyyykWookieeHavenAddsGenerationAtEachFriendlyWookieeSiteAndStopsWhenTheyLeave() {
        var scn = scenario();
        scn.StartGame();
        var haven = scn.GetLSCard("haven");
        var forest = scn.GetLSCard("forest");
        scn.MoveLocationToTable(haven);
        scn.MoveLocationToTable(forest);
        scn.MoveCardsToLocation(haven, scn.GetLSCard("chewie"));
        scn.MoveCardsToLocation(forest, scn.GetLSCard("wookiee"));
        assertGeneration(scn, haven, scn.LS, 3);
        assertGeneration(scn, forest, scn.LS, 3);
        assertGeneration(scn, haven, scn.DS, 1);
        scn.MoveOutOfPlay(scn.GetLSCard("wookiee"));
        assertGeneration(scn, forest, scn.LS, 2);
        assertGeneration(scn, haven, scn.LS, 3);
        scn.MoveOutOfPlay(haven);
        assertGeneration(scn, forest, scn.LS, 2);
    }

    @Test
    public void KashyyykWookieeHavenCountsWookieeInsideEnclosedVehicle() {
        var scn = scenario();
        scn.StartGame();
        var haven = scn.GetLSCard("haven");
        scn.MoveLocationToTable(haven);
        scn.MoveCardsToLocation(haven, scn.GetLSCard("vehicle"));
        scn.BoardAsPassenger(scn.GetLSCard("vehicle"), scn.GetLSCard("wookiee"));
        assertGeneration(scn, haven, scn.LS, 3);
        scn.MoveOutOfPlay(scn.GetLSCard("wookiee"));
        assertGeneration(scn, haven, scn.LS, 2);
    }

    @Test
    public void KashyyykWookieeHavenDoesNotCountEnemyWookieeNonWookieeOrOtherPlanet() {
        var scn = scenario();
        scn.StartGame();
        var haven = scn.GetLSCard("haven");
        scn.MoveLocationToTable(haven);
        scn.MoveCardsToLocation(haven, scn.GetDSCard("snoova"), scn.GetLSCard("rebel"));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("chewie"));
        assertGeneration(scn, haven, scn.LS, 2);
        assertGeneration(scn, scn.GetLSStartingLocation(), scn.LS, 1);
    }

    @Test
    public void KashyyykWookieeHavenIncreasesActualActivationByOne() {
        var scn = scenario();
        scn.StartGame();
        var haven = scn.GetLSCard("haven");
        scn.MoveLocationToTable(haven);
        scn.MoveCardsToLocation(haven, scn.GetLSCard("wookiee"));
        scn.SkipToLSTurn(Phase.ACTIVATE);
        int available = (int) scn.game().getModifiersQuerying().getTotalForceGeneration(scn.gameState(), scn.LS);
        int before = scn.GetLSForcePileCount();
        assertGeneration(scn, haven, scn.LS, 3);
        assertEquals(available, scn.LSActivateMaxForceAndPass());
        assertEquals(before + available, scn.GetLSForcePileCount());
    }

    private void assertGeneration(VirtualTableScenario scn, PhysicalCardImpl site, String player, int amount) {
        assertEquals(amount, scn.game().getModifiersQuerying().getForceGenerationFromLocation(scn.gameState(), site, player), scn.epsilon);
    }

    @Test
    public void KashyyykWookieeHavenLosesOneAtExactlyEightCardsAndTwoWookiees() { checkEnd(8, 2, false, false, true); }
    @Test
    public void KashyyykWookieeHavenLosesOneWithNineCardsAndTwoWookiees() { checkEnd(9, 2, false, false, true); }
    @Test
    public void KashyyykWookieeHavenDoesNotTriggerWithSevenCards() { checkEnd(7, 2, false, false, true); }
    @Test
    public void KashyyykWookieeHavenDoesNotCountOneWookieeAndANonWookieeAsTwo() { checkEnd(8, 1, false, false, true); }
    @Test
    public void KashyyykWookieeHavenDoesNotTriggerAtContestedSite() { checkEnd(8, 2, true, false, true); }
    @Test
    public void KashyyykWookieeHavenDoesNotCountWookieeAtAnotherSite() { checkEnd(8, 2, false, true, true); }
    @Test
    public void KashyyykWookieeHavenDoesNotTriggerAtEndOfDarkDeploy() { checkEnd(8, 2, false, false, false); }

    private void checkEnd(int hand, int wookiees, boolean contested, boolean split, boolean lightTurn) {
        var scn = scenario();
        scn.StartGame();
        var haven = scn.GetLSCard("haven");
        scn.MoveLocationToTable(haven);
        scn.MoveCardsToLocation(haven, scn.GetLSCard("chewie"), scn.GetLSCard("rebel"));
        if (wookiees == 2) scn.MoveCardsToLocation(split ? scn.GetLSStartingLocation() : haven, scn.GetLSCard("wookiee"));
        if (contested) scn.MoveCardsToLocation(haven, scn.GetDSCard("snoova"));
        if (lightTurn) scn.SkipToLSTurn(Phase.DEPLOY);
        else scn.SkipToDSTurn(Phase.DEPLOY);
        for (var card : new ArrayList<>(scn.GetLSHand())) scn.MoveCardsToTopOfLSReserveDeck((PhysicalCardImpl) card);
        for (int i = 0; i < hand; i++) scn.MoveCardsToHand(scn.GetLSCard("hand" + i));
        assertEquals(hand, scn.GetLSHandCount());
        int life = scn.GetDSLifeForceRemaining();
        if (lightTurn) { scn.LSPass(); scn.DSPass(); }
        else { scn.DSPass(); scn.LSPass(); }
        scn.PassAllResponses();
        boolean loss = lightTurn && hand >= 8 && wookiees == 2 && !contested && !split;
        if (loss) { scn.DSChooseCard(scn.GetTopOfDSForcePile()); scn.PassAllResponses(); }
        assertEquals(life - (loss ? 1 : 0), scn.GetDSLifeForceRemaining());
        assertEquals(Phase.BATTLE, scn.GetCurrentPhase());
    }
}
