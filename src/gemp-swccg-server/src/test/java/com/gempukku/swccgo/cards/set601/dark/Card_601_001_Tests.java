package com.gempukku.swccgo.cards.set601.dark;

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

/** Knowledge And Defense (V) (Legacy Block 4, 1240_31 / 601_1); text per the Virtual Cards Master List (2014). */
public class Card_601_001_Tests {
    private static final String PLAY_SHIELD = "Play a Defensive Shield";

    private VirtualTableScenario scenario() {
        var light = new HashMap<String, String>();
        light.put("recoil", "4_58");
        light.put("filler", "1_19");
        var dark = new HashMap<String, String>();
        dark.put("kad", "601_1");
        dark.put("interrupt", "1_234");
        dark.put("filler", "1_194");
        var shields = new StartingSetup() {
            @Override public HashMap<String, String> Cards() {
                var cards = new HashMap<String, String>();
                cards.put("a", "13_51"); cards.put("b", "13_52"); cards.put("c", "13_54");
                cards.put("d", "13_61"); cards.put("e", "13_63");
                return cards;
            }
            @Override public void Setup(VirtualTableScenario scn) { }
        };
        return new VirtualTableScenario(light, dark, 20, 20,
                StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, shields, VirtualTableScenario.Open);
    }

    @Test
    public void KnowledgeAndDefenseVTextIsTheMasterListText() {
        var scn = scenario();
        var b = scn.GetDSCard("kad").getBlueprint();
        assertEquals("Knowledge And Defense", b.getTitle());
        assertTrue(b.hasVirtualSuffix());
        assertEquals(Side.DARK, b.getSide());
        assertEquals(CardSubtype.STARTING, b.getCardSubtype());
        assertEquals("Deploy on table with up to 15 cards from outside your deck face-down under here. Four times per game, may play a Defensive Shield from here (as if from hand). Unless canceling your Interrupt, opponent may not play Recoil In Fear until the end of your first turn.", b.getGameText());
    }

    @Test
    public void KnowledgeAndDefenseVPlaysOnlyDefensiveShieldsFromUnderItFourTimesPerGame() {
        var scn = scenario();
        scn.StartGame();
        var kad = scn.GetDSCard("kad");
        assertInZone(Zone.SIDE_OF_TABLE, kad);
        // A non-shield placed under it may not be played from here.
        scn.StackCardsOn(kad, scn.GetDSCard("interrupt"));
        scn.SkipToDSTurn(Phase.DEPLOY);
        for (var key : new String[]{"a", "b", "c", "d"}) {
            assertTrue(scn.DSCardActionAvailable(kad, PLAY_SHIELD));
            scn.DSUseCardAction(kad, PLAY_SHIELD);
            assertFalse(scn.DSHasCardChoiceAvailable(scn.GetDSCard("interrupt")));
            scn.DSChooseCard(scn.GetDSCard(key));
            scn.PassAllResponses();
            assertInZone(Zone.SIDE_OF_TABLE, scn.GetDSCard(key));
            if (scn.LS.equals(scn.GetDecidingPlayer())) scn.LSPass();
        }
        assertInZone(Zone.STACKED_FACE_DOWN, scn.GetDSCard("e"));
        assertFalse(scn.DSCardActionAvailable(kad, PLAY_SHIELD));
    }

    @Test
    public void OpponentMayNotPlayRecoilInFearUntilTheEndOfYourFirstTurn() {
        var scn = scenario();
        scn.StartGame();
        var recoil = scn.GetLSCard("recoil");
        scn.MoveCardsToLSHand(recoil, scn.GetLSCard("filler"));
        scn.MoveCardsToDSHand(scn.GetDSCard("filler"));
        var q = scn.game().getModifiersQuerying();
        // During Dark Side's first turn (the game starts in it): blocked, even when Light Side gets to act.
        scn.SkipToPhase(Phase.DEPLOY);
        scn.EnsureLSForcePile(5);
        assertEquals(1, scn.gameState().getPlayersLatestTurnNumber(scn.DS));
        assertTrue(q.hasGameTextModification(scn.gameState(), recoil, ModifyGameTextType.RECOIL_IN_FEAR__MAY_NOT_BE_PLAYED_EXCEPT_TO_CANCEL_INTERRUPT));
        scn.DSPass();
        assertEquals(scn.LS, scn.GetDecidingPlayer());
        assertFalse(scn.LSCardPlayAvailable(recoil));
        // After Dark Side's first turn: allowed again.
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureLSForcePile(5);
        assertFalse(q.hasGameTextModification(scn.gameState(), recoil, ModifyGameTextType.RECOIL_IN_FEAR__MAY_NOT_BE_PLAYED_EXCEPT_TO_CANCEL_INTERRUPT));
        assertTrue(scn.LSCardPlayAvailable(recoil));
    }
}
