package com.gempukku.swccgo.cards.set5.dark;

import com.gempukku.swccgo.common.TextState;
import java.time.LocalDate;

/**
 * Set: Cloud City
 * Title: Human Shield
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Human Shield]] (Decipher archive scan); superseded by the Players Committee errata (Advanced Rulebook Appendix A); supersededOn is an upper bound, the exact PC errata date is not on the wiki.
 * The bare card (Card5_145) plays the PC errata's USED clause when an opponent's weapon is about to 'hit' the Imperial;
 * this state plays it after the 'hit', as printed. The LOST clause plays the same in both.
 */
public class Card5_145o extends Card5_145 {
    public Card5_145o() {
        super();
        setGameText("USED: If an opponent's weapon just 'hit' an Imperial present with a captive being escorted, the captive is 'hit' instead. LOST: During a battle at a site, you may forfeit any or all captives present.");
        setTextState(new TextState("5_145", "Printed",
                "Cloud City printing (Decipher, November 1997)",
                LocalDate.of(1997, 11, 1), LocalDate.of(2023, 1, 1)));
    }

    /** As printed, USED: after the Imperial was just 'hit', the captive is 'hit' instead (the Imperial is restored to normal). */
    @Override
    protected boolean playsWhenAboutToBeHit() {
        return false;
    }
}
