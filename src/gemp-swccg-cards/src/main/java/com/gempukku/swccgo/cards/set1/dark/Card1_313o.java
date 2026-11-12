package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Premiere
 * Type: Weapon
 * Title: Boosted TIE Cannon
 * Text state: o (as printed, December 1995; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Boosted TIE Cannon]] and its Decipher archive scan (Premiere-D-boostedtiecannon-decipher-archive.gif); superseded by
 * the Decipher Glossary Version 2.0 errata (November 1998).
 * Under the rules this text plays as the bare card: "any" on a weapon deploys only on your own cards (Decipher Glossary 2.0, "weapons — deploying and carrying"), and a starship or vehicle compared to its "armor" or "maneuver" uses the one it has (its defense value).
 */
public class Card1_313o extends Card1_313 {
    public Card1_313o() {
        super();
        setGameText("Use 1 Force to deploy on any TIE Advanced x1. May target using 1 Force. Draw destiny. Capital starship hit if destiny -1 > armor. Starfighter hit if destiny +1 > maneuver.");
        setTextState(new TextState("1_313", "Printed",
                "Premiere printing (Decipher, December 1995)",
                LocalDate.of(1995, 12, 1), LocalDate.of(1998, 11, 1)));
    }
}
