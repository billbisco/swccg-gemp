package com.gempukku.swccgo.cards.set1111.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.PutCardsFromHandOnUsedPileEffect;
import com.gempukku.swccgo.logic.effects.RecirculateEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Set 11: Gathering Storm
 * Type: Effect
 * Title: He Is Not Ready
 */
public class Card1111_023 extends AbstractNormalEffect {
    public Card1111_023() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.He_Is_Not_Ready, Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_ORIGINAL_SET_11, Rarity.V);
        setVirtualSuffix(true);
        setLore("\"All his life has he looked away. To the future; to the horizon. Never his mind on where he was! Hmm? What he was doing! Hm.\"");
        setGameText("Deploy on table. Just after opponent draws a starting hand of 8 cards, unless you started an Objective and/or you take into hand or deploy from Reserve Deck any other Effects, opponent must place all but 5 cards from hand in their Used Pile; re-circulate. (Immune to Alter.)");
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        final String playerId = self.getOwner();
        final String opponent = game.getOpponent(playerId);
        GameTextActionId gameTextActionId = GameTextActionId.OVS__HE_IS_NOT_READY__DUMP_STARTING_HAND;

        if (TriggerConditions.isStartOfEachTurn(game, effectResult)
                && GameConditions.isOncePerGame(game, self, gameTextActionId)
                && !GameConditions.canSpot(game, self, Filters.and(Filters.your(self), Filters.Objective))
                && !GameConditions.canSpot(game, self, Filters.and(Filters.your(self), Filters.Effect, Filters.not(self)))) {
            int handSize = GameConditions.numCardsInHand(game, opponent);
            if (handSize > 5) {
                final int toPlace = handSize - 5;
                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Place all but 5 cards from opponent's hand in Used Pile");
                action.appendUsage(new OncePerGameEffect(action));
                action.appendEffect(new PutCardsFromHandOnUsedPileEffect(action, opponent, toPlace, toPlace));
                action.appendEffect(new RecirculateEffect(action, opponent));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
