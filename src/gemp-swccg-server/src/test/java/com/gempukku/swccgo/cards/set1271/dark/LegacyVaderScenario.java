package com.gempukku.swccgo.cards.set1271.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;

final class LegacyVaderScenario {
    private LegacyVaderScenario() {}

    static VirtualTableScenario scenario() {
        var light = new HashMap<String, String>();
        light.put("luke", "1_19");
        light.put("obi", "1_21");
        light.put("fambaa", "14_59");
        light.put("booma", "14_63");
        light.put("clash", "5_38");
        light.put("farm", "1_132");
        var dark = new HashMap<String, String>();
        dark.put("vader", "1271_2");
        dark.put("rise", "1271_19");
        dark.put("limbs", "1271_13");
        dark.put("cape", "1271_14");
        dark.put("cybernetic", "1271_15");
        dark.put("helmet", "1271_16");
        dark.put("mask", "1271_17");
        dark.put("premiereVader", "1_168");
        dark.put("trooper", "1_194");
        dark.put("devastator", "1_301");
        dark.put("customTie", "1_306");
        dark.put("system", "1_282");
        return new VirtualTableScenario(light, dark, 40, 40,
            StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
            StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
            StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    static VirtualTableScenario board(String equipment) {
        var s = scenario();
        s.StartGame();
        s.MoveCardsToDSSideOfTable(s.GetDSCard("rise"));
        s.MoveCardsToLocation(s.GetDSStartingLocation(), s.GetDSCard("vader"));
        s.MoveLocationToTable(s.GetLSCard("farm"));
        if (equipment != null) s.AttachCardsTo(s.GetDSCard("vader"), s.GetDSCard(equipment));
        return s;
    }

    static void equipmentStats(String key, String title, int destiny, boolean virtual, Icon... additionalIcons) {
        var s = scenario();
        var b = s.GetDSCard(key).getBlueprint();
        assertEquals(title, b.getTitle());
        assertEquals(destiny, b.getDestiny(), s.epsilon);
        assertEquals(Side.DARK, b.getSide());
        assertEquals(Uniqueness.UNIQUE, b.getUniqueness());
        assertEquals(CardCategory.EFFECT, b.getCardCategory());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_1, b.getExpansionSet());
        assertEquals(Rarity.V, b.getRarity());
        assertEquals(virtual, b.hasVirtualSuffix());
        s.BlueprintCardTypeCheck(b, List.of(CardType.EFFECT));
        var icons = new ArrayList<>(List.of(Icon.EFFECT, Icon.VADER, Icon.LEGACY_BLOCK_7));
        icons.addAll(List.of(additionalIcons));
        s.BlueprintIconCheck(b, icons);
        s.BlueprintKeywordCheck(b, List.of());
    }

    static VirtualTableScenario prepareBoomaBattle(String equipment) {
        var s = board(equipment);
        var fambaa = s.GetLSCard("fambaa");
        s.MoveCardsToLocation(s.GetDSStartingLocation(), fambaa, s.GetLSCard("luke"));
        s.AttachCardsTo(fambaa, s.GetLSCard("booma"));
        s.SkipToLSTurn(Phase.CONTROL);
        s.EnsureLSForcePile(10);
        s.SkipToPhase(Phase.BATTLE);
        s.StartBattleAndSkipToWeaponsSegment(s.LS, s.GetDSStartingLocation());
        s.PrepareLSDestiny(0);
        return s;
    }

    static void fireBooma(VirtualTableScenario s) {
        var weapon = s.GetLSCard("booma");
        s.LSUseCardAction(weapon, "Fire");
        for (int i = 0; i < 20; i++) {
            if (s.DSAnyActionsAvailable() && s.DSActionAvailable("Lose 4 Force to take Vader")) break;
            if (!s.GetCurrentDecision().getText().toLowerCase(java.util.Locale.ROOT).contains("optional response")) break;
            s.PlayerPass(s.GetDecidingPlayer());
        }
        assertNotEquals("Booma must draw the prepared card", s.GetLSDestiny(0), s.gameState().getTopOfReserveDeck(s.LS));
        if (s.DSHasCardChoiceAvailable(s.GetDSCard("vader"))) s.DSChooseCard(s.GetDSCard("vader"));
    }
}
