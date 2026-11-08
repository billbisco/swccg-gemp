package com.gempukku.swccgo.cards.set2261.light;

import com.gempukku.swccgo.cards.set1261.light.Card1261_105;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Type: Interrupt
 * Title: Odin Nesloor & First Aid
 * Original version of 1261_105: the Virtual Block 6.1 release sheet text, "free if reduced by", "(who is also protected ...)" and "place this Interrupt" (the PC card list, PC-E1, rewords them).
 * Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_105 extends Card1261_105 {
    public Card2261_105() {
        setGameText("If your character's forfeit was just reduced to 0, use 1 Force (free if reduced by a [Permanent Weapon] weapon) to restore it to normal. (Immune to Sense.) OR Cancel an attempt to 'operate' on a character (who is also protected from any further attempts until start of opponent's next turn). OR During your move phase, target any or all of your characters at one site to 'transport' (relocate) to an exterior or battleground site. Draw destiny. Use that much Force to 'transport,' or place this Interrupt in Lost Pile.");
        setLegacyVersion(new LegacyVersion("1261_105", "",
                "Virtual Block 6.1 release sheet (2009-09-04), card #95", LocalDate.of(2009, 9, 4), LocalDate.of(2012, 7, 21)));
    }
}
