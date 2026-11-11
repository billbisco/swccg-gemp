package com.gempukku.swccgo.cards.set2261.light;

import com.gempukku.swccgo.cards.set1261.light.Card1261_105;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Title: Odin Nesloor & •First Aid
 * PC-E1 version of 1261_105: the PC website card-list text, shown there by 2012-07-21 (the card list was generated from the
 * Holotable card data). The Virtual Cards Master List (2013-04-27, PC-E2, the current card) repeats the
 * release-sheet wording; every publication is a version (Bill, 2026-11-11). Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_1105 extends Card1261_105 {
    public Card2261_1105() {
        super();
        setGameText("If your character's forfeit was just reduced to 0, use 1 Force (free if forfeit reduced by a [Permanent Weapon] weapon) to restore it to normal. (Immune to Sense.) OR Cancel an attempt to 'operate' on a character; character is protected from any further attempts until start of opponent's next turn. OR During your move phase, target any or all of your characters at one site to 'transport' (relocate) to an exterior or battleground site. Draw destiny. Use that much Force to 'transport,' or place Interrupt in Lost Pile.");
        setLegacyVersion(new LegacyVersion("1261_105", "PC-E1",
                "PC website card list (by 2012-07-21)",
                LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}
