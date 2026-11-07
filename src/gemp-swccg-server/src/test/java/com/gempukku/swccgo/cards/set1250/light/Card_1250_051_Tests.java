package com.gempukku.swccgo.cards.set1250.light;

import com.gempukku.swccgo.common.Agenda;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class Card_1250_051_Tests {
    private static final String TAKE = "Take battle destiny card into hand";
    private static final String DOWNLOAD = "Deploy a battleground except Endor system";
    private static final String EMPEROR = "Target a character present";

    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("mothma", "1250_51"); put("senate", "12_75"); put("chamber", "12_76");
            put("endor", "8_68"); put("endorSite", "8_69"); put("naboo", "12_78");
            put("luke", "1_19"); put("leia", "1_17"); put("bail", "1116_1");
        }}, new HashMap<>() {{
            put("emperor", "1106_46"); put("aks", "12_97"); put("lott", "12_110");
            put("vader", "1_168"); put("trooper", "1_194");
        }}, 35, 35, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void SenatorMonMothmaMatchesFinal51EPrinting() {
        var s = scenario(); var bp = s.GetLSCard("mothma").getBlueprint();
        assertEquals("Senator Mon Mothma", bp.getTitle()); assertFalse(bp.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, bp.getUniqueness());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_5, bp.getExpansionSet());
        assertEquals(1, bp.getDestiny(), s.epsilon); assertEquals(3, bp.getDeployCost(), s.epsilon);
        assertEquals(3, bp.getPower(), s.epsilon); assertEquals(4, bp.getAbility(), s.epsilon);
        assertEquals(3, bp.getPolitics(), s.epsilon); assertEquals(6, bp.getForfeit(), s.epsilon);
        assertEquals(List.of(Persona.MON_MOTHMA), bp.getPersonas().stream().toList());
        assertEquals("Native of Chandrila. Leader of the Alliance. A former member of the Imperial Senate "
                + "where she was a formidable opponent of then-Senator Palpatine. Friend of Bail Organa.", bp.getLore());
        s.BlueprintCardTypeCheck(bp, new ArrayList<>(List.of(CardType.REBEL, CardType.REPUBLIC)));
        s.BlueprintIconCheck(bp, new ArrayList<>(List.of(Icon.REBEL, Icon.REPUBLIC, Icon.ENDOR, Icon.LEGACY_BLOCK_5)));
        s.BlueprintKeywordCheck(bp, new ArrayList<>(List.of(Keyword.FEMALE, Keyword.SENATOR, Keyword.LEADER)));
    }

    @Test
    public void SenatorMonMothmaProvidesOrderAndPeace() {
        var s = scenario(); s.StartGame(); var mothma = s.GetLSCard("mothma");
        s.MoveCardsToLocation(s.GetLSStartingLocation(), mothma);
        assertEquals(Set.of(Agenda.ORDER, Agenda.PEACE),
                Set.copyOf(s.game().getModifiersQuerying().getAgendas(s.gameState(), mothma)));
    }

    private VirtualTableScenario emperor(boolean mothmaHere) {
        var s = scenario(); s.StartGame(); var senate = s.GetLSCard("senate");
        s.MoveLocationToTable(senate);
        s.MoveCardsToLocation(mothmaHere ? senate : s.GetLSStartingLocation(), s.GetLSCard("mothma"));
        s.MoveCardsToLocation(senate, s.GetLSCard("bail"), s.GetLSCard("luke"),
                s.GetDSCard("emperor"), s.GetDSCard("aks"));
        s.SkipToDSTurn(Phase.CONTROL);
        if (s.GetDSForcePileCount() == 0) s.DSActivateForceCheat(2);
        s.MoveCardsToHand(s.GetDSCard("trooper"));
        s.PrepareDSDestiny(5);
        assertTrue(s.DSCardActionAvailable(s.GetDSCard("emperor"), EMPEROR));
        s.DSUseCardAction(s.GetDSCard("emperor"), EMPEROR); s.PassAllResponses();
        assertTrue(s.DSDecisionAvailable("Choose character"));
        return s;
    }

    @Test
    public void EmperorCannotTargetEitherPlayersPoliticiansHereButCanMakeLukeLost() {
        var s = emperor(true);
        var choices = s.DSGetCardChoices();
        for (var politician : List.of(s.GetLSCard("mothma"), s.GetLSCard("bail"), s.GetDSCard("aks"))) {
            assertFalse(politician.toString(), choices.contains(Integer.toString(politician.getCardId())));
        }
        var luke = s.GetLSCard("luke");
        assertTrue(choices.contains(Integer.toString(luke.getCardId())));
        resolveEmperor(s, luke);
        assertTrue(s.GetLSLostPile().contains(luke));
    }

    @Test
    public void EmperorCanTargetPoliticianElsewhere() {
        var s = emperor(false); var bail = s.GetLSCard("bail");
        assertTrue(s.DSGetCardChoices().contains(Integer.toString(bail.getCardId())));
        resolveEmperor(s, bail);
        assertTrue(s.GetLSLostPile().contains(bail));
    }

    private void resolveEmperor(VirtualTableScenario s, PhysicalCardImpl target) {
        int initialLoss = s.GetDSLostPileCount();
        s.DSChooseCard(target); s.PassAllResponses();
        assertTrue(s.DSDecisionAvailable("Choose Force to lose"));
        s.DSChooseCard(s.GetDSCard("trooper"));
        s.PassAllResponses(); s.PassDestinyDrawResponses(); s.PassAllResponses();
        assertEquals(initialLoss + 1, s.GetDSLostPileCount());
    }

    private boolean lightAction(VirtualTableScenario s, String label) {
        return s.GetADParam(s.LS, "cardId") != null && s.GetADParam(s.LS, "actionText") != null
                && s.LSCardActionAvailable(s.GetLSCard("mothma"), label);
    }

    private VirtualTableScenario battle(boolean atSenate, String opposition) {
        var s = scenario(); s.StartGame(); var senate = s.GetLSCard("senate"); var site = s.GetLSStartingLocation();
        s.MoveLocationToTable(senate);
        s.MoveCardsToLocation(atSenate ? senate : s.GetDSStartingLocation(), s.GetLSCard("mothma"));
        if (opposition != null) s.MoveCardsToLocation(senate, s.GetDSCard(opposition));
        s.MoveCardsToLocation(site, s.GetLSCard("bail"), s.GetLSCard("leia"), s.GetDSCard("vader"));
        s.LSActivateForceCheat(2);
        s.LSStartBattleAndSkipToWeaponsSegment(site);
        s.PassWeaponsSegmentActions(); s.PassResponses("BEFORE_BATTLE_DESTINY_DRAWS");
        assertTrue("Light must be offered two real battle destiny draws", s.GetLSBattleDestinyCount() >= 2);
        return s;
    }

    private void passUntilTakeOrAfterDraw(VirtualTableScenario s) {
        for (int i = 0; i < 30 && !lightAction(s, TAKE); i++) {
            String prompt = s.GetCurrentDecision().getText();
            if (!prompt.contains("DESTINY_DRAWN")) return;
            if (s.LSDecisionAvailable(prompt)) s.LSPass(); else s.DSPass();
        }
    }

    private void beginDraws(VirtualTableScenario s) {
        s.MoveCardsToTopOfLSReserveDeck(s.GetLSDestiny(4), s.GetLSDestiny(3));
        assertTrue(s.LSDecisionAvailable("2 battle destiny?"));
        s.LSChooseYes();
        advanceToDrawResponse(s);
    }

    private void advanceToDrawResponse(VirtualTableScenario s) {
        s.PassResponses("COST_TO_DRAW_DESTINY_CARD"); s.PassResponses("ABOUT_TO_DRAW_DESTINY_CARD");
        passUntilTakeOrAfterDraw(s);
    }

    private void finishTake(VirtualTableScenario s, PhysicalCardImpl destiny) {
        assertTrue(s.GetCurrentDecision().getText(), lightAction(s, TAKE));
        int hand = s.GetLSHandCount();
        s.LSUseCardAction(s.GetLSCard("mothma"), TAKE); s.PassDestinyDrawResponses();
        assertEquals(Zone.HAND, destiny.getZone()); assertEquals(hand + 1, s.GetLSHandCount());
    }

    private void finishLightDraws(VirtualTableScenario s, int count, int total) {
        var battle = s.gameState().getBattleState();
        for (int i = 0; i < 40 && battle.getNumBattleDestinyDrawn(s.LS) < count; i++) {
            String prompt = s.GetCurrentDecision().getText();
            assertFalse("Mothma is offered again before finishing the same turn", lightAction(s, TAKE));
            assertTrue(prompt, prompt.contains("Optional responses"));
            s.PlayerPass(s.GetDecidingPlayer());
        }
        assertEquals(count, battle.getNumBattleDestinyDrawn(s.LS));
        assertEquals(total, s.GetLSTotalDestiny());
    }

    @Test
    public void SenateMajorityTakesPhysicalBattleDestinyAndKeepsValueButOnlyOnceThisTurn() {
        var s = battle(true, null);
        beginDraws(s);
        finishTake(s, s.GetLSDestiny(3));
        advanceToDrawResponse(s);
        assertFalse(lightAction(s, TAKE)); s.PassDestinyDrawResponses();
        finishLightDraws(s, 2, 7);
        assertNotEquals(Zone.HAND, s.GetLSDestiny(4).getZone());
    }

    @Test
    public void DecliningFirstBattleDestinyDoesNotConsumeTheUse() {
        var s = battle(true, null);
        beginDraws(s); assertTrue(lightAction(s, TAKE));
        s.LSPass(); s.PassDestinyDrawResponses(); assertNotEquals(Zone.HAND, s.GetLSDestiny(3).getZone());
        advanceToDrawResponse(s);
        finishTake(s, s.GetLSDestiny(4));
        finishLightDraws(s, 2, 7);
    }

    @Test
    public void LaterDarkTurnAllowsANewLightBattleDestinyRecovery() {
        var s = battle(true, null);
        beginDraws(s); finishTake(s, s.GetLSDestiny(3)); finishLightDraws(s, 2, 7);
        s.PassResponses("BATTLE_DESTINY_DRAWS_COMPLETE_FOR_PLAYER");
        assertTrue(s.DSDecisionAvailable("battle destiny?")); s.DSChooseNo();
        for (int i = 0; i < 45 && s.gameState().getBattleState() != null; i++) {
            String prompt = s.GetCurrentDecision().getText();
            if (s.DSDecisionAvailable("card from battle to forfeit")) s.DSChooseCard(s.GetDSCard("vader"));
            else {
                assertTrue(prompt, prompt.contains("Optional responses") || prompt.contains("action to play or Pass"));
                s.PlayerPass(s.GetDecidingPlayer());
            }
        }
        assertNull(s.gameState().getBattleState());
        assertTrue(s.GetDSLostPile().contains(s.GetDSCard("vader")));

        var secondSite = s.GetDSStartingLocation();
        s.MoveCardsToLocation(secondSite, s.GetLSCard("luke"), s.GetDSCard("trooper"));
        advanceToDarkControl(s);
        s.DSActivateForceCheat(2);
        s.SkipToPhase(Phase.BATTLE);
        s.DSInitiateBattle(secondSite);
        s.PassAllResponses();
        s.SkipToPowerSegment();
        if (s.DSDecisionAvailable("battle destiny?")) s.DSChooseNo();
        s.PassResponses("BATTLE_DESTINY_DRAWS_COMPLETE_FOR_PLAYER");
        assertTrue(s.GetCurrentDecision().getText(), s.LSDecisionAvailable("1 battle destiny?"));
        s.PrepareLSDestiny(2); s.LSChooseYes(); advanceToDrawResponse(s);
        finishTake(s, s.GetLSDestiny(2)); finishLightDraws(s, 1, 2);
    }

    @Test
    public void TiedSenateDoesNotOfferTheBattleDestinyAction() { assertNoTake(battle(true, "aks")); }

    @Test
    public void OpposingSenateMajorityDoesNotOfferTheBattleDestinyAction() { assertNoTake(battle(true, "lott")); }

    @Test
    public void AwayFromSenateDoesNotOfferTheBattleDestinyAction() { assertNoTake(battle(false, null)); }

    private void assertNoTake(VirtualTableScenario s) {
        beginDraws(s);
        assertFalse(lightAction(s, TAKE)); s.PassDestinyDrawResponses();
        advanceToDrawResponse(s);
        assertFalse(lightAction(s, TAKE)); s.PassDestinyDrawResponses();
        assertNotEquals(Zone.HAND, s.GetLSDestiny(3).getZone());
        assertNotEquals(Zone.HAND, s.GetLSDestiny(4).getZone());
        assertEquals(7, s.GetLSTotalDestiny());
    }

    @Test
    public void SenatorMonMothmaDoesNotTakeOpponentsBattleDestiny() {
        var s = battle(true, null);
        assertTrue(s.LSDecisionAvailable("battle destiny?"));
        s.LSChooseNo(); s.PassResponses("BATTLE_DESTINY_DRAWS_COMPLETE_FOR_PLAYER");
        var darkDestiny = s.GetDSDestiny(3);
        s.PrepareDSDestiny(3);
        assertTrue(s.DSDecisionAvailable("battle destiny?"));
        s.DSChooseYes();
        s.PassResponses("COST_TO_DRAW_DESTINY_CARD"); s.PassResponses("ABOUT_TO_DRAW_DESTINY_CARD");
        passUntilTakeOrAfterDraw(s);
        assertFalse(lightAction(s, TAKE)); s.PassDestinyDrawResponses();
        assertNotEquals(Zone.HAND, darkDestiny.getZone()); assertEquals(4, s.GetDSTotalDestiny());
    }

    private VirtualTableScenario reserve(boolean darkTurn) {
        var s = scenario(); s.StartGame();
        s.MoveCardsToLocation(s.GetLSStartingLocation(), s.GetLSCard("mothma"));
        if (darkTurn) s.SkipToDSTurn(Phase.CONTROL); else s.SkipToLSTurn(Phase.DEPLOY);
        stockReserve(s);
        if (darkTurn && s.DSAnyDecisionsAvailable()) s.DSPass();
        return s;
    }

    private void stockReserve(VirtualTableScenario s) {
        s.MoveCardsToTopOfLSReserveDeck(s.GetLSCard("endor"), s.GetLSCard("endorSite"),
                s.GetLSCard("chamber"), s.GetLSCard("naboo"));
    }

    private void selectable(VirtualTableScenario s, String label, boolean expected) {
        var ids = s.LSGetADParamAsList("blueprintId");
        int index = ids.indexOf(s.GetLSCard(label).getBlueprintId(true));
        assertTrue(label + " should be in Reserve", index >= 0);
        assertEquals(label, Boolean.toString(expected), s.LSGetADParam("selectable")[index]);
    }

    private void download(VirtualTableScenario s, String label, boolean audit) {
        int force = s.GetLSForcePileCount();
        assertTrue(lightAction(s, DOWNLOAD));
        s.LSUseCardAction(s.GetLSCard("mothma"), DOWNLOAD); s.PassAllResponses();
        if (audit) {
            selectable(s, "endor", false); selectable(s, "chamber", false);
            selectable(s, "endorSite", true); selectable(s, "naboo", true);
        }
        var card = s.GetLSCard(label);
        s.LSChooseCard(card); s.PassAllResponses();
        if (s.LSDecisionAvailable("On which side")) s.LSChooseOption("Right");
        s.PassAllResponses();
        assertEquals(Zone.LOCATIONS, card.getZone()); assertEquals(force, s.GetLSForcePileCount());
    }

    @Test
    public void ReserveSearchExcludesOnlyEndorSystemAndNonBattlegroundsAndReshuffles() {
        var s = reserve(false); var marker = s.GetLSCard("endor");
        s.gameState().markCardsRevealedFromPile(List.of(marker));
        assertTrue(s.gameState().isCardRevealedFromPile(marker));
        download(s, "endorSite", true);
        assertFalse(s.gameState().isCardRevealedFromPile(marker));
    }

    @Test
    public void ReserveSearchCannotDeployDuringDarkTurn() {
        var s = reserve(true);
        assertFalse(lightAction(s, DOWNLOAD));
        s.SkipToLSTurn(Phase.CONTROL);
        // Activation can move the previous top cards out of Reserve Deck.
        stockReserve(s);
        s.SkipToPhase(Phase.DEPLOY);
        download(s, "naboo", false);
    }

    @Test
    public void ReserveSearchRemainsExhaustedOnLaterTurn() {
        var s = reserve(false); download(s, "naboo", false);
        s.SkipToPhase(Phase.MOVE); assertTrue(s.AwaitingLSMovePhaseActions()); assertFalse(lightAction(s, DOWNLOAD));
        advanceToDarkControl(s);
        if (s.DSAnyDecisionsAvailable()) s.DSPass();
        assertFalse(lightAction(s, DOWNLOAD));
        s.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(s.AwaitingLSDeployPhaseActions());
        assertFalse(lightAction(s, DOWNLOAD));
        assertEquals(Zone.RESERVE_DECK, s.GetLSCard("endorSite").getZone());
    }

    private void advanceToDarkControl(VirtualTableScenario s) {
        for (int i = 0; i < 100 && !(s.GetCurrentPlayer().equals(s.DS) && s.GetCurrentPhase() == Phase.CONTROL); i++) {
            String prompt = s.GetCurrentDecision().getText();
            if (prompt.equals("You have not activated Force. Do you want to Pass?")) {
                if (s.DSDecisionAvailable(prompt)) s.DSChooseYes(); else s.LSChooseYes();
            } else {
                assertTrue(prompt, prompt.contains("action or Pass") || prompt.contains("Optional responses")
                        || prompt.contains("RECIRCULATED"));
                if (s.DSDecisionAvailable(prompt)) s.DSPass(); else s.LSPass();
            }
        }
        assertEquals(s.DS, s.GetCurrentPlayer()); assertEquals(Phase.CONTROL, s.GetCurrentPhase());
    }
}
