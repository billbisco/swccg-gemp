package com.gempukku.swccgo.cards.set2281.light;

import com.gempukku.swccgo.cards.set1281.light.Card1281_035;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.1
 * Type: Effect
 * Title: Scoundrel's Charm
 * Original version of 1281_35: the Virtual Block 8.1 release sheet text (2012-05-11, slip 00; the PC card image and the Master List of
 * 2013-04-27 print it). The PC website card list (by 2012-07-21) reads "top 3 cards of your Force pile" and "from an opponent's". Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2281_035 extends Card1281_035 {
    public Card2281_035() {
        super();
        setGameText("Deploy from under Scoundrel's Luck on a scoundrel. Once per turn, may peek at the top three cards of your Force Pile and take one into hand; reshuffle. Once per turn, may use 1 Force to add or subtract 1 from opponent's just-drawn destiny.");
        setLegacyVersion(new LegacyVersion("1281_35", "",
                "Virtual Block 8.1 release sheet (2012-05-11), card #00; superseded by the PC card list text (by 2012-07-21)", LocalDate.of(2012, 5, 11), LocalDate.of(2012, 7, 21)));
    }
}
