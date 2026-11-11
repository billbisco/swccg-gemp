package com.gempukku.swccgo.cards.set2240.light;

import com.gempukku.swccgo.cards.set1240.light.Card1240_091;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 4
 * Type: Effect
 * Title: Uncharted Settlements
 *
 * PC-E2 version of 1240_91: the PC card image (unchartedsettlements.gif), dated by its server upload (2013-02-25,
 * a bulk upload; the image may be older). It prints the release-sheet wording. The Master List of 2013-04-27 (the
 * current card) follows it (Bill 2026-11-11: a PC card image is its own version, placed at its upload date; batch
 * R8). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2240_2091 extends Card1240_091 {
    public Card2240_2091() {
        super();
        setGameText("Deploy on [Virtual Block 4] Hidden Base. Organized Attack, S-Foils, Revolution, Your Destiny (unless you chose Rebel/Rebel Starship), and [Virtual Block 4] Ellorrs Madak are canceled. You may not deploy operatives or [Maintenance] cards. Force generation at Endor system may not be canceled. Unless Leave Them To Me on table, opponent's combat vehicles, Dark Jedi, [Maintenance] cards, and [Permanent Weapon] cards are deploy +2 to Rebel Cell sites. Opponent generates no Force at Hidden Landing Site. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1240_91", "PC-E2",
                "PC card image (server upload 2013-02-25); superseded by the Master List text (2013-04-27)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
