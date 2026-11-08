package com.gempukku.swccgo.cards.set2240.dark;

import com.gempukku.swccgo.cards.set1240.dark.Card1240_040;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 4
 * Type: Epic Event
 * Title: If The Trace Was Correct (V)
 * Original version of 1240_40: the Virtual Block 4 release sheet text (Dec 2009), naming Galen's Fighter (the starship
 * renamed Rogue Shadow on the Virtual Cards Master List, Apr 2013). Plays the same as the current card; only the wording differs. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2240_040 extends Card1240_040 {
    public Card2240_040() {
        super();
        setGameText("Deploy on table. Instead of firing Galen's Lightsaber, may 'throw' it to Used Pile: draw destiny; choose up to half that number (round up) of opponent's ability < 3 characters present to be hit. Galen's Fighter may deploy to sites, moves for free (unless relocating), is immune to attrition < 4, and you may use 3 Force to relocate it to a battleground site (as a regular move during your move phase). If it just moved to a site opponent occupies, may take top card of Used Pile into hand (or if it just landed, any one card in Used Pile; reshuffle).");
        setLegacyVersion(new LegacyVersion("1240_40", "",
                "Virtual Block 4 release sheet (2009-12-07), card #37", LocalDate.of(2009, 12, 7), LocalDate.of(2013, 4, 27)));
    }
}
