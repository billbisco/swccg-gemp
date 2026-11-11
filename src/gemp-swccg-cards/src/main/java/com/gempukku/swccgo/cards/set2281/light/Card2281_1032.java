package com.gempukku.swccgo.cards.set2281.light;

import com.gempukku.swccgo.cards.set1281.light.Card1281_032;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.1
 * Type: Effect
 * Title: Imperial Navigation Charts
 * PC-E1 version of 1281_32: the errata posted on the PC forum on 2012-07-13 ("Current Rulings/Errata" p739757: "the
 * stealing of the Charts is no longer optional"), printed by the re-made 8.1 sheet (2012-07-18). The PC website card list
 * then printed the optional wording again (PC-E2, 2281_2032); the Master List (2013-04-27) and PC card image print these
 * words again (PC-E3, the current card).
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2281_1032 extends Card1281_032 {
    public Card2281_1032() {
        super();
        setGameText("Deploy on a Nar Shaddaa battleground site. If you control three Nar Shaddaa locations or just won a battle (or initiated a Force drain) here, 'steal' this Effect (relocate to your side of table). Place in Used Pile if opponent controls three Nar Shaddaa locations. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1281_32", "PC-E1", "PC forum errata post p739757 (2012-07-13); re-made Virtual Block 8.1 release sheet (2012-07-18)", LocalDate.of(2012, 7, 13), LocalDate.of(2012, 7, 21)));
    }
}
