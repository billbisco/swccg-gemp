package com.gempukku.swccgo.cards.set1250.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.PlaceCardsInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardsOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DrawCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.MayMoveAsReactModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/** Legacy Block 5 Light, final printed Wookiee (54E). */
public class Card1250_054 extends AbstractAlien {
    public Card1250_054() {
        super(Side.LIGHT, 2, 4, 4, 1, 4, "Wookiee", Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_BLOCK_5, Rarity.V);
        setVirtualSuffix(true);
        setSpecies(Species.WOOKIEE);
        setLore("Wookiees are known to be fierce warriors. Combine high technology with a primitive lifestyle. Escaped Imperial slavery after the Battle of Endor.");
        setGameText("May move as a 'react'. While with another Wookiee, draws one battle destiny if not able to otherwise. If about to be lost, may place any cards on him in owner's Used Pile and may draw top card from Reserve Deck.");
        addIcons(Icon.SPECIAL_EDITION, Icon.WARRIOR, Icon.EPISODE_I);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return List.of(new MayMoveAsReactModifier(self),
                new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, new WithCondition(self, Filters.Wookiee), 1));
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game,
            EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.isAboutToBeLost(game, effectResult, self)
                || TriggerConditions.isAboutToBeForfeitedToLostPile(game, effectResult, self)) {
            Collection<PhysicalCard> attached = Filters.filter(self.getCardsAttached(), game, Filters.canBeTargetedBy(self));
            boolean canDraw = !game.getGameState().getReserveDeck(playerId).isEmpty();
            if (attached.isEmpty() && !canDraw) return null;
            OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Save attached cards and/or draw a card");
            if (!attached.isEmpty()) {
                action.appendEffect(new ChooseCardsOnTableEffect(action, playerId,
                        "Choose cards on Wookiee to place in owner's Used Pile", 0, attached.size(), attached) {
                    @Override
                    protected void cardsSelected(Collection<PhysicalCard> selectedCards) {
                        if (!selectedCards.isEmpty()) action.insertEffect(new PlaceCardsInUsedPileFromTableEffect(action, selectedCards));
                    }
                });
            }
            if (canDraw) {
                action.appendEffect(new PlayoutDecisionEffect(action, playerId,
                        new YesNoDecision("Draw top card of Reserve Deck into hand?") {
                    @Override
                    protected void yes() {
                        action.insertEffect(new DrawCardIntoHandFromReserveDeckEffect(action, playerId));
                    }
                    @Override
                    protected void no() { }
                }));
            }
            return Collections.singletonList(action);
        }
        return null;
    }
}
