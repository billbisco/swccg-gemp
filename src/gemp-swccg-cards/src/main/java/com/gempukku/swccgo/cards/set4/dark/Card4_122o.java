package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;

import java.time.LocalDate;

/**
 * Set: Dagobah
 * Type: Effect
 * Title: Flagship
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Printed Dagobah (April 1997) text, from wiki.swccg.com [[Flagship]] (Decipher archive scan). As printed your other
 * starships may move as a 'react' only to the same system (sectors came later). Decipher's Glossary Version 2.0
 * (November 1998, [[Flagship (Errata)]], Card4_122d) added "or sector". "If starship lost" plays as the current card's
 * "about to be lost": Flagship leaves table with the starship, so GEMP takes the Force loss as it is lost.
 */
public class Card4_122o extends Card4_122 {
    public Card4_122o() {
        super();
        setGameText("Use 2 Force to deploy on one of your Star Destroyers. Your other starships may move as a 'react' to same system (for free). If starship lost, you lose X Force, where X = starship's armor. (Immune to your Alter.)");
        setTextState(new TextState("4_122", "Printed", "Dagobah printing (Decipher, April 1997)",
                LocalDate.of(1997, 4, 1), LocalDate.of(1998, 11, 1)));
    }

    @Override
    protected Filter getReactLocation(PhysicalCard self) {
        return Filters.and(Filters.system, super.getReactLocation(self));
    }
}
