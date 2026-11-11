package com.gempukku.swccgo.cards.set2261.dark;

import com.gempukku.swccgo.cards.set1261.dark.Card1261_033;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Type: Effect
 * Title: Dark Waters & Krayt Dragon Bones
 * PC-E2 version of 1261_33: the errata posted on the PC forum on 2010-07-27 ("Current Rulings/Errata" p568455), which
 * moved the creature sentence to the front ("May deploy a non-[Selective] ferocious creature here"; the slips' ▼ is kept
 * here, as icon notation is not a version). Superseded by PC-E3 (forum 2012-05-09, 2261_3033). The PC website card list
 * later printed the same words (PC-E4, 2261_4033). Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_2033 extends Card1261_033 {
    public Card2261_2033() {
        setGameText("Deploy on Rancor Pit. May ▼ a non-[Selective] ferocious creature here. During your battle phase, if you have no presence here, may relocate a captive from Audience Chamber to here. If opponent's character here was just eaten, retrieve 3 Force. If a creature here is lost, owner loses Force equal to half its printed deploy cost (round up). Once per turn, if a creature here is about to draw destiny for ferocity, may instead use its destiny number. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1261_33", "PC-E2",
                "PC forum errata post p568455 (2010-07-27)", LocalDate.of(2010, 7, 27), LocalDate.of(2012, 5, 9)));
    }
}
