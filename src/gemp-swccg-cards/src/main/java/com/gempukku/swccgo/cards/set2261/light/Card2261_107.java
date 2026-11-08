package com.gempukku.swccgo.cards.set2261.light;

import com.gempukku.swccgo.cards.set601.light.Card601_253;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Type: Starship
 * Title: Wedge In Red Squadron 1
 * Original version of 1261_107: the Virtual Block 6.1 release sheet wording (Sep 2009), "attrition or total power"; same play as the current card.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_107 extends Card601_253 {
    public Card2261_107() {
        setGameText("Permanent pilot is •Wedge, who provides ability of 3. During a battle with opponent's [Independent] starship, may add one destiny to attrition or total power. Immune to Come With Me, Tallon Roll, and attrition < 4.");
        setLegacyVersion(new LegacyVersion("1261_107", "",
                "Virtual Block 6.1 release sheet (2009-09-04), card #112", LocalDate.of(2009, 9, 4), LocalDate.of(2013, 4, 27)));
    }
}
