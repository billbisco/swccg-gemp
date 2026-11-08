package com.gempukku.swccgo.cards.set2240.light;

import com.gempukku.swccgo.cards.set1240.light.Card1240_068;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 4
 * Type: Character
 * Title: LE-BO2D9 (Leebo)
 * Original version of 1240_68: the Virtual Block 4 release sheet text (Dec 2009, slip 70); the PC website card list
 * (by 2012-07-21) has the current wording ("peek at top card of" became "peek at the top card of"). Plays the same as the current card; only the
 * wording differs. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2240_068 extends Card1240_068 {
    public Card2240_068() {
        super();
        setGameText("May be battled. If with Dash Rendar, adds one destiny to attrition only. Once per turn, if at a battleground, you may peek at top card of opponent's Reserve Deck; may place that card on top of opponent's Used Pile.");
        setLegacyVersion(new LegacyVersion("1240_68", "",
                "Virtual Block 4 release sheet (2009-12-07), card #70; superseded by the PC card list text (by 2012-07-21)", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
