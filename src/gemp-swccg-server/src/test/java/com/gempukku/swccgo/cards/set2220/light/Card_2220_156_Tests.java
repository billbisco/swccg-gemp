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

/** Through The Force Things You Will See: original (pre-errata) version of 1220_156. */
public class Card_2220_156_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("obiwan", "1_21");
                    put("second", "2220_156");
                    put("version", "2220_156");
                    put("current", "1220_156");
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
        assertEquals("If you are about to recirculate at the end of your turn, and you have fewer cards in Used Pile than Force Pile, place your Used Pile under your Force Pile instead. OR Place the top card of your Used Pile on top of your Force Pile.",
                card.getGameText());
        var version = card.getLegacyVersion();
        assertEquals("1220_156", version.currentPrintingId());
        assertEquals("", version.label());
        assertEquals(LocalDate.of(2009, 12, 7), version.printedOn());
        assertEquals(LocalDate.of(2013, 4, 27), version.supersededOn());
        assertEquals(GameUtils.getFullName(current), GameUtils.getFullName(card));
    }

    private static final String PLACE = "Place top card of Used Pile on Force Pile";

    private void play(VirtualTableScenario scn, com.gempukku.swccgo.game.PhysicalCardImpl card, com.gempukku.swccgo.game.PhysicalCardImpl used) {
        for (int i = 0; i < 16; i++) {
            if (scn.LSAnyActionsAvailable() && scn.LSCardPlayAvailable(card, PLACE))
                break;
            if (scn.LSGetDecision() != null && !scn.LSAnyActionsAvailable()) {
                scn.LSPass();
                continue;
            }
            if (scn.DSGetDecision() != null) {
                scn.DSPass();
                continue;
            }
            scn.PassAllResponses();
        }
        assertTrue(scn.LSCardPlayAvailable(card, PLACE));
        scn.LSPlayCard(card, PLACE);
        for (int i = 0; i < 12 && used.getZone() != Zone.FORCE_PILE && used.getZone() != Zone.TOP_OF_FORCE_PILE; i++)
            scn.PassAllResponses();
        assertInZone(Zone.FORCE_PILE, used);
    }

    @Test
    public void IsAnOutOfPlayInterruptWithNoOncePerGameLimit() {
        var scn = GetScenario();
        var through = scn.GetLSCard("version");
        var second = scn.GetLSCard("second");
        var luke = scn.GetLSCard("luke");
        var obiwan = scn.GetLSCard("obiwan");

        //test1: Out Of Play Interrupt
        //test2: placed out of play after use
        //test3: a second copy is still playable in the same game (the current card: once per game)
        assertEquals(CardSubtype.OUT_OF_PLAY, through.getBlueprint().getCardSubtype()); //test1
        scn.StartGame();
        scn.MoveCardsToLSHand(through, second);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfLSUsedPile(luke);
        play(scn, through, luke);
        assertInZone(Zone.OUT_OF_PLAY, through); //test2
        scn.MoveCardsToTopOfLSUsedPile(obiwan);
        play(scn, second, obiwan); //test3
    }
}
