package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class Card_1261_078_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(
            new HashMap<>(Map.of("shield", "1261_78", "obi", "1_21", "luke", "1_19")),
            new HashMap<>(Map.of("vader", "1_168", "event", "5_129", "circle", "1_271",
                "feeling", "4_116", "command", "3_109")), 40, 40,
            StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
            StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
            StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test public void HeCanGoAboutHisBusinessPrintedStatsIconsAndKeywordsAreCorrect() {
        var s = scenario();
        var b = s.GetLSCard("shield").getBlueprint();
        assertEquals("He Can Go About His Business", b.getTitle());
        assertEquals(Side.LIGHT, b.getSide());
        assertEquals(0, b.getDestiny(), s.epsilon);
        assertEquals(Uniqueness.UNIQUE, b.getUniqueness());
        assertTrue(b.hasVirtualSuffix());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, b.getExpansionSet());
        s.BlueprintCardTypeCheck(b, List.of(CardType.DEFENSIVE_SHIELD));
        s.BlueprintIconCheck(b, List.of(Icon.DEFENSIVE_SHIELD, Icon.REFLECTIONS_III, Icon.LEGACY_BLOCK_6));
        s.BlueprintKeywordCheck(b, List.of());
    }

    private VirtualTableScenario epicDuel(boolean shield, boolean vaderWins) {
        var s = scenario();
        s.StartGame();
        var vader = s.GetDSCard("vader");
        var obi = s.GetLSCard("obi");
        var circle = s.GetDSCard("circle");
        s.MoveCardsToLocation(s.GetDSStartingLocation(), vader, obi);
        s.MoveCardsToDSHand(circle);
        s.MoveCardsToDSSideOfTable(s.GetDSCard("event"));
        if (shield) s.MoveCardsToLSSideOfTable(s.GetLSCard("shield"));
        s.SkipToDSTurn(Phase.CONTROL);
        s.EnsureDSForcePile(4);
        s.MoveCardsToTopOfDSReserveDeck(s.GetDSDestiny(vaderWins ? 6 : 0), s.GetDSDestiny(vaderWins ? 5 : 1));
        s.MoveCardsToTopOfLSReserveDeck(s.GetLSDestiny(vaderWins ? 0 : 6), s.GetLSDestiny(vaderWins ? 1 : 5));
        assertTrue(s.DSCardPlayAvailable(circle, "Initiate epic duel"));
        s.DSPlayCard(circle, "Initiate epic duel");
        if (s.DSHasCardChoiceAvailable(vader)) s.DSChooseCard(vader);
        if (s.DSHasCardChoiceAvailable(obi)) s.DSChooseCard(obi);
        s.PassAllResponses();
        s.PassDestinyDrawResponses();
        s.PassAllResponses();
        return s;
    }

    @Test public void HeCanGoAboutHisBusinessMakesObiWanLostAndPreventsLightForceLossFromAnActualEpicDuel() {
        var s = epicDuel(true, true);
        assertTrue(s.GetLSLostPile().contains(s.GetLSCard("obi")));
        assertFalse(s.AwaitingLSForceLossPayment());
        assertEquals(1, s.GetLSLostPileCount());
        assertEquals(Zone.AT_LOCATION, s.GetDSCard("vader").getZone());
    }

    @Test public void HeCanGoAboutHisBusinessMakesVaderLostAndPreventsDarkForceLossFromAnActualEpicDuel() {
        var s = epicDuel(true, false);
        assertTrue(s.GetDSLostPile().contains(s.GetDSCard("vader")));
        assertFalse(s.AwaitingDSForceLossPayment());
        assertEquals(Zone.AT_LOCATION, s.GetLSCard("obi").getZone());
    }

    @Test public void HeCanGoAboutHisBusinessDoesNotChangeAnEpicDuelWhenItIsAbsent() {
        var s = epicDuel(false, true);
        assertEquals(Zone.OUT_OF_PLAY, s.GetLSCard("obi").getZone());
        assertTrue(s.AwaitingLSForceLossPayment());
    }

    @Test public void HeCanGoAboutHisBusinessCancelsActualBadFeelingHaveIDeployment() {
        var s = scenario();
        s.StartGame();
        var feeling = s.GetDSCard("feeling");
        s.MoveCardsToLSSideOfTable(s.GetLSCard("shield"));
        s.MoveCardsToDSHand(feeling);
        s.SkipToDSTurn(Phase.DEPLOY);
        s.DSDeployCard(feeling);
        s.PassAllResponses();
        assertTrue(s.GetDSLostPile().contains(feeling));
    }
}
