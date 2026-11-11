package com.gempukku.swccgo.cards.set2263.light;

import com.gempukku.swccgo.cards.set1263.light.Card1263_026;
import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.3
 * Type: Character
 * Subtype: Alien
 * Title: Phylo Gandish (V)
 * Original version of 1263_26: the Virtual Block 6.3 release sheet (official text as of 2010-03-15, released 2010-03-12;
 * also the PC card image, the PC website card list and the 2013 Master List): only opponent's [Virtual Block 6] cards.
 * Superseded by the errata posted on the PC forum on 2013-10-30 (p847474), which added [Virtual Block 4] (PC-E1, the
 * current card). Anything Goes only (docs/legacy-versions.md).
 */
public class Card2263_026 extends Card1263_026 {
    public Card2263_026() {
        setGameText("Adds 2 to power of anything she pilots. If opponent's [Virtual Block 6] card just added a destiny to total power or attrition here, draw two destiny, choose one, and subtract it from their attrition and total power. Your characters here are immune to You Are Beaten and attrition < 3.");
        setLegacyVersion(new LegacyVersion("1263_26", "",
                "Virtual Block 6.3 release sheet (2010-03-15)", LocalDate.of(2010, 3, 12), LocalDate.of(2013, 2, 25)));
    }

    @Override
    protected Filterable opponentsCardIcons() {
        return Icon.LEGACY_BLOCK_6;
    }
}
