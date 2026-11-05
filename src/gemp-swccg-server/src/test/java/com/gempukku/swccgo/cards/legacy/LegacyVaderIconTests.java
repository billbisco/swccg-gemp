package com.gempukku.swccgo.cards.legacy;

import com.gempukku.swccgo.common.Icon;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;

/**
 * Icon.VADER is the [Vader] icon printed on Virtual Block 7 Vader cards.
 */
public class LegacyVaderIconTests {

    @Test
    public void VaderIconIsParsedByName() {
        //test1: deck-builder filters and formats look icons up by enum name
        assertSame(Icon.VADER, Icon.getIconFromName("VADER")); //test1
    }

    @Test
    public void VaderIconDisplaysAsVaderAndIsNotAnExpansionIcon() {
        //test1: display name
        //test2: it is a card icon, not a set icon
        assertEquals("Vader", Icon.VADER.getHumanReadable()); //test1
        assertFalse(Icon.VADER.isExpansionIcon()); //test2
    }
}
