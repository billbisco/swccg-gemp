package com.gempukku.swccgo.cards.set2273.light;

import com.gempukku.swccgo.cards.set601.light.Card601_054;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Title: Master Kenobi
 * PC-E4 version of 1273_27 (Batch S1, Bill 2026-11-11: a PC card image's capitals count): the PC card image (on the PC
 * server by 2013-02-25) prints "Used Pile; Once per turn" with a capital O. Followed by the Master List (PC-E5,
 * 2013-04-27, the current card), lowercase. Same play.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2273_4027 extends Card601_054 {
    public Card2273_4027() {
        setGameText("While 'communing': you may not deploy [Maintenance] cards or [Episode I] Jedi; Rebels of ability < 5 draw one battle destiny if unable to otherwise and, once per turn, may lose 1 Force (or use 2 Force) to place one just forfeited in Used Pile; Once per turn, may deploy from Reserve Deck a Tatooine battleground (or Obi-Wan's Hut); reshuffle.");
        setLegacyVersion(new LegacyVersion("1273_27", "PC-E4",
                "PC card image (on the PC server by 2013-02-25)", LocalDate.of(2013, 2, 25), LocalDate.of(2013, 4, 27)));
    }
}
