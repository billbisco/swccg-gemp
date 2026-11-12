package com.gempukku.swccgo.cards.set10.light;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Reflections II
 * Type: Device
 * Title: Obi-Wan's Journal
 * Text state: o (as printed, 10 January 2001; docs/legacy-versions.md "Text states")
 * <p>
 * The PC text reads "present here", "may not be Disarmed" and "Lost if about to be stolen"; GEMP already plays the
 * printed meaning the same way (the Journal never ends up with the stealer). The Players Committee's errata (earliest source found: the PC Decipher Errata sheet, "Official text as of June 1, 2017";
 * 2023 Advanced Rulebook Appendix A) gives the current text (bare id); the date the PC first issued it is not established.
 * The wording change does not change how the card plays, so this state reuses the current card's game text code
 * and differs only in its text and text-state information.
 * Text from wiki.swccg.com [[Obi-Wan's Journal]].
 */
public class Card10_015o extends Card10_015 {
    public Card10_015o() {
        super();
        setGameText("Deploy on Luke or Obi-Wan. Your characters present armed with a unique (•) lightsaber Weapon card are immune to Disarmed, once per battle may cancel a weapon destiny just drawn, and that lightsaber's Force drain modifiers may not be canceled. Lost if stolen.");
        setTextState(new TextState("10_15", "Printed",
                "Reflections II printing (Decipher, 10 January 2001)",
                LocalDate.of(2001, 1, 10), LocalDate.of(2017, 6, 1)));
    }
}
