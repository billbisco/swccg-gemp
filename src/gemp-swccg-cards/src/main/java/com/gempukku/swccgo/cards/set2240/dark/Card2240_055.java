package com.gempukku.swccgo.cards.set2240.dark;

import com.gempukku.swccgo.cards.set1240.dark.Card1240_055;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 4
 * Type: Location
 * Subtype: Site
 * Title: Kessel: Spice Mines - Extraction Facility
 * Original version of 1240_55: the Virtual Block 4 printing (2009-12-07), whose slip left the Episode I icon of the
 * card underneath (Tatooine: Desert Landing Site) showing, as the PC card image does. On 2011-07-25 the slip was changed
 * to cover it (PC forum p637956: "This location is NOT an 'Episode I' location"; PC-E1, the current card).
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2240_055 extends Card1240_055 {
    public Card2240_055() {
        addIcons(Icon.EPISODE_I);
        setLegacyVersion(new LegacyVersion("1240_55", "",
                "Virtual Block 4 release sheet (2009-12-07), card #54; PC card image", LocalDate.of(2009, 12, 7), LocalDate.of(2011, 7, 25)));
    }
}
