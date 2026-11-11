package com.gempukku.swccgo.cards.set2230.dark;

import com.gempukku.swccgo.cards.set1230.dark.Card1230_040;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 3
 * Type: Interrupt
 * Title: •Abyssin Ornament & •Wounded Wookiee (V)
 * PC-E1 version of 1230_40 (Batch R8-U): the PC website card list text (by 2012-07-21; the list was generated from the
 * Holotable card data), after the original (2230_2040) and before the PC card image (on the PC server by 2013-02-25, PC-E2, 2230_3040), which prints the original words again.
 * Against the original: one change: "choose: ▼ Rancor Pit …, OR ▼ Power Of The Hutt …" becomes "choose to ▼: Rancor Pit …, OR Power Of The Hutt …" (one arrow before the colon instead of one before each choice; the list writes it "choose to deploy from Reserve Deck:").
 * Written-out icons in the list are notation, so the text here uses the icons. Same play as the current card.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2230_1040 extends Card1230_040 {
    public Card2230_1040() {
        super();
        setGameText("USED: Creature attacks at pits are immune to At Peace and Yarna d'al' Gargan until end of your next turn. (Immune to Sense.) OR Cancel an attempt to target a rancor with a character weapon. OR Use 1 Force (2 if a rancor on table) to [download] Nal Hutta. STARTING: If Great Pit Of Carkoon on table, [download] an Effect that is always [Immune to Alter] and choose to [download]: Rancor Pit and a rancor there, OR Power Of The Hutt and Sarlacc. Place this Interrupt in hand.");
        setLegacyVersion(new LegacyVersion("1230_40", "PC-E1",
                "PC website card list (by 2012-07-21)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}
