package com.gempukku.swccgo.cards.set2281.light;

import com.gempukku.swccgo.cards.set1281.light.Card1281_032;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.1
 * Type: Effect
 * Title: Imperial Navigation Charts
 * Original version of 1281_32: the first Virtual Block 8.1 printing (official text as of 2012-05-11), whose steal was
 * optional ("may 'steal'"). No copy of the first sheet survives (the surviving sheet was re-made on 2012-07-18 with
 * the errata); the wording is inferred from the PC website card list (2012-07-21 to 2013-01-11; PC-E2 = 2281_2032).
 * Superseded by the errata posted on the PC forum on 2012-07-13 (p739757: "the stealing of the Charts is no longer
 * optional"; PC-E1 = 2281_1032); the Master List (2013-04-27) prints those words again (PC-E3, the current card). Until legacy batch B41 this class held the mandatory text and the current card the optional one.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2281_032 extends Card1281_032 {
    public Card2281_032() {
        super();
        setGameText("Deploy on a Nar Shaddaa battleground site. If you control three Nar Shaddaa locations or just won a battle (or initiated a Force drain) here, may 'steal' (relocate to your side of table). Place in Used Pile if opponent controls three Nar Shaddaa locations. (Immune to Alter).");
        setLegacyVersion(new LegacyVersion("1281_32", "",
                "Virtual Block 8.1 (2012-05-11), text inferred from the PC website card list", LocalDate.of(2012, 5, 11), LocalDate.of(2012, 7, 13)));
    }

    @Override
    protected boolean stealIsOptional() {
        return true;
    }
}
