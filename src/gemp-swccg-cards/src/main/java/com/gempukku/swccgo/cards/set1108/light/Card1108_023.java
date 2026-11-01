package com.gempukku.swccgo.cards.set1108.light;

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
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChoosePlayerBySideEffect;
import com.gempukku.swccgo.logic.modifiers.DestinyModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeCanceledModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 8: Jabba's Court
 * Type: Effect
 * Title: Ultimatum
 */
public class Card1108_023 extends AbstractNormalEffect {
    public Card1108_023() {
        super(Side.LIGHT, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Ultimatum, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_8, Rarity.V);
        setVirtualSuffix(true);
        setLore("\"Jabba! This is your last chance. Free us or die.\"");
        setGameText("Deploy on table. Once per turn, during a battle at a Jabba's Palace site, your destiny draws are each +1 and your Interrupts may not be canceled. May place this Effect out of play to cause a player to activate 2 Force.");
        addIcons(Icon.JABBAS_PALACE);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId battleId = GameTextActionId.OVS__ULTIMATUM__DESTINY_AND_INTERRUPTS;
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, battleId)
                && GameConditions.isDuringBattleAt(game, Filters.Jabbas_Palace_site)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, battleId);
            action.setText("Destiny draws +1; Interrupts may not be canceled");
            action.setActionMsg("Make " + playerId + "'s destiny draws each +1 and Interrupts not cancelable for this battle");
            action.appendUsage(
                    new OncePerTurnEffect(action));
            action.appendEffect(
                    new AddUntilEndOfBattleModifierEffect(action,
                            new DestinyModifier(self, Filters.your(self), 1),
                            playerId + "'s destiny draws are each +1"));
            action.appendEffect(
                    new AddUntilEndOfBattleModifierEffect(action,
                            new MayNotBeCanceledModifier(self, Filters.and(Filters.your(self), Filters.Interrupt)),
                            playerId + "'s Interrupts may not be canceled"));
            actions.add(action);
        }

        GameTextActionId activateId = GameTextActionId.OVS__ULTIMATUM__ACTIVATE_FORCE;
        final String opponent = game.getOpponent(playerId);
        final boolean youCanActivate = GameConditions.canActivateForce(game, playerId);
        final boolean opponentCanActivate = GameConditions.canActivateForce(game, opponent);
        if (youCanActivate || opponentCanActivate) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, activateId);
            action.setText("Place out of play to make a player activate 2 Force");
            action.setActionMsg("Place " + GameUtils.getCardLink(self) + " out of play to cause a player to activate 2 Force");
            if (youCanActivate && opponentCanActivate) {
                action.appendTargeting(
                        new ChoosePlayerBySideEffect(action, playerId) {
                            @Override
                            protected void playerChosen(SwccgGame game, final String playerChosen) {
                                action.appendCost(
                                        new PlaceCardOutOfPlayFromTableEffect(action, self));
                                action.appendEffect(
                                        new ActivateForceEffect(action, playerChosen, 2));
                            }
                        });
            } else {
                action.appendCost(
                        new PlaceCardOutOfPlayFromTableEffect(action, self));
                action.appendEffect(
                        new ActivateForceEffect(action, youCanActivate ? playerId : opponent, 2));
            }
            actions.add(action);
        }
        return actions;
    }
}
