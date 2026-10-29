package com.gempukku.swccgo.cards.set1220.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.PutCardFromLostPileInUsedPileEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Block 2
 * Type: Effect
 * Title: All My Urchins
 */
public class Card1220_113 extends AbstractNormalEffect {
    public Card1220_113() {
        super(Side.LIGHT, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.All_My_Urchins, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("One little, two little, three little Bespinites.");
        setGameText("Deploy on table. Once per turn, if all your ability on table is provided by Cloud Cars, Han (except Han, Chewie, And The Falcon), and aliens, may place your just forfeited card in Used Pile.");
        addIcons(Icon.CLOUD_CITY);
    }

    private Filter abilityProviders() {
        return Filters.or(Filters.cloud_car, Filters.and(Filters.Han, Filters.not(Filters.title("Han, Chewie, And The Falcon"))), Filters.alien);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.justForfeited(game, effectResult, playerId, Filters.any)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId)
                && GameConditions.isAllAbilityOnTableProvidedBy(game, self, playerId, abilityProviders())) {
            PhysicalCard justForfeitedCard = ((LostFromTableResult) effectResult).getCard();
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Place " + GameUtils.getFullName(justForfeitedCard) + " in Used Pile");
            action.setActionMsg("Place " + GameUtils.getCardLink(justForfeitedCard) + " in Used Pile");
            action.appendUsage(
                    new OncePerTurnEffect(action));
            action.appendEffect(
                    new PutCardFromLostPileInUsedPileEffect(action, playerId, justForfeitedCard, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
