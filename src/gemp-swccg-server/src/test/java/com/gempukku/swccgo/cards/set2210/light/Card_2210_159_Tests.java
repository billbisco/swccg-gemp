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

/** Precise Hit: original (pre-errata) version of 1210_159. */
public class Card_2210_159_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("chewie", "1210_94");
                    put("han", "1_11");
                    put("version", "2210_159");
                    put("current", "1210_159");
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
        assertEquals("USED: If you just hit a target with a non-lightsaber non-[Permanent Weapon] weapon, place target (and all cards on it) in owner's Used Pile. LOST: Cancel Heroic (and any immunity to attrition) on a character or starship this turn.",
                card.getGameText());
        var version = card.getLegacyVersion();
        assertEquals("1210_159", version.currentPrintingId());
        assertEquals("", version.label());
        assertEquals(LocalDate.of(2009, 12, 7), version.printedOn());
        assertEquals(LocalDate.of(2012, 7, 21), version.supersededOn());
        assertEquals(GameUtils.getFullName(current), GameUtils.getFullName(card));
    }

    @Test
    public void LostCancelsImmunityToAttritionOnANonHeroicCharacter() {
        var scn = GetScenario();
        var precise = scn.GetLSCard("version");
        var vader = scn.GetDSCard("vader");

        //test1: Vader is not Heroic but may be chosen (the current card needs a Heroic target)
        //test2: his immunity to attrition is canceled this turn
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), vader);
        scn.MoveCardsToLSHand(precise);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertFalse(scn.HasKeyword(vader, Keyword.HEROIC));
        assertEquals(5, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), vader), scn.epsilon);
        assertTrue(scn.LSCardPlayAvailable(precise, "Cancel Heroic")); //test1
        scn.LSPlayCard(precise, "Cancel Heroic");
        if (scn.LSHasCardChoiceAvailable(vader))
            scn.LSChooseCard(vader);
        scn.PassAllResponses();
        assertEquals(0, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), vader), scn.epsilon); //test2
        assertInZone(Zone.LOST_PILE, precise);
    }

    @Test
    public void LostCancelsHeroicOnAHeroicCharacter() {
        var scn = GetScenario();
        var precise = scn.GetLSCard("version");
        var chewie = scn.GetLSCard("chewie");
        var han = scn.GetLSCard("han");

        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), chewie, han);
        scn.MoveCardsToLSHand(precise);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.HasKeyword(chewie, Keyword.HEROIC));
        scn.LSPlayCard(precise, "Cancel Heroic");
        if (scn.LSHasCardChoiceAvailable(chewie))
            scn.LSChooseCard(chewie);
        scn.PassAllResponses();
        assertFalse(scn.HasKeyword(chewie, Keyword.HEROIC));
    }
}
