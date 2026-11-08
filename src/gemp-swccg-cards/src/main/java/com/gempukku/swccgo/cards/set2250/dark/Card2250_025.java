package com.gempukku.swccgo.cards.set2250.dark;

import com.gempukku.swccgo.cards.set1250.dark.Card1250_025;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Type: Effect
 * Title: Wipe Them Out, All Of Them (V)
 * Original version of 1250_25: the Virtual Block 5 release sheet text, "[Permanent Weapon] weapon destiny draws"
 * (the PC card list, PC-E1, drops "weapon"). Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2250_025 extends Card1250_025 {
    public Card2250_025() {
        setGameText("Deploy on a site. Unless a player controls a battleground site, that player's [Permanent Weapon] weapon destiny draws are -2. During opponent's draw phase, if opponent's non-battleground site (with no related locations) on table, may activate or retrieve 1 Force. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1250_25", "",
                "Virtual Block 5 release sheet (2009-12-07), card #24", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
