package com.gempukku.swccgo.cards.set2220.dark;

import com.gempukku.swccgo.cards.set1220.dark.Card1220_023;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 2
 * Type: Effect
 * Title: 3,720 To 1
 *
 * PC-E2 version of 1220_23: the PC card image (3720to1.gif), dated by its server upload (2013-02-25, a bulk
 * upload; the image may be older). It prints the release-sheet wording. The Master List of 2013-04-27 (the current
 * card) follows it (Bill 2026-11-11: a PC card image is its own version, placed at its upload date; batch R8).
 * Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2220_2023 extends Card1220_023 {
    public Card2220_2023() {
        super();
        setGameText("Deploy on table. Droids are destiny +1. Once per turn, may place the bottom two cards in Lost Pile out of play to deploy from Lost Pile: Forced Servitude, a character weapon, a location, or an Effect that deploys on a location (without 'lose' in game text). (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1220_23", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
