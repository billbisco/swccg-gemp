package com.gempukku.swccgo.cards.set1291.light;

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

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1291_051_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("cylinders", "1291_51");
                    put("trooper", "1116_9"); // Clone Trooper (clone)
                    put("clonepilot", "221_55"); // Clone Pilot (clone)
                    put("luke", "1_19"); // not a clone
                }},
                new HashMap<>() {{
                    put("vader", "1_168");
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

    @Test
    public void CloningCylindersStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("cylinders").getBlueprint();
        assertEquals("Cloning Cylinders", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);
            add(Icon.EPISODE_I);
            add(Icon.LEGACY_BLOCK_9);
        }});
        assertTrue(card.isImmuneToCardTitle(Title.Alter));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_9_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void CloningCylindersUsesFourForceToDeployOnTable() {
        var scn = GetScenario();
        var cylinders = scn.GetLSCard("cylinders");
        scn.StartGame();
        scn.MoveCardsToLSHand(cylinders);
        scn.EnsureLSForcePile(6);
        scn.SkipToLSTurn(Phase.DEPLOY);
        int forceBefore = scn.GetLSForcePileCount();
        assertTrue(scn.LSCardPlayAvailable(cylinders));
        scn.LSPlayCard(cylinders);
        scn.PassAllResponses();
        assertInZone(Zone.SIDE_OF_TABLE, cylinders);
        assertEquals(forceBefore - 4, scn.GetLSForcePileCount());
    }

    @Test
    public void CloningCylindersStacksCloneFromLostPileFaceDownForTwoForceDuringControlPhase() {
        var scn = GetScenario();
        var cylinders = scn.GetLSCard("cylinders");
        var trooper = scn.GetLSCard("trooper");
        var clonePilot = scn.GetLSCard("clonepilot");
        var luke = scn.GetLSCard("luke");

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(cylinders);
        scn.MoveCardsToTopOfLSLostPile(trooper, clonePilot, luke);
        scn.EnsureLSForcePile(6);
        scn.SkipToLSTurn(Phase.CONTROL);
        int forceBefore = scn.GetLSForcePileCount();

        assertTrue(scn.AwaitingLSControlPhaseActions());
        assertTrue(scn.LSCardActionAvailable(cylinders, "Stack a clone from Lost Pile"));
        scn.LSUseCardAction(cylinders, "Stack a clone from Lost Pile");
        scn.PassResponses("Use 2 Force");
        assertTrue(scn.LSHasCardChoiceAvailable(trooper));
        scn.LSChooseCard(trooper);
        scn.PassAllResponses();

        assertInZone(Zone.STACKED_FACE_DOWN, trooper);
        assertTrue(scn.IsStackedOn(cylinders, trooper));
        assertEquals(forceBefore - 2, scn.GetLSForcePileCount());

        // No once-per-turn limit: may do it again in the same control phase
        if (scn.AwaitingDSControlPhaseActions()) {
            scn.DSPass();
        }
        assertTrue(scn.AwaitingLSControlPhaseActions());
        assertTrue(scn.LSCardActionAvailable(cylinders, "Stack a clone from Lost Pile"));
        scn.LSUseCardAction(cylinders, "Stack a clone from Lost Pile");
        scn.PassResponses("Use 2 Force");
        scn.LSChooseCard(clonePilot);
        scn.PassAllResponses();
        assertInZone(Zone.STACKED_FACE_DOWN, clonePilot);
        assertEquals(forceBefore - 4, scn.GetLSForcePileCount());
        assertInZone(Zone.LOST_PILE, luke);
    }

    @Test
    public void CloningCylindersStackActionNotAvailableOutsideControlPhase() {
        var scn = GetScenario();
        var cylinders = scn.GetLSCard("cylinders");
        var trooper = scn.GetLSCard("trooper");

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(cylinders);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSLostPile(trooper);
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertFalse(scn.LSCardActionAvailable(cylinders, "Stack a clone from Lost Pile"));
    }

    @Test
    public void CloningCylindersTurnsStackedCardsFaceUpAtStartOfYourTurn() {
        var scn = GetScenario();
        var cylinders = scn.GetLSCard("cylinders");
        var trooper = scn.GetLSCard("trooper");

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(cylinders);
        scn.StackCardsFaceDownOn(cylinders, trooper);
        assertInZone(Zone.STACKED_FACE_DOWN, trooper);

        // Still face down during opponent's turn
        scn.SkipToPhase(Phase.CONTROL);
        assertInZone(Zone.STACKED_FACE_DOWN, trooper);

        scn.SkipToLSTurn(Phase.ACTIVATE);
        assertInZone(Zone.STACKED, trooper);
    }

    @Test
    public void CloningCylindersFaceUpStackedCardMayDeployAsIfFromHand() {
        var scn = GetScenario();
        var cylinders = scn.GetLSCard("cylinders");
        var trooper = scn.GetLSCard("trooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(cylinders);
        scn.StackCardsOn(cylinders, trooper);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSDeployAvailable(trooper));
        scn.LSDeployCard(trooper);
        if (scn.LSDecisionAvailable("Choose where to deploy")) {
            scn.LSChooseCard(site);
        }
        scn.PassAllResponses();
        assertAtLocation(site, trooper);
    }

    @Test
    public void CloningCylindersFaceDownStackedCardMayNotDeploy() {
        var scn = GetScenario();
        var cylinders = scn.GetLSCard("cylinders");
        var trooper = scn.GetLSCard("trooper");

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(cylinders);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.StackCardsFaceDownOn(cylinders, trooper);
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertFalse(scn.LSDeployAvailable(trooper));
    }
}
