package com.gempukku.swccgo.cards.set6.light;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Jabba's Palace
 * Type: Weapon
 * Title: Vibro-Ax
 * Text state: o (as printed, May 1998; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Vibro-Ax]] and its Decipher archive scan (JabbasPalace-L-vibroax-decipher-archive.gif); superseded by
 * [[Vibro-Ax (PC Errata)]], AR 2023 Appendix A.
 * The printed text has no "a character" after "May target"; the excluding comparison is by power, so it plays as the bare card.
 */
public class Card6_090o extends Card6_090 {
    public Card6_090o() {
        super();
        setGameText("Use 3 Force to deploy on any alien warrior. Adds 1 to power. May target using 1 Force. Both players draw destiny. Target immediately excluded from battle if warrior's power + your destiny > target's power + opponent's destiny.");
        setTextState(new TextState("6_90", "Printed",
                "Jabba's Palace printing (Decipher, May 1998)",
                LocalDate.of(1998, 5, 1), LocalDate.of(2023, 1, 1)));
    }
}
