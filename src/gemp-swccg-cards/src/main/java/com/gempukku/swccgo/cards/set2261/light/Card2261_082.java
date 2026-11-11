package com.gempukku.swccgo.cards.set2261.light;

import com.gempukku.swccgo.cards.set1261.light.Card1261_082;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 6.1
 * Type: Effect
 * Title: Maneuvering Flaps & Nick Of Time
 * Original version of 1261_82: the Virtual Block 6.1 release sheet text ("▲ its matching pilot character ... and deploy
 * both simultaneously", "May not be suspended."; the Master List reprints it). The PC card list, PC-E1 and now the
 * current card, drops "May not be suspended." Gameplay test:
 * Card_1261_082_Tests.originalVersionDeploysAndMayNotBeSuspendedWhileAnotherEffectIs.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_082 extends Card1261_082 {
    public Card2261_082() {
        setGameText("Deploy on table. Your vehicles with a pilot character or gunner aboard are not hit when 'thrown.' During your deploy phase, may reveal an unpiloted combat vehicle from hand to ▲ its matching pilot character (or vice versa) and deploy both simultaneously. Whenever you play Desperate Tactics, Lucky Shot, On Target, Rapid Fire, or Steady Aim, may draw top card from Reserve Deck. May not be suspended. (Immune to Alter.)");
        setLegacyVersion(new LegacyVersion("1261_82", "",
                "Virtual Block 6.1 release sheet (2009-09-04), card #81", LocalDate.of(2009, 9, 4), LocalDate.of(2012, 7, 21)));
    }
}
