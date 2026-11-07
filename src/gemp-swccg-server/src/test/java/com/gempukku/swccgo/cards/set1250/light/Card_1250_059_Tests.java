package com.gempukku.swccgo.cards.set1250.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

public class Card_1250_059_Tests {
    private static final String DEPLOY = "Deploy Jedi Archives or a battleground planet location";

    private VirtualTableScenario scenario(String pairId) {
        return new VirtualTableScenario(new HashMap<>() {{
            put("plans", "1250_59"); put("nightfall", "1210_132"); put("ordinary", "1_60");
            put("archives", "1117_4"); put("system", "12_78"); put("forest", "8_74"); put("onTableForest", "8_74");
            put("council", "12_76"); put("sector", "5_85"); put("otherSite", "8_72");
            put("copy1", pairId); put("copy2", pairId); put("clone1", "1116_8"); put("clone2", "1116_8");
            put("interrupt1", "1_70"); put("interrupt2", "1_70");
        }}, new HashMap<>() {{
            put("monnok", "2_135"); put("alter", "1_234"); put("vader", "1_168");
        }}, 35, 35, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    private VirtualTableScenario scenario() { return scenario("1_28"); }

    @Test
    public void AJedisPlansMetadataMatchesOfficialEffect() {
        var s = scenario(); var bp = s.GetLSCard("plans").getBlueprint();
        assertEquals("A Jedi's Plans", bp.getTitle()); assertFalse(bp.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, bp.getUniqueness()); assertEquals(3, bp.getDestiny(), s.epsilon);
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_5, bp.getExpansionSet());
        assertEquals("The guidance of experienced Jedi allowed Luke to confront Vader.", bp.getLore());
        s.BlueprintCardTypeCheck(bp, new ArrayList<>(List.of(CardType.EFFECT)));
        s.BlueprintIconCheck(bp, new ArrayList<>(List.of(Icon.EFFECT, Icon.SPECIAL_EDITION, Icon.LEGACY_BLOCK_5)));
        s.BlueprintKeywordCheck(bp, new ArrayList<>());
    }

    private void attach(VirtualTableScenario s) {
        s.AttachCardsTo(s.GetLSStartingLocation(), s.GetLSCard("plans"));
    }

    private void deployPlans(VirtualTableScenario s) {
        var plans = s.GetLSCard("plans"); var site = s.GetLSStartingLocation();
        var system = s.GetLSCard("system"); s.MoveLocationToTable(system);
        s.MoveCardsToHand(plans); s.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(s.LSCardPlayAvailable(plans)); s.LSDeployCard(plans);
        assertTrue(s.LSHasCardChoiceAvailable(site)); assertFalse(s.LSHasCardChoiceAvailable(system));
        s.LSChooseCard(site); s.PassAllResponses(); assertEquals(site, plans.getAttachedTo());
    }

    @Test
    public void AJedisPlansActuallyDeploysOnSiteAndCancelsExistingNightfall() {
        var s = scenario(); s.StartGame();
        var nightfall = s.GetLSCard("nightfall"); s.MoveCardsToLSSideOfTable(nightfall);
        deployPlans(s);
        if (s.LSDecisionAvailable("Choose card to cancel")) s.LSChooseCard(nightfall);
        s.PassAllResponses();
        assertTrue(s.GetLSLostPile().contains(nightfall));
    }

    @Test
    public void AJedisPlansCancelsActualLaterNightfallPlay() {
        var s = scenario(); s.StartGame(); attach(s); var nightfall = s.GetLSCard("nightfall");
        s.MoveCardsToHand(nightfall); s.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(s.LSCardPlayAvailable(nightfall)); s.LSDeployCard(nightfall); s.PassAllResponses();
        assertTrue(s.GetLSLostPile().contains(nightfall));
        assertEquals(s.GetLSStartingLocation(), s.GetLSCard("plans").getAttachedTo());
    }

    private void startMonnok(VirtualTableScenario s, int hand, boolean withOtherPairs, boolean active) {
        s.StartGame();
        if (active) attach(s);
        var monnok = s.GetDSCard("monnok"); s.MoveCardsToHand(monnok);
        s.SkipToDSTurn(Phase.ACTIVATE);
        s.DSActivateForceCheat(4);
        for (var card : List.copyOf(s.GetLSHand())) s.MoveCardsToTopOfLSReserveDeck((PhysicalCardImpl) card);
        s.MoveCardsToHand(s.GetLSCard("copy1"), s.GetLSCard("copy2"));
        if (withOtherPairs) {
            s.MoveCardsToHand(s.GetLSCard("clone1"), s.GetLSCard("clone2"),
                    s.GetLSCard("interrupt1"), s.GetLSCard("interrupt2"));
        }
        for (int i = 1; s.GetLSHandCount() < hand; i++) s.MoveCardsToHand(s.GetLSFiller(i));
        assertEquals(hand, s.GetLSHandCount());
        s.SkipToPhase(Phase.CONTROL);
        assertTrue(s.DSPlayLostInterruptAvailable(monnok)); s.DSPlayLostInterrupt(monnok);
        for (int i = 0; i < 40 && !s.GetDSLostPile().contains(monnok); i++) {
            if (s.LSDecisionAvailable("Choose card to put on Lost Pile")) {
                var ids = s.LSGetADParamAsList("cardId"); assertFalse(ids.isEmpty());
                s.LSDecided(ids.getFirst());
            } else if (s.DSDecisionAvailable("Opponent's hand")) {
                s.DSPass();
            } else {
                s.PassResponses();
            }
        }
        assertTrue(s.GetDSLostPile().contains(monnok));
    }

    private void checkMonnok(String pair, int hand, boolean protectedPair) {
        var s = scenario(pair);
        startMonnok(s, hand, false, true);
        for (String key : List.of("copy1", "copy2")) {
            assertEquals(protectedPair, s.GetLSHand().contains(s.GetLSCard(key)));
            assertEquals(!protectedPair, s.GetLSLostPile().contains(s.GetLSCard(key)));
        }
    }

    @Test
    public void AJedisPlansProtectsNonuniqueCharacterAtSevenCards() { checkMonnok("1_28", 7, true); }

    @Test
    public void AJedisPlansAtEightProtectsNoncloneCharactersButNotClonesOrInterrupts() {
        var s = scenario(); startMonnok(s, 8, true, true);
        for (String key : List.of("copy1", "copy2")) assertTrue(s.GetLSHand().contains(s.GetLSCard(key)));
        for (String key : List.of("clone1", "clone2", "interrupt1", "interrupt2"))
            assertTrue(key, s.GetLSLostPile().contains(s.GetLSCard(key)));
    }

    @Test
    public void AJedisPlansDoesNotProtectAtNineCards() { checkMonnok("1_28", 9, false); }

    @Test
    public void AJedisPlansProtectsNonuniqueVehiclesFromActualMonnok() { checkMonnok("3_69", 8, true); }

    @Test
    public void AJedisPlansProtectsNonuniqueStarshipsFromActualMonnok() { checkMonnok("1_140", 8, true); }

    @Test
    public void AJedisPlansProtectsNonuniqueWeaponsFromActualMonnok() { checkMonnok("1_153", 8, true); }

    @Test
    public void AJedisPlansProtectsNonuniqueDevicesFromActualMonnok() { checkMonnok("1_35", 8, true); }

    @Test
    public void AJedisPlansDoesNotProtectUniqueCharacter() { checkMonnok("1_19", 8, false); }

    @Test
    public void WithoutActivePlansMonnokActuallyLosesOrdinaryCopiesAtEight() {
        var s = scenario(); startMonnok(s, 8, false, false);
        assertTrue(s.GetLSLostPile().contains(s.GetLSCard("copy1")));
        assertTrue(s.GetLSLostPile().contains(s.GetLSCard("copy2")));
    }

    private boolean canDownload(VirtualTableScenario s) {
        return s.GetADParam(s.LS, "cardId") != null && s.GetADParam(s.LS, "actionText") != null
                && s.LSCardActionAvailable(s.GetLSCard("plans"), DEPLOY);
    }

    private void passDecision(VirtualTableScenario s) {
        String prompt = s.GetCurrentDecision().getText();
        if (prompt.equals("You have not activated Force. Do you want to Pass?")) {
            if (s.DSDecisionAvailable(prompt)) s.DSChooseYes(); else s.LSChooseYes();
        } else {
            assertTrue(prompt, prompt.contains("action or Pass") || prompt.contains("Optional responses")
                    || prompt.contains("RECIRCULATED"));
            if (s.DSDecisionAvailable(prompt)) s.DSPass(); else s.LSPass();
        }
    }

    private VirtualTableScenario downloadSetup(int force) {
        var s = scenario(); s.StartGame(); attach(s);
        s.MoveLocationToTable(s.GetLSCard("onTableForest"));
        s.SkipToLSTurn(Phase.ACTIVATE);
        var candidates = List.of(s.GetLSCard("archives"), s.GetLSCard("system"), s.GetLSCard("forest"),
                s.GetLSCard("council"), s.GetLSCard("sector"), s.GetLSCard("otherSite"));
        s.MoveCardsToTopOfLSReserveDeck(candidates.toArray(PhysicalCardImpl[]::new));
        int current = s.GetLSForcePileCount();
        if (current > force) s.UseForceCheat(s.LS, current - force);
        if (current < force) {
            // Fund with ordinary Reserve cards after placing the search candidates, so neither step removes the other fixture.
            var fuel = s.GetLSReserveDeck().stream().filter(card -> !candidates.contains(card)).limit(force - current)
                    .map(card -> (PhysicalCardImpl) card).toArray(PhysicalCardImpl[]::new);
            assertEquals(force - current, fuel.length);
            s.MoveCardsToTopOfLSReserveDeck(fuel); s.LSActivateForceCheat(fuel.length);
        }
        for (int i = 0; i < 20 && s.GetCurrentPhase() == Phase.ACTIVATE; i++) passDecision(s);
        assertEquals(Phase.CONTROL, s.GetCurrentPhase());
        assertEquals(force, s.GetLSForcePileCount());
        return s;
    }

    private void checkChoice(VirtualTableScenario s, String card, boolean expected) {
        int index = s.LSGetADParamAsList("blueprintId").indexOf(s.GetLSCard(card).getBlueprintId(true));
        assertTrue(card + " missing from Reserve Deck", index >= 0);
        assertEquals(card, Boolean.toString(expected), s.LSGetADParam("selectable")[index]);
    }

    private void download(VirtualTableScenario s, String target, boolean audit) {
        var card = s.GetLSCard(target); int force = s.GetLSForcePileCount();
        assertTrue(canDownload(s)); s.LSUseCardAction(s.GetLSCard("plans"), DEPLOY); s.PassAllResponses();
        if (audit) {
            checkChoice(s, "archives", true); checkChoice(s, "system", true); checkChoice(s, "otherSite", true);
            checkChoice(s, "forest", false); checkChoice(s, "council", false); checkChoice(s, "sector", false);
        }
        s.LSChooseCard(card);
        if (s.LSDecisionAvailable("On which side")) s.LSChooseOption("Right");
        s.PassAllResponses(); assertEquals(Zone.LOCATIONS, card.getZone());
        assertEquals(force - 2, s.GetLSForcePileCount());
    }

    @Test
    public void AJedisPlansDownloadsNonBattlegroundJediArchivesForTwoForceAndReshuffles() {
        var s = downloadSetup(2); var marker = s.GetLSCard("sector");
        s.gameState().markCardsRevealedFromPile(List.of(marker)); assertTrue(s.gameState().isCardRevealedFromPile(marker));
        download(s, "archives", true);
        assertFalse(s.gameState().isCardRevealedFromPile(marker));
    }

    @Test
    public void AJedisPlansCanDownloadBattlegroundPlanetSystem() { download(downloadSetup(2), "system", true); }

    @Test
    public void AJedisPlansCannotDownloadWithOneForce() { assertFalse(canDownload(downloadSetup(1))); }

    @Test
    public void AJedisPlansOncePerTurnSpansPhasesAndResetsNextTurn() {
        var s = downloadSetup(4);
        int oldTurn = s.gameState().getPlayersLatestTurnNumber(s.LS);
        download(s, "archives", false); s.SkipToPhase(Phase.MOVE);
        assertEquals(oldTurn, s.gameState().getPlayersLatestTurnNumber(s.LS));
        assertTrue(s.AwaitingLSMovePhaseActions()); assertFalse(canDownload(s));
        for (int i = 0; i < 100 && !(s.GetCurrentPlayer().equals(s.DS)
                && s.GetCurrentPhase() == Phase.CONTROL); i++) passDecision(s);
        assertEquals(s.DS, s.GetCurrentPlayer()); assertEquals(Phase.CONTROL, s.GetCurrentPhase());
        if (s.DSAnyDecisionsAvailable()) s.DSPass();
        download(s, "system", false);
    }

    @Test
    public void AJedisPlansImmuneToActualAlterWhileOrdinaryEffectIsCanceled() {
        var s = scenario(); s.StartGame(); attach(s); var site = s.GetLSStartingLocation();
        var plans = s.GetLSCard("plans"); var ordinary = s.GetLSCard("ordinary");
        s.AttachCardsTo(site, ordinary); s.MoveCardsToLocation(s.GetDSStartingLocation(), s.GetDSCard("vader"));
        var alter = s.GetDSCard("alter"); s.MoveCardsToHand(alter); s.SkipToDSTurn(Phase.CONTROL);
        s.PrepareDSDestiny(3);
        assertTrue(s.DSCardPlayAvailable(alter)); s.DSPlayCard(alter, "cancel Effect");
        if (s.DSDecisionAvailable("Choose Effect")) {
            assertFalse(s.DSHasCardChoiceAvailable(plans)); assertTrue(s.DSHasCardChoiceAvailable(ordinary));
            s.DSChooseCard(ordinary);
        }
        if (s.DSDecisionAvailable("Choose a highest-ability character")) s.DSChooseCard(s.GetDSCard("vader"));
        s.PassDestinyDrawResponses(); s.PassAllResponses();
        assertEquals(site, plans.getAttachedTo()); assertTrue(s.GetLSLostPile().contains(ordinary));
    }
}
