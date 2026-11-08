package com.gempukku.swccgo.cards.set2281.light;

import com.gempukku.swccgo.cards.set1281.light.Card1281_027;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.1
 * Type: Character
 * Subtype: Alien
 * Title: Han Solo, Innocent Scoundrel
 * Original version of 1281_27 (Virtual Block 8.1 release sheet), before the PC website card list rephrase (2012-12-12, PC-E1, the current card). Same play. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2281_027 extends Card1281_027 {
    public Card2281_027() {
        super();
        setGameText("Adds 3 to power of anything he pilots. Draws one battle destiny if unable to otherwise. During battle, may fire a blaster twice. Once per turn, if opponent is about to draw weapon destiny targeting Han, they draw two and you choose one. Immune to attrition < 3 (< 4 if armed).");
        setLegacyVersion(new LegacyVersion("1281_27", "", "Virtual Block 8.1 release sheet (2012-05-11)", LocalDate.of(2012, 5, 11), LocalDate.of(2012, 12, 12)));
    }
}
