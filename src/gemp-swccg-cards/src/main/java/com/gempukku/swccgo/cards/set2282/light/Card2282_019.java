package com.gempukku.swccgo.cards.set2282.light;

import com.gempukku.swccgo.cards.set1282.light.Card1282_019;
import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.2
 * Type: Character
 * Subtype: Alien
 * Title: Rennek (V)
 * Original version of 1282_19 (Virtual Block 8.2 release sheet): "reset a participating character's forfeit to 0" (the PC website card list, 2012-12-12, PC-E1, says "reduce"). Anything Goes only (docs/legacy-versions.md).
 */
public class Card2282_019 extends Card1282_019 {
    public Card2282_019() {
        super();
        setGameText("May be stacked on I Know That Laugh from hand, using 2 Force. While stacked on I Know That Laugh, stack face-up and during any battle may draw destiny. If destiny < number of your aliens in battle, reset a participating character's forfeit to 0.");
        setLegacyVersion(new LegacyVersion("1282_19", "", "Virtual Block 8.2 release sheet (2012-09-14)", LocalDate.of(2012, 9, 14), LocalDate.of(2012, 12, 12)));
    }
}
