package com.gempukku.swccgo.cards.set3.light;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Hoth
 * Type: Weapon
 * Title: Atgar Laser Cannon
 * Text state: o (as printed, November 1996; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Atgar Laser Cannon]] and its Decipher archive scan (Hoth-L-atgarlasercannon-decipher-archive.gif); superseded by
 * the Decipher Glossary Version 2.0 errata (November 1998).
 * Under the rules this text plays as the bare card: "any" on a weapon deploys only on your own cards (Decipher Glossary 2.0, "weapons — deploying and carrying"), and a starship or vehicle compared to its "armor" or "maneuver" uses the one it has (its defense value).
 */
public class Card3_072o extends Card3_072 {
    public Card3_072o() {
        super();
        setGameText("Deploy on an exterior planet site. Cannot be moved. Your warrior present may target at same or adjacent site using 2 Force. Draw destiny. Vehicle 'crashes' if destiny +2 > armor. Vehicle hit if destiny +1 > maneuver.");
        setTextState(new TextState("3_72", "Printed",
                "Hoth printing (Decipher, November 1996)",
                LocalDate.of(1996, 11, 1), LocalDate.of(1998, 11, 1)));
    }
}
