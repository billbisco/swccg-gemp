package com.gempukku.swccgo.cards.set1240.light;

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

import static com.gempukku.swccgo.framework.Assertions.assertInHand;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1240_089_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("scrambled", "1240_89");
                    put("delay", "2_34");
                }},
                new HashMap<>() {{
                    put("monnok", "2_135");
                    put("motion", "12_137");
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

    @Test
    public void ScrambledTransmissionVStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("scrambled").getBlueprint();

        assertEquals(Title.Scrambled_Transmission, card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.SPECIAL_EDITION);
            add(Icon.EFFECT);
            add(Icon.LEGACY_BLOCK_4);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
        assertTrue(card.isImmuneToCardTitle(Title.Alter));
    }

    @Test
    public void ScrambledTransmissionVDeploysOnTableAndTakesLogisticalDelayOncePerTurn() {
        var scn = GetScenario();
        var scrambled = scn.GetLSCard("scrambled");
        var delay = scn.GetLSCard("delay");

        scn.StartGame();
        scn.MoveCardsToLSHand(scrambled);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSDeployAvailable(scrambled));
        scn.LSDeployCard(scrambled);
        scn.PassAllResponses();
        assertEquals(Zone.SIDE_OF_TABLE, scrambled.getZone());

        scn.DSPass();
        scn.MoveCardsToTopOfLSReserveDeck(delay);
        assertTrue(scn.LSCardActionAvailable(scrambled, "Take Logistical Delay"));
        scn.LSUseCardAction(scrambled, "Take Logistical Delay");
        scn.PassAllResponses();
        scn.LSChooseCard(delay);
        scn.PassAllResponses();
        assertInHand(delay);

        scn.DSPass();
        assertFalse(scn.LSCardActionAvailable(scrambled, "Take Logistical Delay"));
    }

    @Test
    public void ScrambledTransmissionVProtectsHandUnless13OrMoreCardsExceptMonnok() {
        var scn = GetScenario();
        var scrambled = scn.GetLSCard("scrambled");
        var monnok = scn.GetDSCard("monnok");
        var motion = scn.GetDSCard("motion");

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(scrambled);
        var mq = scn.game().getModifiersQuerying();

        // Fewer than 13 cards in hand: opponent may not peek at or remove them (except with Monnok).
        assertTrue(scn.gameState().getHand(scn.LS).size() < 13);
        assertTrue(mq.mayNotLookAtOpponentsHand(scn.gameState(), motion, scn.DS));
        assertTrue(mq.mayNotRemoveCardsFromOpponentsHand(scn.gameState(), motion, scn.DS));
        assertFalse(mq.mayNotLookAtOpponentsHand(scn.gameState(), monnok, scn.DS));
        assertFalse(mq.mayNotRemoveCardsFromOpponentsHand(scn.gameState(), monnok, scn.DS));

        // 13 or more cards in hand: protection ends.
        while (scn.gameState().getHand(scn.LS).size() < 13) {
            scn.MoveCardsToLSHand(scn.GetTopOfLSReserveDeck());
        }
        assertEquals(13, scn.gameState().getHand(scn.LS).size());
        assertFalse(mq.mayNotLookAtOpponentsHand(scn.gameState(), motion, scn.DS));
        assertFalse(mq.mayNotRemoveCardsFromOpponentsHand(scn.gameState(), motion, scn.DS));
    }

    @Test
    public void ScrambledTransmissionVLoses2ForceToCancelPoliticalEffect() {
        var scn = GetScenario();
        var scrambled = scn.GetLSCard("scrambled");
        var motion = scn.GetDSCard("motion");

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(scrambled);
        scn.MoveCardsToDSSideOfTable(motion);
        scn.SkipToLSTurn(Phase.DEPLOY);

        int lifeForce = scn.GetPlayerLifeForceRemaining(scn.LS);
        assertTrue(scn.LSCardActionAvailable(scrambled, "cancel a Political Effect"));
        scn.LSUseCardAction(scrambled, "cancel a Political Effect");
        if (scn.LSHasCardChoiceAvailable(motion)) {
            scn.LSChooseCard(motion);
        }
        scn.PassAllResponses();
        if (scn.AwaitingLSForceLossPayment()) {
            scn.LSPayRemainingForceLossFromReserveDeck();
            scn.PassAllResponses();
        }
        assertEquals(lifeForce - 2, scn.GetPlayerLifeForceRemaining(scn.LS));
        assertTrue(motion.getZone() == Zone.LOST_PILE || motion.getZone() == Zone.TOP_OF_LOST_PILE);

        // Shares the once-per-turn limit with the Logistical Delay option.
        scn.DSPass();
        assertFalse(scn.LSCardActionAvailable(scrambled, "Take Logistical Delay"));
    }
}
