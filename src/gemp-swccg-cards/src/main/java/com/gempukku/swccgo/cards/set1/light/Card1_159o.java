package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Premiere
 * Type: Weapon
 * Title: Quad Laser Cannon
 * Text state: o (as printed, December 1995; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Quad Laser Cannon]] and its Decipher archive scan (Premiere-L-quadlasercannon-decipher-archive.gif); superseded by
 * the Decipher Glossary Version 2.0 errata (November 1998).
 * Under the rules this text plays as the bare card: "any" on a weapon deploys only on your own cards (Decipher Glossary 2.0, "weapons — deploying and carrying"), and a starship or vehicle compared to its "armor" or "maneuver" uses the one it has (its defense value).
 */
public class Card1_159o extends Card1_159 {
    public Card1_159o() {
        super();
        setGameText("Use 2 Force to deploy on Corellian Corvette or Millennium Falcon. May target using 1 Force. Draw destiny. Capital starship hit if destiny > armor. Starfighter hit if destiny +1 > maneuver.");
        setTextState(new TextState("1_159", "Printed",
                "Premiere printing (Decipher, December 1995)",
                LocalDate.of(1995, 12, 1), LocalDate.of(1998, 11, 1)));
    }
}
