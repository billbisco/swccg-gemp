package com.gempukku.swccgo.cards.set2271.dark;

import com.gempukku.swccgo.cards.set601.dark.Card601_076;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 7.1
 * Type: Effect
 * Title: Combat Response (V)
 * Original version of 1271_9 (class 601_76): the Virtual Block 7.1 release sheet (2010-06-18; copy re-made 2010-07-28) text.
 * Superseded by the PC website card list text (PC-E1, by 2012-07-21: "to a system within 3 parsecs" for "to system within 3 parsecs"), the current card.
 * Plays the same as the current card; only the wording differs. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2271_009 extends Card601_076 {
    public Card2271_009() {
        super();
        setGameText("Deploy on table. Tallon Roll is canceled. Once per turn, may reveal an unpiloted starfighter from hand to [upload] its matching pilot character (or vice versa) and deploy both simultaneously. Your unique (•) TIEs (except Scythe Squadron TIEs) at systems may use 1 Force to relocate (as a regular move) to system within 3 parsecs. [Immune to Alter]");
        setLegacyVersion(new LegacyVersion("1271_9", "",
                "Virtual Block 7.1 release sheet (2010-06-18; copy re-made 2010-07-28)", LocalDate.of(2010, 6, 18), LocalDate.of(2012, 7, 21)));
    }
}
