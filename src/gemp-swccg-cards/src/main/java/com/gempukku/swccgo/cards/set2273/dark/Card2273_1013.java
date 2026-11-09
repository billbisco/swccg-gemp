package com.gempukku.swccgo.cards.set2273.dark;

import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 7
 * Type: Epic Event
 * Title: Insidious Prisoner
 *
 * PC-E1 version of 1273_13: the PC website card list text (VBlock7Type, by 2012-07-21, same to 2013-01-11):
 * "Evil is Everywhere" and the published typo "Once per turn, it a player controls this site". Superseded by the
 * Virtual Cards Master List (Apr 2013, PC-E2, the current card: "before drawing a starting hand opponent may deploy ...").
 * Plays as the original (2273_13). Anything Goes only (docs/legacy-versions.md).
 */
public class Card2273_1013 extends Card2273_013 {
    public Card2273_1013() {
        setGameText("If A Stunning Move on table, deploy on Palpatine's Quarters (if [Virtual Block 5] We Have A Plan on table, this card always relocates to Palpatine if possible (even if a captive)); opponent may deploy a Jedi and/or a lightsaber here. Evil is Everywhere: Opponent may not Force drain here. Adds one [Dark Side] at same site. If just lost, relocate to Blockade Flagship: Prison. Once per turn, it a player controls this site, they may have this card (unless on Palpatine) follow their first character to move from here using landspeed or docking bay transit to a non-shielded site.");
        setLegacyVersion(new LegacyVersion("1273_13", "PC-E1",
                "PC website card list (by 2012-07-21; last seen 2013-01-11); superseded by the Virtual Cards Master List (2013-04-27)",
                LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }
}
