package com.gempukku.swccgo.cards.set2261.dark;

import com.gempukku.swccgo.cards.set1261.dark.Card1261_034;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Title: Fleet Security Protocols
 * PC-E3 version of 1261_34: the Players Committee card image fleetsecurityprotocols.gif (folder Virtual6-Dark, upload
 * date 2013-02-25), which repeats the revised 2010 sheet words (PC-E1) after the card list (PC-E2) had changed them.
 * The Master List (2013-04-27, PC-E4) repeats the same words and is the current card. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_3034 extends Card1261_034 {
    public Card2261_3034() {
        super();
        setGameText("Deploy on table. During battle involving your starship (or vehicle), it is power +2 for each of your characters at its related sites (they may be forfeited to reduce battle damage and attrition). Once per turn, may ▼ a non-unique starship (or vehicle) site (if such sites leave table, place them and all your cards there in Used Pile). (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1261_34", "PC-E3",
                "Players Committee card image (folder upload date 2013-02-25)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
