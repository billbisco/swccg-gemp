package com.gempukku.swccgo.cards.set2250.light;

import com.gempukku.swccgo.cards.set1250.light.Card1250_033;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Type: Admiral's Order
 * Title: Let's Go Left (V)
 * PC-E2 version of 1250_33 (Batch S1, H1: the PC card image wording, 2250_1033, is the 9 September 2009 errata PC-E1;
 * the card list text, the current card, is PC-E3): the Virtual Block 5 release sheet text. "non-[Presence] characters (and vehicles)"
 * names no owner, so at a system a player controls with a capital starship both players' such cards deploy -1 to
 * related sites and sectors (the PC card image, PC-E1, and the PC card list, PC-E3, say "their").
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2250_033 extends Card1250_033 {
    public Card2250_033() {
        setGameText("At each system a player controls with a capital starship, non-[Presence] characters (and vehicles) deploy -1 to related sites and sectors. If you occupy a system with a capital starship (or three unique starships) and have no Objective on table, may add 1 to each of your Force drains at related battleground sites (opponent loses no more than 2 Force from those drains).");
        setLegacyVersion(new LegacyVersion("1250_33", "PC-E2",
                "Virtual Block 5 release sheet (2009-12-07), card #33E", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }

    @Override
    protected boolean discountOnlyControllersCards() {
        return false;
    }
}
