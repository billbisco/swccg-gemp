package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.logic.actions.InitiateAttackNonCreatureAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.TriggeringResultEffect;
import com.gempukku.swccgo.logic.timing.results.EatenResult;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class Card_1261_033_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>(Map.of("luke", "1_19", "mynock", "4_4")),
                new HashMap<>(Map.of("waters", "1261_33", "pit", "6_166", "rancor", "6_139",
                        "dragon", "7_211", "trooper", "1_194")),
                26, 26, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void printedComboIdentityAndAlterImmunity() {
        var scn = scenario();
        var bp = scn.GetDSCard("waters").getBlueprint();
        assertEquals("Dark Waters & Krayt Dragon Bones", bp.getTitle());
        assertFalse(bp.hasVirtualSuffix());
        assertEquals(Side.DARK, bp.getSide());
        assertEquals(1, bp.getDestiny(), scn.epsilon);
        assertEquals(Uniqueness.UNIQUE, bp.getUniqueness());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, bp.getExpansionSet());
        assertEquals(Rarity.V, bp.getRarity());
        assertEquals(PlayCardZoneOption.ATTACHED, bp.getSinglePlayCardZoneOption());
        assertTrue(bp.getTitles().containsAll(List.of(Title.Dark_Waters, Title.Krayt_Dragon_Bones)));
        scn.BlueprintCardTypeCheck(bp, new ArrayList<>(List.of(CardType.EFFECT)));
        scn.BlueprintIconCheck(bp, new ArrayList<>(List.of(Icon.EFFECT, Icon.A_NEW_HOPE, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(bp, new ArrayList<>(List.of(Keyword.DEPLOYS_ON_LOCATION)));
        assertTrue(bp.isImmuneToCardTitle(Title.Alter));
    }

    @Test
    public void deploysOnlyOnTheRancorPit() {
        var scn = scenario();
        var waters = scn.GetDSCard("waters");
        var pit = scn.GetDSCard("pit");
        scn.StartGame();
        scn.MoveLocationToTable(pit);
        scn.MoveCardsToDSHand(waters);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.EnsureDSForcePile(5);
        scn.DSDeployCard(waters);
        assertFalse(scn.DSHasCardChoiceAvailable(scn.GetDSStartingLocation()));
        if (scn.DSHasCardChoiceAvailable(pit))
            scn.DSChooseCard(pit);
        scn.PassAllResponses();
        assertSame(pit, waters.getAttachedTo());
    }

    @Test(timeout = 10000)
    public void lossAtThePitChargesTheActualCreatureOwnerRoundedUpAndRemoteLossDoesNot() {
        for (String key : List.of("dragon", "mynock", "rancor")) {
            var scn = scenario();
            var pit = scn.GetDSCard("pit");
            var waters = scn.GetDSCard("waters");
            boolean lightOwns = key.equals("mynock");
            boolean atPit = !key.equals("rancor");
            var creature = lightOwns ? scn.GetLSCard(key) : scn.GetDSCard(key);
            scn.StartGame();
            scn.MoveLocationToTable(pit);
            scn.AttachCardsTo(pit, waters);
            openControl(scn);
            scn.MoveCardsToLocation(atPit ? pit : scn.GetDSStartingLocation(), creature);
            scn.EnsureDSForcePile(10);
            scn.EnsureLSForcePile(10);
            int beforeDS = scn.GetDSLostPileCount();
            int beforeLS = scn.GetLSLostPileCount();
            var action = new TopLevelGameTextAction(waters, waters.getCardId());
            scn.DSExecuteAdHocEffect(waters, new LoseCardFromTableEffect(action, creature));
            resolveLoss(scn);
            int extra = atPit ? (int) Math.ceil(creature.getBlueprint().getDeployCost() / 2.0) : 0;
            assertEquals(beforeDS + (lightOwns ? 0 : 1 + extra), scn.GetDSLostPileCount());
            assertEquals(beforeLS + (lightOwns ? 1 + extra : 0), scn.GetLSLostPileCount());
        }
    }

    @Test(timeout = 10000)
    public void actualEatenResultAtPitAddsExactlyThreeRetrievalBeyondTheLocationText() {
        for (boolean inPlay : List.of(false, true)) {
            var scn = scenario();
            var pit = scn.GetDSCard("pit");
            var waters = scn.GetDSCard("waters");
            var rancor = scn.GetDSCard("rancor");
            var luke = scn.GetLSCard("luke");
            scn.StartGame();
            scn.MoveLocationToTable(pit);
            if (inPlay)
                scn.AttachCardsTo(pit, waters);
            openControl(scn);
            scn.MoveCardsToLocation(pit, rancor, luke);
            scn.MoveCardsToTopOfDSLostPile(scn.GetDSReserveDeck().stream().limit(13)
                    .map(PhysicalCardImpl.class::cast).toArray(PhysicalCardImpl[]::new));
            int before = scn.GetDSLostPileCount();
            int forfeit = scn.GetForfeit(luke);
            var action = new TopLevelGameTextAction(rancor, rancor.getCardId());
            scn.DSExecuteAdHocEffect(rancor, new TriggeringResultEffect(action,
                    new EatenResult(luke, null, null, forfeit, false, rancor, pit)));
            for (int i = 0; i < 8 && scn.DSDecisionAvailable("Required responses"); i++) {
                var choices = scn.DSGetDecision().getDecisionParameters();
                var sources = java.util.Arrays.asList(choices.get("blueprintId"));
                int index = sources.indexOf(inPlay ? "1261_33" : "6_166");
                if (index < 0)
                    index = sources.indexOf("6_166");
                assertTrue("Missing native retrieval choice: " + sources, index >= 0);
                scn.DSDecided(choices.get("actionId")[index]);
            }
            scn.PassAllResponses();
            assertEquals(before - forfeit - (inPlay ? 3 : 0), scn.GetDSLostPileCount());
        }
    }

    private VirtualTableScenario fullScenario() {
        return new VirtualTableScenario(new HashMap<>(Map.of("luke", "1_19")),
                new HashMap<>(Map.of("waters", "1261_33", "pit", "6_166", "chamber", "6_162",
                        "wampa", "3_93", "womprat", "7_215", "trooper", "1_194", "trooper2", "1_194")),
                26, 26, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test(timeout = 15000)
    public void nonSelectiveFerociousCreatureMayDeployHereButSelectiveOneAndNoWatersMayNot() {
        for (boolean inPlay : List.of(true, false)) {
            var scn = fullScenario();
            var pit = scn.GetDSCard("pit");
            var wampa = scn.GetDSCard("wampa");
            var womprat = scn.GetDSCard("womprat");
            scn.StartGame();
            scn.MoveLocationToTable(pit);
            if (inPlay)
                scn.AttachCardsTo(pit, scn.GetDSCard("waters"));
            scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("trooper"));
            scn.MoveCardsToDSHand(wampa, womprat);
            scn.SkipToDSTurn(Phase.DEPLOY);
            scn.EnsureDSForcePile(10);
            assertEquals(inPlay, scn.DSDeployAvailable(wampa));
            assertFalse(scn.DSDeployAvailable(womprat));
            if (inPlay) {
                scn.DSDeployCard(wampa);
                if (scn.DSHasCardChoiceAvailable(pit))
                    scn.DSChooseCard(pit);
                scn.PassAllResponses();
                assertSame(pit, scn.game().getModifiersQuerying().getLocationThatCardIsAt(scn.gameState(), wampa));
            }
        }
    }

    private void captiveSetup(VirtualTableScenario scn, boolean presenceAtPit, Phase phase) {
        var pit = scn.GetDSCard("pit");
        var chamber = scn.GetDSCard("chamber");
        scn.StartGame();
        scn.MoveLocationToTable(pit);
        scn.MoveLocationToTable(chamber);
        scn.AttachCardsTo(pit, scn.GetDSCard("waters"));
        scn.MoveCardsToLocation(chamber, scn.GetDSCard("trooper"));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("luke"));
        scn.CaptureCardWith(scn.GetDSCard("trooper"), scn.GetLSCard("luke"));
        if (presenceAtPit)
            scn.MoveCardsToLocation(pit, scn.GetDSCard("trooper2"));
        scn.SkipToDSTurn(phase);
    }

    private static final String RELOCATE = "Relocate captive";

    @Test(timeout = 15000)
    public void inYourBattlePhaseWithNoPresenceHereMayRelocateACaptiveFromAudienceChamber() {
        var scn = fullScenario();
        captiveSetup(scn, false, Phase.BATTLE);
        var waters = scn.GetDSCard("waters");
        var luke = scn.GetLSCard("luke");
        assertTrue(luke.isCaptive());
        assertTrue(scn.DSCardActionAvailable(waters, RELOCATE));
        scn.DSUseCardAction(waters, RELOCATE);
        if (scn.DSHasCardChoiceAvailable(luke))
            scn.DSChooseCard(luke);
        scn.PassAllResponses();
        assertSame(scn.GetDSCard("pit"), scn.game().getModifiersQuerying().getLocationThatCardIsAt(scn.gameState(), luke));
    }

    @Test(timeout = 15000)
    public void captiveRelocationNeedsYourBattlePhaseAndNoPresenceHere() {
        var withPresence = fullScenario();
        captiveSetup(withPresence, true, Phase.BATTLE);
        assertFalse(withPresence.DSCardActionAvailable(withPresence.GetDSCard("waters"), RELOCATE));

        var deployPhase = fullScenario();
        captiveSetup(deployPhase, false, Phase.DEPLOY);
        assertFalse(deployPhase.DSCardActionAvailable(deployPhase.GetDSCard("waters"), RELOCATE));
    }

    private Float attackAndMaybeSubstitute(VirtualTableScenario scn, boolean atPit, boolean substitute) {
        var pit = scn.GetDSCard("pit");
        var waters = scn.GetDSCard("waters");
        var wampa = scn.GetDSCard("wampa");
        var luke = scn.GetLSCard("luke");
        var where = atPit ? pit : scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveLocationToTable(pit);
        scn.AttachCardsTo(pit, waters);
        scn.MoveCardsToLocation(where, wampa, luke);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.PrepareLSDestiny(1);
        scn.PrepareDSDestiny(6);
        scn.carryOutEffectInPhaseActionByPlayer(scn.DS, new InitiateAttackNonCreatureAction(wampa));
        if (scn.DSGetDecision() != null && scn.DSHasCardChoicesAvailable(luke))
            scn.DSChooseCard(luke);
        boolean offered = false;
        for (int i = 0; i < 60 && scn.gameState().getAttackState() != null
                && scn.gameState().getAttackState().getFerocityDestinyTotal(wampa) == null; i++) {
            if (scn.DSCardActionAvailable(waters, "for ferocity")) {
                offered = true;
                if (substitute) {
                    scn.DSUseCardAction(waters, "for ferocity");
                    continue;
                }
            }
            if (scn.DSDecisionAvailable("weapons segment") || scn.LSDecisionAvailable("weapons segment")
                    || scn.DSDecisionAvailable("Choose weapons") || scn.LSDecisionAvailable("Choose weapons")) {
                scn.PassWeaponsSegmentActions();
                continue;
            }
            scn.PlayerPass(scn.GetDecidingPlayer());
        }
        assertEquals(atPit, offered);
        return scn.gameState().getAttackState().getFerocityDestinyTotal(wampa);
    }

    @Test(timeout = 20000)
    public void creatureHereMayUseItsDestinyNumberInsteadOfDrawingFerocityDestiny() {
        var scn = fullScenario();
        assertEquals(3f, attackAndMaybeSubstitute(scn, true, true), scn.epsilon);
    }

    @Test(timeout = 20000)
    public void ferocitySubstitutionIsOptionalAndOnlyForCreaturesHere() {
        var declined = fullScenario();
        assertEquals(6f, attackAndMaybeSubstitute(declined, true, false), declined.epsilon);
        var elsewhere = fullScenario();
        assertEquals(6f, attackAndMaybeSubstitute(elsewhere, false, false), elsewhere.epsilon);
    }

    private void openControl(VirtualTableScenario scn) {
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("trooper"));
        scn.SkipToDSTurn(Phase.CONTROL);
    }

    private void resolveLoss(VirtualTableScenario scn) {
        for (int i = 0; i < 30; i++) {
            if (scn.DSDecisionAvailable("Choose Force to lose")) {
                scn.DSChooseCard(scn.GetTopOfDSForcePile());
            } else if (scn.LSDecisionAvailable("Choose Force to lose")) {
                scn.LSChooseCard(scn.GetTopOfLSForcePile());
            } else if (scn.GetCurrentDecision().getText().toLowerCase().contains("optional response")) {
                scn.PassAllResponses();
            } else {
                return;
            }
        }
        fail("Loss window did not settle");
    }
}
