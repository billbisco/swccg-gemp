package com.gempukku.swccgo.cards.set1220.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
import com.gempukku.swccgo.cards.effects.AddDestinyToTotalPowerEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.effects.ReduceForceLossEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LostForceResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 2
 * Type: Interrupt
 * Subtype: Lost
 * Title: Surreptitious Glance
 */
public class Card1220_155 extends AbstractLostInterrupt {
    public Card1220_155() {
        super(Side.LIGHT, 4, Title.Surreptitious_Glance, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("After working together for a long time, Lando and Lobot developed a speechless form of commnication.");
        setGameText("If this card is lost from your Reserve Deck, it satisfies an additional 2 Force loss. If your Lando and Lobot are defending in a battle, add two destiny to power only and one battle destiny.");
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    public List<TriggerAction> getLostFromLifeForceOptionalTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self) {
        if (effectResult instanceof LostForceResult
                && ((LostForceResult) effectResult).getZone() == Zone.RESERVE_DECK) {
            OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, self.getCardId());
            action.setText("Satisfy additional 2 Force loss");
            action.appendEffect(
                    new ReduceForceLossEffect(action, playerId, 2));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        if (GameConditions.isDuringBattle(game)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(self), Filters.Lando, Filters.defendingBattle))
                && GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(self), Filters.Lobot, Filters.defendingBattle))) {
            List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Add destiny to power and battle destiny");
            action.allowResponses("Add two destiny to power only and one battle destiny",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            action.appendEffect(
                                    new AddDestinyToTotalPowerEffect(action, 2, playerId));
                            if (GameConditions.canAddBattleDestinyDraws(game, self)) {
                                action.appendEffect(
                                        new AddBattleDestinyEffect(action, 1));
                            }
                        }
                    }
            );
            actions.add(action);
            return actions;
        }
        return null;
    }
}
