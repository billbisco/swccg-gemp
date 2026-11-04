package com.gempukku.swccgo.cards.set1283.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
import com.gempukku.swccgo.cards.effects.MoveAsReactEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 8.3
 * Type: Interrupt
 * Subtype: Lost
 * Title: Rug Hug (V)
 */
public class Card1283_023 extends AbstractLostInterrupt {
    public Card1283_023() {
        super(Side.LIGHT, 4, "Rug Hug", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_8_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("Wookiees are known to be creatures of great emotion and are very protective of family and friends. Chewbacca has come to treat Luke as a member of his own family.");
        setGameText("Use 2 Force to move one of your characters as a 'react' to a battle opponent just initiated. OR If Luke (or a Jedi) and your alien are in battle together, add one battle destiny. OR Once per game, activate X Force, where X = number of your aliens on table.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        String opponent = game.getOpponent(playerId);

        if (TriggerConditions.battleInitiated(game, effectResult, opponent)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 2)) {
            Filter characterFilter = Filters.and(Filters.your(self), Filters.character, Filters.canMoveAsReactAsActionFromOtherCard(self, true, 0, false));
            if (GameConditions.canTarget(game, self, characterFilter)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Move character as a 'react'");
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose character", characterFilter) {
                            @Override
                            protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                                action.addAnimationGroup(targetedCard);
                                action.appendCost(
                                        new UseForceEffect(action, playerId, 2));
                                action.allowResponses("Move " + GameUtils.getCardLink(targetedCard) + " as a 'react'",
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                PhysicalCard finalCharacter = action.getPrimaryTargetCard(targetGroupId);
                                                action.appendEffect(
                                                        new MoveAsReactEffect(action, finalCharacter, true));
                                            }
                                        });
                            }
                        });
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        if (lukeOrJediInBattleWithYourAlien(game, self)
                && GameConditions.canAddBattleDestinyDraws(game, self)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Add one battle destiny");
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new AddBattleDestinyEffect(action, 1));
                        }
                    });
            actions.add(action);
        }

        GameTextActionId activateId = GameTextActionId.LEGACY__RUG_HUG_V__ACTIVATE_FORCE;
        final int numAliens = Filters.countActive(game, self, Filters.and(Filters.your(self), Filters.alien));
        if (numAliens > 0
                && GameConditions.isOncePerGame(game, self, activateId)
                && GameConditions.canActivateForce(game, playerId)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, activateId);
            action.setText("Activate " + numAliens + " Force");
            action.appendUsage(
                    new OncePerGameEffect(action));
            action.allowResponses("Activate Force equal to the number of your aliens on table",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            int x = Filters.countActive(game, self, Filters.and(Filters.your(self), Filters.alien));
                            action.appendEffect(
                                    new ActivateForceEffect(action, playerId, x));
                        }
                    });
            actions.add(action);
        }
        return actions;
    }

    /**
     * True when Luke (or a Jedi) is in the battle together with one of your aliens (a different card).
     */
    private static boolean lukeOrJediInBattleWithYourAlien(SwccgGame game, PhysicalCard self) {
        if (!GameConditions.isDuringBattle(game)) {
            return false;
        }
        Filter lukeOrJedi = Filters.and(Filters.your(self), Filters.or(Filters.Luke, Filters.Jedi), Filters.participatingInBattle);
        for (PhysicalCard card : Filters.filterActive(game, self, lukeOrJedi)) {
            if (GameConditions.canSpot(game, self, Filters.and(Filters.your(self), Filters.alien, Filters.participatingInBattle, Filters.not(card)))) {
                return true;
            }
        }
        return false;
    }
}
