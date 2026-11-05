package com.gempukku.swccgo.cards.set1273.dark;

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

public class Card_1273_007_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("tatooine", "1_127"); // Tatooine (system)
                    put("celebration", "7_77"); // Tatooine Celebration
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("isb", "1273_7");
                    put("trooper1", "1_194"); // Imperial trooper
                    put("trooper2", "1_194");
                    put("trooper3", "1_194");
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
    public void ISBSectorCommanderStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("isb").getBlueprint();
        assertEquals("ISB Sector Commander", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(3, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.IMPERIAL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.LEADER);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.IMPERIAL);
            add(Icon.PILOT);
            add(Icon.WARRIOR);
            add(Icon.LEGACY_BLOCK_7);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    private VirtualTableScenario deployWithImperialsLost() {
        var scn = GetScenario();
        var isb = scn.GetDSCard("isb");
        scn.StartGame();
        scn.MoveCardsToDSHand(isb);
        scn.MoveCardsToTopOfDSLostPile(scn.GetDSCard("trooper1"), scn.GetDSCard("trooper2"), scn.GetDSCard("trooper3"));
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSDeployCard(isb);
        scn.DSChooseCard(scn.GetDSStartingLocation());
        LegacyVb73DarkTestSupport.passUntilAction(scn, scn.DS, "Retrieve");
        return scn;
    }

    @Test
    public void ISBSectorCommanderRetrievesUpToTwoImperialsWhenDeployed() {
        var scn = deployWithImperialsLost();
        assertTrue(scn.DSActionAvailable("Retrieve up to two Imperials"));
        scn.DSChooseAction("Retrieve up to two Imperials");
        LegacyVb73DarkTestSupport.resolveUntil(scn, () -> scn.GetDSLostPileCount() < 2);
        assertEquals(1, scn.GetDSLostPileCount());
    }

    @Test
    public void ISBSectorCommanderMayInsteadRetrieveOneImperialIntoHand() {
        var scn = deployWithImperialsLost();
        int handBefore = scn.GetDSHandCount();
        scn.DSChooseAction("Retrieve an Imperial into hand");
        LegacyVb73DarkTestSupport.resolveUntil(scn, () -> scn.GetDSLostPileCount() < 3);
        assertEquals(2, scn.GetDSLostPileCount());
        assertEquals(handBefore + 1, scn.GetDSHandCount());
    }

    private boolean celebrationActive(boolean isbThere, boolean lukeThere) {
        var scn = GetScenario();
        var tatooine = scn.GetLSCard("tatooine");
        var celebration = scn.GetLSCard("celebration");
        var marketplace = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.AttachCardsTo(tatooine, celebration);
        if (isbThere) {
            scn.MoveCardsToLocation(marketplace, scn.GetDSCard("isb"));
        }
        if (lukeThere) {
            scn.MoveCardsToLocation(marketplace, scn.GetLSCard("luke"));
        }
        scn.SkipToDSTurn(Phase.CONTROL);
        return scn.IsCardActive(celebration);
    }

    @Test
    public void ISBSectorCommanderSuspendsCelebrationAtRelatedSystemWhilePresentAtSiteYouControl() {
        assertTrue(celebrationActive(false, false));
        assertFalse(celebrationActive(true, false));
        // Light is also present, so Dark does not control the site
        assertTrue(celebrationActive(true, true));
    }

    @Test
    public void ISBSectorCommanderUsesOneForceToDeployTrooper() {
        var scn = GetScenario();
        var isb = scn.GetDSCard("isb");
        var trooper = scn.GetDSCard("trooper1");
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), isb);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfDSReserveDeck(trooper);
        int forceBefore = scn.GetDSForcePileCount();
        assertTrue(scn.DSCardActionAvailable(isb, "Deploy trooper"));
        scn.DSUseCardAction(isb, "Deploy trooper");
        LegacyVb73DarkTestSupport.passUntilCardChoice(scn, scn.DS);
        scn.DSChooseCard(trooper);
        scn.DSChooseCard(scn.GetDSStartingLocation());
        scn.PassAllResponses();
        assertInZone(Zone.AT_LOCATION, trooper);
        // 1 Force for the text plus the trooper's deploy cost
        assertTrue(scn.GetDSForcePileCount() < forceBefore - 1);
    }
}
