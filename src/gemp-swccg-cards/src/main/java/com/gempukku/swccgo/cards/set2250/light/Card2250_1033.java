package com.gempukku.swccgo.cards.set2250.light;

import com.gempukku.swccgo.cards.set1250.light.Card1250_033;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Type: Admiral's Order
 * Title: Let's Go Left (V)
 * PC-E1 version of 1250_33 (Batch S1, Bill 2026-11-11, H1): the PC card image wording (slip 01E; the same image is in the
 * swccgvkit-legacy svn r1 of 2009-12-09), dated as the 9 September 2009 errata the PC card list stamps on the card:
 * "their non-[Presence] characters (and vehicles)". "their" limits the discount to the controlling player's cards, as on
 * the current card. The September 2009 first printing is lost. Followed by the release sheet text (PC-E2, 2250_33).
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2250_1033 extends Card1250_033 {
    public Card2250_1033() {
        setGameText("At each system a player controls with a capital starship, their non-[Presence] characters (and vehicles) deploy -1 to related sites and sectors. If you occupy a system with a capital starship (or three unique starships) and have no Objective on table, may add 1 to each of your Force drains at related battleground sites (opponent loses no more than 2 Force from those drains).");
        setLegacyVersion(new LegacyVersion("1250_33", "PC-E1",
                "PC card image (slip 01E; in swccgvkit-legacy svn r1, 2009-12-09), the 2009-09-09 errata", LocalDate.of(2009, 9, 9), LocalDate.of(2009, 12, 7)));
    }
}
