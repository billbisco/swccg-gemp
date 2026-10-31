package com.gempukku.swccgo.cards.set1103.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.CardsInHandEqualToOrMoreThanCondition;
import com.gempukku.swccgo.cards.effects.PeekAtOpponentsHandEffect;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardsOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotBePlacedInUsedPileModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 3: Revolution
 * Type: Effect
 * Title: Scanner Techs
 */
public class Card1103_020 extends AbstractNormalEffect {
    public Card1103_020() {
        super(Side.LIGHT, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Scanner_Techs, Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_ORIGINAL_SET_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("Specialized scanner technicians examine scanner output to identify the presence of life forms. Experienced operators can even identify species and gender.");
        setGameText("Deploy on table. During opponent's move phase, may use 3 Force to peek at opponent's hand or place a Political Effect (and all cards on it) in owner's Used Pile. Unless you have 13 or more cards in hand, opponent may not place those cards in Used Pile. (Immune to Alter.)");
        addIcons(Icon.A_NEW_HOPE);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotBePlacedInUsedPileModifier(self,
                Filters.or(Filters.Political_Effect, Filters.stackedOn(self, Filters.Political_Effect)),
                new NotCondition(new CardsInHandEqualToOrMoreThanCondition(playerId, 13))));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        if (GameConditions.isDuringOpponentsPhase(game, playerId, Phase.MOVE)
                && GameConditions.canUseForce(game, playerId, 3)) {
            List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

            final TopLevelGameTextAction peekAction = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, GameTextActionId.LEGACY__SCANNER_TECHS_V__PEEK_OR_PLACE_POLITICAL_EFFECT);
            peekAction.setText("Peek at opponent's hand");
            peekAction.appendCost(
                    new UseForceEffect(peekAction, playerId, 3));
            peekAction.appendEffect(
                    new PeekAtOpponentsHandEffect(peekAction, playerId));
            actions.add(peekAction);

            final Collection<PhysicalCard> politicalEffects = findPoliticalEffects(game);
            if (!politicalEffects.isEmpty()) {
                final TopLevelGameTextAction placeAction = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, GameTextActionId.LEGACY__SCANNER_TECHS_V__PLACE_POLITICAL_EFFECT);
                placeAction.setText("Place a Political Effect in Used Pile");
                placeAction.appendCost(
                        new UseForceEffect(placeAction, playerId, 3));
                placeAction.appendEffect(
                        new ChooseCardsOnTableEffect(placeAction, playerId, "Choose a Political Effect", 1, 1, politicalEffects) {
                            @Override
                            protected boolean getUseShortcut() {
                                return true;
                            }

                            @Override
                            protected void cardsSelected(Collection<PhysicalCard> selectedCards) {
                                PhysicalCard targetedCard = selectedCards.iterator().next();
                                placeAction.appendEffect(
                                        new PlaceCardInUsedPileFromTableEffect(placeAction, targetedCard, false, Zone.USED_PILE));
                            }
                        });
                actions.add(placeAction);
            }
            return actions;
        }
        return null;
    }

    private Collection<PhysicalCard> findPoliticalEffects(SwccgGame game) {
        Collection<PhysicalCard> politicalEffects = new LinkedList<PhysicalCard>(Filters.filterAllOnTable(game, Filters.Political_Effect));
        if (!politicalEffects.isEmpty()) {
            return politicalEffects;
        }
        for (PhysicalCard card : game.getGameState().getCardsInPlay()) {
            if (card.getBlueprint().getCardSubtype() == CardSubtype.POLITICAL) {
                politicalEffects.add(card);
            }
        }
        return politicalEffects;
    }
}
