package com.gempukku.swccgo.cards.set1230.light;

import com.gempukku.swccgo.common.CardSubtype;
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

import static org.junit.Assert.*;

public class Card_1230_096_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("attack", "1230_96");
            put("attack2", "1230_96");
            put("first", "1_146");
            put("second", "1_146");
            put("third", "1_146");
            put("ship", "9_73");
            put("otherShip", "9_71");
            put("capital", "9_74");
            put("republic", "14_53");
            put("tatooine", "1_127");
            put("alderaan", "1_121");
            put("luke", "1_19");
        }}, new HashMap<>() {{
            put("destroyer", "1_301");
            put("vader", "1_168");
            put("control", "9_114");
        }}, 40, 40, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void TheTimeForOurAttackHasComeStatsIconsAndKeywordsAreCorrect() {
        var scn = scenario();
        var card = scn.GetLSCard("attack").getBlueprint();
        assertEquals("The Time For Our Attack Has Come", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.IMMEDIATE, card.getCardSubtype());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
        assertTrue(card.isImmuneToCardTitle(Title.Control));
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{ add(CardType.EFFECT); }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT); add(Icon.DEATH_STAR_II); add(Icon.LEGACY_BLOCK_3);
        }});
    }

    private VirtualTableScenario ready(int fighters) {
        var scn = scenario();
        scn.StartGame();
        scn.SkipToLSTurn(Phase.ACTIVATE);
        scn.MoveLocationToTable(scn.GetLSCard("tatooine"));
        scn.MoveLocationToTable(scn.GetLSCard("alderaan"));
        if (fighters > 0)
            scn.MoveCardsToLocation(scn.GetLSCard("tatooine"), scn.GetLSCard("first"));
        if (fighters > 1)
            scn.MoveCardsToLocation(scn.GetLSCard("tatooine"), scn.GetLSCard("third"));
        scn.MoveCardsToLSHand(scn.GetLSCard("attack"), scn.GetLSCard("second"));
        scn.EnsureLSForcePile(20);
        scn.MoveCardsToTopOfLSReserveDeck(scn.GetLSCard("ship"));
        scn.SkipToPhase(Phase.DEPLOY);
        return scn;
    }

    private void reachAttackResponse(VirtualTableScenario scn) {
        for (int i = 0; i < 20; i++) {
            if (scn.LSCardPlayAvailable(scn.GetLSCard("attack"))
                    || !scn.GetCurrentDecision().getText().toLowerCase().contains("optional response"))
                return;
            scn.PlayerPass(scn.GetDecidingPlayer());
        }
    }

    private void deploySecond(VirtualTableScenario scn, String destination) {
        scn.LSDeployCard(scn.GetLSCard("second"));
        scn.LSChooseCard(scn.GetLSCard(destination));
        reachAttackResponse(scn);
    }

    private void finishDownload(VirtualTableScenario scn, boolean reserve) {
        scn.LSUseCardAction(scn.GetLSCard("attack"), reserve ? "Reserve Deck" : "Lost Pile");
        finishSelectedDownload(scn);
    }

    private void finishSelectedDownload(VirtualTableScenario scn) {
        scn.LSChooseCard(scn.GetLSCard("ship"));
        scn.LSChooseCard(scn.GetLSCard("tatooine"));
        scn.PassAllResponses();
        scn.LSChooseCard(scn.GetLSCard("ship"));
        scn.PassAllResponses();
        assertEquals(Zone.ATTACHED, scn.GetLSCard("attack").getZone());
        assertEquals(scn.GetLSCard("ship"), scn.GetLSCard("attack").getAttachedTo());
    }

    @Test
    public void TheTimeForOurAttackHasComeCannotDeployWithoutAnArrival() {
        var scn = ready(2);
        assertFalse(scn.LSCardPlayAvailable(scn.GetLSCard("attack")));
    }

    @Test
    public void TheTimeForOurAttackHasComeDeploysANewShipFromReserveAndAttachesToIt() {
        var scn = ready(1);
        int force = scn.GetLSForcePileCount();
        deploySecond(scn, "tatooine");
        assertTrue(scn.LSCardPlayAvailable(scn.GetLSCard("attack"), "Reserve Deck"));
        finishDownload(scn, true);
        assertEquals(scn.GetLSCard("tatooine"), scn.GetLSCard("ship").getAtLocation());
        assertEquals(force - 6, scn.GetLSForcePileCount());
        assertEquals(7, scn.GetPower(scn.GetLSCard("ship")));
        assertEquals(6, scn.GetHyperspeed(scn.GetLSCard("ship")));
        assertEquals(4, scn.GetPower(scn.GetLSCard("first")));
    }

    @Test
    public void TheTimeForOurAttackHasComeCanDeployTheNewShipFromLostPile() {
        var scn = ready(1);
        scn.MoveCardsToTopOfLSLostPile(scn.GetLSCard("ship"));
        deploySecond(scn, "tatooine");
        assertTrue(scn.LSCardPlayAvailable(scn.GetLSCard("attack"), "Lost Pile"));
        finishDownload(scn, false);
        assertFalse(scn.GetLSLostPile().contains(scn.GetLSCard("ship")));
    }

    @Test
    public void TheTimeForOurAttackHasComeDoesNotTriggerForTheFirstStarfighter() {
        var scn = ready(0);
        deploySecond(scn, "tatooine");
        assertFalse(scn.LSCardPlayAvailable(scn.GetLSCard("attack")));
        assertEquals(Zone.HAND, scn.GetLSCard("attack").getZone());
    }

    @Test
    public void TheTimeForOurAttackHasComeDoesNotTriggerForTheThirdStarfighter() {
        var scn = ready(2);
        deploySecond(scn, "tatooine");
        assertFalse(scn.LSCardPlayAvailable(scn.GetLSCard("attack")));
    }

    @Test
    public void TheTimeForOurAttackHasComeDoesNotUseTwoFightersAtADifferentSystem() {
        var scn = ready(2);
        deploySecond(scn, "alderaan");
        assertFalse(scn.LSCardPlayAvailable(scn.GetLSCard("attack")));
    }

    @Test
    public void TheTimeForOurAttackHasComeCannotAttachToAPreexistingStarship() {
        var scn = ready(1);
        scn.MoveCardsToLocation(scn.GetLSCard("tatooine"), scn.GetLSCard("capital"));
        deploySecond(scn, "tatooine");
        scn.LSUseCardAction(scn.GetLSCard("attack"), "Reserve Deck");
        scn.LSChooseCard(scn.GetLSCard("ship"));
        scn.LSChooseCard(scn.GetLSCard("tatooine"));
        scn.PassAllResponses();
        assertFalse(scn.LSHasCardChoiceAvailable(scn.GetLSCard("capital")));
        scn.LSChooseCard(scn.GetLSCard("ship"));
        scn.PassAllResponses();
        assertEquals(scn.GetLSCard("ship"), scn.GetLSCard("attack").getAttachedTo());
    }

    @Test
    public void TheTimeForOurAttackHasComeMayBeDeclinedWithoutDeployingAnything() {
        var scn = ready(1);
        deploySecond(scn, "tatooine");
        assertTrue(scn.LSCardPlayAvailable(scn.GetLSCard("attack")));
        scn.PassAllResponses();
        assertEquals(Zone.HAND, scn.GetLSCard("attack").getZone());
        assertEquals(Zone.RESERVE_DECK, scn.GetLSCard("ship").getZone());
    }

    private VirtualTableScenario attached() {
        var scn = scenario();
        scn.StartGame();
        scn.SkipToLSTurn(Phase.ACTIVATE);
        scn.MoveLocationToTable(scn.GetLSCard("tatooine"));
        scn.MoveLocationToTable(scn.GetLSCard("alderaan"));
        scn.MoveCardsToLocation(scn.GetLSCard("tatooine"), scn.GetLSCard("ship"));
        scn.AttachCardsTo(scn.GetLSCard("ship"), scn.GetLSCard("attack"));
        scn.EnsureLSForcePile(20);
        scn.SkipToPhase(Phase.MOVE);
        return scn;
    }

    @Test
    public void TheTimeForOurAttackHasComeBonusHyperspeedMakesTheActualMoveLegal() {
        var scn = attached();
        assertTrue(scn.LSMoveAvailable(scn.GetLSCard("ship")));
        scn.LSMoveCard(scn.GetLSCard("ship"), scn.GetLSCard("alderaan"));
        scn.PassAllResponses();
        assertEquals(scn.GetLSCard("alderaan"), scn.GetLSCard("ship").getAtLocation());
    }

    @Test
    public void TheTimeForOurAttackHasComeMakesOneActualAdditionalMoveAndPaysItsCost() {
        var scn = attached();
        scn.LSMoveCard(scn.GetLSCard("ship"), scn.GetLSCard("alderaan"));
        scn.PassAllResponses();
        int force = scn.GetLSForcePileCount();
        assertFalse(scn.LSMoveAvailable(scn.GetLSCard("ship")));
        assertTrue(scn.LSCardActionAvailable(scn.GetLSCard("attack"), "move again"));
        scn.LSUseCardAction(scn.GetLSCard("attack"), "move again");
        scn.LSChooseCard(scn.GetLSCard("tatooine"));
        scn.PassAllResponses();
        assertEquals(scn.GetLSCard("tatooine"), scn.GetLSCard("ship").getAtLocation());
        assertEquals(force - 1, scn.GetLSForcePileCount());
        assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("attack"), "move again"));
    }

    @Test
    public void TheTimeForOurAttackHasComeAdditionalMoveCannotBeUsedWithoutEnoughForce() {
        var scn = attached();
        scn.DrainLSForcePileTo(0);
        scn.LSPass();
        assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("attack"), "move again"));
    }

    @Test
    public void TheTimeForOurAttackHasComeAdditionalMoveLimitPersistsIntoLaterTurns() {
        var scn = attached();
        scn.LSUseCardAction(scn.GetLSCard("attack"), "move again");
        scn.LSChooseCard(scn.GetLSCard("alderaan"));
        scn.PassAllResponses();
        scn.SkipToDSTurn(Phase.MOVE);
        scn.SkipToLSTurn(Phase.MOVE);
        assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("attack"), "move again"));
        assertTrue(scn.LSMoveAvailable(scn.GetLSCard("ship")));
    }

    @Test
    public void TheTimeForOurAttackHasComeTriggersWhenTheSecondFighterActuallyMovesToTheSystem() {
        var scn = scenario();
        scn.StartGame();
        scn.SkipToLSTurn(Phase.ACTIVATE);
        scn.MoveLocationToTable(scn.GetLSCard("tatooine"));
        scn.MoveLocationToTable(scn.GetLSCard("alderaan"));
        scn.MoveCardsToLocation(scn.GetLSCard("tatooine"), scn.GetLSCard("first"));
        scn.MoveCardsToLocation(scn.GetLSCard("alderaan"), scn.GetLSCard("second"));
        scn.MoveCardsToLSHand(scn.GetLSCard("attack"));
        scn.EnsureLSForcePile(20);
        scn.MoveCardsToTopOfLSReserveDeck(scn.GetLSCard("ship"));
        scn.SkipToPhase(Phase.MOVE);
        scn.LSMoveCard(scn.GetLSCard("second"), scn.GetLSCard("tatooine"));
        reachAttackResponse(scn);
        assertTrue(scn.LSCardPlayAvailable(scn.GetLSCard("attack"), "Reserve Deck"));
        finishDownload(scn, true);
        assertEquals(scn.GetLSCard("tatooine"), scn.GetLSCard("ship").getAtLocation());
    }

    @Test
    public void TheTimeForOurAttackHasComeDoesNotTriggerForARepublicStarfighter() {
        var scn = ready(1);
        scn.MoveCardsToLSHand(scn.GetLSCard("republic"));
        scn.LSDeployCard(scn.GetLSCard("republic"));
        scn.LSChooseCard(scn.GetLSCard("tatooine"));
        reachAttackResponse(scn);
        assertFalse(scn.LSCardPlayAvailable(scn.GetLSCard("attack")));
    }

    private VirtualTableScenario attritionReady(int destiny) {
        var scn = scenario();
        scn.StartGame();
        scn.SkipToLSTurn(Phase.ACTIVATE);
        scn.MoveLocationToTable(scn.GetLSCard("tatooine"));
        scn.MoveCardsToLocation(scn.GetLSCard("tatooine"), scn.GetLSCard("ship"),
                scn.GetLSCard("first"), scn.GetLSCard("second"), scn.GetLSCard("third"), scn.GetDSCard("destroyer"));
        scn.BoardAsPilot(scn.GetDSCard("destroyer"), scn.GetDSCard("vader"));
        scn.AttachCardsTo(scn.GetLSCard("ship"), scn.GetLSCard("attack"));
        scn.SkipToPhase(Phase.BATTLE);
        scn.LSInitiateBattle(scn.GetLSCard("tatooine"));
        scn.PassAllResponses();
        scn.PrepareLSDestiny(0);
        scn.PrepareDSDestiny(destiny);
        scn.SkipToDamageSegment(true);
        scn.PassAllResponses();
        if (scn.AwaitingDSBattleDamagePayment()) {
            scn.DSPayBattleDamageFromCardInPlay(scn.GetDSCard("destroyer"));
            scn.PassAllResponses();
        }
        assertTrue(scn.AwaitingLSAttritionPayment());
        assertEquals(destiny, scn.GetUnpaidLSAttrition());
        return scn;
    }

    @Test
    public void TheTimeForOurAttackHasComeProtectsItsHostFromFourAttritionInAnActualBattle() {
        var scn = attritionReady(4);
        assertFalse(scn.LSHasCardChoiceAvailable(scn.GetLSCard("ship")));
        assertTrue(scn.LSHasCardChoiceAvailable(scn.GetLSCard("first")));
        scn.LSPayAttritionFromCardInPlay(scn.GetLSCard("first"));
        scn.PassAllResponses();
        assertEquals(Zone.AT_LOCATION, scn.GetLSCard("ship").getZone());
        assertEquals(Zone.ATTACHED, scn.GetLSCard("attack").getZone());
    }

    @Test
    public void TheTimeForOurAttackHasComeDoesNotProtectItsHostFromFiveAttrition() {
        var scn = attritionReady(5);
        assertTrue(scn.LSHasCardChoiceAvailable(scn.GetLSCard("ship")));
        scn.LSPayAttritionFromCardInPlay(scn.GetLSCard("ship"));
        scn.PassAllResponses();
        assertTrue(scn.GetLSLostPile().contains(scn.GetLSCard("ship")));
        assertTrue(scn.GetLSLostPile().contains(scn.GetLSCard("attack")));
    }

    private VirtualTableScenario optionalFreeReady() {
        var scn = ready(1);
        scn.ApplyAdHocModifier(new com.gempukku.swccgo.logic.modifiers.DeployCostModifier(scn.GetLSCard("attack"), 1));
        scn.ApplyAdHocModifier(new com.gempukku.swccgo.logic.modifiers.MayDeployFreeModifier(scn.GetLSCard("attack")));
        deploySecond(scn, "tatooine");
        assertTrue(scn.LSCardPlayAvailable(scn.GetLSCard("attack"), "Reserve Deck for free"));
        assertTrue(scn.LSCardPlayAvailable(scn.GetLSCard("attack"), "Reserve Deck for 1 Force"));
        return scn;
    }

    @Test
    public void TheTimeForOurAttackHasComeOptionalFreeModeStillPaysForTheDownloadedShip() {
        var scn = optionalFreeReady();
        int force = scn.GetLSForcePileCount();
        scn.LSUseCardAction(scn.GetLSCard("attack"), "Reserve Deck for free");
        finishSelectedDownload(scn);
        assertEquals(force - 4, scn.GetLSForcePileCount());
    }

    @Test
    public void TheTimeForOurAttackHasComePaidModeRetainsItsModifiedEffectCost() {
        var scn = optionalFreeReady();
        int force = scn.GetLSForcePileCount();
        scn.LSUseCardAction(scn.GetLSCard("attack"), "Reserve Deck for 1 Force");
        finishSelectedDownload(scn);
        assertEquals(force - 5, scn.GetLSForcePileCount());
    }

    @Test
    public void TheTimeForOurAttackHasComeDoesNotOfferAnUnpayableModifiedEffectCost() {
        var scn = ready(1);
        scn.ApplyAdHocModifier(new com.gempukku.swccgo.logic.modifiers.DeployCostModifier(scn.GetLSCard("attack"), 1));
        scn.DrainLSForcePileTo(2);
        deploySecond(scn, "tatooine");
        assertEquals(0, scn.GetLSForcePileCount());
        assertFalse(scn.LSCardPlayAvailable(scn.GetLSCard("attack")));
        assertEquals(Zone.HAND, scn.GetLSCard("attack").getZone());
    }
}
