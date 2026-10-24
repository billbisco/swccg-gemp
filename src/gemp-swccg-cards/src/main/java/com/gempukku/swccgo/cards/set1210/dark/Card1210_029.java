package com.gempukku.swccgo.cards.set1210.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.ForceDrainState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Block 1
 * Type: Effect
 * Title: A Bright Center To The Universe
 */
public class Card1210_029 extends AbstractNormalEffect {
    public Card1210_029() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.A_Bright_Center_To_The_Universe, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("The intimidating power of the Empire was focused in the core systems, allowing the Emperor to ignore minor activities occurring on the Outer Rim.");
        setGameText("Deploy on table. Once per turn, if you control more battlegrounds than opponent, when you lose Force to a Force drain, may retrieve X Force (may not be canceled), where X = all bonuses to that Force drain.");
        addIcons(Icon.SPECIAL_EDITION);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__A_BRIGHT_CENTER_TO_THE_UNIVERSE_V__RETRIEVE_FORCE_DRAIN_BONUSES;
        String opponent = game.getOpponent(playerId);

        if (TriggerConditions.isAboutToLoseForceFromForceDrainAt(game, effectResult, playerId, Filters.any)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)) {
            int yours = Filters.countTopLocationsOnTable(game, Filters.and(Filters.battleground, Filters.controls(playerId)));
            int theirs = Filters.countTopLocationsOnTable(game, Filters.and(Filters.battleground, Filters.controls(opponent)));
            if (yours <= theirs) {
                return null;
            }

            ForceDrainState forceDrainState = game.getGameState().getForceDrainState();
            PhysicalCard location = forceDrainState != null ? forceDrainState.getLocation() : null;
            if (location == null) {
                return null;
            }

            Icon icon = Icon.LIGHT_FORCE;
            int icons = game.getModifiersQuerying().getIconCount(game.getGameState(), location, icon);
            float amount = game.getModifiersQuerying().getForceDrainAmount(game.getGameState(), location, opponent);
            final int x = (int) amount - icons;
            if (x <= 0) {
                return null;
            }

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Retrieve " + x + " Force");
            action.appendUsage(
                    new OncePerTurnEffect(action));
            action.appendEffect(
                    new RetrieveForceEffect(action, playerId, x) {
                        @Override
                        public boolean mayNotBeCanceled() {
                            return true;
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
