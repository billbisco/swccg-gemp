package com.gempukku.swccgo.cards.set2220.dark;

import com.gempukku.swccgo.cards.set1220.dark.Card1220_043;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 2
 * Type: Effect
 * Title: Precision Targeting
 * Original version of 1220_43: the Virtual Block 2 release sheet text (Dec 2009, slip 43); the PC website card list
 * (by 2012-07-21) has the current wording ("on your capital starship." became "on you capital starship."). Plays the same as the current card; only the
 * wording differs. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2220_043 extends Card1220_043 {
    public Card2220_043() {
        super();
        setGameText("Deploy on table. Your capital starship weapon destiny draws are +1. During your deploy phase, may use 1 Force to [download] a starship weapon (for free) on your capital starship. If a starship was just hit, may place this Effect in Lost Pile to make that starship forfeit = 0.");
        setLegacyVersion(new LegacyVersion("1220_43", "",
                "Virtual Block 2 release sheet (2009-12-07), card #43; superseded by the PC card list text (by 2012-07-21)", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
