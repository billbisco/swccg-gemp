package com.gempukku.swccgo.cards.set2240.light;

import com.gempukku.swccgo.cards.set1240.light.Card1240_091;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 4
 * Type: Effect
 * Title: Uncharted Settlements
 * Original version of 1240_91: the Virtual Block 4 release sheet (2009-12-07; surviving copy re-made 2010-04-20) text.
 * Superseded by the PC website card list text (PC-E1, by 2012-07-21: "Ellors Madak" for "Ellorrs Madak" (spelling as published) and "S-foils" for "S-Foils"), the current card.
 * Plays the same as the current card; only the wording differs. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2240_091 extends Card1240_091 {
    public Card2240_091() {
        super();
        setGameText("Deploy on [Virtual Block 4] Hidden Base. Organized Attack, S-Foils, Revolution, Your Destiny (unless you chose Rebel/Rebel Starship), and [Virtual Block 4] Ellorrs Madak are canceled. You may not deploy operatives or [Maintenance] cards. Force generation at Endor system may not be canceled. Unless Leave Them To Me on table, opponent's combat vehicles, Dark Jedi, [Maintenance] cards, and [Permanent Weapon] cards are deploy +2 to Rebel Cell sites. Opponent generates no Force at Hidden Landing Site. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1240_91", "",
                "Virtual Block 4 release sheet (2009-12-07; surviving copy re-made 2010-04-20)", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
