package com.gempukku.swccgo.cards.set601.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import org.junit.Test;

import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/** Anger, Fear, Aggression (V) (Legacy Block 4, 1240_78 / 601_39); text per the Virtual Cards Master List (2014). */
public class Card_601_039_Tests {
    private static final String PLAY_SHIELD = "Play a Defensive Shield";

    private VirtualTableScenario scenario() {
        var light = new HashMap<String, String>();
        light.put("afa", "601_39");
        light.put("interrupt", "1_105");
        light.put("filler", "1_19");
        var dark = new HashMap<String, String>();
        dark.put("uitf", "4_151");
        dark.put("filler", "1_194");
        var shields = new StartingSetup() {
            @Override public HashMap<String, String> Cards() {
                var cards = new HashMap<String, String>();
                cards.put("a", "13_1"); cards.put("b", "13_3"); cards.put("c", "13_4");
                cards.put("d", "13_6"); cards.put("e", "13_8");
                return cards;
            }
            @Override public void Setup(VirtualTableScenario scn) { }
        };
        return new VirtualTableScenario(light, dark, 20, 20,
                StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                shields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void AngerFearAggressionVTextIsTheMasterListText() {
        var scn = scenario();
        var b = scn.GetLSCard("afa").getBlueprint();
        assertEquals("Anger, Fear, Aggression", b.getTitle());
        assertTrue(b.hasVirtualSuffix());
        assertEquals(Side.LIGHT, b.getSide());
        assertEquals(CardSubtype.STARTING, b.getCardSubtype());
        assertEquals("Deploy on table with up to 15 cards from outside your deck face-down under here. Four times per game, may play a Defensive Shield from here (as if from hand). Unless canceling your Interrupt, opponent may not play Uncertain Is The Future until the end of your first turn.", b.getGameText());
    }

    @Test
    public void AngerFearAggressionVPlaysOnlyDefensiveShieldsFromUnderItFourTimesPerGame() {
        var scn = scenario();
        scn.StartGame();
        var afa = scn.GetLSCard("afa");
        assertInZone(Zone.SIDE_OF_TABLE, afa);
        // A non-shield placed under it may not be played from here.
        scn.StackCardsOn(afa, scn.GetLSCard("interrupt"));
        scn.SkipToLSTurn(Phase.DEPLOY);
        for (var key : new String[]{"a", "b", "c", "d"}) {
            assertTrue(scn.LSCardActionAvailable(afa, PLAY_SHIELD));
            scn.LSUseCardAction(afa, PLAY_SHIELD);
            assertFalse(scn.LSHasCardChoiceAvailable(scn.GetLSCard("interrupt")));
            scn.LSChooseCard(scn.GetLSCard(key));
            scn.PassAllResponses();
            assertInZone(Zone.SIDE_OF_TABLE, scn.GetLSCard(key));
            if (scn.DS.equals(scn.GetDecidingPlayer())) scn.DSPass();
        }
        assertInZone(Zone.STACKED_FACE_DOWN, scn.GetLSCard("e"));
        assertFalse(scn.LSCardActionAvailable(afa, PLAY_SHIELD));
    }

    @Test
    public void OpponentMayNotPlayUncertainIsTheFutureUntilTheEndOfYourFirstTurn() {
        var scn = scenario();
        scn.StartGame();
        var uitf = scn.GetDSCard("uitf");
        scn.MoveCardsToDSHand(uitf, scn.GetDSCard("filler"));
        scn.MoveCardsToLSHand(scn.GetLSCard("filler"));
        var q = scn.game().getModifiersQuerying();
        // During Light Side's first turn: blocked, even when Dark Side gets to act.
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureDSForcePile(5);
        assertEquals(1, scn.gameState().getPlayersLatestTurnNumber(scn.LS));
        assertTrue(q.hasGameTextModification(scn.gameState(), uitf, ModifyGameTextType.UNCERTAIN_IS_THE_FUTURE__MAY_NOT_BE_PLAYED_EXCEPT_TO_CANCEL_INTERRUPT));
        scn.LSPass();
        assertEquals(scn.DS, scn.GetDecidingPlayer());
        assertFalse(scn.DSCardPlayAvailable(uitf));
        // After Light Side's first turn: allowed again.
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.EnsureDSForcePile(5);
        assertFalse(q.hasGameTextModification(scn.gameState(), uitf, ModifyGameTextType.UNCERTAIN_IS_THE_FUTURE__MAY_NOT_BE_PLAYED_EXCEPT_TO_CANCEL_INTERRUPT));
        assertTrue(scn.DSCardPlayAvailable(uitf));
    }
}
