package com.gempukku.swccgo.cards.set1282.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.legacy.MissionTestSupport;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInHand;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1282_001_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("leia", "1_17");
                }},
                new HashMap<>() {{
                    put("chevin", "1282_1");
                    put("trandoshan", "6_126"); // Trandoshan (non-unique)
                    put("bossk", "4_92"); // Bossk (unique Trandoshan)
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

    /** Test-only Dark alien with the slaver keyword (GEMP grants 'slaver' only through other cards' text). */
    private static AbstractAlien testSlaver(String title) {
        return new AbstractAlien(Side.DARK, 1, 1, 1, 1, 1, title, Uniqueness.UNRESTRICTED, ExpansionSet.PREMIERE, Rarity.C1) {
            {
                addKeyword(Keyword.SLAVER);
            }
        };
    }

    private static PhysicalCardImpl slaver(VirtualTableScenario scn, int id) {
        return MissionTestSupport.putInHand(scn, id, scn.DS, testSlaver("Test Slaver " + id));
    }

    /** Passes responses (both players) until Dark Side is offered the given card action, or nothing is pending. */
    private static void passUntilDSCardAction(VirtualTableScenario scn, PhysicalCardImpl card, String text) {
        passUntil(scn, () -> scn.DSAnyDecisionsAvailable() && !scn.GetDSAvailableActions().isEmpty() && scn.DSCardActionAvailable(card, text));
    }

    /** Passes responses (both players) until the condition holds, or nothing is pending. */
    static void passUntil(VirtualTableScenario scn, java.util.function.BooleanSupplier done) {
        for (int i = 0; i < 20; ++i) {
            if (done.getAsBoolean())
                return;
            if (scn.LSAnyDecisionsAvailable())
                scn.LSPass();
            else if (scn.DSAnyDecisionsAvailable())
                scn.DSPass();
            else
                return;
        }
    }

    @Test
    public void ChevinStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("chevin").getBlueprint();
        assertEquals("Chevin", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(1, card.getAbility(), scn.epsilon);
        assertEquals(2, card.getForfeit(), scn.epsilon);
        assertEquals(Species.CHEVIN, card.getSpecies());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.ALIEN);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.MERCENARY);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.ALIEN);
            add(Icon.WARRIOR);
            add(Icon.JABBAS_PALACE);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void ChevinWhenDeployedMayUseOneForceToTakeNonUniqueTrandoshanIntoHand() {
        var scn = GetScenario();
        var chevin = scn.GetDSCard("chevin");
        var trandoshan = scn.GetDSCard("trandoshan");
        var bossk = scn.GetDSCard("bossk");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToHand(chevin);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.EnsureDSForcePile(6);
        scn.MoveCardsToTopOfDSReserveDeck(trandoshan, bossk);

        scn.DSDeployCard(chevin);
        scn.DSChooseCard(site);
        passUntilDSCardAction(scn, chevin, "Take a non-unique Trandoshan");
        int forceBefore = scn.GetDSForcePileCount();
        assertTrue(scn.DSCardActionAvailable(chevin, "Take a non-unique Trandoshan"));
        scn.DSUseCardAction(chevin, "Take a non-unique Trandoshan");
        passUntil(scn, () -> scn.DSAnyDecisionsAvailable() && !scn.DSGetCardChoices().isEmpty());
        assertTrue(scn.DSHasCardChoiceAvailable(trandoshan));
        // Bossk (unique Trandoshan) is shown but not selectable
        assertEquals(1, scn.DSGetSelectableCount());
        scn.DSChooseCard(trandoshan);
        scn.PassAllResponses();

        assertInHand(trandoshan);
        assertEquals(forceBefore - 1, scn.GetDSForcePileCount());
    }

    @Test
    public void ChevinIsPowerPlusOneWhileYourOtherSlaverPresent() {
        var scn = GetScenario();
        var chevin = scn.GetDSCard("chevin");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, chevin);
        assertEquals(2, scn.GetPower(chevin));

        var slaver = slaver(scn, 128201);
        scn.MoveCardsToLocation(site, slaver);
        assertEquals(3, scn.GetPower(chevin));
    }

    @Test
    public void ChevinIsNotPowerPlusOneWhenSlaverIsElsewhere() {
        var scn = GetScenario();
        var chevin = scn.GetDSCard("chevin");
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), chevin);
        var slaver = slaver(scn, 128202);
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), slaver);
        assertEquals(2, scn.GetPower(chevin));
    }

    private void loseBattleWithAttrition(VirtualTableScenario scn, PhysicalCardImpl site) {
        var luke = scn.GetLSCard("luke");
        var leia = scn.GetLSCard("leia");
        scn.MoveCardsToLocation(site, luke, leia);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.EnsureLSForcePile(4);
        scn.LSInitiateBattle(site);
        scn.PrepareLSDestiny(6);
        scn.PrepareDSDestiny(0);
        scn.SkipToDamageSegment(true);
    }

    @Test
    public void ChevinMayGoOutOfPlayToSatisfyAllAttritionWithTwoOtherSlaversAtSite() {
        var scn = GetScenario();
        var chevin = scn.GetDSCard("chevin");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        var slaver1 = slaver(scn, 128203);
        var slaver2 = slaver(scn, 128204);
        scn.MoveCardsToLocation(site, chevin, slaver1, slaver2);
        loseBattleWithAttrition(scn, site);

        assertTrue(scn.GetUnpaidDSAttrition() > 0);
        assertTrue(scn.DSCardActionAvailable(chevin, "Place out of play to satisfy all attrition"));
        scn.DSUseCardAction(chevin, "Place out of play to satisfy all attrition");
        scn.PassAllResponses();

        assertInZone(Zone.OUT_OF_PLAY, chevin);
        assertEquals(0, scn.GetUnpaidDSAttrition());
        assertInZone(Zone.AT_LOCATION, slaver1);
        assertInZone(Zone.AT_LOCATION, slaver2);
    }

    @Test
    public void ChevinMayNotSatisfyAttritionWithOnlyOneOtherSlaverAtSite() {
        var scn = GetScenario();
        var chevin = scn.GetDSCard("chevin");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        var slaver1 = slaver(scn, 128205);
        scn.MoveCardsToLocation(site, chevin, slaver1);
        loseBattleWithAttrition(scn, site);

        assertTrue(scn.GetUnpaidDSAttrition() > 0);
        assertTrue(scn.AwaitingDSAttritionPayment());
        assertFalse(scn.DSDecisionAvailable("Optional"));
    }
}
