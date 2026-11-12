package com.gempukku.swccgo.cards.set6.dark;

import com.gempukku.swccgo.common.TextState;
import java.time.LocalDate;

/**
 * Set: Jabba's Palace
 * Title: Barada
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Barada]] (Decipher archive scan); superseded by the Players Committee errata (Advanced Rulebook Appendix A); supersededOn is an upper bound, the exact PC errata date is not on the wiki.
 * The printed wording differs from the current text only in wording, not in how GEMP plays it, so this state reuses the current card's game text code and differs only in its text and text-state information.
 */
public class Card6_096o extends Card6_096 {
    public Card6_096o() {
        super();
        setGameText("Deploys only to a Jabba's Palace site. Power = 0 at any location other than a Tatooine site. Power +2 at any Jabba's Palace site, while Jabba on table. Your transport vehicles lost from same site may go to your Used Pile rather than your Lost Pile.");
        setTextState(new TextState("6_96", "Printed",
                "Jabba's Palace printing (Decipher, May 1998)",
                LocalDate.of(1998, 5, 1), LocalDate.of(2023, 1, 1)));
    }
}
