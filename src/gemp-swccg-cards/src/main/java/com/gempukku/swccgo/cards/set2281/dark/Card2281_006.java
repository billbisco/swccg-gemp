package com.gempukku.swccgo.cards.set2281.dark;

import com.gempukku.swccgo.cards.set1281.dark.Card1281_006;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.1
 * Type: Character
 * Subtype: Alien
 * Title: R'tic H'weei
 * Original version of 1281_6 (Virtual Block 8.1 release sheet), before the PC website card list text (2012-07-21, PC-E1). Same play as the current card in GEMP. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2281_006 extends Card1281_006 {
    public Card2281_006() {
        super();
        setGameText("Once per game, may ▲ Jawa Blaster or a card with 'sandcrawler' in title or gametext. Once per turn, if present at a Tatooine battleground and there is more than 1 Force in opponent's Force Pile, you may use 1 Force in opponent's Force Pile.");
        setLegacyVersion(new LegacyVersion("1281_6", "", "Virtual Block 8.1 release sheet (2012-05-11)", LocalDate.of(2012, 5, 11), LocalDate.of(2012, 7, 21)));
    }
}
