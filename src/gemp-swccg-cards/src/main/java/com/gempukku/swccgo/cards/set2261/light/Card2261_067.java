package com.gempukku.swccgo.cards.set2261.light;

import com.gempukku.swccgo.cards.set1261.light.Card1261_067;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Type: Character
 * Title: Jabba's Prize
 * Original version of 1261_67: the Virtual Block 6.1 release sheet text (the PC card list, PC-E1 and now the current
 * card, moves the Falcon sentence and says "toward" / "start under"). Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_067 extends Card1261_067 {
    public Card2261_067() {
        setGameText("Jabba's Prize is a Light Side card and does not count towards your deck limit (but subtracts one from the number of cards you may place under your Starting Effect). Reveal to opponent when deploying your Starting Effect. For remainder of game, you may not deploy [Maintenance] Falcon. Deploys only at start of game if Jabba's Prize is at Security Tower (replaces opponent's Jabba's Prize imprisoned in Security Tower); otherwise place out of play. May not be placed in Reserve Deck. Jabba's Prize is a persona of Corran Horn only while on table. If Jabba's Prize was just released or leaves table, place it out of play. While Jabba's Prize is at Audience Chamber, opponent's battle destiny draws there are +1.");
        setLegacyVersion(new LegacyVersion("1261_67", "",
                "Virtual Block 6.1 release sheet (2009-09-04), card #65", LocalDate.of(2009, 9, 4), LocalDate.of(2012, 7, 21)));
    }
}
