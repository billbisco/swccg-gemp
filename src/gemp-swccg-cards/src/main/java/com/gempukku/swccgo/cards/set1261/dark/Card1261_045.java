package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.SatisfyAllBattleDamageAndAttritionEffect;
import com.gempukku.swccgo.cards.effects.choose.ChooseAndLoseCardFromHandEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.PutCardFromLostPileInUsedPileEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;
import java.util.Collections;
import java.util.List;

/** Legacy Block 6.1 One Bright Spot. */
public class Card1261_045 extends AbstractUsedInterrupt {
    public Card1261_045() {
        super(Side.DARK, 4, "One Bright Spot", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setLore("Even a Jedi cannot always foresee future events.");
        setGameText("If Galen was just lost and Juno Eclipse is on table (or vice versa), place the just-lost character on your Used Pile instead. OR If Juno Eclipse is participating in a battle, may lose Galen from hand to satisfy all battle damage and attrition against you.");
        addIcons(Icon.DAGOBAH);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult result, final PhysicalCard self) {
        if ((TriggerConditions.justLost(game, result, Filters.and(Filters.your(self), Filters.Galen))
                && GameConditions.canSpot(game, self, Filters.Juno))
                || (TriggerConditions.justLost(game, result, Filters.and(Filters.your(self), Filters.Juno))
                && GameConditions.canSpot(game, self, Filters.Galen))) {
            final PhysicalCard justLost = ((LostFromTableResult) result).getCard();
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Place just-lost character on Used Pile");
            action.allowResponses(new RespondablePlayCardEffect(action) {
                @Override protected void performActionResults(Action targetingAction) {
                    // This is relocation of a just-lost card, not Force retrieval.
                    action.appendEffect(new PutCardFromLostPileInUsedPileEffect(action, playerId, justLost, true));
                }
            });
            return Collections.singletonList(action);
        }
        if (TriggerConditions.isResolvingBattleDamageAndAttrition(game, result, playerId)
                && (GameConditions.isBattleDamageRemaining(game, playerId) || GameConditions.isAttritionRemaining(game, playerId))
                && GameConditions.isDuringBattleWithParticipant(game, Filters.Juno)
                && GameConditions.hasInHand(game, playerId, Filters.Galen)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Satisfy all battle damage and attrition");
            action.appendCost(new ChooseAndLoseCardFromHandEffect(action, playerId, Filters.Galen));
            action.allowResponses(new RespondablePlayCardEffect(action) {
                @Override protected void performActionResults(Action targetingAction) {
                    action.appendEffect(new SatisfyAllBattleDamageAndAttritionEffect(action, playerId));
                }
            });
            return Collections.singletonList(action);
        }
        return null;
    }
}
