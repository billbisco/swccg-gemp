package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class Card_1261_055_Tests {
    private static final String REDUCE = "Reduce Force loss from You're A Slave? to zero";

    private VirtualTableScenario scenario() {
        return scenario("1261_55");
    }

    private VirtualTableScenario scenario(String mosEspa) {
        return new VirtualTableScenario(new HashMap<>(Map.of("revolution", "1_62")),
                new HashMap<>(Map.of("mos", mosEspa, "slave", "12_180", "unrelated", "1_168",
                        "shield", "601_78", "trooper", "1_194",
                        "watto", "11_65", "junkyard", "12_178")),
                28, 28, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void printedMosEspaIdentityAndBothForceIcons() {
        var scn = scenario();
        var bp = scn.GetDSCard("mos").getBlueprint();
        assertEquals(Title.Mos_Espa, bp.getTitle());
        assertTrue(bp.hasVirtualSuffix());
        assertEquals(Side.DARK, bp.getSide());
        assertEquals(CardSubtype.SITE, bp.getCardSubtype());
        assertEquals(Uniqueness.UNIQUE, bp.getUniqueness());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, bp.getExpansionSet());
        assertEquals(Rarity.V, bp.getRarity());
        assertEquals(2, bp.getIconCount(Icon.DARK_FORCE));
        assertEquals(1, bp.getIconCount(Icon.LIGHT_FORCE));
        scn.BlueprintCardTypeCheck(bp, new ArrayList<>(List.of(CardType.LOCATION)));
        scn.BlueprintIconCheck(bp, new ArrayList<>(List.of(Icon.DARK_FORCE, Icon.DARK_FORCE, Icon.LIGHT_FORCE,
                Icon.TATOOINE, Icon.EPISODE_I, Icon.EXTERIOR_SITE, Icon.PLANET, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(bp, new ArrayList<>());
    }

    @Test(timeout = 10000)
    public void darkForceLossDoublesExactlyOnceAndOnlyFromTheNamedFace() {
        var scn = scenario();
        start(scn);
        scn.EnsureDSForcePile(10);
        int before = scn.GetDSLostPileCount();
        causeLoss(scn, scn.GetDSCard("slave"), scn.DS);
        resolveLoss(scn, false);
        assertEquals(before + 4, scn.GetDSLostPileCount());

        restoreDarkPriority(scn);
        causeLoss(scn, scn.GetDSCard("unrelated"), scn.DS);
        resolveLoss(scn, false);
        assertEquals(before + 6, scn.GetDSLostPileCount());
    }

    @Test(timeout = 10000)
    public void lightCanReduceOnlyTheNamedLossAndOnlyOncePerGame() {
        var scn = scenario();
        start(scn);
        scn.EnsureLSForcePile(10);
        int before = scn.GetLSLostPileCount();
        causeLoss(scn, scn.GetDSCard("slave"), scn.LS);
        assertTrue(resolveLoss(scn, true));
        assertEquals(before, scn.GetLSLostPileCount());

        restoreDarkPriority(scn);
        causeLoss(scn, scn.GetDSCard("slave"), scn.LS);
        assertFalse(resolveLoss(scn, true));
        assertEquals(before + 2, scn.GetLSLostPileCount());
    }

    @Test
    public void lightTextCancelsEndorShieldAndMakesOnlyThisSiteImmuneToRevolution() {
        var scn = scenario();
        start(scn);
        var mos = scn.GetDSCard("mos");
        var shield = scn.GetDSCard("shield");
        var mods = scn.game().getModifiersQuerying();
        assertTrue(mods.isImmuneToCardTitle(scn.gameState(), mos, Title.Revolution));
        assertFalse(mods.isImmuneToCardTitle(scn.gameState(), scn.GetDSStartingLocation(), Title.Revolution));
        scn.MoveCardsToDSSideOfTable(shield);
        scn.SkipToPhase(Phase.DEPLOY);
        scn.PassAllResponses();
        assertTrue(scn.GetDSLostPile().contains(shield));
    }

    /** DS places Vader face down with You're A Slave?, LS uses 2 Force; returns Force DS spent to deploy Vader. */
    private int forceDarkPaysWhenLightUsesTwoForce(String mosEspa) {
        var scn = scenario(mosEspa);
        var slave = scn.GetDSCard("slave");
        var vader = scn.GetDSCard("unrelated");
        start(scn);
        // Its setup is complete, as if deployed normally and flipped; this must be set before the deploy phase opens.
        // Keep it on this side: Watto present at Watto's Junkyard and Dark occupies Mos Espa.
        scn.MoveLocationToTable(scn.GetDSCard("junkyard"));
        scn.MoveCardsToLocation(scn.GetDSCard("junkyard"), scn.GetDSCard("watto"));
        scn.MoveCardsToLocation(scn.GetDSCard("mos"), scn.GetDSCard("trooper"));
        slave.setObjectiveDeploymentComplete(true);
        scn.MoveCardsToDSHand(vader);
        restoreDarkPriority(scn);
        scn.SkipToPhase(Phase.DEPLOY);
        assertEquals(Title.Youre_A_Slave, slave.getTitle());
        scn.EnsureDSForcePile(10);
        scn.EnsureLSForcePile(5);
        int dsBefore = scn.GetDSForcePileCount();
        int lsBefore = scn.GetLSForcePileCount();

        scn.DSUseCardAction(slave, "Place card face down on side of table");
        if (scn.DSHasCardChoiceAvailable(vader)) // a single card in hand is chosen automatically
            scn.DSChooseCard(vader);
        assertTrue("LS=" + (scn.LSGetDecision() == null ? null : scn.LSGetDecision().getText()), scn.LSChoiceAvailable(mosEspa.equals("1261_55")
                ? "Use 2 Force (opponent deploys card for 2 Force)" : "Use 2 Force (opponent deploys card for free)"));
        scn.LSChooseOption("Use 2 Force");
        for (int i = 0; i < 10 && vader.getZone() != Zone.AT_LOCATION; i++) {
            if (scn.DSHasCardChoiceAvailable(scn.GetDSStartingLocation()))
                scn.DSChooseCard(scn.GetDSStartingLocation());
            else
                scn.PassAllResponses();
        }
        assertEquals("DS=" + (scn.DSGetDecision() == null ? null : scn.DSGetDecision().getText())
                + " LS=" + (scn.LSGetDecision() == null ? null : scn.LSGetDecision().getText()), Zone.AT_LOCATION, vader.getZone());
        assertEquals(lsBefore - 2, scn.GetLSForcePileCount());
        return dsBefore - scn.GetDSForcePileCount();
    }

    @Test(timeout = 20000)
    public void darkTextTreatsFreeOnYoureASlaveAsTwoForce() {
        assertEquals(2, forceDarkPaysWhenLightUsesTwoForce("1261_55"));
    }

    @Test(timeout = 20000)
    public void withoutMosEspaVYoureASlaveStillDeploysForFree() {
        assertEquals(0, forceDarkPaysWhenLightUsesTwoForce("11_93"));
    }

    private void start(VirtualTableScenario scn) {
        var slave = scn.GetDSCard("slave");
        scn.MoveCardsToDSHand(slave);
        scn.StartGame();
        scn.MoveLocationToTable(scn.GetDSCard("mos"));
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("trooper"));
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.MoveCardsToDSSideOfTable(slave);
        scn.gameState().flipCard(scn.game(), slave, true);
        assertEquals(Title.Youre_A_Slave, slave.getTitle());
    }

    private void causeLoss(VirtualTableScenario scn, PhysicalCardImpl source, String player) {
        var action = new TopLevelGameTextAction(source, scn.DS, source.getCardId());
        scn.DSExecuteAdHocEffect(source, new LoseForceEffect(action, player, 2));
    }

    private boolean resolveLoss(VirtualTableScenario scn, boolean reduce) {
        boolean reduced = false;
        for (int i = 0; i < 30; i++) {
            var ds = scn.DSGetDecision();
            var ls = scn.LSGetDecision();
            if (reduce && ls != null && ls.getText().toLowerCase().contains("optional response")
                    && scn.LSCardActionAvailable(scn.GetDSCard("mos"), REDUCE)) {
                scn.LSUseCardAction(scn.GetDSCard("mos"), REDUCE);
                reduced = true;
            } else if (scn.DSDecisionAvailable("Choose Force to lose")) {
                scn.DSChooseCard(scn.GetTopOfDSForcePile());
            } else if (scn.LSDecisionAvailable("Choose Force to lose")) {
                scn.LSChooseCard(scn.GetTopOfLSForcePile());
            } else if (ls != null && ls.getText().toLowerCase().contains("optional response")) {
                scn.LSPass();
            } else if (ds != null && ds.getText().toLowerCase().contains("optional response")) {
                scn.DSPass();
            } else {
                return reduced;
            }
        }
        fail("Force loss did not settle");
        return false;
    }

    private void restoreDarkPriority(VirtualTableScenario scn) {
        if (scn.AwaitingLSControlPhaseActions())
            scn.LSPass();
        assertTrue(scn.AwaitingDSControlPhaseActions());
    }
}
