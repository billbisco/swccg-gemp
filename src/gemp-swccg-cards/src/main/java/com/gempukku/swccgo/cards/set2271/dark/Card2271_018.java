package com.gempukku.swccgo.cards.set2271.dark;

import com.gempukku.swccgo.cards.set1271.dark.Card1271_018;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 7.1
 * Type: Epic Event
 * Title: Commence Primary Ignition (V)
 * Original version of 1271_18: the text of the Virtual Block 7.1 release sheet (2010-06-18); the PC card image and the
 * Master List of 2013-04-27 print it (the Master List copy is a stale reprint). It has "If destiny + Y - Z > 8". The
 * PC website card list (by 2012-07-21, PC-E1, the current card) adds parentheses: "If (destiny + Y - Z) > 8"
 * (swccg-guild/work#236: spelling, capitals and punctuation are versions; batch C1). Same play. Anything Goes only
 * (docs/legacy-versions.md).
 */
public class Card2271_018 extends Card1271_018 {
    public Card2271_018() {
        super();
        setGameText("If Death Star orbits Alderaan (and [Virtual Block 7] Central Core on table), deploy on Death Star and fire Superlaser at Alderaan; it is 'blown away.' Once during your control phase, if Death Star orbiting a planet (except a Rebel Base) without related sites, may fire Superlaser at planet (regardless of Objective restrictions) as follows: Draw destiny. Add 2 if you control that system. If destiny + Y - Z > 8, target is 'blown away' and opponent loses 4 Force from Life Force. Y = total Death Star sites where opponent has no presence. Z = total sites at Yavin 4.");
        setLegacyVersion(new LegacyVersion("1271_18", "",
                "Virtual Block 7.1 release sheet (2010-06-18); superseded by the PC card list text (by 2012-07-21)", LocalDate.of(2010, 6, 18), LocalDate.of(2012, 7, 21)));
    }
}
