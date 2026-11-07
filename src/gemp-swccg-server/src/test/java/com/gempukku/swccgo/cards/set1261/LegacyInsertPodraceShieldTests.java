package com.gempukku.swccgo.cards.set1261;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import static org.junit.Assert.*;

/** Shared behavior of the two Block6 insert/Podrace shields, exercised for each physical printing. */
public abstract class LegacyInsertPodraceShieldTests {
    protected abstract boolean dark();
    protected abstract String shieldId();
    protected abstract String primaryId();
    protected abstract String conversionId();
    protected abstract String title();

    protected VirtualTableScenario scenario() {
        var light = new HashMap<String,String>();
        light.put("fear", "13_5");
        light.put("insert", "1_42");
        light.put("trooper", "1_28");
        light.put("char1", "1_19");
        light.put("char2", "1_17");
        light.put("char3", "1_21");
        light.put("boonta", "11_24");
        light.put("tax", "13_4");
        light.put("didIt", "11_25");
        light.put("cantina", "1_128");
        light.put("farm", "1_132");
        light.put("nonBattleground", "4_87");
        var dark = new HashMap<String,String>();
        dark.put("fear", "13_69");
        dark.put("insert", "1_208");
        dark.put("trooper", "1_194");
        dark.put("char1", "1_168");
        dark.put("char2", "1_194");
        dark.put("char3", "1_171");
        dark.put("boonta", "11_79");
        dark.put("tax", "13_86");
        dark.put("wattos", "11_75");
        dark.put("scanner", "1_266");
        dark.put("arena", "11_94");
        var owner = dark() ? dark : light;
        owner.put("shield", shieldId());
        owner.put("otherPrinting", shieldId().equals(primaryId()) ? conversionId() : primaryId());
        return new VirtualTableScenario(light, dark, 60, 60,
            StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
            StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
            StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    protected String owner(VirtualTableScenario s) { return dark() ? s.DS : s.LS; }
    protected String enemy(VirtualTableScenario s) { return dark() ? s.LS : s.DS; }
    protected PhysicalCardImpl card(VirtualTableScenario s, boolean own, String name) {
        return dark() == own ? s.GetDSCard(name) : s.GetLSCard(name);
    }
    protected void turn(VirtualTableScenario s, boolean own, Phase phase) {
        if (dark() == own) s.SkipToDSTurn(phase); else s.SkipToLSTurn(phase);
    }
    protected void table(VirtualTableScenario s, boolean own, PhysicalCardImpl... cards) {
        if (dark() == own) s.MoveCardsToDSSideOfTable(cards); else s.MoveCardsToLSSideOfTable(cards);
    }
    protected void hand(VirtualTableScenario s, boolean own, PhysicalCardImpl... cards) {
        if (dark() == own) s.MoveCardsToDSHand(cards); else s.MoveCardsToLSHand(cards);
    }
    protected List<? extends PhysicalCard> lost(VirtualTableScenario s, boolean own) {
        return dark() == own ? s.GetDSLostPile() : s.GetLSLostPile();
    }
    protected int force(VirtualTableScenario s, boolean own) {
        return dark() == own ? s.GetDSForcePileCount() : s.GetLSForcePileCount();
    }
    protected void force(VirtualTableScenario s, boolean own, int count) {
        if (dark() == own) s.EnsureDSForcePile(count); else s.EnsureLSForcePile(count);
    }
    protected void play(VirtualTableScenario s, boolean own, PhysicalCardImpl card) {
        if (dark() == own) s.DSPlayCard(card); else s.LSPlayCard(card);
    }
    protected void use(VirtualTableScenario s, boolean own, PhysicalCardImpl card, String text) {
        if (dark() == own) s.DSUseCardAction(card, text); else s.LSUseCardAction(card, text);
    }
    protected VirtualTableScenario ready(boolean shield) {
        var s = scenario(); s.StartGame(); turn(s, true, Phase.ACTIVATE); s.SkipToPhase(Phase.CONTROL);
        if (shield) table(s, true, card(s, true, "shield"));
        return s;
    }

    protected void checkFinalPrintedStatsIconsAndKeywordsAreCorrect() {
        var s = scenario(); var b = card(s, true, "shield").getBlueprint();
        assertEquals(title(), b.getTitle()); assertTrue(b.hasVirtualSuffix());
        assertEquals(dark() ? Side.DARK : Side.LIGHT, b.getSide());
        assertEquals(0, b.getDestiny(), s.epsilon); assertEquals(Uniqueness.UNIQUE, b.getUniqueness());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, b.getExpansionSet()); assertEquals(Rarity.V, b.getRarity());
        s.BlueprintCardTypeCheck(b, new ArrayList<>() {{ add(CardType.DEFENSIVE_SHIELD); }});
        s.BlueprintKeywordCheck(b, new ArrayList<Keyword>());
        s.BlueprintIconCheck(b, new ArrayList<>() {{ add(Icon.DEFENSIVE_SHIELD); add(Icon.REFLECTIONS_III); add(Icon.LEGACY_BLOCK_6); }});
    }

    protected void checkBothPrintingLookupsRetainPhysicalImageIdsAndOneCanonicalRuleClass() {
        var s = scenario(); var a = card(s, true, "shield"); var b = card(s, true, "otherPrinting");
        assertEquals(shieldId(), a.getBlueprintId(true)); assertNotEquals(a.getBlueprintId(true), b.getBlueprintId(true));
        assertEquals(a.getBlueprint().getClass(), b.getBlueprint().getClass());
        assertEquals(a.getBlueprint().getTitle(), b.getBlueprint().getTitle());
        assertEquals(primaryId(), new SwccgCardBlueprintLibrary().getBaseBlueprintId(shieldId()));
    }

    protected void checkShieldActuallyPlaysFromTheStartingEffectForFree() {
        var s = ready(false); var fear = card(s, true, "fear"); var shield = card(s, true, "shield");
        table(s, true, fear); s.StackCardsOn(fear, shield); s.SkipToPhase(Phase.DEPLOY);
        int before = force(s, true); use(s, true, fear, "Defensive Shield");
        if (dark()) s.DSChooseCard(shield); else s.LSChooseCard(shield);
        s.PassAllResponses(); assertEquals(Zone.SIDE_OF_TABLE, shield.getZone()); assertEquals(before, force(s, true));
    }

    protected void checkASecondPrintingCannotBePlayedAlongsideTheSameShieldTitle() {
        var s = ready(true); var fear = card(s, true, "fear");
        table(s, true, fear); s.StackCardsOn(fear, card(s, true, "otherPrinting")); s.SkipToPhase(Phase.DEPLOY);
        assertFalse(dark() ? s.DSCardActionAvailable(fear, "Defensive Shield") : s.LSCardActionAvailable(fear, "Defensive Shield"));
    }

    protected void checkBothPrintingsCountTowardTheExistingOutsideDeckShieldCap() throws com.gempukku.swccgo.game.DeckInvalidException {
        var library = new SwccgCardBlueprintLibrary();
        var deck = new com.gempukku.swccgo.logic.vo.SwccgDeck("shield-cap");
        deck.addCard(dark() ? "13_69" : "13_5");
        for (int n = 0; n < 10; n++) deck.addCardOutsideDeck(shieldId());
        com.gempukku.swccgo.game.formats.DefaultSwccgFormat.validateOriginalStartingEffectOutsideDeckShieldCap(library, deck);
        deck.addCardOutsideDeck(shieldId());
        try {
            com.gempukku.swccgo.game.formats.DefaultSwccgFormat.validateOriginalStartingEffectOutsideDeckShieldCap(library, deck);
            fail("Eleven physical shields exceed the original Starting Effect cap");
        } catch (com.gempukku.swccgo.game.DeckInvalidException expected) { assertTrue(expected.getMessage().contains("More defensive shields")); }
    }

    protected void checkAnOpponentsActualInsertPlayIsCanceled() { assertInsertCanceled(false); }
    protected void checkAnOwnersActualInsertPlayIsAlsoCanceled() { assertInsertCanceled(true); }
    private void assertInsertCanceled(boolean ownInsert) {
        var s = ready(true); var insert = card(s, ownInsert, "insert");
        hand(s, ownInsert, insert); turn(s, ownInsert, Phase.DEPLOY); play(s, ownInsert, insert); s.PassAllResponses();
        assertTrue(lost(s, ownInsert).contains(insert)); assertFalse(insert.isInserted());
    }

    private void activateOne(VirtualTableScenario s) {
        if (dark()) { s.DSChooseAction("Activate Force"); s.DSDecided("1"); }
        else { s.LSChooseAction("Activate Force"); s.LSDecided("1"); }
        if (s.GetCurrentDecision().getText().contains("amount of Force to allow opponent"))
            s.PlayerDecided(enemy(s), "1");
        s.PassAllResponses();
    }
    private int activatePastInsert(boolean shield) {
        var s = scenario(); s.StartGame(); turn(s, true, Phase.ACTIVATE);
        if (shield) table(s, true, card(s, true, "shield"));
        var insert = card(s, false, "insert");
        var cover = dark() ? s.GetDSFiller(1) : s.GetLSFiller(1);
        // Activating the normal top card exposes the pre-existing insert through the real engine reveal rule.
        if (dark()) s.MoveCardsToTopOfDSReserveDeck(insert, cover); else s.MoveCardsToTopOfLSReserveDeck(insert, cover);
        insert.setInserted(true); int before = force(s, true); activateOne(s);
        assertTrue(lost(s, false).contains(insert));
        boolean canContinue = dark() ? s.DSActionAvailable("Activate Force") : s.LSActionAvailable("Activate Force");
        assertEquals(shield, canContinue);
        if (shield) activateOne(s);
        return force(s, true) - before;
    }
    protected void checkCancelingARevealedInsertAllowsActualActivationToContinue() {
        int stopped = activatePastInsert(false); int continued = activatePastInsert(true);
        assertTrue("Shield must prevent the insert from stopping activation", continued > stopped);
    }

    protected void checkShieldDoesNotCancelAnOrdinaryCharacterDeployment() {
        var s = ready(true); var trooper = card(s, true, "trooper"); hand(s, true, trooper);
        s.SkipToPhase(Phase.DEPLOY); force(s, true, 5); int before = force(s, true);
        var site = dark() ? s.GetDSStartingLocation() : s.GetLSStartingLocation();
        if (dark()) { s.DSDeployCard(trooper); s.DSChooseCard(site); }
        else { s.LSDeployCard(trooper); s.LSChooseCard(site); }
        s.PassAllResponses(); assertEquals(site, trooper.getAtLocation()); assertEquals(before - 1, force(s, true));
    }

    protected void checkActualPodraceFromOwnersCardProtectsLossAndOpponentsRetrieval() { finishRace(true, true); }
    protected void checkActualPodraceFromOpponentsCardAlsoProtectsLossAndRetrieval() { finishRace(true, false); }
    protected void checkWithoutTheShieldTheSameRaceLosesForceAndRetrievalCanBeCanceled() { finishRace(false, true); }

    private void finishRace(boolean shield, boolean ownBoonta) {
        var s = ready(shield); var arena = s.GetDSCard("arena"); var boonta = card(s, ownBoonta, "boonta");
        s.MoveLocationToTable(arena); s.AttachCardsTo(arena, boonta); table(s, true, card(s, true, "tax"));
        s.SkipToPhase(Phase.DEPLOY); turn(s, true, Phase.CONTROL);
        use(s, true, boonta, "Initiate a Podrace"); s.PassAllResponses(); assertTrue(GameConditions.isDuringPodrace(s.game()));
        // A valid already-drawn race history totals25; the printed Boonta card will finish the race in Move phase.
        for (int n = 3; n <= 7; n++) {
            var destiny = dark() ? s.GetLSDestiny(n) : s.GetDSDestiny(n);
            s.StackCardsOn(arena, destiny); destiny.setRaceDestinyForPlayer(enemy(s));
        }
        assertEquals(25, s.game().getModifiersQuerying().getHighestRaceTotal(s.gameState(), enemy(s)), s.epsilon);
        for (int n = 1; n <= 6; n++) {
            var filler = dark() ? s.GetLSFiller(n) : s.GetDSFiller(n);
            if (dark()) s.MoveCardsToTopOfLSLostPile(filler); else s.MoveCardsToTopOfDSLostPile(filler);
        }
        s.DrainForcePileTo(enemy(s), 0); assertEquals(0, force(s, false)); int ownLost = lost(s, true).size(); int enemyLost = lost(s, false).size();
        s.SkipToPhase(Phase.MOVE);
        for (int step = 0; step < 40; step++) {
            s.PassAllResponses();
            if (!GameConditions.isDuringPodrace(s.game())) break;
            if (dark() ? s.AwaitingDSForceLossPayment() : s.AwaitingLSForceLossPayment()) {
                if (dark()) s.DSPayRemainingForceLossFromReserveDeck(); else s.LSPayRemainingForceLossFromReserveDeck();
                continue;
            }
            var decision = s.GetCurrentDecision(); String text = decision.getText(); String player = s.GetDecidingPlayer();
            if (text.contains("to proceed with Force retrieval?")) {
                if (player.equals(s.DS)) s.DSChooseNo(); else s.LSChooseNo(); continue;
            }
            if (text.contains("Choose effect to be performed first")) { s.PlayerDecided(player, "0"); continue; }
            String[] ids = decision.getDecisionParameters().get("cardId");
            if (text.contains("Used Pile") && ids != null && ids.length > 0) { s.PlayerDecided(player, ids[0]); continue; }
            fail("Unexpected Podrace decision: " + text);
        }
        assertFalse("The real Podrace must finish", GameConditions.isDuringPodrace(s.game()));
        assertEquals(ownLost + (shield ? 0 : 6), lost(s, true).size());
        assertEquals(enemyLost - (shield ? 6 : 0), lost(s, false).size());
    }
}
