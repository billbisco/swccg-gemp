package com.gempukku.swccgo.cards.set1112.dark;

import com.gempukku.swccgo.cards.AbstractImmediateEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.cards.effects.StackCardFromVoidEffect;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeStackedCardIntoHandEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.DestinyDrawnResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Set 12: Martyrs & Magistrates
 * Type: Effect
 * Subtype: Immediate
 * Title: You Want This, Don't You?
 */
public class Card1112_043 extends AbstractImmediateEffect {
    public Card1112_043() {
        super(Side.DARK, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.You_Want_This_Dont_You, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_12, Rarity.V);
        setVirtualSuffix(true);
        setLore("'I can feel the hate swelling in you now...'");
        setGameText("If opponent just drew a card for destiny, deploy on table and stack that card face up here. Opponent may lose 3 Force to take card stacked here into hand; place this card in Used Pile. (Immune to Control.)");
        addIcons(Icon.TATOOINE);
        addImmuneToCardTitle(Title.Control);
    }

    @Override
    protected List<PlayCardAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);
        if (TriggerConditions.isDestinyJustDrawnBy(game, effectResult, opponent)
                && effectResult instanceof DestinyDrawnResult) {
            final PhysicalCard drawn = ((DestinyDrawnResult) effectResult).getCard();
            if (drawn != null) {
                PlayCardAction action = getPlayCardAction(playerId, game, self, self, false, 0, null, null, null, null, null, false, 0, Filters.any, null);
                if (action != null) {
                    action.appendEffect(
                            new StackCardFromVoidEffect(action, drawn, self));
                    return Collections.singletonList(action);
                }
            }
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getOpponentsCardGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        if (GameConditions.hasStackedCards(game, self)
                && GameConditions.canUseForce(game, playerId, 3)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId);
            action.setText("Lose 3 Force to take stacked card into hand");
            action.setActionMsg("Lose 3 Force to take " + GameUtils.getCardLink(self) + "'s stacked card into hand");
            action.appendCost(
                    new LoseForceEffect(action, playerId, 3, true));
            action.appendEffect(
                    new TakeStackedCardIntoHandEffect(action, playerId, self));
            action.appendEffect(
                    new PlaceCardInUsedPileFromTableEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }
}
