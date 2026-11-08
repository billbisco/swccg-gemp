package com.gempukku.swccgo.cards.set2282.light;

import com.gempukku.swccgo.logic.timing.UsageEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.cards.set1282.light.Card1282_012;
import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;

import java.time.LocalDate;

/**
 * Set: Virtual Block 8.2
 * Type: Character
 * Subtype: New Republic
 * Title: Anakin Solo
 * Original version of 1282_12 (Virtual Block 8.2 release sheet): steals "Once per turn" (the PC website card list, 2012-12-12, PC-E1, says "Once during battle"). Anything Goes only (docs/legacy-versions.md).
 */
public class Card2282_012 extends Card1282_012 {
    public Card2282_012() {
        super();
        setGameText("Power +2 while armed with a lightsaber. May use any 'stolen' lightsaber. Once per turn, where present, may steal an opponent's just-lost lightsaber. Immune to attrition < 3 (< 5 while armed with a 'stolen' lightsaber).");
        setLegacyVersion(new LegacyVersion("1282_12", "", "Virtual Block 8.2 release sheet (2012-09-14)", LocalDate.of(2012, 9, 14), LocalDate.of(2012, 12, 12)));
    }

    /** The release sheet says "Once per turn". */
    @Override
    protected boolean mayStealNow(SwccgGame game, PhysicalCard self, String playerId, int gameTextSourceCardId) {
        return GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId);
    }

    @Override
    protected UsageEffect stealUsageLimit(OptionalGameTextTriggerAction action) {
        return new OncePerTurnEffect(action);
    }
}
