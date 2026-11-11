package com.gempukku.swccgo.cards.set2210.dark;

import com.gempukku.swccgo.cards.set1210.dark.Card1210_045;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 1
 * Type: Effect
 * Title: Maneuver Check
 *
 * PC-E2 version of 1210_45: the PC card image (maneuvercheck.gif), dated by its server upload (2013-02-25, a bulk
 * upload; the image may be older). It prints the release-sheet wording. The Master List of 2013-04-27 (the current
 * card) follows it (Bill 2026-11-11: a PC card image is its own version, placed at its upload date; batch R8).
 * Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2210_2045 extends Card1210_045 {
    public Card2210_2045() {
        super();
        setGameText("Deploy on table. Starships may not move the turn they are deployed (or carried) to Death Star. Once per game, if Alderaan 'blown away,' may target a system. Unless opponent has a capital (or [Episode I]) starship there, Objectives treat target as a Subjugated planet. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1210_45", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
