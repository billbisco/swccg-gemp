package com.gempukku.swccgo.cards.set2220.dark;

import com.gempukku.swccgo.cards.set1220.dark.Card1220_023;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 2
 * Type: Effect
 * Title: 3,720 To 1
 * Original version of 1220_23: the Virtual Block 2 release sheet (2009-12-07) text.
 * Superseded by the PC website card list text (PC-E1, by 2012-07-21: "the bottom 2 cards" for "the bottom two cards" and "to deploy (from Lost Pile)" for "to deploy from Lost Pile:"), the current card.
 * Plays the same as the current card; only the wording differs. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2220_023 extends Card1220_023 {
    public Card2220_023() {
        super();
        setGameText("Deploy on table. Droids are destiny +1. Once per turn, may place the bottom two cards in Lost Pile out of play to deploy from Lost Pile: Forced Servitude, a character weapon, a location, or an Effect that deploys on a location (without 'lose' in game text). (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1220_23", "",
                "Virtual Block 2 release sheet (2009-12-07)", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
