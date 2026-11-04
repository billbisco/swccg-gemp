package com.gempukku.swccgo.cards.set1291.dark;

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
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1291_014_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("eigap", "1291_14");
                    put("vader", "1_168");
                    put("droideka", "200_80"); // [Separatist] card, destiny 2
                    put("trooper", "1_194"); // not [Separatist], destiny 1
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

    protected VirtualTableScenario GetStartingScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("eigap", "1291_14");
                    put("racks", "14_96"); // Droid Racks
                    put("fanfare", "11_69"); // deploys on table, always Immune to Alter
                    put("anakin", "11_70"); // His Name Is Anakin: deploys on table, always Immune to Alter
                }},
                10, 10,
                StartingSetup.DefaultLSGroundLocation,
                Card_1291_029_Tests.SeparatistUprisingObjective,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void EverythingIsGoingAsPlannedStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("eigap").getBlueprint();
        assertEquals("Everything Is Going As Planned", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.USED_OR_STARTING, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.INTERRUPT);
            add(Icon.LEGACY_BLOCK_9);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_9_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    /**
     * Vader battles Luke and Dark draws one battle destiny (the given card); the responses pass until Dark's response
     * window to that draw.
     */
    private void darkDrawsBattleDestiny(VirtualTableScenario scn, String destinyCard) {
        darkDrawsBattleDestiny(scn, destinyCard, true);
    }

    private void darkDrawsBattleDestiny(VirtualTableScenario scn, String destinyCard, boolean darkHasResponse) {
        var eigap = scn.GetDSCard("eigap");
        var vader = scn.GetDSCard("vader");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, vader, luke);
        scn.MoveCardsToHand(eigap);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();
        scn.SkipToPowerSegment();
        scn.MoveCardsToTopOfOwnReserveDeck(scn.GetDSCard(destinyCard));
        assertTrue(scn.DSDecisionAvailable("Do you want to draw 1 battle destiny?"));
        scn.DSChooseYes();
        scn.PassResponses("COST_TO_DRAW_DESTINY_CARD");
        scn.PassResponses("ABOUT_TO_DRAW_DESTINY_CARD");
        assertTrue(scn.LSDecisionAvailable("DESTINY_DRAWN"));
        scn.LSPass();
        assertEquals(darkHasResponse, scn.DSDecisionAvailable("Just drew"));
    }

    @Test
    public void EverythingIsGoingAsPlannedUsedTakesJustDrawnSeparatistDestinyCardIntoHand() {
        var scn = GetScenario();
        var eigap = scn.GetDSCard("eigap");
        var droideka = scn.GetDSCard("droideka");
        darkDrawsBattleDestiny(scn, "droideka");
        assertTrue(scn.DSCardPlayAvailable(eigap, "Take destiny card into hand"));
        scn.DSPlayCard(eigap, "Take destiny card into hand");
        scn.PassAllResponses();
        assertInZone(Zone.HAND, droideka);
        assertInZone(Zone.USED_PILE, eigap);
    }

    @Test
    public void EverythingIsGoingAsPlannedUsedMayAlsoCancelAndRedrawThatDestiny() {
        var scn = GetScenario();
        var eigap = scn.GetDSCard("eigap");
        var droideka = scn.GetDSCard("droideka");
        darkDrawsBattleDestiny(scn, "droideka");
        // the redraw is a destiny 5
        scn.PrepareDSDestiny(5);
        assertTrue(scn.DSCardPlayAvailable(eigap, "cancel and redraw"));
        scn.DSPlayCard(eigap, "cancel and redraw");
        scn.PassAllResponses();
        assertInZone(Zone.HAND, droideka);
        // redrawn destiny 5, +1 from Vader in battle (the canceled Droideka would total 3)
        assertEquals(6, scn.GetDSTotalDestiny());
    }

    @Test
    public void EverythingIsGoingAsPlannedUsedNotOfferedForNonSeparatistDestinyCard() {
        var scn = GetScenario();
        var eigap = scn.GetDSCard("eigap");
        // Dark gets no response window to the draw because nothing (Everything Is Going As Planned included) responds
        darkDrawsBattleDestiny(scn, "trooper", false);
        assertFalse(scn.DSDecisionAvailable("Just drew"));
    }

    @Test
    public void EverythingIsGoingAsPlannedStartingDeploysDroidRacksAndTwoAlwaysImmuneEffects() {
        var scn = GetStartingScenario();
        var eigap = scn.GetDSCard("eigap");
        var racks = scn.GetDSCard("racks");
        var fanfare = scn.GetDSCard("fanfare");
        var anakin = scn.GetDSCard("anakin");
        scn.StartGame();
        assertTrue(scn.DSDecisionAvailable("Choose starting interrupt"));
        scn.DSChooseCard(eigap);
        scn.PassAllResponses();
        // Droid Racks is the only Droid Racks in Reserve Deck, so it deploys without a choice; the two Effects
        // are chosen one at a time (the second is then the only one left)
        assertInZone(Zone.SIDE_OF_TABLE, racks);
        assertTrue(scn.DSDecisionAvailable("Choose card to deploy from Reserve Deck"));
        assertTrue(scn.DSHasCardChoiceAvailable(anakin));
        scn.DSChooseCard(fanfare);
        scn.PassAllResponses();
        assertInZone(Zone.SIDE_OF_TABLE, racks);
        assertInZone(Zone.SIDE_OF_TABLE, fanfare);
        assertInZone(Zone.SIDE_OF_TABLE, anakin);
        assertTrue(eigap.getZone() == Zone.LOST_PILE || eigap.getZone() == Zone.TOP_OF_LOST_PILE);
    }

    @Test
    public void EverythingIsGoingAsPlannedStartingNotOfferedWithoutSeparatistUprising() {
        var scn = GetScenario();
        var eigap = scn.GetDSCard("eigap");
        scn.StartGame();
        assertFalse(scn.DSDecisionAvailable("Choose starting interrupt") && scn.DSHasCardChoiceAvailable(eigap));
    }
}
