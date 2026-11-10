package com.gempukku.swccgo.cards.set2250.light;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import com.gempukku.swccgo.cards.set1250.light.Card1250_034;
import com.gempukku.swccgo.common.LegacyVersion;

import java.time.LocalDate;

/**
 * Set: Virtual Block 5
 * Type: Character
 * Subtype: Rebel/Republic
 * Title: Bail Organa
 * Original version of 1250_34: the Virtual Block 5 release sheet text, "if opponent just lost Force to a Force drain
 * by a Rebel, may retrieve 1 Force" (the Virtual Cards Master List, PC-E1 and now the current card, says "if you just
 * Force drained with a Rebel"). Anything Goes only (docs/legacy-versions.md).
 */
public class Card2250_034 extends Card1250_034 {
    public Card2250_034() {
        setGameText("Agenda: rebellion. Adds one battle destiny with Leia. Your [Coruscant] senators and [Coruscant] Political Effects are lost. While at Senate: unique (•) Rebels of ability < 4 are power +2, and, if opponent just lost Force to a Force drain by a Rebel, may retrieve 1 Force.");
        setLegacyVersion(new LegacyVersion("1250_34", "",
                "Virtual Block 5 release sheet (2009-12-07), card #35; superseded by the PC forum errata (2014-01-03)", LocalDate.of(2009, 12, 7), LocalDate.of(2014, 1, 3)));
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game,
            EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (GameConditions.isAtLocation(game, self, Filters.Galactic_Senate)
                && TriggerConditions.justLostForceFromForceDrainAt(game, effectResult, game.getOpponent(playerId),
                        Filters.sameLocationAs(self, Filters.and(Filters.your(self), Filters.Rebel)), true)
                && GameConditions.hasLostPile(game, playerId)) {
            OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Retrieve 1 Force");
            action.appendEffect(new RetrieveForceEffect(action, playerId, 1));
            return Collections.singletonList(action);
        }
        return null;
    }
}
