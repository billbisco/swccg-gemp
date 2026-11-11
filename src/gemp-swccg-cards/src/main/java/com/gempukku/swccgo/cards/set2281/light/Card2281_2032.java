package com.gempukku.swccgo.cards.set2281.light;

import com.gempukku.swccgo.cards.set1281.light.Card1281_032;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.1
 * Type: Effect
 * Title: Imperial Navigation Charts
 * PC-E2 version of 1281_32: the PC website card list (2012-07-21 to 2013-01-11), which printed the optional "may 'steal'"
 * wording again after the 2012-07-13 errata (every published text is a version, Bill 2026-11-11). Superseded by the
 * Virtual Cards Master List (2013-04-27, PC-E3, the current card).
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2281_2032 extends Card1281_032 {
    public Card2281_2032() {
        super();
        setGameText("Deploy on a Nar Shaddaa battleground site. If you control three Nar Shaddaa locations or just won a battle (or initiated a Force drain) here, may 'steal' (relocate to your side of table). Place in Used Pile if opponent controls three Nar Shaddaa locations. (Immune to Alter).");
        setLegacyVersion(new LegacyVersion("1281_32", "PC-E2",
                "PC website card list (2012-07-21 to 2013-01-11)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }

    @Override
    protected boolean stealIsOptional() {
        return true;
    }
}
