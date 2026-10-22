package com.gempukku.swccgo.cards.set1200.light;

import com.gempukku.swccgo.cards.AbstractDefensiveShield;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.UseValueForTotalBattleDestinyWhenAllCanceledEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseStackedCardEffect;
import com.gempukku.swccgo.logic.effects.choose.PlayStackedDefensiveShieldEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Shields
 * Type: Defensive Shield
 * Title: Chasm
 */
public class Card1200_007 extends AbstractDefensiveShield {
    public Card1200_007() {
        super(Side.LIGHT, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Chasm", ExpansionSet.VIRTUAL_SHIELDS, Rarity.V);
        setVirtualSuffix(true);
        setLore("At Cloud City, Luke came face to face with his own destiny. Looking into the abyss, he made his decision.");
        setGameText("Plays on table. Substituted battle destinies do not count against battle destiny limits. If all your battle destinies were canceled (and not redrawn) use 1 for your total battle destiny instead. Once per game, may play a Defensive Shield from under your Starting Effect.");
    }

    @Override
    public Uniqueness getUniqueness() {
        return Uniqueness.UNRESTRICTED;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__CHASM_V__PLAY_DEFENSIVE_SHIELD;
        PhysicalCard startingEffect = Filters.findFirstActive(game, self, Filters.and(Filters.your(self), Filters.Starting_Effect));
        Filter filter = Filters.and(Filters.Defensive_Shield, Filters.playable(self));
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && startingEffect != null
                && GameConditions.hasStackedCards(game, startingEffect, filter)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Play a Defensive Shield");
            action.appendUsage(new OncePerGameEffect(action));
            action.appendTargeting(
                    new ChooseStackedCardEffect(action, playerId, startingEffect, filter) {
                        @Override
                        protected void cardSelected(PhysicalCard selectedCard) {
                            action.appendEffect(new PlayStackedDefensiveShieldEffect(action, self, selectedCard));
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();

        // "Substituted battle destinies do not count against battle destiny limits" is how the engine already draws
        // destiny (DrawDestinyEffect counts a substituted draw toward the number drawn, not toward the limit).
        // "If all your battle destinies were canceled (and not redrawn) use 1 for your total battle destiny instead."
        if (TriggerConditions.isBattleDestinyDrawingJustCompletedForBothPlayers(game, effectResult)
                && UseValueForTotalBattleDestinyWhenAllCanceledEffect.allBattleDestiniesCanceled(game.getGameState(), playerId)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Use 1 for total battle destiny");
            action.setActionMsg("Use 1 for " + playerId + "'s total battle destiny");
            action.appendEffect(
                    new UseValueForTotalBattleDestinyWhenAllCanceledEffect(action, playerId, 1));
            return Collections.singletonList(action);
        }
        return null;
    }
}
