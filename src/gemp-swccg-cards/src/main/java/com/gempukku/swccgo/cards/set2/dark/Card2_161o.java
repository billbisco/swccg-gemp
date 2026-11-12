package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: A New Hope
 * Type: Weapon
 * Title: Superlaser
 * Text state: o (as printed, July 1996; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Superlaser]] and its Decipher archive scan (ANH-D-superlaser-decipher-archive.gif); superseded by
 * the Decipher Glossary Version 2.0 errata (November 1998).
 * Under the rules this text plays as the bare card: "any" on a weapon deploys only on your own cards (Decipher Glossary 2.0, "weapons — deploying and carrying"), and a starship or vehicle compared to its "armor" or "maneuver" uses the one it has (its defense value).
 */
public class Card2_161o extends Card2_161 {
    public Card2_161o() {
        super();
        setGameText("Deploy on Death Star system at parsec 0. May target a capital starship at Death Star system, or at a system it orbits, using 4 Force. Draw two destiny. Capital starship hit if total destiny > armor.");
        setTextState(new TextState("2_161", "Printed",
                "A New Hope printing (Decipher, July 1996)",
                LocalDate.of(1996, 7, 1), LocalDate.of(1998, 11, 1)));
    }
}
