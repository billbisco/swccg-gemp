package com.gempukku.swccgo.cards.set2210.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.GameUtils;

import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.*;
import static org.junit.Assert.*;

/** Death Star Gunner: original (pre-errata) version of 1210_8. */
public class Card_2210_008_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("detention", "1_284");
                    put("bay", "1_285");
                    put("superlaser", "2_161");
                    put("cpi", "2_130");
                    put("version", "2210_8");
                    put("current", "1210_8");
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
    public void VersionHasTheOldTextAndVersionInfo() {
        var scn = GetScenario();
        var card = scn.GetDSCard("version").getBlueprint();
        var current = scn.GetDSCard("current").getBlueprint();

        assertEquals(current.getTitle(), card.getTitle());
        assertEquals(current.getUniqueness(), card.getUniqueness());
        assertEquals(current.getSide(), card.getSide());
        assertEquals(current.hasVirtualSuffix(), card.hasVirtualSuffix());
        assertEquals(current.getCardTypes(), card.getCardTypes());
        for (Icon icon : Icon.values())
            assertEquals(icon.name(), current.getIconCount(icon), card.getIconCount(icon));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_1, card.getExpansionSet());
        assertTrue(card.isLegacy());
        assertEquals("While on Death Star: cancels opponent's game text on your non-docking bay Death Star sites, your [A New Hope] Epic Event total is cumulatively +2, and, during your deploy phase, may place this character in Used Pile to take a Superlaser into hand from Reserve Deck; reshuffle.",
                card.getGameText());
        var version = card.getLegacyVersion();
        assertEquals("1210_8", version.currentPrintingId());
        assertEquals("", version.label());
        assertEquals(LocalDate.of(2009, 12, 7), version.printedOn());
        assertEquals(LocalDate.of(2012, 8, 27), version.supersededOn());
        assertEquals(GameUtils.getFullName(current), GameUtils.getFullName(card));
    }

    @Test
    public void OnDeathStarCancelsOpponentsGameTextOnYourNonDockingBayDeathStarSites() {
        var scn = GetScenario();
        var gunner = scn.GetDSCard("version");
        var detention = scn.GetDSCard("detention");
        var bay = scn.GetDSCard("bay");

        //test1: Light Side game text of Detention Block Corridor is canceled
        //test2: your own (Dark Side) game text there is not
        //test3: the docking bay keeps its Light Side game text
        scn.StartGame();
        scn.MoveLocationToTable(detention);
        scn.MoveLocationToTable(bay);
        scn.MoveCardsToLocation(detention, gunner);
        scn.SkipToLSTurn(Phase.DEPLOY); // the game text rule cancels the text when the table changes
        assertTrue(detention.isLocationGameTextCanceledForPlayer(scn.LS)); //test1
        assertFalse(detention.isLocationGameTextCanceledForPlayer(scn.DS)); //test2
        assertFalse(bay.isLocationGameTextCanceledForPlayer(scn.LS)); //test3
    }

    @Test
    public void NotOnDeathStarCancelsNothing() {
        var scn = GetScenario();
        var gunner = scn.GetDSCard("version");
        var detention = scn.GetDSCard("detention");

        scn.StartGame();
        scn.MoveLocationToTable(detention);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), gunner);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertFalse(detention.isLocationGameTextCanceledForPlayer(scn.LS));
    }

    @Test
    public void CanceledLightSideTextMeansNoForceDrainBonusThere() {
        var scn = GetScenario();
        var gunner = scn.GetDSCard("version");
        var detention = scn.GetDSCard("detention");
        var luke = scn.GetLSCard("luke");

        scn.StartGame();
        scn.MoveLocationToTable(detention);
        scn.MoveCardsToLocation(detention, gunner, luke);
        scn.EnsureLSForcePile(5);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertFalse(scn.LSForceDrainAvailable(detention));
    }

    @Test
    public void AddsTwoToAnhEpicEventTotalAndTakesSuperlaser() {
        var scn = GetScenario();
        var gunner = scn.GetDSCard("version");
        var detention = scn.GetDSCard("detention");
        var cpi = scn.GetDSCard("cpi");
        var superlaser = scn.GetDSCard("superlaser");

        scn.StartGame();
        scn.MoveLocationToTable(detention);
        scn.MoveCardsToLocation(detention, gunner);
        scn.MoveCardsToDSSideOfTable(cpi);
        assertEquals(2, scn.game().getModifiersQuerying().getEpicEventCalculationTotal(scn.gameState(), cpi, 0), scn.epsilon);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfDSReserveDeck(superlaser);
        assertTrue(scn.DSCardActionAvailable(gunner, "Take Superlaser into hand"));
        scn.DSUseCardAction(gunner, "Take Superlaser into hand");
        scn.PassAllResponses();
        scn.PassCardAndForceUseResponses();
        scn.DSChooseCard(superlaser);
        scn.PassAllResponses();
        assertEquals(Zone.HAND, superlaser.getZone());
    }
}
