package com.gempukku.swccgo.cards.set2274.light;

import com.gempukku.swccgo.cards.set1274.light.Card1274_019;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 7.4
 * Type: Effect
 * Title: Rebel Sacrifice
 * Original version of 1274_19: the text of the Virtual Block 7.4 release sheet (2011-12-16), card #05; the PC card
 * image prints it. The PC website card list (by 2012-07-21, PC-E1, 2274_1019) changes it: two changes: the full stop
 * after “Death Star” moves outside the closing quote mark (“Death Star.” becomes “Death Star”.), and “a Force drain”
 * is written “a Force Drain”, with a capital D. The list's missing space after the written-out [Light Side] icon
 * (“[Light Side],and”) is icon notation, not a change. The Master List of 2013-04-27 prints the original words again;
 * it is listed as its own version (PC-E2, the current card; Bill 2026-11-11: every publication is a version). Same
 * play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2274_019 extends Card1274_019 {
    public Card2274_019() {
        super();
        setGameText("Deploy on table if Obi-Wan 'communing.' 'Tatooine' on Obi-Wan is treated as 'Death Star.' Your Death Star sites gain one [Light Side], and if opponent loses Force to battle damage (or a Force drain) at such sites, stack the first card lost face-down here. Attack Run is canceled. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1274_19", "",
                "Virtual Block 7.4 release sheet (2011-12-16), card #05; superseded by the PC card list text (by 2012-07-21)", LocalDate.of(2011, 12, 16), LocalDate.of(2012, 7, 21)));
    }
}
