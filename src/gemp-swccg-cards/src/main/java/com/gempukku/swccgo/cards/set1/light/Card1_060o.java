package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;

import java.time.LocalDate;

/**
 * Set: Premiere
 * Type: Effect
 * Title: Rebel Planners
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Printed Premiere (December 1995) text, from wiki.swccg.com [[Rebel Planners]] (Decipher archive scan). As printed it
 * adds X only at the related system. Decipher's Glossary Version 2.0 (November 1998, [[Rebel Planners (Errata)]]) added
 * "and related sectors", which is the current card (Card1_060).
 */
public class Card1_060o extends Card1_060 {
    public Card1_060o() {
        super();
        setGameText("Use 1 Force to deploy at Massassi War Room or any docking bay. Adds X to total power of your starships at the related system, where X = the number of your starships present.");
        setTextState(new TextState("1_60", "Printed", "Premiere (December 1995)",
                LocalDate.of(1995, 12, 1), LocalDate.of(1998, 11, 1)));
    }

    @Override
    protected Filter getRelatedLocations(PhysicalCard self) {
        return Filters.relatedSystem(self);
    }

    @Override
    protected Filter getLocationsWhenModified(PhysicalCard self) {
        return Filters.system;
    }
}
