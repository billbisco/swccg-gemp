package com.gempukku.swccgo.cards.set2250.light;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;

import static com.gempukku.swccgo.cards.LegacyVersionAssertions.assertLegacyVersion;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/** Pre-errata We'll Handle This (V) (Dec 2009 sheet), a version of 1250_82: no New Republic deployment restriction. */
public class Card_2250_082_Tests {
    private static StartingSetup councilStart(String objectiveId) {
        return new StartingSetup() {
            @Override
            public HashMap<String, String> Cards() {
                return new HashMap<>() {{ put("objective", objectiveId); put("chamber", "12_76"); }};
            }

            @Override
            public void Setup(VirtualTableScenario s) {
                if (s.LSDecisionAvailable("Choose your Objective")) s.LSChooseCard(s.GetLSCard("objective"));
                if (s.LSDecisionAvailable("Choose Jedi Council Chamber")) s.LSChooseCard(s.GetLSCard("chamber"));
            }
        };
    }

    private VirtualTableScenario scenario(String objectiveId) {
        return scenario(objectiveId, false);
    }

    private VirtualTableScenario scenario(String objectiveId, boolean withCurrent) {
        return new VirtualTableScenario(new HashMap<>() {{
            if (withCurrent) put("current", "1250_82");
            put("newrepublic", "601_46"); put("luke", "1_19"); put("senate", "12_75");
            put("yoda", "12_35"); put("depa", "12_4"); put("mace", "12_13");
        }}, new HashMap<>() {{
            put("trooper", "1_194");
        }}, 40, 40, councilStart(objectiveId), StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void WellHandleThisOriginalIdentity() {
        var s = scenario("2250_82", true);
        var objective = s.GetLSCard("objective");
        assertLegacyVersion(objective.getBlueprint(), s.GetLSCard("current").getBlueprint(), "1250_82", "",
                LocalDate.of(2009, 12, 7), LocalDate.of(2012, 8, 4),
                "Deploy Jedi Council Chamber. For remainder of game, you may not deploy Affect Mind, Senate, Rebel starships, or Rebels of ability > 2. You lose no Force to Vengeance Of The Dark Prince or Your Destiny. Jedi Survivor's [Maintenance] is canceled. Speak With The Jedi Council is a Used Interrupt. While this side up, Yoda is deploy -3 to Jedi Council Chamber. Your Force drains are -1. Flip this card if Yoda and two other Jedi are at Jedi Council Chamber.");
        var back = objective.getOtherSideBlueprint();
        assertEquals(Title.Duel_Of_The_Fates, back.getTitle());
        assertEquals(s.GetLSCard("current").getOtherSideBlueprint().getGameText(), back.getGameText());
        assertEquals("1250_82", back.getLegacyVersion().currentPrintingId());
    }

    private void checkNewRepublic(String objectiveId, boolean mayDeploy) {
        var s = scenario(objectiveId);
        s.StartGame();
        assertTrue(s.GetLSCard("objective").isObjectiveDeploymentComplete());
        var jaina = s.GetLSCard("newrepublic");
        var luke = s.GetLSCard("luke");
        var senate = s.GetLSCard("senate");
        s.MoveCardsToHand(jaina, luke, senate);
        s.SkipToLSTurn(Phase.DEPLOY);
        s.LSActivateForceCheat(12);
        assertEquals(mayDeploy, s.LSDeployAvailable(jaina));
        assertFalse(s.LSDeployAvailable(luke));
        assertFalse(s.LSDeployAvailable(senate));
    }

    @Test
    public void WellHandleThisOriginalAllowsNewRepublicCharacters() {
        checkNewRepublic("2250_82", true);
    }

    @Test
    public void CurrentWellHandleThisBlocksNewRepublicCharacters() {
        checkNewRepublic("1250_82", false);
    }

    @Test
    public void WellHandleThisOriginalFlipsToItsOwnBack() {
        var s = scenario("2250_82");
        s.StartGame();
        var chamber = s.GetLSCard("chamber");
        s.MoveCardsToLocation(chamber, s.GetLSCard("depa"), s.GetLSCard("mace"), s.GetLSCard("yoda"));
        s.SkipToLSTurn(Phase.DEPLOY);
        var objective = s.GetLSCard("objective");
        assertTrue(objective.isFlipped());
        assertEquals(Title.Duel_Of_The_Fates, objective.getBlueprint().getTitle());
        assertEquals("1250_82", objective.getBlueprint().getLegacyVersion().currentPrintingId());
    }
}
