package com.gempukku.swccgo.cards.set3.light;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Title: Anakin's Lightsaber
 * Text state: d (Decipher's last word; docs/legacy-versions.md "Text states")
 * <p>
 * Decipher's Glossary Supplement (29 January 2002) text: the current text plus the note that it matches any Skywalker persona meeting the deploy conditions, and deploys on Vader only once he has crossed over to the Light Side. GEMP's current card (Card3_071) already plays that way, so this state differs only in its text and text-state information. The text is as the wiki transcribes it ("Skwalker").
 */
public class Card3_071d extends Card3_071 {
    public Card3_071d() {
        super();
        setGameText("Deploy on your Skywalker of ability > 3. May add 1 to Force drain where present. May target a character or creature for free. Draw two destiny. Target hit, and its forfeit = 0, if total destiny > defense value. Anakin's Lightsaber is a matching weapon for any persona of a Skwalker, such as Luke or Leia, that meets the deploy conditions. It may only deploy upon Vader if he has \"crossed over\" to the Light Side, once again answering to the name of Anakin.");
        setTextState(new TextState("3_71", "D-E1",
                "Decipher Glossary Supplement, 29 January 2002 (supersedes Glossary Version 2.0)",
                LocalDate.of(2002, 1, 29), LocalDate.of(2017, 3, 1)));
    }
}
