package com.gempukku.swccgo.cards.set1250.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.InterruptPersonaReferences;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.decisions.CardActionSelectionDecision;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.*;

public class Card_1250_021_Tests {
    private VirtualTableScenario scenario() {
        var scn = new VirtualTableScenario(new HashMap<>(Map.of("obi", "1_21", "luke", "1_19",
                "alter", "1_71", "skill", "1_64", "maintenance", "13_27", "permanent", "108_2",
                "enemyInterrupt", "5_49")),
                new HashMap<>(Map.ofEntries(Map.entry("trained", "1250_21"), Map.entry("vader", "1_168"),
                        Map.entry("saber", "1_324"), Map.entry("trooper", "1_194"), Map.entry("skill", "1_209"),
                        Map.entry("maintenance", "13_59"), Map.entry("permanent", "109_11"),
                        Map.entry("maul", "11_54"), Map.entry("mara", "110_10"), Map.entry("duel", "1110_13"))),
                28, 28, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
        scn.StartGame();
        return scn;
    }

    @Test public void TrainedInTheJediArtsPrintedMetadataMatches() {
        var scn = scenario();
        var b = scn.GetDSCard("trained").getBlueprint();
        assertEquals("Trained In The Jedi Arts", b.getTitle());
        assertEquals(Side.DARK, b.getSide());
        assertEquals(Uniqueness.UNIQUE, b.getUniqueness());
        assertEquals(CardSubtype.NORMAL, b.getCardSubtype());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_5, b.getExpansionSet());
        assertEquals(Rarity.V, b.getRarity());
        assertEquals(5, b.getDestiny(), scn.epsilon);
        assertTrue(b.isLegacy());
        assertFalse(b.hasVirtualSuffix());
        assertEquals("While Qui-Gon spent years maintaining peace, Maul was sharpening his combat skills. Those years of training came to fruition in one fateful moment.", b.getLore());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(List.of(CardType.EFFECT)));
        scn.BlueprintIconCheck(b, new ArrayList<>(List.of(Icon.EFFECT, Icon.REFLECTIONS_III,
                Icon.EPISODE_I, Icon.LEGACY_BLOCK_5)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>());
    }

    @Test public void TrainedInTheJediArtsActualDeployLosesOwnPermanentWeaponAndMaintenanceButKeepsOrdinarySaber() {
        var scn = scenario();
        var site = scn.GetDSStartingLocation();
        var trained = scn.GetDSCard("trained");
        var vader = scn.GetDSCard("vader");
        var saber = scn.GetDSCard("saber");
        var permanent = scn.GetDSCard("permanent");
        var maintenance = scn.GetDSCard("maintenance");
        scn.MoveCardsToLocation(site, vader, permanent, maintenance);
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("maintenance"), scn.GetLSCard("permanent"));
        scn.AttachCardsTo(vader, saber);
        scn.MoveCardsToDSHand(trained);
        scn.SkipToPhase(Phase.DEPLOY);
        assertTrue(scn.DSCardPlayAvailable(trained));
        scn.DSPlayCard(trained);
        finishDeploy(scn);
        assertInZone(Zone.SIDE_OF_TABLE, trained);
        assertInZone(Zone.LOST_PILE, permanent);
        assertInZone(Zone.LOST_PILE, maintenance);
        assertInZone(Zone.ATTACHED, saber);
        assertInZone(Zone.AT_LOCATION, vader);
        assertInZone(Zone.AT_LOCATION, scn.GetLSCard("permanent"));
        assertInZone(Zone.AT_LOCATION, scn.GetLSCard("maintenance"));
    }

    @Test public void TrainedInTheJediArtsLosesAPermanentWeaponCharacterDeployedLater() {
        var scn = scenario();
        var site = scn.GetDSStartingLocation();
        var permanent = scn.GetDSCard("permanent");
        scn.MoveCardsToSideOfTable(scn.GetDSCard("trained"));
        scn.MoveCardsToLocation(site, scn.GetDSCard("trooper"));
        scn.MoveCardsToDSHand(permanent);
        scn.EnsureDSForcePile(7);
        scn.SkipToPhase(Phase.DEPLOY);
        assertTrue(scn.DSDeployAvailable(permanent));
        scn.DSDeployCard(permanent);
        if (scn.DSHasCardChoiceAvailable(site)) scn.DSChooseCard(site);
        finishDeploy(scn);
        assertInZone(Zone.LOST_PILE, permanent);
    }

    private void finishDeploy(VirtualTableScenario scn) {
        for (int i = 0; i < 60 && !scn.AwaitingDSDeployPhaseActions(); i++) {
            if (scn.DSHasCardChoiceAvailable(scn.GetDSCard("maintenance"))) {
                scn.DSChooseCard(scn.GetDSCard("maintenance"));
            } else if (scn.DSHasCardChoiceAvailable(scn.GetDSCard("permanent"))) {
                scn.DSChooseCard(scn.GetDSCard("permanent"));
            } else if (scn.LSGetDecision() instanceof CardActionSelectionDecision) scn.LSPass();
            else if (scn.DSGetDecision() instanceof CardActionSelectionDecision) scn.DSPass();
            else fail("Unexpected deployment decision: " + scn.GetCurrentDecision().getText());
        }
        assertTrue(scn.AwaitingDSDeployPhaseActions());
    }

    @Test public void TrainedInTheJediArtsCannotBeTargetedByAlter() {
        var scn = scenario();
        var trained = scn.GetDSCard("trained");
        var alter = scn.GetLSCard("alter");
        scn.MoveCardsToSideOfTable(trained);
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("luke"));
        scn.MoveCardsToLSHand(alter);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertFalse(scn.LSCardPlayAvailable(alter));
        assertInZone(Zone.SIDE_OF_TABLE, trained);
    }

    @Test public void TrainedInTheJediArtsAllowsMaulToSubstituteForVaderInActualInterrupt() { duel("maul"); }

    @Test public void TrainedInTheJediArtsAllowsMaraToSubstituteForVaderInActualInterrupt() { duel("mara"); }

    private void duel(String substitute) {
        var scn = scenario();
        var trained = scn.GetDSCard("trained");
        var interrupt = scn.GetDSCard("duel");
        var character = scn.GetDSCard(substitute);
        var obi = scn.GetLSCard("obi");
        scn.MoveCardsToSideOfTable(trained);
        scn.MoveCardsToHand(interrupt);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), character, obi);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSDestiny(6), scn.GetDSDestiny(5));
        scn.MoveCardsToTopOfLSReserveDeck(scn.GetLSDestiny(0), scn.GetLSDestiny(1));
        assertFalse(InterruptPersonaReferences.mayTreatVaderAsMaulOrMara(scn.game(), scn.GetLSCard("enemyInterrupt")));
        assertTrue(scn.DSCardPlayAvailable(interrupt, "Initiate epic duel"));
        scn.DSPlayCard(interrupt, "Initiate epic duel");
        scn.DSChooseCard(obi);
        if (scn.DSDecisionAvailable("Choose Vader")) scn.DSChooseCard(character);
        scn.PassAllResponses();
        scn.PassAllResponses();
        scn.PassAllResponses();
        assertInZone(Zone.OUT_OF_PLAY, obi);
        assertInZone(Zone.AT_LOCATION, character);
    }

    @Test public void TrainedInTheJediArtsPermissionEndsWhenItLeavesPlay() {
        var scn = scenario();
        var trained = scn.GetDSCard("trained");
        var interrupt = scn.GetDSCard("duel");
        scn.MoveCardsToSideOfTable(trained);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("maul"), scn.GetLSCard("obi"));
        scn.MoveCardsToHand(interrupt);
        scn.SkipToDSTurn(Phase.CONTROL);
        assertTrue(scn.DSCardPlayAvailable(interrupt, "Initiate epic duel"));
        scn.MoveOutOfPlay(trained);
        assertFalse(scn.DSCardPlayAvailable(interrupt, "Initiate epic duel"));
    }

    @Test public void TrainedInTheJediArtsIncreasesActualOwnForceDrainOnlyAtOwnSkillEffect() {
        for (boolean ownSkill : List.of(false, true)) {
            var scn = scenario();
            var site = scn.GetDSStartingLocation();
            var trooper = scn.GetDSCard("trooper");
            scn.MoveCardsToSideOfTable(scn.GetDSCard("trained"));
            scn.MoveCardsToLocation(site, trooper);
            scn.AttachCardsTo(trooper, ownSkill ? scn.GetDSCard("skill") : scn.GetLSCard("skill"));
            scn.SkipToPhase(Phase.CONTROL);
            int before = scn.GetLSLostPileCount();
            scn.DSForceDrainAt(site);
            for (int i = 0; i < 40 && !scn.AwaitingDSControlPhaseActions(); i++) {
                if (scn.AwaitingLSForceLossPayment()) scn.LSPayRemainingForceLossFromReserveDeck();
                else if (scn.LSGetDecision() instanceof CardActionSelectionDecision) scn.LSPass();
                else if (scn.DSGetDecision() instanceof CardActionSelectionDecision) scn.DSPass();
                else fail("Unexpected drain decision: " + scn.GetCurrentDecision().getText());
            }
            assertTrue(scn.AwaitingDSControlPhaseActions());
            assertEquals(ownSkill ? 2 : 1, scn.GetLSLostPileCount() - before);
        }
    }

    @Test public void TrainedInTheJediArtsIncreasesEachActualBattleDestinyAtSkillEffectOnly() {
        for (boolean atSkill : List.of(false, true)) {
            var scn = scenario();
            var skillSite = scn.GetDSStartingLocation();
            var site = atSkill ? skillSite : scn.GetLSStartingLocation();
            var trooper = scn.GetDSCard("trooper");
            scn.MoveCardsToSideOfTable(scn.GetDSCard("trained"));
            scn.MoveCardsToLocation(skillSite, trooper);
            scn.AttachCardsTo(trooper, scn.GetDSCard("skill"));
            scn.MoveCardsToLocation(site, scn.GetDSCard("maul"), scn.GetLSCard("luke"));
            scn.ApplyAdHocModifier(new AddsBattleDestinyModifier(site, 1));
            scn.SkipToDSTurn(Phase.BATTLE);
            scn.EnsureDSForcePile(2);
            scn.DSInitiateBattle(site);
            scn.PassAllResponses();
            scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSDestiny(3), scn.GetDSDestiny(2));
            scn.PrepareLSDestiny(3);
            scn.SkipToPowerSegment();
            assertTrue(scn.GetCurrentDecision().getText(), scn.DSDecisionAvailable("battle destiny?"));
            scn.DSChooseYes();
            scn.PassDestinyDrawResponses();
            scn.PassDestinyDrawResponses();
            scn.PassResponses("BATTLE_DESTINY_DRAWS_COMPLETE_FOR_PLAYER");
            assertTrue(scn.GetCurrentDecision().getText(), scn.LSDecisionAvailable("battle destiny?"));
            scn.LSChooseYes();
            scn.PassDestinyDrawResponses();
            scn.PassResponses("BATTLE_DESTINY_DRAWS_COMPLETE_FOR_PLAYER");
            scn.PassResponses("BATTLE_DESTINY_DRAWS_COMPLETE_FOR_BOTH_PLAYERS");
            assertEquals(atSkill ? 7 : 5, scn.GetDSTotalDestiny());
            assertEquals(3, scn.GetLSTotalDestiny());
        }
    }
}
