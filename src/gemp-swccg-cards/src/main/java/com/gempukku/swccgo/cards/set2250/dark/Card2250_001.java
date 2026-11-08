package com.gempukku.swccgo.cards.set2250.dark;

import com.gempukku.swccgo.cards.set1250.dark.Card1250_001;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Type: Admiral's Order
 * Title: Nothing Can Get Through Our Shield (V)
 * Original version of 1250_1: the Virtual Block 5 release sheet text. "non-[Presence] characters (and vehicles)"
 * names no owner, so at a system a player controls with a capital starship both players' such cards deploy -1 to
 * related sites and sectors (the PC card list, PC-E1, made it "his").
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2250_001 extends Card1250_001 {
    public Card2250_001() {
        setGameText("At each system a player controls with a capital starship, non-[Presence] characters (and vehicles) deploy -1 to related sites and sectors. If you occupy a system with a capital starship (or three unique starships) and have no Objective on table, may add 1 to each of your Force drains at related battleground sites (opponent loses no more than 2 Force from those drains).");
        setLegacyVersion(new LegacyVersion("1250_1", "",
                "Virtual Block 5 release sheet (2009-12-07), card #01E", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }

    @Override
    protected boolean discountOnlyControllersCards() {
        return false;
    }
}
