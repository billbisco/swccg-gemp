package com.gempukku.swccgo.cards.set2272.light;

import com.gempukku.swccgo.cards.set1272.light.Card1272_026;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 7.2
 * Type: Effect
 * Title: The Camp (V)
 * Original version of 1272_26: the Virtual Block 7.2 release sheet text (Dec 2010), "no unique (•) character of ability > 3".
 * Superseded by the PC website card list text (PC-E1, by 2012-07-21: "characters"), the current card. Plays the same as
 * the current card; only the wording differs. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2272_026 extends Card1272_026 {
    public Card2272_026() {
        super();
        setGameText("Unless your [Reflections 2] objective on table, deploy on table. If [Permanent Weapon] Maul in battle at a site, and you have no unique (•) character of ability > 3 (except Luke of ability = 4) there, lightsabers there may not be 'swung' twice. Once per game, may take one K'lor'slug or a [Setup] card into hand from Reserve Deck; reshuffle. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1272_26", "",
                "Virtual Block 7.2 release sheet (2010-12-15)", LocalDate.of(2010, 12, 15), LocalDate.of(2012, 7, 21)));
    }
}
