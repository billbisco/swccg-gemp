package com.gempukku.swccgo.cards.set2240.dark;

import com.gempukku.swccgo.cards.set1240.dark.Card1240_036;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 4
 * Type: Effect
 * Title: There Is No Try & Oppressive Enforcement (V)
 * Original version of 1240_36: the Virtual Block 4 release sheet text (Dec 2009), "While present, each Imperial Council Member adds one [Dark Side Force]" (the Virtual Cards Master List, Apr 2013, added "here").
 * The current text is the Virtual Cards Master List (Apr 2013, PC-E1). Plays the same as the current card; only the wording differs.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2240_036 extends Card1240_036 {
    public Card2240_036() {
        super();
        setGameText("Deploy on Conference Room. Revolution is canceled. You may not deploy aliens (except bounty hunters and Mara Jade) or [Episode I] cards. Emperor and your admirals, generals, and moffs are Imperial Council Members and influence +1. While present, each Imperial Council Member adds one [Dark Side Force]. Once per turn, may use 1 Force to deploy one Laser Gate or a Political Effect from Reserve Deck; reshuffle. Your cards may deploy to table from Political Effects. May place your canceled Political Effect in Used Pile. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1240_36", "",
                "Virtual Block 4 release sheet (2009-12-07), card #34E", LocalDate.of(2009, 12, 7), LocalDate.of(2013, 4, 27)));
    }
}
