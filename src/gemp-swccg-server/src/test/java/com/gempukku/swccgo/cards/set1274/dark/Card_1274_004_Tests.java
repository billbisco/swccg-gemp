package com.gempukku.swccgo.cards.set1274.dark;

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

public class Card_1274_004_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("xwing", "1_146");
                    put("sabotage", "1210_162");
                }},
                new HashMap<>() {{
                    put("tibanna", "1274_4");
                    put("tibanna2", "1274_4");
                    put("bespin", "5_164");
                    put("clouds", "5_174");
                    put("tie", "1_304");
                    put("trooper", "1_194");
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
    public void TibannaFloatingRefineryStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("tibanna").getBlueprint();

        assertEquals("Tibanna Floating Refinery", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.DEVICE);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.DEPLOYS_ON_LOCATION);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DEVICE);
            add(Icon.LEGACY_BLOCK_7);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void TibannaFloatingRefineryDeploysOnACloudSector() {
        var scn = GetScenario();
        var tibanna = scn.GetDSCard("tibanna");
        var bespin = scn.GetDSCard("bespin");
        var clouds = scn.GetDSCard("clouds");

        scn.StartGame();
        scn.MoveLocationToTable(bespin);
        scn.MoveLocationToTable(clouds);
        scn.MoveCardsToDSHand(tibanna);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSCardPlayAvailable(tibanna));
        scn.DSPlayCard(tibanna);
        assertTrue(scn.DSHasCardChoiceAvailable(clouds));
        scn.DSChooseCard(clouds);
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(clouds, tibanna));
    }

    @Test
    public void TibannaFloatingRefineryForceGenerationPlusOneAtOccupiedCloudSectors() {
        var scn = GetScenario();
        var tibanna = scn.GetDSCard("tibanna");
        var bespin = scn.GetDSCard("bespin");
        var clouds = scn.GetDSCard("clouds");
        var tie = scn.GetDSCard("tie");

        scn.StartGame();
        scn.MoveLocationToTable(bespin);
        scn.MoveLocationToTable(clouds);
        scn.MoveCardsToLocation(clouds, tie);
        float before = scn.game().getModifiersQuerying().getForceGenerationFromLocation(scn.gameState(), clouds, scn.DS);
        scn.AttachCardsTo(clouds, tibanna);
        float after = scn.game().getModifiersQuerying().getForceGenerationFromLocation(scn.gameState(), clouds, scn.DS);
        assertEquals(before + 1, after, scn.epsilon);
    }

    @Test
    public void TibannaFloatingRefineryDuringControlPhaseMayDrawDestinyGreaterThanTwoToRetrieveOneForce() {
        var scn = GetScenario();
        var tibanna = scn.GetDSCard("tibanna");
        var bespin = scn.GetDSCard("bespin");
        var clouds = scn.GetDSCard("clouds");
        var tie = scn.GetDSCard("tie");
        var trooper = scn.GetDSCard("trooper");
        var vader = scn.GetDSCard("vader");
        var marketplace = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(bespin);
        scn.MoveLocationToTable(clouds);
        scn.MoveCardsToLocation(clouds, tie);
        scn.MoveCardsToLocation(marketplace, trooper);
        scn.AttachCardsTo(clouds, tibanna);
        scn.MoveCardsToTopOfDSLostPile(vader);
        scn.SkipToDSTurn(Phase.CONTROL);
        int usedBefore = scn.GetDSUsedPileCount();
        assertTrue(scn.DSCardActionAvailable(tibanna, "Draw destiny to retrieve Force"));
        var destCard = scn.GetDSDestiny(7);
        scn.MoveCardsToTopOfDSReserveDeck(destCard);
        scn.DSUseCardAction(tibanna, "Draw destiny to retrieve Force");
        scn.PassResponses("Determine costs");
        scn.PassResponses("About to draw");
        scn.PassResponses("Just drew");
        scn.PassDestinyDrawResponses();
        scn.PassAllResponses();
        scn.LSPass();
        scn.PassAllResponses();
        assertTrue(scn.GetDSUsedPileCount() > usedBefore);
        assertTrue(destCard.getZone() == Zone.USED_PILE || destCard.getZone() == Zone.TOP_OF_USED_PILE
                || vader.getZone() == Zone.USED_PILE || vader.getZone() == Zone.TOP_OF_USED_PILE);
        assertTrue(scn.AwaitingDSControlPhaseActions());
        assertFalse(scn.DSCardActionAvailable(tibanna, "Draw destiny to retrieve Force"));
    }

    @Test
    public void TibannaFloatingRefineryDoesNotRetrieveWhenDestinyIsTwoOrLess() {
        var scn = GetScenario();
        var tibanna = scn.GetDSCard("tibanna");
        var bespin = scn.GetDSCard("bespin");
        var clouds = scn.GetDSCard("clouds");
        var tie = scn.GetDSCard("tie");
        var trooper = scn.GetDSCard("trooper");
        var vader = scn.GetDSCard("vader");
        var marketplace = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(bespin);
        scn.MoveLocationToTable(clouds);
        scn.MoveCardsToLocation(clouds, tie);
        scn.MoveCardsToLocation(marketplace, trooper);
        scn.AttachCardsTo(clouds, tibanna);
        scn.MoveCardsToTopOfDSLostPile(vader);
        scn.SkipToDSTurn(Phase.CONTROL);
        assertTrue(scn.DSCardActionAvailable(tibanna, "Draw destiny to retrieve Force"));
        scn.PrepareDSDestiny(2);
        scn.DSUseCardAction(tibanna, "Draw destiny to retrieve Force");
        scn.PassAllResponses();
        assertTrue(vader.getZone() == Zone.LOST_PILE || vader.getZone() == Zone.TOP_OF_LOST_PILE);
    }

    @Test
    public void TibannaFloatingRefineryIfLostFromTableIsPlacedInUsedPile() {
        var scn = GetScenario();
        var tibanna = scn.GetDSCard("tibanna");
        var sabotage = scn.GetLSCard("sabotage");
        var bespin = scn.GetDSCard("bespin");
        var clouds = scn.GetDSCard("clouds");

        scn.StartGame();
        scn.MoveLocationToTable(bespin);
        scn.MoveLocationToTable(clouds);
        scn.AttachCardsTo(clouds, tibanna);
        scn.MoveCardsToLSHand(sabotage);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.PrepareLSDestiny(1);
        assertTrue(scn.LSCardPlayAvailable(sabotage));
        scn.LSPlayCard(sabotage);
        assertTrue(scn.LSDecisionAvailable("Choose"));
        scn.LSChooseCard(tibanna);
        scn.PassAllResponses();
        assertTrue(tibanna.getZone() == Zone.USED_PILE || tibanna.getZone() == Zone.TOP_OF_USED_PILE);
    }
}
