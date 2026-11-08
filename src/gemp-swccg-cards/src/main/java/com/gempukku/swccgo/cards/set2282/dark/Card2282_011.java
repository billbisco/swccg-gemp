package com.gempukku.swccgo.cards.set2282.dark;

import com.gempukku.swccgo.cards.set1282.dark.Card1282_011;
import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotTargetToBeCapturedModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.StandardEffect;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.2
 * Type: Weapon
 * Subtype: Character
 * Title: Assassin's Blaster Rifle
 * Original version of 1282_11 (Virtual Block 8.2 release sheet): "Deploy on your assassin" and the hit target "may not be captured this turn" (the PC website card list, 2012-12-12, PC-E1, says "an assassin" and "may not be captured"). Anything Goes only (docs/legacy-versions.md).
 */
public class Card2282_011 extends Card1282_011 {
    public Card2282_011() {
        super();
        setGameText("Deploy on your assassin; assassin is power +1. May target a character at same or adjacent site for free. Draw destiny. If destiny +1 > defense value: target hit, may not be captured this turn, and you may place a bounty on target in Used Pile to retrieve 2 Force.");
        setLegacyVersion(new LegacyVersion("1282_11", "", "Virtual Block 8.2 release sheet (2012-09-14)", LocalDate.of(2012, 9, 14), LocalDate.of(2012, 12, 12)));
    }

    /** The release sheet says the target "may not be captured this turn". */
    @Override
    protected StandardEffect mayNotBeCapturedEffect(Action action, PhysicalCard self, PhysicalCard cardHit) {
        return new AddUntilEndOfTurnModifierEffect(action,
                new MayNotTargetToBeCapturedModifier(self, cardHit),
                GameUtils.getCardLink(cardHit) + " may not be captured this turn");
    }
}
