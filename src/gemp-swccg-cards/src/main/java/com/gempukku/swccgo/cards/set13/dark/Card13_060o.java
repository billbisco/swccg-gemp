package com.gempukku.swccgo.cards.set13.dark;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Reflections III
 * Title: Colo Claw Fish (Dark)
 * Text state: o (as printed, 31 October 2001; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Colo Claw Fish (Dark)]] (Decipher archive scan); the current text is [[Colo Claw Fish (Dark) (PC Errata)]]
 * (Players Committee errata, Advanced Rulebook 2023 Appendix A). As printed it has no "(limit once per destiny draw)"; this state drops that limit.
 * supersededOn is an upper bound (Advanced Rulebook 2023 Appendix A, PDF dated 30 July 2023).
 */
public class Card13_060o extends Card13_060 {
    public Card13_060o() {
        super();
        setGameText("Deploy on table. Cancels Opee Sea Killer. While no card here, you may place a card from hand face-up here. If you just drew weapon or battle destiny, you may exchange it for card here, which then counts as that destiny draw. (Immune to Alter.)");
        setTextState(new TextState("13_60", "Printed",
                "Reflections III printing (Decipher, 31 October 2001)",
                LocalDate.of(2001, 10, 31), LocalDate.of(2023, 7, 30)));
    }

    /** As printed: no "(limit once per destiny draw)". */
    @Override
    protected boolean limitedToOncePerDestinyDraw() {
        return false;
    }
}
