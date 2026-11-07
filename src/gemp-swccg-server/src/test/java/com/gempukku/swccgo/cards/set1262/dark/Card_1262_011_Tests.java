package com.gempukku.swccgo.cards.set1262.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.decisions.CardActionSelectionDecision;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.*;

public class Card_1262_011_Tests {
    private static final String TAKE = "Take Rise, My Friend into hand from Reserve Deck";

    private VirtualTableScenario scenario() {
        var scn = new VirtualTableScenario(
                new HashMap<>(Map.of("luke", "601_52", "ben", "1_21", "alter", "1_71", "escape", "11_36")),
                new HashMap<>(Map.of("effect", "1262_11", "rise", "9_140", "vader", "1_168", "emperor", "9_109",
                        "trooper", "1_194")),
                30, 30, StartingSetup.DefaultLSGroundLocation, StartingSetup.BHBMObjective,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
        scn.StartGame();
        return scn;
    }

    @Test public void SoBeItJediPrintedMetadataMatches() {
        var scn = scenario();
        var b = scn.GetDSCard("effect").getBlueprint();
        assertEquals("So Be It, Jedi", b.getTitle());
        assertEquals("\"Now, young Skywalker ... you will die.\"", b.getLore());
        assertEquals(5, b.getDestiny(), scn.epsilon);
        assertEquals(Side.DARK, b.getSide());
        assertEquals(Uniqueness.UNIQUE, b.getUniqueness());
        assertEquals(CardSubtype.NORMAL, b.getCardSubtype());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_2, b.getExpansionSet());
        assertEquals(Rarity.V, b.getRarity());
        assertFalse(b.hasVirtualSuffix());
        assertTrue(b.isLegacy());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(List.of(CardType.EFFECT)));
        scn.BlueprintIconCheck(b, new ArrayList<>(List.of(Icon.EFFECT, Icon.DEATH_STAR_II, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>());
    }

    @Test public void SoBeItJediActuallyDeploysAndTakesRiseMyFriendOnlyOncePerGame() {
        var scn = scenario();
        var effect = scn.GetDSCard("effect");
        var rise = scn.GetDSCard("rise");
        var trooper = scn.GetDSCard("trooper");
        scn.MoveCardsToDSHand(effect);
        scn.SkipToPhase(Phase.DEPLOY);
        scn.DSDeployCard(effect);
        scn.PassAllResponses();
        assertInZone(Zone.SIDE_OF_TABLE, effect);
        scn.MoveCardsToTopOfDSReserveDeck(rise, trooper);
        var remaining = new ArrayList<>(scn.GetDSReserveDeck());
        remaining.remove(rise);
        scn.gameState().getRandom().setSeed(4096);
        scn.DSUseCardAction(effect, TAKE);
        int index = scn.DSGetBPChoices().indexOf(trooper.getBlueprintId(true));
        assertTrue(index >= 0);
        assertEquals("false", scn.GetADParam(scn.DS, "selectable")[index]);
        scn.DSChooseCard(rise);
        scn.PassAllResponses();
        assertInZone(Zone.HAND, rise);
        assertEquals(new HashSet<>(remaining), new HashSet<>(scn.GetDSReserveDeck()));
        assertNotEquals(remaining, scn.GetDSReserveDeck());
        scn.MoveCardsToTopOfDSReserveDeck(rise);
        scn.SkipToDSTurn(Phase.CONTROL);
        assertFalse(scn.DSCardActionAvailable(effect, TAKE));
    }

    @Test public void SoBeItJediActuallyStacksAfterOpponentsAlterOnlyIfRebellionIsOnTable() {
        for (boolean rebellionOnTable : List.of(false, true)) {
            var scn = scenario();
            var effect = scn.GetDSCard("effect");
            var rebellion = scn.GetDSCard("rebellion");
            var alter = scn.GetLSCard("alter");
            var ben = scn.GetLSCard("ben");
            scn.MoveCardsToDSSideOfTable(effect);
            if (!rebellionOnTable) scn.MoveCardsToDSHand(rebellion);
            scn.MoveCardsToLSHand(alter);
            scn.MoveCardsToLocation(scn.GetLSStartingLocation(), ben);
            scn.SkipToLSTurn(Phase.CONTROL);
            scn.PrepareLSDestiny(1);
            scn.LSPlayCard(alter, "cancel Effect");
            assertTrue(scn.LSHasCardChoiceAvailable(effect));
            scn.LSChooseCard(effect);
            assertTrue(scn.LSHasCardChoiceAvailable(ben));
            scn.LSChooseCard(ben);
            scn.PassAllResponses();
            assertInZone(rebellionOnTable ? Zone.STACKED : Zone.LOST_PILE, effect);
            if (rebellionOnTable) assertSame(rebellion, effect.getStackedOn());
        }
    }

    @Test public void SoBeItJediBlocksJediEscapeAgainstAnActualObjectiveDuel() {
        for (boolean effectOnTable : List.of(false, true)) {
            var scn = scenario();
            var objective = scn.GetDSCard("bhbm");
            var throne = scn.GetDSCard("throne");
            var luke = scn.GetLSCard("luke");
            var escape = scn.GetLSCard("escape");
            // Stage the back-side state with non-captive Luke, so Jedi Escape would otherwise be legal.
            scn.gameState().flipCard(scn.game(), objective, true);
            scn.MoveCardsToLocation(throne, scn.GetDSCard("vader"), scn.GetDSCard("emperor"), luke);
            scn.MoveCardsToLSHand(escape);
            scn.EnsureLSForcePile(2);
            if (effectOnTable) scn.MoveCardsToDSSideOfTable(scn.GetDSCard("effect"));
            scn.SkipToPhase(Phase.CONTROL);
            assertTrue(objective.isFlipped());
            assertFalse(luke.isCaptive());
            scn.DSChooseAction("Initiate a Luke/Vader duel");
            for (int i = 0; i < 25 && !scn.LSDecisionAvailable("Duel just initiated"); i++) {
                if (scn.DSGetDecision() instanceof CardActionSelectionDecision) scn.DSPass();
                else if (scn.LSGetDecision() instanceof CardActionSelectionDecision) scn.LSPass();
                else fail("Unexpected Objective duel decision: " + scn.GetCurrentDecision().getText());
            }
            assertTrue(scn.LSDecisionAvailable("Duel just initiated"));
            assertSame(objective, scn.gameState().getDuelState().getCardInitiatedDuel());
            assertEquals(!effectOnTable, scn.LSCardPlayAvailable(escape, "Cancel duel"));
            if (!effectOnTable) {
                scn.LSPlayCard(escape, "Cancel duel");
                scn.PassAllResponses();
                assertInZone(Zone.LOST_PILE, escape);
            }
        }
    }
}
