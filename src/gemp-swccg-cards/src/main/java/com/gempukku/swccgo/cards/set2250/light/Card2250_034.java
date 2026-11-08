package com.gempukku.swccgo.cards.set2250.light;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.set1250.light.Card1250_034;
import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Block 5
 * Type: Character
 * Subtype: Rebel/Republic
 * Title: Bail Organa
 * PC-E1 version of 1250_34: the Virtual Cards Master List text (2013-04-27), "if you just Force drained with a
 * Rebel, may retrieve 1 Force" (the Dec 2009 sheet, which GEMP's current card shows, needs opponent to have just lost
 * Force to that drain). Anything Goes only (docs/legacy-versions.md).
 */
public class Card2250_034 extends Card1250_034 {
    public Card2250_034() {
        setGameText("Agenda: rebellion. Adds one battle destiny with Leia. Your [Coruscant] senators and [Coruscant] Political Effects are lost. While at Senate: unique (•) Rebels of ability < 4 are power +2, and, if you just Force drained with a Rebel, may retrieve 1 Force.");
        setLegacyVersion(new LegacyVersion("1250_34", "PC-E1",
                "Virtual Cards Master List (2013-04-27)", LocalDate.of(2013, 4, 27), null));
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game,
            EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (GameConditions.isAtLocation(game, self, Filters.Galactic_Senate)
                && TriggerConditions.forceDrainCompleted(game, effectResult, playerId,
                        Filters.sameLocationAs(self, Filters.and(Filters.your(self), Filters.Rebel)))
                && GameConditions.hasLostPile(game, playerId)) {
            OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Retrieve 1 Force");
            action.appendEffect(new RetrieveForceEffect(action, playerId, 1));
            return Collections.singletonList(action);
        }
        return null;
    }
}
