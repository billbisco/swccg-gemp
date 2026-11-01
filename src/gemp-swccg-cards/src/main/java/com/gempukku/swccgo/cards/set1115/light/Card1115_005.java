package com.gempukku.swccgo.cards.set1115.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
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
import com.gempukku.swccgo.logic.effects.PutStackedCardsInUsedPileEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DrawCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.DestinyDrawnResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Set 15: Galactic Frontier
 * Type: Character
 * Subtype: Rebel
 * Title: Beru Lars
 */
public class Card1115_005 extends AbstractRebel {
    public Card1115_005() {
        super(Side.LIGHT, 2, 1, 1, 1, 3, Title.Beru_Lars, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_15, Rarity.V);
        setVirtualSuffix(true);
        setLore("Wife of Owen Lars and guardian of Luke Skywalker. Quietly lobbied Owen to allow Luke to attend Academy. Maintains Lars' farm hydroponics labs.");
        setGameText("Once per turn, if present at a farm and a destiny was just drawn that is within 1 of the destiny of your 'crop,' may use 1 Force to place 'crop' in Used Pile. Whenever you play Beru Stew or Blue Milk, may draw top card from your Reserve Deck.");
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__BERU_LARS_V__PLACE_CROP;
        if (TriggerConditions.isDestinyJustDrawn(game, effectResult)
                && !((DestinyDrawnResult) effectResult).isCanceled()
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.isPresentAt(game, self, Filters.farm)
                && GameConditions.canUseForce(game, playerId, 1)) {
            Float drawnDestiny = ((DestinyDrawnResult) effectResult).getDestinyDrawValue();
            if (drawnDestiny == null) {
                PhysicalCard drawn = ((DestinyDrawnResult) effectResult).getCard();
                if (drawn == null) {
                    return null;
                }
                drawnDestiny = game.getModifiersQuerying().getDestiny(game.getGameState(), drawn);
            }
            PhysicalCard hydro = Filters.findFirstActive(game, self, Filters.and(Filters.your(self), Filters.title("Lars' Hydroponics Station"), Filters.hasStacked(Filters.any)));
            if (hydro == null) {
                return null;
            }
            PhysicalCard crop = game.getGameState().getStackedCards(hydro).get(0);
            float cropDestiny = game.getModifiersQuerying().getDestiny(game.getGameState(), crop);
            if (Math.abs(drawnDestiny - cropDestiny) <= 1) {
                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Place 'crop' in Used Pile");
                action.setActionMsg("Use 1 Force to place " + GameUtils.getCardLink(crop) + " in Used Pile");
                action.appendUsage(
                        new OncePerTurnEffect(action));
                action.appendCost(
                        new UseForceEffect(action, playerId, 1));
                action.appendEffect(
                        new PutStackedCardsInUsedPileEffect(action, playerId, Collections.singletonList(crop), true));
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalBeforeTriggers(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__BERU_LARS_V__DRAW_CARD;
        if (TriggerConditions.isPlayingCard(game, effect, playerId, Filters.or(Filters.Beru_Stew, Filters.Blue_Milk))
                && GameConditions.hasReserveDeck(game, playerId)) {
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Draw top card of Reserve Deck");
            action.setActionMsg("Draw the top card of Reserve Deck");
            action.appendEffect(
                    new DrawCardIntoHandFromReserveDeckEffect(action, playerId));
            return Collections.singletonList(action);
        }
        return null;
    }
}
