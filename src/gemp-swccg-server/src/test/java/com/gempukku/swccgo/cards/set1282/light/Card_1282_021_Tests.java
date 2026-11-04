package com.gempukku.swccgo.cards.set1282.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInHand;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1282_021_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("laugh", "1282_21");
                    put("rennek", "1282_19"); // Rennek (V): stacks face up
                    put("ithorian", "6_20"); // non-unique alien
                    put("ithorian2", "6_20");
                    put("devaronian", "6_10"); // non-unique alien
                    put("luke", "1_19"); // not an alien
                }},
                new HashMap<>() {{
                    put("monnok", "2_135");
                    put("trooper", "1_194");
                    put("trooper2", "1_194");
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

    protected VirtualTableScenario GetAgentsScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("laugh", "1282_21");
                    put("agents", "112_1"); // Agents In The Court
                }},
                new HashMap<>() {{
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
    public void IKnowThatLaughStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("laugh").getBlueprint();

        assertEquals("I Know That Laugh", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        assertEquals("Jabba's decadent behavior makes him susceptible to deception. Leia and Lando exploited this weakness, posing as Jabba's kind of scum.", card.getLore());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertTrue(card.isImmuneToCardTitle(Title.Alter));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void IKnowThatLaughMayNotDeployWithoutAgentsInTheCourt() {
        var scn = GetAgentsScenario();
        var laugh = scn.GetLSCard("laugh");

        scn.StartGame();
        scn.MoveCardsToLSHand(laugh);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertFalse(scn.LSDeployAvailable(laugh));
    }

    @Test
    public void IKnowThatLaughDeploysOnTableIfAgentsInTheCourtOnTable() {
        var scn = GetAgentsScenario();
        var laugh = scn.GetLSCard("laugh");
        var agents = scn.GetLSCard("agents");

        scn.StartGame();
        scn.MoveCardsToLSHand(laugh);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToLSSideOfTable(agents);
        scn.LSPass();
        scn.DSPass();
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertTrue(scn.LSDeployAvailable(laugh));
        scn.LSDeployCard(laugh);
        scn.PassAllResponses();
        assertEquals(Zone.SIDE_OF_TABLE, laugh.getZone());
    }

    @Test
    public void IKnowThatLaughAddsOneAttritionAgainstOpponentPerAlienInBattle() {
        var scn = GetScenario();
        var laugh = scn.GetLSCard("laugh");
        var ithorian = scn.GetLSCard("ithorian");
        var devaronian = scn.GetLSCard("devaronian");
        var luke = scn.GetLSCard("luke");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(laugh);
        scn.MoveCardsToLocation(site, ithorian, devaronian, luke, trooper);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PrepareLSDestiny(1);
        scn.PrepareDSDestiny(1);
        scn.SkipToDamageSegment(true);

        // Attrition against opponent = your total battle destiny + 1 for each of two aliens (Luke is not an alien)
        assertEquals(scn.GetLSTotalDestiny() + 2, scn.GetUnpaidDSAttrition());
    }

    @Test
    public void IKnowThatLaughMayStackYourJustLostAlienFaceDown() {
        var scn = GetScenario();
        var laugh = scn.GetLSCard("laugh");
        var ithorian = scn.GetLSCard("ithorian");
        var trooper = scn.GetDSCard("trooper");
        var trooper2 = scn.GetDSCard("trooper2");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(laugh);
        scn.MoveCardsToLocation(site, ithorian, trooper, trooper2);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.SkipToDamageSegment(false);
        assertTrue(scn.DSWonBattle());
        scn.LSChooseCard(ithorian);

        assertTrue(LegacyVb82TestSupport.passUntilLSCardAction(scn, laugh, "Stack"));
        scn.LSUseCardAction(laugh, "Stack");
        scn.PassAllResponses();

        assertTrue(scn.IsStackedOn(laugh, ithorian));
        assertEquals(Zone.STACKED_FACE_DOWN, ithorian.getZone());
    }

    @Test
    public void IKnowThatLaughStacksRennekVFaceUp() {
        var scn = GetScenario();
        var laugh = scn.GetLSCard("laugh");
        var rennek = scn.GetLSCard("rennek");
        var vader = scn.GetDSCard("vader");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(laugh);
        scn.MoveCardsToLocation(site, rennek, vader);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.SkipToDamageSegment(false);
        scn.LSChooseCard(rennek);

        assertTrue(LegacyVb82TestSupport.passUntilLSCardAction(scn, laugh, "Stack"));
        scn.LSUseCardAction(laugh, "Stack");
        scn.PassAllResponses();

        assertTrue(scn.IsStackedOn(laugh, rennek));
        assertEquals(Zone.STACKED, rennek.getZone());
    }

    private static boolean isLost(com.gempukku.swccgo.game.PhysicalCard card) {
        return card.getZone() == Zone.LOST_PILE || card.getZone() == Zone.TOP_OF_LOST_PILE;
    }

    private static void resolveMonnok(VirtualTableScenario scn) {
        for (int i = 0; i < 10 && (scn.DSAnyDecisionsAvailable() || scn.LSAnyDecisionsAvailable()) && !scn.AwaitingDSControlPhaseActions(); i++) {
            scn.PassResponses();
        }
    }

    @Test
    public void IKnowThatLaughMakesNonUniqueAliensImmuneToMonnok() {
        var scn = GetScenario();
        var laugh = scn.GetLSCard("laugh");
        var ithorian = scn.GetLSCard("ithorian");
        var ithorian2 = scn.GetLSCard("ithorian2");
        var monnok = scn.GetDSCard("monnok");

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(laugh);
        scn.MoveCardsToLSHand(ithorian, ithorian2);
        scn.MoveCardsToDSHand(monnok);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.DSActivateForceCheat(4);

        assertTrue(scn.DSPlayLostInterruptAvailable(monnok));
        scn.DSPlayLostInterrupt(monnok);
        resolveMonnok(scn);

        assertInHand(ithorian);
        assertInHand(ithorian2);
    }

    @Test
    public void MonnokStillHitsDuplicateNonUniqueAliensWithoutIKnowThatLaugh() {
        var scn = GetScenario();
        var ithorian = scn.GetLSCard("ithorian");
        var ithorian2 = scn.GetLSCard("ithorian2");
        var monnok = scn.GetDSCard("monnok");

        scn.StartGame();
        scn.MoveCardsToLSHand(ithorian, ithorian2);
        scn.MoveCardsToDSHand(monnok);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.DSActivateForceCheat(4);

        scn.DSPlayLostInterrupt(monnok);
        for (int i = 0; i < 12 && !(isLost(ithorian) && isLost(ithorian2)); i++) {
            if (scn.LSDecisionAvailable("Choose card to put on Lost Pile")) {
                scn.LSChooseCard(isLost(ithorian) ? ithorian2 : ithorian);
            }
            else if (scn.DSDecisionAvailable("Opponent's hand")) {
                scn.DSPass();
            }
            else {
                scn.PassResponses();
            }
        }

        assertEquals(Zone.LOST_PILE, ithorian.getZone() == Zone.TOP_OF_LOST_PILE ? Zone.LOST_PILE : ithorian.getZone());
        assertEquals(Zone.LOST_PILE, ithorian2.getZone() == Zone.TOP_OF_LOST_PILE ? Zone.LOST_PILE : ithorian2.getZone());
    }
}
