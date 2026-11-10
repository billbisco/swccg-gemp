package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.common.Icon;

/**
 * Set: Virtual Block 6.1
 * Type: Defensive Shield
 * Title: Your Insight Serves You Well (Death Star II) (V)
 * Printed on the Death Star II base card (Virtual Block 6.1 slip D58, PC card 6047; Death Star II icon). The PC image also
 * prints destiny 5 and the Effect icon of the Death Star II card (an image error; the shield's destiny stays 0 here). The
 * Reflections III printing (slip D60, "(Ref III)") is 1261_73. Same text and play; own id and image (Bill 2026-11-09, two-base
 * printings). Until B35 this id was an alias of 1261_73.
 */
public class Card1261_075 extends Card1261_073 {
    public Card1261_075() {
        super();
        setBasePrintingTitle("Your Insight Serves You Well (Death Star II)");
        removeIcon(Icon.REFLECTIONS_III);
        addIcon(Icon.DEATH_STAR_II);
    }
}
