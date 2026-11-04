package com.gempukku.swccgo.cards.set1291.light;

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

public class Card_1291_055_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("growing", "1291_55");
                    put("obi", "11_6"); // Obi-Wan Kenobi (ability 7)
                    put("clonepilot", "221_55"); // [Clone Army] card, destiny 3
                    put("luke", "1_19"); // not [Clone Army], destiny 2
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
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
                    put("growing", "1291_55");
                    put("cylinders", "1291_51"); // Cloning Cylinders
                    put("army", "1291_49"); // A Grand Army Of The Republic: deploys on table, always Immune to Alter
                    put("blueone", "1291_54"); // What About That Blue One?: deploys on table, always Immune to Alter
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                }},
                10, 10,
                Card_1291_069_Tests.RepublicAtWarObjective,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void TheDarkSideIsGrowingStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("growing").getBlueprint();
        assertEquals("The Dark Side Is Growing", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.USED_OR_STARTING, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.INTERRUPT);
            add(Icon.EPISODE_I);
            add(Icon.LEGACY_BLOCK_9);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_9_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    /**
     * Obi-Wan battles a Stormtrooper and Light draws one battle destiny (the given card); the responses pass until
     * Light's response window to that draw.
     */
    private void lightDrawsBattleDestiny(VirtualTableScenario scn, String destinyCard, boolean lightHasResponse) {
        var growing = scn.GetLSCard("growing");
        var obi = scn.GetLSCard("obi");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetLSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, obi, trooper);
        scn.MoveCardsToHand(growing);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();
        scn.SkipToPowerSegment();
        scn.MoveCardsToTopOfOwnReserveDeck(scn.GetLSCard(destinyCard));
        assertTrue(scn.LSDecisionAvailable("Do you want to draw 1 battle destiny?"));
        scn.LSChooseYes();
        scn.PassResponses("COST_TO_DRAW_DESTINY_CARD");
        scn.PassResponses("ABOUT_TO_DRAW_DESTINY_CARD");
        assertTrue(scn.DSDecisionAvailable("DESTINY_DRAWN"));
        scn.DSPass();
        assertEquals(lightHasResponse, scn.LSDecisionAvailable("Just drew"));
    }

    @Test
    public void TheDarkSideIsGrowingUsedTakesJustDrawnCloneArmyDestinyCardIntoHand() {
        var scn = GetScenario();
        var growing = scn.GetLSCard("growing");
        var clonePilot = scn.GetLSCard("clonepilot");
        lightDrawsBattleDestiny(scn, "clonepilot", true);
        assertTrue(scn.LSCardPlayAvailable(growing, "Take destiny card into hand"));
        scn.LSPlayCard(growing, "Take destiny card into hand");
        scn.PassAllResponses();
        assertInZone(Zone.HAND, clonePilot);
        assertInZone(Zone.USED_PILE, growing);
    }

    @Test
    public void TheDarkSideIsGrowingUsedMayAlsoCancelAndRedrawThatDestiny() {
        var scn = GetScenario();
        var growing = scn.GetLSCard("growing");
        var clonePilot = scn.GetLSCard("clonepilot");
        lightDrawsBattleDestiny(scn, "clonepilot", true);
        scn.PrepareLSDestiny(5);
        assertTrue(scn.LSCardPlayAvailable(growing, "cancel and redraw"));
        scn.LSPlayCard(growing, "cancel and redraw");
        scn.PassAllResponses();
        assertInZone(Zone.HAND, clonePilot);
        // redrawn destiny 5 (the canceled Clone Pilot would total 3)
        assertEquals(5, scn.GetLSTotalDestiny());
    }

    @Test
    public void TheDarkSideIsGrowingUsedNotOfferedForNonCloneArmyDestinyCard() {
        var scn = GetScenario();
        // Light gets no response window to the draw because nothing (The Dark Side Is Growing included) responds
        lightDrawsBattleDestiny(scn, "luke", false);
    }

    @Test
    public void TheDarkSideIsGrowingStartingDeploysCloningCylindersAndTwoAlwaysImmuneEffects() {
        var scn = GetStartingScenario();
        var growing = scn.GetLSCard("growing");
        var cylinders = scn.GetLSCard("cylinders");
        var army = scn.GetLSCard("army");
        var blueOne = scn.GetLSCard("blueone");
        scn.StartGame();
        assertTrue(scn.LSDecisionAvailable("Choose starting interrupt"));
        scn.LSChooseCard(growing);
        scn.PassAllResponses();
        assertInZone(Zone.SIDE_OF_TABLE, cylinders);
        assertTrue(scn.LSDecisionAvailable("Choose card to deploy from Reserve Deck"));
        assertTrue(scn.LSHasCardChoiceAvailable(blueOne));
        scn.LSChooseCard(army);
        scn.PassAllResponses();
        assertInZone(Zone.SIDE_OF_TABLE, cylinders);
        assertInZone(Zone.SIDE_OF_TABLE, army);
        assertInZone(Zone.SIDE_OF_TABLE, blueOne);
        assertTrue(growing.getZone() == Zone.LOST_PILE || growing.getZone() == Zone.TOP_OF_LOST_PILE);
    }

    @Test
    public void TheDarkSideIsGrowingStartingNotOfferedWithoutRepublicAtWar() {
        var scn = GetScenario();
        var growing = scn.GetLSCard("growing");
        scn.StartGame();
        assertFalse(scn.LSDecisionAvailable("Choose starting interrupt") && scn.LSHasCardChoiceAvailable(growing));
    }
}
