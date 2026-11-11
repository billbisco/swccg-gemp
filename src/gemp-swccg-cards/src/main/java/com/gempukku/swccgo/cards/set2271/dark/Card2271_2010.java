package com.gempukku.swccgo.cards.set2271.dark;

import com.gempukku.swccgo.cards.set1271.dark.Card1271_010;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 7.1
 * Title: I Want Every Part Of This Ship Checked
 *
 * PC-E2 version of 1271_10: the PC card image (iwanteverypartofthisshipchecked.gif), dated by its server upload (2013-02-25, a bulk upload; the image may be older). It prints the release-sheet wording again, word for word.
 * The Master List of 2013-04-27 (the current card) follows it (Bill 2026-11-11: every publication is a version; a PC card image is its own version at its upload
 * date; batch R8). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2271_2010 extends Card1271_010 {
    public Card2271_2010() {
        super();
        setGameText("Deploy on table. Once per game, if Obi-Wan on table, may [download] Vader. May place this Effect in Used Pile to [upload] a [Grabber]. If you just attempted to [download] or [upload] a card, and the card was not found, may place this Effect in Lost Pile to search your Force Pile for that card; reshuffle.");
        setLegacyVersion(new LegacyVersion("1271_10", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
