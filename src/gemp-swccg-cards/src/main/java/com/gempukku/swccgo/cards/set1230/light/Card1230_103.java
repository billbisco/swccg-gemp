package com.gempukku.swccgo.cards.set1230.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelWeaponTargetingEffect;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleActionProxyEffect;
import com.gempukku.swccgo.logic.effects.CancelDestinyEffect;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Block 3
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Don't Forget The Droids (V)
 */
public class Card1230_103 extends AbstractUsedOrLostInterrupt {
    public Card1230_103() {
        super(Side.LIGHT, 6, "Don't Forget The Droids", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("'We're on our way!'");
        setGameText("USED: Cancel an attempt to target a droid with a weapon. LOST: If a battle was just initiated at a site, lose 1 Force and place your droid there out of play to cancel all opponent's weapon and battle destiny draws during this battle.");
        addIcons(Icon.JABBAS_PALACE);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self) {
        if (TriggerConditions.isTargetedByWeapon(game, effect, Filters.droid, Filters.any)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Cancel targeting");
            action.allowResponses("Cancel an attempt to target a droid with a weapon",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new CancelWeaponTargetingEffect(action));
                        }
                    }
            );
            return Collections.singletonList(action);
        }

        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);
        Filter yourDroidAtBattle = Filters.and(Filters.your(self), Filters.droid, Filters.at(Filters.battleLocation));

        if (TriggerConditions.battleInitiatedAt(game, effectResult, Filters.site)
                && GameConditions.canSpot(game, self, yourDroidAtBattle)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Place droid out of play");
            action.appendTargeting(
                    new ChooseCardOnTableEffect(action, playerId, "Choose droid to place out of play", yourDroidAtBattle) {
                        @Override
                        protected void cardSelected(PhysicalCard droid) {
                            action.appendCost(
                                    new LoseForceEffect(action, playerId, 1, true));
                            action.appendCost(
                                    new PlaceCardOutOfPlayFromTableEffect(action, droid));
                            action.allowResponses("Cancel opponent's weapon and battle destiny draws",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            action.appendEffect(new AddUntilEndOfBattleActionProxyEffect(action,
                                                    new AbstractActionProxy() {
                                                        @Override
                                                        public List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult) {
                                                            if ((TriggerConditions.isBattleDestinyJustDrawnBy(game, effectResult, opponent)
                                                                    || TriggerConditions.isWeaponDestinyJustDrawnBy(game, effectResult, opponent))
                                                                    && GameConditions.canCancelDestiny(game, playerId)) {
                                                                RequiredGameTextTriggerAction cancel = new RequiredGameTextTriggerAction(self, self.getCardId());
                                                                cancel.setText("Cancel opponent's destiny draw");
                                                                cancel.appendEffect(new CancelDestinyEffect(cancel));
                                                                return Collections.singletonList(cancel);
                                                            }
                                                            return null;
                                                        }
                                                    }));
                                        }
                                    }
                            );
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
