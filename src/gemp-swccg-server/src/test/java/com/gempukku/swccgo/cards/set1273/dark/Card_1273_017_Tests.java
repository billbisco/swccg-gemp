package com.gempukku.swccgo.cards.set1273.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1273_017_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("obiwan", "1_21"); // Obi-Wan Kenobi (Jedi)
                }},
                new HashMap<>() {{
                    put("quarters", "1273_17");
                    put("grievous", "203_27"); // General Grievous
                    put("prisoner", "211_11"); // Insidious Prisoner (Set 11)
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
    public void PalpatinesQuartersStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("quarters").getBlueprint();
        assertEquals("Coruscant: Palpatine's Quarters", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(CardSubtype.SITE, card.getCardSubtype());
        assertEquals(2, card.getIconCount(Icon.DARK_FORCE));
        assertEquals(1, card.getIconCount(Icon.LIGHT_FORCE));
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.LOCATION);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DARK_FORCE);
            add(Icon.LIGHT_FORCE);
            add(Icon.INTERIOR_SITE);
            add(Icon.EPISODE_I);
            add(Icon.PLANET);
            add(Icon.LEGACY_BLOCK_7);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void PalpatinesQuartersLightForceDrainIsMinusOne() {
        var scn = GetScenario();
        var quarters = scn.GetDSCard("quarters");
        var obiwan = scn.GetLSCard("obiwan");
        scn.StartGame();
        scn.MoveLocationToTable(quarters);
        scn.MoveCardsToLocation(quarters, obiwan);
        // Light drains on the two Dark icons, minus one
        assertEquals(1, scn.game().getModifiersQuerying().getForceDrainAmount(scn.gameState(), quarters, scn.LS), scn.epsilon);
    }

    @Test
    public void PalpatinesQuartersJediHereMayNotHavePowerReduced() {
        var scn = GetScenario();
        var quarters = scn.GetDSCard("quarters");
        var obiwan = scn.GetLSCard("obiwan");
        scn.StartGame();
        scn.MoveLocationToTable(quarters);
        int printed = scn.GetPower(obiwan);
        scn.ApplyAdHocModifier(new PowerModifier(scn.GetDSStartingLocation(), obiwan, -2));
        assertEquals(printed - 2, scn.GetPower(obiwan));
        scn.MoveCardsToLocation(quarters, obiwan);
        assertEquals(printed, scn.GetPower(obiwan));
    }

    @Test
    public void PalpatinesQuartersGrievousImmuneToAttritionOnlyWhileInsidiousPrisonerOnTable() {
        var scn = GetScenario();
        var quarters = scn.GetDSCard("quarters");
        var grievous = scn.GetDSCard("grievous");
        var prisoner = scn.GetDSCard("prisoner");
        scn.StartGame();
        scn.MoveLocationToTable(quarters);
        scn.MoveCardsToLocation(quarters, grievous);
        var qs = scn.game().getModifiersQuerying();
        assertEquals(4, qs.getImmunityToAttritionLessThan(scn.gameState(), grievous), scn.epsilon);
        scn.AttachCardsTo(quarters, prisoner);
        assertEquals(Float.MAX_VALUE, qs.getImmunityToAttritionLessThan(scn.gameState(), grievous), scn.epsilon);
    }
}
