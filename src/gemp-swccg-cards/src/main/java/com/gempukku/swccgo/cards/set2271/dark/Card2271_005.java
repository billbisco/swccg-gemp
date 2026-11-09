package com.gempukku.swccgo.cards.set2271.dark;

import com.gempukku.swccgo.cards.set601.dark.Card601_189;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 7.1
 * Type: Character
 * Subtype: Imperial
 * Title: Ysanne Isard
 * Original version of 1271_5: the Virtual Block 7.1 release sheet printed this card as "•Ice-Heart" (no "(V)"). The
 * Players Committee renamed it Ysanne Isard (card image; PC card list by 2013-03-14, still Ice-Heart on 2013-01-11;
 * Virtual Cards Master List, official text 2013-04-27; probably
 * announced with the PC's "New Persona Changes" of 2013-01-29). The game text never changed. Bill (2026-11-08):
 * "Although they have different names; they are the same persona" - the class keeps the current title and the
 * Ysanne Isard persona, so it is the same card as every Ysanne Isard. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2271_005 extends Card601_189 {
    public Card2271_005() {
        super();
        setLegacyVersion(new LegacyVersion("1271_5", "",
                "Virtual Block 7.1 release sheet (2010-06-18), printed as Ice-Heart; renamed Ysanne Isard (PC card list by 2013-03-14, Virtual Cards Master List 2013-04-27)",
                LocalDate.of(2010, 6, 18), LocalDate.of(2013, 3, 14)));
        setPrintedTitle("Ice-Heart");
    }
}
