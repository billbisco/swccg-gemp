package com.gempukku.swccgo.logic.timing.processes.pregame;

import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.TestBase;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.DeckInvalidException;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.game.SwccgFormat;
import com.gempukku.swccgo.game.setup.GameOption;
import com.gempukku.swccgo.game.setup.GameOptions;
import com.gempukku.swccgo.game.setup.GameSetup;
import com.gempukku.swccgo.logic.vo.SwccgDeck;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * swccg-guild/work#142 R2b/R2c, game options "Starting: Interrupts" and "Starting: Effects": more than one of each at
 * game start. 1 is the rules.
 */
public class StartingCountsTests {
    private static final String DS = TestBase.DS;

    private VirtualTableScenario scenario(HashMap<String, String> ds, int interrupts, int effects) {
        VirtualTableScenario scn = new VirtualTableScenario(new HashMap<>(), ds, 30, 30,
                StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields,
                "anything_goes");
        scn.gameState().setStartingInterruptsCount(interrupts);
        scn.gameState().setStartingEffectsCount(effects);
        return scn;
    }

    @Test
    public void StartingEffectsTwoDeploysBoth() {
        HashMap<String, String> ds = new HashMap<>() {{
            put("fear", "13_69");   // Fear Is My Ally
            put("kad", "200_110");  // Knowledge And Defense (V)
        }};
        VirtualTableScenario scn = scenario(ds, 1, 2);
        scn.StartGame();
        PhysicalCardImpl fear = scn.GetDSCard("fear");
        PhysicalCardImpl kad = scn.GetDSCard("kad");
        assertTrue("both Starting Effects deploy: fear=" + fear.getZone() + " kad=" + kad.getZone(),
                fear.getZone().isInPlay() && kad.getZone().isInPlay());
    }

    @Test
    public void StartingEffectsOneDeploysOnlyOne() {
        HashMap<String, String> ds = new HashMap<>() {{
            put("fear", "13_69");
            put("kad", "200_110");
        }};
        VirtualTableScenario scn = scenario(ds, 1, 1);
        scn.StartGame();
        int inPlay = (scn.GetDSCard("fear").getZone().isInPlay() ? 1 : 0) + (scn.GetDSCard("kad").getZone().isInPlay() ? 1 : 0);
        assertEquals(1, inPlay);
    }

    @Test
    public void StartingEffectsOptionLetsADeckHoldMore() throws DeckInvalidException {
        var library = VirtualTableScenario._formatLibrary;
        SwccgDeck deck = new SwccgDeck("two effects");
        deck.addCard("13_69");
        deck.addCard("200_110");
        for (int i = 0; i < 58; i++)
            deck.addCard("1_194");
        SwccgFormat open = library.effectiveFormat(GameSetup.of(library.getFormat("open"), Map.of(), GameOption.Scope.CASUAL));
        assertThrows(DeckInvalidException.class, () -> open.validateDeck(deck));
        library.effectiveFormat(GameSetup.of(library.getFormat("open"), Map.of(GameOptions.STARTING_EFFECTS, "2"), GameOption.Scope.CASUAL))
                .validateDeck(deck);
    }

    @Test
    public void StartingInterruptsTwoOffersAMultipleChoice() {
        HashMap<String, String> ds = new HashMap<>() {{
            put("si1", "9_139");
            put("si2", "9_139");
        }};
        VirtualTableScenario scn = scenario(ds, 2, 1);
        if (scn.DSDecisionAvailable("Select OK to start game")) scn.DSDecided("0");
        if (scn.LSDecisionAvailable("Select OK to start game")) scn.LSDecided("0");
        StartingSetup.DefaultDSGroundLocation.Setup(scn);
        StartingSetup.DefaultLSGroundLocation.Setup(scn);
        var decision = scn.userFeedback().getAwaitingDecision(DS);
        assertNotNull("a Starting Interrupt box", decision);
        assertEquals("Choose up to 2 starting interrupts", decision.getText());
        assertEquals("2", decision.getDecisionParameters().get("max")[0]);
    }
}
