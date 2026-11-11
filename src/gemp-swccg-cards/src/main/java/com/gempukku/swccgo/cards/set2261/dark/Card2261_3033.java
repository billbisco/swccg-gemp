package com.gempukku.swccgo.cards.set2261.dark;

import com.gempukku.swccgo.cards.set1261.dark.Card1261_033;
import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Type: Effect
 * Title: Dark Waters & Krayt Dragon Bones
 * PC-E3 version of 1261_33: the errata posted on the PC forum on 2012-05-09 ("Current Rulings/Errata" p719737: "Obviously
 * it obsoletes any previous errata to this card"): deploys on Rancor Pit or Execution Arena (Pit) and relocates a captive
 * only while on Rancor Pit. The PC website card list (by 2012-07-21) printed the PC-E2 words again (PC-E4, 2261_4033).
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_3033 extends Card1261_033 {
    public Card2261_3033() {
        setGameText("Deploy on Rancor Pit or Execution Arena. During your battle phase, if on Rancor Pit and you have no presence here, may relocate a captive from Audience Chamber to here. If opponent's character here was just eaten, retrieve 3 Force. If a creature here is lost, owner loses Force equal to half its printed deploy cost (round up). Once per turn, if a creature here is about to draw destiny for ferocity, may instead use its destiny number. May ▼ a non-[Selective] ferocious creature here. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1261_33", "PC-E3",
                "PC forum errata post p719737 (2012-05-09)", LocalDate.of(2012, 5, 9), LocalDate.of(2012, 7, 21)));
    }

    @Override
    protected Filter deployTarget() {
        return Filters.or(Filters.Rancor_Pit, Filters.title("Execution Arena (Pit)"));
    }

    @Override
    protected boolean relocationNeedsRancorPit() {
        return true;
    }
}
