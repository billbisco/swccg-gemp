package com.gempukku.swccgo.cards.set2261.light;

import com.gempukku.swccgo.cards.set1261.light.Card1261_062;
import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Title: Baragwin (V)
 * PC-E1 version of 1261_62: the PC website card-list text (by 2012-07-21; generated from Holotable data): "This
 * character is lost", "Your non-alien characters at sites", "whenever". The PC card image (2013-02-25) and the Master
 * List (2013-04-27, the current card) print the release-sheet text again (all your non-alien characters are reset).
 * Plays differently from the current card. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_1062 extends Card1261_062 {
    public Card2261_1062() {
        super();
        setGameText("This character is lost if a Starting Effect on table. Your non-alien characters at sites are power and forfeit = 0. If present at a battleground site, your Force drains at battleground sites where you have two aliens are +1, and whenever you deploy an alien, retrieve 1 Force.");
        setLegacyVersion(new LegacyVersion("1261_62", "PC-E1", "PC website card list (by 2012-07-21)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }

    /** "Your non-alien characters at sites": only those at sites are reset. */
    @Override
    protected Filter nonAliensReset(PhysicalCard self) {
        return Filters.and(Filters.your(self), Filters.character, Filters.not(Filters.alien), Filters.at(Filters.site));
    }
}
