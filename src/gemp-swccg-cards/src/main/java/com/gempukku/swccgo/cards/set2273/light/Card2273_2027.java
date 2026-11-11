package com.gempukku.swccgo.cards.set2273.light;

import com.gempukku.swccgo.cards.set601.light.Card601_054;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 7.3
 * Type: Character
 * Title: Master Kenobi
 * PC-E3 version of 1273_27 (Batch S1, Bill 2026-11-11: the forum CBT errata of 2012-04-20 is PC-E2, 2273_3027): the PC website card list text (2012-07-21 .. 2013-01-11), which added the cost "may lose
 * 1 Force (or use 2 Force) to" place a just-forfeited Rebel in Used Pile and printed "once per turn," twice (kept as
 * published). It plays as the current card; then the PC card image (PC-E4, 2273_4027, "Once per turn", by 2013-02-25) and the Master List (PC-E5, 2013-04-27, the current card), which repeats the forum text word for word.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2273_2027 extends Card601_054 {
    public Card2273_2027() {
        setGameText("While 'communing': you may not deploy [Maintenance] cards or [Episode I] Jedi; Rebels of ability < 5 draw one battle destiny if unable to otherwise and, once per turn, once per turn, may lose 1 Force (or use 2 Force) to place one just forfeited in Used Pile; once per turn, may deploy from Reserve Deck a Tatooine battleground (or Obi-Wan's Hut); reshuffle.");
        setLegacyVersion(new LegacyVersion("1273_27", "PC-E3",
                "PC website card list (by 2012-07-21)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 2, 25)));
    }
}
