package com.gempukku.swccgo.cards.set1105.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
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
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.PlaceJustLostCardOutOfPlayFromLostPileEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Set 5: A Dark Time
 * Type: Effect
 * Title: No Disintegrations!
 */
public class Card1105_014 extends AbstractNormalEffect {
    public Card1105_014() {
        super(Side.LIGHT, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.No_Disintegrations, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_5, Rarity.V);
        setVirtualSuffix(true);
        setLore("'There will be a substantial reward for the one who finds the Millennium Falcon. You are free to use any methods necessary, but I want them alive.'");
        setGameText("Deploy on table. During opponent's turn, may place a just forfeited Rebel of ability > 2 out of play; opponent loses 3 Force.");
        addIcons(Icon.DAGOBAH);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);
        if (TriggerConditions.justForfeited(game, effectResult, playerId, Filters.and(Filters.Rebel, Filters.abilityMoreThan(2)))
                && GameConditions.isOpponentsTurn(game, self)) {
            PhysicalCard justForfeitedCard = ((LostFromTableResult) effectResult).getCard();
            OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Place " + GameUtils.getFullName(justForfeitedCard) + " out of play");
            action.setActionMsg("Place " + GameUtils.getCardLink(justForfeitedCard) + " out of play; " + opponent + " loses 3 Force");
            action.appendEffect(
                    new PlaceJustLostCardOutOfPlayFromLostPileEffect(action, playerId, playerId, justForfeitedCard, false));
            action.appendEffect(
                    new LoseForceEffect(action, opponent, 3));
            return Collections.singletonList(action);
        }
        return null;
    }
}
