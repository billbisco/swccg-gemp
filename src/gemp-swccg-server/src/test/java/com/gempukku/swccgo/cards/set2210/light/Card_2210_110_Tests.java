package com.gempukku.swccgo.cards.set2210.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.GameUtils;

import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.*;
import static org.junit.Assert.*;

/** Rebel Tech: original (pre-errata) version of 1210_110. */
public class Card_2210_110_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("warroom", "1_139");
                    put("ruins", "2_068");
                    put("bay", "1_136");
                    put("attackrun", "2_42");
                    put("advance", "2_43");
                    put("version", "2210_110");
                    put("current", "1210_110");
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
    public void VersionHasTheOldTextAndVersionInfo() {
        var scn = GetScenario();
        var card = scn.GetLSCard("version").getBlueprint();
        var current = scn.GetLSCard("current").getBlueprint();

        assertEquals(current.getTitle(), card.getTitle());
        assertEquals(current.getUniqueness(), card.getUniqueness());
        assertEquals(current.getSide(), card.getSide());
        assertEquals(current.hasVirtualSuffix(), card.hasVirtualSuffix());
        assertEquals(current.getCardTypes(), card.getCardTypes());
        for (Icon icon : Icon.values())
            assertEquals(icon.name(), current.getIconCount(icon), card.getIconCount(icon));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_1, card.getExpansionSet());
        assertTrue(card.isLegacy());
        assertEquals("Dark Side game text on your non-docking bay Yavin 4 sites is canceled. Your Attack Run total is cumulatively +1. During your deploy phase, may place this character in Used Pile to ▲ one Advance Preparation or Death Star: Trench.",
                card.getGameText());
        var version = card.getLegacyVersion();
        assertEquals("1210_110", version.currentPrintingId());
        assertEquals("", version.label());
        assertEquals(LocalDate.of(2009, 12, 7), version.printedOn());
        assertEquals(LocalDate.of(2010, 6, 17), version.supersededOn());
        assertEquals(GameUtils.getFullName(current), GameUtils.getFullName(card));
    }

    @Test
    public void CancelsDarkSideGameTextOnYourNonDockingBayYavin4SitesEvenWhenNotOnYavin4() {
        var scn = GetScenario();
        var tech = scn.GetLSCard("version");
        var ruins = scn.GetLSCard("ruins");
        var bay = scn.GetLSCard("bay");
        var vader = scn.GetDSCard("vader");

        //test1: Dark Side text of Massassi Ruins canceled while Rebel Tech is not on Yavin 4
        //test2: Light Side text not canceled
        //test3: docking bay not affected
        //test4: DS Force drain +1 there is gone (current card: 3)
        scn.StartGame();
        scn.MoveLocationToTable(ruins);
        scn.MoveLocationToTable(bay);
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), tech);
        scn.MoveCardsToLocation(ruins, vader);
        scn.SkipToLSTurn(Phase.DEPLOY); // the game text rule cancels the text when the table changes
        assertTrue(ruins.isLocationGameTextCanceledForPlayer(scn.DS)); //test1
        assertFalse(ruins.isLocationGameTextCanceledForPlayer(scn.LS)); //test2
        assertFalse(bay.isLocationGameTextCanceledForPlayer(scn.DS)); //test3
        scn.EnsureDSForcePile(5);
        scn.SkipToDSTurn(Phase.CONTROL);
        assertTrue(scn.DSForceDrainAvailable(ruins));
        scn.DSForceDrainAt(ruins);
        scn.LSPass();
        scn.PassForceDrainStartResponses();
        assertEquals(2, scn.GetForceDrainTotal()); //test4
    }

    @Test
    public void AddsOneToAttackRunTotalEvenWhenNotOnYavin4() {
        var scn = GetScenario();
        var tech = scn.GetLSCard("version");
        var attackrun = scn.GetLSCard("attackrun");

        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), tech);
        scn.MoveCardsToLSSideOfTable(attackrun);
        assertEquals(1, scn.game().getModifiersQuerying().getEpicEventCalculationTotal(scn.gameState(), attackrun, 0), scn.epsilon);
    }

    @Test
    public void PlacesSelfInUsedPileToTakeAdvancePreparationEvenWhenNotOnYavin4() {
        var scn = GetScenario();
        var tech = scn.GetLSCard("version");
        var advance = scn.GetLSCard("advance");

        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), tech);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSReserveDeck(advance);
        assertTrue(scn.LSCardActionAvailable(tech, "Take card into hand from Reserve Deck"));
        scn.LSUseCardAction(tech, "Take card into hand from Reserve Deck");
        scn.PassAllResponses();
        scn.PassCardAndForceUseResponses();
        scn.LSChooseCard(advance);
        scn.PassAllResponses();
        assertTrue(tech.getZone() == Zone.USED_PILE || tech.getZone() == Zone.TOP_OF_USED_PILE);
        assertEquals(Zone.HAND, advance.getZone());
    }
}
