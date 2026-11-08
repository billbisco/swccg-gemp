package com.gempukku.swccgo.cards.set2230.light;

import com.gempukku.swccgo.cards.set1230.light.Card1230_065;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 3
 * Type: Character
 * Title: Aved Luun (V)
 * Original version of 1230_65: the Virtual Block 3 release sheet text (Dec 2009), "May ▼ one Jawa Camp, Sergeant Doallyn, or Thedit".
 * The current text is the PC website card list (by Jul 2012, PC-E1). Plays the same as the current card; only the wording differs.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2230_065 extends Card1230_065 {
    public Card2230_065() {
        super();
        setGameText("Deploys only on Tatooine. Adds one battle destiny with Kalit. Your Jawas and sandcrawlers are destiny +2. May deploy one Jawa Camp, Sergeant Doallyn, or Thedit from Reserve Deck; reshuffle.");
        setLegacyVersion(new LegacyVersion("1230_65", "",
                "Virtual Block 3 release sheet (2009-12-07), card #65", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
