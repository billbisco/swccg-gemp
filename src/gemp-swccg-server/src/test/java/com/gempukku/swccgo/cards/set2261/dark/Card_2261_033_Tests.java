package com.gempukku.swccgo.cards.set2261.dark;

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

/** Pre-errata Dark Waters & Krayt Dragon Bones (Sep 2009 slip), a version of 1261_33. */
public class Card_2261_033_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>(Map.of("luke", "1_19", "mynock", "4_4")),
                new HashMap<>(Map.of("waters", "2261_33", "pit", "6_166", "rancor", "6_139",
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
        assertTrue(bp.isLegacy());
        var version = bp.getLegacyVersion();
        assertEquals("1261_33", version.currentPrintingId());
        assertEquals("", version.label());
        assertEquals(java.time.LocalDate.of(2009, 9, 4), version.printedOn());
        assertEquals("Dark Waters & Krayt Dragon Bones",
                com.gempukku.swccgo.logic.GameUtils.getFullName(bp));
        assertEquals("Deploy on a site with \"Waters\" in game text. During your battle phase, if on Rancor Pit, may relocate a captive from Audience Chamber to here. If opponent's character here was just eaten, retrieve 3 Force. If a creature here is lost, owner loses Force equal to half its printed deploy cost (round up). Once per turn, if a creature here is about to draw destiny for ferocity, may instead use its destiny number. May use 1 Force to ▼ a non-[Selective] ferocious creature here. (Immune to Alter.)", bp.getGameText());
    }

    private VirtualTableScenario watersSitesScenario() {
        return new VirtualTableScenario(new HashMap<>(Map.of("luke", "1_19")),
                new HashMap<>(Map.of("waters", "2261_33", "pit", "6_166", "arena", "1240_52", "chamber", "6_162",
                        "krayt", "7_211", "womprat", "7_215", "ysalamir", "1240_17", "trooper", "1_194")),
                26, 26, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void deploysOnASiteWithWatersInGameTextAndNowhereElse() {
        var scn = watersSitesScenario();
        var waters = scn.GetDSCard("waters");
        var pit = scn.GetDSCard("pit");
        var arena = scn.GetDSCard("arena");
        var chamber = scn.GetDSCard("chamber");
        scn.StartGame();
        scn.MoveLocationToTable(pit);
        scn.MoveLocationToTable(arena);
        scn.MoveLocationToTable(chamber);
        var game = scn.game();
        var target = com.gempukku.swccgo.filters.Filters.gameTextContains("Waters");
        assertTrue(target.accepts(game, pit));
        assertTrue(target.accepts(game, arena));
        assertFalse(target.accepts(game, chamber));
        assertFalse(target.accepts(game, scn.GetDSStartingLocation()));
        scn.MoveCardsToDSHand(waters);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.EnsureDSForcePile(5);
        scn.DSDeployCard(waters);
        assertTrue(scn.DSHasCardChoiceAvailable(pit));
        assertTrue(scn.DSHasCardChoiceAvailable(arena));
        assertFalse(scn.DSHasCardChoiceAvailable(chamber));
        assertFalse(scn.DSHasCardChoiceAvailable(scn.GetDSStartingLocation()));
        scn.DSChooseCard(arena);
        scn.PassAllResponses();
        assertSame(arena, waters.getAttachedTo());
    }

    private static List<String> selectableBlueprints(VirtualTableScenario scn) {
        var params = scn.DSGetDecision().getDecisionParameters();
        var ids = params.get("blueprintId");
        var selectable = params.get("selectable");
        List<String> result = new ArrayList<>();
        for (int i = 0; ids != null && i < ids.length; i++)
            if (selectable == null || "true".equals(selectable[i]))
                result.add(ids[i]);
        return result;
    }

    private static final String DEPLOY_CREATURE = "Deploy creature from Reserve Deck";

    @Test(timeout = 15000)
    public void mayUseOneForceToDeployANonSelectiveFerociousCreatureHereFromReserveDeck() {
        var scn = watersSitesScenario();
        var waters = scn.GetDSCard("waters");
        var arena = scn.GetDSCard("arena");
        var krayt = scn.GetDSCard("krayt");
        var womprat = scn.GetDSCard("womprat");
        var ysalamir = scn.GetDSCard("ysalamir");
        scn.StartGame();
        scn.MoveLocationToTable(arena);
        scn.AttachCardsTo(arena, waters);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("trooper"));
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.EnsureDSForcePile(12);
        scn.MoveCardsToTopOfDSReserveDeck(krayt, womprat, ysalamir);
        int force = scn.GetDSForcePileCount();
        assertTrue(scn.DSCardActionAvailable(waters, DEPLOY_CREATURE));
        scn.DSUseCardAction(waters, DEPLOY_CREATURE);
        scn.PassAllResponses();
        scn.PassCardAndForceUseResponses();
        var offered = selectableBlueprints(scn);
        assertTrue(offered.toString(), offered.contains(krayt.getBlueprintId(true)));
        assertFalse("selective creature offered", offered.contains(womprat.getBlueprintId(true)));
        assertFalse("non-ferocious creature offered", offered.contains(ysalamir.getBlueprintId(true)));
        scn.DSChooseCard(krayt);
        scn.PassAllResponses();
        assertSame(arena, scn.game().getModifiersQuerying().getLocationThatCardIsAt(scn.gameState(), krayt));
        assertEquals(force - 1 - krayt.getBlueprint().getDeployCost().intValue(), scn.GetDSForcePileCount());
    }

    @Test(timeout = 15000)
    public void creatureDeployFromReserveNeedsYourDeployPhase() {
        var battle = watersSitesScenario();
        var waters = battle.GetDSCard("waters");
        battle.StartGame();
        battle.MoveLocationToTable(battle.GetDSCard("arena"));
        battle.AttachCardsTo(battle.GetDSCard("arena"), waters);
        battle.MoveCardsToLocation(battle.GetDSStartingLocation(), battle.GetDSCard("trooper"));
        battle.SkipToDSTurn(Phase.BATTLE);
        battle.EnsureDSForcePile(5);
        assertFalse(dsHas(battle, waters, DEPLOY_CREATURE));
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
                int index = sources.indexOf(inPlay ? "2261_33" : "6_166");
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
        return new VirtualTableScenario(new HashMap<>(Map.of("luke", "1_19", "rebel", "1_28")),
                new HashMap<>(Map.of("waters", "2261_33", "pit", "6_166", "chamber", "6_162", "wampa", "3_93",
                        "womprat", "7_215", "trooper", "1_194", "trooper2", "1_194", "rancor", "6_139",
                        "imperialcity", "7_277", "arena", "1240_52")),
                26, 26, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
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
    public void inYourBattlePhaseOnRancorPitMayRelocateACaptiveFromAudienceChamberEvenWithYourPresenceHere() {
        var scn = fullScenario();
        captiveSetup(scn, true, Phase.BATTLE);
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
    public void captiveRelocationIsOncePerBattlePhase() {
        var scn = fullScenario();
        var pit = scn.GetDSCard("pit");
        var chamber = scn.GetDSCard("chamber");
        var waters = scn.GetDSCard("waters");
        var luke = scn.GetLSCard("luke");
        var rebel = scn.GetLSCard("rebel");
        scn.StartGame();
        scn.MoveLocationToTable(pit);
        scn.MoveLocationToTable(chamber);
        scn.AttachCardsTo(pit, waters);
        scn.MoveCardsToLocation(chamber, scn.GetDSCard("trooper"), scn.GetDSCard("trooper2"));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), luke, rebel);
        scn.CaptureCardWith(scn.GetDSCard("trooper"), luke);
        scn.CaptureCardWith(scn.GetDSCard("trooper2"), rebel);
        scn.SkipToDSTurn(Phase.BATTLE);
        assertTrue(scn.DSCardActionAvailable(waters, RELOCATE));
        scn.DSUseCardAction(waters, RELOCATE);
        scn.DSChooseCard(luke);
        for (int i = 0; i < 20 && !scn.AwaitingDSBattlePhaseActions(); i++) {
            if (scn.LSReleaseDecisionAvailable())
                scn.LSChooseRally();
            else if (scn.GetCurrentDecision().getText().toLowerCase().contains("optional")
                    || scn.LSDecisionAvailable("Choose Battle action"))
                scn.PlayerPass(scn.GetDecidingPlayer());
            else
                fail("Unexpected decision: " + scn.GetCurrentDecision().getText());
        }
        assertTrue(scn.AwaitingDSBattlePhaseActions());
        assertSame(pit, scn.game().getModifiersQuerying().getLocationThatCardIsAt(scn.gameState(), luke));
        assertTrue(rebel.isCaptive());
        assertSame(chamber, scn.game().getModifiersQuerying().getLocationThatCardIsAt(scn.gameState(), rebel));
        assertFalse(scn.DSCardActionAvailable(waters, RELOCATE));
    }

    @Test(timeout = 15000)
    public void captiveRelocationNeedsYourBattlePhaseAndThisOnRancorPit() {
        var scn = fullScenario();
        var arena = scn.GetDSCard("arena");
        var chamber = scn.GetDSCard("chamber");
        var waters = scn.GetDSCard("waters");
        scn.StartGame();
        scn.MoveLocationToTable(arena);
        scn.MoveLocationToTable(chamber);
        scn.AttachCardsTo(arena, waters);
        scn.MoveCardsToLocation(chamber, scn.GetDSCard("trooper"));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("luke"));
        scn.CaptureCardWith(scn.GetDSCard("trooper"), scn.GetLSCard("luke"));
        scn.SkipToDSTurn(Phase.BATTLE);
        assertTrue(scn.GetLSCard("luke").isCaptive());
        assertFalse(dsHas(scn, waters, RELOCATE));

        var deployPhase = fullScenario();
        captiveSetup(deployPhase, false, Phase.DEPLOY);
        assertFalse(deployPhase.DSCardActionAvailable(deployPhase.GetDSCard("waters"), RELOCATE));
    }

    private static boolean dsHas(VirtualTableScenario scn, PhysicalCardImpl card, String text) {
        try {
            return scn.DSCardActionAvailable(card, text);
        } catch (IllegalStateException e) {
            return false;
        }
    }

    private Float attackAndMaybeSubstitute(VirtualTableScenario scn, boolean atPit, boolean substitute) {
        var pit = scn.GetDSCard("pit");
        var waters = scn.GetDSCard("waters");
        var wampa = scn.GetDSCard("rancor");
        var luke = scn.GetLSCard("luke");
        var where = atPit ? pit : scn.GetDSCard("imperialcity");
        scn.StartGame();
        scn.MoveLocationToTable(pit);
        scn.MoveLocationToTable(scn.GetDSCard("imperialcity"));
        scn.AttachCardsTo(pit, waters);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.MoveCardsToLocation(where, wampa, luke);
        scn.PrepareLSDestiny(1);
        scn.PrepareDSDestiny(6);
        scn.carryOutEffectInPhaseActionByPlayer(scn.DS, new InitiateAttackNonCreatureAction(wampa));
        if (scn.DSGetDecision() != null && scn.DSHasCardChoicesAvailable(luke))
            scn.DSChooseCard(luke);
        boolean offered = false;
        for (int i = 0; i < 60 && !(scn.gameState().getAttackState() != null
                && scn.gameState().getAttackState().getFerocityDestinyTotal(wampa) != null); i++) {
            if (dsHas(scn, waters, "for ferocity")) {
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
        assertEquals(String.join("\n", scn.gameState().getLastMessages()) + "\nDS=" + scn.DSGetDecision() + " LS=" + scn.LSGetDecision(), atPit, offered);
        return scn.gameState().getAttackState().getFerocityDestinyTotal(wampa);
    }

    @Test(timeout = 20000)
    public void creatureHereMayUseItsDestinyNumberInsteadOfDrawingFerocityDestiny() {
        var scn = fullScenario();
        assertEquals(1f, attackAndMaybeSubstitute(scn, true, true), scn.epsilon);
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

    private VirtualTableScenario bothVersionsScenario() {
        return new VirtualTableScenario(new HashMap<>(Map.of("luke", "1_19")),
                new HashMap<>(Map.of("old", "2261_33", "current", "1261_33", "pit", "6_166", "trooper", "1_194")),
                26, 26, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void isTheSameUniqueCardAsTheCurrentVersion() {
        for (boolean currentOnTable : List.of(true, false)) {
            for (boolean otherOut : List.of(false, true)) {
                var scn = bothVersionsScenario();
                var pit = scn.GetDSCard("pit");
                var onTable = scn.GetDSCard(currentOnTable ? "current" : "old");
                var inHand = scn.GetDSCard(currentOnTable ? "old" : "current");
                scn.StartGame();
                scn.MoveLocationToTable(pit);
                if (otherOut)
                    scn.AttachCardsTo(pit, onTable);
                scn.MoveCardsToDSHand(inHand);
                scn.SkipToDSTurn(Phase.DEPLOY);
                scn.EnsureDSForcePile(10);
                assertEquals("current on table=" + currentOnTable + ", other version out=" + otherOut,
                        !otherOut, dsHas(scn, inHand, "Deploy"));
            }
        }
    }
}
