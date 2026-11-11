package com.gempukku.swccgo.cards.set2274.light;

import com.gempukku.swccgo.cards.set1274.light.Card1274_019;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 7.4
 * Type: Effect
 * Title: Rebel Sacrifice
 * PC-E1 version of 1274_19. Order (Bill 2026-11-11: every publication is a version; a PC card image is placed at its
 * server upload date): original (2274_19) = the Virtual Block 7.4 release sheet (2011-12-16), card #05; PC-E1
 * (2274_1019) = the PC website card list (by 2012-07-21): two changes: the full stop after “Death Star” moves outside
 * the closing quote mark (“Death Star.” becomes “Death Star”.), and “a Force drain” is written “a Force Drain”, with
 * a capital D. The list's missing space after the written-out [Light Side] icon (“[Light Side],and”) is icon
 * notation, not a change. PC-E2 (2274_2019) = the PC card image (uploaded 2013-02-25; it may have been made earlier),
 * the original words again; PC-E3 = the Master List (2013-04-27), the original words again, the current card. Same
 * play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2274_1019 extends Card1274_019 {
    public Card2274_1019() {
        super();
        setGameText("Deploy on table if Obi-Wan 'communing.' 'Tatooine' on Obi-Wan is treated as 'Death Star'. Your Death Star sites gain one [Light Side], and if opponent loses Force to battle damage (or a Force Drain) at such sites, stack the first card lost face-down here. Attack Run is canceled. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1274_19", "PC-E1",
                "PC website card list (by 2012-07-21; Virtual master list to 2013-01-11); superseded by the PC card image (uploaded 2013-02-25)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}
