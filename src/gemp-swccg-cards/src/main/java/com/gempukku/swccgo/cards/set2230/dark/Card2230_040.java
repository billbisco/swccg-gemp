package com.gempukku.swccgo.cards.set2230.dark;

import com.gempukku.swccgo.cards.set1230.dark.Card1230_040;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 3
 * Type: Interrupt
 * Title: Abyssin Ornament & Wounded Wookiee (V)
 * Original version of 1230_40: the Virtual Block 3 release sheet text (Dec 2009), "choose: ▼ Rancor Pit and a rancor there, OR ▼ Power Of The Hutt and Sarlacc".
 * The current text is the PC website card list (by Jul 2012, PC-E1). Plays the same as the current card; only the wording differs.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2230_040 extends Card1230_040 {
    public Card2230_040() {
        super();
        setGameText("USED: Creature attacks at pits are immune to At Peace and Yarna d'al' Gargan until end of your next turn. (Immune to Sense.) OR Cancel an attempt to target a rancor with a character weapon. OR Use 1 Force (2 if a rancor on table) to deploy Nal Hutta from Reserve Deck; reshuffle. STARTING: If Great Pit Of Carkoon on table, deploy from Reserve Deck an Effect that is always [Immune to Alter] and choose: deploy from Reserve Deck Rancor Pit and a rancor there, OR deploy from Reserve Deck Power Of The Hutt and Sarlacc. Place this Interrupt in hand.");
        setLegacyVersion(new LegacyVersion("1230_40", "",
                "Virtual Block 3 release sheet (2009-12-07), card #41", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }
}
