package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Premiere
 * Type: Weapon
 * Title: Gaderffii Stick
 * Text state: o (as printed, December 1995; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Gaderffii Stick]] and its Decipher archive scan (Premiere-D-gaderffiistick-decipher-archive.gif); superseded by
 * the Decipher Glossary Version 2.0 errata (November 1998).
 * Decipher Glossary 2.0 ("Gaderffii Stick"): "opponent" here is the Tusken Raider's opponent in battle, so a success stops the weapons of one target character, which is how the bare card plays.
 */
public class Card1_315o extends Card1_315 {
    public Card1_315o() {
        super();
        setGameText("Use 2 Force to deploy on any Tusken Raider. May target for free. If a battle has just been initiated where present, immediately draw two destiny. If total destiny > 5, opponent's weapons are \"knocked away\" (may not be used this battle).");
        setTextState(new TextState("1_315", "Printed",
                "Premiere printing (Decipher, December 1995)",
                LocalDate.of(1995, 12, 1), LocalDate.of(1998, 11, 1)));
    }
}
