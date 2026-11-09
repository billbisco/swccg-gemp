package com.gempukku.swccgo.cards.set2220.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.GameUtils;

import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.*;
import static org.junit.Assert.*;

/** Vader's Bounty: original (pre-errata) version of 1220_50. */
public class Card_2220_050_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{

                }},
                new HashMap<>() {{
                    put("boba", "5_91");
                    put("version", "2220_50");
                    put("current", "1220_50");
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
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_2, card.getExpansionSet());
        assertTrue(card.isLegacy());
        assertEquals("Deploy on table. During battle, while non-[Reflections 3] Boba Fett with Vader, neither may be excluded or lost until the damage segment. Slave I is destiny +2 when drawn for weapon or battle destiny. May place this Effect in Lost Pile to make Boba Fett's total weapon destiny +3.",
                card.getGameText());
        var version = card.getLegacyVersion();
        assertEquals("1220_50", version.currentPrintingId());
        assertEquals("", version.label());
        assertEquals(LocalDate.of(2009, 12, 7), version.printedOn());
        assertEquals(LocalDate.of(2012, 7, 21), version.supersededOn());
        assertEquals(GameUtils.getFullName(current), GameUtils.getFullName(card));
    }

    @Test
    public void VersionIsTheSameUniqueCardAsTheCurrentPrinting() {
        for (boolean currentOnTable : java.util.List.of(true, false)) {
            for (boolean otherOut : java.util.List.of(false, true)) {
                var scn = GetScenario();
                var onTable = scn.GetDSCard(currentOnTable ? "current" : "version");
                var inHand = scn.GetDSCard(currentOnTable ? "version" : "current");
                scn.StartGame();

                if (otherOut)
                    scn.MoveCardsToDSSideOfTable(onTable);
                scn.MoveCardsToDSHand(inHand);
                scn.SkipToDSTurn(Phase.DEPLOY);
                scn.EnsureDSForcePile(5);
                assertEquals("current on table=" + currentOnTable + ", other version out=" + otherOut,
                        !otherOut, scn.DSCardPlayAvailable(inHand));
            }
        }
    }

    @Test
    public void MayPlaceInLostPileOutsideBattleWithoutVader() {
        var scn = GetScenario();
        var bounty = scn.GetDSCard("version");
        var current = scn.GetDSCard("current");
        var boba = scn.GetDSCard("boba");

        //test2: offered with Boba Fett alone, outside battle (the current card needs a battle with Vader)
        //test3: placed in Lost Pile
        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(bounty);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), boba);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSCardActionAvailable(bounty, "Place in Lost Pile")); //test2
        scn.DSUseCardAction(bounty, "Place in Lost Pile");
        scn.PassAllResponses();
        assertInZone(Zone.LOST_PILE, bounty); //test3
    }

    @Test
    public void NotOfferedWithoutBobaFettOnTable() {
        var scn = GetScenario();
        var bounty = scn.GetDSCard("version");
        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(bounty);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertFalse(scn.DSCardActionAvailable(bounty, "Place in Lost Pile"));
    }
}
