package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.*;

public class Card_1261_080_Tests {
    private static final String PLAY_SHIELD = "Play a Defensive Shield";

    private VirtualTableScenario scenario(boolean includeEffect, boolean outsideCards) {
        var light = new HashMap<String, String>();
        if (includeEffect) light.put("jabba", "1261_80");
        light.put("celebration", "7_55"); light.put("bespin", "5_76"); light.put("plaza", "7_113");
        light.put("obi", "1_21"); light.put("han", "1_11");
        var shields = new StartingSetup() {
            @Override public HashMap<String, String> Cards() {
                var cards = new HashMap<String, String>();
                cards.put("a", "13_1"); cards.put("b", "13_3"); cards.put("c", "13_4");
                cards.put("d", "13_6"); cards.put("ordinary", "1_66");
                return cards;
            }
            @Override public void Setup(VirtualTableScenario scn) { }
        };
        return new VirtualTableScenario(light, new HashMap<>(), 60, 60,
                StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSSpaceSystem,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                outsideCards ? shields : StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test public void JabbaUsesFinalStartingEffectWithBlankLore() {
        var scn = scenario(true, false); var b = scn.GetLSCard("jabba").getBlueprint();
        assertEquals("Jabba, This Is Your Last Chance!", b.getTitle()); assertEquals(5, b.getDestiny(), scn.epsilon);
        assertNull(b.getLore()); assertFalse(b.hasVirtualSuffix()); assertEquals(Uniqueness.UNIQUE, b.getUniqueness());
        assertEquals(Side.LIGHT, b.getSide()); assertEquals(Rarity.V, b.getRarity());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, b.getExpansionSet()); assertEquals(CardSubtype.STARTING, b.getCardSubtype());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(List.of(CardType.EFFECT)));
        scn.BlueprintIconCheck(b, new ArrayList<>(List.of(Icon.EFFECT, Icon.JABBAS_PALACE, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>());
    }

    @Test public void JabbaActuallyStartsWithFiveOutsideCardsFaceDownIncludingANonShield() {
        var scn = scenario(true, true); scn.StartGame(); var effect = scn.GetLSCard("jabba");
        assertInZone(Zone.SIDE_OF_TABLE, effect);
        var stacked = scn.gameState().getStackedCards(effect);
        assertEquals(5, stacked.size());
        for (var key : List.of("a", "b", "c", "d", "ordinary")) {
            assertTrue(stacked.contains(scn.GetLSCard(key)));
            assertInZone(Zone.STACKED_FACE_DOWN, scn.GetLSCard(key));
        }
    }

    private void playShield(VirtualTableScenario scn, String key) {
        var effect = scn.GetLSCard("jabba"); var shield = scn.GetLSCard(key);
        scn.LSUseCardAction(effect, PLAY_SHIELD);
        assertTrue(scn.LSHasCardChoiceAvailable(shield));
        assertFalse(scn.LSHasCardChoiceAvailable(scn.GetLSCard("ordinary")));
        scn.LSChooseCard(shield); scn.PassAllResponses();
        assertInZone(Zone.SIDE_OF_TABLE, shield);
    }

    @Test public void JabbaActuallyPlaysTwoShieldsAndCannotPlayAThirdNextTurn() {
        var scn = scenario(true, true); scn.StartGame(); scn.SkipToLSTurn(Phase.CONTROL);
        playShield(scn, "a");
        if (scn.GetAwaitingDecision(scn.DS) != null) scn.DSPass();
        playShield(scn, "b");
        scn.SkipToLSTurn(Phase.CONTROL);
        assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("jabba"), PLAY_SHIELD));
        assertInZone(Zone.STACKED_FACE_DOWN, scn.GetLSCard("c"), scn.GetLSCard("d"), scn.GetLSCard("ordinary"));
    }

    private VirtualTableScenario celebration(boolean includeEffect) {
        var scn = scenario(includeEffect, false); scn.StartGame();
        var chasm = scn.GetLSStartingLocation(); var plaza = scn.GetLSCard("plaza");
        if (chasm.getZone() != Zone.LOCATIONS) scn.MoveLocationToTable(chasm);
        scn.MoveLocationToTable(plaza); scn.MoveLocationToTable(scn.GetLSCard("bespin"));
        scn.MoveCardsToLocation(chasm, scn.GetLSCard("han")); scn.MoveCardsToLocation(plaza, scn.GetLSCard("obi"));
        scn.MoveCardsToLSHand(scn.GetLSCard("celebration")); scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSCardPlayAvailable(scn.GetLSCard("celebration")));
        scn.LSPlayCard(scn.GetLSCard("celebration"));
        if (scn.LSHasCardChoiceAvailable(scn.GetLSCard("bespin"))) scn.LSChooseCard(scn.GetLSCard("bespin"));
        scn.PassAllResponses();
        return scn;
    }

    @Test public void JabbaCancelsActualCloudCityCelebrationWithoutEitherObjectiveFace() {
        var scn = celebration(true); assertInZone(Zone.LOST_PILE, scn.GetLSCard("celebration"));
        var ordinary = celebration(false);
        assertEquals(ordinary.GetLSCard("bespin"), ordinary.GetLSCard("celebration").getAttachedTo());
    }
}
