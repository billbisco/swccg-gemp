package com.gempukku.swccgo.cards.set2272.light;

import com.gempukku.swccgo.cards.set1272.light.Card1272_018;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 7.2
 * Type: Character
 * Subtype: Alien
 * Title: Tarfful, Wookiee Insurgent
 * Version PC-E2 of 1272_18: the errata posted on the PC rules forum on 2013-04-13 (t=36574, p802181) that added
 * "(as if just lost)" to the stacking text, rescinded on 2013-04-15 (p802670); the card then went back to the PC-E1
 * text (the current card). Bill (2026-11-10): rescinded errata are versions. GEMP plays it like the current card (the
 * inactive/supporting state of a Wookiee stacked on Grrrghrrrgh! is not modelled here). Anything Goes only
 * (docs/legacy-versions.md).
 */
public class Card2272_1018 extends Card1272_018 {
    public Card2272_1018() {
        super();
        setGameText("\"Occupy\" on Forest Depths is treated as \"control.\" Non-unique Wookiees here are Heroic. Once per game, when deployed, may 'roar' to take one card into hand from Used Pile (reshuffle) and/or stack a Wookiee (as if just lost) from hand on Grrrghrrrgh!.");
        setLegacyVersion(new LegacyVersion("1272_18", "PC-E2",
                "PC forum errata of 2013-04-13 (Current Rulings/Errata, p802181), rescinded 2013-04-15 (p802670)",
                LocalDate.of(2013, 4, 13), LocalDate.of(2013, 4, 15)));
    }
}
