package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Premiere
 * Type: Weapon
 * Title: Turbolaser Battery
 * Text state: o (as printed, December 1995; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Turbolaser Battery]] and its Decipher archive scan (Premiere-D-turbolaserbattery-decipher-archive.gif); superseded by
 * the Decipher Glossary Version 2.0 errata (November 1998).
 * Under the rules this text plays as the bare card: "any" on a weapon deploys only on your own cards (Decipher Glossary 2.0, "weapons — deploying and carrying"), and a starship or vehicle compared to its "armor" or "maneuver" uses the one it has (its defense value).
 */
public class Card1_323o extends Card1_323 {
    public Card1_323o() {
        super();
        setGameText("Use 3 Force to deploy on any Star Destroyer or any mobile system. May target using 2 Force. Draw two destiny. Capital starship hit if total destiny -2 > armor. Starfighter hit if total destiny -5 > maneuver.");
        setTextState(new TextState("1_323", "Printed",
                "Premiere printing (Decipher, December 1995)",
                LocalDate.of(1995, 12, 1), LocalDate.of(1998, 11, 1)));
    }
}
