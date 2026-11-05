package com.gempukku.swccgo.logic.timing.processes.pregame;

import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.TestBase;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

/**
 * swccg-guild/work#143, game option "Starting: Decipher location rules": when both players choose the same unique
 * starting location, both set it aside and choose again, with no conversion offered (Decipher Glossary Supplement,
 * 2002, p.19). Without it the PC rule applies: the Dark Side player is asked whether to allow conversion.
 */
public class DecipherLocationRulesTests {
    private static final String DS = TestBase.DS;
    private static final String LS = TestBase.LS;

    private VirtualTableScenario scenario(boolean decipher) {
        VirtualTableScenario scn = new VirtualTableScenario(
                new HashMap<>() {{
                    put("tatooine", "1_127");  // Tatooine (Light Side system)
                    put("db94", "1_129");      // Tatooine: Docking Bay 94
                }},
                new HashMap<>() {{
                    put("tatooine", "1_289");  // Tatooine (Dark Side system)
                    put("market", "12_176");   // Tatooine: Marketplace
                }}, 30, 30,
                StartingSetup.DoNothingSetup, StartingSetup.DoNothingSetup,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields,
                VirtualTableScenario.Open);
        scn.gameState().setDecipherLocationRules(decipher);
        if (scn.DSDecisionAvailable("Select OK to start game")) scn.DSDecided("0");
        if (scn.LSDecisionAvailable("Select OK to start game")) scn.LSDecided("0");
        scn.DSChooseCard(scn.GetDSCard("tatooine"));
        scn.LSChooseCard(scn.GetLSCard("tatooine"));
        return scn;
    }

    @Test
    public void DecipherLocationRulesBothChooseAgainWithoutConversion() {
        VirtualTableScenario scn = scenario(true);
        assertFalse("no conversion question", scn.DSDecisionAvailable("Will you allow the other player to convert"));
        assertTrue("Dark Side chooses again", scn.DSDecisionAvailable("Choose starting location"));
        assertTrue("Light Side chooses again", scn.LSDecisionAvailable("Choose starting location"));
        List<String> offered = Arrays.asList(scn.GetAwaitingDecisionParams(DS).get("blueprintId"));
        assertFalse("Tatooine is set aside: " + offered, offered.contains("1_289"));
        PhysicalCardImpl market = scn.GetDSCard("market");
        PhysicalCardImpl db94 = scn.GetLSCard("db94");
        scn.DSChooseCard(market);
        scn.LSChooseCard(db94);
        StringBuilder pending = new StringBuilder();
        for (String p : new String[]{DS, LS}) {
            var d = scn.userFeedback().getAwaitingDecision(p);
            if (d != null) pending.append(p).append("=").append(d.getText()).append("; ");
        }
        assertTrue("market=" + market.getZone() + " db94=" + db94.getZone() + " pending: " + pending, market.getZone().isInPlay());
        // two Tatooine sites: the second may ask on which side of the first it goes
        for (int i = 0; i < 3 && !db94.getZone().isInPlay(); i++) {
            if (scn.LSAnyDecisionsAvailable()) scn.PlayerDecided(LS, "0");
            else if (scn.DSAnyDecisionsAvailable()) scn.PlayerDecided(DS, "0");
        }
        assertTrue("db94=" + db94.getZone(), db94.getZone().isInPlay());
    }

    @Test
    public void PCRulesAskTheDarkSidePlayerAboutConversion() {
        VirtualTableScenario scn = scenario(false);
        assertTrue(scn.DSDecisionAvailable("Will you allow the other player to convert"));
    }
}
