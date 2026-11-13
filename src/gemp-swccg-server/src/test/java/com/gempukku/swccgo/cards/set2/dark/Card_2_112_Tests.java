package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_2_112_Tests {
    private VirtualTableScenario GetScenario() {
        var scn = new VirtualTableScenario(
                new HashMap<>() {{
                    put("captive", "1_19");
                    put("hand", "1_19");
                }},
                new HashMap<>() {{
                    put("ito", "2_93");
                    put("hypo", "2_112");
                    put("guard", "1_194");
                    put("escort", "1_194");
                    put("farm", "1_294");
                    put("prison", "1_284");
                }},
                20, 20,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open);
        scn.StartGame();
        scn.MoveLocationToTable(scn.GetDSCard("farm"));
        scn.MoveLocationToTable(scn.GetDSCard("prison"));
        scn.MoveCardsToLocation(scn.GetDSCard("farm"), scn.GetDSCard("ito"),
                scn.GetDSCard("guard"), scn.GetDSCard("escort"));
        scn.AttachCardsTo(scn.GetDSCard("ito"), scn.GetDSCard("hypo"));
        scn.MoveCardsToLSHand(scn.GetLSCard("hand"));
        scn.EnsureLSForcePile(8);
        return scn;
    }

    @Test
    public void HypoCannotIncreaseDrainWithoutCaptive() {
        //test1: a legal drain does not offer Hypo without its printed captive condition.
        var scn = GetScenario();
        var farm = scn.GetDSCard("farm");
        scn.SkipToDSTurn(Phase.CONTROL);
        assertTrue(scn.DSForceDrainAvailable(farm)); //test1
        scn.DSForceDrainAt(farm);
        scn.LSPass();
        assertTrue(scn.IsActiveForceDrain()); //test1
        assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("hypo"), "Add 1 to Force drain")); //test1
    }

    @Test
    public void HypoCannotIncreaseDrainForCaptiveAtAnotherSite() {
        //test1: a captive elsewhere does not satisfy present-with or with-imprisoned.
        var scn = GetScenario();
        var farm = scn.GetDSCard("farm");
        var escort = scn.GetDSCard("escort");
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), escort);
        scn.CaptureCardWith(escort, scn.GetLSCard("captive"));
        scn.SkipToDSTurn(Phase.CONTROL);
        assertTrue(scn.DSForceDrainAvailable(farm)); //test1
        scn.DSForceDrainAt(farm);
        scn.LSPass();
        assertTrue(scn.IsActiveForceDrain()); //test1
        assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("hypo"), "Add 1 to Force drain")); //test1
    }

    @Test
    public void HypoIncreasesDrainByOneWhenPresentWithCaptive() {
        //test1: the permitted optional choice changes the actual drain from two to three.
        var scn = GetScenario();
        var farm = scn.GetDSCard("farm");
        var hypo = scn.GetDSCard("hypo");
        scn.CaptureCardWith(scn.GetDSCard("escort"), scn.GetLSCard("captive"));
        scn.SkipToDSTurn(Phase.CONTROL);
        assertTrue(scn.DSForceDrainAvailable(farm)); //test1
        scn.DSForceDrainAt(farm);
        scn.LSPass();
        assertTrue(scn.DSCardActionAvailable(hypo, "Add 1 to Force drain")); //test1
        scn.DSUseCardAction(hypo, "Add 1 to Force drain");
        scn.PassAllResponses();
        scn.PassForceDrainStartResponses();
        assertEquals(3, scn.GetForceDrainTotal()); //test1
    }

    @Test
    public void HypoIncreasesDrainWithImprisonedCaptiveAtSameSite() {
        //test1: the explicit imprisoned-captive alternative permits the drain choice.
        var scn = GetScenario();
        var prison = scn.GetDSCard("prison");
        var hypo = scn.GetDSCard("hypo");
        scn.MoveCardsToLocation(prison, scn.GetDSCard("ito"), scn.GetDSCard("guard"));
        scn.gameState().imprisonCharacter(scn.game(), scn.GetLSCard("captive"), prison);
        scn.SkipToDSTurn(Phase.CONTROL);
        assertTrue(scn.DSForceDrainAvailable(prison)); //test1
        scn.DSForceDrainAt(prison);
        scn.LSPass();
        assertTrue(scn.DSCardActionAvailable(hypo, "Add 1 to Force drain")); //test1
        scn.DSUseCardAction(hypo, "Add 1 to Force drain");
        scn.PassAllResponses();
        scn.PassForceDrainStartResponses();
        assertEquals(2, scn.GetForceDrainTotal()); //test1
    }

    private void AnswerQuestion(VirtualTableScenario scn, String answer) {
        scn.DSUseCardAction(scn.GetDSCard("hypo"), "Ask yes-or-no question");
        scn.PassAllResponses();
        assertTrue(scn.DSDecisionAvailable("Select OK after typing yes-or-no question"));
        scn.DSChoose("OK");
        assertTrue(scn.LSDecisionAvailable("Choose answer to yes-or-no question"));
        scn.LSChoose(answer);
        scn.PassAllResponses();
        if (!answer.startsWith("Refuse")) {
            ReturnControlToDark(scn);
        }
    }

    private void ReturnControlToDark(VirtualTableScenario scn) {
        assertTrue(scn.AwaitingLSControlPhaseActions());
        scn.LSPass();
        assertTrue(scn.AwaitingDSControlPhaseActions());
    }

    @Test
    public void HypoDoesNotOfferQuestionWithoutCaptive() {
        //test1: the same captive requirement applies to the question option.
        var scn = GetScenario();
        scn.SkipToDSTurn(Phase.CONTROL);
        assertTrue(scn.AwaitingDSControlPhaseActions()); //test1
        assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("hypo"), "Ask yes-or-no question")); //test1
    }

    @Test
    public void HypoQuestionChoicePreventsDrainChoiceThatControlPhase() {
        //test1: a truthful answer costs no Force; test2: choosing the question spends the shared use.
        var scn = GetScenario();
        var farm = scn.GetDSCard("farm");
        var hypo = scn.GetDSCard("hypo");
        scn.CaptureCardWith(scn.GetDSCard("escort"), scn.GetLSCard("captive"));
        scn.SkipToDSTurn(Phase.CONTROL);
        int lostBefore = scn.GetLSLostPileCount();
        assertTrue(scn.DSCardActionAvailable(hypo, "Ask yes-or-no question")); //test1
        AnswerQuestion(scn, "Yes");
        assertEquals(lostBefore, scn.GetLSLostPileCount()); //test1
        assertFalse(scn.DSCardActionAvailable(hypo, "Ask yes-or-no question")); //test2
        assertTrue(scn.DSForceDrainAvailable(farm)); //test2
        scn.DSForceDrainAt(farm);
        scn.LSPass();
        assertTrue(scn.IsActiveForceDrain()); //test2
        assertFalse(scn.DSCardActionAvailable(hypo, "Add 1 to Force drain")); //test2
    }

    @Test
    public void HypoDrainChoicePreventsQuestionThatControlPhase() {
        //test1: the actual drain is increased; test2: the other option is then unavailable.
        var scn = GetScenario();
        var farm = scn.GetDSCard("farm");
        var hypo = scn.GetDSCard("hypo");
        scn.CaptureCardWith(scn.GetDSCard("escort"), scn.GetLSCard("captive"));
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.DSForceDrainAt(farm);
        scn.LSPass();
        assertTrue(scn.DSCardActionAvailable(hypo, "Add 1 to Force drain")); //test1
        scn.DSUseCardAction(hypo, "Add 1 to Force drain");
        scn.PassAllResponses();
        scn.PassForceDrainStartResponses();
        assertEquals(3, scn.GetForceDrainTotal()); //test1
        scn.LSPayRemainingForceLossFromReserveDeck();
        scn.PassForceDrainEndResponses();
        scn.PassAllResponses();
        ReturnControlToDark(scn);
        assertTrue(scn.AwaitingDSControlPhaseActions()); //test2
        assertFalse(scn.DSCardActionAvailable(hypo, "Ask yes-or-no question")); //test2
    }

    @Test
    public void HypoRefusingQuestionLosesExactlyOneForce() {
        //test1: declining to answer invokes the printed one-Force loss, not additional loss.
        var scn = GetScenario();
        scn.CaptureCardWith(scn.GetDSCard("escort"), scn.GetLSCard("captive"));
        scn.SkipToDSTurn(Phase.CONTROL);
        int lostBefore = scn.GetLSLostPileCount();
        AnswerQuestion(scn, "Refuse to answer (Lose 1 Force)");
        assertTrue(scn.AwaitingLSForceLossPayment()); //test1
        scn.LSPayForceLossFromReserveDeck();
        scn.PassAllResponses();
        assertEquals(lostBefore + 1, scn.GetLSLostPileCount()); //test1
        assertFalse(scn.AwaitingLSForceLossPayment()); //test1
    }

    @Test
    public void HypoQuestionChoiceResetsOnNextOwnControlPhase() {
        //test1: use is exhausted now; test2: a new own control phase restores the choice.
        var scn = GetScenario();
        var hypo = scn.GetDSCard("hypo");
        scn.CaptureCardWith(scn.GetDSCard("escort"), scn.GetLSCard("captive"));
        scn.SkipToDSTurn(Phase.CONTROL);
        AnswerQuestion(scn, "No");
        assertFalse(scn.DSCardActionAvailable(hypo, "Ask yes-or-no question")); //test1
        scn.SkipToLSTurn(Phase.DRAW);
        scn.SkipToDSTurn(Phase.CONTROL);
        assertTrue(scn.AwaitingDSControlPhaseActions()); //test2
        assertTrue(scn.DSCardActionAvailable(hypo, "Ask yes-or-no question")); //test2
    }

    @Test
    public void HypoQuestionSupportsImprisonedCaptiveAtSameSite() {
        //test1: the explicit imprisoned-captive alternative also permits asking a question.
        var scn = GetScenario();
        var prison = scn.GetDSCard("prison");
        var hypo = scn.GetDSCard("hypo");
        scn.MoveCardsToLocation(prison, scn.GetDSCard("ito"), scn.GetDSCard("guard"));
        scn.gameState().imprisonCharacter(scn.game(), scn.GetLSCard("captive"), prison);
        scn.SkipToDSTurn(Phase.CONTROL);
        assertTrue(scn.DSCardActionAvailable(hypo, "Ask yes-or-no question")); //test1
        AnswerQuestion(scn, "Yes");
        assertFalse(scn.DSCardActionAvailable(hypo, "Ask yes-or-no question")); //test1
    }

    @Test
    public void HypoDoesNotUseImprisonedCaptiveAtAnotherSite() {
        //test1: neither option is unlocked by an imprisoned captive elsewhere.
        var scn = GetScenario();
        var farm = scn.GetDSCard("farm");
        var hypo = scn.GetDSCard("hypo");
        scn.gameState().imprisonCharacter(scn.game(), scn.GetLSCard("captive"), scn.GetDSCard("prison"));
        scn.SkipToDSTurn(Phase.CONTROL);
        assertFalse(scn.DSCardActionAvailable(hypo, "Ask yes-or-no question")); //test1
        assertTrue(scn.DSForceDrainAvailable(farm)); //test1
        scn.DSForceDrainAt(farm);
        scn.LSPass();
        assertTrue(scn.IsActiveForceDrain()); //test1
        assertFalse(scn.DSCardActionAvailable(hypo, "Add 1 to Force drain")); //test1
    }

    @Test
    public void HypoDoesNotUseFrozenCaptive() {
        //test1: an inactive frozen captive cannot satisfy the interrogation condition.
        var scn = GetScenario();
        var farm = scn.GetDSCard("farm");
        var hypo = scn.GetDSCard("hypo");
        var captive = scn.GetLSCard("captive");
        scn.CaptureCardWith(scn.GetDSCard("escort"), captive);
        scn.FreezeCard(captive);
        scn.SkipToDSTurn(Phase.CONTROL);
        assertFalse(scn.DSCardActionAvailable(hypo, "Ask yes-or-no question")); //test1
        assertTrue(scn.DSForceDrainAvailable(farm)); //test1
        scn.DSForceDrainAt(farm);
        scn.LSPass();
        assertTrue(scn.IsActiveForceDrain()); //test1
        assertFalse(scn.DSCardActionAvailable(hypo, "Add 1 to Force drain")); //test1
    }

    @Test
    public void HypoDecliningDrainChoiceLeavesQuestionAvailable() {
        //test1: merely offering the optional bonus does not spend the shared use.
        var scn = GetScenario();
        var farm = scn.GetDSCard("farm");
        var hypo = scn.GetDSCard("hypo");
        scn.CaptureCardWith(scn.GetDSCard("escort"), scn.GetLSCard("captive"));
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.DSForceDrainAt(farm);
        scn.LSPass();
        assertTrue(scn.DSCardActionAvailable(hypo, "Add 1 to Force drain")); //test1
        scn.DSPass();
        scn.PassForceDrainStartResponses();
        assertEquals(2, scn.GetForceDrainTotal()); //test1
        scn.LSPayRemainingForceLossFromReserveDeck();
        scn.PassForceDrainEndResponses();
        scn.PassAllResponses();
        ReturnControlToDark(scn);
        assertTrue(scn.AwaitingDSControlPhaseActions()); //test1
        assertTrue(scn.DSCardActionAvailable(hypo, "Ask yes-or-no question")); //test1
    }

    @Test
    public void HypoDrainChoiceResetsOnNextOwnControlPhase() {
        //test1: choosing the bonus does not exhaust it on later turns.
        var scn = GetScenario();
        var farm = scn.GetDSCard("farm");
        var hypo = scn.GetDSCard("hypo");
        scn.CaptureCardWith(scn.GetDSCard("escort"), scn.GetLSCard("captive"));
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.DSForceDrainAt(farm);
        scn.LSPass();
        scn.DSUseCardAction(hypo, "Add 1 to Force drain");
        scn.PassAllResponses();
        scn.PassForceDrainStartResponses();
        assertEquals(3, scn.GetForceDrainTotal()); //test1
        scn.LSPayRemainingForceLossFromReserveDeck();
        scn.PassForceDrainEndResponses();
        scn.PassAllResponses();
        ReturnControlToDark(scn);
        scn.SkipToLSTurn(Phase.DRAW);
        scn.SkipToDSTurn(Phase.CONTROL);
        assertTrue(scn.DSForceDrainAvailable(farm)); //test1
        scn.DSForceDrainAt(farm);
        scn.LSPass();
        assertTrue(scn.DSCardActionAvailable(hypo, "Add 1 to Force drain")); //test1
    }

    private VirtualTableScenario GetPurchasedHypoScenario() {
        var scn = new VirtualTableScenario(
                new HashMap<>() {{
                    put("captive", "1_19");
                    put("purchaser", "1_33");
                }},
                new HashMap<>() {{
                    put("ito", "2_93");
                    put("hypo", "2_112");
                    put("hand", "1_194");
                    put("prison", "6_164");
                }});
        scn.StartGame();
        var prison = scn.GetDSCard("prison");
        var ito = scn.GetDSCard("ito");
        var hypo = scn.GetDSCard("hypo");
        var purchaser = scn.GetLSCard("purchaser");
        scn.MoveLocationToTable(prison);
        scn.MoveCardsToLocation(prison, ito, purchaser);
        scn.AttachCardsTo(ito, hypo);
        scn.MoveCardsToDSHand(scn.GetDSCard("hand"));
        scn.gameState().imprisonCharacter(scn.game(), scn.GetLSCard("captive"), prison);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.EnsureLSForcePile(8);
        scn.PrepareLSDestiny(5);
        assertTrue(scn.LSCardActionAvailable(purchaser, "'Purchase' transport vehicle or droid"));
        scn.LSUseCardAction(purchaser, "'Purchase' transport vehicle or droid");
        scn.LSChooseCard(ito);
        scn.PassAllResponses();
        if (scn.AwaitingDSControlPhaseActions()) {
            scn.DSPass();
        }
        assertTrue(scn.AwaitingLSControlPhaseActions());
        assertEquals(4, scn.GetLSForcePileCount());
        assertEquals(scn.gameState().getLightPlayer(), ito.getOwner());
        assertEquals(scn.gameState().getLightPlayer(), hypo.getOwner());
        assertEquals(ito, hypo.getAttachedTo());
        assertTrue(scn.GetLSCard("captive").isImprisoned());
        assertEquals(prison, scn.GetLSCard("captive").getAttachedTo());
        assertTrue(GameConditions.hasHand(scn.game(), scn.gameState().getDarkPlayer()));
        assertTrue(GameConditions.canUseDevice(scn.game(), hypo));
        return scn;
    }

    @Test
    public void HypoPurchasedByLightCannotAskAboutImprisonedCaptive() {
        //test1: the official stolen/purchased IT-O ruling forbids the question even with a captive here.
        var scn = GetPurchasedHypoScenario();
        assertFalse(scn.LSCardActionAvailable(scn.GetDSCard("hypo"), "Ask yes-or-no question")); //test1
    }

    @Test
    public void HypoPurchasedByLightCannotIncreaseLegalForceDrain() {
        //test1: Light may make its native drain, but purchased Hypo cannot add a captive bonus.
        var scn = GetPurchasedHypoScenario();
        var prison = scn.GetDSCard("prison");
        assertTrue(scn.LSForceDrainAvailable(prison)); //test1
        scn.LSForceDrainAt(prison);
        scn.DSPass();
        assertTrue(scn.IsActiveForceDrain()); //test1
        assertFalse(scn.LSCardActionAvailable(scn.GetDSCard("hypo"), "Add 1 to Force drain")); //test1
        scn.PassAllResponses();
        scn.PassForceDrainStartResponses();
        assertEquals(1, scn.GetForceDrainTotal()); //test1
    }

}
