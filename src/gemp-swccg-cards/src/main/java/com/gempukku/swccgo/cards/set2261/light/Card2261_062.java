package com.gempukku.swccgo.cards.set2261.light;

import com.gempukku.swccgo.cards.set1261.light.Card1261_062;
import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Type: 
 * Title: Baragwin (V)
 * Original version of 1261_62: the Virtual Block 6.1 release sheet (2009-09-04) text.
 * Superseded by the PC website card list text (PC-E1, by 2012-07-21: "This character is lost" for "Lost", "Your non-alien characters at sites" for "Your non-alien characters" and "whenever" for "when"), the current card.
 * Plays differently from the current card (see below). Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_062 extends Card1261_062 {
    public Card2261_062() {
        super();
        setGameText("Lost if a Starting Effect on table. Your non-alien characters are power and forfeit = 0. If present at a battleground site, your Force drains at battleground sites where you have two aliens are +1, and when you deploy an alien, retrieve 1 Force.");
        setLegacyVersion(new LegacyVersion("1261_62", "",
                "Virtual Block 6.1 release sheet (2009-09-04)", LocalDate.of(2009, 9, 4), LocalDate.of(2012, 7, 21)));
    }

    /** The release-sheet text resets all your non-alien characters, not only those at sites. */
    @Override
    protected Filter nonAliensReset(PhysicalCard self) {
        return Filters.and(Filters.your(self), Filters.character, Filters.not(Filters.alien));
    }
}
