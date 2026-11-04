package com.gempukku.swccgo.cards.effects;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotRemoveJustLostCardsFromLostPileModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.HitResult;

import java.util.Collections;
import java.util.List;

/**
 * Shared clause of the Virtual Block 7.4 lightsabers (Cyborg Commander's Lightsabers, Sidious' Lightsaber):
 * "Target hit, and may not satisfy attrition (or be removed from Lost Pile this turn)". The hit and attrition part is
 * the fire action; this adds the Lost Pile part when the weapon hits a card.
 */
public final class LegacyLightsaberHitRules {
    private LegacyLightsaberHitRules() {
    }

    public static List<RequiredGameTextTriggerAction> mayNotBeRemovedFromLostPileThisTurnWhenHit(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.justHitBy(game, effectResult, Filters.any, self)) {
            PhysicalCard cardHit = ((HitResult) effectResult).getCardHit();
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Make " + GameUtils.getFullName(cardHit) + " unable to be removed from Lost Pile this turn");
            action.appendEffect(
                    new AddUntilEndOfTurnModifierEffect(action,
                            new MayNotRemoveJustLostCardsFromLostPileModifier(self, Filters.sameCardId(cardHit)),
                            GameUtils.getCardLink(cardHit) + " may not be removed from Lost Pile this turn"));
            return Collections.singletonList(action);
        }
        return null;
    }
}
