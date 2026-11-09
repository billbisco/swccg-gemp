package com.gempukku.swccgo.cards.set2210.light;

import com.gempukku.swccgo.cards.set1210.light.Card1210_098;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 1
 * Type: Character
 * Title: Commander Vanden Willard
 * Original version of 1210_98: the Virtual Block 1 release sheet (2009-12-07) text.
 * Superseded by the PC website card list text (PC-E1, by 2012-07-21: "take a non-[Permanent Weapon] Leia" (an added "a") for "▲ non-[Permanent Weapon] Leia"), the current card.
 * Plays the same as the current card; only the wording differs. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2210_098 extends Card1210_098 {
    public Card2210_098() {
        super();
        setGameText("Leia is defense value and forfeit +3. Your senators at battleground sites and characters at Yavin 4 locations are forfeit +2. Once per game, may take non-[Permanent Weapon] Leia into hand from Reserve Deck; reshuffle.");
        setLegacyVersion(new LegacyVersion("1210_98", "",
                "Virtual Block 1 release sheet (2009-12-07)", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
