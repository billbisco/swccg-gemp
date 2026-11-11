package com.gempukku.swccgo.cards.set2220.dark;

import com.gempukku.swccgo.cards.set1220.dark.Card1220_083;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 2
 * Type: Weapon
 * Subtype: Vehicle
 * Title: AT-AT Cannon
 *
 * PC-E2 version of 1220_83: the PC card image (atatcannon.gif), dated by its server upload (2013-02-25, a bulk
 * upload; the image may be older). It prints the release-sheet wording. The Master List of 2013-04-27 (the current
 * card) follows it (Bill 2026-11-11: a PC card image is its own version, placed at its upload date; batch R8).
 * Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2220_2083 extends Card1220_083 {
    public Card2220_2083() {
        super();
        setGameText("Deploy on your non-[Maintenance] AT-AT. May target a character or vehicle at same or adjacent site for free. Draw destiny. Target hit if destiny +1 (+2 if a vehicle) > defense value. If just lost from table, place this weapon in Used Pile.");
        setLegacyVersion(new LegacyVersion("1220_83", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
