package com.gempukku.swccgo.cards.set11.light;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Tatooine
 * Type: Effect
 * Title: Great Shot, Kid!
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Printed Tatooine (Decipher, May 2001) text from wiki.swccg.com [[Great Shot, Kid!]] and its Decipher archive scan
 * (Tat-L-greatshotkid-decipher-archive.jpg). The Players Committee rewrote it in the Star Wars CCG Rules Supplement
 * (updated 9 February 2004; [[Great Shot, Kid! (PC Errata)]]) to "Whenever you deploy a unique (•) starship to a system
 * location, retrieve 3 Force". Bill 2026-11-12 (ai-team-skills RULINGS.md "printed-text readings" 3): wording only;
 * retrieve 3 however the unique starship was deployed, so the printed text plays as the bare card.
 */
public class Card11_018o extends Card11_018 {
    public Card11_018o() {
        super();
        setGameText("Deploy on table. If Death Star \"blown away\": once during each of your turns you may deploy (for free) a starship from hand or Reserve Deck and reshuffle; if you just deployed a unique (•) starship to a system location, retrieve 3 Force. (Immune to Alter.)");
        setTextState(new TextState("11_18", "Printed",
                "Tatooine printing (Decipher, May 2001)",
                LocalDate.of(2001, 5, 1), LocalDate.of(2004, 2, 9)));
    }
}
