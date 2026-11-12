package com.gempukku.swccgo.cards.set6.dark;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Jabba's Palace
 * Type: Weapon
 * Title: Double Laser Cannon
 * Text state: o (as printed, May 1998; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Double Laser Cannon]] and its Decipher archive scan (JP-D-doublelasercannon-decipher-archive.gif); superseded by
 * the Decipher Glossary Version 2.0 errata (November 1998).
 * Under the rules this text plays as the bare card: "any" on a weapon deploys only on your own cards (Decipher Glossary 2.0, "weapons — deploying and carrying"), and a starship or vehicle compared to its "armor" or "maneuver" uses the one it has (its defense value).
 */
public class Card6_176o extends Card6_176 {
    public Card6_176o() {
        super();
        setGameText("Use 4 Force to deploy on Jabba's Sail Barge or a sandcrawler; it is power +3 and immune to attrition < 5. Your warrior present, if aboard, may target using 2 Force. Draw destiny. Vehicle hit if destiny +2 > armor or maneuver.");
        setTextState(new TextState("6_176", "Printed",
                "Jabba's Palace printing (Decipher, May 1998)",
                LocalDate.of(1998, 5, 1), LocalDate.of(1998, 11, 1)));
    }
}
