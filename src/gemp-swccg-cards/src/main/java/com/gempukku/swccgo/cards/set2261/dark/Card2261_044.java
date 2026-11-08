package com.gempukku.swccgo.cards.set2261.dark;

import com.gempukku.swccgo.cards.set1261.dark.Card1261_044;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Type: Interrupt
 * Title: Lana Dobreed & Sacrifice
 * Original version of 1261_44: the Virtual Block 6.1 release sheet text, "place this Interrupt in Lost Pile" (the PC card list, PC-E1, drops "this").
 * Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_044 extends Card1261_044 {
    public Card2261_044() {
        setGameText("If your character's forfeit was just reduced to 0, use 1 Force (free if forfeit reduced by a [Permanent Weapon] weapon) to restore it to normal. (Immune to Sense.) OR Lose 2 Force to cancel an attempt by Yoda or Obi-Wan to cancel your Interrupt or your just drawn weapon destiny. OR During your move phase, target any or all of your characters at one site to 'transport' (relocate) to an exterior or battleground site. Draw destiny. Use that much Force to 'transport,' or place this Interrupt in Lost Pile.");
        setLegacyVersion(new LegacyVersion("1261_44", "",
                "Virtual Block 6.1 release sheet (2009-09-04), card #43", LocalDate.of(2009, 9, 4), LocalDate.of(2012, 7, 21)));
    }
}
