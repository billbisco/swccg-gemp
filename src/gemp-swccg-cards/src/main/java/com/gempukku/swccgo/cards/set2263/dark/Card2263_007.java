package com.gempukku.swccgo.cards.set2263.dark;

import com.gempukku.swccgo.cards.set601.dark.Card601_186;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.3
 * Type: Character
 * Subtype: Imperial
 * Title: Maarek Stele, The Emperor's Reach
 * Original version of 1263_7: the Virtual Block 6.3 release sheet printed this card as "The Emperor's Reach". The
 * Players Committee renamed it Maarek Stele, The Emperor's Reach; the game text did not change. The slip has a comma
 * after "Once per game" that the Master List drops (punctuation only, not a separate version). Bill (2026-11-09): a
 * title change is a version. Same card and persona as the current printing (Display Title is display only). Anything
 * Goes only (docs/legacy-versions.md).
 */
public class Card2263_007 extends Card601_186 {
    public Card2263_007() {
        super();
        setGameText("[Pilot] 3. Once per game, may [upload] a TIE Defender. While piloting, opponent may not cancel or substitute battle destiny draws here. While piloting an Imperial starfighter, it is maneuver +2 and he draws one battle destiny if unable to otherwise.");
        setLegacyVersion(new LegacyVersion("1263_7", "",
                "Virtual Block 6.3 release sheet (released 2010-03-12; copy re-made 2010-04-04), card #3-08, printed as The Emperor's Reach; renamed Maarek Stele, The Emperor's Reach (PC \"New Persona Changes\" 2013-01-29; PC card list by 2013-03-13; Virtual Cards Master List 2013-04-27)",
                LocalDate.of(2010, 3, 12), LocalDate.of(2013, 4, 27)));
        setDisplayTitle("The Emperor's Reach");
    }
}
