package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: A New Hope
 * Title: Attack Run
 * Text state: d (Decipher's last word; docs/legacy-versions.md "Text states")
 * <p>
 * Decipher errata text from wiki.swccg.com [[Attack Run (Errata)]] (Glossary 2.0); superseded by the Players Committee errata, wiki [[Attack Run (PC Errata)]] (date unknown; upper bound March 2017).
 * The printed wording plays the same as the current text in GEMP, so this state reuses the bare card's game text
 * code (Card2_042) and differs only in its game text and text-state information.
 */
public class Card2_042d extends Card2_042 {
    public Card2_042d() {
        super();
        setGameText("Deploy on Death Star: Trench. During your move phase, you may make an Attack Run as follows: Enter Trench: Move up to 3 of your starfighters into trench (for free). Dark Side may immediately follow with up to 3 TIEs (for free). Provide Cover: Identify your lead starfighter (Proton Torpedoes* required) and wingmen (if any). Turbolaser Batteries and TIEs with weapons may now target your starfighters (wingmen first, then lead if no wingmen remaining). Hit starfighters are immediately lost. It's Away!: Draw two destiny. Pull Up!: All starfighters now move to Death Star system (for free). If (total destiny + X + Y - Z) > 15, Death Star is \"blown away.\" X = ability of lead pilot or 3 if Targeting Computer is present. Y = total sites at largest Rebel Base (Yavin 4 or Hoth). Z = highest ability of TIE pilots in trench. * Your Proton Torpedoes are immune to Overload.");
        setTextState(new TextState("2_42", "D-E1",
                "Decipher Glossary Version 2.0, November 1998",
                LocalDate.of(1998, 11, 1), LocalDate.of(2017, 3, 1)));
    }
}
