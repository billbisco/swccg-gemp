package com.gempukku.swccgo.cards.set2240.light;

import com.gempukku.swccgo.cards.set1240.light.Card1240_079;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 4
 * Type: Effect
 * Title: Corellian Engineering Corporation (V)
 * Original version of 1240_79: the Virtual Block 4 release sheet text ("X = number of spaceport sites controlled by
 * Corellians"), also on the PC card image and the Master List (2013-04-27). Replaced on 2014-03-04 (Competitive Balance
 * Team errata, PC forum "Current Rulings/Errata": "your spaceport sites controlled by your Corellians"). GEMP already
 * counted only your sites controlled with your Corellians, so the play is the same. Anything Goes only
 * (docs/legacy-versions.md).
 */
public class Card2240_079 extends Card1240_079 {
    public Card2240_079() {
        super();
        setGameText("Deploy on table if [Virtual Block 4] Watch Your Step on table. X = number of spaceport sites controlled by Corellians. While you control Corellia, your total battle destiny is +X, and once per turn, if you are about to lose Force (except from your card), may reduce loss by X. [Immune to Alter]");
        setLegacyVersion(new LegacyVersion("1240_79", "",
                "Virtual Block 4 release sheet (2009-12-07), card #78", LocalDate.of(2009, 12, 7), LocalDate.of(2014, 3, 4)));
    }
}
