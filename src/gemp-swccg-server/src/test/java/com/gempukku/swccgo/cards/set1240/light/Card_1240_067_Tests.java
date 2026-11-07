package com.gempukku.swccgo.cards.set1240.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
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
import java.util.List;
import java.util.Map;

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.*;

public class Card_1240_067_Tests {
    private static final String BOUSHH = "Deploy Boushh from Reserve Deck";
    private static final String AX = "Deploy Vibro-Ax on Chewie";
    private static final String BOTTOM = "Take bottom card of Force Pile into hand";

    private VirtualTableScenario Started() {
        var scn = new VirtualTableScenario(new HashMap<>(Map.of(
                "chewie", "1240_67", "boushh", "110_1", "ax", "6_90", "docking", "12_74",
                "leia", "5_7", "lando", "112_3")), new HashMap<>(Map.of(
                "droid", "5_97", "insult", "5_138", "trooper", "1_194", "otherTrooper", "1_194")),
                30, 30, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
        scn.StartGame();
        scn.MoveLocationToTable(scn.GetLSCard("docking"));
        return scn;
    }

    @Test
    public void ChewbaccaProtectorMatchesTheBlockFourPrinting() {
        var scn = Started();
        var card = scn.GetLSCard("chewie").getBlueprint();
        assertEquals("Chewbacca, Protector", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(1, card.getDestiny(), scn.epsilon);
        assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(6, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(4, card.getArmor(), scn.epsilon);
        assertEquals(7, card.getForfeit(), scn.epsilon);
        assertEquals(Species.WOOKIEE, card.getSpecies());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>(List.of(CardType.REBEL)));
        scn.BlueprintKeywordCheck(card, new ArrayList<>(List.of(Keyword.SMUGGLER)));
        scn.BlueprintPersonaCheck(card, new ArrayList<>(List.of(Persona.CHEWIE)));
        scn.BlueprintIconCheck(card, new ArrayList<>(List.of(Icon.REBEL, Icon.PILOT, Icon.WARRIOR,
                Icon.REFLECTIONS_II, Icon.LEGACY_BLOCK_4)));
    }

    private boolean IsSpy(VirtualTableScenario scn) {
        return scn.game().getModifiersQuerying().hasKeyword(scn.gameState(), scn.GetLSCard("chewie"), Keyword.SPY);
    }

    @Test
    public void ChewbaccaDeploysToCoruscantWithNoActivatedForceAndIsASpyOnlyThere() {
        var scn = Started();
        var chewie = scn.GetLSCard("chewie");
        var docking = scn.GetLSCard("docking");
        scn.SkipToLSTurn(Phase.ACTIVATE);
        scn.MoveCardsToLSHand(chewie);
        LeaveActivateWithForce(scn, true, 0);
        scn.SkipToPhase(Phase.DEPLOY);
        scn.LSDeployCardAndPassResponses(chewie, docking);
        assertAtLocation(docking, chewie);
        assertEquals(0, scn.GetLSForcePileCount());
        assertTrue(IsSpy(scn));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), chewie);
        assertFalse(IsSpy(scn));
    }

    @Test
    public void ChewbaccaPaysPrintedThreeForceAtOtherSites() {
        var scn = Started();
        var chewie = scn.GetLSCard("chewie");
        scn.MoveCardsToLSHand(chewie);
        scn.EnsureLSForcePile(3);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.DrainLSForcePileTo(3);
        scn.LSDeployCardAndPassResponses(chewie, scn.GetLSStartingLocation());
        assertAtLocation(scn.GetLSStartingLocation(), chewie);
        assertEquals(0, scn.GetLSForcePileCount());
        assertFalse(IsSpy(scn));
    }

    @Test
    public void ChewbaccaCannotDeployOutsideCoruscantWithOnlyTwoForce() {
        var scn = Started();
        var chewie = scn.GetLSCard("chewie");
        scn.MoveCardsToLSHand(chewie);
        scn.EnsureLSForcePile(2);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.DrainLSForcePileTo(2);
        scn.LSDeployCard(chewie);
        assertTrue(scn.LSHasCardChoiceAvailable(scn.GetLSCard("docking")));
        assertFalse(scn.LSHasCardChoiceAvailable(scn.GetLSStartingLocation()));
    }

    private VirtualTableScenario DownloadReady(int force, Phase phase) {
        var scn = Started();
        scn.MoveCardsToLocation(scn.GetLSCard("docking"), scn.GetLSCard("chewie"));
        scn.SkipToLSTurn(Phase.ACTIVATE);
        // Held aside while the Force Pile is set, so that none of them is counted as Force.
        scn.MoveCardsToLSHand(scn.GetLSCard("boushh"), scn.GetLSCard("ax"), scn.GetLSCard("lando"));
        LeaveActivateWithForce(scn, true, force);
        scn.MoveCardsToTopOfLSReserveDeck(scn.GetLSCard("boushh"), scn.GetLSCard("ax"), scn.GetLSCard("lando"));
        assertEquals(force, scn.GetLSForcePileCount());
        scn.SkipToPhase(phase);
        return scn;
    }

    private boolean SelectableInReserve(VirtualTableScenario scn, PhysicalCardImpl card) {
        var blueprints = scn.GetADParam(scn.LS, "blueprintId");
        var selectable = scn.GetADParam(scn.LS, "selectable");
        assertNotNull(blueprints);
        assertEquals(blueprints.length, selectable.length);
        for (int i = 0; i < blueprints.length; i++) {
            if (blueprints[i].equals(card.getBlueprintId(true)) && Boolean.parseBoolean(selectable[i])) return true;
        }
        return false;
    }

    @Test
    public void ChewbaccaDownloadsOriginalBoushhPaysFourAtCoruscantAndReshuffles() {
        var scn = DownloadReady(4, Phase.DEPLOY);
        var boushh = scn.GetLSCard("boushh");
        var docking = scn.GetLSCard("docking");
        var remaining = new ArrayList<>(scn.GetLSReserveDeck());
        remaining.remove(boushh);
        scn.gameState().setRandomSeed(124067);
        scn.LSUseCardAction(scn.GetLSCard("chewie"), BOUSHH);
        assertTrue(SelectableInReserve(scn, boushh));
        assertFalse(SelectableInReserve(scn, scn.GetLSCard("ax")));
        scn.LSChooseCard(boushh);
        if (scn.LSHasCardChoiceAvailable(docking)) scn.LSChooseCard(docking);
        scn.PassAllResponses();
        assertAtLocation(docking, boushh);
        assertTrue(boushh.isUndercover());
        assertEquals(0, scn.GetLSForcePileCount());
        assertInZone(Zone.RESERVE_DECK, scn.GetLSCard("ax"));
        assertNotEquals(remaining, new ArrayList<>(scn.GetLSReserveDeck()));
    }

    @Test
    public void ChewbaccaDownloadsOriginalVibroAxOnlyOnHimselfPaysThreeAndReshuffles() {
        var scn = DownloadReady(3, Phase.DEPLOY);
        var chewie = scn.GetLSCard("chewie");
        var ax = scn.GetLSCard("ax");
        var remaining = new ArrayList<>(scn.GetLSReserveDeck());
        remaining.remove(ax);
        scn.gameState().setRandomSeed(124067);
        scn.LSUseCardAction(chewie, AX);
        assertTrue(SelectableInReserve(scn, ax));
        assertFalse(SelectableInReserve(scn, scn.GetLSCard("boushh")));
        assertFalse(SelectableInReserve(scn, scn.GetLSCard("lando")));
        scn.LSChooseCard(ax);
        if (scn.LSHasCardChoiceAvailable(chewie)) scn.LSChooseCard(chewie);
        scn.PassAllResponses();
        assertInZone(Zone.ATTACHED, ax);
        assertEquals(chewie, ax.getAttachedTo());
        assertEquals(7, scn.GetPower(chewie));
        assertEquals(0, scn.GetLSForcePileCount());
        assertInZone(Zone.RESERVE_DECK, scn.GetLSCard("boushh"));
        assertNotEquals(remaining, new ArrayList<>(scn.GetLSReserveDeck()));
    }

    @Test
    public void DownloadDoesNotAllowVibroAxToDeployFromHandOnChewbacca() {
        var scn = DownloadReady(3, Phase.DEPLOY);
        var ax = scn.GetLSCard("ax");
        scn.MoveCardsToLSHand(ax);
        assertFalse(scn.LSCardPlayAvailable(ax));
        assertInZone(Zone.HAND, ax);
    }

    @Test
    public void ChewbaccaCanUseTheDownloadedVibroAxInAnActualBattle() {
        var scn = DownloadReady(5, Phase.DEPLOY);
        var chewie = scn.GetLSCard("chewie");
        var ax = scn.GetLSCard("ax");
        var docking = scn.GetLSCard("docking");
        var trooper = scn.GetDSCard("trooper");
        var other = scn.GetDSCard("otherTrooper");
        scn.MoveCardsToLocation(docking, trooper, other);
        scn.LSUseCardAction(chewie, AX);
        scn.LSChooseCard(ax);
        if (scn.LSHasCardChoiceAvailable(chewie)) scn.LSChooseCard(chewie);
        scn.PassAllResponses();
        assertEquals(chewie, ax.getAttachedTo());
        scn.SkipToPhase(Phase.BATTLE);
        scn.LSStartBattleAndSkipToWeaponsSegment(docking);
        assertTrue(scn.LSCardActionAvailable(ax, "Fire Vibro-Ax"));
        scn.PrepareLSDestiny(3);
        scn.PrepareDSDestiny(1);
        scn.LSUseCardAction(ax, "Fire Vibro-Ax");
        scn.LSChooseCard(trooper);
        scn.PassAllResponses();
        assertFalse(scn.IsParticipatingInBattle(trooper));
        assertTrue(scn.IsParticipatingInBattle(other));
    }

    @Test
    public void ChewbaccaCannotDownloadOutsideHisOwnDeployPhase() {
        var scn = DownloadReady(6, Phase.CONTROL);
        var chewie = scn.GetLSCard("chewie");
        assertFalse(scn.LSCardActionAvailable(chewie, BOUSHH));
        assertFalse(scn.LSCardActionAvailable(chewie, AX));
        scn.SkipToDSTurn(Phase.DEPLOY);
        if (scn.AwaitingDSDeployPhaseActions()) scn.DSPass();
        assertFalse(scn.LSCardActionAvailable(chewie, BOUSHH));
        assertFalse(scn.LSCardActionAvailable(chewie, AX));
    }

    @Test
    public void UnaffordableDownloadsCannotSelectTheOriginalCards() {
        var scn = DownloadReady(2, Phase.DEPLOY);
        scn.LSUseCardAction(scn.GetLSCard("chewie"), AX);
        assertFalse(SelectableInReserve(scn, scn.GetLSCard("ax")));
        assertEquals(2, scn.GetLSForcePileCount());

        var other = DownloadReady(3, Phase.DEPLOY);
        other.LSUseCardAction(other.GetLSCard("chewie"), BOUSHH);
        assertFalse(SelectableInReserve(other, other.GetLSCard("boushh")));
        assertEquals(3, other.GetLSForcePileCount());
    }

    private VirtualTableScenario WithLeia(boolean together, int force, Phase phase) {
        var scn = Started();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("leia"));
        var chewieSite = together ? scn.GetLSStartingLocation() : scn.GetLSCard("docking");
        scn.MoveCardsToLocation(chewieSite, scn.GetLSCard("chewie"));
        scn.SkipToLSTurn(Phase.ACTIVATE);
        LeaveActivateWithForce(scn, true, force);
        scn.SkipToPhase(phase);
        return scn;
    }

    @Test
    public void ChewbaccaTakesOnlyBottomOfForcePileWithLeiaAndMayDoSoAgainOnNextTurn() {
        var scn = WithLeia(true, 3, Phase.CONTROL);
        var chewie = scn.GetLSCard("chewie");
        var top = scn.gameState().getTopOfCardPile(scn.LS, Zone.FORCE_PILE);
        var bottom = scn.gameState().getBottomOfCardPile(scn.LS, Zone.FORCE_PILE);
        assertNotEquals(top, bottom);
        int hand = scn.GetLSHandCount();
        scn.LSUseCardAction(chewie, BOTTOM);
        scn.PassAllResponses();
        assertInZone(Zone.HAND, (PhysicalCardImpl) bottom);
        assertEquals(top, scn.gameState().getTopOfCardPile(scn.LS, Zone.FORCE_PILE));
        assertEquals(2, scn.GetLSForcePileCount());
        assertEquals(hand + 1, scn.GetLSHandCount());
        if (scn.AwaitingDSControlPhaseActions()) scn.DSPass();
        assertFalse(scn.LSCardActionAvailable(chewie, BOTTOM));
        scn.SkipToDSTurn(Phase.ACTIVATE);
        scn.EnsureLSForcePile(1);
        scn.SkipToPhase(Phase.CONTROL);
        if (scn.AwaitingDSControlPhaseActions()) scn.DSPass();
        assertTrue(scn.LSCardActionAvailable(chewie, BOTTOM));
        bottom = scn.gameState().getBottomOfCardPile(scn.LS, Zone.FORCE_PILE);
        scn.LSUseCardAction(chewie, BOTTOM);
        scn.PassAllResponses();
        assertInZone(Zone.HAND, (PhysicalCardImpl) bottom);
    }

    @Test
    public void ChewbaccaCannotTakeBottomWithLeiaElsewhereOrWithEmptyForcePile() {
        var scn = WithLeia(false, 3, Phase.CONTROL);
        var chewie = scn.GetLSCard("chewie");
        assertFalse(scn.LSCardActionAvailable(chewie, BOTTOM));
        var empty = WithLeia(true, 0, Phase.CONTROL);
        assertEquals(0, empty.GetLSForcePileCount());
        assertFalse(empty.LSCardActionAvailable(empty.GetLSCard("chewie"), BOTTOM));
    }

    private void InsultLeia(boolean together) {
        var scn = WithLeia(together, 0, Phase.CONTROL);
        var leia = scn.GetLSCard("leia");
        var insult = scn.GetDSCard("insult");
        var droid = scn.GetDSCard("droid");
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), droid);
        scn.MoveCardsToDSHand(insult);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.DSPlayCard(insult, "Insult");
        if (scn.DSHasCardChoiceAvailable(droid)) scn.DSChooseCard(droid);
        if (scn.DSHasCardChoiceAvailable(leia)) scn.DSChooseCard(leia);
        scn.PassAllResponses();
        assertEquals(!together, scn.game().getModifiersQuerying().isGameTextCanceled(scn.gameState(), leia));
        assertInZone(Zone.USED_PILE, insult);
    }

    @Test public void NativeInsultCannotCancelLeiasGameTextWhileWithChewbacca() { InsultLeia(true); }
    @Test public void NativeInsultCancelsLeiasGameTextWhileChewbaccaIsElsewhere() { InsultLeia(false); }

    /**
     * From the given player's activate phase, sets that player's Force Pile to exactly force and leaves the phase
     * without activating, so the next phase's actions are offered against that Force.
     */
    static void LeaveActivateWithForce(VirtualTableScenario scn, boolean light, int force) {
        String player = light ? scn.LS : scn.DS;
        scn.EnsureForcePile(player, force);
        scn.DrainForcePileTo(player, force);
        if (light) scn.LSPass(); else scn.DSPass();
        if (scn.DecisionAvailable(player, "You have not activated Force")) scn.ChooseOption(player, "Yes");
        scn.PassActivateActions();
    }
}
