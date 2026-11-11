package com.gempukku.swccgo.cards.set2250.light;

import com.gempukku.swccgo.cards.set1250.light.Card1250_068;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Type: Interrupt
 * Subtype: Used
 * Title: Wookiee Guide (V)
 * Version PC-E1 of 1250_68: the errata posted on the PC rules forum on 2013-04-13 (t=36574, p802181) that added
 * "(as if just lost)" to the first function, rescinded on 2013-04-15 (p802670); the card then went back to its
 * original text (the current card). Text as posted ("Reserve deck"). Bill (2026-11-10): rescinded errata are versions.
 * GEMP plays it like the current card (the inactive/supporting state of a Wookiee stacked on Grrrghrrrgh! is not
 * modelled here). Anything Goes only (docs/legacy-versions.md).
 */
public class Card2250_068 extends Card1250_068 {
    public Card2250_068() {
        super();
        setGameText("Stack a Wookiee (as if just lost) from your Lost Pile on Grrrghrrrgh!. OR Deploy a Wookiee stacked on Grrrghrrrgh! to Kashyyyk (as if from hand). OR Once per game, draw X cards from top of Reserve deck, where X = number of cards stacked on Grrrghrrrgh!.");
        setLegacyVersion(new LegacyVersion("1250_68", "PC-E1",
                "PC forum errata of 2013-04-13 (Current Rulings/Errata, p802181), rescinded 2013-04-15 (p802670)",
                LocalDate.of(2013, 4, 13), LocalDate.of(2013, 4, 15)));
    }
}
