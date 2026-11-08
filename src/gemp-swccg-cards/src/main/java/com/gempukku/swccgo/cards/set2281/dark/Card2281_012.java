package com.gempukku.swccgo.cards.set2281.dark;

import com.gempukku.swccgo.cards.set1281.dark.Card1281_012;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.1
 * Type: Effect
 * Title: On The Hunt
 * Original version of 1281_12 (Virtual Block 8.1 release sheet), before the PC website card list rephrase (2012-07-21, PC-E1, the current card). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2281_012 extends Card1281_012 {
    public Card2281_012() {
        super();
        setGameText("Deploy on Contract Killers. Assassins are defense value +1. Once per game may retrieve Death Mark. At end of opponent's turn, if their character targeted by Death Mark is at a non-battleground or aboard their starship, opponent loses 3 Force. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1281_12", "", "Virtual Block 8.1 release sheet (2012-05-11)", LocalDate.of(2012, 5, 11), LocalDate.of(2012, 7, 21)));
    }
}
