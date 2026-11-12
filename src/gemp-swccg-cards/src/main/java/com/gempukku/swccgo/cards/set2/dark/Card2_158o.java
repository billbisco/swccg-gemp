package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: A New Hope
 * Type: Weapon
 * Title: Enhanced TIE Laser Cannon
 * Text state: o (as printed, July 1996; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Enhanced TIE Laser Cannon]] and its Decipher archive scan (ANH-D-enhancedtielasercannon-decipher-archive.gif); superseded by
 * the Decipher Glossary Version 2.0 errata (November 1998).
 * Under the rules this text plays as the bare card: "any" on a weapon deploys only on your own cards (Decipher Glossary 2.0, "weapons — deploying and carrying"), and a starship or vehicle compared to its "armor" or "maneuver" uses the one it has (its defense value).
 */
public class Card2_158o extends Card2_158 {
    public Card2_158o() {
        super();
        setGameText("Use 1 Force to deploy on any TIE. May target using 1 Force. Draw destiny. Capital starship hit if destiny -2 > armor. Starfighter hit if destiny > maneuver.");
        setTextState(new TextState("2_158", "Printed",
                "A New Hope printing (Decipher, July 1996)",
                LocalDate.of(1996, 7, 1), LocalDate.of(1998, 11, 1)));
    }
}
