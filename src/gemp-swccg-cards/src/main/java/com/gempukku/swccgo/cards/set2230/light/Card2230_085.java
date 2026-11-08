package com.gempukku.swccgo.cards.set2230.light;

import com.gempukku.swccgo.cards.set1230.light.Card1230_085;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 3
 * Type: Effect
 * Title: Commando Training (V)
 * Original version of 1230_85: the Virtual Block 3 release sheet text (Dec 2009), "(and 'power -1' in their game text)"; the PC moved the parenthesis on 2010-06-28 ("moved parenthesis for clarification", PC-E1).
 * The current text is the PC errata of 2010-06-28 (PC-E1). Plays the same as the current card; only the wording differs.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2230_085 extends Card1230_085 {
    public Card2230_085() {
        super();
        setGameText("Deploy on table. Ben Kenobi is deploy +1. Your C-3PO, Lando, and Rebels (except [Death Star II] Jedi) ignore location deployment restrictions (and 'power -1' in their game text) and apply their Endor deployment modifiers at any location. Once per game, may deploy a [Virtual Block 4] Effect from Reserve Deck; reshuffle. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1230_85", "",
                "Virtual Block 3 release sheet (2009-12-07), card #86", LocalDate.of(2009, 12, 7), LocalDate.of(2010, 6, 28)));
    }
}
