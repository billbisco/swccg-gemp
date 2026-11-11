package com.gempukku.swccgo.cards.set2271.dark;

import com.gempukku.swccgo.cards.set1271.dark.Card1271_019;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 7.1
 * Title: Rise Of The Dark Lord
 *
 * PC-E2 version of 1271_19: the PC card image (riseofthedarklord.gif), dated by its server upload (2013-02-25, a bulk upload; the image may be older). It prints the release-sheet wording again, word for word.
 * The Master List of 2013-04-27 (the current card) follows it (Bill 2026-11-11: every publication is a version; a PC card image is its own version at its upload
 * date; batch R8). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2271_2019 extends Card1271_019 {
    public Card2271_2019() {
        super();
        setGameText("Deploy on table (only at start of game) with three different [Vader] Effects from Reserve Deck face down under here. Once per turn, may deploy a [Vader] Effect from here (never from elsewhere). Limit one [Vader] Effect on table and, when lost, stack it under here. You may cancel your [Vader] Effect at any time. Twisted And Evil: Dark Jedi Presence, Jedi Presence, Revenge Of The Sith and Sith Tests are canceled. When using The Empire's Back to deploy Vader, it is a Used Interrupt. If you just drew a [Vader] card for destiny, may take it into hand.");
        setLegacyVersion(new LegacyVersion("1271_19", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
