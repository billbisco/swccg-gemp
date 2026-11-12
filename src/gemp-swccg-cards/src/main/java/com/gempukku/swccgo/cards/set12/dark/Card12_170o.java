package com.gempukku.swccgo.cards.set12.dark;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Coruscant
 * Type: Location
 * Subtype: Site
 * Title: Naboo: Battle Plains
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Printed Coruscant (Decipher, August 2001) text from wiki.swccg.com [[Naboo: Battle Plains (Dark)]] and its Decipher
 * archive scan (Cor-D-naboobattleplains-decipher-archive.jpg): Dark Side "Unless Gungan Energy Shield on table, your
 * tanks are each power +2 here." The Players Committee errata ([[Naboo: Battle Plains (Dark) (PC Errata)]], SWCCG 2023
 * Advanced Rulebook Appendix A; upper bound 30 July 2023) reads "AATs". Bill 2026-11-12 (ai-team-skills RULINGS.md
 * "printed-text readings" 2): every AAT is a tank and the errata only clarifies, so the printed text plays as the bare
 * card (wording only).
 */
public class Card12_170o extends Card12_170 {
    public Card12_170o() {
        super();
        setLocationDarkSideGameText("Unless Gungan Energy Shield on table, your tanks are each power +2 here.");
        setTextState(new TextState("12_170", "Printed",
                "Coruscant printing (Decipher, August 2001)",
                LocalDate.of(2001, 8, 1), LocalDate.of(2023, 7, 30)));
    }
}
