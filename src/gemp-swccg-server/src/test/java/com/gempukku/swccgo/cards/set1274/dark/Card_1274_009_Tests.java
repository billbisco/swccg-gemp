package com.gempukku.swccgo.cards.set1274.dark;

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
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BooleanSupplier;

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1274_009_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("han", "1_11");
                    put("luke", "1_19");
                    put("nabrun", "1_97");   // Nabrun Leids
                    put("bacta", "3_32");    // Bacta Tank: places a just-lost character here instead of Lost Pile
                }},
                new HashMap<>() {{
                    put("wayt", "1274_9");
                    put("vader", "1_168");
                }},
                30, 30,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    private static boolean dsCan(VirtualTableScenario scn, PhysicalCardImpl card, String text) {
        try {
            return scn.DSCardActionAvailable(card, text);
        } catch (IllegalStateException e) {
            return false;
        }
    }

    private static boolean lsCan(VirtualTableScenario scn, PhysicalCardImpl card, String text) {
        try {
            return scn.LSCardActionAvailable(card, text);
        } catch (IllegalStateException e) {
            return false;
        }
    }

    private static boolean passUntil(VirtualTableScenario scn, BooleanSupplier condition) {
        for (int i = 0; i < 30; i++) {
            if (condition.getAsBoolean()) {
                return true;
            }
            if (scn.DSGetDecision() != null) {
                scn.DSPass();
            } else if (scn.LSGetDecision() != null) {
                scn.LSPass();
            } else {
                return false;
            }
        }
        return condition.getAsBoolean();
    }

    @Test
    public void WhereAreYouTakingThisThingStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("wayt").getBlueprint();

        assertEquals("Where Are You Taking This ... Thing?", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.IMMEDIATE, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{ add(CardType.EFFECT); }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);
            add(Icon.LEGACY_BLOCK_7);
        }});
        assertTrue(card.isImmuneToCardTitle(Title.Control));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void WhereAreYouTakingThisThingDeploysWhenOpponentDrewDestinyForNabrunLeidsAndStacksItFaceDown() {
        var scn = GetScenario();
        var wayt = scn.GetDSCard("wayt");
        var nabrun = scn.GetLSCard("nabrun");
        var han = scn.GetLSCard("han");
        var from = scn.GetLSStartingLocation();
        var to = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToDSHand(wayt);
        scn.MoveCardsToLSHand(nabrun);
        scn.MoveCardsToLocation(from, han);
        scn.EnsureLSForcePile(5);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.PrepareLSDestiny(0);
        scn.LSPlayCard(nabrun);
        scn.LSChooseCard(from);
        scn.LSChooseCard(to);
        scn.LSChooseCard(han);
        assertTrue(passUntil(scn, () -> dsCan(scn, wayt, null)));
        scn.DSPlayCard(wayt);
        scn.PassAllResponses();

        assertTrue(wayt.getZone().isInPlay());
        assertTrue(scn.IsStackedOn(wayt, nabrun));
        assertEquals(Zone.STACKED_FACE_DOWN, nabrun.getZone());
        assertAtLocation(from, han);
    }

    @Test
    public void WhereAreYouTakingThisThingCancelsNabrunLeidsWhileOnTable() {
        var scn = GetScenario();
        var wayt = scn.GetDSCard("wayt");
        var nabrun = scn.GetLSCard("nabrun");
        var han = scn.GetLSCard("han");
        var from = scn.GetLSStartingLocation();
        var to = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(wayt);
        scn.MoveCardsToLSHand(nabrun);
        scn.MoveCardsToLocation(from, han);
        scn.EnsureLSForcePile(5);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.PrepareLSDestiny(0);
        scn.LSPlayCard(nabrun);
        scn.LSChooseCard(from);
        scn.LSChooseCard(to);
        scn.LSChooseCard(han);
        scn.PassAllResponses();
        if (scn.LSDecisionAvailable("Do you want")) {
            scn.LSChooseYes();
            scn.PassAllResponses();
        }
        assertAtLocation(from, han);
        assertInZone(Zone.LOST_PILE, nabrun);
    }

    @Test
    public void WhereAreYouTakingThisThingDeploysWhenOpponentRemovesJustLostCharacterFromLostPile() {
        var scn = GetScenario();
        var wayt = scn.GetDSCard("wayt");
        var bacta = scn.GetLSCard("bacta");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToDSHand(wayt);
        scn.MoveCardsToLSSideOfTable(bacta);
        scn.MoveCardsToLocation(site, luke);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.LSExecuteAdHocEffect(luke, new LoseCardFromTableEffect(new TopLevelGameTextAction(luke, luke.getCardId()), luke));
        assertTrue(passUntil(scn, () -> lsCan(scn, bacta, "Stack")));
        scn.LSUseCardAction(bacta, "Stack");
        assertTrue(passUntil(scn, () -> dsCan(scn, wayt, null)));
        scn.DSPlayCard(wayt);
        scn.PassAllResponses();

        assertTrue(wayt.getZone().isInPlay());
        assertTrue(scn.IsStackedOn(wayt, luke));
        assertEquals(Zone.STACKED_FACE_DOWN, luke.getZone());
        assertFalse(scn.IsStackedOn(bacta, luke));
    }

    @Test
    public void WhereAreYouTakingThisThingOpponentMayUseTwoForceToPlaceStackedCardInUsedPile() {
        var scn = GetScenario();
        var wayt = scn.GetDSCard("wayt");
        var luke = scn.GetLSCard("luke");

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(wayt);
        scn.StackCardsFaceDownOn(wayt, luke);
        scn.EnsureLSForcePile(5);
        scn.SkipToLSTurn(Phase.DEPLOY);
        int forceBefore = scn.GetLSForcePileCount();
        assertTrue(scn.LSCardActionAvailable(wayt, "Use 2 Force"));
        scn.LSUseCardAction(wayt, "Use 2 Force");
        if (scn.LSDecisionAvailable("Choose")) {
            scn.LSChooseCard(luke);
        }
        scn.PassAllResponses();
        assertInZone(Zone.USED_PILE, luke);
        assertEquals(forceBefore - 2, scn.GetLSForcePileCount());
    }
}
