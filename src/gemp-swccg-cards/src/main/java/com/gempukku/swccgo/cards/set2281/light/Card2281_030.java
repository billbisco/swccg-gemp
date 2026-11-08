package com.gempukku.swccgo.cards.set2281.light;

import com.gempukku.swccgo.cards.set1281.light.Card1281_030;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.1
 * Type: Character
 * Subtype: Republic
 * Title: Supreme Chancellor Valorum (V)
 * Original version of 1281_30 (Virtual Block 8.1 release sheet): the sheet wording, before the PC website card list rephrase (2012-07-21, PC-E1). Same play as the current card. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2281_030 extends Card1281_030 {
    public Card2281_030() {
        super();
        setGameText("Agenda: order. May be targeted by Might Of The Republic. Your characters with politics (except Wedge and senators) are deploy -1 (and gain [Republic]) here. During your turn, may use 1 Force to take into hand from Reserve Deck a card with 'governor,' 'political,' or 'Valorum' in lore; reshuffle.");
        setLegacyVersion(new LegacyVersion("1281_30", "", "Virtual Block 8.1 release sheet (2012-05-11)", LocalDate.of(2012, 5, 11), LocalDate.of(2012, 7, 21)));
    }
}
