package com.gempukku.swccgo.cards.set1110.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardFromHandEffect;
import com.gempukku.swccgo.logic.effects.choose.ExchangeCardInHandWithCardInLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ResetPowerModifier;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Set: Virtual Set 10: Fourth Anthology
 * Type: Effect
 * Title: Insignificant Rebellion (V)
 */
public class Card1110_010 extends AbstractNormalEffect {
    public Card1110_010() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Insignificant_Rebellion, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_10, Rarity.V);
        setVirtualSuffix(true);
        setLore("'Your fleet is lost. And your friends on the Endor moon will not survive. There is no escape, my young apprentice.'");
        setGameText("If Ralltiir Operations on table, deploy on table. Ounee Ta is canceled. Your troopers are power = 2. Whenever you deploy a site to Ralltiir, may activate 3 Force. Whenever you draw battle destiny, may exchange a card from hand with a card of the same card type in Lost Pile. (Immune to Alter.)");
        addIcons(Icon.DEATH_STAR_II);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return GameConditions.canSpot(game, self, Filters.Ralltiir_Operations);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ResetPowerModifier(self, Filters.and(Filters.your(self), Filters.trooper), 2));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, final SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        List<OptionalGameTextTriggerAction> actions = new LinkedList<OptionalGameTextTriggerAction>();

        if (TriggerConditions.justDeployed(game, effectResult, playerId, Filters.Ralltiir_site)
                && GameConditions.canActivateForce(game, playerId)) {
            final OptionalGameTextTriggerAction activate = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            activate.setText("Activate 3 Force");
            activate.appendEffect(
                    new ActivateForceEffect(activate, playerId, 3));
            actions.add(activate);
        }

        GameTextActionId gameTextActionId = GameTextActionId.INSIGNIFICANT_REBELLION__LOST_PILE_SWAP;
        final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
        // Check condition(s)
        if (TriggerConditions.isBattleDestinyJustDrawnBy(game, effectResult, game.getDarkPlayer())) {
            action.setText("Exchange a card in hand with a card in Lost Pile");
            action.appendEffect(
                    new ChooseCardFromHandEffect(action, playerId) {
                        @Override
                        protected void cardSelected(SwccgGame game, PhysicalCard selectedCard) {
                            Set<CardType> cardTypes = game.getModifiersQuerying().getCardTypes(game.getGameState(), selectedCard);
                            Filter filterForCardInLostPile = Filters.none;
                            for (CardType cardType : cardTypes) {
                                filterForCardInLostPile = Filters.or(filterForCardInLostPile, Filters.type(cardType));
                            }
                            action.appendEffect(new ExchangeCardInHandWithCardInLostPileEffect(action, playerId, selectedCard, filterForCardInLostPile));
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(final SwccgGame game, Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect,  Filters.title(Title.Ounee_Ta))
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        List<RequiredGameTextTriggerAction> actions = new LinkedList<RequiredGameTextTriggerAction>();

        if (TriggerConditions.isTableChanged(game, effectResult)) {
            if (GameConditions.canTargetToCancel(game, self, Filters.title(Title.Ounee_Ta))) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                // Build action using common utility
                CancelCardActionBuilder.buildCancelCardAction(action, Filters.title(Title.Ounee_Ta), Title.Ounee_Ta);
                actions.add(action);
            }
        }

        return actions;
    }
}
