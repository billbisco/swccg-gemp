package com.gempukku.swccgo.cards.set2210.light;

import com.gempukku.swccgo.cards.set601.light.Card601_101;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 1
 * Type: Effect
 * Title: Rycar Ryjerd (V)
 * Original version of 1210_136 (current class 601_101): the Virtual Block 1 release sheet (2009-12-07): if your
 * character is about to be lost from table, all devices and Effects on it go to their owners' Used Piles (not optional).
 * Superseded on 2014-03-27 (PC forum "Current Rulings/Errata" p874174: "each player may place all their devices and
 * Effects"; PC-E1, the current card). Anything Goes only (docs/legacy-versions.md).
 */
public class Card2210_136 extends Card601_101 {
    public Card2210_136() {
        setGameText("Deploy on table. If your character is about to be lost from table, place all devices and Effects on that character in owner's Used Pile. Twice per game, may ▲ a device (or an Effect) that deploys on a character. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1210_136", "",
                "Virtual Block 1 release sheet (2009-12-07), card #137", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }

    @Override
    protected boolean eachPlayerMayPlace() {
        return false;
    }
}
