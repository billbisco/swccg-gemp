package com.gempukku.swccgo.cards.set2220.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.GameUtils;

import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.*;
import static org.junit.Assert.*;

/** Wars Not Make One Great: original (pre-errata) version of 1220_136. */
public class Card_2220_136_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("inner1", "13_24");
                    put("inner2", "13_24");
                    put("version", "2220_136");
                    put("current", "1220_136");
                }},
                new HashMap<>() {{

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
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_2, card.getExpansionSet());
        assertTrue(card.isLegacy());
        assertEquals("Deploy on table. During battles where your Jedi is present, players may not draw more than two battle destiny. During your turn, may take Inner Strength into hand from Reserve Deck; reshuffle. (Immune to Alter.)",
                card.getGameText());
        var version = card.getLegacyVersion();
        assertEquals("1220_136", version.currentPrintingId());
        assertEquals("", version.label());
        assertEquals(LocalDate.of(2009, 12, 7), version.printedOn());
        assertEquals(LocalDate.of(2013, 7, 24), version.supersededOn());
        assertEquals(GameUtils.getFullName(current), GameUtils.getFullName(card));
    }

    @Test
    public void VersionIsTheSameUniqueCardAsTheCurrentPrinting() {
        for (boolean currentOnTable : java.util.List.of(true, false)) {
            for (boolean otherOut : java.util.List.of(false, true)) {
                var scn = GetScenario();
                var onTable = scn.GetLSCard(currentOnTable ? "current" : "version");
                var inHand = scn.GetLSCard(currentOnTable ? "version" : "current");
                scn.StartGame();

                if (otherOut)
                    scn.MoveCardsToLSSideOfTable(onTable);
                scn.MoveCardsToLSHand(inHand);
                scn.SkipToLSTurn(Phase.DEPLOY);
                scn.EnsureLSForcePile(5);
                assertEquals("current on table=" + currentOnTable + ", other version out=" + otherOut,
                        !otherOut, scn.LSCardPlayAvailable(inHand));
            }
        }
    }

    private static final String TAKE = "Take Inner Strength into hand from Reserve Deck";

    private void take(VirtualTableScenario scn, com.gempukku.swccgo.game.PhysicalCardImpl wars, com.gempukku.swccgo.game.PhysicalCardImpl inner) {
        scn.LSUseCardAction(wars, TAKE);
        int safety = 0;
        while (safety++ < 16 && !scn.LSHasCardChoiceAvailable(inner)) {
            if (scn.DSGetDecision() != null)
                scn.DSPass();
            else if (scn.LSGetDecision() != null)
                scn.LSPass();
            else
                break;
        }
        int before = scn.GetLSHandCount();
        scn.LSChooseCard(inner);
        scn.PassAllResponses();
        assertEquals(before + 1, scn.GetLSHandCount());
    }

    @Test
    public void MayTakeInnerStrengthMoreThanOnceDuringYourTurnButNotOnOpponentsTurn() {
        var scn = GetScenario();
        var wars = scn.GetLSCard("version");
        var inner1 = scn.GetLSCard("inner1");
        var inner2 = scn.GetLSCard("inner2");

        //test1: a second take in the same turn (the current card: once during your turn)
        //test2: not during opponent's turn
        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(wars);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfLSReserveDeck(inner1, inner2);
        assertTrue(scn.LSCardActionAvailable(wars, TAKE));
        take(scn, wars, inner1);
        if (scn.AwaitingDSControlPhaseActions())
            scn.DSPass();
        assertTrue(scn.LSCardActionAvailable(wars, TAKE));
        take(scn, wars, inner2); //test1
        scn.MoveCardsToTopOfLSReserveDeck(inner1);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.DSPass();
        assertFalse(scn.LSCardActionAvailable(wars, TAKE)); //test2
    }
}
