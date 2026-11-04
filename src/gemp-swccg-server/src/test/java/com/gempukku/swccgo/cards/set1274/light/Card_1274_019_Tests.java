package com.gempukku.swccgo.cards.set1274.light;

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
import java.util.function.BooleanSupplier;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1274_019_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("sacrifice", "1274_19");
                    put("kenobi", "601_54");
                    put("communing", "216_24");
                    put("db327", "1_124");
                    put("controlroom", "1_123");
                    put("dunesea", "1_130");
                    put("attackrun", "2_42");
                    put("luke", "1_19");
                    put("obi", "1_21");
                }},
                new HashMap<>() {{
                    put("corridor", "1_284");
                    put("trooper", "1_194");
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

    static void passUntil(VirtualTableScenario scn, BooleanSupplier done) {
        for (int i = 0; i < 40; ++i) {
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

    private void kenobiCommuning(VirtualTableScenario scn) {
        var communing = scn.GetLSCard("communing");
        scn.MoveCardsToLSSideOfTable(communing);
        scn.StackCardsOn(communing, scn.GetLSCard("kenobi"));
    }

    @Test
    public void RebelSacrificeStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("sacrifice").getBlueprint();

        assertEquals("Rebel Sacrifice", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);
            add(Icon.LEGACY_BLOCK_7);
        }});
        assertTrue(card.isImmuneToCardTitle(Title.Alter));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void RebelSacrificeDeploysOnTableOnlyIfObiWanCommuning() {
        var scn = GetScenario();
        var sacrifice = scn.GetLSCard("sacrifice");
        scn.StartGame();
        scn.MoveCardsToHand(sacrifice);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertFalse(scn.LSDeployAvailable(sacrifice));

        kenobiCommuning(scn);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertTrue(scn.LSDeployAvailable(sacrifice));
        scn.LSDeployCard(sacrifice);
        scn.PassAllResponses();
        assertInZone(Zone.SIDE_OF_TABLE, sacrifice);
    }

    @Test
    public void RebelSacrificeYourDeathStarSitesGainOneLightSideIcon() {
        var scn = GetScenario();
        var sacrifice = scn.GetLSCard("sacrifice");
        var controlRoom = scn.GetLSCard("controlroom");
        var corridor = scn.GetDSCard("corridor");
        scn.StartGame();
        scn.MoveLocationToTable(controlRoom);
        scn.MoveLocationToTable(corridor);
        assertEquals(0, scn.GetIconCount(controlRoom, Icon.LIGHT_FORCE));
        assertEquals(0, scn.GetIconCount(corridor, Icon.LIGHT_FORCE));
        scn.MoveCardsToLSSideOfTable(sacrifice);
        assertEquals(1, scn.GetIconCount(controlRoom, Icon.LIGHT_FORCE));
        assertEquals(0, scn.GetIconCount(corridor, Icon.LIGHT_FORCE));
    }

    @Test
    public void RebelSacrificeTatooineOnObiWanIsTreatedAsDeathStar() {
        var scn = GetScenario();
        var sacrifice = scn.GetLSCard("sacrifice");
        var kenobi = scn.GetLSCard("kenobi");
        var db327 = scn.GetLSCard("db327");
        var duneSea = scn.GetLSCard("dunesea");
        scn.StartGame();
        kenobiCommuning(scn);
        scn.MoveCardsToLSSideOfTable(sacrifice);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureLSForcePile(4);
        scn.MoveCardsToTopOfOwnReserveDeck(db327, duneSea);
        assertTrue(scn.LSCardActionAvailable(kenobi, "Deploy location from Reserve Deck"));
        scn.LSUseCardAction(kenobi, "Deploy location from Reserve Deck");
        assertEquals(1, scn.LSGetSelectableCount());
        scn.LSChooseCard(db327);
        scn.PassAllResponses();
        assertTrue(db327.getZone().isInPlay());
        assertInZone(Zone.RESERVE_DECK, duneSea);
    }

    @Test
    public void RebelSacrificeStacksFirstCardLostToForceDrainAtYourDeathStarSite() {
        var scn = GetScenario();
        var sacrifice = scn.GetLSCard("sacrifice");
        var db327 = scn.GetLSCard("db327");
        var luke = scn.GetLSCard("luke");
        scn.StartGame();
        scn.MoveLocationToTable(db327);
        scn.MoveCardsToLSSideOfTable(sacrifice);
        scn.MoveCardsToLocation(db327, luke);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.LSForceDrainAvailable(db327));
        scn.LSForceDrainAt(db327);
        passUntil(scn, () -> scn.AwaitingDSForceLossPayment() || scn.AwaitingLSControlPhaseActions());
        if (scn.AwaitingDSForceLossPayment()) {
            scn.DSPayRemainingForceLossFromReserveDeck();
        }
        passUntil(scn, () -> scn.AwaitingLSControlPhaseActions());
        assertEquals(1, scn.GetStackedCards(sacrifice).size());
    }

    @Test
    public void RebelSacrificeStacksFirstCardLostToBattleDamageAtYourDeathStarSite() {
        var scn = GetScenario();
        var sacrifice = scn.GetLSCard("sacrifice");
        var db327 = scn.GetLSCard("db327");
        var obi = scn.GetLSCard("obi");
        var trooper = scn.GetDSCard("trooper");
        scn.StartGame();
        scn.MoveLocationToTable(db327);
        scn.MoveCardsToLSSideOfTable(sacrifice);
        scn.MoveCardsToLocation(db327, obi, trooper);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(db327);
        scn.PrepareLSDestiny(3);
        scn.SkipToDamageSegment(true);
        assertTrue(scn.LSWonBattle());
        passUntil(scn, () -> scn.AwaitingDSBattleDamagePayment() || scn.AwaitingLSBattlePhaseActions());
        for (int i = 0; i < 10 && scn.AwaitingDSBattleDamagePayment(); ++i) {
            scn.DSPayBattleDamageFromReserveDeck();
            passUntil(scn, () -> scn.AwaitingDSBattleDamagePayment() || scn.AwaitingLSBattlePhaseActions()
                    || scn.DSDecisionAvailable("card from battle to forfeit"));
        }
        if (scn.DSDecisionAvailable("card from battle to forfeit")) {
            scn.DSChooseCard(trooper);
            passUntil(scn, () -> scn.AwaitingLSBattlePhaseActions());
        }
        assertTrue(scn.AwaitingLSBattlePhaseActions());
        assertEquals(1, scn.GetStackedCards(sacrifice).size());
    }

    @Test
    public void RebelSacrificeCancelsAttackRun() {
        var scn = GetScenario();
        var sacrifice = scn.GetLSCard("sacrifice");
        var attackRun = scn.GetLSCard("attackrun");
        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(attackRun);
        scn.MoveCardsToHand(sacrifice);
        kenobiCommuning(scn);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSDeployCard(sacrifice);
        passUntil(scn, () -> scn.AwaitingLSDeployPhaseActions() && attackRun.getZone() != Zone.SIDE_OF_TABLE);
        assertInZone(Zone.LOST_PILE, attackRun);
    }
}
