package com.gempukku.swccgo.game.setup;

import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * swccg-guild/work#175: option labels and chips read "Area: setting" (Bill 2026-11-04), and the form lists the options
 * by area.
 */
public class OptionWordingTests {
    private static final List<String> AREAS = List.of("Deck", "Shields", "Starting", "Objectives", "Timer");

    private static String area(String text) {
        return text.substring(0, text.indexOf(": "));
    }

    @Test
    public void EveryLabelAndChipReadsAreaSetting() {
        var anythingGoes = VirtualTableScenario._formatLibrary.getFormat("anything_goes");
        for (GameOption option : GameOptions.all()) {
            assertTrue(option.getLabel(), option.getLabel().contains(": ") && AREAS.contains(area(option.getLabel())));
            String value = option.getType() == GameOption.Type.BOOLEAN ? "true"
                    : option.getType() == GameOption.Type.CHOICE ? option.getDefault(anythingGoes) : "2";
            String chip = option.getShortLabel(value);
            assertEquals(option.getId() + " chip " + chip, area(option.getLabel()), area(chip));
        }
    }

    @Test
    public void TheFormListsOptionsByArea() {
        List<String> areas = new ArrayList<>();
        for (GameOption option : GameOptions.all()) {
            String a = area(option.getLabel());
            if (areas.isEmpty() || !areas.get(areas.size() - 1).equals(a))
                areas.add(a);
        }
        assertEquals(AREAS, areas);
    }
}
